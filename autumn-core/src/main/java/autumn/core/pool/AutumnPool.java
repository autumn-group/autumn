package autumn.core.pool;

public final class AutumnPool {
    private volatile static AutumnPool singleton = null;
    private ConnectionFactory connectionFactory;


    private AutumnPool() {

    }

    private void init() {
        connectionFactory = ConnectionFactory.getInstance();
    }

    public static AutumnPool getInstance() {
        if (singleton == null) {
            synchronized (AutumnPool.class) {
                if (singleton == null) {
                    singleton = new AutumnPool();
                    singleton.init();
                    return singleton;
                }
            }
        }
        return singleton;
    }


}
