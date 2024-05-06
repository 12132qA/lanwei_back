package org.example.annotation;

import org.example.enums.UserOperFrequencyTyoeEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GlobalInterceptor {

    /**
     *
     * 是否需要登录
     * */
    boolean checkLogin() default false;

    /**
     *是否需要校验参数
     * */
    boolean checkParams() default false;


    boolean checkParam() default false;

    /**
     * 频次校验
     *
     * */

    UserOperFrequencyTyoeEnum frequencyType() default UserOperFrequencyTyoeEnum.NO_CHECK;
}
