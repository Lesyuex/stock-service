package com.jobeth.annotion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface  QtIndex {
    String value() default "";
    String desc() default "";
}
