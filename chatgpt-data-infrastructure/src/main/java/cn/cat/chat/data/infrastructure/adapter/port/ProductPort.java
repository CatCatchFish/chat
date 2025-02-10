package cn.cat.chat.data.infrastructure.adapter.port;

import cn.cat.api.dto.LockMarketPayOrderRequestDTO;
import cn.cat.api.dto.LockMarketPayOrderResponseDTO;
import cn.cat.api.response.Response;
import cn.cat.chat.data.domain.order.adapter.port.IProductPort;
import cn.cat.chat.data.domain.order.model.entity.MarketPayDiscountEntity;
import cn.cat.chat.data.infrastructure.gateway.IGroupBuyMarketService;
import cn.cat.types.enums.ResponseCode;
import cn.cat.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;

@Slf4j
@Component
public class ProductPort implements IProductPort {

    @Value("${app.config.group-buy-market.notify-url}")
    private String notifyUrl;
    @Value("${app.config.group-buy-market.source}")
    private String source;
    @Value("${app.config.group-buy-market.channel}")
    private String channel;

    private final IGroupBuyMarketService groupBuyMarketService;

    public ProductPort(IGroupBuyMarketService groupBuyMarketService) {
        this.groupBuyMarketService = groupBuyMarketService;
    }

    @Override
    public MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId) {
        LockMarketPayOrderRequestDTO requestDTO = LockMarketPayOrderRequestDTO.builder()
                .userId(userId)
                .teamId(teamId)
                .activityId(activityId)
                .goodsId(productId)
                .source(source)
                .channel(channel)
                .outTradeNo(orderId)
                .notifyUrl(notifyUrl)
                .build();

        try {
            Call<Response<LockMarketPayOrderResponseDTO>> responseCall = groupBuyMarketService.lockMarketPayOrder(requestDTO);
            Response<LockMarketPayOrderResponseDTO> response = responseCall.execute().body();
            if (null == response) return null;

            if (!ResponseCode.SUCCESS.getCode().equals(response.getCode())) {
                throw new AppException(response.getCode(), response.getInfo());
            }

            LockMarketPayOrderResponseDTO responseDTO = response.getData();

            return MarketPayDiscountEntity.builder()
                    .originalPrice(responseDTO.getOriginalPrice())
                    .deductionPrice(responseDTO.getDeductionPrice())
                    .payPrice(responseDTO.getPayPrice())
                    .build();
        } catch (Exception e) {
            log.error("锁定商品订单失败 {}", userId, e);
            return null;
        }
    }

}
