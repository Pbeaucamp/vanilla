
package bpm.gateway.core.transformations.calcul;


public class Script {

	private String name;

	private String scriptFunction = "";
	private int type;
	
//	private Transformation owner;

	public void setType(String type){
		this.type = Integer.parseInt(type);
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public String getName() {
		return name;
	}

//	protected void setOwner(Transformation c){
//		owner = c;
//	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getScriptFunction() {
		return scriptFunction;
	}

	public void setScriptFunction(String scriptFunction) {
		this.scriptFunction = scriptFunction;
	}
	
	
}
