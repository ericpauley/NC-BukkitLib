package com.nodinchan.ncloader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class Reflection {
	
	public static Set<Field> getFields(Object object, Class<? extends Annotation>... annotations) {
		Set<Field> fields = new HashSet<Field>();
		
		invalid:
			for (Field field : object.getClass().getFields()) {
				for (Class<? extends Annotation> annotation : annotations) {
					if (field.getAnnotation(annotation) == null)
						continue invalid;
				}
			}
		
		return fields;
	}
	
	public static Set<Method> getMethods(Object object, Class<? extends Annotation>... annotations) {
		Set<Method> methods = new HashSet<Method>();
		
		invalid:
			for (Method method : object.getClass().getMethods()) {
				for (Class<? extends Annotation> annotation : annotations) {
					if (method.getAnnotation(annotation) == null)
						continue invalid;
				}
			}
		
		return methods;
	}
}