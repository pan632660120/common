package com.panbo.commons.rabbitmq;

import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author PanBo 2020/10/30 19:47
 */
public class RabbitMQChannelPoolConfig extends GenericObjectPoolConfig<Channel> {
    public RabbitMQChannelPoolConfig() {
        setTestWhileIdle(true);
        setJmxEnabled(true);
        setBlockWhenExhausted(true);
        setMaxWaitMillis(5000);
        setTestOnBorrow(true);
        setTestOnReturn(true);
        setMaxTotal(500);
        setMaxIdle(20);
        setMinIdle(10);
        setTimeBetweenEvictionRunsMillis(6000);
        setSoftMinEvictableIdleTimeMillis(20000);
    }
}
