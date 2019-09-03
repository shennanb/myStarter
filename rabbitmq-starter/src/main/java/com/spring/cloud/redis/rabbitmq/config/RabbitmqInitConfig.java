package com.spring.cloud.redis.rabbitmq.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RabbitmqInitConfig {

    /**
     * 路由键
     */
    private String routerKey;

    /**
     * 交换机
     */
    private String exchange;

    /**
     * 队列
     */
    private String queue;

    /**
     * 交换机的类型
     */
    private String type;

}
