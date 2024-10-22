package cn.cat.chat.data.domain.auth.repository;

public interface IAuthRepository {

    String getCodeUserOpenId(String code);

    void removeCodeByOpenId(String code, String openId);
}
