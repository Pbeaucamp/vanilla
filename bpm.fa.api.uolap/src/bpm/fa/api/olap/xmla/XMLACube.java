package bpm.fa.api.olap.xmla;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;

import org.fasd.olap.Drill;

import bpm.fa.api.item.ItemDependent;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPQuery;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.Topx;
import bpm.fa.api.olap.projection.Projection;
import bpm.fa.api.olap.query.WhereClauseException;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.log.Log;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Member;

/**
 * Implementation to connect and interact with XMLA-based cubes.
 *
 * @author manu
 * 
 * TODO : MS AS != Mondrian != spec
 */
public class XMLACube extends Observable implements OLAPCube {
	private XMLAStructure cube;
	private OLAPQuery mdx, old;
	private boolean showEmpty = true;
	
	private OLAPResult last 	= null; 	// last result
	private boolean showProps 	= false; 	// do we show props
	
	public XMLACube(XMLAStructure cube) {
		this.cube = cube;
		
		mdx = new OLAPQuery("[" + cube.getCubeName() + "]", this);
		
		//We only have one group of measures
		
		mdx.setup(cube.getDimensions(), cube.getMeasures().get(0));
		mdx.setActive(true);
	}

	public void add(ItemElement e) {
		mdx.pushState();
		// in case we the itemElement is a fake created on a Hierarchy
		for(Dimension d : cube.getDimensions()){
			if (!e.getDataMember().getUniqueName().startsWith(d.getUniqueName())){
				continue;
			}
			for(Hierarchy h : d.getHierarchies()){
				if (h.getUniqueName().equals(e.getDataMember().getUniqueName())){
					if (h.getDefaultMember() != null){
						if(e.isCol())
							mdx.addcol(h.getDefaultMember().getUniqueName());
						else{
							mdx.addrow(h.getDefaultMember().getUniqueName());
						}
					}
					else{
						for(OLAPMember m : h.getFirstLevel().getMembers()){
							if(e.isCol())
								mdx.addcol(m.getUniqueName());
							else{
								mdx.addrow(m.getUniqueName());
							}
						}
					}
					break;
				}
			}
		}
		
		
		if(e.isCol())
			mdx.addcol(e.getDataMember().getUniqueName());
		else{
			mdx.addrow(e.getDataMember().getUniqueName());
		}
	}

	public void addAfter(ItemElement e, ItemElement after) {
		mdx.pushState();
		if (after.isCol()){
			mdx.addcolOrder(e.getDataMember().getUniqueName(), after.getDataMember().getUniqueName(), false);
			
		}
		else{
			mdx.addrowOrder(e.getDataMember().getUniqueName(), after.getDataMember().getUniqueName(), false);
		}
	}

	public void addBefore(ItemElement e, ItemElement before) {
		mdx.pushState();
		if (before.isCol()){
			mdx.addcolOrder(e.getDataMember().getUniqueName(), before.getDataMember().getUniqueName(), true);
			
		}
		else{
			mdx.addrowOrder(e.getDataMember().getUniqueName(), before.getDataMember().getUniqueName(), true);
		}
	}

	public void addWhere(ItemElement e) {
		mdx.pushState();
		try {
			mdx.addWhere(e.getDataMember().getUniqueName());
		} catch (WhereClauseException e1) {
			
			e1.printStackTrace();
			mdx.popBWState();
		}
	}

	public OLAPResult doQuery() throws Exception {
		String buf = mdx.getMDX();
		
		cube.setShowTotals(mdx.isShowTotals());

		OLAPResult r = cube.executeQuery(this, buf, showProps, null);
		if(r != null){
			last = r;
		}
		else{
			undo();
		}
		
		return last;
	}
	
	public OLAPResult doQuery(String mdxQuery) throws Exception {
		last = cube.executeQuery(this, mdxQuery, showProps, null);
		
		return last;
	}

	public boolean drilldown(ItemElement e) {
		OLAPMember mb =  e.getDataMember();
		
		boolean result = false;
		
		mdx.pushState();
	
		//XXX
//		if (!mb.isSynchronized()){
//			Hierarchy hiera = null;
//			
//			for(Dimension d : getDimensions()){
//				for(Hierarchy h : d.getHierarchies()){
//					if (h.getUniqueName().equals(mb.getHierarchy())){
//						hiera = h;
//						break;
//					}
//				}
//				if (hiera!= null)
//					break;
//			}
//			
//			result = addChilds(mb, hiera);
//		}
		
		Log.info("Drilling down on : " + mb.getUniqueName());
		
		
		
		//XXX ths part should be writed cleanely because it sucks
		if (mb instanceof XMLAMember){
			if (isDrillable((XMLAMember)mb)){
				if (e.isCol()) {
					mdx.addcol(mb.getUniqueName() + ".children");
				}
				else{
					mdx.addrow(mb.getUniqueName() + ".children");
				}
			}
		}
		return false;
	}

