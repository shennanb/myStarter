package service;

import cn.hutool.core.lang.UUID;
import code.RabbitMqQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Component
@Slf4j
public class RabbitmqService {

    @Autowired
    public RabbitTemplate rabbitTemplate;

    /**
     * 发送mq消息，消息实体持久化。
     */
    public void sendSerializable(@Valid @NotNull RabbitMqQueue queue) {
        String uuid = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend("", queue.getQueue(), queue.getContent() ,(mp) -> {
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setDeliveryMode(MessageDeliveryMode.fromInt(2));
            return new Message(String.valueOf(queue.getContent()).getBytes(), messageProperties);
        }, new CorrelationData(uuid));
        log.debug("RabbitmqService <入队成功> {}",uuid);
    }

    /**
     * 发送mq消息
     */
    public void send(@Valid @NotNull RabbitMqQueue queue) {
        String uuid = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend("", queue.getQueue(), queue.getContent() ,new CorrelationData(uuid));
        log.debug("RabbitmqService <入队成功> {}",uuid);
    }

}
