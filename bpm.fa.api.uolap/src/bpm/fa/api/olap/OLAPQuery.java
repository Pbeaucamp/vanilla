package bpm.fa.api.olap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.ktree.KQueryTreeNew;
import bpm.fa.api.olap.projection.Projection;
import bpm.fa.api.olap.query.WhereClauseException;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.copy.DeepCopy;

/**
 * OLAPQuery is the class for manipulating Queries on OLAP Cubes.
 * Serializable for multiple reasons.
 * Is NOT the public API we want to expose, too low level.
 */

public class OLAPQuery implements Serializable {
	//Actual members of the Query;
	private Vector<String> cols = new Vector<String>();
	private Vector<String> rows = new Vector<String>();
	private Vector<String> where = new Vector<String>();
	private Vector<OLAPCalcMeasure> static_mesures = new Vector<OLAPCalcMeasure>();

	//storage purpose
//	private ArrayList<Dimension> dimlist;
//	private ArrayList<Measure> meslist;
	//replaced by 
	private String col0;
	private String row0;
	private String row1;
	
	//Properties for generating MDX
	private boolean showEmpty = true; //do we show empty cells
	private boolean showProps = false; //do we show properties
	private boolean expandMode = true; //do we expand or replace
	
	//State keeping variables
	private boolean stateActive = false;
	private boolean modified = false;
	
	private OLAPQuery previous = null;
	private OLAPQuery next = null;
	
	private String cubename;
	
	//should always be used instead of viewcube's
	private HashMap<String, Integer> colsz = null;
	private HashMap<String, Integer> rowsz = null;
	
	private List<Topx> topx = new ArrayList<Topx>();
	
	private HashMap<String, String> personalNames = new HashMap<String, String>();
	
	private HashMap<String, Boolean> percentMeasures = new HashMap<String, Boolean>();
	
	private boolean showTotals = true;
	
	private String reportTitle;
	
	private List<Parameter> parameters = new ArrayList<Parameter>();
	
	private List<Dimension> dimensions;
	
	private Projection projection;
	
	private HashMap<String, String> sortElements;
	
	private boolean applyProjection;
	private transient OLAPCube cube;
	
	public OLAPQuery(String cubename, OLAPCube cube) {
		this.cube = cube;
		this.cubename = cubename;
	}
	
	public OLAPQuery(OLAPCube cube) {
		this.cube = cube;
	}
	
	public OLAPQuery(Collection<Dimension> dimensions, OLAPCube cube) {
		this.cube = cube;
		this.dimensions = new ArrayList<Dimension>(dimensions);
	}
	
	public OLAPQuery(String cubename, Collection<Dimension> dimensions, OLAPCube cube) {
		this.cube = cube;
		this.cubename = cubename;
		this.dimensions = new ArrayList<Dimension>(dimensions);
	}
	
	/**
	 * @author LCA
	 * init when load from a view
	 * @param view
	 */
	public void setup(RepositoryCubeView view){
		stateActive = false;
		
		cols.removeAllElements();
		rows.removeAllElements();
		
		where.removeAllElements();
		static_mesures.removeAllElements();
		
		topx = new ArrayList<Topx>();
		personalNames = new HashMap<String, String>();
		percentMeasures = new HashMap<String, Boolean>();
		
		sortElements = view.getSortElements();
		
		for (String s : view.getCols())
			cols.add(s);
		for (String s : view.getRows())
			rows.add(s);
		for (String s : view.getWheres())
			where.add(s);
		for(Topx t : view.getTopx()) {
			topx.add(t);
		}
		for(String uname : view.getPersonalNames().keySet()) {
			personalNames.put(uname, view.getPersonalNames().get(uname));
		}
		for(String percent : view.getPercentMeasures().keySet()) {
			percentMeasures.put(percent, view.getPercentMeasures().get(percent));
		}

		reportTitle = view.getReportTitle();
		showTotals = view.isShowTotals();
		stateActive = true;
		
		parameters = view.getParameters();
		for(Parameter param : parameters) {
			if(param.getUname() != null && !param.getUname().equalsIgnoreCase("")) {
				where.add(param.getUname());
			}
		}
		showEmpty = view.isShowEmpty();
		showProps = view.isShowProps();
		
		
	}
	
	/**
	 * Needed for first run, initializes the query.
	 * Cubename should not change.
	 * @param dl dimension list
	 * @param ml measure list
	 */
	public void setup(ArrayList<Dimension> dl, Measure ml) {
		stateActive = false;
		
		cols.removeAllElements();
		rows.removeAllElements();
		
		where.removeAllElements();
		static_mesures.removeAllElements();
		
		col0 = dl.get(1).getHierarchies().iterator().next().getDefaultMember().getUniqueName() ;//+ ".members";
		addcol(col0);
		
		row0 = dl.get(0).getHierarchies().iterator().next().getDefaultMember().getUniqueName() ;//+ ".members";
		addrow(row0);
		
//		col0 = dl.get(1).getHierarchies().iterator().next().getFirstLevel().getUniqueName() + ".members";
//		addcol(col0);
//		
//		row0 = dl.get(0).getHierarchies().iterator().next().getFirstLevel().getUniqueName() + ".members";
//		addrow(row0);
		
		row1 = ml.getUniqueName();
		addrow(row1);
		
		stateActive = true;
		
		dimensions = new ArrayList<Dimension>(dl);
	}
	
	
	/**
	 * Needed for first run, initializes the query.
	 * Cubename should not change.
	 * @param dl dimension list
	 * @param a measureGroup
	 */
	public void setup(ArrayList<Dimension> dl, MeasureGroup ml) {
		stateActive = false;
		
		cols.removeAllElements();
		rows.removeAllElements();
		
		where.removeAllElements();
		static_mesures.removeAllElements();

		col0 = dl.get(1).getHierarchies().iterator().next().getDefaultMember().getUniqueName();
		addcol(col0);
		
		if (!dl.get(0).getHierarchies().iterator().next().getFirstLevel().getUniqueName().equals(row1)){
			row0 = dl.get(0).getHierarchies().iterator().next().getDefaultMember().getUniqueName();
			addrow(row0);
		}
		else{
			row0 = dl.get(2).getHierarchies().iterator().next().getDefaultMember().getUniqueName() + ".members";
			addrow(row0);
		}
		
		if (ml.getUniqueName() == null || ml.getUniqueName().equals("")){
			row1 = ml.getMeasures().iterator().next().getUniqueName();
			addrow(row1);
		}
		else{
			if(ml.getUniqueName().contains("[")) {
				row1 = ml.getUniqueName();
			}
			else {
				row1 = ml.getMeasures().iterator().next().getUniqueName();
			}
			addrow(row1);
		}
		
		stateActive = true;
		
		dimensions = new ArrayList<Dimension>(dl);;
	}
	
