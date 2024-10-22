package cn.cat.chat.data.trigger.http;

import cn.cat.chat.data.domain.auth.model.entity.AuthStateEntity;
import cn.cat.chat.data.domain.auth.model.valobj.AuthTypeVO;
import cn.cat.chat.data.domain.auth.service.AuthService;
import cn.cat.chat.data.domain.weixin.model.entity.MessageTextEntity;
import cn.cat.chat.data.domain.weixin.model.entity.UserBehaviorMessageEntity;
import cn.cat.chat.data.domain.weixin.model.valobj.MsgTypeVO;
import cn.cat.chat.data.domain.weixin.service.IWeiXinBehaviorService;
import cn.cat.chat.data.types.common.Constants;
import cn.cat.chat.data.types.model.Response;
import cn.cat.chat.data.types.sdk.weixin.XmlUtil;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/${app.config.api-version}/auth")
public class AuthController {
    @Resource
    private AuthService authService;
    @Resource
    private IWeiXinBehaviorService weiXinBehaviorService;

    @PostMapping("/genCode")
    public Response<String> genCode(@RequestParam String openid) {
        log.info("生成验证码开始，用户ID: {}", openid);
        try {
            UserBehaviorMessageEntity entity = new UserBehaviorMessageEntity();
            entity.setOpenId(openid);
            entity.setMsgType(MsgTypeVO.TEXT.getCode());
            entity.setContent("405");
            String xml = weiXinBehaviorService.acceptUserBehavior(entity);
            MessageTextEntity messageTextEntity = XmlUtil.xmlToBean(xml, MessageTextEntity.class);
            log.info("生成验证码完成，用户ID: {} 生成结果：{}", openid, messageTextEntity.getContent());
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(messageTextEntity.getContent())
                    .build();
        } catch (Exception e) {
            log.info("生成验证码失败，用户ID: {}", openid);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                    .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("/login")
    public Response<String> doLogin(@RequestParam String code) {
        log.info("鉴权检验，验证码：{}", code);
        try {
            AuthStateEntity authStateEntity = authService.doLogin(code);
            log.info("鉴权登录校验完成，验证码: {} 结果: {}", code, JSON.toJSONString(authStateEntity));
            // 拦截，鉴权失败
            if (!AuthTypeVO.A0000.getCode().equals(authStateEntity.getCode())) {
                return Response.<String>builder()
                        .code(authStateEntity.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }

            // 放行，鉴权成功
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(authStateEntity.getToken())
                    .build();
        } catch (Exception e) {
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
