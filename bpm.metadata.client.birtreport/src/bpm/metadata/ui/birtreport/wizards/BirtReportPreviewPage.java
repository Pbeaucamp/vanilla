package bpm.metadata.ui.birtreport.wizards;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.PageOptions;
import bpm.fwr.api.beans.Constants.Orientation;
import bpm.fwr.api.beans.components.GridComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.metadata.ui.birtreport.templates.DefaultTemplate;
import bpm.metadata.ui.birtreport.tools.ConnectionHelper;
import bpm.metadata.ui.birtreport.tools.RunReport;
import bpm.metadata.ui.birtreport.trees.TreeDataStreamElement;
import bpm.metadata.ui.birtreport.trees.TreeFilter;
import bpm.metadata.ui.birtreport.trees.TreePrompt;
import bpm.metadata.ui.birtreport.trees.TreeResource;


public class BirtReportPreviewPage  extends WizardPage{
	
	//Report parameters
	private FWRReport report;
	private List<TreeDataStreamElement> groups, columns;
	private List<TreeResource> ressources;
	private List<FwrPrompt> fwrPrompts;
	private List<FWRFilter> fwrFilters;
	private String packageName, modelName;
	private int itemId;
	
	//We display the report in html
	private Browser browser;
	
	private Composite mainComposite;
	
	protected BirtReportPreviewPage(String pageName, int id) {
		super(pageName);
		this.itemId = id;
	}

