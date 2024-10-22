package cn.cat.chat.data.domain.order.service;

import cn.cat.chat.data.domain.order.model.entity.*;
import cn.cat.chat.data.domain.order.model.valobj.PayStatusVO;
import cn.cat.chat.data.domain.order.repository.IOrderRepository;
import cn.cat.chat.data.types.common.Constants;
import cn.cat.chat.data.types.exception.ChatGPTException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
public abstract class AbstractOrderService implements IOrderService {
    @Resource
    protected IOrderRepository orderRepository;

    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) {
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
        } else  if (null != unpaidOrderEntity && null == unpaidOrderEntity.getPayUrl()) {
            // 不存在未支付订单，生成新订单
            log.info("创建订单-存在，未生成微信支付，返回 openid: {} orderId: {}", openid, Objects.requireNonNull(unpaidOrderEntity).getOrderId());
            PayOrderEntity payOrderEntity = this.doPrepayOrder(openid, unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getProductName(), unpaidOrderEntity.getTotalAmount());
            log.info("创建订单-完成，生成支付单。openid: {} orderId: {} payUrl: {}", openid, payOrderEntity.getOrderId(), payOrderEntity.getPayUrl());
            return payOrderEntity;
        }

        // 2. 商品查询
        ProductEntity productEntity = orderRepository.queryProduct(productId);
        if (!productEntity.isAvailable()) {
            throw new ChatGPTException(Constants.ResponseCode.ORDER_PRODUCT_ERR.getCode(), Constants.ResponseCode.ORDER_PRODUCT_ERR.getInfo());
        }

        // 3. 订单保存
        OrderEntity orderEntity = this.doSaveOrder(openid, productEntity);
        log.info("创建订单-完成，保存订单。openid: {} orderId: {}", openid, orderEntity.getOrderId());

        // 4. 创建支付
        PayOrderEntity payOrderEntity = this.doPrepayOrder(openid, orderEntity.getOrderId(), productEntity.getProductName(), orderEntity.getTotalAmount());
        log.info("创建订单-完成，生成支付单。openid: {} orderId: {} payUrl: {}", openid, orderEntity.getOrderId(), payOrderEntity.getPayUrl());

        return payOrderEntity;
    }

    protected abstract OrderEntity doSaveOrder(String openid, ProductEntity productEntity);

    protected abstract PayOrderEntity doPrepayOrder(String openid, String orderId, String productName, BigDecimal amountTotal);
}
