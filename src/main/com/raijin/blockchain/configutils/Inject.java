package com.raijin.blockchain.configutils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

    String value() default "";

}
