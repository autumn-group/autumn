package cloud.micronative.autumn.core.pool;

import org.apache.thrift.TServiceClient;

public interface ConcurrentBagEntry<T extends TServiceClient> {
    int STATE_NOT_IN_USE = 0;
    int STATE_IN_USE = 1;
    int STATE_REMOVED = -1;
    int STATE_RESERVED = -2;

    boolean compareAndSet(int expectState, int newState);
    void setState(int newState);
    int getState();
    T getEntry();
    void close();
}
