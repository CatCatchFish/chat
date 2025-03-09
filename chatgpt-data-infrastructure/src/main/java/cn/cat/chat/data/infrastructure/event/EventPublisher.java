package cn.cat.chat.data.infrastructure.event;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventPublisher {

    @Resource(name = "payback_call")
    private RTopic rTopic;

    public void publish(String topic, String outTradeNo) {
        try {
            rTopic.publish(outTradeNo);
            log.info("发送MQ消息 topic:{} message:{}", topic, outTradeNo);
        } catch (Exception e) {
            log.error("发送MQ消息失败 topic:{} message:{}", topic, outTradeNo, e);
            throw e;
        }
    }

}
