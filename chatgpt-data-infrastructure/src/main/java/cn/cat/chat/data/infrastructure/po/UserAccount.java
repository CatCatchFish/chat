package cn.cat.chat.data.infrastructure.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccount {
    private Long id;
    private String openid;
    private Integer totalQuota;
    private Integer surplusQuota;
    private String modelTypes;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
