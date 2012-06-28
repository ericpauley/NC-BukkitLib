package com.nodinchan.ncbukkit.command.casting;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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