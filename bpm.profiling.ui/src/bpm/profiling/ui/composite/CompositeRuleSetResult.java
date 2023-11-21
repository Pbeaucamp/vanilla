package bpm.profiling.ui.composite;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jfree.data.general.DefaultPieDataset;

import bpm.profiling.database.bean.AnalysisConditionResult;
import bpm.profiling.database.bean.RuleSetBean;
import bpm.profiling.database.bean.TagBean;
import bpm.profiling.runtime.core.Condition;
import bpm.profiling.ui.dialogs.DialogCreateTag;
import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableDefaultModel;
import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.renderers.DefaultCellRenderer;

public class CompositeRuleSetResult extends Composite {

	
	private KTable ktable;
	private HashMap<Condition, AnalysisConditionResult> results;
	private List<TagBean> tags = new ArrayList<TagBean>();
	
	private CompositeRuleSet compositeRuleSet;
	private RuleSetBean ruleSet;
	
	
	private CompositePieChart pieChart;
	private CompositeListTag tagList;
	private Composite chartContainer; 
	
	public CompositeRuleSetResult(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		buildContent();
	}
	
	private void buildContent(){
		
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		l.setText("Rule Set Informations");
		
		compositeRuleSet  = new CompositeRuleSet(this, SWT.NONE);
		compositeRuleSet.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		l2.setText("Rule Set Results");
		
		ktable = new KTable(this, SWT.FULL_SELECTION | SWTX.AUTO_SCROLL);
		ktable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		chartContainer = new Composite(this, SWT.NONE);
		chartContainer.setLayoutData(new GridData(GridData.BEGINNING, GridData.END, false, false));
		chartContainer.setLayout(new GridLayout(2, false));

		createMenu();
	}
	
