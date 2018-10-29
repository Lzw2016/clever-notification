package org.clever.notification.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-26 17:27 <br/>
 */
@Slf4j
public class RabbitMQTest {

    private final static String QUEUE_NAME = "hello";

    @Test
    public void t01() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.159.131");
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //为通道指明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "Hello World!";

        int count = 10000 * 100;
        for (int i = 0; i < count; i++) {
            //发布消息
            channel.basicPublish("", QUEUE_NAME, null, String.format("%s%s", message, i).getBytes("UTF-8"));
            if (i % 1000 == 0) {
                log.info("### {}", i);
            }
        }
        //关闭连接
        channel.close();
        connection.close();
    }
}
