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
                                      final String password, final String virtualHost) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPassword(password);
        this.factory.set(connectionFactory);
    }
    public RabbitMQChannelPoolFactory(final String host, final int port, final String username,
                                      final String password) {
        this(host, port, username, password, "/");
    }

    /**
     * 创建一个对象，当在GenericObjectPool类中调用borrowObject方法时，如果当前对象池中没有空闲的对象，
     * GenericObjectPool就会调用该方法，创建一个新对象，并把这个对象封装到PooledObject类中，并交给对象池管理
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<Channel> makeObject() throws Exception {
        final ConnectionFactory cf = factory.get();
        return new DefaultPooledObject<>(cf.newConnection().createChannel());
    }

    /**
     * 销毁对象，当对象池检测到某个对象的空闲时间(idle)超时，或使用完对象归还到对象池之前被检测到对象已经无效时，
     * 就会调用这个方法销毁对象。对象的销毁一般和业务相关，但必须明确的是，当调用这个方法之后，对象的生命周期必须结果。
     * 如果是对象是线程，线程必须已结束；
     * 如果是socket，socket必须已close；
     * 如果是文件操作，文件数据必须已flush，且文件正常关闭。
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<Channel> pooledObject) throws Exception {
        if(pooledObject != null && pooledObject.getObject() != null && pooledObject.getObject().isOpen()){
            pooledObject.getObject().close();
        }

    }

    /**
     * 检测一个对象是否有效。在对象池中的对象必须是有效的，
     * 这个有效的概念是，从对象池中拿出的对象是可用的。比如，如果是socket,那么必须保证socket是连接可用的。
     * 在从对象池获取对象或归还对象到对象池时，会调用这个方法，判断对象是否有效，如果无效就会销毁。
     * @param pooledObject
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<Channel> pooledObject) {
        return pooledObject != null && pooledObject.getObject() != null && pooledObject.getObject().isOpen();
    }

    /**
     * 激活一个对象或者说启动对象的某些操作。比如，如果对象是socket，如果socket没有连接，或意外断开了，
     * 可以在这里启动socket的连接。它会在检测空闲对象的时候，如果设置了测试空闲对象是否可以用，就会调用这个方法，
     * 在borrowObject的时候也会调用。另外，如果对象是一个包含参数的对象，可以在这里进行初始化。让使用者感觉这是一个新创建的对象一样。
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject<Channel> pooledObject) throws Exception {

    }

    /**
     * 钝化一个对象。在向对象池归还一个对象是会调用这个方法。这里可以对对象做一些清理操作。
     * 比如清理掉过期的数据，下次获得对象时，不受旧数据的影响。
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<Channel> pooledObject) throws Exception {

    }
}