	/**
	 * 
	 * @return the from, ie [cube_name]
	 */
	public String getCubeName() {
		return cubename;
	}
	
	public void setCubeName(String c) {
		this.cubename = c;
	}
	
	/**
	 * Restores the query to default state
	 * temporarily offing the state machine
	 */
	public void restore() {
		clearStates();
		stateActive = false;
		cols.removeAllElements();
		rows.removeAllElements();
		
		where.removeAllElements();
		static_mesures.removeAllElements();
		
		//XXX we should get default members...
		addcol(col0);
		
		addrow(row0);
		addrow(row1);
		
		topx.clear();
		percentMeasures.clear();
		personalNames.clear();
		showTotals = true;
		
		
		stateActive = true;
		////pushState();
	}
	
	public Vector<String> getCols() {
		return cols;
	}
	
	public Vector<String> getRows() {
		return rows;
	}
	
	public Vector<String> getWhere() {
		return where;
	}
	
	public Vector<OLAPCalcMeasure> getStaticMeasures() {
		return static_mesures;
	}
	
	public boolean getShowEmpty() {
		
		return showEmpty;
	}
	
	public String getMDX() throws Exception{
		List<String> dim = new ArrayList<String>();
		
		//XXX check measures positions
		Predicate measureCheck = new Predicate() {			
			@Override
			public boolean evaluate(Object arg0) {
				return ((String)arg0).startsWith("[Measures].");
			}
		};
		Collection resRow = CollectionUtils.select(rows, measureCheck);
		Collection resCol = CollectionUtils.select(cols, measureCheck);
		if(!resRow.isEmpty() && !resCol.isEmpty()) {
			throw new Exception("Cannot execute a query with measures in both rows and columns");
		}
		
		//XXX change rows and cols depending on wheres
		List<String> filteredRows = new ArrayList<String>(rows);
		List<String> filteredCols = new ArrayList<String>(cols);
		
		List<String> toKeep = new ArrayList<String>(where);
		
		for(String filter : where) {
		
			String[] buf = filter.split("].");
			String 	 hiera = buf[0];
			
			List<String> notAllowedCols = new ArrayList<String>();
			List<String> notAllowedRows = new ArrayList<String>();
			for(String s : filteredCols){
				if (s.startsWith(hiera)){
					notAllowedCols.add(filter);	
				}
			}
			if(!notAllowedCols.contains(filter)) {
				for(String s : filteredRows){
					if (s.startsWith(hiera)){
						notAllowedRows.add(filter);	
					}
				}
			}
			
			//well, well, well, now we're dealing with some shit
			List<String> dimElements = null;
			if(!notAllowedCols.isEmpty()) {
				dimElements = filteredCols;
			}
			else if(!notAllowedRows.isEmpty()) {
				dimElements = filteredRows;
			}
			
			if(dimElements != null) {
				String[] unameParts = filter.split("\\]\\.\\[");
				
				String actualMember = "[" + unameParts[0].replace("[", "").replace("]", "") + "].[" + unameParts[1].replace("[", "").replace("]", "") + "]";
				
				List<String> toRemove = new ArrayList<String>();
				for(String mem : dimElements) {
					if(mem.startsWith(actualMember)) {
						
						//Look if it's a submember of the actual one
						if(mem.length() > filter.length()) {
							if(mem.startsWith(filter)) {
								continue;
							}
						}
						
						boolean keepIt = false;
						for(String tk : toKeep) {
							if(mem.startsWith(tk)) {
								keepIt = true;
								break;
							}
						}
						
						if(!keepIt) {
							toRemove.add(mem);
						}
					}
				}
				dimElements.removeAll(toRemove);
				
				dimElements.add(filter);
				
				for(int i = 1 ; i < unameParts.length - 1; i++) {
					String uname = "[" + unameParts[0].replace("[", "").replace("]", "") + "]";
					for(int j = 1 ; j <= i ; j++) {
						uname += ".[" + unameParts[j].replace("[", "").replace("]", "") + "]";
					}
					dimElements.add(uname);
				}
				
				//Re-add other filters if neeeded
				for(String w : where) {
					if(w.startsWith(actualMember)) {
						dimElements.add(w);
					}
				}
			}
		}
		
		for(Dimension d : dimensions) {
			boolean find = false;
			for(String r : filteredRows) {
				if(r.contains(d.getUniqueName().replace("]",""))) {
					
					Hierarchy hiera = null;
					
					if (d.getHierarchies().size() == 1){
						hiera = d.getHierarchies().iterator().next();
					}
					else{
						for(Hierarchy h : d.getHierarchies()){
							if (r.startsWith(h.getUniqueName())){
								hiera = h;
								break;
							}
						}
					}
					
					
					if (hiera.getDefaultMember() != null){
//						isMondrian = hiera.getDefaultMember() instanceof MondrianMember;
						dim.add(hiera.getDefaultMember().getUniqueName().replace("&amp;", "&"));
					}
					else{
						for(OLAPMember m : hiera.getFirstLevel().getMembers()){
//							isMondrian = m instanceof MondrianMember;
							dim.add(m.getUniqueName().replace("&amp;", "&"));
						}
					}
					
					find = true;
					break;
				}
			}
			if(!find) {
				for(String r : filteredCols) {
					if(r.contains(d.getUniqueName().replace("]",""))) {
						Hierarchy hiera = null;
						
						if (d.getHierarchies().size() == 1){
							hiera = d.getHierarchies().iterator().next();
						}
						else{
							for(Hierarchy h : d.getHierarchies()){
								if (r.startsWith(h.getUniqueName())){
									hiera = h;
									break;
								}
							}
						}
						
						
						if (hiera.getDefaultMember() != null){
							dim.add(hiera.getDefaultMember().getUniqueName().replace("&amp;", "&"));
//							isMondrian = hiera.getDefaultMember() instanceof MondrianMember;
						}
						else{
							for(OLAPMember m : hiera.getFirstLevel().getMembers()){
//								isMondrian = m instanceof MondrianMember;
								dim.add(m.getUniqueName().replace("&amp;", "&"));
							}
						}
						break;
					}
				}
			}
		}
		
		List<String> percents = new ArrayList<String>();
		for(String p : percentMeasures.keySet()) {
			percents.add(p);
		}
		
//		KQueryTreeNew tree = new KQueryTreeNew(showEmpty, percents, dim);
		KQueryTreeNew tree = null;
		if(applyProjection && projection != null) {
			tree = new KQueryTreeNew(showEmpty, percents, dim, projection);
		}
		else {
			tree = new KQueryTreeNew(showEmpty, percents, dim);
		}
		
		for (int i = 0; i < static_mesures.size(); i++) {
			//System.out.println(">>" + static_mesures.get(i).getUniq());
			tree.addWithMember(static_mesures.get(i).getUniq());
		}
		
//		System.out.println(">> cols :");
		for (int i = 0; i < filteredCols.size(); i++) {
			Topx top = null;
			for(Topx t : this.topx) {
				String elment = t.getElementName() + ".children";
				if(elment.equals(filteredCols.get(i))) {
					top = new Topx(elment, t.getElementTarget(), t.getCount(), t.isOnRow());
				}
			}
			if(top != null) {
				tree.addCol(filteredCols.get(i), top);
			}
			else {
				String per = null;
				for(String percent : percentMeasures.keySet()) {
					if(filteredCols.get(i).equals(percent)) {
						per = percent + "." + percentMeasures.get(percent);
					}
				}
				tree.addCol(filteredCols.get(i), per);
			}
		}

		for (int i = 0; i < filteredRows.size(); i++) {
			Topx top = null;
			for(Topx t : this.topx) {
				String elment = t.getElementName() + ".children";
				if(elment.equals(filteredRows.get(i))) {
					top = new Topx(elment, t.getElementTarget(), t.getCount(), t.isOnRow());
				}
			}
			if(top != null) {
				tree.addRow(filteredRows.get(i) , top);
			}
			else {
				String per = null;
				for(String percent : percentMeasures.keySet()) {
					if(filteredRows.get(i).equals(percent)) {
						per = percent + "." + percentMeasures.get(percent);
					}
				}
				tree.addRow(filteredRows.get(i), per);
			}
		}
		
		for (int i = 0; i < where.size(); i++) {
			String w = where.get(i);
//			if(!filteredRows.contains(w) && !filteredCols.contains(w)) {
				
				//XXX check if it's the all member, if yes, add the childs
				boolean itsAAllMember = false;
				LOOK:for(Dimension d : dimensions) {
					if(w.contains(d.getUniqueName().replace("]",""))) {
						for(Hierarchy h: d.getHierarchies()) {
							if (w.startsWith(h.getUniqueName())){								
								if(h.getDefaultMember().getUniqueName().equals(w)) {
									
									OLAPMember mem = h.getDefaultMember();
									if(mem.getMembers() == null || mem.getMembers().isEmpty()) {
										cube.addChilds(mem, h);
									}
									
									for(Object m : mem.getMembers()) {
										tree.addWhere(((OLAPMember)m).getUniqueName());
									}
									
									itsAAllMember = true;
									break LOOK;
								}
								
							}
						}
					}
				}
				
				if(!itsAAllMember) {
					tree.addWhere(where.get(i));
				}
//			}
		}
		
		tree.setFrom("FROM " + cubename);
		
		return tree.getMDX();
	}
	
