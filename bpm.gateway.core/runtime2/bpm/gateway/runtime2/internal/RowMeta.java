package bpm.gateway.runtime2.internal;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;

public class RowMeta {
	private List<Class<?>> javaClasses = new ArrayList<Class<?>>();
	
	protected RowMeta(StreamDescriptor descriptor) throws Exception{
		for(StreamElement e : descriptor.getStreamElements()){
			try {
				if(e.className != null && !e.className.isEmpty()) {
					javaClasses.add(Class.forName(e.className));
				}
				else {
					javaClasses.add(Class.forName("java.lang.String"));
				}
			} 
			catch (ClassNotFoundException e2) {
				try {
					
					javaClasses.add(Class.forName(e.className, true, this.getClass().getClassLoader()));
				} catch (Exception e1) {
					//XXX Because Oracle is a pile of shit
					javaClasses.add(Class.forName("java.sql.Timestamp"));
				}
			}
		}
	}
	
	public Class<?> getJavaClasse(int colNumber){
		return javaClasses.get(colNumber);
	}
	
	public int getSize(){
		return javaClasses.size();
	}
}
