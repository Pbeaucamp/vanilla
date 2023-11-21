package bpm.vanilla.portal.client.wizard;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.LevelMember;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.datasource.DatasourceDatasetManager;
import bpm.gwt.commons.client.dialog.AxisBrowseDialog;
import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.utils.alert.AlertConditionItem;
import bpm.gwt.commons.client.utils.alert.AlertConditionItem.ConditionType;
import bpm.gwt.commons.client.utils.alert.IConditionParent;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertConstants;
import bpm.vanilla.platform.core.beans.alerts.AlertKpi;
import bpm.vanilla.platform.core.beans.alerts.AlertKpi.KpiEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertRepository;
import bpm.vanilla.platform.core.beans.alerts.AlertRepository.ObjectEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertSystem;
import bpm.vanilla.platform.core.beans.alerts.AlertSystem.SystemEvent;
import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.core.beans.alerts.ConditionKpi;
import bpm.vanilla.platform.core.beans.alerts.ConditionRepository;
import bpm.vanilla.platform.core.beans.alerts.IAlertInformation;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.biPortal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class AddAlertEventPage extends Composite implements IGwtPage, IConditionParent {
	private static AddAlertEventPageUiBinder uiBinder = GWT.create(AddAlertEventPageUiBinder.class);

	interface AddAlertEventPageUiBinder extends UiBinder<Widget, AddAlertEventPage> {}

	interface MyStyle extends CssResource {
		
	}

	@UiField
	ListBox lstSubType, lstDataset, lstOperator, lstAxis, lstMetrics;

	@UiField
	LabelTextBox txtItem, txtValue;
	
	@UiField
	Button btnBrowseRep, btnValueSelect, btnNewDataset;
	
	@UiField
	Image btnAdd;
	
	@UiField
	HTMLPanel panelRepository, panelConditions, panelConditionsList, panelKpi;

	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private int index;
	private Alert currentAlert;
	private RepositoryItem item;
	private List<Condition> conditions = new ArrayList<Condition>();
	private List<Dataset> datasets;
	private List<Metric> metrics;
	private List<Axis> axis;
	private LevelMember levelItem;

	public AddAlertEventPage(IGwtWizard parent, int index, Alert currentAlert) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		
		this.currentAlert = currentAlert;
				
		lstSubType.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstDataset.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstOperator.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstAxis.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstMetrics.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		btnBrowseRep.addStyleName(VanillaCSS.COMMONS_BUTTON);
		btnValueSelect.addStyleName(VanillaCSS.COMMONS_BUTTON);
		btnNewDataset.addStyleName(VanillaCSS.COMMONS_BUTTON);
		initSubTypes();
		
		
//		txtItem.addDomHandler(new ChangeHandler() {
//			
//			@Override
//			public void onChange(ChangeEvent event) {
//				AddAlertEventPage.this.parent.updateBtn();
//			}
//		}, ChangeEvent.getType());
	}

	private void initSubTypes() {
		switch(currentAlert.getTypeEvent()){
		case OBJECT_TYPE:
			for(ObjectEvent ev2 : ObjectEvent.values()){
				lstSubType.addItem(ev2.getLabel(), ev2.toString());
			}
			initPanelRepository();
			panelKpi.setVisible(false);
			break;
		case SYSTEM_TYPE:
			for(SystemEvent ev2 : SystemEvent.values()){
				lstSubType.addItem(ev2.getLabel(), ev2.toString());
			}
			panelRepository.setVisible(false);
			panelKpi.setVisible(false);
			panelConditions.setVisible(false);
			break;
		case KPI_TYPE:
			for(KpiEvent ev2 : KpiEvent.values()){
				lstSubType.addItem(ev2.getLabel(), ev2.toString());
			}
			initPanelKpi();
			panelRepository.setVisible(false);
			break;
		default:
			break;
		
		}
		if(currentAlert.getEventObject() != null){
			for(int i=0; i< lstSubType.getItemCount(); i++){
				if(currentAlert.getEventObject().getSubtypeEventName().equals(lstSubType.getValue(i))){
					lstSubType.setSelectedIndex(i);
				}
			}
		}
		parent.updateBtn();
	}
	
	private void initPanelRepository() {
		txtItem.setEnabled(false);
		if(currentAlert.getTypeEvent().equals(TypeEvent.OBJECT_TYPE) && ((AlertRepository)currentAlert.getEventObject()) != null &&
			((AlertRepository)currentAlert.getEventObject()).getRepositoryId() != 0 && 
			((AlertRepository)currentAlert.getEventObject()).getDirectoryItemId() != 0){
				CommonService.Connect.getInstance().getItemById(((AlertRepository)currentAlert.getEventObject()).getDirectoryItemId(), new GwtCallbackWrapper<RepositoryItem>(parent, true) {
					@Override
					public void onSuccess(RepositoryItem result) {
						item = result;
						txtItem.setText(item.getName());
						parent.updateBtn();
					}
				}.getAsyncCallback());
		} else {
			item = null;
			txtItem.setText("");
			parent.updateBtn();
		}
		
		refreshDatasets();
	}
	
	public void refreshDatasets(){
		
		CommonService.Connect.getInstance().getDatasets(new GwtCallbackWrapper<List<Dataset>>(parent, false) {
			@Override
			public void onSuccess(List<Dataset> result) {
				
				datasets = result;
				lstDataset.addItem("");
				for(Dataset dts : result){
					lstDataset.addItem(dts.getName());
				}
				
				if(currentAlert.getTypeEvent().equals(TypeEvent.OBJECT_TYPE)){
					lstDataset.setSelectedIndex(0);
					if(currentAlert.getDataProviderId() != 0){
						for(int i=0; i< datasets.size(); i++){
							if(datasets.get(i).getId() == currentAlert.getDataProviderId()){
								lstDataset.setSelectedIndex(i+1);
								break;
							}
						}
					}
					
				}
				initPanelConditions();
			}
		}.getAsyncCallback());
	}
	
	private void initPanelKpi() {
		txtValue.setEnabled(false);
		CommonService.Connect.getInstance().getAllMetrics(new GwtCallbackWrapper<List<Metric>>(parent, true) {
			@Override
			public void onSuccess(List<Metric> result) {
				metrics = result;
				lstMetrics.addItem("");
				for(Metric met : metrics){
					lstMetrics.addItem(met.getName());
				}
				
				if(currentAlert.getTypeEvent().equals(TypeEvent.KPI_TYPE) && ((AlertKpi)currentAlert.getEventObject()) != null){
					lstMetrics.setSelectedIndex(0);
					if(((AlertKpi)currentAlert.getEventObject()).getMetricId() != 0){
						for(int i=0; i< metrics.size(); i++){
							if(metrics.get(i).getId() == ((AlertKpi)currentAlert.getEventObject()).getMetricId()){
								lstMetrics.setSelectedIndex(i+1);
								break;
							}
						}
					}
					
					onMetricChange(null);
					
					if(((AlertKpi)currentAlert.getEventObject()).getAxisId() != 0){
						for(int i=0; i< axis.size(); i++){
							if(axis.get(i).getId() == ((AlertKpi)currentAlert.getEventObject()).getAxisId()){
								lstAxis.setSelectedIndex(i);
								break;
							}
						}
						//if(((AlertKpi)currentAlert.getEventModel()).getLevelIndex() != 0){
							levelItem = new LevelMember();
							levelItem.setLabel(((AlertKpi)currentAlert.getEventObject()).getLevelValue());
							levelItem.setLevel(axis.get(lstAxis.getSelectedIndex()).getChildren().get(((AlertKpi)currentAlert.getEventObject()).getLevelIndex()));
							txtValue.setText(levelItem.getLabel());
							parent.updateBtn();
						//}
					}
				}
				initPanelConditions();
			}
		}.getAsyncCallback());
		
	}
	
	private void initPanelConditions() {
		panelConditionsList.clear();
		if(currentAlert.getTypeEvent().equals(TypeEvent.OBJECT_TYPE)){
			if(currentAlert.getConditions() != null && lstDataset.getSelectedIndex() !=0){
				for(Condition cond : currentAlert.getConditions()){
					AlertConditionItem item = new AlertConditionItem(AddAlertEventPage.this, datasets.get(lstDataset.getSelectedIndex()-1).getMetacolumns(), ConditionType.REPOSITORY);
					item.setColumn1(cond.getLeftOperand());
					if(cond.getConditionObject() instanceof ConditionRepository){
						item.setColumn2(((ConditionRepository)cond.getConditionObject()).isRightOperandField(), cond.getRightOperand());
					} 
					
					item.setFilterName(cond.getOperator());
					panelConditionsList.add(item);
				}
			} else {
				panelConditionsList.clear();
			}
			updateConditions();
		} else if(currentAlert.getTypeEvent().equals(TypeEvent.KPI_TYPE)){
			if(currentAlert.getConditions() != null){
				for(Condition cond : currentAlert.getConditions()){
					AlertConditionItem item = new AlertConditionItem(AddAlertEventPage.this, null, ConditionType.KPI);
					item.setKpiType(((ConditionKpi)cond.getConditionObject()).getType());
					item.setKpiField1(cond.getLeftOperand());
					item.setKpiField2(cond.getRightOperand());
					if(((ConditionKpi)cond.getConditionObject()).getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.VALUE_TYPE])){
						item.setKpiFilter(cond.getOperator());
					} else if(((ConditionKpi)cond.getConditionObject()).getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.MISSING_TYPE])){
						item.setKpiFilter(((ConditionKpi)cond.getConditionObject()).getMissingType());
					} else if(((ConditionKpi)cond.getConditionObject()).getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.STATE_TYPE])){
						item.setKpiFilter(((ConditionKpi)cond.getConditionObject()).getStateType());
					}
					panelConditionsList.add(item);
				}
			} else {
				panelConditionsList.clear();
			}
			updateConditions();
		}
		
		
		lstOperator.addItem(AlertConstants.AND);
		lstOperator.addItem(AlertConstants.OR);
		
		if(!currentAlert.getTypeEvent().equals(TypeEvent.SYSTEM_TYPE) &&
				currentAlert.getOperator() != null){
			for(int i=0; i<lstOperator.getItemCount(); i++){
				if(lstOperator.getValue(i).equals(currentAlert.getOperator())){
					lstOperator.setSelectedIndex(i);
				}
			}
		}
	}


	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete() ? true : false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		switch (currentAlert.getTypeEvent()) {
		case KPI_TYPE:
			return levelItem != null && conditions.size() > 0;
		case OBJECT_TYPE:
			return true;//return item != null && conditions.size() > 0;
		case SYSTEM_TYPE:
			return true;
		default:
			return true;
		}
	}
	
	@UiHandler("lstSubType")
	public void onSubTypeChange(ChangeEvent event) {
		if(currentAlert.getTypeEvent().equals(TypeEvent.OBJECT_TYPE)){
			item = null;
		txtItem.setText("");
		}
		parent.updateBtn();
	}
	
	@UiHandler("lstDataset")
	public void onDatasourceChange(ChangeEvent event) {
		panelConditionsList.clear();
	}
	
	@UiHandler("lstMetrics")
	public void onMetricChange(ChangeEvent event) {
		lstAxis.clear();
		txtValue.clear();
		axis = new ArrayList<Axis>();
		levelItem = null;
		
		if(lstMetrics.getSelectedIndex() != 0){
			Metric met = metrics.get(lstMetrics.getSelectedIndex()-1);
			for(Axis axe : met.getLinkedAxis()){
				lstAxis.addItem(axe.getName());
			}
			axis = met.getLinkedAxis();
		}
		
		
	}
	
	@UiHandler("lstAxis")
	public void onAxisChange(ChangeEvent event) {
		txtValue.clear();
		levelItem = null;
	}
	
