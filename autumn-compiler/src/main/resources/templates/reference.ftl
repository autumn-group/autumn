package ${packageName};

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class _${simpleClassName}Proxy implements ${className}.Iface {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private volatile GenericObjectPool<TServiceClient> pool = null;

    private GenericObjectPool<TServiceClient> getPool() {
        if(null != pool) {
            return pool;
        }
        synchronized (this) {
            GenericObjectPool.Config config = getPoolConfig();
            ${packageName}._${simpleClassName}PoolFactory clientPool = new ${packageName}._${simpleClassName}PoolFactory();
            this.pool = new GenericObjectPool<>(clientPool, config);
            return pool;
        }
    }

    private GenericObjectPool.Config getPoolConfig() {
        GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
        poolConfig.maxActive = 10;
        poolConfig.maxIdle = 1;
        poolConfig.minIdle = 0;
        poolConfig.minEvictableIdleTimeMillis = 10000;
        poolConfig.timeBetweenEvictionRunsMillis = 10000 * 2L;
        poolConfig.testOnBorrow=true;
        poolConfig.testOnReturn=false;
        poolConfig.testWhileIdle=false;
        return poolConfig;
    }
    <#list methodElements as method>
    @Override
    public ${method.returnType} ${method.name}(
            <#list method.paramTypes as paramType>
                <#if paramType_index < method.paramTypes?size - 1>
                  ${paramType} var${paramType_index},
                <#else>
                    ${paramType} var${paramType_index}
                </#if>
            </#list>
        ) throws TException {
        boolean flag = true;
        ${className}.Client client = null;
        try {
            client = (${className}.Client)getPool().borrowObject();
            ${method.returnType} result = client.${method.name}(
            <#list method.paramTypes as paramType>
                <#if paramType_index < method.paramTypes?size - 1>
                  paramType var${paramType_index},
                <#else>
                  var${paramType_index}
                </#if>
            </#list>
            );
            <#if method.returnType != "void">
                return result;
            </#if>
        } catch (Exception e) {
            flag = false;
            log.warn("${className}.${method.name} invoke error: exception:", e);
        } finally {
            try {
                if(flag){
                    pool.returnObject(client);
                }else{
                    pool.invalidateObject(client);
                }
            } catch (Exception e) {
                log.warn("${className}.${method.name} close error: exception:", e);
            }
        }
        <#if method.returnType == "int">
            return 0;
        <#else>
            return null;
        </#if>
    }
    </#list>



}
