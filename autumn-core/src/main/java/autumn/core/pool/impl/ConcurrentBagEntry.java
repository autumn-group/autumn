package autumn.core.pool.impl;

public interface ConcurrentBagEntry<T> {
    int STATE_NOT_IN_USE = 0;
    int STATE_IN_USE = 1;
    int STATE_REMOVED = -1;
    int STATE_RESERVED = -2;

    boolean compareAndSet(int expectState, int newState);
    void setState(int newState);
    int getState();
    T getEntry();
    String getService();
    String getIpPort();
    String getId();
    void close();
}
