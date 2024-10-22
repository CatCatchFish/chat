package cn.cat.chat.data.domain.weixin.service.validate;

import cn.cat.chat.data.domain.weixin.service.IWeiXinValidateService;
import cn.cat.chat.data.types.sdk.weixin.SignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WeiXinValidateService implements IWeiXinValidateService {
    @Value("${wx.config.token}")
    private String token;

    @Override
    public boolean checkSign(String signature, String timestamp, String nonce) {
        return SignatureUtil.check(token, signature, timestamp, nonce);
    }
}