	private boolean isDrillable(XMLAMember mb) {
		if (mb.getHieraName() == null){
			return true;
		}
		
		for(Dimension d : cube.getDimensions()){
			for(Hierarchy h : d.getHierarchies()){
				if (h.getUniqueName().equals(mb.getHieraName())){
					return mb.getLevelDepth() <= h.getLevel().size() ;
				}
			}
		}
		
		return false;
//		return true;
	}
	
	public OLAPResult drillthrough(ItemValue v, int level) throws Exception{
		if (v.getDependent().size() != 3){
			throw new Exception("Dependant Items havent been defined");
		}
		
		
		String s = 	"drillthrough select {" + v.getDependent().get(0).getUname() + "} ON COLUMNS,"+
				" {" + v.getDependent().get(2).getUname() + "} ON ROWS " + 
				"from [Sales] where (" + v.getDependent().get(1).getUname() + ")" ;
		
		return cube.executeDrillThrough(s);
	}

	public void drillup(ItemElement e) {
		mdx.pushState();
		
		XMLAMember mb = (XMLAMember) e.getDataMember();
		
		Log.info("Drilling up on : " + mb.getUniqueName());

		String uname = mb.getUniqueName();
		Hierarchy hiera = null;
		Level itemLevel = null;
		if (mb.getDimension() != null && mb.getDimension().getUniqueName() != null){
			
			for(Hierarchy h : mb.getDimension().getHierarchies()){
				if (mb.getUniqueName().startsWith(h.getUniqueName())){
					hiera = h;
					
					for(Level l : h.getLevel()){
						if (mb.getUniqueName().startsWith(l.getUniqueName())){
							itemLevel = l;
							break;
						}
					}
					break;
				}
			}
		}
		
		
		
		if (e.isCol()) {
			mdx.removeCol(uname);
			
			if (itemLevel != null){
				
				for(int k = itemLevel.getLevelNumber(); k < hiera.getLevel().size(); k++){
					
					for(int i = 0; i < mdx.getCols().size(); i++){
						if (mdx.getCols().get(i).startsWith(hiera.getLevel().get(k).getUniqueName())){
							if (mdx.getCols().get(i).endsWith(".children")){
								mdx.removeCol(mdx.getCols().get(i).replace(".children", ""));
							}
							else{
								mdx.removeCol(mdx.getCols().get(i));
							}
							i--;
						}
						
					}
				}
				
				
			}
			
		}
		else {
			mdx.removeRow(uname);
			if (itemLevel != null){
				
				for(int k = itemLevel.getLevelNumber(); k < hiera.getLevel().size(); k++){
					
					for(int i = 0; i < mdx.getRows().size(); i++){
						if (mdx.getRows().get(i).startsWith(hiera.getLevel().get(k).getUniqueName())){
							if (mdx.getRows().get(i).endsWith(".children")){
								mdx.removeRow(mdx.getRows().get(i).replace(".children", ""));
							}
							else{
								mdx.removeRow(mdx.getRows().get(i));
							}
							i--;
						}
						
					}
				}
				
				
			}
		}
	}

	public Collection<Dimension> getDimensions() {
		return cube.getDimensions();
	}

	public Collection<String> getFilters() {
		
		return null;
	}

	public OLAPResult getLastResult() {
		return last;
	}

	public Collection<MeasureGroup> getMeasures() {
		return cube.getMeasures();
	}

	public boolean getShowEmpty() {
		return showEmpty;
//		return false;
	}

	public boolean getShowProperties() {
		
		return false;
	}

	public boolean getStateActive() {
		
		return false;
	}

	public RepositoryCubeView getView() {
		RepositoryCubeView view = new RepositoryCubeView();
		
		for(String s : mdx.getCols()){
			view.addCol(s);
		}
		
		for(String s : mdx.getRows()){
			view.addRow(s);
		}
		
		for(String s : mdx.getWhere()){
			view.addWhere(s);
		}
		
		view.setTopx(mdx.getTopx());
		
		view.setPersonalNames(mdx.getPersonalNames());
		
		view.setPercentMeasures(mdx.getPercentMeasures());
		
		view.setReportTitle(mdx.getReportTitle());
		
		view.setShowTotals(mdx.isShowTotals());
		
		view.setParameters(mdx.getParameters());
		
		view.setRowCount(last.getRaw().size());
		
		view.setColCount(last.getRaw().get(0).size());
		view.setShowEmpty(showEmpty);
		
		view.setShowProps(showProps);
		return view;
	}

	public void moveAfter(ItemElement e, ItemElement after) {
		mdx.pushState();
		
		if (e.isCol()) {
			mdx.moveCol(e.getDataMember().getUniqueName(), after.getDataMember().getUniqueName(), false);
		}
		else {
			mdx.moveRow(e.getDataMember().getUniqueName(), after.getDataMember().getUniqueName(), false);
		}
		
	}

