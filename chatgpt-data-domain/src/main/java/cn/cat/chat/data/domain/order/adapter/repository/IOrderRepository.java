package cn.cat.chat.data.domain.order.adapter.repository;

import cn.cat.chat.data.domain.order.model.aggregates.CreateOrderAggregate;
import cn.cat.chat.data.domain.order.model.entity.PayOrderEntity;
import cn.cat.chat.data.domain.order.model.entity.ProductEntity;
import cn.cat.chat.data.domain.order.model.entity.ShopCartEntity;
import cn.cat.chat.data.domain.order.model.entity.UnpaidOrderEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description 订单仓储接口
 */
public interface IOrderRepository {
    // 查询未支付订单
    UnpaidOrderEntity queryUnpaidOrder(ShopCartEntity shopCartEntity);

    // 查询商品
    ProductEntity queryProduct(Integer productId);

    // 保存订单
    void saveOrder(CreateOrderAggregate aggregate);

    // 更新订单支付信息
    void updateOrderPayInfo(PayOrderEntity payOrderEntity);

    // 更新订单状态为支付成功
    boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, Date payTime);

    // 查询订单
    CreateOrderAggregate queryOrder(String orderId);

    // 订单商品发货
    void deliverGoods(String orderId);

    // 查询待补货订单
    List<String> queryReplenishmentOrder();

    // 查询有效期内，未接收到支付回调的订单
    List<String> queryNoPayNotifyOrder();

    // 查询超时关闭订单
    List<String> queryTimeoutCloseOrderList();

    // 关闭订单
    boolean changeOrderClose(String orderId);

    // 查询所有商品
    List<ProductEntity> queryProductList();
}
