package bpm.workflow.runtime.model.activities;

import java.util.List;
import java.util.Locale;

import org.dom4j.Element;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;


public class AirActivity extends AbstractActivity implements IComment {

	public ActivityVariables varSucceed;
	private static int number = 0;
	private String comment, NameR, path;
	private String RChoice; //E = Ecriture I = Importation
	private int RId = -1, RSModelId = -1;
	private int choice = -1; //0 = AIR 1 = R
	RScriptModel script, resultR;
	private RemoteSmartManager remoteSmart;
	private boolean saveScript;
	
	public AirActivity() {
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_endIteration");
		varSucceed.setType(0);
		
		number++;
	}
	
	public AirActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_endIteration");
		varSucceed.setType(0);
	}
	
	@Override
	public IActivity copy() {
		AirActivity a = new AirActivity();
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		return a;
	}

	@Override
	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (NameR == null || NameR.equals("")) {
			buf.append("For activity " + name + ", the algorithm is not set.\n");
		}
			
		return buf.toString();
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			try {
				if(choice == 0){
					//AIR
					if(RSModelId == -1){
						activityResult = false;
					}else{
						RemoteSmartManager remoteSmart = getAirRemote();
						try{
							List<RScriptModel> tmpList;
							tmpList = remoteSmart.getRScriptModelsbyScript(RId);
							for(int i = 0; i < tmpList.size(); i++){
								if(tmpList.get(i).getNumVersion() == RSModelId){
									resultR = remoteSmart.executeScriptR(tmpList.get(i), null);
									if(resultR.isScriptError()){
										activityResult = false;
									}else{
										activityResult = true;
									}
								}
							}
							
						} catch(Exception ex){
							activityResult = true;
							activityState = ActivityState.FAILED;
							failureCause = ex.getMessage();
							ex.printStackTrace();
						}
					}
				}else if(choice == 1){
					//R
					if(RChoice.equals("E")){
						//Ecriture
						if(!script.getScript().equals("")){
							RemoteSmartManager remoteSmart = getAirRemote();
							try{
								resultR = remoteSmart.executeScriptR(script, null);
								if(resultR.isScriptError()){
									activityResult = false;
									activityState = ActivityState.FAILED;
								}else{
									activityResult = true;
								}
								
							}catch (Exception e){
								activityResult = false;
								activityState = ActivityState.FAILED;
								failureCause = e.getMessage();
								e.printStackTrace();
							}
						}else{
							activityResult = false;
							activityState = ActivityState.FAILED;
						}
					}else if(RChoice.equals("I")){
						//Importation
						if(!path.equals("")){
							//Execute le script
							if(!script.getScript().equals("")){
								RemoteSmartManager remoteSmart = getAirRemote();
								try{
									resultR = remoteSmart.executeScriptR(script, null);
									if(resultR.isScriptError()){
										activityResult = false;
										activityState = ActivityState.FAILED;
									}else{
										activityResult = true;
									}
									
								}catch (Exception e){
									activityResult = false;
									activityState = ActivityState.FAILED;
									failureCause = e.getMessage();
									e.printStackTrace();
								}
							}else{
								activityResult = false;
								activityState = ActivityState.FAILED;
							}
							
							activityResult = true;
						}else{
							activityResult = false;
							activityState = ActivityState.FAILED;
						}
					}else{
						activityResult = false;
						activityState = ActivityState.FAILED;
					}
				}else{
					activityResult = false;
					activityState = ActivityState.FAILED;
				}
				
			} catch(Exception e) {
				activityResult = true;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}
			
			super.finishActivity();
		}
		
	}


	public String getSession() throws Exception {
		IVanillaContext ctx = workflowInstance.getRepositoryApi().getContext().getVanillaContext();
		RemoteAdminManager admin = new RemoteAdminManager(ctx.getVanillaUrl(), null, Locale.getDefault());
		User u = workflowInstance.getVanillaApi().getVanillaSecurityManager().authentify("", ctx.getLogin(), ctx.getPassword(), false);
		String session = admin.connect(u);
		
		return session;
	}
	
	public RemoteSmartManager getAirRemote() {
		try {
			remoteSmart = new RemoteSmartManager(workflowInstance.getRepositoryApi().getContext().getVanillaContext().getVanillaUrl(), getSession(), Locale.getDefault());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return remoteSmart;
	}
	
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("AirActivity");
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if(this.RId != -1){
			e.addElement("RId").setText(String.valueOf(this.RId));
		}
		if(this.RSModelId != -1){
			e.addElement("RSModelId").setText(String.valueOf(this.RSModelId));
		}
		if(this.NameR != null){
			e.addElement("NameR").setText(this.NameR);
		}
		if(this.path != null){
			e.addElement("path").setText(this.path);
		}
		if(this.RChoice != null){
			e.addElement("RChoice").setText(this.RChoice);
		}
		if(this.choice != -1){
			e.addElement("choice").setText(String.valueOf(this.choice));
		}
		if(this.saveScript){
			e.addElement("saveScript").setText("True");
			e.addElement("script").setText(this.script.getScript());
		}
		return e;
	}
	
	public void decreaseNumber() {
		number--;
	}

	
	////GETTERS AND SETTERS////
	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public void setComment(String text) {
		this.comment = text;
	}
	
	public String getNameR() {
		return NameR;
	}

	public void setNameR(String nameR) {
		this.NameR = nameR;
	}

	public int getRId() {
		return RId;
	}

	public void setRId(int rId) {
		this.RId = rId;
	}
	
	public void setRId(String rId){
		try{
			this.RId = Integer.parseInt(rId);
		}catch(NumberFormatException e){
			
		}
	}

	
	public RScriptModel getScript() {
		return script;
	}

	public void setScript(RScriptModel script) {
		this.script = script;
	}

	public RScriptModel getResultR() {
		return resultR;
	}

	public void setResultR(RScriptModel result) {
		this.resultR = result;
	}

	public int getRSModelId() {
		return RSModelId;
	}

	public void setRSModelId(int rSModelId) {
		this.RSModelId = rSModelId;
	}
	
	public void setRSModelId(String rSModelId){
		try{
			this.RSModelId = Integer.parseInt(rSModelId);
		}catch(NumberFormatException e){
			
		}
	}

	public int getChoice() {
		return choice;
	}

	public void setChoice(int choice) {
		this.choice = choice;
	}
	
	public void setChoice(String choice){
		try{
			this.choice = Integer.parseInt(choice);
		}catch(NumberFormatException e){
			
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRChoice() {
		return RChoice;
	}

	public void setRChoice(String rChoice) {
		RChoice = rChoice;
	}

	public boolean isSaveScript() {
		return saveScript;
	}

	public void setSaveScript(boolean saveScript) {
		this.saveScript = saveScript;
	}
	
	public void setSaveScript(String saveScript){
		try{
			this.saveScript = Boolean.parseBoolean(saveScript);
		}catch(NumberFormatException e){
			
		}
	}
	
	public void setScripText(String Script){
		if(script != null){
			script.setScript(Script);
		}else{
			script = new RScriptModel();
			script.setScript(Script);
		}
	}


}
