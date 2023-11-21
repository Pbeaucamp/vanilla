package bpm.workflow.runtime.model.activities.filemanagement;

import org.dom4j.Element;

import bpm.workflow.runtime.model.ActivityVariables;

public class PutSSHActivity extends PutFTPActivity {
	private static int number = 0;
	
	/**
	 * do not use, only for xml
	 */
	public PutSSHActivity(){
		varSucceed = new ActivityVariables();
		nomInterne = "_getSSHResult";
		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
	}
	
	public PutSSHActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();
		nomInterne = "_getSSHResult";
		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("PutSSHActivity");
		
		return e;
	}
	
	public PutSSHActivity copy() {
		PutSSHActivity a = new PutSSHActivity();
		
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
	
		return a;
	}

}
