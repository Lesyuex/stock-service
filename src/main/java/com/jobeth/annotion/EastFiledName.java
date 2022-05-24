package com.jobeth.annotion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/24 10:37
 * Description: -
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EastFiledName {
    String value() default "";
}
