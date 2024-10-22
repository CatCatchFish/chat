package cn.cat.chat.data.trigger.mq;

import cn.cat.chat.data.domain.order.service.IOrderService;
import cn.cat.chat.data.types.model.RedisTopic;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service("redisTopicListener")
@RedisTopic(topic = "payback_call")
public class RedisTopicListener implements MessageListener<String> {
    @Resource
    private IOrderService orderService;

    @Override
    public void onMessage(CharSequence charSequence, String tradeNo) {
        log.info("支付完成，发货并记录，开始。订单：{}", tradeNo);
        //orderService.deliverGoods(tradeNo);
    }
}
