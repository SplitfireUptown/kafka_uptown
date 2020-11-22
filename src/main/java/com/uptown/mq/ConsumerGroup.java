package com.uptown.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

/**
 * @Author lixiaofei
 * @create 2020/10/22 5:31 下午
 */
@Slf4j
@Component
public class ConsumerGroup {

    // 统一存放客户端的map 创建后加入
    @Resource(name = "globalKafkaConsumerThreadMap")
    Map<String, KafkaConsumerRunnable> globalKafkaConsumerThreadMap;

    @Resource(name = "defaultThreadPool")
    ThreadPoolExecutor defaultThreadPool;

    /**
     * 创建监听线程并执行
     * @param groupId
     * @param topic
     * @param bootServer
     */
    public void createKafkaThread(String groupId, String topic, String bootServer) {
        System.out.println("当前线程"+Thread.currentThread().getName());
        KafkaConsumerRunnable consumerThread = new KafkaConsumerRunnable(bootServer, groupId, topic);
        // 将新建的连接客户端加入到map
        globalKafkaConsumerThreadMap.put(topic, consumerThread);
//        new Thread(consumerThread).start();
        defaultThreadPool.execute(consumerThread);
    }

    /**
     * 停止监听线程
     */
    public void shutdown(String topic) {
        Thread mainThread = Thread.currentThread();
        ThreadGroup mainThreadGroup = mainThread.getThreadGroup();

        int count = mainThreadGroup.activeCount();
        Thread[] threads = new Thread[count];

        mainThreadGroup.enumerate(threads, true);
        Stream.of(threads).filter(Thread::isAlive).forEach(i -> {
            if (topic.equals(i.getName())) {
                // 从map中获取 consumer
                KafkaConsumerRunnable nowKafkaConsumerThread = globalKafkaConsumerThreadMap.get(topic);
                nowKafkaConsumerThread.shutDown();
            }
        });
    }

}