//	@UiHandler("txtItem")
//	public void onNameChange(ChangeEvent event) {
//		parent.updateBtn();
//	}
	
	@UiHandler("btnBrowseRep")
	public void onBrowse(ClickEvent e) {
		int repositoryType = 0;
		switch(getSelectedObjectSubType()){
		case BIRT:
			
		case BIRT_NEW_VERSION:
			repositoryType = IRepositoryApi.CUST_TYPE;
			break;
		case FWR:
			
		case FWR_NEW_VERSION:
			repositoryType = IRepositoryApi.FWR_TYPE;
			break;
		case GTW:
			repositoryType = IRepositoryApi.GTW_TYPE;
			break;
		default:
			repositoryType = IRepositoryApi.CUST_TYPE;
			break;
		}
		
		final RepositoryDialog dial = new RepositoryDialog(repositoryType);
		
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					item = dial.getSelectedItem();
					txtItem.setText(item.getName());
					parent.updateBtn();
				}
			}
		});
		dial.center();
	}
	
	@UiHandler("btnValueSelect")
	public void onValueClick(ClickEvent event) {
		
		final AxisBrowseDialog dial = new AxisBrowseDialog(axis.get(lstAxis.getSelectedIndex()));
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {		
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dial.isConfirm()) {
					
					levelItem = dial.getSelectedMember();
					
					txtValue.setText(levelItem.getLabel());
					parent.updateBtn();
				}
			}
		});
		dial.center();
	}
	
	@UiHandler("btnAdd")
	public void onAddRowLevel(ClickEvent event){
		if(currentAlert.getTypeEvent().equals(TypeEvent.OBJECT_TYPE)){
			if(lstDataset.getSelectedIndex() != 0){
				panelConditionsList.add(new AlertConditionItem(this, datasets.get(lstDataset.getSelectedIndex()-1).getMetacolumns(), ConditionType.REPOSITORY));
			}
		} else {
			panelConditionsList.add(new AlertConditionItem(AddAlertEventPage.this, null, ConditionType.KPI));
		}
		
	}
	
	@UiHandler("btnNewDataset")
	public void onNewDatasetClick(ClickEvent event){
		User user = biPortal.get().getInfoUser().getUser();
		
		DatasourceDatasetManager dial = new DatasourceDatasetManager(user);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {		
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refreshDatasets();
			}
		});
	}

	public RepositoryItem getItemReposotory() {
		return item;
	}
	
	public List<Condition> getConditions() {
		return conditions;
	}
	
	public IAlertInformation getAlertModel() {
		switch(currentAlert.getTypeEvent()){
		case OBJECT_TYPE:
			AlertRepository alr = new AlertRepository();
			if(item == null){
				alr.setDirectoryItemId(0);
				alr.setRepositoryId(0);
			} else {
				alr.setDirectoryItemId(item.getId());
				alr.setRepositoryId(biPortal.get().getInfoUser().getRepository().getId());
			}
			
			for(ObjectEvent ev2 : ObjectEvent.values()){
				if(lstSubType.getValue(lstSubType.getSelectedIndex()).equals(ev2.toString())){
					alr.setSubtypeEvent(ev2);
					break;
				}
			}
			return alr;
		case SYSTEM_TYPE:
			AlertSystem als = new AlertSystem();
			for(SystemEvent ev2 : SystemEvent.values()){
				if(lstSubType.getValue(lstSubType.getSelectedIndex()).equals(ev2.toString())){
					als.setSubtypeEvent(ev2);
					break;
				}
			}
			return als;
		case KPI_TYPE:
			AlertKpi alk = new AlertKpi();
			alk.setSubtypeEvent(KpiEvent.KPI);
			alk.setAxisId(axis.get(lstAxis.getSelectedIndex()).getId());
			alk.setMetricId(metrics.get(lstMetrics.getSelectedIndex()-1).getId());
			alk.setLevelIndex(levelItem.getLevel().getLevelIndex());
			alk.setLevelValue(levelItem.getLabel());
			return alk;
		default:
			return null;
		}
	}
	
	public ObjectEvent getSelectedObjectSubType() {
		for(ObjectEvent ev2 : ObjectEvent.values()){
			if(lstSubType.getValue(lstSubType.getSelectedIndex()).equals(ev2.toString())){
				return ev2;
			}
		}
		return null;
	}
	
	public String getOperator() {
		if(!currentAlert.getTypeEvent().equals(TypeEvent.SYSTEM_TYPE)){
			return lstOperator.getValue(lstOperator.getSelectedIndex());
		} else {
			return null;
		}	
	}
	
	public int getDataProviderId() {
		if(datasets != null && !datasets.isEmpty() && lstDataset.getSelectedIndex() !=0){
			return datasets.get(lstDataset.getSelectedIndex()-1).getId();
		} else {
			return 0;
		}
	}

	@Override
	public void updateConditions() {
		conditions.clear();
		for(int i=0; i< panelConditionsList.getWidgetCount(); i++){
			if(panelConditionsList.getWidget(i) instanceof AlertConditionItem){
				conditions.add(((AlertConditionItem)panelConditionsList.getWidget(i)).getCondition());
			}
		}
		parent.updateBtn();
	}
}