	/*
	 * swap cols/rows
	 */
	
	public void swapAxes() {
		////pushState();
		
		Vector<String> tmp;
		
//		/*
//		 * find the Measures
//		 */
//		List<String> measures = new ArrayList<String>();
//		boolean measuresInCol = false;
//		for(String s : cols){
//			if (s.startsWith("[Measures].")){
//				measuresInCol = true;
//				measures.add(s);
//			}
//		}
//		if (! measuresInCol){
//			for(String s : rows){
//				if (s.startsWith("[Measures].")){
//					measures.add(s);
//				}
//			}
//		}
//		
//		if (measuresInCol){
//			cols.removeAll(measures);
//		}
//		else{
//			rows.removeAll(measures);
//		}
//		
		
		tmp = cols;
		cols = rows;
		rows = tmp;
		
//		if (measuresInCol){
//			cols.addAll(measures);
//		}
//		else{
//			rows.addAll(measures);
//		}
	}
	
	/*
	 * Normal Adds
	 */
	
	public boolean addcol(String newcol) {
		return addNormal(cols, newcol);
		
	}
	
	public boolean addrow(String newrow) {
		return addNormal(rows, newrow);
	}
	
	private boolean addNormal(Vector<String> list, String tmp) {
		StringTokenizer st = new StringTokenizer("" + tmp, "]");
		String hiera;
		
		hiera = st.nextToken();
		
		int insertat = -1;
		for (int i=0; i < list.size(); i++) {
			if (list.get(i).compareToIgnoreCase(tmp) == 0) {
				//MessageDialog.openInformation(viewer.getTable().getShell(),Language.getAnInternationalizeString("error")/* "Error"*/, Language.getAnInternationalizeString("Item_already_query_")/*"Item already in query."*/);
				return false;
			}
			else if (list.get(i).startsWith(hiera)) {
				insertat = i;
			}
		}
		
		////pushState();
		
		if (insertat == -1) 
			list.add(tmp);
		else
			list.insertElementAt(tmp, insertat + 1);
		
		return true;
	}
	
