package autumn.core.pool.impl;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import autumn.core.util.AutumnException;
import lombok.extern.slf4j.Slf4j;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.locks.LockSupport.parkNanos;

@Slf4j
public class ConcurrentBag implements AutoCloseable {
    private volatile boolean closed;
    private final ConcurrentLinkedQueue<ConcurrentBagEntry> sharedList = new ConcurrentLinkedQueue<>();
    private final SynchronousQueue<ConcurrentBagEntry> handoffQueue = new SynchronousQueue<>(true);
    private final AtomicInteger waiters = new AtomicInteger(0);
    private int maxWaiters;

    public ConcurrentBag() {
        maxWaiters = 100;
    }

    public ConcurrentBagEntry borrow(long timeout, final TimeUnit timeUnit) throws InterruptedException {
        ConcurrentBagEntry entry = sharedList.poll();
        if(null != entry) {
            entry.compareAndSet(ConcurrentBagEntry.STATE_NOT_IN_USE, ConcurrentBagEntry.STATE_IN_USE);
            return entry;
        }

        int waiting = waiters.incrementAndGet();
        if(waiting > maxWaiters) {
            throw new AutumnException("autumn pool waiters over max!");
        }

        try {
            timeout = timeUnit.toNanos(timeout);
            do {
                final long start = currentTime();
                final ConcurrentBagEntry bagEntry = handoffQueue.poll(timeout, NANOSECONDS);
                if (bagEntry == null || bagEntry.compareAndSet(ConcurrentBagEntry.STATE_NOT_IN_USE, ConcurrentBagEntry.STATE_IN_USE)) {
                    return bagEntry;
                }

                timeout -= elapsedNanos(start);
            } while (timeout > 1_000);

            return null;
        } finally {
            waiters.decrementAndGet();
        }
    }

    public void requite(final ConcurrentBagEntry bagEntry){
        bagEntry.setState(ConcurrentBagEntry.STATE_NOT_IN_USE);

        for (int i = 0; waiters.get() > 0; i++) {
            if (bagEntry.getState() != ConcurrentBagEntry.STATE_NOT_IN_USE || handoffQueue.offer(bagEntry)) {
                return;
            } else if ((i & 0xff) == 0xff) {
                parkNanos(MICROSECONDS.toNanos(10));
            } else {
                Thread.yield();
            }
        }
        sharedList.offer(bagEntry);
    }

    public void add(ConcurrentBagEntry entry) {
        if(closed) {
            log.info("ConcurrentBag has been closed, ignoring add()");
            return;
        }
        // spin until a thread takes it or none are waiting
        while (waiters.get() > 0 &&
                entry.getState() == ConcurrentBagEntry.STATE_NOT_IN_USE &&
                !handoffQueue.offer(entry)) {
            Thread.yield();
        }
        sharedList.add(entry);
    }


    @Override
    public void close() throws Exception {
        closed = true;

        sharedList.forEach(it -> {
            it.setState(ConcurrentBagEntry.STATE_REMOVED);
            it.getEntry().close();
        });
        sharedList.clear();
        handoffQueue.clear();
        waiters.set(0);
    }

    private long elapsedNanos(final long startTime) {
        return System.nanoTime() - startTime;
    }

    private long currentTime() {
        return System.nanoTime();
    }
}