	public void moveBefore(ItemElement e, ItemElement before) {
		mdx.pushState();
		
		if (e.isCol()) {
			mdx.moveCol(e.getDataMember().getUniqueName(), before.getDataMember().getUniqueName(), true);
		}
		else {
			mdx.moveRow(e.getDataMember().getUniqueName(), before.getDataMember().getUniqueName(), true);
		}
		
	}

	public void redo() {
		
		
	}

	public boolean remove(ItemElement e) {
		mdx.pushState();
		
		if (e.isCol()) {
			return mdx.delcol(e.getDataMember().getUniqueName(), false);
		}
		else {
			return mdx.delrow(e.getDataMember().getUniqueName(), false);
		}
		
	}

	public void removeLevel(ItemElement e) {
		mdx.pushState();
		
		String[] buf = e.getDataMember().getUniqueName().split("]");
		String res = "";
		//skip the last one
		for (int i=0; i < (buf.length - 1); i++) {
			res += buf[i] + "]";
		}
		
		if (e.isCol()) {
			mdx.removeCol(res);
		}
		else {
			mdx.removeRow(res);
		}
		
	}

	public void removeWhere(ItemElement e) {
		mdx.pushState();
		
		mdx.delWhere(e.getDataMember().getUniqueName());
		
	}

	public void restore() {
		mdx.pushState();
		mdx.restore();
	}

	public void setStateActive(boolean active) {
		
		
	}

	public void setshowEmpty(boolean show) {
		showEmpty = show;
		mdx.setShowEmpty(show);
		
	}

	public void setshowProperties(boolean show) {
		
		
	}

	public void swapAxes() {
		mdx.pushState();
		mdx.swapAxes();
		
	}

	public void undo() {
		OLAPQuery q = mdx.popBWState();
		
		if (q != null)
			mdx = q;
		
	}
	
	public boolean addChilds(OLAPMember mb, Hierarchy h) throws Exception {
		return cube.addMemberChilds(mb);
	}
	
	/**
	 * used internally to associate query element to model one
	 * query one is dirty
	 * @return
	 */
	private OLAPMember findMember(OLAPMember mb) {
		Iterator<Dimension> it = getDimensions().iterator();
		
		while (it.hasNext()) {
			Dimension d = it.next();
			
			if (d.getUniqueName().equalsIgnoreCase(mb.getDimension().getUniqueName())) {
				return (XMLAMember) d.findMember(mb);
			}
		}
		
		Log.info("Couldn't find specified Member : " + mb.getUniqueName());
		return null;
	}
	
	/**
	 * 
	 * - mb0
	 * 	- mb01
	 * 	 - mb011
	 * 	 - mb012
	 * 	  - mb0121
	 * 	- mb02
	 * 
	 * findSons(mb01) returns mb011, mb012, mb0121
	 * 
	 * @param e
	 */
	private void findSons(ItemElement e) {
		
	}

	public void setView(RepositoryCubeView view) {
		for(Parameter param : view.getParameters()) {
			param.setUname(param.getLevel() + ".&amp;[" + param.getValue() + "]");
		}
		
		mdx.setup(view);
	}

	public String getQuery() throws Exception{
		return mdx.getMDX();
	}

	public List<String> getDrillsUrl(ItemValue v) {
		
		return null;
	}

	
	
public void setMeasureUseOnlyLastLevelMember(String measureName, String timeHierarchyName, String timeDimensionName) throws Exception{
		
		boolean foundMeasure = false;
		for(MeasureGroup mg : cube.getMeasures()){
			for(Measure m : mg.getMeasures()){
				if (m.getName().equals(measureName)){
					foundMeasure = true;
					break;
				}
			}
		}
		
		if (! foundMeasure){
			throw new Exception("Cannot find the Measure named " + measureName);
		}
		
		boolean dimensionFound = false;
		for(Dimension d : cube.getDimensions()){
			if (d.getName().equals(timeDimensionName)){
				dimensionFound = true;
				if (timeHierarchyName != null){
					boolean foundHiera = false;
					for(Hierarchy h : d.getHierarchies()){
						if (h.getName().equals(timeHierarchyName)){
							Level lastLvl = h.getLevel().get(h.getLevel().size() - 1);
							
							mdx.measureAsLast(true, measureName, "([Measures].[" + measureName +  "],ClosingPeriod ( [" + timeDimensionName +  "].[" + lastLvl.getName() + "], [" + timeDimensionName + "].CurrentMember))");
							foundHiera = true;
						}
					}
					
					if (!foundHiera){
						throw new Exception("Cannot find the Hierarchy named " + timeHierarchyName + " on Dimension " + timeDimensionName);
					}
				}
				else{
					Hierarchy h  = d.getHierarchies().iterator().next();
					Level lastLvl = h.getLevel().get(h.getLevel().size() - 1);
					
					mdx.measureAsLast(true, measureName, "([Measures].[" + measureName +  "],ClosingPeriod ( [" + timeDimensionName +  "].[" + lastLvl.getName() + "], [" + timeDimensionName + "].CurrentMember))");

				}
				
			}
			if (!dimensionFound){
				throw new Exception("Cannot find the Dimension named " + timeDimensionName);
			}
		}
		
		
	}
	
