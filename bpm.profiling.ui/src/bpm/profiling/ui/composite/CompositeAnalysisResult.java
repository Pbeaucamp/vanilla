package bpm.profiling.ui.composite;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.jfree.data.category.DefaultCategoryDataset;

import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.database.bean.AnalysisResultBean;
import bpm.profiling.database.bean.TagBean;
import bpm.profiling.runtime.core.AnalysisExecutor;
import bpm.profiling.ui.Activator;
import bpm.profiling.ui.dialogs.DialogCreateTag;
import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableDefaultModel;
import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.renderers.DefaultCellRenderer;



public class CompositeAnalysisResult extends Composite {

	private KTable ktable;
	
	private CompositeBar3DChart barChart;
	private Composite chartContainer; 
	
	private HashMap<AnalysisContentBean, AnalysisResultBean> input;
	
	private List<TagBean> tags = new ArrayList<TagBean>();
	private CompositeListTag tagList;
	
	
	public CompositeAnalysisResult(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		buildContent();
	}
	
	
	private void buildContent(){
		ktable = new KTable(this, SWT.FULL_SELECTION | SWTX.AUTO_SCROLL);
		ktable.setLayoutData(new GridData(GridData.FILL_BOTH));

		chartContainer = new Composite(this, SWT.NONE);
		chartContainer.setLayoutData(new GridData(GridData.BEGINNING, GridData.END, false, false));
		chartContainer.setLayout(new GridLayout(2, false));
		
		createMenu();
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

            	
            	ActionCreateTag ac = new ActionCreateTag("Add Tag on selected row", i[0]);
            	menuMgr.add(ac);
            	menuMgr.update();
            	
            	
            }
        });   
        ktable.setMenu(menuMgr.createContextMenu(ktable));
        

	}
	
	
	private class ActionCreateTag extends Action{
		int fieldIndice;
		
		public ActionCreateTag(String name, int fieldIndice){
			super(name);
			this.fieldIndice = fieldIndice;
		}
		
		public void run(){
			DialogCreateTag dial = new DialogCreateTag(getShell());
			
			if (dial.open() == DialogCreateTag.OK){
				TagBean tag = dial.getTag();
				tag.setFieldIndice(fieldIndice);
				tags.add(tag);
				tagList.setInput(tags);
				
			}
		}
	}
	
	public void setInput(HashMap<AnalysisContentBean, AnalysisResultBean> values){
		input = values;
		
		KTableModel model = new ResultModel(input);
		ktable.setModel(model);
		/*
		 * build chart DataSet
		 */
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		
		
		for(AnalysisContentBean c : input.keySet()){
			dataSet.addValue(input.get(c).getBlankCount() == null ? 0 : input.get(c).getBlankCount(), "Blank Count", c.getColumnName());
			dataSet.addValue(input.get(c).getDistinctCount() == null ? 0 : input.get(c).getDistinctCount(), "Distinct Count", c.getColumnName());
			dataSet.addValue(input.get(c).getZeroCount() == null ? 0 : input.get(c).getZeroCount(), "Zero Count", c.getColumnName());
			dataSet.addValue(input.get(c).getNullCount() == null ? 0 : input.get(c).getNullCount(), "Null Count", c.getColumnName());
		}
		
		
		
		
		/*
		 * draw chart
		 */
		if (barChart != null  && !barChart.isDisposed()){
			barChart.dispose();
		}
		
		if (tagList != null  && !tagList.isDisposed()){
			tagList.dispose();
		}
		
		
		barChart = new CompositeBar3DChart(chartContainer, dataSet, "General Counters");
		barChart.setLayoutData(new GridData(GridData.BEGINNING, GridData.END, false, false));
		
		
		/*
		 * 
		 */
		tagList = new CompositeListTag(chartContainer, SWT.NONE);
		tagList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		
		chartContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		
		chartContainer.layout();
		this.redraw();
		barChart.drawChart(new Rectangle(0, 0, 500, 350));

	}
	
	public void setInput(Date currentDate, AnalysisInfoBean infos) throws Exception{
		input = AnalysisExecutor.executeFullAnalysis(currentDate, Activator.getDefault().getConnection(infos.getConnectionId()), infos);
		
		setInput(input);		
		
		
	}
	
	public  HashMap<AnalysisContentBean, AnalysisResultBean> getInput(){
		return input;
	}
	
	public String[][] getInputAsArray(){
		return ((ResultModel)ktable.getModel()).values;
	}
	
	
	public class ResultModel extends KTableDefaultModel{
		private String[][] values;
		
		
		private DefaultCellRenderer renderer = new DefaultCellRenderer(SWT.NONE);
		public ResultModel(HashMap<AnalysisContentBean, AnalysisResultBean> values ){
			this.values = new String[values.size() + 1][14];
			
			int rowCount = 1;
			
			this.values[0] = new String[14];
			this.values[0][0] = "Field Name";
			this.values[0][1] = "Data Type";
			this.values[0][2] = "Distinct Count";
			this.values[0][3] = "Highest Value";
			this.values[0][4] = "Highest Value Count";
			this.values[0][5] = "Lowest Value";
			this.values[0][6] = "Lowest Value Count";
			this.values[0][7] = "Average Value";
			this.values[0][8] = "Blank Count";
			this.values[0][9] = "Blank Percent";
			this.values[0][10] = "Null count";
			this.values[0][11] = "Null Percent";
			this.values[0][12] = "Zero count";
			this.values[0][13] = "Zero Percent";
			
			
			for(AnalysisContentBean key : values.keySet()){
				this.values[rowCount] = new String[14];
				this.values[rowCount][0] = key.getColumnName();
				this.values[rowCount][1] = values.get(key).getDataType();
				this.values[rowCount][3] = values.get(key).getHightValue();
				this.values[rowCount][5] = values.get(key).getLowValue();
				try{
					this.values[rowCount][7] = NumberFormat.getNumberInstance().format(values.get(key).getAvgValue());
				}catch(Exception e){
					this.values[rowCount][7] = values.get(key).getAvgValue() + "";
				}
				
				this.values[rowCount][8] = values.get(key).getBlankCount() + "";
				try{
					this.values[rowCount][9] = NumberFormat.getPercentInstance().format(values.get(key).getBlankPercent());
				}catch(Exception e){
					this.values[rowCount][9] =values.get(key).getBlankPercent() + "";
				}
				
				this.values[rowCount][2] = values.get(key).getDistinctCount()+ "";
				this.values[rowCount][4] = values.get(key).getHightValueCount()+ "";
				this.values[rowCount][6] = values.get(key).getLowValueCount()+ "";
				
				
				try{
					this.values[rowCount][11] = NumberFormat.getPercentInstance().format(values.get(key).getNullPercent());
				}catch(Exception e){
					this.values[rowCount][11] =values.get(key).getNullPercent() + "";
				}
				
				this.values[rowCount][10] = values.get(key).getNullCount()+ "";
				this.values[rowCount][12] = values.get(key).getZeroCount()+ "";
				
				
				try{
					this.values[rowCount][13] = NumberFormat.getPercentInstance().format(values.get(key).getZeroPercent());
				}catch(Exception e){
					this.values[rowCount][13] =values.get(key).getZeroPercent() + "";
				}
				
				rowCount++;
				
			}
			
			for(int i = 0; i < this.values.length; i++){
				for(int j = 0; j < this.values[i].length; j++){
					if (this.values[i][j] ==  null){
						this.values[i][j] = new String("");
					}
				}
			}
			System.out.println();
			
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
			return 14;
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


	public List<TagBean> getTags() {
		return tags;
	}


	public void setTags(List<TagBean> analysisTags) {
		tags = analysisTags;
		tagList.setInput(tags);
		
	}
	
	
	
}
