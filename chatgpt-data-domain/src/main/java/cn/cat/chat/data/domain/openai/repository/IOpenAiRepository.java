package cn.cat.chat.data.domain.openai.repository;

import cn.cat.chat.data.domain.openai.model.entity.UserAccountQuotaEntity;

public interface IOpenAiRepository {
    int subAccountQuota(String openai);

    UserAccountQuotaEntity queryUserAccount(String openid);
}
