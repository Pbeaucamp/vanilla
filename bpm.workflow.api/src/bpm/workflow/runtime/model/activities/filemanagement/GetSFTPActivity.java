package bpm.workflow.runtime.model.activities.filemanagement;

import org.dom4j.Element;

import bpm.workflow.runtime.model.ActivityVariables;

public class GetSFTPActivity extends GetFTPActivity {
	private static int number = 0;
	private String filePath;
	
	public GetSFTPActivity(){
		varSucceed = new ActivityVariables();
		setNomInterne("_getSFTPResult");
		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
		number++;
	}
	
	public GetSFTPActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		setNomInterne("_getSFTPResult");
		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
		filePath = this.getId() + "_outputpath";
	
	}

	public GetSFTPActivity copy() {
		GetSFTPActivity a = new GetSFTPActivity();
		
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);

		return a;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("GetFTPActivity");
		return e;
	}

}
