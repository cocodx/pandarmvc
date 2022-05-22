package com.javalol.mvc.annotation;


import java.lang.annotation.*;

//注解使用的地方
@Target(value = ElementType.FIELD)
//注解保留策略，一般是runtime
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    String value();
}
