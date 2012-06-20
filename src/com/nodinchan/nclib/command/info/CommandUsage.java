package com.nodinchan.nclib.command.info;

public @interface CommandUsage {
	
	String usage() default "/<command>";
}