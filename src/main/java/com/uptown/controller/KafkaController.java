package com.uptown.controller;

import com.uptown.controller.form.TopicForm;
import com.uptown.mq.ConsumerGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * @Author lixiaofei
 * @create 2020/10/22 8:03 下午
 */
@RestController
public class KafkaController {

    @Autowired
    ConsumerGroup consumerGroup;


    @GetMapping("/")
    public String hello() {
        return "hello, kafka!";
    }

    @GetMapping("/monitorKafka")
    public void runConsumer(TopicForm topicForm) {
        System.out.println("当前线程"+Thread.currentThread().getName());
        consumerGroup.createKafkaThread(topicForm.getGroupId(), topicForm.getTopic(), topicForm.getBootServer());
    }

    @GetMapping("/killMonitor")
    public void killMonitor(@NotNull String topicName) {
        consumerGroup.shutdown(topicName);
    }

    @GetMapping("/showAllThread")
    public void showAllThread() {
        Thread mainThread = Thread.currentThread();
        ThreadGroup mainThreadGroup = mainThread.getThreadGroup();

        int count = mainThreadGroup.activeCount();
        Thread[] threads = new Thread[count];

        mainThreadGroup.enumerate(threads, true);
        for (Thread thread : threads) {
            System.out.println(thread.getName());
        }
    }

}
