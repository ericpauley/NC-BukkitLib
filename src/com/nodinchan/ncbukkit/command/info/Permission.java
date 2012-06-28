package com.nodinchan.ncbukkit.command.info;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	
	String value() default "";
}