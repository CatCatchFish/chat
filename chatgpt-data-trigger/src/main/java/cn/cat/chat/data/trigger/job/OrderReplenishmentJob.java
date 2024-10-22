package cn.cat.chat.data.trigger.job;

import cn.cat.chat.data.domain.order.service.IOrderService;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OrderReplenishmentJob {
    @Resource
    private IOrderService orderService;
    @Resource(name = "payback_call")
    private RTopic rTopic;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        try {
            List<String> orderIds = orderService.queryReplenishmentOrder();
            if (orderIds.isEmpty()) {
                log.info("定时任务，订单补货不存在，查询 orderIds is null");
                return;
            }
            for (String orderId : orderIds) {
                log.info("定时任务，订单补货开始。orderId: {}", orderId);
                rTopic.publish(orderId);
            }
        } catch (Exception e) {
            log.error("定时任务，订单补货失败。", e);
        }
    }

}
