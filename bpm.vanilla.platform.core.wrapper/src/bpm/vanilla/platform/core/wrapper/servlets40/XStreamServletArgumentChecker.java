package bpm.vanilla.platform.core.wrapper.servlets40;

import java.lang.reflect.Method;

import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class XStreamServletArgumentChecker {

	public void checkArguments(Method m, XmlArgumentsHolder holder) throws Exception{
		
		
		if (holder.getArguments().size() != m.getParameterTypes().length){
			throw new Exception("Bad argument number");
		}
		
		int i = 0;
		for(Class<?> c : m.getParameterTypes()){
			Object o = holder.getArguments().get(i++);
			if (o != null){
				if (c.isPrimitive()){
					//
				
					try{
						o.getClass().getMethod("valueOf", c);
					}catch(Exception ex){
						throw new Exception("Primitive type problem : unable to find the method valueOf(" + c.getName() + ") from class " + o.getClass().getName() );
					}
					
				}
				else if (!c.isAssignableFrom(o.getClass())){
					throw new Exception("Argument of type " + c.getName() + " expected instead of " + o.getClass().getName());
				}
			}
			else if (c.isPrimitive()){
				throw new Exception("Primitive argument expected and null value sended");
			}
		}
	}
}
