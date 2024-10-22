package cn.cat.chat.data.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpenAIProductEnableModel {

    CLOSE(0, "无效，已关闭"),
    OPEN(1, "有效，使用中"),
    ;

    private final Integer code;

    private final String info;

    public static OpenAIProductEnableModel get(Integer code) {
        return switch (code) {
            case 1 -> OPEN;
            default -> CLOSE;
        };

    }

}
