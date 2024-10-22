package cn.cat.chat.data.test;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@SpringBootTest
public class ApiTest {
    @Resource
    private AlipayClient alipayClient;

    @Test
    public void test() throws Exception {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "215727557445");
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        log.info("测试结果：{}", response.getBody());

        String total = response.getTotalAmount();
        BigDecimal bigDecimal = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
        log.info("交易金额：{}", bigDecimal);

        String tradeStatus = response.getTradeStatus();
        log.info("交易状态：{}", tradeStatus);

        String tradeNo = response.getTradeNo();
        log.info("交易号：{}", tradeNo);
    }
}
