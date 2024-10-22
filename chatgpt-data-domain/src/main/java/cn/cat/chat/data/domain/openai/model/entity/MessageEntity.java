package cn.cat.chat.data.domain.openai.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {
    private String role;
    private String content;
    private String name;

    public String getRole() {
        if (!role.matches("\\d+")) {
            return role;
        }
        return switch (Integer.parseInt(role)) {
            case 1 -> "user";
            case 2 -> "assistant";
            default -> "system";
        };
    }
}
