package cn.cat.chat.data.trigger.http.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePayRequestDTO {

    // 产品编号
    private Integer productId;
    // 拼团队伍 - 队伍ID
    private String teamId;
    // 活动ID，来自于页面调用拼团试算后，获得的活动ID信息
    private Long activityId;
    // 营销类型 - 0无营销
    private Integer marketType = 0;

}
