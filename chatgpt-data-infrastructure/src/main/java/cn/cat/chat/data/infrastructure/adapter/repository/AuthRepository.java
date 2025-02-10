package cn.cat.chat.data.infrastructure.adapter.repository;

import cn.cat.chat.data.domain.auth.repository.IAuthRepository;
import cn.cat.chat.data.infrastructure.redis.IRedisService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class AuthRepository implements IAuthRepository {
    private static final String Key = "weixin_code";

    @Resource
    private IRedisService redisService;

    @Override
    public String getCodeUserOpenId(String code) {
        return redisService.getValue(Key + "_" + code);
    }

    @Override
    public void removeCodeByOpenId(String code, String openId) {
        redisService.remove(Key + "_" + code);
        redisService.remove(Key + "_" + openId);
    }
}
