package cloud.micronative.autumn.core.pool;

public class ConcurrentBag<T extends ConcurrentBagEntry> implements AutoCloseable {
    private volatile boolean closed;



    @Override
    public void close() throws Exception {
        closed = true;
    }
}
