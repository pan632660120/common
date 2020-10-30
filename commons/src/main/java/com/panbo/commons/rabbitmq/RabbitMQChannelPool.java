package com.panbo.commons.rabbitmq;

import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author PanBo 2020/10/30 19:52
 */
public class RabbitMQChannelPool extends RabbitMQChannelAbstract{
    public RabbitMQChannelPool(GenericObjectPoolConfig<Channel> poolConfig, final String host, final int port, final String username,
                               final String password) {
        super(poolConfig, host, port, username, password);
    }

    public RabbitMQChannelPool(final String host, final int port, final String username, final String password) {
        this(new RabbitMQChannelPoolConfig<>(), host, port, username, password);
    }
}
