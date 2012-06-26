package com.nodinchan.ncbukkit.command.casting;

public abstract class Parameter<T> {
	
	/**
	 * The class to cast the parameter to
	 * 
	 * @return The class to cast to
	 */
	public abstract Class<T> castTo();
	
	/**
	 * Cast the argument to the class
	 * 
	 * @param argument The argument given
	 * 
	 * @return The casted argument
	 * 
	 * @throws Exception
	 */
	public abstract T cast(String argument) throws Exception;
}