package cn.cat.chat.data.domain.openai.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserAccountStatusVO {
    AVAILABLE(0, "可用"),
    FREEZE(1, "冻结");
    private final Integer code;
    private final String info;

    public static UserAccountStatusVO get(Integer code) {
        return switch (code) {
            case 0 -> UserAccountStatusVO.AVAILABLE;
            case 1 -> UserAccountStatusVO.FREEZE;
            default -> UserAccountStatusVO.AVAILABLE;
        };
    }
}
