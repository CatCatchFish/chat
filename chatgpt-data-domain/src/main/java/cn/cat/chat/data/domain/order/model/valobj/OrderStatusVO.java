package cn.cat.chat.data.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusVO {
    CREATE(0, "创建完成"),
    WAIT(1, "等待发货"),
    COMPLETED(2, "发货完成"),
    CLOSE(3, "系统关单"),
    ;
    private final Integer code;
    private final String desc;

    public static OrderStatusVO get(Integer code) {
        return switch (code) {
            case 1 -> WAIT;
            case 2 -> COMPLETED;
            case 3 -> CLOSE;
            default -> CREATE;
        };
    }

}
