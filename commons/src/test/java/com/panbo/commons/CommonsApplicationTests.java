package com.panbo.commons;

import com.panbo.commons.rabbitmq.RabbitMQChannelPool;
import com.panbo.commons.rabbitmq.RabbitMQChannelPoolConfig;
import com.panbo.commons.util.RabbitMQUtil;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommonsApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void rabbitTest(){
        RabbitMQChannelPoolConfig config = new RabbitMQChannelPoolConfig();
        RabbitMQChannelPool pool = new RabbitMQChannelPool(config, "localhost", 5672, "guest","guest");
        Channel channel = pool.getChannel();
        String entity = "Hello!";
        RabbitMQUtil.send(channel, "normal_exchange", "ehuzhu_normal", entity);
        pool.returnChannel(channel);
        System.out.println("完成发送！");

    }

}
