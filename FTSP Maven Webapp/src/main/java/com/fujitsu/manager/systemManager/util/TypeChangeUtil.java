package com.fujitsu.manager.systemManager.util;
public class TypeChangeUtil {
	//根据对象具体类型转成Integer
	public static Integer changeObjToInteger(Object o){
		if(o instanceof String){
			return Integer.parseInt((String)o);
		}
		if(o instanceof Long){
			return ((Long)o).intValue();
		}
		if(o instanceof Integer){
			return (Integer)o;
		}
		return 0;
	}
	
	
	public static void main(String[] args) {
		
		
		

	}

}
