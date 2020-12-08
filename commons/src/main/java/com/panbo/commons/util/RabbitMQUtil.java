package com.panbo.commons.util;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author PanBo 2020/11/2 15:42
 */
@Slf4j
public class RabbitMQUtil {
    /**
     * 发送消息
     *
     * @param channel    channel
     * @param exchange   交换机
     * @param routingKey 路由键
     * @param properties 属性
     * @param entity     实体
     */
    public static void send(Channel channel, String exchange, String routingKey, AMQP.BasicProperties properties, String entity) throws RuntimeException {
        try {
            channel.basicPublish(exchange, routingKey, false, properties, entity.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("send mq message exception：{}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("send mq message exception:" + e.getMessage());
        }

    }

    /**
     * 发送消息
     *
     * @param channel    channel
     * @param exchange   交换机
     * @param routingKey 路由键
     * @param entity     实体
     */
    public static void send(Channel channel, String exchange, String routingKey, String entity) throws RuntimeException {
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                .builder()
                .deliveryMode(2)
                .contentType("application/json")
                .contentEncoding("UTF-8")
                .build();
        send(channel, exchange, routingKey, basicProperties, entity);

    }
}
