package cn.cat.chat.data.infrastructure.dao;

import cn.cat.chat.data.infrastructure.po.UserAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserAccountDao {
    int subAccountQuota(String openid);

    UserAccount queryUserAccount(String openid);

    void insert(UserAccount userAccount);

    int addAccountQuota(UserAccount userAccountPOReq);
}
