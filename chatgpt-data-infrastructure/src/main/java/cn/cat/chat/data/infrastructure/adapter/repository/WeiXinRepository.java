package cn.cat.chat.data.infrastructure.adapter.repository;

import cn.cat.chat.data.domain.weixin.repository.IWeiXinRepository;
import cn.cat.chat.data.infrastructure.redis.IRedisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class WeiXinRepository implements IWeiXinRepository {
    private static final String Key = "weixin_code";

    @Resource
    private IRedisService redisService;

    @Override
    public String genCode(String openId) {
        // 获取值
        String isExitCode = redisService.getValue(Key + "_" + openId);
        if (StringUtils.isNotBlank(isExitCode)) return isExitCode;
        // 生成值 分布式锁
        RLock lock = redisService.getLock(Key);
        try {
            lock.lock(15, TimeUnit.SECONDS);

            String code = RandomStringUtils.randomNumeric(4);
            // 防重校验&重新生成
            for (int i = 0; i < 10 && StringUtils.isNotBlank(redisService.getValue(Key + "_" + code)); i++) {
                if (i < 3) {
                    code = RandomStringUtils.randomNumeric(4);
                } else if (i < 5) {
                    code = RandomStringUtils.randomNumeric(5);
                } else if (i < 9) {
                    code = RandomStringUtils.randomNumeric(6);
                    log.warn("验证码重复，生成6位字符串验证码 {} {}", openId, code);
                } else {
                    return "您的验证码获取失败，请重新回复405获取。";
                }
            }
            // 保存值
            redisService.setValue(Key + "_" + code, openId, 3 * 60 * 1000);
            redisService.setValue(Key + "_" + openId, code, 3 * 60 * 1000);
            return code;
        } finally {
            lock.unlock();
        }
    }
}
