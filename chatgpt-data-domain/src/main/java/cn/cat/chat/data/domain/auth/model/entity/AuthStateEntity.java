package cn.cat.chat.data.domain.auth.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cat
 * 授权状态实体类
 * @date 2021/01/26 16:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthStateEntity {
    /**
     * 授权状态码
     */
    private String code;
    /**
     * 授权信息
     */
    private String info;
    /**
     * 授权用户的openId
     */
    private String openId;
    /**
     * 授权用户的token
     * 用于验证用户身份
     */
    private String token;
}
