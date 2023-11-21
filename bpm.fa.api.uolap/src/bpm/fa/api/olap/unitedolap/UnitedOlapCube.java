package bpm.fa.api.olap.unitedolap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.fasd.olap.Drill;

import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPQuery;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.Topx;
import bpm.fa.api.olap.helper.StructureHelper;
import bpm.fa.api.olap.projection.Projection;
import bpm.fa.api.olap.query.MissingLastTimeDimensionException;
import bpm.fa.api.olap.query.WhereClauseException;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.log.Log;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.IRuntimeContext;

public class UnitedOlapCube implements OLAPCube {

	private UnitedOlapStructure structure;
	private OLAPQuery mdx;
	private OLAPResult last;
	private OLAPResult lastWithoutTotals;
	private OLAPResult lastProjResult;
	
	private boolean showProperties = false;
	private boolean showEmpty = true;
	private boolean stateActive = true;
	
	private IRuntimeContext runtimeContext;
	
	
	private List<Dimension> dimensions;
	
	public UnitedOlapCube(UnitedOlapStructure structure, RepositoryCubeView view, IRuntimeContext runtimeContext) {
		this(structure, runtimeContext);
		mdx.setup(view);
	}
	
	public UnitedOlapCube(UnitedOlapStructure structure, IRuntimeContext runtimeContext) {
		this.structure = structure;
		
		mdx = new OLAPQuery("[" + structure.getCubeName() + "]", this);
		
		mdx.setup(structure.getDimensions(), structure.getMeasures().get(0).getMeasures().iterator().next());
		mdx.setActive(true);
		
		this.runtimeContext = runtimeContext;
		
	}
	
	public UnitedOlapCube(UnitedOlapStructure structure, String cubeName, IRuntimeContext runtimeContext) {
		this.structure = structure;
		
		mdx = new OLAPQuery("[" + cubeName + "]", this);
		
		mdx.setup(structure.getDimensions(), structure.getMeasures().get(0).getMeasures().iterator().next());
		mdx.setActive(true);
		this.runtimeContext = runtimeContext;
	}

