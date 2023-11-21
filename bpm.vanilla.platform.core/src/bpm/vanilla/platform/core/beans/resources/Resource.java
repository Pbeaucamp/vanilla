package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Resource implements Serializable {

	public enum TypeResource {
		USER(0),
		CIBLE(1),
		CERTIFICAT(2),
		SOURCE(3),
		XSD(4),
		MAIL(5),
		VARIABLE(6),
		PARAMETER(7),
		DATABASE_SERVER(8),
		CONNECTOR(9),
		SOCIAL_SERVER(10),
		LOV(11), 
		APPLICATION_SERVER(12);
		
		private int type;

		private static Map<Integer, TypeResource> map = new HashMap<Integer, TypeResource>();
		static {
	        for (TypeResource serverType : TypeResource.values()) {
	            map.put(serverType.getType(), serverType);
	        }
	    }
		
		private TypeResource(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static TypeResource valueOf(int serverType) {
	        return map.get(serverType);
	    }
	}
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private TypeResource type;

	public Resource() {
	}

	public Resource(String name, TypeResource type) {
		this.name = name;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name != null ? name : "";
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public TypeResource getTypeResource() {
		return type;
	}
	
	public abstract List<Variable> getVariables();
	
	public abstract List<Parameter> getParameters();
}
