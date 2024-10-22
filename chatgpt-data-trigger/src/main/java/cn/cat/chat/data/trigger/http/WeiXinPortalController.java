package cn.cat.chat.data.trigger.http;

import cn.cat.chat.data.domain.weixin.model.entity.MessageTextEntity;
import cn.cat.chat.data.domain.weixin.model.entity.UserBehaviorMessageEntity;
import cn.cat.chat.data.domain.weixin.service.IWeiXinBehaviorService;
import cn.cat.chat.data.domain.weixin.service.IWeiXinValidateService;
import cn.cat.chat.data.types.sdk.weixin.XmlUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/${app.config.api-version}/wx/portal/{appid}")
public class WeiXinPortalController {
    @Value("${wx.config.originalid}")
    private String originalId;
    @Resource
    private IWeiXinValidateService weiXinValidateService;
    @Resource
    private IWeiXinBehaviorService weiXinBehaviorService;

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String validate(@PathVariable String appid,
                           @RequestParam(value = "signature", required = false) String signature,
                           @RequestParam(value = "timestamp", required = false) String timestamp,
                           @RequestParam(value = "nonce", required = false) String nonce,
                           @RequestParam(value = "echostr", required = false) String echostr) {
        try {
            log.info("微信公众号验签信息{}开始 [{}, {}, {}, {}]", appid, signature, timestamp, nonce, echostr);
            if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
                throw new IllegalArgumentException("请求参数非法，请核实!");
            }
            boolean check = weiXinValidateService.checkSign(signature, timestamp, nonce);
            log.info("微信公众号验签信息{}完成 check：{}", appid, check);
            if (!check) {
                return null;
            }
            return echostr;
        } catch (Exception e) {
            log.error("微信公众号验签信息{}失败 [{}, {}, {}, {}]", appid, signature, timestamp, nonce, echostr, e);
            return null;
        }
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String appid,
                       @RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        try {
            log.info("接收微信公众号信息请求：{}\n开始，请求体\n {}", openid, requestBody);
            MessageTextEntity message = XmlUtil.xmlToBean(requestBody, MessageTextEntity.class);
            UserBehaviorMessageEntity entity = UserBehaviorMessageEntity.builder()
                    .openId(openid)
                    .msgType(message.getMsgType())
                    .content(StringUtils.isBlank(message.getContent()) ? null : message.getContent().trim())
                    .fromUserName(message.getFromUserName())
                    .event(message.getEvent())
                    .createTime(new Date(Long.parseLong(message.getCreateTime()) * 1000L))
                    .build();
            String result = weiXinBehaviorService.acceptUserBehavior(entity);
            log.info("接收微信公众号信息请求：{}，完成，返回结果\n {}", openid, result);
            return result;
        } catch (Exception e) {
            log.error("接收微信公众号信息请求{}失败 {}", openid, requestBody, e);
            return "";
        }
    }
}