	public void moveSwapAxis(String str, String target, boolean before, boolean isRow) throws Exception{
		StringTokenizer st = new StringTokenizer("" + str, "]");
		String hiera = st.nextToken() + "]";
		
		if (!isRow){
			if (cols.contains(str)) {
				
				
				Vector<String> tmp = getFromHieraColItems(hiera);
				
				stateActive = false;
				for (int i=0; i < tmp.size(); i++) {
					delcol(tmp.get(i), true);
					//addWithOrder(cols, tmp.get(i), target, before);
					//move(rows, tmp.get(i), target, before);
				}
				for (int i=0; i < tmp.size(); i++) {
					addrow(tmp.get(i));
				}
//				addrow()
				
				stateActive = true;
			}
			else{
				throw new Exception("");
			}
		}
		else{
			Vector<String> tmp = getFromHieraRowItems(hiera);
			stateActive = false;
			for (int i=0; i < tmp.size(); i++) {
				delrow(tmp.get(i), true);
				//addWithOrder(cols, tmp.get(i), target, before);
			//	move(cols, tmp.get(i), target, before);
			}
			for (int i=0; i < tmp.size(); i++) {
				addcol(tmp.get(i));
			}
			
			stateActive = true;
		}
	}
	
	public void moveRow(String str, String target, boolean before) {
		StringTokenizer st = new StringTokenizer("" + str, "]");
		String hiera = st.nextToken() + "]";
		if (cols.contains(str)) {
			
			
			Vector<String> tmp = getFromHieraColItems(hiera);
			
			stateActive = false;
			for (int i=0; i < tmp.size(); i++) {
				delcol(tmp.get(i), true);
				//addWithOrder(cols, tmp.get(i), target, before);
				//move(rows, tmp.get(i), target, before);
			}
			for (int i=0; i < tmp.size(); i++) {
				addrow(tmp.get(i));
			}
//			addrow()
			
			stateActive = true;
		}
		else if (isSwitch(cols, str)){
			Vector<String> tmp = getFromHieraColItems(hiera);
			stateActive = false;
			for (int i=0; i < tmp.size(); i++) {
				delcol(tmp.get(i), true);
				//addWithOrder(cols, tmp.get(i), target, before);
			//	move(cols, tmp.get(i), target, before);
			}
			for (int i=0; i < tmp.size(); i++) {
				addrow(tmp.get(i));
			}
			
			stateActive = true;
		}
		else 
			move(rows, str, target, before);
	}
	
	private boolean isSwitch(List<String> list, String str){
		for(String s : list){
			if (s.split("]")[0].equals(str.split("]")[0])){
				return true;
			}
		}
		
		return false;
	}
	
	public void moveCol(String str, String target, boolean before) {
		
		StringTokenizer st = new StringTokenizer("" + str, "]");
		String hiera = st.nextToken() + "]";
		if (rows.contains(str)) {
			
			
			Vector<String> tmp = getFromHieraRowItems(hiera);
			
			stateActive = false;
			for (int i=0; i < tmp.size(); i++) {
				delrow(tmp.get(i), true);
				//addWithOrder(cols, tmp.get(i), target, before);
			//	move(cols, tmp.get(i), target, before);
			}
			
			for (int i=0; i < tmp.size(); i++) {
				addcol(tmp.get(i));
			}
			
			stateActive = true;
		}
		else if (isSwitch(rows, str)){
			Vector<String> tmp = getFromHieraRowItems(hiera);
			stateActive = false;
			for (int i=0; i < tmp.size(); i++) {
				delrow(tmp.get(i), true);
				//addWithOrder(cols, tmp.get(i), target, before);
			//	move(cols, tmp.get(i), target, before);
			}
			for (int i=0; i < tmp.size(); i++) {
				addcol(tmp.get(i));
			}
			
			stateActive = true;
		}
		else 
			move(cols, str, target, before);
	}
	
