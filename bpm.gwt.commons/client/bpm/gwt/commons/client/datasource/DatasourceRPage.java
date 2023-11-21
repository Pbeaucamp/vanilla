package bpm.gwt.commons.client.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceR;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasourceRPage extends Composite implements IDatasourceObjectPage {

	private static DatasourceRPageUiBinder uiBinder = GWT.create(DatasourceRPageUiBinder.class);

	interface DatasourceRPageUiBinder extends UiBinder<Widget, DatasourceRPage> {
	}
	
	@UiField
	Label lblNote;
	
	@UiField
	ListBox lstPackage, lstDataset;
	

	private Datasource datasource;
	private DatasourceWizard parent;

	public DatasourceRPage(DatasourceWizard parent, Datasource datasource) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		lstPackage.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstDataset.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		
		fillRPackages();
		lblNote.setText(LabelsConstants.lblCnst.RDatasetNote());
		this.datasource = datasource;
		
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 1;
	}

	@Override
	public IDatasourceObject getDatasourceObject() {
		
		
		DatasourceR R = new DatasourceR();
		
		if(datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceR) {
			R = (DatasourceR) datasource.getObject();
		}
		
		String dataset = lstDataset.getValue(lstDataset.getSelectedIndex());
		if (dataset != null) {
			R.setOriginalDatasetR(dataset);
		}
		
		String pack = lstPackage.getValue(lstPackage.getSelectedIndex());
		if (pack != null) {
			R.setPackageR(pack);
		}
		return R;
	}
	
	private void fillRPackages() {
//		rPackagesList = new ArrayList<String>();
//		rPackagesList.add("      ");
		lstPackage.clear();
		lstPackage.addItem("	");
		
		RScriptModel box = new RScriptModel();
//		String script = "manual_result <- installed.packages()[,\"Package\"]";
		String script = "packs <- rownames(installed.packages())\n";
		script += "manual_result <- c()\n";
		script += "for (i in 1:length(packs)) {\n";
		script += "if (file.exists(paste(system.file(package = packs[i]),\"/data\", sep=\"\"))==TRUE) {\n";
		script += "manual_result[length(manual_result)+1] = packs[i]\n";
		script += "}\n";
		script += "}\n";
		box.setScript(script);
		box.setOutputs("manual_result".split(" "));
		parent.showWaitPart(true);
		CommonService.Connect.getInstance().runRScript(box, new AsyncCallback<RScriptModel>() {	
			@Override
			public void onSuccess(RScriptModel result) {
				parent.showWaitPart(false);
				List<String> list = new ArrayList<String>(Arrays.asList((result.getOutputVarstoString().get(0).split("\t"))));
				Collections.sort(list, new Comparator<String>() {
					@Override
					public int compare(String m1, String m2) {
						return m1.compareToIgnoreCase(m2);
					}
				});
				for(String pack: list){
					lstPackage.addItem(pack, pack);	
				}
//				rPackagesList.addAll(list);
//				onTypeChange(null);
				
				if(datasource != null && datasource.getObject() instanceof DatasourceR) {
					for(int i=0; i<lstPackage.getItemCount(); i++){
						if(lstPackage.getValue(i).equals(((DatasourceR)datasource.getObject()).getPackageR())){
							lstPackage.setSelectedIndex(i);
							fillRDatasets(((DatasourceR)datasource.getObject()).getPackageR());
							break;
						}
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				parent.showWaitPart(false);
				InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(),LabelsConstants.lblCnst.GetInstalledPackagesError(), caught.getMessage(), caught);
				dial.center();
			}
		});
		
	}
	
	private void fillRDatasets(String pack) {
		lstDataset.clear();
		
		RScriptModel box = new RScriptModel();
		String script = "manual_result <- (data(package = \""+ pack.trim() +"\")$results)[,c('Item','Title')]\n";
		script += "manual_result <- manual_result[order(manual_result[,2]),]";
		box.setScript(script);
		box.setOutputs("manual_result".split(" "));
		parent.showWaitPart(true);
		CommonService.Connect.getInstance().runRScript(box, new AsyncCallback<RScriptModel>() {	
			@Override
			public void onSuccess(RScriptModel result) {
				parent.showWaitPart(false);
				List<String> list = new ArrayList<String>(Arrays.asList((result.getOutputVarstoString().get(0).split("\t"))));
				if(list.size()>0){
					lstDataset.addItem("	");
					List<String> names = list.subList(0, list.size()/2);
					List<String> titles = list.subList(list.size()/2, list.size());
					int i = 0;
					for(String dts: names){
						lstDataset.addItem(titles.get(i), dts);
						i++;
					}
					
					
				} else {
					lstDataset.addItem(LabelsConstants.lblCnst.NoDataset());
				}
				
				if(datasource != null && datasource.getObject() instanceof DatasourceR) {
					for(int i=0; i<lstDataset.getItemCount(); i++){
						if(lstDataset.getValue(i).equals(((DatasourceR)datasource.getObject()).getOriginalDatasetR())){
							lstDataset.setSelectedIndex(i);
							break;
						}
					}
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				parent.showWaitPart(false);
				InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(),LabelsConstants.lblCnst.GetDatasetError(), caught.getMessage(), caught);
				dial.center();
			}
		});
		
	}
	
//	@UiHandler("lstDataset")
//	public void onRDatasetChange(ChangeEvent e) {
//		if(lstDataset.getSelectedIndex() != 0){
////			dataset.setDatasourceId(selectedDatasource.getId());
////			dataset.setRequest(lstDatasource.getValue(lstDatasource.getSelectedIndex()) +";"+ lstRDataset.getValue(lstRDataset.getSelectedIndex()));
//			/* package;dataset */
//		}
//		parent.updateBtn();
//	}
	
	@UiHandler("lstPackage")
	public void onRPackageChange(ChangeEvent e) {
		String rPackage = lstPackage.getValue(lstPackage.getSelectedIndex());
		fillRDatasets(rPackage);
	}
	
	

}
