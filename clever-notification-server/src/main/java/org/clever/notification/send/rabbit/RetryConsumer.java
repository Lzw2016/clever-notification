package org.clever.notification.send.rabbit;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.notification.model.BaseMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

import java.io.IOException;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-12-02 14:23 <br/>
 */
@Slf4j
public abstract class RetryConsumer<T extends BaseMessage> {

    public static enum Action {
        /**
         * 任务已完成(删除消息)
         */
        ACCEPT,

        /**
         * 任务拒绝执行(删除消息)
         */
        REJECT,

        /**
         * 任务需要重试
         */
        RETRY,

        /**
         * 跳过任务(下次调度给别的消费节点)
         */
        PASS
    }

    protected static final String RetryCount = "retryCount";
    private static final String SysName = "sysName";
    private RabbitTemplate rabbitTemplate;
    private RetryStrategy retryStrategy = RetryStrategy.DOUBLE;

    public RetryConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public RetryConsumer(RabbitTemplate rabbitTemplate, RetryStrategy retryStrategy) {
        this.rabbitTemplate = rabbitTemplate;
        this.retryStrategy = retryStrategy;
    }

    protected void onMessage(T message, String messageType, long deliveryTag, int retryCount, Channel channel) throws IOException {
        Action action = Action.ACCEPT;
        try {
            action = handle(message);
            if (action == null) {
                action = Action.RETRY;
            }
        } catch (Exception e) {
            action = Action.RETRY;
            throw ExceptionUtils.unchecked(e);
        } finally {
            if (action == Action.REJECT) {
                log.warn("不处理数据, SendId={}", message.getSendId());
            }
            if (action == Action.RETRY) {
                if (message == null) {
                    log.info("无法重试 [message is null]");
                }
                if (message != null) {
                    retryCount++;
                    long ttl = retryStrategy.getTtl(retryCount);
                    if (ttl <= -1) {
                        log.info("重试结束, 重试次数: {}", retryCount);
                    }
                    final long retryCountFinal = retryCount;
                    final long ttlFinal = ttl;
                    if (ttl > -1) {
                        rabbitTemplate.convertAndSend(
                                RabbitConfig.ExchangeName,
                                RabbitConfig.getRetryRoutingKey(messageType, message.getSysName()),
                                message,
                                msg -> {
                                    msg.getMessageProperties().setHeader(RetryCount, retryCountFinal);
                                    msg.getMessageProperties().setHeader(SysName, message.getSysName());
                                    msg.getMessageProperties().setExpiration(String.valueOf(ttlFinal));
                                    return msg;
                                },
                                new CorrelationData(message.getSendId().toString())
                        );
                    }
                }
            }
            // 消息消费 ACK
            if (action == Action.PASS) {
                channel.basicRecover(true);
            } else {
                channel.basicAck(deliveryTag, false);
            }
        }
    }

    /**
     * 消息处理
     *
     * @param message 待消费的消息
     * @return 消息处理结果
     * @throws Exception 消费失败
     */
    protected abstract Action handle(T message) throws Exception;
}