	@Override
	public void add(ItemElement e) throws WhereClauseException{
		checkWhereClause(e);
		mdx.pushState();
		try{
			String[] uname = e.getDataMember().getUniqueName().split("\\]\\.\\[");
			String dimension = uname[0];
			if(e.isCol()) {
				for(String r : mdx.getRows()) {
					if(r.startsWith(dimension)) {
						return;
					}
				}
				
				mdx.addcol(e.getDataMember().getUniqueName());
			}
			else {
				for(String r : mdx.getCols()) {
					if(r.startsWith(dimension)) {
						return;
					}
				}
				
				mdx.addrow(e.getDataMember().getUniqueName());
			}
		
			mdx.getMDX();
			
//			if (!getStateActive()){
//				getLastResult().addItem(e);
//			}
			
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after add " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");
			mdx = mdx.popBWState();
		}
		
	}

	
	private void checkWhereClause(ItemElement e) throws WhereClauseException{
//		List<String> whereMembers = mdx.getWheresDimensionUname(e.getDataMember().getUniqueName());
//		
//		if (!whereMembers.isEmpty()){
//			throw new WhereClauseException("The dimension " + e.getDataMember().getHierarchy() + " is already present in the Where clause.", whereMembers);
//		}
	}
	
	@Override
	public void addAfter(ItemElement e, ItemElement after) throws WhereClauseException,MissingLastTimeDimensionException{
		
		checkWhereClause(e);
		checkIfLast(e);
		mdx.pushState();
		try{
			//detect all missing parent Members
			List<String> missingMembers = new ArrayList<String>();
			
			Vector<String> v = null;
			if (after.isCol()){
				v = mdx.getCols();
			}
			else{
				v = mdx.getRows();
			}
			OLAPMember cur = e.getDataMember().getParent();
			while(cur != null && cur.getParent() != null){
				boolean found = false;
				for(String s : v){
					if (cur.getUniqueName().equals(s) || s.equals(cur.getParent().getUniqueName() + ".children")){
						found = true;
						break;
					}
				}
				
				if (!found){
					missingMembers.add(cur.getUniqueName());
				}
				
				cur = cur.getParent();
			}
			// check if the All elements is present, of not, the missing elements are not required
			boolean missingRequired = false;
			if (cur != null){
				for(String s : v){
					if (s.equals(cur.getUniqueName()) ){
						missingRequired = true; break;
					}
				}
			}
			
			
			
			
			if (after.isCol()){
				if (missingRequired){
					// re-order the missing required
					for(String s : missingMembers){
						mdx.addcol(s);
					}
					
				}
				mdx.addcolOrder(e.getDataMember().getUniqueName(), after.getDataMember().getUniqueName(), false);
				
			}
			else{
				if (missingRequired){
					// re-order the missing required
					for(String s : missingMembers){
						mdx.addrow(s);
					}
					
				}
				mdx.addrowOrder(e.getDataMember().getUniqueName(), after.getDataMember().getUniqueName(), false);
			}
		
			mdx.getMDX();
			
//			if (!getStateActive()){
//				getLastResult().addItem(e);
//			}
			
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after add " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");
			mdx = mdx.popBWState();
		}
	}

	@Override
	public void addBefore(ItemElement e, ItemElement before) throws WhereClauseException, MissingLastTimeDimensionException{
		checkWhereClause(e);
		checkIfLast(e);
		mdx.pushState();
		try{
			if (before.isCol()){
				mdx.addcolOrder(e.getDataMember().getUniqueName(), before.getDataMember().getUniqueName(), true);
				
			}
			else{
				mdx.addrowOrder(e.getDataMember().getUniqueName(), before.getDataMember().getUniqueName(), true);
			}
		
			mdx.getMDX();
			
//			if (!getStateActive()){
//				getLastResult().addItem(e);
//			}
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after add " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");
			mdx = mdx.popBWState();
		}
	}

	private void checkIfLast(ItemElement e) throws MissingLastTimeDimensionException {
		
		if(e.getDataMember().getUniqueName().contains("[Measures]")) {
			for(Measure mes : structure.getUtdMeasures()) {
				if(mes.getUname().equals(e.getDataMember().getUniqueName())) {
					if(mes.getCalculationType() != null && (mes.getCalculationType().equals("last") || mes.getCalculationType().equals("first"))) {
						String di = mes.getLastDimensionName();
						for(Dimension dim : getDimensions()) {
							if(dim.getName().equals(di)) {
								String uname = dim.getUniqueName().replace("]", "");
								for(String s : mdx.getCols()) {
									if(s.startsWith(uname)) {
										return;
									}
								}
								for(String s : mdx.getRows()) {
									if(s.startsWith(uname)) {
										return;
									}
								}
								throw new MissingLastTimeDimensionException("Missing dimension " + dim.getUniqueName() + " for the measure " + mes.getUname(), mes.getUname(), dim.getUniqueName());
							}
						}
					}
					break;
				}
			}
		}
		
		
	}

	@Override
	public boolean addChilds(OLAPMember memb, Hierarchy h) throws Exception {
		boolean added =  structure.addChilds(memb, runtimeContext);
		Hierarchy hiera = null;
		if (added){
			if (h != null ){
				hiera = h;
			}
			else if (memb.getHiera() != null){
				hiera = memb.getHiera();
			}
			
			if (hiera != null){
				OLAPMember m = hiera.getDefaultMember().findMember(memb.getUniqueName());
				
				if (m == null && hiera.getDefaultMember().getMembers().isEmpty()){
					addChilds(hiera.getDefaultMember(), hiera);
				}
				else {
					try {
						String[] parts = m.getUniqueName().split("\\]\\.\\[");
						if(parts.length > hiera.getLevel().size()) {
							return added;
						}
						
						addChilds(m, h);
						for(OLAPMember c : memb.getMembersVector()){
							m.addMember(c);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}

				}
			}
			
		}
		
		return added;
	}

	@Override
	public void addParameter(String name, String value, String level) {
		Parameter param = new Parameter();
		param.setLevel(level);
		param.setName(name);
		param.setValue(value);
		mdx.getParameters().add(param);
	}

	@Override
	public void addPercentMeasure(String measureName, boolean showMeasure) {
		mdx.getPercentMeasures().put(measureName, showMeasure);
	}

	@Override
	public void addPersonalName(String uname, String pname) {
		mdx.getPersonalNames().put(uname, pname);
	}

	@Override
	public void addTopx(Topx topx) {
		mdx.pushState();
		try{
			mdx.addTopx(topx);
		
			mdx.getMDX();
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after add " + topx.getElementName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");
			mdx = mdx.popBWState();
		}
	}

	@Override
	public void addWhere(ItemElement e) throws WhereClauseException{
		mdx.pushState();
		try{
			mdx.addWhere(e.getDataMember().getUniqueName());
		
			mdx.getMDX();
		}catch(WhereClauseException ex){
			throw ex;
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after addWhere " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");

			mdx = mdx.popBWState();
		}
	}

	@Override
	public OLAPResult doQuery() throws Exception {
//		structure.setShowTotals(mdx.isShowTotals());

//		if (!getStateActive()){
//			return getLastResult();
//		}
		
		String buf = null;
		try {
			buf = mdx.getMDX();
		} catch (Exception e) {
			undo();
			e.printStackTrace();
			throw e;
		}
		OLAPResult res = null;
		if(mdx.isApplyProjection() && mdx.getProjection().getType().equals(Projection.TYPE_EXTRAPOLATION)) {
			res = structure.executeProjectionQuery(this, buf, showProperties, getStateActive(), runtimeContext, mdx.getProjection());
		}
		else {
			res = structure.executeQuery(this, buf, showProperties, getStateActive(), runtimeContext);
		}
		if(mdx.isApplyProjection()) {
			lastProjResult = res;
			return lastProjResult;
		}
		else {
			last = res;
		}
		
		if (mdx.isShowTotals()){
			lastWithoutTotals = null;
			return last;
		}
		else{
			lastWithoutTotals = last.clone();
			StructureHelper.removeTotals(lastWithoutTotals.getRaw());
			return lastWithoutTotals;
		}
		
		
	}

	@Override
	public OLAPResult doQuery(String mdxQuery) throws Exception {
		
		last = structure.executeQuery(this, mdxQuery, showProperties, getStateActive(), runtimeContext);
		
		return last;
	}

	@Override
	public boolean drilldown(ItemElement e) {
		
		
		UnitedOlapMember m = (UnitedOlapMember) e.getDataMember();
		boolean result = false;
		
		mdx.pushState();
		try{
			result = addChilds(m, m.getHiera());
		
		
			if (e.isCol()) {
				mdx.addcol(m.getUniqueName() + ".children");
			}
			else {
				mdx.addrow(m.getUniqueName() + ".children");
			}
		
		
			mdx.getMDX();
			
			e.setDrilled(true);
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after drillDown " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");

			mdx = mdx.popBWState();
		}
		return result;
	}

	@Override
	public OLAPResult drillthrough(ItemValue v, int level) throws SQLException, Exception {
		Log.info("Drilling through");
		return structure.drillthroughSql(v, runtimeContext);
	}
	
	@Override
	public OLAPResult drillthrough(ItemValue v, int level, Projection projection) throws SQLException, Exception {
		return structure.drillthroughSql(v, runtimeContext, projection);
	}

	@Override
	public void drillup(ItemElement e) {
		mdx.pushState();
		try{
			
			String uname = e.getDataMember().getUniqueName();
			List<String> elementToRemove = new ArrayList<String>();
			Vector<String> vect = null;
			
			Log.info("Drilling up on : " + uname);
			if (e.isCol()) {
				
				vect = mdx.getCols();
			}
			else {
				vect = mdx.getRows();

			}
			
			for(int i = 0; i < vect.size(); i++){
				
				if (vect.get(i).startsWith(uname)){
					if (vect.get(i).endsWith(".children")){
//						vect.set(i, vect.get(i).replace(".children",""));
						elementToRemove.add(vect.get(i));
					}
					else if (!vect.get(i).equals(uname)){
						elementToRemove.add(vect.get(i));
					}
//					else if (!vect.get(i).equals(uname)){
//						elementToRemove.add(vect.get(i));
//					}
				}
			}
			
			vect.removeAll(elementToRemove);
		
			mdx.getMDX();
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after drillUP " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");

			mdx = mdx.popBWState();
		}
	}

	@Override
	public HashMap<String, String> findChildsForReporter(String uname) {
		try {
			return structure.findChildsForReporter(uname, runtimeContext);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<Dimension> getDimensions() {
//		if (dimensions == null){
//			dimensions = new ArrayList<Dimension>();
//			
//			
//			for(Dimension d : structure.getDimensions()){
//				Dimension clone = d.clone();
//				dimensions.add(clone);
//				
//				
//				for(Hierarchy h : clone.getHierarchies()){
//					UnitedOlapMember defMem = new UnitedOlapMember("All " + d.getName(), h.getUniqueName() + ".[All " + d.getName() + "]", "All " + d.getName(), h);
//				
//					h.setDefaultMember(defMem);
//				}
//			}
//		}
				
		return structure.getDimensions();
	}

	@Override
	public List<String> getDrillsUrl(ItemValue v) {
		return structure.getDrillsUrl(v);
	}

	@Override
	public Collection<String> getFilters() {
		return mdx.getWhere();
	}

	@Override
	public OLAPResult getLastResult() {
		if (isShowTotals()){
			return last;
		}
		else{
			if (lastWithoutTotals == null){
				lastWithoutTotals = last.clone();
				StructureHelper.removeTotals(lastWithoutTotals.getRaw());
			}
			return lastWithoutTotals;
		}
		
	}

	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getLevels() {
		return structure.getLevels();
	}

	@Override
	public OLAPQuery getMdx() {
		return mdx;
	}

	@Override
	public Collection<MeasureGroup> getMeasures() {
		List<MeasureGroup> map = new ArrayList<MeasureGroup>();
		
		for (int i=0; i < structure.getMeasures().size(); i++) {
			MeasureGroup m = structure.getMeasures().get(i);
			map.add(m);
		}
		
		return map;
	}

	@Override
	public List<Parameter> getParameters() {
		return mdx.getParameters();
	}

	@Override
	public List<String> getParametersValues(String level) {
		List<String> l = new ArrayList<String>();
		
		
		
		String word = "";
		
		for(Dimension d : getDimensions()){
			if (level.contains(d.getUniqueName())){
				word = d.getName();
				break;
			}
		}
		
		try{
			l.addAll(structure.searchOnDimensions(word, level, runtimeContext));
		}catch(Exception ex){
			ex.printStackTrace();
			Logger.getLogger(getClass()).error("Unable to find values for level " + level  + " - " + ex.getMessage(), ex);
		}
		
		return l;
	}

	@Override
	public HashMap<String, Boolean> getPercentMeasures() {
		return mdx.getPercentMeasures();
	}

	@Override
	public HashMap<String, String> getPersonalNames() {
		return mdx.getPersonalNames();
	}

	@Override
	public String getQuery() throws Exception{
		return mdx.getMDX();
	}

	@Override
	public String getReportTitle() {
		return mdx.getReportTitle();
	}

	@Override
	public boolean getShowEmpty() {
		return showEmpty;
	}

	@Override
	public boolean getShowProperties() {
		return showProperties;
	}

	@Override
	public boolean getStateActive() {
		return stateActive;
	}

	@Override
	public List<Topx> getTopx() {
		return mdx.getTopx();
	}

	@Override
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
		
		view.setSortElements(mdx.getSortElements());
		return view;
	}

	@Override
	public boolean isShowTotals() {
		return mdx.isShowTotals();
	}

	@Override
	public void moveAfter(ItemElement e, ItemElement after) {
		mdx.pushState();
		try{
			if (e.isCol() && after.isCol()) {
				mdx.moveCol(e.getDataMember().getUniqueName(), after.getDataMember().getUniqueName(), true);
			}
			else if (e.isRow() && after.isRow()){
				mdx.moveRow(e.getDataMember().getUniqueName(), after.getDataMember().getUniqueName(), true);
			}
			else{
				mdx.moveSwapAxis(e.getDataMember().getUniqueName(), after.getDataMember().getUniqueName(), true, e.isRow());
			}
		
			mdx.getMDX();
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after move " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");
			mdx = mdx.popBWState();
		}
	}

	@Override
	public void moveBefore(ItemElement e, ItemElement before) {
		mdx.pushState();
		try{
			if (e.isCol() && before.isCol()) {
				mdx.moveCol(e.getDataMember().getUniqueName(), before.getDataMember().getUniqueName(), false);
			}
			else if (e.isRow() && before.isRow()){
				mdx.moveRow(e.getDataMember().getUniqueName(), before.getDataMember().getUniqueName(), false);
			}
			else{
				mdx.moveSwapAxis(e.getDataMember().getUniqueName(), before.getDataMember().getUniqueName(), false, e.isRow());
			}
		
			mdx.getMDX();
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after move " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");
			mdx = mdx.popBWState();
		}
	}

	@Override
	public void redo() {
		OLAPQuery q = mdx.popFWState();
		
		if (q != null)
			mdx = q;
	}

	@Override
	public boolean remove(ItemElement e) {
		if (!mdx.canRemove(e)){
			return false;
		}
		
		
		mdx.pushState();
		boolean result = false;
		try{
			
			if (e.isCol()) {
				result= mdx.delcol(e.getDataMember().getUniqueName(), false);
			}
			else {
				result= mdx.delrow(e.getDataMember().getUniqueName(), false);
			}
			
			mdx.getMDX();
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after move " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");
			mdx = mdx.popBWState();
		}
		return result;

	}

	@Override
	public void removeLevel(ItemElement e) {
		if (!mdx.canRemove(e)){
			return ;
		}
		
		mdx.pushState();
		try{
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
		
			mdx.getMDX();
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after move " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");
			mdx = mdx.popBWState();
		}
	}

	@Override
	public void removePercentMeasure(String measureName) {
		mdx.getPercentMeasures().remove(measureName);
		
	}

	@Override
	public void removePersonalName(String uname) {
		mdx.getPersonalNames().remove(uname);
	}

	@Override
	public void removeTopx(Topx topx) {
		mdx.removeTopx(topx);
	}

	@Override
	public void removeWhere(ItemElement e) {
		mdx.pushState();
		try{
			mdx.delWhere(e.getDataMember().getUniqueName());
		
		
			mdx.getMDX();
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after move " + e.getDataMember().getUniqueName() + "- " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");
			mdx = mdx.popBWState();
		}
	}

	@Override
	public void restore() {
		mdx.pushState();
		mdx.restore();
	}

	@Override
	public List<String> searchOnDimensions(String word, String level) {
		try {
			return structure.searchOnDimensions(word, level, runtimeContext);
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Unable to search for the word : " + word, e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setMeasureUseOnlyLastLevelMember(String measureName, String timeHierarchyName, String timeDimensionName) throws Exception {
		//TODO
	}

	@Override
	public void setParameters(List<Parameter> parameters) {
		mdx.setParameters(parameters);
	}

	@Override
	public void setReportTitle(String reportTitle) {
		mdx.setReportTitle(reportTitle);
	}

	@Override
	public void setShowTotals(boolean showTotals) {
		if (showTotals == mdx.isShowTotals()){
			return;
		}
		mdx.setShowTotals(showTotals);
		
//		try{
//			if (!showTotals){
//				MondrianStructure.removeTotals(last.getRaw());
//			}
//			else{
//				//TODO : to be better
//				doQuery();
//			}
//		}catch(Exception ex){
//			
//		}
		
	}

	@Override
	public void setStateActive(boolean active) {
		stateActive = active;
	}

	@Override
	public void setView(RepositoryCubeView view) {
		for(Parameter param : view.getParameters()) {
			param.setUname(structure.findUnameForParameter(param, runtimeContext));
		}
		
		mdx.setup(view);
	}

	@Override
	public void setshowEmpty(boolean show) {
		showEmpty = show;
		mdx.setShowEmpty(show);
	}

	@Override
	public void setshowProperties(boolean show) {
		showProperties = show;
	}

	@Override
	public void swapAxes() {
		mdx.pushState();
		try{
			mdx.swapAxes();
			mdx.getMDX();
		}catch(Exception ex){
			Logger.getLogger(this.getClass()).warn("Cannot generate MDX Query after swappingAxes - " + ex.getMessage(), ex);
			Logger.getLogger(this.getClass()).warn("OLAPQuery PopedBW");
			mdx = mdx.popBWState();
		}
		
	}

	@Override
	public void undo() {
		OLAPQuery q = mdx.popBWState();
		
		if (q != null)
			mdx = q;
	}

	@Override
	public void unsetMeasureUseOnlyLastLevelMember(String originalMeasureName) throws Exception {
		
		
	}

	public void initServer(String group, String user, String password, boolean isEncrypted) throws Exception {
		structure.initServer(group,user,password,isEncrypted);
	}

	@Override
	public OLAPResult doQuery(int limit) throws Exception {
//		structure.setShowTotals(mdx.isShowTotals());

		String buf = mdx.getMDX();
		Logger.getLogger(getClass()).info("Executing query ");//: \n" + buf);
		
		last = structure.executeQuery(this, buf, showProperties, limit, runtimeContext);
		
		return last;
	}

	@Override
	public OLAPResult doQuery(String mdxQuery, int limit) throws Exception {
//		structure.setShowTotals(mdx.isShowTotals());

		Logger.getLogger(getClass()).info("Executing query");// : \n" + mdxQuery);
		
		last = structure.executeQuery(this, mdxQuery, showProperties, limit, runtimeContext);
		
		return last;
	}

	@Override
	public OLAPMember findOLAPMember(String uname) {
		for(Dimension d : getDimensions()){
			for(bpm.fa.api.olap.Hierarchy h : d.getHierarchies()){
				if (uname.startsWith(h.getUniqueName())){
//					UnitedOlapServiceProvider.getInstance().getModelService().getSubMembers(memb.getUniqueName(), utdSchema.getId(), item.getCubename(), ctx);
					try{
						return ((UnitedOlapMember)h.getDefaultMember()).findMember(
								uname,
								structure.getUtdSchemaId(),
								structure.getCubeName(),
								runtimeContext);
					}catch(Exception ex){
						Logger.getLogger(getClass()).error(ex.getMessage(), ex);
					}
					
				}
			}
		}
		
		
		return null;
	}

	@Override
	public void close() {
		this.dimensions.clear();
		this.dimensions = null;
		
		if (this.lastWithoutTotals != null){
			this.lastWithoutTotals.getRaw().clear();
		}
		this.lastWithoutTotals = null;
		if (this.last != null){
			this.last.getRaw().clear();
		}
		this.last = null;
		this.mdx = null;
		this.runtimeContext = null;
		this.structure = null;
		
	}

	@Override
	public String getSchemaId() {
		return structure.getUtdSchemaId();
	}

	@Override
	public List<Relation> getRelations() {
		List<Relation> relations = new ArrayList<Relation>();
		for(Datasource ds : structure.getUtdSchema().getDatasources()) {
			for(Relation rel : ds.getRelations()) {
				if(!relations.contains(rel)) {
					relations.add(rel);
				}
			}
		}
		return relations;
	}

	@Override
	public void setApplyProjection(boolean applyProjection) {
		mdx.setApplyProjection(applyProjection);
	}

	@Override
	public void setProjection(Projection projection) {
		mdx.setProjection(projection);
		mdx.setApplyProjection(true);
	}

	@Override
	public OLAPResult getLastProjectionResult() {
		return lastProjResult;
	}

	@Override
	public Projection createForecastData() throws Exception {
		return structure.createForecastData(mdx.getProjection(), runtimeContext);
	}

	@Override
	public Member refreshTimeDimension() throws Exception {
		return structure.refreshTimeDimension(mdx.getProjection(), runtimeContext);
	}

	@Override
	public boolean canBeRemoved(ItemElement item) throws Exception {
		return mdx.canRemove(item);
	}

	@Override
	public List<Drill> getDrills() {
		return structure.getDrills();
	}

	@Override
	public void setSorting(HashMap<String, String> sortElements) {
		mdx.setSortElements(sortElements);
	}



}
