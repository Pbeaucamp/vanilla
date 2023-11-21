package bpm.profiling.ui.views;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import bpm.profiling.database.AnalysisManager;
import bpm.profiling.database.bean.AnalysisConditionResult;
import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.database.bean.AnalysisResultBean;
import bpm.profiling.database.bean.RuleSetBean;
import bpm.profiling.database.bean.TagBean;
import bpm.profiling.runtime.core.AnalysisExecutor;
import bpm.profiling.runtime.core.Condition;
import bpm.profiling.ui.Activator;
import bpm.profiling.ui.composite.CompositeAnalysisResult;
import bpm.profiling.ui.composite.CompositeRuleSetResult;
import bpm.profiling.ui.dialogs.DialogSelectRuleSet;

public class ViewResult extends ViewPart {

	public static final String ID = "bpm.profiling.ui.view.result";
	private CompositeAnalysisResult result ;
	private AnalysisInfoBean infos;
	private Composite main;
	
	private TabFolder folder;
	private List<CompositeRuleSetResult> items = new ArrayList<CompositeRuleSetResult>();
	
	private ToolBar bar;
	
	private HashMap<AnalysisContentBean, HashMap<RuleSetBean, List<AnalysisConditionResult>>> values;
	public ViewResult() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		
		main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());
		
		createToolbar(main);
	
		
		
		
	}

	@Override
	public void setFocus() {
		

	}
	
	
	
	public void loadFromHistoric(AnalysisInfoBean analysisInfo, Date date){
//		bar.setEnabled(false);
		
		items.clear();
		if (folder != null && !folder.isDisposed()){
			folder.dispose();
		}
		folder = new TabFolder(main, SWT.NONE);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		
		
		values = new HashMap<AnalysisContentBean, HashMap<RuleSetBean, List<AnalysisConditionResult>>>();
		HashMap<AnalysisContentBean, AnalysisResultBean> analysisResults = new HashMap<AnalysisContentBean, AnalysisResultBean>();
		List<TagBean> analysisTags = new ArrayList<TagBean>();
		
		AnalysisManager mgr = Activator.helper.getAnalysisManager();
		for(AnalysisContentBean content : mgr.getAllAnalysisContentFor(analysisInfo)){
			analysisResults.put(content, mgr.getAnalysisResultsFor(content, date));

			analysisTags.addAll(mgr.getTagsFor(analysisResults.get(content)));
			
			
			
			if (values.get(content) == null ){
				values.put(content, new HashMap<RuleSetBean, List<AnalysisConditionResult>>());
				
			}
			
			for(RuleSetBean rsB : mgr.getRuleSetsFor(content)){
				
				if (values.get(content).get(rsB) == null){
					values.get(content).put(rsB, new ArrayList<AnalysisConditionResult>());
				}
				
				
				for(Condition c : mgr.getConditionForRuleSet(rsB)){
					for(AnalysisConditionResult ar : mgr.getConditionResultFor(c.getId(), date)){
						values.get(content).get(rsB).add(ar);
					}
				}
				
				
				
				for(AnalysisConditionResult ar : mgr.getConditionResultFor(rsB, date)){
					values.get(content).get(rsB).add(ar);
				}
				
				
			}
			for(RuleSetBean rsb : values.get(content).keySet()){
				
				
				HashMap<Condition, AnalysisConditionResult> map = new HashMap<Condition, AnalysisConditionResult> ();
				List<TagBean> conditionTags = new ArrayList<TagBean>();
				
				for(AnalysisConditionResult v : values.get(content).get(rsb)){
					Condition c = Activator.helper.getAnalysisManager().getConditionForId(v.getConditionId());
					
					
					if (c != null){
						map.put(c, v);
					}
					else{
						Condition cd = new Condition();
						cd.setId(-1);
						map.put(cd, v);
					}
					
					
					for(TagBean t : mgr.getTagsFor(v)){
						conditionTags.add(t);
					}
					
				}
				
				
				TabItem tb = new TabItem(folder, SWT.NONE);
				tb.setText("RuleSet " + rsb.getName());
				tb.setToolTipText("RuleSet "+ rsb.getName() + " for Column " + content.getTableName() + "." + content.getColumnName());
				CompositeRuleSetResult comp = new CompositeRuleSetResult(folder, SWT.NONE);
				comp.setInput(rsb, map);
				comp.setTags(conditionTags);
				tb.setControl(comp);
				items.add(comp);

			}
			
		}

		TabItem general = new TabItem(folder, SWT.NONE);
		general.setControl(result = new CompositeAnalysisResult(folder, SWT.NONE));
		
		general.setText("General Results");
		result.setInput(analysisResults);
		result.setTags(analysisTags);
		main.layout();
	}
	
	private void executeAnalysis(){
		bar.setEnabled(true);
		Date currentDate = Calendar.getInstance().getTime();
		
		ViewAnalysis view = (ViewAnalysis)getSite().getWorkbenchWindow().getActivePage().findView(ViewAnalysis.ID);
		AnalysisInfoBean infos = view.getSelection();
		
		this.infos = infos;
		
		
		
		
		items.clear();
		if (folder != null && !folder.isDisposed()){
			folder.dispose();
		}
		folder = new TabFolder(main, SWT.NONE);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabItem general = new TabItem(folder, SWT.NONE);
		general.setControl(result = new CompositeAnalysisResult(folder, SWT.NONE));
		general.setText("General Results");
		

			try {
				result.setInput(currentDate, infos);
				values = AnalysisExecutor.executeAnalysisForConditions(currentDate,
							Activator.getDefault().getConnection(infos.getConnectionId()), 
							infos);
				
			
				
				for(AnalysisContentBean content : values.keySet()){
					HashMap<RuleSetBean, List<AnalysisConditionResult>> rsBV = values.get(content);
					
					
					for(RuleSetBean rsb : rsBV.keySet()){
					
						
						HashMap<Condition, AnalysisConditionResult> map = new HashMap<Condition, AnalysisConditionResult> ();
						
						for(AnalysisConditionResult v : rsBV.get(rsb)){
							Condition c = Activator.helper.getAnalysisManager().getConditionForId(v.getConditionId());
							
							
							if (c != null){
								map.put(c, v);
							}
							else{
								Condition cd = new Condition();
								cd.setId(-1);
								map.put(cd, v);
							}
							
						}
						
						
						TabItem tb = new TabItem(folder, SWT.NONE);
						tb.setText("RuleSet " + rsb.getName());
						tb.setToolTipText("RuleSet "+ rsb.getName() + " for Column " + content.getTableName() + "." + content.getColumnName());
						CompositeRuleSetResult comp = new CompositeRuleSetResult(folder, SWT.NONE);
						comp.setInput(rsb, map);
						tb.setControl(comp);
						items.add(comp);
	
					}
					
				}
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}

		main.layout();
	}
	
	

	
	private void createToolbar(Composite parent){
		bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText("Save Result");
		it.setImage(Activator.getDefault().getImageRegistry().get("save"));
		it.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				DialogSelectRuleSet dial = new DialogSelectRuleSet(getSite().getShell(), infos);
				
				if(dial.open() != DialogSelectRuleSet.OK){
					return;
				}
				
				
				for(AnalysisResultBean b : result.getInput().values()){
					Activator.helper.getAnalysisManager().createAnalysisResult(b);
					for(TagBean tag : result.getTags()){
						if (tag.getResultId() == null){
							tag.setResultId(b.getId());
							try {
								Activator.helper.getAnalysisManager().createTag(tag);
							} catch (Exception e1) {
								
								e1.printStackTrace();
							}
						}
						
					}
				}

				
				for(HashMap<RuleSetBean, List<AnalysisConditionResult>> m : values.values()){
					for(RuleSetBean rs : dial.getRuleSets()){
						RuleSetBean key = null;
						
						for(RuleSetBean r : m.keySet()){
							if (r.getId() == rs.getId()){
								key = r;
								break;
							}
						}
						
						if (m.get(key) == null){
							continue;
						}
						
						for(AnalysisConditionResult a : m.get(key)){
							try {
								
								Activator.helper.getAnalysisManager().createConditionResult(a);
								
								
								for(CompositeRuleSetResult c : items){
									if (c.getRuleSet().getId() == key.getId()){
										
										for(TagBean tag : c.getTags()){
											if (tag.getRuleSetId() == c.getRuleSet().getId() && tag.getResultConditionId() == null){
												tag.setResultConditionId(a.getId());
												Activator.helper.getAnalysisManager().createTag(tag);
											}
											
										}
										
									}
								}
								
								
								
							} catch (Exception e1) {
								e1.printStackTrace();
								MessageDialog.openError(getSite().getShell(), "Error", e1.getMessage());
							}
						}
					}
					
					
					
				}
				
				
			}
			
		});
	
	
	
		ToolItem xls = new ToolItem(bar, SWT.PUSH);
		xls.setToolTipText("Export as XLS");
		xls.setImage(Activator.getDefault().getImageRegistry().get("xls"));
		xls.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				FileDialog fd = new FileDialog(getSite().getShell());
				fd.setFilterExtensions(new String[]{"*.xls"});
				String s = fd.open();
				
				if (s!= null){
					try {
						createXLS(s);
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), "Error when writing XLS file", e1.getMessage());
					}
				}
				
				
				
			}
			
		});
		
		ToolItem run = new ToolItem(bar, SWT.PUSH);
		run.setToolTipText("Launch analysis");
		run.setImage(Activator.getDefault().getImageRegistry().get("run"));
		run.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				BusyIndicator.showWhile( Display.getDefault(), new Runnable(){
					public void run(){
						executeAnalysis();
					}
				});

				
			}
			
		});

	}
	
	
	private void createXLS(String fileName) throws Exception{
		if (!fileName.endsWith(".xls")){
			fileName += ".xls";
		}
		File f = new File(fileName);
		if (!f.exists()){
			f.createNewFile();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			
		WritableWorkbook workbook = Workbook.createWorkbook(f);
		
		/*
		 * information Page
		 */
		WritableCellFormat headerFormat = new WritableCellFormat();
		headerFormat.setBackground(Colour.AQUA);
		headerFormat.setAlignment(Alignment.FILL);
		
		WritableCellFormat standardFormat = new WritableCellFormat();
		WritableCellFormat ruleSetAllFormat = new WritableCellFormat();
		ruleSetAllFormat.setBackground(Colour.DARK_RED2);
		
		WritableSheet sheet = workbook.createSheet("General Information", 0);
		List<Label> listLabel = new ArrayList<Label>();
		listLabel.add(new Label(0,0, "Analysis Name"));
		listLabel.add(new Label(1,0, infos.getName()));
		listLabel.add(new Label(0,1, "Description"));
		listLabel.add(new Label(1,1, infos.getDescription()));
		
		listLabel.add(new Label(0,2, "Date"));
		listLabel.add(new Label(1,2, sdf.format(Calendar.getInstance().getTime())));
		
		listLabel.add(new Label(0,3, "Creator"));
		listLabel.add(new Label(1,3, infos.getCreator()));
		listLabel.add(new Label(0,4, "Last Modification"));
		listLabel.add(new Label(1,4, sdf.format(infos.getModification())));
		
		
		for(Label l : listLabel){
			sheet.addCell(l);
		}
		
			
		
		/*
		 * general Page
		 */
		sheet = workbook.createSheet("Analysis Result", 1);
		
		String[][] values = result.getInputAsArray();
		
		for(int i = 0; i < values.length; i++){
			for(int j = 0; j < values[i].length; j++){
				sheet.addCell(new Label(j,i, values[i][j]));
			}
		}
		
		int n = 2; 
		
		for(CompositeRuleSetResult cmp : items){
			sheet = workbook.createSheet("Rule Set " + cmp.getRuleSet().getName(), n++);
			
		
			values = cmp.getInput();
			for(int i = 0; i < values.length; i++){
				for(int j = 0; j < values[i].length; j++){
					sheet.addCell(new Label(j, i, values[i][j]));
				}
			}
		}
		
		workbook.write();
		workbook.close();
	}
	
	
}