	@Override
	public void createControl(Composite parent) {
		// create main composite & set layout
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Button generateReport = new Button(mainComposite, SWT.NONE);
		generateReport.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
		generateReport.setText("Generate the report");
		generateReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Cursor cursor = new Cursor(mainComposite.getDisplay(), SWT.CURSOR_WAIT);
				mainComposite.setCursor(cursor);
				
				fwrPrompts = new ArrayList<FwrPrompt>();
				fwrFilters = new ArrayList<FWRFilter>();
				for(TreeResource r : ressources) {
					if(r instanceof TreeFilter){
						fwrFilters.add(transformToApiFilter((TreeFilter) r));
					}
					if(r instanceof TreePrompt){
						fwrPrompts.add(transformToApiPrompt((TreePrompt) r));
					}
				}
				report = buildReport();
				RunReport run = null;
				try{
					run = new RunReport(report);
					String html = run.getHtml();
					String path = System.getProperty("user.dir") + "/tempHtml.html";
					FileWriter fw = new FileWriter(path, false);
					
					BufferedWriter output = new BufferedWriter(fw);
					output.write(html);
					output.flush();
					output.close();
					browser.setUrl(path);
					setPageComplete(true);


					mainComposite.setCursor(null);
					
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), "Generating Report", "An error occured - "  + ex.getMessage());
					
				}finally{
					mainComposite.setCursor( null);
					cursor.dispose();
				}
								
				
				
				
			}
		});
		
		browser = new Browser(mainComposite, SWT.BORDER);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setText("");
		
		// page setting
		setControl(mainComposite);
		setPageComplete(false);
	}
	
	
	public void fillInformations(List<TreeDataStreamElement> gr, 
			List<TreeDataStreamElement> cl, 
			List<TreeResource> res,
			String packName,
			String modName){
		
		this.groups = gr;
		this.columns = cl;
		this.ressources = res;
		this.packageName = packName;
		this.modelName = modName;
	}
	
	
	private FWRReport buildReport(){		
		FWRReport report = new FWRReport();
		report.setRowCount(1);
		report.setColCount(1);
		
		DataSource ds = transformToApiDatasource();		
		
		DataSet dataSet = new DataSet();
		List<Column> allColumns = new ArrayList<Column>();
		allColumns.addAll(transformToApiColumns(groups));
		allColumns.addAll(transformToApiColumns(columns));
		dataSet.setColumns(allColumns);
		for(FWRFilter fil : fwrFilters){
			dataSet.addFwrFilter(fil);
		}
		for(FwrPrompt prt : fwrPrompts) {
			dataSet.addPrompt(prt);
		}
		dataSet.setName("dataset");
		dataSet.setLanguage("fr");
		dataSet.setDatasource(ds);
		
		GridComponent rep = new GridComponent();
			
		//add columns
		List<Column> cols = new ArrayList<Column>();
		cols.addAll(transformToApiColumns(groups));
		cols.addAll(transformToApiColumns(columns));
		
		//add groups
		ArrayList<Column> grps = new ArrayList<Column>();
		grps.addAll(transformToApiColumns(groups));
		
		rep.setColumns(cols);
		rep.setGroups(grps);
					
		//set styles to the report
		DefaultTemplate template = new DefaultTemplate();
		rep.setDataCellsStyle(template.getDataStyle());
		rep.setHeaderCellsStyle(template.getHeaderStyle());
		rep.setOddRowsBackgroundColor(template.getOddRowsBackgroundColor());
		
		//add filters and prompts to the report (for the load to work)
		rep.setFilters(new ArrayList<String>());
		rep.setFwrFilters(new ArrayList<FWRFilter>());
		rep.setRelations(new ArrayList<FwrRelationStrategy>());
		for(FWRFilter fil : fwrFilters){
			dataSet.addFwrFilter(fil);
		}
		for(FwrPrompt prt : fwrPrompts) {
			dataSet.addPrompt(prt);
		}
		
		//set dataset and coordonates for the report
		rep.setDataset(dataSet);
		report.setSelectedLanguage(dataSet.getLanguage());
		rep.setX(0);
		rep.setY(0);
				
		//add dataset to the complexReport
		report.addComponent(rep);
		
		//add output options
		report.setHeader(new PageOptions());
		report.getHeader().setText("");
		report.setFooter(new PageOptions());
		report.getFooter().setText("");
		report.setOrientation(Orientation.PORTRAIT);
		report.setOutput("html");
		HashMap<String, String> margins = new HashMap<String, String>();
		margins.put("top", "10");
		margins.put("bottom", "10");
		margins.put("left", "10");
		margins.put("right", "10");
		report.setMargins(margins);
		report.setPageSize("A4");
		
		report.setTitleStyle(template.getTitleStyle());
		report.setSubTitleStyle(template.getSubTitleStyle());
		
		return report;
	}

	public List<Column> transformToApiColumns(List<TreeDataStreamElement> columns) {
		if(!columns.isEmpty()) {
			List<Column> res = new ArrayList<Column>();
			for (TreeDataStreamElement c : columns) {
				Column col = new Column();
				HashMap<String, String> titles = new HashMap<String, String>();
				titles.put("fr", c.getDataStreamElement().getOuputName(Locale.getDefault()));
				col.setTitles(titles);
				col.setJavaClass(null);
				col.setName(c.getName());
				col.setBusinessTableParent(c.getParent().getName());
				
				res.add(col);
			}
			return res;
		}
		return new ArrayList<bpm.fwr.api.beans.dataset.Column>();
	}
	
	public DataSource transformToApiDatasource() {

		DataSource ds = new DataSource();
		ds.setName("datasource");
		ds.setBusinessModel(modelName);
		ds.setBusinessPackage(packageName);
		ds.setConnectionName("Default");
		ds.setGroup(ConnectionHelper.getInstance().getGroupName());
		ds.setItemId(itemId);
		ds.setRepositoryId(ConnectionHelper.getInstance().getRepositoryId());
		ds.setUser(ConnectionHelper.getInstance().getLogin());
		if (ConnectionHelper.getInstance().getIsEncrypted() == null){
			ds.setEncrypted(false);
		}
		else{
			ds.setEncrypted(ConnectionHelper.getInstance().getIsEncrypted());	
		}
		
		ds.setPassword(ConnectionHelper.getInstance().getPassword());
		ds.setUrl(ConnectionHelper.getInstance().getRepositoryUrl());
		return ds;
	}
	
	private FwrPrompt transformToApiPrompt(TreePrompt prompt){
		FwrPrompt pro = new FwrPrompt();
		InputDialog promptDial = new InputDialog(getShell(), 
				"Prompt Dialog : " + prompt.getName(), 
				"Please choose a value:", 
				"", 
				null);
		promptDial.open();
		pro.setName(prompt.getName());
		pro.setQuestion(prompt.getPrompt().getQuestion());
//		pro.setOperator(prompt.getPrompt().getOperator());
		pro.initSelectedValues(promptDial.getValue());
		return pro;
	}
	
	private FWRFilter transformToApiFilter(TreeFilter filter){
		FWRFilter fil = new FWRFilter();
		fil.setName(filter.getName());
		fil.setColumnName(filter.getFilter().getDataStreamElementName());
		fil.setTableName(filter.getFilter().getDataStreamName());
		fil.setOperator("=");
		fil.setValues(filter.getFilter().getValues());
		return fil;
	}
	
	public FWRReport getReport() {
		return report;
	}
	
}
