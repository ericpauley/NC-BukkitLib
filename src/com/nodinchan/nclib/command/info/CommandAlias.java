package com.nodinchan.nclib.command.info;

public @interface CommandAlias {
	
	String[] aliases() default {};
}