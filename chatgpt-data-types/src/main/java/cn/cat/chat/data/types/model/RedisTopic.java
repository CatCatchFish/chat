package cn.cat.chat.data.types.model;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RedisTopic {
    String topic() default "";
}