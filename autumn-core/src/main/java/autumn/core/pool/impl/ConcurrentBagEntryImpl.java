package autumn.core.pool.impl;

import autumn.core.pool.AutumnPool;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentBagEntryImpl<T> implements ConcurrentBagEntry{
    private final AtomicInteger state =  new AtomicInteger(STATE_NOT_IN_USE);
    private String service;
    private String ipPort;
    private String id;
    private T entry;
    public ConcurrentBagEntryImpl(String service, String ipPort, T entry) {
        this.service = service;
        this.entry = entry;
        this.ipPort = ipPort;
        id = UUID.randomUUID().toString();
    }

    @Override
    public T getEntry() {
        return entry;
    }

    @Override
    public String getService() {
        return this.service;
    }

    @Override
    public String getIpPort() {
        return this.ipPort;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void close() {
        AutumnPool pool = AutumnPool.getInstance();
        pool.release(this.service,this);
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