	/**
	 * 
	 * @param list
	 * @param str
	 * @param target
	 * @param before
	 */
	private void move(Vector<String> list, String str, String target, boolean before) {
		
//		StringTokenizer st = new StringTokenizer("" + str, "]");
//		
//		String dim = st.nextToken();
//		
//		if (!dim.equalsIgnoreCase("[Measures")) {
//			dim += "].[" + st.nextToken() + "]";
//		}
//
//		int index = -1; //= list.indexOf(target);
//		
//		for (int i=0; i < list.size(); i++) {
//			System.out.println("comparing to = " + list.get(i));
//			if (list.get(i).equalsIgnoreCase(target)) {
//				index = i;
//				break;
//			}
//		}
//		
//		System.out.println("Inserting " + str + (before ? " before " : " after ") + "pos = " + index);
//		
//		list.remove(str);
////		
////		list.insertElementAt(str, before ? pos : pos + 1);
//		
//		
//		list.insertElementAt(str, index);
//
//		System.out.println("move finished");
		
		
		
		List<String> toMove = new ArrayList<String>();
		
		int index = -1;
		
		for(String s : list){
			if (s.split("]")[0].equals(str.split("]")[0])){
				toMove.add(s);
			}
		}
		
		for (int i=0; i < list.size(); i++) {
			Logger.getLogger(getClass()).trace("comparing to = " + list.get(i));
			if (list.get(i).split("]")[0].equals(target.split("]")[0])) {
				index = i;
				break;
			}
		}
		
		list.removeAll(toMove);
		list.addAll(index, toMove);
	}
	
	/*
	 * Ordered adds (reorganisation)
	 * 
	 * must not fail
	 */
	@Deprecated
	public void addcolOrder(String newcol, String target, boolean before) {
		
		
		String[] uname = newcol.split("\\]\\.\\[");
		String dimension = uname[0];
		
		for(String r : rows) {
			if(r.startsWith(dimension)) {
				return;
			}
		}
		
		if(target.contains(" %")) {
			target = target.replace(" %", "");
		}
		
		//if present on other side, delete and move all
		if (rows.contains(newcol)) {
			//split to get hiera
			StringTokenizer st = new StringTokenizer("" + newcol, "]");
			String hiera = st.nextToken() + "]";
			
			Vector<String> tmp = getFromHieraRowItems(hiera);
			
			stateActive = false;
			for (int i=0; i < tmp.size(); i++) {
				//stateActive = false;
				delrow(tmp.get(i), true);
				//stateActive = true;
				addWithOrder(cols, tmp.get(i), target, before);
			}
			stateActive = true;
			//delrow(hiera);
		}
		//simple case :)
		else 
			addWithOrder(cols, newcol, target, before);
	}
	@Deprecated
	public void addrowOrder(String newrow, String target, boolean before) {
		
		String[] uname = newrow.split("\\]\\.\\[");
		String dimension = uname[0];
		
		for(String r : cols) {
			if(r.startsWith(dimension)) {
				return;
			}
		}
		
		if(target.contains(" %")) {
			target = target.replace(" %", "");
		}
		
		if (cols.contains(newrow)) {
			//split to get hiera
			StringTokenizer st = new StringTokenizer("" + newrow, "]");
			String hiera = st.nextToken();
			
			Vector<String> tmp = getFromHieraColItems(hiera);
			
			stateActive = false;
			for (int i=0; i < tmp.size(); i++) {
				
				delcol(tmp.get(i), true);
				
				addWithOrder(rows, tmp.get(i), target, before);
			}
			stateActive = true;
			
			//delcol(hiera);
		}
		//simple case :)
		else 
			addWithOrder(rows, newrow, target, before);
	}
	@Deprecated
	private void addWithOrder(Vector<String> list, String tmp, String target, boolean before) {
		for (int i=0; i < list.size(); i++) {
			Logger.getLogger(getClass()).trace(list.get(i));
		}
		
		if (list.remove(tmp))
			Logger.getLogger(getClass()).trace("Successfully removed " + tmp);
		
		int insertat = -1;
		for (int i=0; i < list.size(); i++) {
			if (list.get(i).compareToIgnoreCase(tmp) == 0) {
				list.remove(i);

				for (int j=0; j < list.size(); j++) {
					if (list.get(j).compareToIgnoreCase(target) == 0) {
						insertat = j;
					}
				}
				break;
			}
			else 
			if (list.get(i).equalsIgnoreCase(target)) {
				insertat = i;
			}
		}
		////pushState();

		Logger.getLogger(getClass()).trace("inserting " + tmp + (before ? " before " : " after ") + target + " at pos = "+ insertat);
	
	///XXX
//	if (!tmp.endsWith(".members")){
//		tmp += ".members";
//	}
	
		list.insertElementAt(tmp, before ? insertat : insertat + 1);
	}
	
	/*
	 * Add where
	 */
	
	public int addWhere(String newwhere) throws WhereClauseException{
//		Logger.getLogger(getClass()).trace("Adding where = " + newwhere);
//		
//
//		String[] buf = newwhere.split("].");
//		String 	 hiera = buf[0];//.replace("[", "");
//		
//		// check if the newwhere does not come from an used hiera
//		List<String> usedMbs = new ArrayList<String>();
//		usedMbs.addAll(cols);
//		usedMbs.addAll(rows);
//		
//		for(String s : usedMbs){
//			if (s.startsWith(hiera)){
//				List<String> notAllowed = new ArrayList<String>();
//				notAllowed.add(newwhere);
//				throw new WhereClauseException("The " + newwhere + " member's Hierarchy is already used within the Query. It cannot be used within teh Where clause.", notAllowed);	
//			}
//		}
//		
//		
//		for (int i=0; i < where.size(); i++) {
//			if (where.get(i).equalsIgnoreCase(newwhere.replace(".children", ""))) {
//				Logger.getLogger(getClass()).trace("ignoring duplicate where");
//				return 0;
//			}
//		}
//		
//		stateActive = false;
//		
//		//just in case
//		newwhere = newwhere.replace(".children", "");
//		
//		String[] tmp = newwhere.split("]");
//		
//		delcol(tmp[0], false); //we delete all from same dimension
//		delrow(tmp[0], false);
//		
//		stateActive = true;
//		
//		where.add(newwhere);
//		
//		return 0;
		
		Logger.getLogger(getClass()).trace("Adding where = " + newwhere);
		
		for (int i=0; i < where.size(); i++) {
			if (where.get(i).equalsIgnoreCase(newwhere.replace(".children", ""))) {
				Logger.getLogger(getClass()).trace("ignoring duplicate where");
				return 0;
			}
		}
		
		newwhere = newwhere.replace(".children", "");
		
		where.add(newwhere);
		
		return 0;
	}
	
