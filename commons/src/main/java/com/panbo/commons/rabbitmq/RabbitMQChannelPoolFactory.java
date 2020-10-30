package com.panbo.commons.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author PanBo 2020/10/30 16:37
 */
public class RabbitMQChannelPoolFactory implements PooledObjectFactory<Channel> {
    private final AtomicReference<ConnectionFactory> factory = new AtomicReference<>();

    public RabbitMQChannelPoolFactory(final String host, final int port, final String username,
                                      final String password) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        this.factory.set(connectionFactory);
    }

    @Override
    public PooledObject<Channel> makeObject() throws Exception {
        final ConnectionFactory cf = factory.get();
        return new DefaultPooledObject<>(cf.newConnection().createChannel());
    }

    @Override
    public void destroyObject(PooledObject<Channel> pooledObject) throws Exception {
        if(pooledObject != null && pooledObject.getObject() != null && pooledObject.getObject().isOpen()){
            pooledObject.getObject().close();
        }

    }

    @Override
    public boolean validateObject(PooledObject<Channel> pooledObject) {
        return pooledObject != null && pooledObject.getObject() != null && pooledObject.getObject().isOpen();
    }

    @Override
    public void activateObject(PooledObject<Channel> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<Channel> pooledObject) throws Exception {

    }
}
