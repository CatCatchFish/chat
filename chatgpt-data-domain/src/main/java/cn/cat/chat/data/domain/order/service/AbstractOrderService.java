package cn.cat.chat.data.domain.order.service;

import cn.cat.chat.data.domain.order.adapter.port.IProductPort;
import cn.cat.chat.data.domain.order.model.entity.*;
import cn.cat.chat.data.domain.order.model.valobj.MarketTypeVO;
import cn.cat.chat.data.domain.order.model.valobj.PayStatusVO;
import cn.cat.chat.data.domain.order.adapter.repository.IOrderRepository;
import cn.cat.chat.data.types.common.Constants;
import cn.cat.chat.data.types.exception.ChatGPTException;
import com.alipay.api.AlipayApiException;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
public abstract class AbstractOrderService implements IOrderService {

    @Resource
    protected IOrderRepository orderRepository;
    @Resource
    protected IProductPort productPort;

    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws AlipayApiException {
        // 0. 基础信息
        String openid = shopCartEntity.getOpenid();
        Integer productId = shopCartEntity.getProductId();
        // 1. 查询当前用户是否存在掉单和未支付订单
        UnpaidOrderEntity unpaidOrderEntity = orderRepository.queryUnpaidOrder(shopCartEntity);
        if (null != unpaidOrderEntity && PayStatusVO.WAIT.equals(unpaidOrderEntity.getPayStatus()) && null != unpaidOrderEntity.getPayUrl()) {
            // 存在未支付订单，直接返回
            log.info("创建订单-存在，已生成微信支付，返回 openid: {} orderId: {} payUrl: {}", openid, unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getPayUrl());
            return PayOrderEntity.builder()
                    .openid(openid)
                    .orderId(unpaidOrderEntity.getOrderId())
                    .payUrl(unpaidOrderEntity.getPayUrl())
                    .payStatus(unpaidOrderEntity.getPayStatus())
                    .build();
        } else if (null != unpaidOrderEntity && null == unpaidOrderEntity.getPayUrl()) {
            // 不存在未支付订单，生成新订单
            Integer marketType = unpaidOrderEntity.getMarketType();
            BigDecimal marketDeductionAmount = unpaidOrderEntity.getMarketDeductionAmount();

            PayOrderEntity payOrderEntity = null;

            if (MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(marketType) && null == marketDeductionAmount) {
                // 团购订单，调用锁单接口
                MarketPayDiscountEntity marketPayDiscountEntity = this.lockMarketPayOrder(shopCartEntity.getOpenid(),
                        shopCartEntity.getTeamId(),
                        shopCartEntity.getActivityId(),
                        shopCartEntity.getProductId().toString(),
                        unpaidOrderEntity.getOrderId());
                payOrderEntity = doPrepayOrder(shopCartEntity.getOpenid(),
                        unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getTotalAmount(), marketPayDiscountEntity);
            } else if (MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(marketType)) {
                payOrderEntity = doPrepayOrder(shopCartEntity.getOpenid(),
                        unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getPayAmount());
            } else {
                payOrderEntity = doPrepayOrder(shopCartEntity.getOpenid(),
                        unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getTotalAmount());
            }

            return payOrderEntity;
        }

        // 2. 商品查询
        ProductEntity productEntity = orderRepository.queryProduct(productId);
        if (!productEntity.isAvailable()) {
            throw new ChatGPTException(Constants.ResponseCode.ORDER_PRODUCT_ERR.getCode(), Constants.ResponseCode.ORDER_PRODUCT_ERR.getInfo());
        }

        // 3. 订单保存
        OrderEntity orderEntity = this.doSaveOrder(openid, shopCartEntity.getMarketType().getCode(), productEntity);
        log.info("创建订单-完成，保存订单。openid: {} orderId: {}", openid, orderEntity.getOrderId());

        // 营销锁单
        MarketPayDiscountEntity marketPayDiscountEntity = null;
        if (MarketTypeVO.GROUP_BUY_MARKET.equals(shopCartEntity.getMarketType())) {
            // 团购锁单
            marketPayDiscountEntity = lockMarketPayOrder(shopCartEntity.getOpenid(),
                    shopCartEntity.getTeamId(),
                    shopCartEntity.getActivityId(),
                    productId.toString(),
                    orderEntity.getOrderId());
        }

        // 4. 创建支付
        PayOrderEntity payOrderEntity = this.doPrepayOrder(
                openid,
                productEntity.getProductName(),
                orderEntity.getOrderId(),
                productEntity.getPrice(),
                marketPayDiscountEntity);

        log.info("创建订单-完成，生成支付单。openid: {} orderId: {} payUrl: {}", openid, orderEntity.getOrderId(), payOrderEntity.getPayUrl());

        return payOrderEntity;
    }

    protected abstract OrderEntity doSaveOrder(String openid, int marketType, ProductEntity productEntity);

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException;

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productName, String orderId, BigDecimal totalAmount, MarketPayDiscountEntity marketPayDiscountEntity) throws AlipayApiException;

    protected abstract MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId);

}
