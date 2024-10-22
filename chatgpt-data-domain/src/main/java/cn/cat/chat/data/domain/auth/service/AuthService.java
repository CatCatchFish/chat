package cn.cat.chat.data.domain.auth.service;

import cn.cat.chat.data.domain.auth.model.entity.AuthStateEntity;
import cn.cat.chat.data.domain.auth.model.valobj.AuthTypeVO;
import cn.cat.chat.data.domain.auth.repository.IAuthRepository;
import com.google.common.cache.Cache;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService extends AbstractAuthService {
    @Resource
    private IAuthRepository repository;


    @Override
    public boolean checkToken(String token) {
        return isVerify(token);
    }

    @Override
    public String openid(String token) {
        Claims claims = decode(token);
        return claims.get("openId").toString();
    }

    @Override
    protected AuthStateEntity checkCode(String code) {
        String openId = repository.getCodeUserOpenId(code);
        if (StringUtils.isEmpty(openId)) {
            log.info("用户输入的验证码不存在：{}", code);
            return AuthStateEntity.builder()
                    .code(AuthTypeVO.A0001.getCode())
                    .info(AuthTypeVO.A0001.getInfo())
                    .build();
        }

        // 移除缓存Key值
        repository.removeCodeByOpenId(code, openId);

        // 验证码校验成功
        return AuthStateEntity.builder()
                .code(AuthTypeVO.A0000.getCode())
                .info(AuthTypeVO.A0000.getInfo())
                .openId(openId)
                .build();
    }
}