	public void unsetMeasureUseOnlyLastLevelMember(String originalMeasureName) throws Exception{
		boolean foundMeasure = false;
		for(MeasureGroup mg : cube.getMeasures()){
			for(Measure m : mg.getMeasures()){
				if (m.getName().equals(originalMeasureName)){
					foundMeasure = true;
					break;
				}
			}
		}
		
		if (! foundMeasure){
			throw new Exception("Cannot find the Measure named " + originalMeasureName);
		}
		
		
		mdx.measureAsLast(false, originalMeasureName, null);
	}


	public void addTopx(Topx topx) {
		mdx.addTopx(topx);
	}

	public void removeTopx(Topx topx) {
		mdx.removeTopx(topx);
	}

	public List<Topx> getTopx() {
		return mdx.getTopx();
	}

	public void addPersonalName(String uname, String pname) {
		mdx.getPersonalNames().put(uname, pname);
	}

	public HashMap<String, String> getPersonalNames() {
		return mdx.getPersonalNames();
	}

	public void removePersonalName(String uname) {
		mdx.getPersonalNames().remove(uname);
	}

	public void addPercentMeasure(String measureName, boolean showMeasure) {
		mdx.getPercentMeasures().put(measureName, showMeasure);
	}

	public HashMap<String, Boolean> getPercentMeasures() {
		return mdx.getPercentMeasures();
	}

	public void removePercentMeasure(String measureName) {
		mdx.getPercentMeasures().remove(measureName);
	}

	public boolean isShowTotals() {
		return mdx.isShowTotals();
	}

	public void setShowTotals(boolean showTotals) {
		mdx.setShowTotals(showTotals);
	}

	public HashMap<String, String> findChildsForReporter(String uname) {
		return cube.findChildsForReporter(uname);
	}

	public String getReportTitle() {
		return mdx.getReportTitle();
	}

	public void setReportTitle(String reportTitle) {
		mdx.setReportTitle(reportTitle);
	}

	public void addParameter(String name, String value, String level) {
		Parameter param = new Parameter();
		param.setLevel(level);
		param.setName(name);
		param.setValue(value);
		mdx.getParameters().add(param);
	}

	public List<Parameter> getParameters() {
		return mdx.getParameters();
	}

	public void setParameters(List<Parameter> parameters) {
		mdx.setParameters(parameters);
	}

	public LinkedHashMap<String, LinkedHashMap<String, String>> getLevels() {
		return cube.getLevels();
	}

	public List<String> getParametersValues(String level) {
		return cube.getParametersValues(level);
	}

	public List<String> searchOnDimensions(String word, String level) {
		return cube.searchOnDimensions(word, level);
	}

	public OLAPQuery getMdx() {
		return mdx;
	}

	@Override
	public OLAPResult doQuery(int limit) throws Exception {
		
		return null;
	}

	@Override
	public OLAPResult doQuery(String mdxQuery, int limit) throws Exception {
		
		return null;
	}


	@Override
	public OLAPMember findOLAPMember(String uname) {
		return cube.getOLAPMember(uname);
	}
	@Override
	public void close() {
		this.cube = null;
		this.old = null;
		if (this.last != null){
			this.last.getRaw().clear();
		}
		this.last = null;
		this.mdx = null;

	}

	@Override
	public String getSchemaId() {
		
		return null;
	}

	@Override
	public List<Relation> getRelations() {
		
		return null;
	}

	@Override
	public void setApplyProjection(boolean applyProjection) {
		
		
	}

	@Override
	public void setProjection(Projection projection) {
		
		
	}

	@Override
	public OLAPResult getLastProjectionResult() {
		
		return null;
	}

	@Override
	public OLAPResult drillthrough(ItemValue v, int level, Projection projection) throws SQLException, Exception {
		
		return null;
	}

	@Override
	public Projection createForecastData() {
		
		return null;
	}

	@Override
	public Member refreshTimeDimension() throws Exception {
		
		return null;
	}

	@Override
	public boolean canBeRemoved(ItemElement item) throws Exception {
		
		return false;
	}

	@Override
	public List<Drill> getDrills() {
		
		return null;
	}

	@Override
	public void setSorting(HashMap<String, String> sortElements) {
		mdx.setSortElements(sortElements);
	}
}
