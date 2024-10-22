package cn.cat.chat.data.domain.openai.model.entity;

import cn.cat.chat.data.domain.openai.model.valobj.UserAccountStatusVO;
import cn.cat.chat.data.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountQuotaEntity {
    private String openid;
    private Integer totalQuota;
    private Integer surplusQuota;
    /**
     * 模型类型；一个卡支持多个模型调用，这代表了允许使用的模型范围
     */
    private List<String> allowModelTypeList;
    /**
     * 账户状态
     */
    private UserAccountStatusVO userAccountStatusVO;
    /**
     * 状态VO对象
     */
    private UserAccountStatusVO statusVO;

    public void genModelTypes(String modelTypes) {
        String[] types = modelTypes.split(Constants.SPLIT);
        this.allowModelTypeList = Arrays.asList(types);
    }
}
