package org.example.annotation;

import com.baomidou.mybatisplus.extension.api.R;
import org.example.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//  parameter
@Target({ElementType.PARAMETER,ElementType.FIELD})

@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyParam {

    boolean required() default false;
    /**
     * 正则
     * */
    int max() default  -1;
    int min() default -1;


    /**
     * 正则表达式
     * */

    VerifyRegexEnum regx() default VerifyRegexEnum.NO;


}
