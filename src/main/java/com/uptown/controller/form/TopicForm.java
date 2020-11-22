package com.uptown.controller.form;

import lombok.Data;

/**
 * @Author lixiaofei
 * @create 2020/10/22 8:07 下午
 */
@Data
public class TopicForm {
    // 消费组
    private String groupId;
    // 监听的主题
    private String topic;
    // 生产者地址
    private String bootServer;

    private String filedId = "system_time";
}
