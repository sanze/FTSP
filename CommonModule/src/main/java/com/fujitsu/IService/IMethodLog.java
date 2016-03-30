package com.fujitsu.IService;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xuxiaojun
 * 
 */
@Target({ElementType.METHOD})     
@Retention(RetentionPolicy.RUNTIME)     
@Documented    
public @interface IMethodLog {
	
    String desc() default "无描述信息";
    InfoType type() default InfoType.SEARCH;
    
    public enum InfoType {
    	SEARCH , MOD , DELETE ;
    } 
}