	/*
	 * Add static
	 */
	
	public void addStatic(String with, String name) {
		//static_mesures.add(with);
		
		String mes = "[Measures].[" + name + "]";
		
		////pushState();
		
		if (hasMeasures(cols)) {
			static_mesures.add(new OLAPCalcMeasure(with, name));
			cols.add(mes);
		}	

		else {
			static_mesures.add(new OLAPCalcMeasure(with, name));
			rows.add(mes);
		}
	}
	
	/**
	 * d
	 * @param s
	 * @return
	 */
	public void addCalcMeasure(OLAPCalcMeasure mes) {
		Logger.getLogger(getClass()).trace("added calc mesure " + mes.getUniq());
		static_mesures.add(mes);
		
		//String tmp = "[Measures].[" + mes.getName() + "]";
		
//		if (hasMeasures(cols)) {
//			cols.add(tmp);
//		}
//		else
//			rows.add(tmp);
	}
	
	public boolean delStatic(String s) {
		
		for (int i=0; i < static_mesures.size(); i++) {
			if (static_mesures.get(i).getName().equals(s)) {
				static_mesures.remove(i);
				Logger.getLogger(getClass()).trace("########### removed " + s);
				return true;
			}
		}
		
		return false;
	}
	
	private boolean hasMeasures(Vector<String> list) {
		for (int i=0; i < list.size(); i++) {
			if (list.get(i).startsWith("[Measures")) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * delete
	 * 
	 * should'nt fail
	 */
	
	public boolean delcol(String newcol, boolean uniq) {
		if (uniq)
			return cols.remove(newcol);
		else
			return delete(cols, newcol);
	}
	
	public boolean delrow(String newrow, boolean uniq) {
		if (uniq)
			return rows.remove(newrow);
		else
			return delete(rows, newrow);
	}
	
	private boolean delete(Vector<String> list, String tmp) {	
		if (list.size() == 1) {
			//MessageDialog.openInformation(viewer.getTable().getShell(), Language.getAnInternationalizeString("error")/*"Error"*/, Language.getAnInternationalizeString("Cant_delete_last_one_")/*"Can't delete the last one."*/);
			return false;
		}
//		
		//list.remove(tmp);
//		String[] buf = tmp.split("]");
//		System.out.println("got first = " + buf[0] + "]");
		
		Logger.getLogger(getClass()).trace("Deleting " + tmp);
		
		////pushState();
		
		Logger.getLogger(getClass()).trace(list.size());
		
		for (int i=0; i < list.size(); i++) {
			if (list.get(i).startsWith(tmp)) {
				list.remove(i);
				Logger.getLogger(getClass()).trace("nb = " + list.size());
				i--;
			}
		}
		
		return true;
	}
	
	public boolean delWhere(String todel) {
		
		boolean result = false;
		
		for (int i=0; i < where.size(); i++) {
			if (where.get(i).startsWith(todel)) {
				where.remove(i);
				result = true;
			}
		}
		
		//look if it was a dimension filter
		String[] buf = todel.split("].");
		String hiera = buf[0];
		
		//look if is col
		List<String> dimElements = new ArrayList<String>();
		boolean isCol = false;;
		for(String col : cols) {
			if(col.startsWith(hiera)) {
				dimElements.add(col);
				isCol = true;
			}
		}
		//look if is row
		if(dimElements.isEmpty()) {
			for(String row : rows) {
				if(row.startsWith(hiera)) {
					dimElements.add(row);
					isCol = false;
				}
			}
		}
		
		//if we found something
		if(!dimElements.isEmpty()) {
			String[] unameParts = todel.split("\\]\\.");
			for(String elem : dimElements) {
				String[] elemParts = elem.split("\\]\\.");
				if(elemParts.length > unameParts.length) {
					if(isCol) {
						cols.remove(elem);
					}
					else {
						rows.remove(elem);
					}
				}
			}
			if(isCol) {
				cols.remove(todel);
			}
			else {
				rows.remove(todel);
			}
			
			result = true;
		}
		
		return result;
	}
	
	/*
	 * replace/drills, it's the same
	 * 
	 * can fail
	 */
	
	public boolean drillOnCol(String oldlvl, String newlvl) {
		return replaceLvl(cols, oldlvl, newlvl);
	}
	
	public boolean drillOnRow(String oldlvl, String newlvl) {
		return replaceLvl(rows, oldlvl, newlvl);
	}
	
	private boolean replaceLvl(Vector<String> list, String oldlvl, String newlvl) {
		////pushState();
		
		for (int i=0; i < list.size(); i++) {
			if (list.get(i).startsWith(oldlvl)) {
				//should be equals()?
				list.set(i, newlvl);
				//list.get(i).set(newlvl);
				Logger.getLogger(getClass()).trace("Drilled from " + oldlvl + " to " + newlvl);
				return true;
			}
		}
		return false;
	}
	
	/*
	 * move up/down
	 * 
	 * fails silently
	 */
	
	public void upCol(String tmp) {
		moveUp(cols, tmp);
	}
	
	public void upRow(String tmp) {
		moveUp(rows, tmp);
	}
	
	public void downCol(String tmp) {
		moveDown(cols, tmp);
	}
	
	public void downRow(String tmp) {
		moveDown(rows, tmp);
	}
	
	private void moveUp(Vector<String> list, String tmp) {
		Vector<String> l = new Vector<String>();
		
		////pushState();
		
		for (int i=0; i < list.size(); i++) {
			if (i+1 < list.size() && list.get(i+1).startsWith(tmp)) {
				l.add(list.get(i+1));
				l.add(list.get(i));
				i++;
			}
			else {
				l.add(list.get(i));
			}
		}		
		list = l;
	}
	
	private void moveDown(Vector<String> list, String tmp) {
		Vector<String> l = new Vector<String>();
		
		////pushState();
		
		for (int i=0; i < list.size(); i++) {
			if (i+1 < list.size() && list.get(i).startsWith(tmp)) {
				l.add(list.get(i+1));
				l.add(list.get(i));
				i++;
			}
			else {
				l.add(list.get(i));
			}
		}		
		list = l;
	}
	
	public void pushState() {
		if (stateActive) {
			this.previous = (OLAPQuery) DeepCopy.copy(this);
			this.modified = true;
		}
	}
	
	private void clearStates() {
//		previous = null;
//		next = null;
	}
	
	//undo/redo leak
	public void setNext(OLAPQuery fq) {
		this.next = fq;
	}
	
	public OLAPQuery popBWState() {
		if (previous != null) 
			previous.setNext((OLAPQuery) DeepCopy.copy(this));
		
		return previous;
	}
	
	public OLAPQuery popFWState() {
		return next;
	}
	
	private Vector<String> getFromHieraColItems(String hiera) {
		Vector<String> tmp = new Vector<String>();
		
		for (int i=0; i < cols.size(); i++) {
			if (cols.get(i).startsWith(hiera))
				tmp.add(cols.get(i));
		}
		
		return tmp;
	}
	
	private Vector<String> getFromHieraRowItems(String hiera) {
		Vector<String> tmp = new Vector<String>();
		
		for (int i=0; i < rows.size(); i++) {
			if (rows.get(i).startsWith(hiera))
				tmp.add(rows.get(i));
		}
		
		return tmp;
	}
	
	public void setActive(boolean b) {
		stateActive = b;
	}
	
	public void setModified(boolean b) {
		modified = b;
	}
	public boolean isModified() {
		return modified;
	}
	
	public void setExpandMode(boolean b) {
		expandMode = b;
	}
	public boolean isExpandMode() {
		return expandMode;
	}
	
	public boolean hasHiera(String hiera) {
		for (int i=0; i < cols.size(); i++) {
			if (cols.get(i).startsWith(hiera)) {
				return true;
			}
		}
		for (int i=0; i < rows.size(); i++) {
			if (rows.get(i).startsWith(hiera)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean colHasHiera(String hiera) {
		for (int i=0; i < cols.size(); i++) {
			if (cols.get(i).startsWith(hiera)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean rowHasHiera(String hiera) {
		for (int i=0; i < rows.size(); i++) {
			if (rows.get(i).startsWith(hiera)) {
				return true;
			}
		}
		return false;
	}
	
	public void clearAxes() {
		cols = new Vector<String>();
		rows = new Vector<String>();
	}
	
	public boolean showProps() {
		return showProps;
	}
	public void setShowProps() {
		showProps = !showProps;
	}
	
	public void setFrom(String s) {
		cubename = s;
	}
	
	public HashMap<String, Integer> getColSZ() {
		return colsz;
	}
	
	public HashMap<String, Integer> getRowSZ() {
		return rowsz;
	}
	
//	public void addColSz(CellSize c) {
//		if (colsz == null)
//			colsz = new HashMap<String, Integer>();
//		
//		colsz.put(c.getKey(), c.getValue());
//	}
//	
//	public void addRowSz(CellSize c) {
//		if (rowsz == null)
//			rowsz = new HashMap<String, Integer>();
//		
//		rowsz.put(c.getKey(), c.getValue());
//	}
	
	public boolean removeCol(String newcol) {
		return remove(cols, newcol);
	}
	
	public boolean removeRow(String newrow) {
		return remove(rows, newrow);
	}
	
	private boolean remove(Vector<String> list, String tmp) {	
		if (list.size() == 1) {
			return false;
		}
		
		////pushState();
		
		Logger.getLogger(getClass()).trace("Delete " + tmp);
		
		
		
		for (int i=0; i < list.size(); i++) {
			if (list.get(i).equals(tmp + ".children") /*&& !list.get(i).equalsIgnoreCase(tmp)*/) {
				list.remove(i);
				i--;
			}
		}
		
		return true;
	}

	//XX LCA
	public void setShowEmpty(boolean showEmpty2) {
		showEmpty = showEmpty2;
		
	}

	
	
	/**
	 * if activate
	 * -Remove the measure with teh given Name from the query
	 * -create a static Measure named "Last " + measureName with the given formula
	 * - add this new measure in the query to replace the removed one
	 * else
	 * - remove the measure "Last " + measureName from the query
	 * - remove teh static measure "Last + measureName
	 * - add the given measure to the query to replace the Old One
	 * 
	 * @param activate : true to replace measure MeasureName by the Given Formula,
	 * false to replace Last MeasureName by the Original one
	 * @param measureName : the measure concerned
	 * @param calcFormula : the formula starting with MEMBER 
	 */
	public void measureAsLast(boolean activate, String measureName, String calcFormula){
		if (activate){
			if (hasMeasures(cols)) {
				delcol("[Measures].[" + measureName + "]", true);
			}	

			else {
				delrow("[Measures].[" + measureName + "]", true);
			}
			
			addStatic(" MEMBER [Measures].[Last " + measureName + "] AS " + calcFormula, "Last " + measureName);

		}
		else{
			delStatic("Last " + measureName);
			if (hasMeasures(cols)) {
				delcol("[Measures].[Last " + measureName + "]", true);
				addcol("[Measures].[" + measureName + "]");
			}	

			else {
				delrow("[Measures].[Last " + measureName + "]", true);
				addrow("[Measures].[" + measureName + "]");
			}
			
			
		}
	}

	public void removeTopx(Topx topx) {
		Topx rm = null;
		for(Topx t : this.topx) {
			if(t.getElementName().equals(topx.getElementName())) {
				rm = t;
				break;
			}
		}
		this.topx.remove(rm);
	}
	public void addTopx(Topx topx) {
		this.topx.add(topx);
	}

	public List<Topx> getTopx() {
		return topx;
	}

	public void setPersonalNames(HashMap<String, String> personalNames) {
		this.personalNames = personalNames;
	}

	public HashMap<String, String> getPersonalNames() {
		return personalNames;
	}

	public void setPercentMeasures(HashMap<String, Boolean> percentMeasures) {
		this.percentMeasures = percentMeasures;
	}

	public HashMap<String, Boolean> getPercentMeasures() {
		return percentMeasures;
	}

	public void setShowTotals(boolean showTotals) {
		this.showTotals = showTotals;
	}

	public boolean isShowTotals() {
		return showTotals;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	
	/**
	 * check if the member with the given uniqueName can be added on rows or Column
	 * @param uniqueName
	 * @return
	 */
	public boolean canMemberBeAdded(String uniqueName) {
		List<String> l = new ArrayList<String>();
		l.addAll(rows);
		l.addAll(cols);
		return canMemberBeAdded(l, uniqueName);
	}
	private  boolean canMemberBeAdded(List<String> l, String uniqueName) {

		String[] unameParts = uniqueName.split("\\]\\.");
		for (String s : l){
			
			String[] sParts = s.split("\\]\\.");
			if (sParts.length == 0){
				return true;
			}
			int matchingPartNumber = 0;
			boolean isChildren = sParts[sParts.length - 1].equals(".children");
			for(int i = 0; i < sParts.length; i++){
				
				if (i < unameParts.length){
					if (sParts[i].equals(unameParts[i])){
						matchingPartNumber ++;
					}
				}
			}
			
			
			if (matchingPartNumber == unameParts.length){
				return false;
			}
			else if (matchingPartNumber == (unameParts.length - 1) && isChildren){
				return false;
			}
			
		}
		return true;
	}
	
	public boolean canAddToRow(String uname){
		List<String> l = new ArrayList<String>();
		l.addAll(rows);
		return canMemberBeAdded(l, uname);
	}
	
	public boolean canAddToCol(String uname){
		List<String> l = new ArrayList<String>();
		l.addAll(cols);
		return canMemberBeAdded(l, uname);
	}
	
	/**
	 * 
	 * @param uname
	 * @return the list of members present in the where clause coming from the same dimension 
	 */
	public List<String> getWheresDimensionUname(String uname){
		List<String> w = new ArrayList<String>();
		
		String[] _p = uname.split("\\]\\.");
		String dimName = _p[0];
		
		for(String s : where){
			String[] pt = s.split("\\]\\.");
			if (dimName.equals(pt[0])){
				w.add(s);
			}
		}
		
		return w;
	}

	/**
	 * 
	 * @param s
	 * @return if the given uname's Dimension is used in row or column in the query
	 */
	public boolean isUsingOutsideWhere(String uname) {
		String[] _p = uname.split("\\]\\.");
		String dimName = _p[0];
		
		
		List<String> l = new ArrayList<String>();
		l.addAll(rows);
		l.addAll(cols);
		
		
		for(String s : l){
			String[] pt = s.split("\\]\\.");
			if (dimName.equals(pt[0])){
				return true;
			}
		}
		

		
		return false;
	}

	public boolean canRemove(Item item) {
		if (item instanceof ItemElement){
			ItemElement e = (ItemElement)item;
			List<String> l = new ArrayList<String>();
//			if (e.getDataMember().getUniqueName().startsWith("[Measures]")){
//				//check if their is another Measure
//				
//				l.addAll(rows);
//				for(String s : l){
//					if (s.startsWith("[Measures]") && ! s.equals(e.getDataMember().getUniqueName())){
//						return true;
//					}
//				}
//				return false;
//			}
//			else{
				//check that there is other another dimension somewhere
				if ((cols.size() <= 1 && e.isCol()) || (rows.size()<=1 && e.isRow())){
					return false;
				}
//			}

			if(e.isCol()) {
				if(cols.contains(e.getDataMember().getUniqueName())) {
					return true;
				}
				return false;
			}
			else {
				if(rows.contains(e.getDataMember().getUniqueName())) {
					return true;
				}
				return false;
			}
		}
		
		
		return true;
	}

	public Projection getProjection() {
		return projection;
	}

	public void setProjection(Projection projection) {
		this.projection = projection;
	}

	public boolean isApplyProjection() {
		return applyProjection;
	}

	public void setApplyProjection(boolean applyProjection) {
		this.applyProjection = applyProjection;
	}

	public HashMap<String, String> getSortElements() {
		return sortElements;
	}

	public void setSortElements(HashMap<String, String> sortElements) {
		this.sortElements = sortElements;
	}
	
	
}
