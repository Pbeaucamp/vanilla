package bpm.gwt.workflow.commons.client.resources.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;

import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesAreaText;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Parameter.Filter;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;
import bpm.vanilla.platform.core.beans.resources.Resource;

public class ParameterResourceProperties extends PropertiesPanel<Resource> implements NameChanger {

	private Parameter parameter;

	private PropertiesAreaText txtQuestion;
	private PropertiesText txtDefaultValue;

	private PropertiesListBox lstType;
	private PropertiesListBox lstLOV;

	private PropertiesListBox lstDatasets;
	private PropertiesListBox lstColumns;

	private CheckBox cbHasParent;
	private PropertiesListBox lstParams;
	private PropertiesListBox lstFilters;

	private boolean isNameValid = true;

	private IResourceManager resourceManager;
	private String typeParam = null;
	
	private List<String> itemsDatasetsR;
	private List<DatabaseServer> databaseServers;

	public ParameterResourceProperties(NameChecker dialog, IResourceManager resourceManager, Parameter myParameter, String typeParam) {
		this(dialog, resourceManager, myParameter);
		this.typeParam = typeParam;
		this.resourceManager = resourceManager;

		initUI();

	}

	public ParameterResourceProperties(NameChecker dialog, IResourceManager resourceManager, Parameter myParameter) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.LARGE_PX, myParameter != null ? myParameter.getId() : 0, myParameter != null ? myParameter.getName() : LabelsCommon.lblCnst.ParameterWithNoAccent(), false, true);
		this.parameter = myParameter != null ? myParameter : new Parameter(LabelsCommon.lblCnst.ParameterWithNoAccent());
		this.resourceManager = resourceManager;

		setNameChecker(dialog);
		setNameChanger(this);

		if (typeParam == null) {
			initUI();
		}
	}

	private void initUI() {
		this.databaseServers = this.resourceManager.getDatabaseServers();
		
		txtQuestion = addTextArea(LabelsCommon.lblCnst.Definition(), parameter.getQuestion(), WidgetWidth.LARGE_PX);

		List<ListItem> items = new ArrayList<>();
		items.add(new ListItem(LabelsCommon.lblCnst.SimpleValue(), Parameter.TypeParameter.SIMPLE.getType()));
		items.add(new ListItem(LabelsCommon.lblCnst.ListOfValues(), Parameter.TypeParameter.LOV.getType()));
		items.add(new ListItem(LabelsCommon.lblCnst.RangeValue(), Parameter.TypeParameter.RANGE.getType()));
		items.add(new ListItem(LabelsCommon.lblCnst.SelectionList(), Parameter.TypeParameter.SELECTION.getType()));
		items.add(new ListItem(LabelsCommon.lblCnst.Database(), Parameter.TypeParameter.DB.getType()));
		
		lstType = addList(LabelsCommon.lblCnst.Type(), items, WidgetWidth.PCT, typeChangeHandler, null);
		lstType.setSelectedIndex(parameter.getParameterType() != null ? parameter.getParameterType().getType() : 0);

		final List<ListItem> lovitems = new ArrayList<>();
		lovitems.add(new ListItem(LabelsCommon.lblCnst.FromDataset(), 0));
		for (ListOfValues lov : resourceManager.getListOfValues()) {
			lovitems.add(new ListItem(lov.getName(), lov.getId()));
		}
		lstLOV = addList(LabelsCommon.lblCnst.ListOfValues(), lovitems, WidgetWidth.PCT, lovChangeHandler, null);

		if (parameter.getParameterType() != null && parameter.getParameterType().equals(TypeParameter.LOV)) {
			for (ListItem item : lovitems) {
				if (parameter.getDefaultValue().equals(item.getValue())) {
					lstLOV.setSelectedIndex(lovitems.indexOf(item));
					break;
				}
			}
		}
		else if (parameter.getParameterType() != null && parameter.getParameterType().equals(TypeParameter.SELECTION)) {
			if (parameter.getListOfValues() != null) {
				for (ListItem item : lovitems) {
					if (parameter.getListOfValues().getId() == Integer.parseInt(item.getValue())) {
						lstLOV.setSelectedIndex(lovitems.indexOf(item));
						break;
					}
				}
			}
			else {
				lstLOV.setSelectedIndex(0);
			}
		}
		else {
			lstLOV.setSelectedIndex(0);
		}
		
		lstDatasets = addList(LabelsCommon.lblCnst.Datasets(), new ArrayList<ListItem>(), WidgetWidth.PCT, datasetChangeHandler, null);
		lstColumns = addList(LabelsCommon.lblCnst.Columns(), new ArrayList<ListItem>(), WidgetWidth.PCT, null, null);

		txtDefaultValue = addText(LabelsCommon.lblCnst.DefaultValue(), parameter.getDefaultValue(), WidgetWidth.LARGE_PX, false);
		cbHasParent = addCheckbox(LabelsCommon.lblCnst.DefineParentParameter(), parameter.getIdParentParam() != 0, hasParentCheckHandler);

		typeChangeHandler.onChange(null);
		checkName(getTxtName(), parameter.getName());

		List<ListItem> paramitems = new ArrayList<>();
		for (Parameter par : resourceManager.getParameters()) {
			if (par.getDataset() != null && par.getDataset().equals(parameter.getDataset()) && par.getId() != parameter.getId()) {
				paramitems.add(new ListItem(par.getName(), par.getId()));
			}
		}

		List<ListItem> filteritems = new ArrayList<>();
		filteritems.add(new ListItem(LabelsCommon.lblCnst.Equals(), 0));
		filteritems.add(new ListItem(LabelsCommon.lblCnst.IsSuperior(), 1));
		filteritems.add(new ListItem(LabelsCommon.lblCnst.IsInferior(), 2));
		filteritems.add(new ListItem(LabelsCommon.lblCnst.Equals(), 3));
		filteritems.add(new ListItem(LabelsCommon.lblCnst.Contains(), 4));
		filteritems.add(new ListItem(LabelsCommon.lblCnst.NotContains(), 5));

		lstParams = addList(LabelsCommon.lblCnst.Parameters(), paramitems, WidgetWidth.PCT, null, null);
		lstFilters = addList(LabelsCommon.lblCnst.Operator(), filteritems, WidgetWidth.PCT, null, null);

		if (parameter.getIdParentParam() != 0) {
			for (int i = 0; i < lstParams.getListBox().getItemCount(); i++) {
				if (lstParams.getValue(i).equals(parameter.getIdParentParam() + "")) {
					lstParams.setSelectedIndex(i);
					break;
				}
			}
			for (int i = 0; i < lstFilters.getListBox().getItemCount(); i++) {
				if (lstFilters.getValue(i).equals(parameter.getFilter().getFiltre() + "")) {
					lstFilters.setSelectedIndex(i);
					break;
				}
			}
		}
		hasParentCheckHandler.onValueChange(null);

		fillRDatasets();
	}

	@Override
	public boolean isValid() {
		return isNameValid;
	}

	@Override
	public void changeName(final String value, boolean isValid) {
		this.isNameValid = isValid;

		WorkflowsService.Connect.getInstance().clearName(value, new GwtCallbackWrapper<String>(this, true, true) {

			@Override
			public void onSuccess(String clearName) {
				parameter.setName(clearName);
				getTxtName().setText(clearName);

				if (!clearName.equals(value)) {
					getTxtName().setTxtError(LabelsCommon.lblCnst.AutomaticNameChange());
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public Resource buildItem() {
		String question = txtQuestion.getText();
		String defaultValue = txtDefaultValue.getText();

		parameter.setQuestion(question);
		parameter.setDefaultValue(defaultValue);

		parameter.setParameterType(TypeParameter.valueOf(Integer.parseInt(lstType.getValue(lstType.getSelectedIndex()))));
		if (parameter.getParameterType().equals(TypeParameter.SELECTION)) {
			if (lstLOV.getSelectedIndex() == 0) {
				parameter.setSelectionDataset(lstDatasets.getValue(lstDatasets.getSelectedIndex()), lstColumns.getValue(lstColumns.getSelectedIndex()));
			}
			else {
				parameter.setSelectionListOfValues(getSelectedLOV());
			}

		}
		else if (parameter.getParameterType().equals(TypeParameter.LOV)) {
			ListOfValues vals = null;
			parameter.setValueListOfValues(vals);
			parameter.setDefaultValue(lstLOV.getValue(lstLOV.getSelectedIndex()));
		}
		else if (parameter.getParameterType().equals(TypeParameter.DB)) {
			DatabaseServer server = findDatabaseServer(lstDatasets.getValue(lstDatasets.getSelectedIndex()));
			String column = lstColumns.getValue(lstColumns.getSelectedIndex());
			parameter.setDataset(server.getId() + "", column);
		}

		if (cbHasParent.getValue()) {
			parameter.setIdParentParam(Integer.parseInt(lstParams.getValue(lstParams.getSelectedIndex())));
			for (Parameter param : resourceManager.getParameters()) {
				if (param.getId() == parameter.getIdParentParam()) {
					parameter.setParentParam(param);
					break;
				}
			}
			parameter.setFilter(Filter.valueOf(Integer.parseInt(lstFilters.getValue(lstFilters.getSelectedIndex()))));
		}
		else {
			parameter.setIdParentParam(0);
			parameter.setParentParam(null);
			parameter.setFilter(null);
		}

		return parameter;
	}

	private ListOfValues getSelectedLOV() {
		for (ListOfValues lov : resourceManager.getListOfValues()) {
			if (lov.getId() == Integer.parseInt(lstLOV.getValue(lstLOV.getSelectedIndex()))) {
				return lov;
			}
		}
		return null;
	}

	private void fillRDatasets() {
		String script = "f<-function(x){try(paste(x,class(get(x)), sep=';'))}\n";
		script += "manual_result <- sapply(ls(),f)\n";
		script += "rm(f)";

		RScriptModel box = new RScriptModel();
		box.setScript(script);
		box.setOutputs("manual_result".split(" "));

		WorkflowsService.Connect.getInstance().executeScript(box, new GwtCallbackWrapper<RScriptModel>(this, true, true) {
			
			@Override
			public void onSuccess(RScriptModel result) {
				List<String> list = new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(0).split("\t")));
				Collections.sort(list);
				
				itemsDatasetsR = new ArrayList<String>();
				for (String item : list) {
					if (item.contains(";") && item.split(";")[1].equals("data.frame")) {
						String value = item.split(";")[0].trim();
						itemsDatasetsR.add(value);
					}
				}
				
				updateDatasetsUI();
			}
		}.getAsyncCallback());
	}

	private void fillRColumns(final boolean firsttime) {
		lstColumns.clear();
		
		String script = "manual_result <- names(" + lstDatasets.getValue(lstDatasets.getSelectedIndex()) + ")";

		RScriptModel box = new RScriptModel();
		box.setScript(script);
		box.setOutputs("manual_result".split(" "));
		
		WorkflowsService.Connect.getInstance().executeScript(box, new GwtCallbackWrapper<RScriptModel>(this, true, true) {
			@Override
			public void onSuccess(RScriptModel result) {
				List<String> list = new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(0).split("\t")));
				if (list.size() > 0)
					Collections.sort(list);
				for (String item : list) {
					if (!item.trim().equals("1"))
						lstColumns.addItem(item.trim());
				}
				if (parameter.getParameterType() != null && parameter.getParameterType().equals(TypeParameter.SELECTION) && parameter.getListOfValues() == null && firsttime) {
					for (int i = 0; i < lstColumns.getListBox().getItemCount(); i++) {
						if (lstColumns.getValue(i).equals(parameter.getColumn())) {
							lstColumns.setSelectedIndex(i);
							break;
						}
					}
				}
			}
		}.getAsyncCallback());
	}

	private void fillColumns() {
		lstColumns.clear();
		
		DatabaseServer databaseServer = findDatabaseServer(lstDatasets.getValue(lstDatasets.getSelectedIndex()));
		
		CommonService.Connect.getInstance().getDataSetMetaData(databaseServer, new GwtCallbackWrapper<ArrayList<DataColumn>>(this, true, true) {
			@Override
			public void onSuccess(ArrayList<DataColumn> result) {
				if (result != null) {
					for (DataColumn column : result) {
						lstColumns.addItem(column.getColumnName());
					}
					if (parameter.getParameterType() != null && parameter.getParameterType().equals(TypeParameter.DB)) {
						for (int i = 0; i < lstColumns.getListBox().getItemCount(); i++) {
							if (lstColumns.getValue(i).equals(parameter.getColumn())) {
								lstColumns.setSelectedIndex(i);
								break;
							}
						}
					}
				}
			}
		}.getAsyncCallback());
	}
	
	private DatabaseServer findDatabaseServer(String databaseServerName) {
		if (databaseServers != null) {
			for (DatabaseServer server : databaseServers) {
				if (server.getName().equals(databaseServerName)) {
					return server;
				}
			}
		}
		return null;
	}
	
	private TypeParameter getTypeParameter() {
		return TypeParameter.valueOf(Integer.parseInt(lstType.getValue(lstType.getSelectedIndex())));
	}
	
	private void updateDatasetsUI() {
		TypeParameter typeParameter = getTypeParameter();
		if (typeParameter == TypeParameter.DB) {
			lstDatasets.setVisible(true);
			lstColumns.setVisible(true);
			cbHasParent.setVisible(false);
			
			lstDatasets.clear();
			if (databaseServers != null) {
				for (DatabaseServer server : databaseServers) {
					lstDatasets.addItem(server.getName());
				}
			}
			
			datasetChangeHandler.onChange(null);
		}
		else {
			boolean value = Integer.parseInt(lstLOV.getValue(lstLOV.getSelectedIndex())) == 0 && lstLOV.getListBox().isVisible();
			lstDatasets.setVisible(value);
			lstColumns.setVisible(value);
			cbHasParent.setVisible(value);

			
			lstDatasets.clear();
			if (itemsDatasetsR != null) {
				for (String item : itemsDatasetsR) {
					lstDatasets.addItem(item);
				}
			}

			if (lstDatasets.getListBox().getItemCount() > 0) {
				if (parameter.getParameterType() != null && parameter.getParameterType().equals(TypeParameter.SELECTION) && parameter.getListOfValues() == null) {
					for (int i = 0; i < lstDatasets.getListBox().getItemCount(); i++) {
						if (lstDatasets.getValue(i).equals(parameter.getDataset())) {
							lstDatasets.setSelectedIndex(i);
							break;
						}
					}
				}
				fillRColumns(true);
			}
		}
	}

	private ChangeHandler typeChangeHandler = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			TypeParameter typeParameter = getTypeParameter();
			if (typeParameter == TypeParameter.SELECTION) {
				lstLOV.setVisible(true);
				txtDefaultValue.setVisible(true);
				if (!lstLOV.getValue(0).equals("0")) {
					lstLOV.getListBox().insertItem(LabelsCommon.lblCnst.FromDataset(), 0 + "", 0);
				}
			}
			else if (typeParameter == TypeParameter.LOV) {
				lstLOV.setVisible(true);
				txtDefaultValue.setVisible(false);
				if (lstLOV.getValue(0).equals("0")) {
					lstLOV.getListBox().removeItem(0);
				}
			}
			else if (typeParameter == TypeParameter.DB) {
				lstLOV.setVisible(false);
				txtDefaultValue.setVisible(false);
			}
			else {
				lstLOV.setVisible(false);
				txtDefaultValue.setVisible(true);
			}
			lovChangeHandler.onChange(null);
		}
	};

	private ChangeHandler lovChangeHandler = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			updateDatasetsUI();
		}
	};

	private ChangeHandler datasetChangeHandler = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			TypeParameter typeParameter = getTypeParameter();
			if (typeParameter == TypeParameter.DB) {
				fillColumns();
			}
			else {
				fillRColumns(false);
			}
		}
	};

	private ValueChangeHandler<Boolean> hasParentCheckHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			boolean hasParent = cbHasParent.getValue();
			lstParams.setVisible(hasParent);
			lstFilters.setVisible(hasParent);
		}
	};
}
