package bpm.workflow.runtime.model.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.resources.Script;
import bpm.workflow.runtime.resources.variables.Variable;

/**
 * Set the result of a calculation into a variable
 * @author Charles MARTIN
 *
 */
public class CalculationActivity extends AbstractActivity {
	
	//I don't know why it's there
	private static String PREBUILT_FUNCTIONS = "";
	static{
		StringBuffer buf = new StringBuffer();

		buf.append("function dateDifference(date1, date2){\n");
		buf.append("     var d1 = new Date();\n");
		buf.append("     d1.setTime(date1);\n");
		buf.append("     var d2 = new Date();\n");
		buf.append("     d2.setTime(date2);\n");
		buf.append("     var d3 = new Date();\n");
		buf.append("     d3.setTime(date1-date2);\n");
		buf.append("     return d3;\n");

		buf.append("}\n");

		PREBUILT_FUNCTIONS = buf.toString();
	}
	
	
	private static int number = 0;
	private List<Script> scripts = new ArrayList<Script>();
	/**
	 * do not use, for XML parsing only
	 */
	public CalculationActivity(){
		number++;
	}
	
	public CalculationActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}


	public IActivity copy() {
		CalculationActivity a = new CalculationActivity();
		a.setName("copy of " + name);
		return a;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
	}
	
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("calculationActivity");
		
		if(getScripts().size() != 0){
			for(Script script : getScripts()){
				Element c = e.addElement("script");
				c.addElement("name").setText(script.getName());
				c.addElement("expression").setText(script.getScriptFunction());
				c.addElement("type").setText(script.getType()+ "");
			
			}
		}
		
		return e;
	}

	/**
	 * 
	 * @return the scripts contained in the calculation step
	 */
	public List<Script> getScripts(){
		return new ArrayList<Script>(scripts);
	}
	
	/**
	 * Remove a script from the calculation activity
	 * @param script to remove
	 * @return
	 */
	public boolean removeScript(Script s){
		boolean b = scripts.remove(s);
		if (b){
			refreshDescriptor();
		}
		return b;
	}
	
	public void refreshDescriptor() {
		
	}
	
	
	/**
	 * Add a script to the calculation activity
	 * @param script to add
	 * @return
	 */
	public boolean addScript(Script s){
		for(Script _s : scripts){
			if (s.getName().equals(_s.getName())){
				return false;
			}
		}

		scripts.add(s);
		
		refreshDescriptor();
		return true;
	}
	
	public void decreaseNumber() {
		number--;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			//init the JS engine
			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();
			
			try{
				for(Script sc : scripts){
	
					String brut = sc.getScriptFunction();
	
					for(String name : workflowInstance.getListVariable().keySet()){
						int typedelavar = workflowInstance.getListVariable().get(name).getType();
	
						if(typedelavar == 0){
							if(name.equalsIgnoreCase("{$E_TIME}")){
								Date actuelle = new Date();
								String temp = brut.replace(name, "\""+actuelle.toLocaleString()+"\"");
								brut = temp;
							}
							else if(name.equalsIgnoreCase("{$IP}")){
								String temp = brut.replace(name, "\""+workflowInstance.getListVariable().get(name).getLastValue()+"\"");
								brut = temp;
							}
							else{
								String temp = brut.replace(name, "\""+workflowInstance.getListVariable().get(name).getLastValue()+"\"");
								brut = temp;
							}
	
						}
						else {
							brut = workflowInstance.parseString(brut);
						}
	
					}
	
	
					String javaScript = PREBUILT_FUNCTIONS + "\n" + brut;
					Object result = cx.evaluateString(scope, javaScript, "<cmd>", 1, null);
	
					switch(sc.getType()){
					case Variable.STRING:
						String valeurobt = String.valueOf(Context.jsToJava(result, String.class));
	
						List<String> valeur = new ArrayList<String>();
						valeur.add(valeurobt);
						workflowInstance.getOrCreateVariable(sc.getName()).setValues(valeur);
						break;
					case Variable.BOOLEAN:
	
						String valeur22 = String.valueOf(Context.jsToJava(result, Boolean.class));
	
						List<String> valeur2 = new ArrayList<String>();
						valeur2.add(valeur22);
						workflowInstance.getOrCreateVariable(sc.getName()).setValues(valeur2);
						break;
					case Variable.DATE:
	
						String valeur33 = String.valueOf(Context.jsToJava(result, Date.class));
	
						List<String> valeur3 = new ArrayList<String>();
						valeur3.add(valeur33);
						workflowInstance.getOrCreateVariable(sc.getName()).setValues(valeur3);
						break;
					case Variable.FLOAT:
						String valeur44 = String.valueOf(Context.jsToJava(result, Float.class));
	
						List<String> valeur4 = new ArrayList<String>();
						valeur4.add(valeur44);
						workflowInstance.getOrCreateVariable(sc.getName()).setValues(valeur4);
						break;
					case Variable.INTEGER:
	
						String valeur55 = String.valueOf(Context.jsToJava(result, Integer.class));
	
						List<String> valeur5 = new ArrayList<String>();
						valeur5.add(valeur55);
						
						break;
	
					}
	
				}
				activityResult = true;
			}
			catch(Exception e){
				e.printStackTrace();
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
			}
			
			super.finishActivity();
		}
	}
	
}
