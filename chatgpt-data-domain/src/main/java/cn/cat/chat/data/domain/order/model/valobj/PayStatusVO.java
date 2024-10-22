package cn.cat.chat.data.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayStatusVO {
    WAIT(0, "等待支付"),
    SUCCESS(1, "支付完成"),
    FAIL(2, "支付失败"),
    ABANDON(3, "放弃支付"),
    ;
    private final Integer code;
    private final String desc;

    public static PayStatusVO get(Integer code) {
        return switch (code) {
            case 1 -> SUCCESS;
            case 2 -> FAIL;
            case 3 -> ABANDON;
            default -> WAIT;
        };
    }
}
