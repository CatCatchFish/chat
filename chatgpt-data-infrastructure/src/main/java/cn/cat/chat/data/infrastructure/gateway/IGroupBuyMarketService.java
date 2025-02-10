package cn.cat.chat.data.infrastructure.gateway;

import cn.cat.api.dto.LockMarketPayOrderRequestDTO;
import cn.cat.api.dto.LockMarketPayOrderResponseDTO;
import cn.cat.api.response.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @description 拼团营销
 */
public interface IGroupBuyMarketService {

    /**
     * 营销锁单
     *
     * @param requestDTO 锁单商品信息
     * @return 锁单结果信息
     */
    @POST("api/v1/gbm/trade/lock_market_pay_order")
    Call<Response<LockMarketPayOrderResponseDTO>> lockMarketPayOrder(@Body LockMarketPayOrderRequestDTO requestDTO);

}
