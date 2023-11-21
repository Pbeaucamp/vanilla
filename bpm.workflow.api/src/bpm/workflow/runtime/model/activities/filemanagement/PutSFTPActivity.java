package bpm.workflow.runtime.model.activities.filemanagement;

import org.dom4j.Element;

import bpm.workflow.runtime.model.ActivityVariables;

public class PutSFTPActivity extends PutFTPActivity {
	private static int number = 0;
	
	/**
	 * do not use, only for xml
	 */
	public PutSFTPActivity(){
		varSucceed = new ActivityVariables();
		nomInterne = "_getSFTPResult";
		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
	}
	
	public PutSFTPActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();
		nomInterne = "_getSFTPResult";
		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("PutSFTPActivity");
		
		return e;
	}
	
	public PutSFTPActivity copy() {
		PutSFTPActivity a = new PutSFTPActivity();
		
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
	
		return a;
	}

}
