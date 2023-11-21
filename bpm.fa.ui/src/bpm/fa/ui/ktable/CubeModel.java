package bpm.fa.ui.ktable;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.OLAPResultFilter;
import bpm.fa.ui.Messages;
import bpm.fa.ui.composite.model.FilterManager;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableDefaultModel;
import de.kupzog.ktable.editors.KTableCellEditorCombo;
import de.kupzog.ktable.editors.KTableCellEditorComboText;
import de.kupzog.ktable.editors.KTableCellEditorText;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;

public class CubeModel extends KTableDefaultModel{

    private List<List<CubeExplorerContent>> content = new ArrayList<List<CubeExplorerContent>>();
//    private List<List<CubeExplorerContent>> displayedContent = new ArrayList<List<CubeExplorerContent>>() ;
    private OLAPCube faCube;
    private OLAPResult res = null;
    
    
//    private boolean filtersEnabled = false;
    
    private FilterManager filterManager;
   
	
    private final FixedCellRenderer m_fixedRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT  |
            TextCellRenderer.INDICATION_FOCUS_ROW);
    

    private final TextCellRenderer m_textRenderer =  new TextCellRenderer(TextCellRenderer.INDICATION_FOCUS_ROW);

    
	public CubeModel(OLAPCube faCube){
		initialize();
		this.faCube = faCube;
		this.filterManager = new FilterManager(faCube);
		try{
			res = faCube.doQuery();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		

		ArrayList<ArrayList<Item>> rawContent = res.getRaw();
		content = new ArrayList<List<CubeExplorerContent>>();
		int rowC = 0;
		for(ArrayList<Item> row : rawContent){
			ArrayList<CubeExplorerContent> tmp = new ArrayList<CubeExplorerContent>();
			int colC = 0;
			for(Item i : row){
				tmp.add(new CubeExplorerContent(i, new java.awt.Point(colC, rowC)));
				colC ++;
			}
			content.add(tmp);
			rowC++;
		}
	}
	
	public OLAPCube getOLAPCube(){
		return faCube;
	}
	
	
	
	
	public void enableFilters(Hierarchy h, boolean enabled){
		
		filterManager.enable(h, enabled);
		applyFilters();
		
	}
	
	public void performQuery(){
		try {
			res = faCube.doQuery();
//			res.dump();
			applyFilters();
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.CubeModel_0, Messages.CubeModel_1 + ex.getMessage());
		}
		
	}
	
	public void performQuery(String mdx){
		try {
			res = faCube.doQuery(mdx);
//			res.dump();
			applyFilters();
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.CubeModel_2, Messages.CubeModel_3 + ex.getMessage());
		}
		
	}
	
	private void rebuildModel(List<List<CubeExplorerContent>> content, ArrayList<ArrayList<Item>> rawContent){

		int rowC = 0;
		
		for(ArrayList<Item> row : rawContent){
			ArrayList<CubeExplorerContent> tmp = new ArrayList<CubeExplorerContent>();
			int colC = 0;
			for(Item i : row){
				tmp.add(new CubeExplorerContent(i, new java.awt.Point(colC, rowC)));
				colC ++;
			}
			content.add(tmp);
			rowC++;
		}
	}
	
	@Override
	public Object doGetContentAt(int col, int row) {
		List<List<CubeExplorerContent>> content = null;
//		if (filtersEnabled){
//			content = displayedContent;
//		}
//		else{
			content = this.content;
//		}
		
		
		if(content.size()<=row || content.get(row).size()<=col){
			return ""; //$NON-NLS-1$
		}
		else{
			if(content.get(row).get(col).getLabel().equals("")) return ""; //$NON-NLS-1$ //$NON-NLS-2$
			else return content.get(row).get(col).getLabel();
		}
	}
	
	@Override
	public KTableCellEditor doGetCellEditor(int col, int row) {
    	if (col<getFixedColumnCount() || row<getFixedRowCount())
    		return null;
        if (col % 3 == 1) 
        {
            KTableCellEditorCombo e = new KTableCellEditorCombo();
            e.setItems(new String[] { "First text", "Second text", //$NON-NLS-1$ //$NON-NLS-2$
                            "third text" }); //$NON-NLS-1$
            return e;
        }
        else if (col % 3 == 2) 
        {
                KTableCellEditorComboText e = new KTableCellEditorComboText();
                e.setItems(new String[] { "You choose", "or type", //$NON-NLS-1$ //$NON-NLS-2$
                                "a new content." }); //$NON-NLS-1$
                return e;
        }
        else
        {
            return new KTableCellEditorText();
        }
	}

	@Override
	public KTableCellRenderer doGetCellRenderer(int col, int row) {
		if (isFixedCell(col, row)){
			return m_fixedRenderer;
		}
        return m_textRenderer;
	}

	@Override
	public int doGetColumnCount() {
		
		List<List<CubeExplorerContent>> content = null;
//		if (filtersEnabled){
//			content = displayedContent;
//		}
//		else{
			content = this.content;
//		}
		
		int maxCol = 0;
		for(List<CubeExplorerContent> lstC : content){
			if(maxCol < lstC.size()){
				maxCol = lstC.size();
			}
			break;
		}
		return maxCol;
	}

	@Override
	public int doGetRowCount() {
		List<List<CubeExplorerContent>> content = null;
//		if (filtersEnabled){
//			content = displayedContent;
//		}
//		else{
			content = this.content;
//		}
		
		
		return content.size();
	}
	
	@Override
	public void doSetContentAt(int col, int row, Object value) {
	}

	@Override
	public int getInitialColumnWidth(int column) {
		return 110;
	}

	@Override
	public int getInitialRowHeight(int row) {
		if (row==0) return 22;
    	return 18;
	}

	@Override
	public int getFixedHeaderColumnCount() {
		
		
		return res.getYFixed();
	}

	@Override
	public int getFixedHeaderRowCount() {
		return res.getXFixed();
	}

	@Override
	public int getFixedSelectableColumnCount() {
		return 0;
	}

	@Override
	public int getFixedSelectableRowCount() {
		return 0;
	}

	@Override
	public int getRowHeightMinimum() {
		return 18;
	}

	@Override
	public boolean isColumnResizable(int col) {
		return true;
	}

	@Override
	public boolean isRowResizable(int row) {
		return true;
	}
	
	@Override
	public Point doBelongsToCell(int col, int row){
		List<List<CubeExplorerContent>> content = null;
//		if (filtersEnabled){
//			content = displayedContent;
//		}
//		else{
			content = this.content;
//		}
		
		//We span if two close elements are both null in the same column in the
		//fixed columns.
		if(row >=1 && col<getFixedHeaderColumnCount()){
			if(content.get(row).get(col).getItem() instanceof ItemNull 
					&& content.get(row-1).get(col).getItem() instanceof ItemNull
					|| content.get(row).get(col).getItem() instanceof ItemElement
					&& content.get(row).get(col).getLabel().equals("") //$NON-NLS-1$
					&& content.get(row-1).get(col).getItem() instanceof ItemElement
					&& content.get(row-1).get(col).getLabel().equals("")){ //$NON-NLS-1$
				return new Point(col, row-1);
			}
		}
			
		//We span if two close elements are both null in the same row in the
		//fixed rows.
		if(col>=getFixedHeaderColumnCount()+1 && row<getFixedHeaderRowCount()){
			if(content.get(row).get(col).getItem() instanceof ItemNull 
					&& content.get(row).get(col-1).getItem() instanceof ItemNull
					|| content.get(row).get(col).getItem() instanceof ItemElement
					&& content.get(row).get(col).getLabel().equals("") //$NON-NLS-1$
					&& content.get(row).get(col-1).getItem() instanceof ItemElement
					&& content.get(row).get(col-1).getLabel().equals("")){ //$NON-NLS-1$
				return new Point(col-1, row);
			}
		}
		
//		// compute rowWrapping
//		int rowWrap = 0;
//		for(int i = row; i >0 ; i--){
//			if (content.get(row).get(i).getItem().getLabel().equals(content.get(row).get(col).getItem().getLabel())){
//				rowWrap++;
//			}
//			
//			
//		}
//		int colWrap = 0;
//		for(int i = col; i >0 ; i--){
//			if (content.get(i).get(col).getItem().getLabel().equals(content.get(row).get(col).getItem().getLabel())){
//				colWrap++;
//			}
//		}
		
        
		//For the value
        return new Point(col, row);
	}
	
	public List<List<CubeExplorerContent>> getContent(){
		List<List<CubeExplorerContent>> content = null;
//		if (filtersEnabled){
//			content = displayedContent;
//		}
//		else{
			content = this.content;
//		}
		
		return content;
	}
	
	public CubeExplorerContent getModelItemAt(Point p ){
		List<List<CubeExplorerContent>> content = null;
//		if (filtersEnabled){
//			content = displayedContent;
//		}
//		else{
			content = this.content;
//		}
		
		try{
			return content.get(p.y).get( p.x);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	
	
	public void refreshResult() {
		try {
			res = faCube.getLastResult();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		//TODO: refresh model content by parsing OLAPResult
		
//		content.clear();
//		rebuildModel(content, res.getRaw());
		applyFilters();
	}

	public FilterManager getFilterManager() {
		return filterManager;
	}

	public void applyFilters() {
		if (filterManager.isEnabled()){
			OLAPResultFilter f = new OLAPResultFilter();

			List<OLAPMember> m = filterManager.getFiltered();
//			res = getOLAPCube().getLastResult();
			
			this.res = f.filter(faCube.getLastResult(), m); 
			content.clear();
			rebuildModel(content, res.getRaw());
		}
		else{
			this.res = faCube.getLastResult(); 
			content.clear();
			rebuildModel(content, res.getRaw());
		}
		
	}

	
	public OLAPResult getOLAPResult(){
		return res;
	}
	
}
