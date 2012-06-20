package com.nodinchan.nclib.command.info;

public @interface CommandInfo {
	
	String name();
	
	int minArgs() default 0;
	
	int maxArgs() default 0;
}