package com.uptown.mq;

import com.alibaba.fastjson.JSON;
import com.uptown.util.RedisUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author lixiaofei
 * @create 2020/10/22 5:25 下午
 */

@Slf4j
@Component
@NoArgsConstructor
public class KafkaConsumerRunnable implements Runnable {

    // 每个线程维护私有的KafkaConsumer实例
    @Getter
    private KafkaConsumer<String, String> consumer;

    @Getter
    private String topicName;

    // TB_STREAM_CONFIG中的连接配置



    /**
     * 封装必要信息
     * @param bootServer 生产者ip
     * @param groupId 分组信息
     * @param topic  订阅主题
     */
    public KafkaConsumerRunnable(String bootServer, String groupId, String topic) {
        topicName = topic;
        Properties props = new Properties();
        props.put("bootstrap.servers", bootServer);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "100");  //自动提交时间间隔 毫秒单位
        props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "latest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  //键反序列化方式
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(props);
    }


    @Override
    public void run() {
        consumer.subscribe(Arrays.asList(topicName));
        Thread.currentThread().setName(topicName);
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);   // 本例使用100ms作为获取超时时间
                for (ConsumerRecord<String, String> record : records) {
                    String value = record.value();
                    log.info("线程 {} 消费kafka数据 -> {} \n 偏移量offset -> {} \n 分区partition -> {}",
                            Thread.currentThread().getName(), value, record.offset(), record.partition());
                    // 2、反序列化数据
                    HashMap resMap = JSON.parseObject(value, HashMap.class);
                    log.info("resMap -> {}", resMap);
                    //获取处理时间 作为redis zset中的sorted
                    Object handleTime = resMap.get("handleTime");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date d = sdf.parse(String.valueOf(handleTime));

                    // 通过connConfig判断是业务时间还是系统时间

                    RedisUtils.zSSet("dataflow:" + topicName, value, Double.valueOf(d.getTime()));
                    log.info("handleTime -> {}", handleTime);
                }
                consumer.commitAsync();
            }


        } catch (WakeupException | ParseException e) {
            log.info("ignore for shutdown");
            e.printStackTrace();
        }
    }

    // 退出后关掉客户端
    public void shutDown() {
        consumer.wakeup();
    }
}