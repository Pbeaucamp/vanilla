package bpm.smart.web.client.wizards;

import java.util.Arrays;
import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.services.SmartAirService;

public class RecodeAgeToRange {

	private IGwtWizard parent;
	
	private String datasetName;
	private String column;
	private String newColumn;
	
	private double[] range = new double[2];
	private int[] modele = {0,18,30,45,60};
	
	private boolean bLoadRange = false;
	

	public RecodeAgeToRange(IGwtWizard parent, String datasetName, String column, String newColumn) {
		this.parent = parent;
		this.datasetName = datasetName;
		this.column = column;
		this.newColumn = newColumn;
		
		getRange();
	
	}
	
	public void getRange() {
		String script = "manual_result<- range(" + datasetName + "$" + column +")";

		RScriptModel model = new RScriptModel();
		model.setScript(script);
		model.setOutputs(new String[] { "manual_result" });

		parent.showWaitPart(true);
		SmartAirService.Connect.getInstance().executeScript(model, new GwtCallbackWrapper<RScriptModel>(parent, true) {
			@Override
			public void onSuccess(RScriptModel result) {
				String values = result.getOutputVarstoString().get(0);
				List<String> distincts = Arrays.asList(values.trim().split("\\t"));

				range[0] = Double.parseDouble(distincts.get(0));
				range[1] = Double.parseDouble(distincts.get(1));
				
				bLoadRange = true;
			}
		}.getAsyncCallback());
	}
	
	public String getGeneratedCode() {
		StringBuffer result = new StringBuffer();
		result.append("if(!'"+ newColumn +"' %in% names(" + datasetName + "))" + datasetName + " <- data.frame("+ datasetName +", "+ newColumn +" = c(NA))\n");
		result.append("if(!'"+ "lbl."+ newColumn +"' %in% names(" + datasetName + "))" + datasetName + " <- data.frame("+ datasetName +", "+ "lbl."+ newColumn +" = c(NA))\n");
		
		for(int i=0; i<modele.length; i++){
			if(modele.length > i+1){
				if((modele[i] >= range[0] && modele[i] <= range[1]) || (modele[i+1] >= range[0] && modele[i+1] <= range[1])){
					result.append(datasetName+"["+datasetName+"$"+column+" >= "+modele[i]+" & "+datasetName+"$"+column+" < "+modele[i+1]+",]$"+newColumn+" <- " + (i+1)+"\n");
					result.append(datasetName+"["+datasetName+"$"+column+" >= "+modele[i]+" & "+datasetName+"$"+column+" < "+modele[i+1]+",]$lbl."+newColumn+" <- '" + modele[i]+"-"+modele[i+1]+"'\n");
				}
				
			} else {
				if((modele[i] >= range[0] && modele[i] <= range[1])){
					result.append(datasetName+"["+datasetName+"$"+column+" >= "+modele[i]+",]$"+newColumn+" <- " + (i+1)+"\n");
					result.append(datasetName+"["+datasetName+"$"+column+" >= "+modele[i]+",]$lbl."+newColumn+" <- '" + modele[i] + "+'"+"\n");
				}
				
			}
			
		}
		result.append( datasetName +"$"+ "lbl."+ newColumn +"<- as.factor("+ datasetName +"$"+ "lbl."+ newColumn +")\n");
		
		return result.toString();
	}
	
	public IGwtWizard getWizardParent() {
		return parent;
	}

	public boolean isbLoadRange() {
		return bLoadRange;
	}

}
