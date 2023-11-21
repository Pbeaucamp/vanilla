package bpm.gwt.commons.client.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.free.metrics.DateTimePickerDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceSocial;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasourceSocialPage extends Composite implements IDatasourceObjectPage {

	private static DatasourceSocialPageUiBinder uiBinder = GWT.create(DatasourceSocialPageUiBinder.class);

	interface DatasourceSocialPageUiBinder extends UiBinder<Widget, DatasourceSocialPage> {
	}
	
	interface MyStyle extends CssResource {
		String lbl();
		String lst();
		String icCalendar();
		String txt();
		String txtmin();
	}
	
	@UiField
	ListBox lstSocial, lstFunction;
	
	@UiField
	HTMLPanel customPanel;

	@UiField
	MyStyle style;
	
	private Datasource datasource;
	private DatasourceWizard parent;
	private DatasourceSocial.SocialType selectedSocial;
	private DatasourceSocial.Function selectedFunction;
	
	private DateTimePickerDialog datePickerStartDialog;
	private DateTimePickerDialog datePickerEndDialog;
	
	private HashMap<String, String> params = new HashMap<String, String>();
	
	private HashMap<String, Widget> customWidgets;
	private HashMap<String, Widget> nonOptionalWidgets;

	public DatasourceSocialPage(DatasourceWizard parent, Datasource datasource) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.datasource = datasource;
		
		lstSocial.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstFunction.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		
		if(datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceSocial) { //on recupere les params
			DatasourceSocial social = (DatasourceSocial) datasource.getObject();
			if(social.getParams() != null && !social.getParams().isEmpty()){
				for(String param : social.getParams().split("&&")){
					params.put(param.split("=")[0], param.split("=")[1]);
				}
			}
		}
		
		fillSocialNetworks();
		fillFunctions();
		generateCustomPanel();
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		if(getMissingInfo() != null){
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.PleaseCompleteInfo() + " " + getMissingInfo());
			return false;
		} else {
			return true;
		}
		
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
		
		
		DatasourceSocial social = new DatasourceSocial();
		
		if(datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceSocial) {
			social = (DatasourceSocial) datasource.getObject();
		}
		
		social.setType(selectedSocial);
		social.setFunction(selectedFunction);
		social.setParams(getCustomInfos());
		
		return social;
	}

	private void fillSocialNetworks() {
		lstSocial.clear();
		
		for(DatasourceSocial.SocialType type : DatasourceSocial.SocialType.values()){
			lstSocial.addItem(type.name());	
		}
				
		if(datasource != null && datasource.getObject() instanceof DatasourceSocial) {
			for(int i=0; i<lstSocial.getItemCount(); i++){
				if(lstSocial.getValue(i).equals(((DatasourceSocial)datasource.getObject()).getType().name())){
					lstSocial.setSelectedIndex(i);
					break;
				}
			}
		}
	
		for(DatasourceSocial.SocialType type : DatasourceSocial.SocialType.values()){
			if(type.name().equals(lstSocial.getValue(lstSocial.getSelectedIndex()))){
				selectedSocial = type;
				break;
			}
		}
	}
	
	private void fillFunctions() {
		lstFunction.clear();

		for(DatasourceSocial.Function func : DatasourceSocial.Function.values()){
			if(func.toString().split("_")[0].equals(selectedSocial.toString())){
				lstFunction.addItem(func.name());	
			}
			
		}
				
		if(datasource != null && datasource.getObject() instanceof DatasourceSocial) {
			for(int i=0; i<lstFunction.getItemCount(); i++){
				if(lstFunction.getValue(i).equals(((DatasourceSocial)datasource.getObject()).getFunction().name())){
					lstFunction.setSelectedIndex(i);
					break;
				}
			}
		}
	
		for(DatasourceSocial.Function func : DatasourceSocial.Function.values()){
			if(func.name().equals(lstFunction.getValue(lstFunction.getSelectedIndex()))){
				selectedFunction = func;
				break;
			}
		}
	}
	
	@UiHandler("lstSocial")
	public void onSocialChange(ChangeEvent e) {
		for(DatasourceSocial.SocialType type : DatasourceSocial.SocialType.values()){
			if(type.name().equals(lstSocial.getValue(lstSocial.getSelectedIndex()))){
				selectedSocial = type;
				break;
			}
		}
		fillFunctions();
	}
	
	@UiHandler("lstFunction")
	public void onFunctionChange(ChangeEvent e) {
		for(DatasourceSocial.Function func : DatasourceSocial.Function.values()){
			if(func.name().equals(lstFunction.getValue(lstFunction.getSelectedIndex()))){
				selectedFunction = func;
				break;
			}
		}
		generateCustomPanel();
	}

	private void generateCustomPanel() {
		customPanel.clear();
		
		switch(selectedFunction){
		case TWITTER_USERS : 
			LabelTextBox txtUsers = new LabelTextBox();
			txtUsers.setPlaceHolder(LabelsConstants.lblCnst.Users() + " " + LabelsConstants.lblCnst.Separatedby() + " ';'");
			txtUsers.addStyleName(style.txt());
			
			if(params.size() > 0) {
				if(params.containsKey("userList")){
					txtUsers.setText(params.get("userList"));
				}
			}
			
			customPanel.add(txtUsers);
			customWidgets = new HashMap<String, Widget>();
			customWidgets.put("userList", txtUsers);
			nonOptionalWidgets = new HashMap<String, Widget>();
			nonOptionalWidgets.put("userList", txtUsers);
			break;
		case TWITTER_TRENDS : 
			Label lbl = new Label(LabelsConstants.lblCnst.Zone());
			ListBox lstZones = new ListBox();
			fillZones(lstZones);
			lbl.addStyleName(style.lbl());
			lstZones.addStyleName(VanillaCSS.COMMONS_LISTBOX);
			
			//selection dans fill
			
			customPanel.add(lbl);
			customPanel.add(lstZones);
			customWidgets = new HashMap<String, Widget>();
			customWidgets.put("woeid", lstZones);
			nonOptionalWidgets = new HashMap<String, Widget>();
			break;
		case TWITTER_SEARCH : 
			LabelTextBox txtKeys = new LabelTextBox();
			LabelTextBox txtnlast = new LabelTextBox();
			Label lblsince = new Label(LabelsConstants.lblCnst.Since());
			Label lbluntil = new Label(LabelsConstants.lblCnst.Until());
			final LabelTextBox txtSince = new LabelTextBox();
			final LabelTextBox txtUntil = new LabelTextBox();
			datePickerStartDialog = new DateTimePickerDialog(new Date(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Date date = datePickerStartDialog.getSelectedDate();
					String dateString = DateTimeFormat.getFormat("yyyy-MM-dd").format(date);
					txtSince.setText(dateString);
				}
			});
			datePickerEndDialog = new DateTimePickerDialog(new Date(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Date date = datePickerEndDialog.getSelectedDate();
					String dateString = DateTimeFormat.getFormat("yyyy-MM-dd").format(date);
					txtUntil.setText(dateString);
				}
			});
			Image imgsince = new Image(CommonImages.INSTANCE.ic_calendar());
			Image imguntil = new Image(CommonImages.INSTANCE.ic_calendar());
			imgsince.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					datePickerStartDialog.center();
				}
			});
			imguntil.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					datePickerEndDialog.center();
				}
			});
			imgsince.addStyleName(style.icCalendar());
			imguntil.addStyleName(style.icCalendar());
			
			txtKeys.setPlaceHolder(LabelsConstants.lblCnst.SearchKeys());
			txtnlast.setPlaceHolder(LabelsConstants.lblCnst.TweetNumber());
			
			txtKeys.addStyleName(style.txt());
			txtnlast.addStyleName(style.txt());
			txtSince.addStyleName(style.txtmin());
			txtUntil.addStyleName(style.txtmin());
			lblsince.addStyleName(style.lbl());
			lbluntil.addStyleName(style.lbl());
			
			if(params.size() > 0) {
				if(params.containsKey("searchString")){
					txtKeys.setText(params.get("searchString"));
				}
				if(params.containsKey("n")){
					txtnlast.setText(params.get("n"));
				}
				if(params.containsKey("since")){
					txtSince.setText(params.get("since"));
				}
				if(params.containsKey("until")){
					txtUntil.setText(params.get("until"));
				}
			}
			
			customPanel.add(txtKeys);
			customPanel.add(txtnlast);
			customPanel.add(lblsince);
			customPanel.add(txtSince);
			customPanel.add(imgsince);
			customPanel.add(lbluntil);
			customPanel.add(txtUntil);
			customPanel.add(imguntil);
			customWidgets = new HashMap<String, Widget>();
			customWidgets.put("searchString", txtKeys);
			customWidgets.put("n", txtnlast);
			customWidgets.put("since", txtSince);
			customWidgets.put("until", txtUntil);
			
			nonOptionalWidgets = new HashMap<String, Widget>();
			nonOptionalWidgets.put("searchString", txtKeys);
			nonOptionalWidgets.put("n", txtnlast);
			break;
		case TWITTER_TIMELINE : 
			LabelTextBox txtUser = new LabelTextBox();
			LabelTextBox txtNlast = new LabelTextBox();
			
			txtUser.setPlaceHolder(LabelsConstants.lblCnst.User());
			txtNlast.setPlaceHolder(LabelsConstants.lblCnst.TweetNumber());
			
			txtUser.addStyleName(style.txt());
			txtNlast.addStyleName(style.txt());
			
			if(params.size() > 0) {
				if(params.containsKey("user")){
					txtUser.setText(params.get("user"));
				}
				if(params.containsKey("n")){
					txtNlast.setText(params.get("n"));
				}
			}
			
			customPanel.add(txtUser);
			customPanel.add(txtNlast);
			customWidgets = new HashMap<String, Widget>();
			customWidgets.put("user", txtUser);
			customWidgets.put("n", txtNlast);
			
			nonOptionalWidgets = new HashMap<String, Widget>();
			nonOptionalWidgets.put("user", txtUser);
			nonOptionalWidgets.put("n", txtNlast);
			break;
		}
		
	}

	private void fillZones(final ListBox lstZones) {
		RScriptModel box = new RScriptModel();
		String script = "library(twitteR)\n";
//		script += "consumer_key    <- 'vIBQsoRkFxLaKIO7FeWa8SNsZ'\n";
//		script += "consumer_secret <- '5JCwWavmw7S6XjTJYzyCAqahEgzGxgLhzaO6qRyN4EmZob4jX0'\n";
//		script += "access_token    <- '449516774-pNNEKCn3MvNUAXkk0SRLgW7hIVIpVsaTzzoRc52M'\n";
//		script += "access_secret   <- 'TJh0ZsJTf3ZMWsuAVok3Vi0DO6r55NHe8gVJOWJoT53aZ'\n";
//		script += "options(httr_oauth_cache=T)\n";
//		script += "setup_twitter_oauth(consumer_key, consumer_secret,  access_token, access_secret )\n";

		script += "zones <- availableTrendLocations()\n";
		script += "manual_result <- c()\n";
		script += "for (i in 1:nrow(zones)) {\n";
		script += "manual_result[length(manual_result)+1] = paste(zones[i,]$name, zones[i,]$country, zones[i,]$woeid, sep=';')\n";
		script += "}\n";
		box.setScript(script);
		box.setOutputs("manual_result".split(" "));
		parent.showWaitPart(true);
		CommonService.Connect.getInstance().runRScript(box, new AsyncCallback<RScriptModel>() {	
			@Override
			public void onSuccess(RScriptModel result) {
				parent.showWaitPart(false);
				List<String> list = new ArrayList<String>(Arrays.asList((result.getOutputVarstoString().get(0).split("\t"))));
				Collections.sort(list, new Comparator<String>(){
					@Override
					public int compare(String arg0, String arg1) {
						return arg0.split(";")[1].compareTo(arg1.split(";")[1]);
					}
				});
				for(String zone: list){
					String[] area = zone.split(";");
					String name;
					if(area[1].equals("")){
						name = area[0];
					} else if(area[0].equals(area[1])){
						name = area[0];
					} else {
						name = area[1] + "(" + area[0] + ")";
					}
					lstZones.addItem(name, area[2]);	
				}

				
				if(params.size() > 0) {
					for(int i=0; i<lstZones.getItemCount(); i++){
						if(lstZones.getValue(i).equals(params.get("woeid"))){
							lstZones.setSelectedIndex(i);
							break;
						}
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				parent.showWaitPart(false);
				MessageHelper.openMessageError(caught.getMessage(), caught);
			}
		});
	}
	
	private String getCustomInfos() {
		String result = "";
		for(String param : customWidgets.keySet()){
			if(customWidgets.get(param) instanceof LabelTextBox){
				if(!((LabelTextBox)customWidgets.get(param)).getText().equals("")){
					result += param + "=" + ((LabelTextBox)customWidgets.get(param)).getText() + "&&";
				}
				
			} else if(customWidgets.get(param) instanceof ListBox) {
				result += param + "=" + ((ListBox)customWidgets.get(param)).getValue(((ListBox)customWidgets.get(param)).getSelectedIndex()) + "&&";
			}
		}
		if(result.length() > 2){
			result = result.substring(0, result.length() - 2);
		}
		return result;
	}

	public DatasourceWizard getWizard() {
		return parent;
	}	

	public String getMissingInfo(){
		for(String param : nonOptionalWidgets.keySet()){
			if(nonOptionalWidgets.get(param) instanceof LabelTextBox){
				if(((LabelTextBox)nonOptionalWidgets.get(param)).getText().equals("")) return ((LabelTextBox)nonOptionalWidgets.get(param)).getPlaceHolder();
			}
		}
		return null;
	}
}