	public void setInput(RuleSetBean ruleSet, HashMap<Condition, AnalysisConditionResult> results) {
		compositeRuleSet.setInput(ruleSet);
		compositeRuleSet.setEnabled(false);
		
		this.ruleSet = ruleSet;
		this.results = results;
		
		
		/*
		 * re-init tags
		 */
		
		
		
		KTableModel model = null;
		model = new ConditionResultModel(results);
		ktable.setModel(model);
		
		
		/*
		 * build chart DataSet
		 */
		DefaultPieDataset dataSet = new DefaultPieDataset();
		
		for(Condition key : results.keySet()){
			
			if (key.getId() <= 0){
				

				try{
					dataSet.setValue("Valid",  results.get(key).getValidCount());
					dataSet.setValue("No Valid", (int)(results.get(key).getValidCount() /  results.get(key).getValidCountPercent() - results.get(key).getValidCount()));
					
					if (results.get(key).getValidCountPercent() < 0.00001d){
						dataSet.setValue("No Valid", 100);
					}
					
					
				}catch(Exception e){
					dataSet.setValue("Valid",  0);
					dataSet.setValue("No Valid", 100);
				}
				
				
				break;
			}
		}
	
		/*
		 * draw chart
		 */
		if (pieChart != null  && !pieChart.isDisposed()){
			pieChart.dispose();
		}
		
		if (tagList != null  && !tagList.isDisposed()){
			tagList.dispose();
		}
		
		
		pieChart = new CompositePieChart(chartContainer, dataSet, ruleSet.getName() + " valid datas");
		pieChart.setLayoutData(new GridData(GridData.BEGINNING, GridData.END, false, false));
		
		
		/*
		 * 
		 */
		tagList = new CompositeListTag(chartContainer, SWT.NONE);
		tagList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
			
		
		chartContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		
		chartContainer.layout();
		this.redraw();
		pieChart.drawChart(new Rectangle(0, 0, 500, 350));

		
		
		this.redraw();
	}

	
	
	
	private void createMenu(){

		final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
            	int[] i =ktable.getRowSelection();
            	if (i == null || i.length < 1 || i[0] == 0){
            		return;
            	}

            
            	ActionCreateTag ac = new ActionCreateTag("Add Tag on selected row", ruleSet.getId());
            	menuMgr.add(ac);
            	menuMgr.update();
            	
            	
            }
        });   
        ktable.setMenu(menuMgr.createContextMenu(ktable));
        

	}
	
	
	private class ActionCreateTag extends Action{
		int ruleSetId;
		
		public ActionCreateTag(String name, int ruleSetId){
			super(name);
			this.ruleSetId = ruleSetId;
		}
		
		public void run(){
			DialogCreateTag dial = new DialogCreateTag(getShell());
			
			if (dial.open() == DialogCreateTag.OK){
				TagBean tag = dial.getTag();
				tag.setRuleSetId(ruleSetId);
				tags.add(tag);
				tagList.setInput(tags);
				
			}
		}
	}
	
	
	
	public class ConditionResultModel extends KTableDefaultModel{
		private String[][] values;
		protected List<Condition> conditionPos = new ArrayList<Condition>();
		
		private DefaultCellRenderer renderer = new DefaultCellRenderer(SWT.NONE);
		public ConditionResultModel(HashMap<Condition, AnalysisConditionResult> values ){
			this.values = new String[values.size() + 1][7];
			
			int rowCount = 1;
			
			this.values[0] = new String[14];
			this.values[0][0] = "Operator";
			this.values[0][1] = "Value1";
			this.values[0][2] = "Value2";
			this.values[0][3] = "ValidCount";
			this.values[0][4] = "ValidPercent";
			this.values[0][5] = "DistinctValidCount";
			this.values[0][6] = "DistinctValidPercent";
			
			
			String[] lastRow = new String[7];
			
			
			for(Condition key : values.keySet()){
				
				if (key.getId() <= 0){
					lastRow[0] = "ALL";
					lastRow[1] = "";
					lastRow[2] = "";
					lastRow[3] = values.get(key).getValidCount() + "";
					lastRow[4] = NumberFormat.getPercentInstance().format(values.get(key).getValidCountPercent());
					lastRow[5] = values.get(key).getDistinctValidCount() + "";
					lastRow[6] = NumberFormat.getPercentInstance().format(values.get(key).getDictinctValidPercent());

					continue;
				}
				
				if (!conditionPos.contains(key)){
					conditionPos.add(key);
				}
				this.values[rowCount] = new String[7];
				this.values[rowCount][0] = Condition.operators[key.getOperator()];
				
				this.values[rowCount][1] = key.getValue1();
				this.values[rowCount][2] = key.getValue2();
				this.values[rowCount][3] = values.get(key).getValidCount() + "";
				this.values[rowCount][4] = NumberFormat.getPercentInstance().format(values.get(key).getValidCountPercent());
				this.values[rowCount][5] = values.get(key).getDistinctValidCount() + "";
				this.values[rowCount][6] = NumberFormat.getPercentInstance().format(values.get(key).getDictinctValidPercent());
				rowCount++;
				
			}
			
			for(int i = 0; i < this.values.length; i++){
				for(int j = 0; j < this.values[i].length; j++){
					if (this.values[i][j] ==  null){
						this.values[i][j] = new String("");
					}
				}
			}
			for(String s : lastRow){
				if (s!= null){
					this.values[rowCount] = lastRow;
					rowCount++;
					break;
				}
			}
			
			
			
		}
		@Override
		public KTableCellEditor doGetCellEditor(int arg0, int arg1) {
			return null;
		}
		@Override
		public KTableCellRenderer doGetCellRenderer(int arg0, int arg1) {
			return renderer;
		}
		@Override
		public int doGetColumnCount() {
			return 7;
		}
		@Override
		public Object doGetContentAt(int arg0, int arg1) {
			try{
				
				if (values[arg1][arg0] == null || values[arg1][arg0] .equals("null")){
					return "";
				}
				return values[arg1][arg0];
			}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
				return "";
			}
			
		}
		@Override
		public int doGetRowCount() {
			return values.length;
		}
		@Override
		public void doSetContentAt(int arg0, int arg1, Object arg2) {
			
		}
		@Override
		public int getInitialColumnWidth(int arg0) {
			return 100;
		}
		@Override
		public int getInitialRowHeight(int arg0) {
			return 20;
		}
		public int getFixedHeaderColumnCount() {
			return 1;
		}
		public int getFixedHeaderRowCount() {
			return 1;
		}
		public int getFixedSelectableColumnCount() {
			return 0;
		}
		public int getFixedSelectableRowCount() {
			return 0;
		}
		public int getRowHeightMinimum() {
			return 0;
		}
		public boolean isColumnResizable(int arg0) {
			return true;
		}
		public boolean isRowResizable(int arg0) {
			return false;
		}

		
	}








	public RuleSetBean getRuleSet(){
		return ruleSet;
	}
	
	public List<TagBean> getTags(){
		return tags;
	}


	public String[][] getInput() {
		ConditionResultModel model = (ConditionResultModel)ktable.getModel();
		return model.values;
		
	}

	public void setTags(List<TagBean> conditionTags) {
		tags = conditionTags;
		tagList.setInput(tags);
		
	}
}
