package com.panbo.commons.util;

import com.panbo.commons.exception.BaseException;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author PanBo 2020/10/30 17:03
 */
public abstract class Pool<T> implements Closeable {
    protected GenericObjectPool<T> internalPool;

    public Pool(final GenericObjectPoolConfig<T> poolConfig, PooledObjectFactory<T> factory){
        initPool(poolConfig, factory);
    }

    protected void initPool(final GenericObjectPoolConfig<T> poolConfig, PooledObjectFactory<T> factory){
        if(internalPool != null){
            try {
                closeInternalPool();
            }catch (Exception e){

            }
        }
        internalPool = new GenericObjectPool<>(factory, poolConfig);
    }

    //对象用完，归还到连接池
    protected void returnResourceObject(final T resource){
        if(resource != null){
            internalPool.returnObject(resource);
        }
    }
    protected void returnBrokenResourceObject(final T resource){
        try {
            internalPool.invalidateObject(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //从连接池中获取一个对象
    public T getResource(){
        try {
            return internalPool.borrowObject();
        } catch (Exception e) {
            throw new BaseException("Could not get a resource from the pool", e);
        }
    }

    protected void closeInternalPool(){
        try {
            internalPool.close();
        }catch (Exception e){
            throw new BaseException("Could not destroy the pool", e);
        }
    }

    @Override
    public void close() throws IOException {
        closeInternalPool();
    }

    private boolean poolInactive(){
        return this.internalPool == null || this.internalPool.isClosed();
    }
    public void addObjects(int count){
        try{
            for(int i = 0; i < count; i++){
                this.internalPool.addObject();
            }
        } catch (Exception e) {
            throw new BaseException("Error trying to add idle objects", e);
        }
    }
    public long getMaxBorrowWaitTimeMillis(){
        return poolInactive() ? -1 : this.internalPool.getMaxBorrowWaitTimeMillis();
    }
    public long getMeanBorrowWaitTimeMillis(){
        return poolInactive() ? -1 : this.internalPool.getMeanBorrowWaitTimeMillis();
    }
    public int getNumActive(){
        return poolInactive() ? -1 : this.internalPool.getNumActive();
    }
    public int getNumIdle(){
        return poolInactive() ? -1 : this.internalPool.getNumIdle();
    }
    public int getNumWaiters(){
        return poolInactive() ? -1 : this.internalPool.getNumWaiters();
    }
}
