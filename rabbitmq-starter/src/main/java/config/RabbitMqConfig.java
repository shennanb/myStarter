package config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.Channel;
import core.exception.BaseException;
import core.exception.SystemErrorType;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.StringHelper;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class RabbitMqConfig {

    @Value("${spring.rabbitmq.address}")
    private String address;
    @Value("${spring.rabbitmq.username}")
    private String userName;
    @Value("${spring.rabbitmq.password}")
    private String passWord;
    @Value("${spring.rabbitmq.virtual-host}")
    private String vhost;
    @Value("${rabbitmq.initConfig}")
    private String initConfig;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(address);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(passWord);
        connectionFactory.setVirtualHost(vhost);
        // 发送消息 显示调用 才能进行消息的回调。
        connectionFactory.setPublisherConfirms(true);
        // 异步处理
        CompletableFuture.runAsync(() -> initExchangeAndQueue(connectionFactory));
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMandatory(true);//设置ReturnCallback有效
        return rabbitTemplate;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    /**
     * @Description 初始化 交换机 和 队列
     **/
    private void initExchangeAndQueue(CachingConnectionFactory connectionFactory){
        log.info("initExchangeAndQueue <初始化队列和交换机 start>");
        // 创建交换机 和 队列 以及绑定他们关系
        Connection connection = null;
        try {
            // 读取配置文件
            if(StrUtil.isEmpty(initConfig)){
                return;
            }
            List<String> strs = CollUtil.newArrayList(initConfig.split(","));
            List<RabbitmqInitConfig> rabbitmqInitConfigs =  strs.stream().map(i -> strToCachingConnectionFactory(i)).collect(Collectors.toList());
            Channel channel = null;
            connection = connectionFactory.createConnection();
            for (RabbitmqInitConfig rabbitmqInitConfig: rabbitmqInitConfigs) {
                try {
                    channel = connection.createChannel(false);
                    // queueName:对列名称
                    channel.queueDeclare(rabbitmqInitConfig.getQueue(), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, null);
//                    exchange：交换机名称 type：交换机的类型(direct/topic/fanout)
//                    channel.exchangeDeclare(rabbitmqInitConfig.getExchange(), rabbitmqInitConfig.getType());
//                    queueName:对列名称 exchange：交换机名称 routingKey：队列跟交换机绑定的键值
//                    channel.queueBind(rabbitmqInitConfig.getQueue(), rabbitmqInitConfig.getExchange(), rabbitmqInitConfig.getRouterKey());
                }catch(Exception e){
                    log.error("initExchangeAndQueue <初始化交换机 和 队列失败，关闭channel失败> ->" + e.getMessage());
                }finally {
                    channel.close();
                }
            }
        }catch(Exception e){
            log.error("initExchangeAndQueue <初始化交换机 和 队列失败> -> " + e.getMessage());
        }finally {
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (Exception e) {
                log.error("initExchangeAndQueue <初始化交换机 和 队列失败，关闭连接失败> -> " + e.getMessage());
            }
        }
        log.info("initExchangeAndQueue <初始化队列和交换机 end>");
    }

    /**
     * @Description 字符串转对象
     **/
    private RabbitmqInitConfig strToCachingConnectionFactory(String str){
        if(str.split(":").length != 4){
            throw new BaseException(SystemErrorType.RABBITMQ_ERROR,"初始化失败,请检查配置文件");
        }
        String exchage = str.split(":")[0];
        String type = str.split(":")[1];
        String queue = str.split(":")[2];
        String routerKey = str.split(":")[3];
        return RabbitmqInitConfig.builder().exchange(exchage).type(type).queue(queue).routerKey(routerKey).build();
    }

}
