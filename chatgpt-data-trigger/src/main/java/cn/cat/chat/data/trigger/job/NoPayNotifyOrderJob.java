package cn.cat.chat.data.trigger.job;

import cn.cat.chat.data.domain.order.service.IOrderService;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 定时任务，检测未接收到或未正确处理的支付回调通知
 */
@Slf4j
@Component
public class NoPayNotifyOrderJob {
    @Resource
    private IOrderService orderService;
    @Resource
    private AlipayClient alipayClient;
    @Resource(name = "payback_call")
    private RTopic rTopic;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void exec() {
        List<String> orderIds = orderService.queryNoPayNotifyOrder();
        if (orderIds.isEmpty()) {
            return;
        }
        for (String orderId : orderIds) {
            // 设置请求参数
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", orderId);
            request.setBizContent(bizContent.toString());
            // 发起订单查询请求

            AlipayTradeQueryResponse response = null;
            try {
                response = alipayClient.execute(request);
            } catch (AlipayApiException e) {
                log.error("支付宝API异常，订单ID: {}, 错误信息: {}", orderId, e.getMessage(), e);
            } catch (Exception e) {
                log.error("定时任务执行异常，订单ID: {}, 异常信息: {}", orderId, e.getMessage(), e);
            }
            assert response != null;
            if (response.isSuccess()) {
                String tradeStatus = response.getTradeStatus();
                if (!"TRADE_SUCCESS".equals(tradeStatus)) {
                    log.info("定时任务，订单支付状态更新，当前订单未支付 orderId is {}", orderId);
                    continue;
                }
                // 订单支付成功，更新订单状态
                String tradeNo = response.getTradeNo();
                String total = response.getTotalAmount();
                BigDecimal totalAmount = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
                Date sendPayDate = response.getSendPayDate();
                boolean isSuccess = orderService.changeOrderPaySuccess(orderId, tradeNo, totalAmount, sendPayDate);
                if (isSuccess) {
                    // 发布消息
                    rTopic.publish(orderId);
                }
            } else {
                System.out.println("调用订单查询接口失败");
            }
        }
    }
}
