package cn.cat.chat.data.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarketTypeVO {

    NO_MARKET(0, "无营销"),
    GROUP_BUY_MARKET(1, "团购营销"),
    ;

    private final Integer code;
    private final String desc;

    public static MarketTypeVO valueOf(Integer code) {
        switch (code) {
            case 0:
                return NO_MARKET;
            case 1:
                return GROUP_BUY_MARKET;
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

}
