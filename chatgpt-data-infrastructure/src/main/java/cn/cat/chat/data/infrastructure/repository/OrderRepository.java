package cn.cat.chat.data.infrastructure.repository;

import cn.cat.chat.data.domain.openai.model.valobj.UserAccountStatusVO;
import cn.cat.chat.data.domain.order.model.aggregates.CreateOrderAggregate;
import cn.cat.chat.data.domain.order.model.entity.*;
import cn.cat.chat.data.domain.order.model.valobj.OrderStatusVO;
import cn.cat.chat.data.domain.order.model.valobj.PayStatusVO;
import cn.cat.chat.data.domain.order.repository.IOrderRepository;
import cn.cat.chat.data.infrastructure.dao.IOpenAIOrderDao;
import cn.cat.chat.data.infrastructure.dao.IOpenAIProductDao;
import cn.cat.chat.data.infrastructure.dao.IUserAccountDao;
import cn.cat.chat.data.infrastructure.po.OpenAIOrder;
import cn.cat.chat.data.infrastructure.po.OpenAIProduct;
import cn.cat.chat.data.infrastructure.po.UserAccount;
import cn.cat.chat.data.types.enums.ChatGLMModel;
import cn.cat.chat.data.types.enums.OpenAIProductEnableModel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class OrderRepository implements IOrderRepository {
    @Resource
    private IOpenAIOrderDao openAIOrderDao;
    @Resource
    private IOpenAIProductDao openAIProductDao;
    @Resource
    private IUserAccountDao userAccountDao;

    @Override
    public UnpaidOrderEntity queryUnpaidOrder(ShopCartEntity shopCartEntity) {
        OpenAIOrder orderReq = new OpenAIOrder();
        orderReq.setOpenid(shopCartEntity.getOpenid());
        orderReq.setProductId(shopCartEntity.getProductId());
        OpenAIOrder orderRes = openAIOrderDao.queryUnpaidOrder(orderReq);
        // 订单不存在 返回null
        if (orderRes == null) return null;
        // 订单存在 构造返回对象
        return UnpaidOrderEntity.builder()
                .openid(shopCartEntity.getOpenid())
                .orderId(orderRes.getOrderId())
                .productName(orderRes.getProductName())
                .totalAmount(orderRes.getTotalAmount())
                .payUrl(orderRes.getPayUrl())
                .payStatus(PayStatusVO.get(orderRes.getPayStatus()))
                .build();
    }

    @Override
    public ProductEntity queryProduct(Integer productId) {
        OpenAIProduct product = openAIProductDao.queryProductByProductId(productId);
        return ProductEntity.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productDesc(product.getProductDesc())
                .quota(product.getQuota())
                .price(product.getPrice())
                .enable(OpenAIProductEnableModel.get(product.getIsEnabled()))
                .build();
    }

    @Override
    public void saveOrder(CreateOrderAggregate aggregate) {
        String openid = aggregate.getOpenid();
        ProductEntity product = aggregate.getProduct();
        OrderEntity order = aggregate.getOrder();
        OpenAIOrder openAIOrder = new OpenAIOrder();
        openAIOrder.setOpenid(openid);
        openAIOrder.setProductId(product.getProductId());
        openAIOrder.setProductName(product.getProductName());
        openAIOrder.setProductQuota(product.getQuota());
        openAIOrder.setOrderId(order.getOrderId());
        openAIOrder.setOrderTime(order.getOrderTime());
        openAIOrder.setOrderStatus(order.getOrderStatus().getCode());
        openAIOrder.setTotalAmount(order.getTotalAmount());
        openAIOrder.setPayType(order.getPayTypeVO().getCode());
        openAIOrder.setPayStatus(PayStatusVO.WAIT.getCode());
        openAIOrderDao.insert(openAIOrder);
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrderEntity) {
        OpenAIOrder order = new OpenAIOrder();
        order.setOrderId(payOrderEntity.getOrderId());
        order.setOpenid(payOrderEntity.getOpenid());
        order.setPayUrl(payOrderEntity.getPayUrl());
        order.setPayStatus(payOrderEntity.getPayStatus().getCode());
        openAIOrderDao.updateOrderPayInfo(order);
    }

    @Override
    public boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, Date payTime) {
        OpenAIOrder order = new OpenAIOrder();
        order.setOrderId(orderId);
        order.setTransactionId(transactionId);
        order.setPayTime(payTime);
        // payAmount
        order.setPayAmount(totalAmount);
        int count = openAIOrderDao.changeOrderPaySuccess(order);
        return count == 1;
    }

    @Override
    public CreateOrderAggregate queryOrder(String orderId) {
        // 查询订单
        OpenAIOrder order = openAIOrderDao.queryOrder(orderId);
        // 查询商品
        ProductEntity product = new ProductEntity();
        product.setProductId(order.getProductId());
        product.setProductName(order.getProductName());
        // 包装订单
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(order.getOrderId());
        orderEntity.setOrderTime(order.getOrderTime());
        orderEntity.setOrderStatus(OrderStatusVO.get(order.getOrderStatus()));
        orderEntity.setTotalAmount(order.getTotalAmount());

        // 包装聚合对象
        CreateOrderAggregate createOrderAggregate = new CreateOrderAggregate();
        createOrderAggregate.setOpenid(order.getOpenid());
        createOrderAggregate.setProduct(product);
        createOrderAggregate.setOrder(orderEntity);
        return createOrderAggregate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 350, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public void deliverGoods(String orderId) {
        OpenAIOrder order = openAIOrderDao.queryOrder(orderId);

        // 1、修改发货状态
        int deliverGoodsCount = openAIOrderDao.updateOrderStatusDeliverGoods(orderId);
        if (1 != deliverGoodsCount) {
            throw new RuntimeException("updateOrderStatusDeliverGoodsCount update count is not equal 1");
        }

        // 2、修改用户额度
        UserAccount userAccount = userAccountDao.queryUserAccount(order.getOpenid());
        UserAccount userAccountReq = new UserAccount();
        userAccountReq.setOpenid(order.getOpenid());
        userAccountReq.setTotalQuota(order.getProductQuota());
        userAccountReq.setSurplusQuota(order.getProductQuota());
        if (null != userAccount) {
            // 账户存在，更新
            int addAccountQuotaCount = userAccountDao.addAccountQuota(userAccountReq);
            if (1 != addAccountQuotaCount) {
                throw new RuntimeException("addAccountQuotaCount update count is not equal 1");
            }
        } else {
            // 账户不存在，新增
            userAccountReq.setStatus(UserAccountStatusVO.AVAILABLE.getCode());
            userAccountReq.setModelTypes(ChatGLMModel.getModelTypes());
            userAccountDao.insert(userAccountReq);
        }
    }

    @Override
    public List<String> queryReplenishmentOrder() {
        return openAIOrderDao.queryReplenishmentOrder();
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return openAIOrderDao.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return openAIOrderDao.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return openAIOrderDao.changeOrderClose(orderId);
    }

    @Override
    public List<ProductEntity> queryProductList() {
        // 查询所有商品
        List<OpenAIProduct> openAIProducts = openAIProductDao.queryProductList();
        // 封装商品列表
        List<ProductEntity> productEntityList = new ArrayList<>();
        for (OpenAIProduct openAIProduct : openAIProducts) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProductId(openAIProduct.getProductId());
            productEntity.setProductName(openAIProduct.getProductName());
            productEntity.setProductDesc(openAIProduct.getProductDesc());
            productEntity.setQuota(openAIProduct.getQuota());
            productEntity.setPrice(openAIProduct.getPrice());
            productEntityList.add(productEntity);
        }
        return productEntityList;
    }
}
