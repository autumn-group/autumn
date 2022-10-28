package cloud.micronative.autumn.core.pool;

public class ClientConcurrentBagEntry implements ConcurrentBagEntry{



    @Override
    public boolean compareAndSet(int expectState, int newState) {
        return false;
    }

    @Override
    public void setState(int newState) {

    }

    @Override
    public int getState() {
        return 0;
    }
}
