package autumn.core.pool.impl;

import org.apache.thrift.transport.TTransport;

public interface ConcurrentBagEntry {
    int STATE_NOT_IN_USE = 0;
    int STATE_IN_USE = 1;
    int STATE_REMOVED = -1;
    int STATE_RESERVED = -2;

    boolean compareAndSet(int expectState, int newState);
    void setState(int newState);
    int getState();
    <T extends TTransport> T getEntry();
    String getService();
    String getIp();
    Integer getPort();
    String getId();
    void close();

}
