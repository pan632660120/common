package com.panbo.commons.rabbitmq;

import com.panbo.commons.util.Pool;
import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author PanBo 2020/10/30 19:53
 */
public class RabbitMQChannelAbstract extends Pool<Channel> {
    public RabbitMQChannelAbstract(GenericObjectPoolConfig<Channel> poolConfig, PooledObjectFactory<Channel> factory) {
        super(poolConfig, factory);
    }

    @Override
    protected void returnBrokenResourceObject(Channel resource) {
        super.returnBrokenResourceObject(resource);
    }

    @Override
    protected void returnResourceObject(Channel resource) {
        super.returnResourceObject(resource);
    }
}
