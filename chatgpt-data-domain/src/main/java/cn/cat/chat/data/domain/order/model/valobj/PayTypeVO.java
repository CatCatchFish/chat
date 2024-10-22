package cn.cat.chat.data.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayTypeVO {
    ALIPAY(0, "支付宝");
    private final Integer code;
    private final String desc;

    public static PayTypeVO get(Integer code) {
        return switch (code) {
            default -> ALIPAY;
        };
    }
}
