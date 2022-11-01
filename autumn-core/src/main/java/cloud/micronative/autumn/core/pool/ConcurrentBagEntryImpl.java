package cloud.micronative.autumn.core.pool;

import org.apache.thrift.TServiceClient;

import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentBagEntryImpl<T extends TServiceClient> implements ConcurrentBagEntry{
    private final AtomicInteger state =  new AtomicInteger(STATE_NOT_IN_USE);
    private T entry;

    public ConcurrentBagEntryImpl(T entry) {
        this.entry = entry;
    }

    @Override
    public T getEntry() {
        return entry;
    }

    @Override
    public void close() {
        AutumnPool pool = AutumnPool.getSigSingleton();
        pool.release(this);
    }

    @Override
    public boolean compareAndSet(int expectState, int newState) {
        return state.compareAndSet(expectState, newState);
    }

    @Override
    public void setState(int newState) {
        state.set(newState);
    }

    @Override
    public int getState() {
        return state.get();
    }
}
