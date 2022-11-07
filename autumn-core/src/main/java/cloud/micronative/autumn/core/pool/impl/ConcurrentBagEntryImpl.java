package cloud.micronative.autumn.core.pool.impl;

import cloud.micronative.autumn.core.pool.AutumnPool;
import org.apache.thrift.TServiceClient;

import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentBagEntryImpl<T extends TServiceClient> implements ConcurrentBagEntry{
    private final AtomicInteger state =  new AtomicInteger(STATE_NOT_IN_USE);
    private String service;
    private T entry;
    public ConcurrentBagEntryImpl(String service, T entry) {
        this.service = service;
        this.entry = entry;
    }

    @Override
    public T getEntry() {
        return entry;
    }

    @Override
    public String getService() {
        return null;
    }

    @Override
    public void close() {
        AutumnPool pool = AutumnPool.getInstance();
        pool.release(this.getService(), this);
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
