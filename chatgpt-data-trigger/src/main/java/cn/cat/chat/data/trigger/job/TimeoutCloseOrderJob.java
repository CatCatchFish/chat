package cn.cat.chat.data.trigger.job;

import cn.cat.chat.data.domain.order.service.IOrderService;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TimeoutCloseOrderJob {
    @Resource
    private IOrderService orderService;
    @Resource
    private AlipayClient alipayClient;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        List<String> orderIds = orderService.queryTimeoutCloseOrderList();
        if (orderIds.isEmpty()) {
            log.info("定时任务，超时15分钟订单关闭，暂无超时未支付订单 orderIds is null");
            return;
        }
        for (String orderId : orderIds) {
            try {
                if (attemptCloseOrder(orderId)) {
                    log.info("定时任务，超时15分钟订单关闭，订单号：{} 关闭成功", orderId);
                } else {
                    log.info("定时任务，超时15分钟订单关闭，订单号：{} 关闭失败，原因：{}", orderId, "支付宝关单接口调用失败");
                }
            } catch (Exception e) {
                log.error("定时任务，超时15分钟订单关闭，订单号：{} 关闭失败，原因：{}", orderId, e.getMessage());
            }
        }
    }

    private boolean attemptCloseOrder(String orderId) throws AlipayApiException {
        // 调用支付宝的关单接口
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId); // 商户的订单号
        request.setBizContent(bizContent.toString());
        AlipayTradeCloseResponse response = alipayClient.execute(request);

        // 处理支付宝关单接口返回结果
        if (response.isSuccess() || response.getCode().equals("40004")) {
            // 1、关单成功的情况 2、订单不存在的情况下，更新数据库
            return orderService.changeOrderClose(orderId);
        } else {
            log.error("定时任务，超时15分钟订单关闭，订单号：{} 关闭失败，原因：{}", orderId, response.getSubMsg());
            return false;
        }
    }
}
