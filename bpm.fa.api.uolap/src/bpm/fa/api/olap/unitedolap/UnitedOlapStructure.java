package bpm.fa.api.olap.unitedolap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.fasd.olap.Drill;
import org.fasd.olap.ICubeView;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.item.ItemProperties;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.OLAPStructure;
import bpm.fa.api.olap.projection.Projection;
import bpm.fa.api.repository.StructureQueryLogger;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.ModelFactory;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.model.impl.ProjectionMeasureCondition;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.ResultCell;
import bpm.united.olap.api.result.ResultLine;
import bpm.united.olap.api.result.impl.EmptyResultCell;
import bpm.united.olap.api.result.impl.ItemResultCell;
import bpm.united.olap.api.result.impl.ValueResultCell;
import bpm.united.olap.api.runtime.IRuntimeContext;

public class UnitedOlapStructure implements OLAPStructure {

	private ArrayList<Dimension> dimensions = new ArrayList<Dimension>();
	private ArrayList<bpm.fa.api.olap.MeasureGroup> measures = new ArrayList<MeasureGroup>();
	private Schema utdSchema;
	private String cubeName;

	private OlapResult lastResult;
	private StructureQueryLogger queryLogger;
	private List<Drill> drills;

	public UnitedOlapStructure(StructureQueryLogger queryLogger, String cubeName, Schema utdSchema, ArrayList<Dimension> dimensions, ArrayList<bpm.fa.api.olap.MeasureGroup> measures, List<Drill> drills) {
		this.dimensions = dimensions;
		this.utdSchema = utdSchema;
		this.measures = measures;
		this.cubeName = cubeName;
		this.queryLogger = queryLogger;
		this.drills = drills;
	}

	public OLAPCube createCube(IRuntimeContext ctx) {

		return new UnitedOlapCube(this, ctx);
	}

	@Override
	public OLAPCube createCube(String path, IRuntimeContext runtimeContext) throws Exception {
		throw new Exception("TODO");
	}

	@Override
	public OLAPCube createCube(String path, List<ICubeView> lstCubeViews, IRuntimeContext runtimeContext) throws Exception {
		throw new Exception("TODO");

	}

	public OLAPResult executeQuery(OLAPCube cube, String str, boolean showProps, boolean computeDatas, IRuntimeContext ctx) throws Exception {
		OlapResult result = null;
		try {
			Date d = new Date();
			result = UnitedOlapServiceProvider.getInstance().getRuntimeService().executeQuery(str, utdSchema.getId(), getCubeName(), computeDatas, ctx);

			if(queryLogger != null) {
				Date e = new Date();
				try {
					queryLogger.storeMdxQuery(str, d, e.getTime() - d.getTime());

				} catch(Exception ex) {
					Logger.getLogger(getClass()).warn("Fail to log MdxQuery in VanillaPlatform - " + ex.getMessage(), ex);
				}
			}

		} catch(Exception e) {
			throw e;
		}
		return parseQuery(result, showProps, str, cube);
	}

	@Override
	public OLAPResult executeQuery(OLAPCube cube, String str, boolean showProps, IRuntimeContext ctx) {
		OlapResult result = null;
		try {
			Date d = new Date();
			result = UnitedOlapServiceProvider.getInstance().getRuntimeService().executeQuery(str, utdSchema.getId(), getCubeName(), true, ctx);

			if(queryLogger != null) {
				Date e = new Date();
				try {
					queryLogger.storeMdxQuery(str, d, e.getTime() - d.getTime());
				} catch(Exception ex) {
					Logger.getLogger(getClass()).warn("Fail to log MdxQuery in VanillaPlatform - " + ex.getMessage(), ex);
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		return parseQuery(result, showProps, str, cube);
	}

	private OLAPResult parseQuery(OlapResult result, boolean showProps, String mdxQuery, OLAPCube cube) {

		lastResult = result;

		ArrayList<ArrayList<Item>> resultTable = new ArrayList<ArrayList<Item>>();
		int ifirst = 1;
		int jfirst = 0;

		// find ifirst - jfirst
		for(ResultLine line : result.getLines()) {

			if(jfirst == 0) {
				for(ResultCell cell : line.getCells()) {
					if(cell instanceof ItemResultCell) {
						break;
					}
					jfirst++;
				}
			}

			else {
				if(line.getCells().get(0) instanceof ItemResultCell) {
					break;
				}
				ifirst++;
			}
		}

		// to check wich items are drilles
		List<ItemElement> createdElements = new ArrayList<ItemElement>();

		// create the result table
		for(ResultLine line : result.getLines()) {

			ArrayList<Item> resultLine = new ArrayList<Item>();
			int j = 0;
			for(ResultCell cell : line.getCells()) {

				Item resultItem = null;
				if(cell instanceof EmptyResultCell) {
					resultItem = new ItemNull();
				}

				else if(cell instanceof ItemResultCell) {
					String uname = ((ItemResultCell) cell).getUname();

					if(((ItemResultCell) cell).getElement() instanceof Member) {
						resultItem = new ItemElement(new UnitedOlapMember((Member) ((ItemResultCell) cell).getElement(), null), j < jfirst ? false : true, false);
					}
					else {
						resultItem = new ItemElement(uname, j < jfirst ? false : true, true);
						//XXX
						((ItemElement)resultItem).getDataMember().setCaption(((ItemResultCell) cell).getCaption());
					}
					if(mdxQuery.contains(uname + ".children")) {
						((ItemElement) resultItem).setDrilled(true);
					}
					createdElements.add((ItemElement) resultItem);

					String[] parts = uname.split("\\]\\.");
					for(Dimension d : cube.getDimensions()) {
						for(bpm.fa.api.olap.Hierarchy h : d.getHierarchies()) {
							if(h.getUniqueName().startsWith(parts[0])) {

								UnitedOlapMember m = ((UnitedOlapMember) ((ItemElement) resultItem).getDataMember());
								if(m.getHiera() == null) {
									m.setHierarchy(h);
								}

								if(!h.getDefaultMember().getUniqueName().equals(uname)) {
									String s = uname.replace(h.getDefaultMember().getUniqueName() + ".", "");

									if(h.isParentChild()) {
										m.setLevel(h.getLevel().get(0));
									}
									else {
										m.setLevel(h.getLevel().get(s.split("\\]|\\]\\.").length - 1));
									}

								}

							}
						}
					}

				}

				else if(cell instanceof ValueResultCell) {
					if(((ValueResultCell) cell).getFormatedValue() != null) {
						resultItem = new ItemValue(((ValueResultCell) cell).getFormatedValue().replace("%", ""), ((ValueResultCell) cell).getValue(), ((ValueResultCell) cell).getDrillthroughId());
					}
					else {
						resultItem = new ItemValue("", 1.2345E-8, "");
					}
				}

				resultLine.add(resultItem);
				j++;
			}

			resultTable.add(resultLine);

		}

		// reparse to set item drilled or not
		updateDrilledState(createdElements);

		// re-reparse to show propeties
		if(showProps) {
			// on columns
			for(int i = 0; i < ifirst; i++) {
				boolean finded = false;
				int j = jfirst;
				while(!finded) {
					Item it = resultTable.get(i).get(j);
					if(it instanceof ItemElement) {
						ItemElement elem = (ItemElement) it;
						if(elem.getLabel() != null && !elem.getLabel().equalsIgnoreCase("")) {
							if(elem.getDataMember().hasProperties()) {
								for(int p = 0; p < elem.getDataMember().getPropertiesName().length; p++) {
									ArrayList<Item> propLine = new ArrayList<Item>();

									for(int k = 0; k < resultTable.get(i).size(); k++) {
										propLine.add(new ItemNull());
									}

									for(int l = j; l < resultTable.get(i).size(); l++) {
										Item item = resultTable.get(i).get(l);
										if(item instanceof ItemElement && ((ItemElement) item).getDataMember() != null && ((ItemElement) item).getDataMember().hasProperties()) {
											ItemProperties proper = new ItemProperties(((ItemElement) item).getDataMember().getPropertiesValue()[p], ((ItemElement) item).getDataMember().getPropertiesName()[p], ((ItemElement) item).getDataMember().getUniqueName());
											propLine.set(l, proper);
										}

									}

									resultTable.add(i + 1, propLine);

									i++;
									ifirst++;
								}
							}
						}
						finded = true;
					}
					j++;
				}
			}

			// on rows
			for(int j = 0; j < jfirst; j++) {
				boolean finded = false;
				int i = ifirst;
				while(!finded) {
					Item it = resultTable.get(i).get(j);
					if(it instanceof ItemElement) {
						ItemElement elem = (ItemElement) it;
						if(elem.getLabel() != null && !elem.getLabel().equalsIgnoreCase("")) {
							if(elem.getDataMember().hasProperties()) {
								for(int p = 0; p < elem.getDataMember().getPropertiesName().length; p++) {

									int count = 0;
									for(int l = i; l < resultTable.size(); l++) {
										Item item = resultTable.get(l).get(j);
										if(item instanceof ItemElement && ((ItemElement) item).getDataMember() != null && ((ItemElement) item).getDataMember().hasProperties()) {
											ItemProperties proper = new ItemProperties(((ItemElement) item).getDataMember().getPropertiesValue()[p], ((ItemElement) item).getDataMember().getPropertiesName()[p], ((ItemElement) item).getDataMember().getUniqueName());
											resultTable.get(l).add(j + 1, proper);
											count = resultTable.get(l).size();
										}
									}

									for(int k = 0; k < resultTable.size(); k++) {
										if(resultTable.get(k).size() < count) {
											resultTable.get(k).add(j + 1, new ItemNull());
										}
									}

									j++;
									jfirst++;
								}
							}
						}
						finded = true;
					}
					i++;
				}
			}
		}
		
		OLAPResult res = new OLAPResult(resultTable, ifirst, jfirst);
		
		SortHelper.sort(res, cube.getMdx().getSortElements(), false, null);
		
		return res;
	}

	private void updateDrilledState(List<ItemElement> createdElements) {
		for(ItemElement e : createdElements) {

			List<ItemElement> parents = new ArrayList<ItemElement>();
			for(ItemElement _e : createdElements) {
				if(e != _e && !e.getDataMember().getUniqueName().equals(_e.getDataMember().getUniqueName())) {
					if(_e.getDataMember().getUniqueName().startsWith(e.getDataMember().getUniqueName()) && !e.isDrilled()) {
						parents.add(e);
					}
				}
			}

			for(ItemElement p : parents) {
				p.setDrilled(true);

			}

		}

	}

	@Override
	public ArrayList<Dimension> getDimensions() {
		return dimensions;
	}

	@Override
	public ArrayList<MeasureGroup> getMeasures() {
		return measures;
	}

	public String findUnameForParameter(Parameter param, IRuntimeContext ctx) {
		try {
			List<String> res = UnitedOlapServiceProvider.getInstance().getModelService().searchOnDimensions(param.getValue(), param.getLevel(), getUtdSchemaId(), getCubeName(), ctx);
			return res.get(0);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getDrillsUrl(ItemValue v) {

		ResultCell cell = lastResult.getLines().get(v.getDrillUrlRow()).getCells().get(v.getDrillUrlCell());

		List<String> urls = new ArrayList<String>();
		for(Cube cube : utdSchema.getCubes()) {
			if(!cube.getName().equals(cubeName)) {

				ValueResultCell value = (ValueResultCell) cell;
				List<String> axes = value.getDataCellId().getIntersections();

				for(String axe : axes) {
					String[] parts = axe.split("\\]\\.");
					String dimName = parts[0].replace("[", "");

					for(bpm.united.olap.api.model.Dimension dim : cube.getDimensions()) {
						if((dim.getName() + "." + dim.getHierarchies().get(0).getName()).equals(dimName)) {
							urls.add(cube.getName() + ";" + dimName + ";" + axe);
						}
					}
				}

			}
		}

		return urls;
	}

	public OLAPResult drillthroughSql(ItemValue v, IRuntimeContext ctx) {

		try {
			OlapResult res = null;

			for(ResultLine line : lastResult.getLines()) {
				for(ResultCell cell : line.getCells()) {
					if(cell instanceof ValueResultCell) {
						ValueResultCell resCell = (ValueResultCell) cell;
						if(resCell.getDrillthroughId().equals(v.getDrillThroughSql())) {
							res = UnitedOlapServiceProvider.getInstance().getRuntimeService().drillthrough(resCell, utdSchema.getId(), getCubeName(), ctx);
						}
					}
					if(res != null && res.getLines() != null) {
						break;
					}
				}
				if(res != null && res.getLines() != null) {
					break;
				}
			}

			if(res != null && res.getLines() != null) {
				ArrayList<ArrayList<Item>> items = new ArrayList<ArrayList<Item>>();
				for(ResultLine line : res.getLines()) {
					ArrayList<Item> itemLine = new ArrayList<Item>();
					for(ResultCell cell : line.getCells()) {
						if(cell instanceof ValueResultCell) {
							ValueResultCell resCell = (ValueResultCell) cell;
							ItemValue val = new ItemValue(resCell.getFormatedValue(), "");
							itemLine.add(val);
						}
					}
					items.add(itemLine);
				}
				OLAPResult result = new OLAPResult(items, items.get(0).size(), items.size());
				return result;
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public OLAPResult drillthroughSql(ItemValue v, IRuntimeContext runtimeContext, Projection projection) {
		try {
			OlapResult res = null;

			for(ResultLine line : lastResult.getLines()) {
				for(ResultCell cell : line.getCells()) {
					if(cell instanceof ValueResultCell) {
						ValueResultCell resCell = (ValueResultCell) cell;
						if(resCell.getDrillthroughId().equals(v.getDrillThroughSql())) {

							bpm.united.olap.api.model.impl.Projection utdProjection = createUnitedOlapProjection(projection);

							res = UnitedOlapServiceProvider.getInstance().getRuntimeService().drillthrough(resCell, utdSchema.getId(), getCubeName(), runtimeContext, utdProjection);
						}
					}
					if(res != null) {
						break;
					}
				}
				if(res != null) {
					break;
				}
			}

			if(res != null) {
				ArrayList<ArrayList<Item>> items = new ArrayList<ArrayList<Item>>();
				for(ResultLine line : res.getLines()) {
					ArrayList<Item> itemLine = new ArrayList<Item>();
					for(ResultCell cell : line.getCells()) {
						if(cell instanceof ValueResultCell) {
							ValueResultCell resCell = (ValueResultCell) cell;
							ItemValue val = new ItemValue(resCell.getFormatedValue(), "");
							itemLine.add(val);
						}
					}
					items.add(itemLine);
				}
				OLAPResult result = new OLAPResult(items, items.get(0).size(), items.size());
				return result;
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private bpm.united.olap.api.model.impl.Projection createUnitedOlapProjection(Projection projection) {

		bpm.united.olap.api.model.impl.Projection result = new bpm.united.olap.api.model.impl.Projection();

		result.setName(projection.getName());
		result.setType(projection.getType());
		if(!projection.getType().equals("What if")) {
			result.setEndDate(projection.getEndDate());
			result.setStartDate(projection.getStartDate());
			result.setProjectionLevel(projection.getProjectionLevel());
		}
		result.setProjectionMeasures(createUnitedProjectionMeasures(projection.getProjectionMeasures()));

		return result;
	}

	private List<ProjectionMeasure> createUnitedProjectionMeasures(List<bpm.fa.api.olap.projection.ProjectionMeasure> projectionMeasures) {

		List<ProjectionMeasure> result = new ArrayList<ProjectionMeasure>();

		for(bpm.fa.api.olap.projection.ProjectionMeasure pm : projectionMeasures) {
			ProjectionMeasure res = new ProjectionMeasure();
			res.setFormula(pm.getFormula());
			res.setUname(pm.getUname());
			res.setConditions(createUnitedOlapProjectionConditions(pm.getConditions()));

			result.add(res);
		}

		return result;
	}

	private List<ProjectionMeasureCondition> createUnitedOlapProjectionConditions(List<bpm.fa.api.olap.projection.ProjectionMeasureCondition> conditions) {

		List<ProjectionMeasureCondition> result = new ArrayList<ProjectionMeasureCondition>();

		for(bpm.fa.api.olap.projection.ProjectionMeasureCondition cond : conditions) {
			ProjectionMeasureCondition res = new ProjectionMeasureCondition();
			res.setFormula(cond.getFormula());
			for(String uname : cond.getMemberUnames()) {
				Member mem = ModelFactory.eINSTANCE.createMember();
				mem.setUname(uname);
				res.addMember(mem);
			}
			result.add(res);
		}

		return result;
	}

	public HashMap<String, String> findChildsForReporter(String uname, IRuntimeContext runtimeContext) throws Exception {
		HashMap<String, String> results = new LinkedHashMap<String, String>();

		OLAPMember olapMember = getOLAPMember(uname);
		if(!(olapMember.getMembers() != null && olapMember.getMembers().size() > 0)) {
			addChilds(olapMember, runtimeContext);
		}

		for(OLAPMember mem : olapMember.getMembersVector()) {
			results.put(mem.getUniqueName(), mem.getHiera().getUniqueName());
		}

		return results;
	}

	public LinkedHashMap<String, LinkedHashMap<String, String>> getLevels() {
		LinkedHashMap<String, LinkedHashMap<String, String>> levels = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		for(Dimension dim : dimensions) {
			if(!dim.getName().equalsIgnoreCase("Measures")) {
				LinkedHashMap<String, String> dimLvl = new LinkedHashMap<String, String>();
				for(bpm.fa.api.olap.Level lvl : dim.getHierarchies().iterator().next().getLevel()) {
					dimLvl.put(lvl.getUniqueName(), lvl.getName());
				}
				levels.put(dim.getName(), dimLvl);
			}
		}
		return levels;
	}

	public List<String> getParametersValues(String level, IRuntimeContext ctx) {
		List<String> results = new ArrayList<String>();
		try {
			List<Member> submems = UnitedOlapServiceProvider.getInstance().getModelService().getSubMembers(level, utdSchema.getId(), getCubeName(), ctx);

			for(Member mem : submems) {
				results.add(mem.getName());
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public List<String> searchOnDimensions(String word, String level, IRuntimeContext ctx) throws Exception {
		return UnitedOlapServiceProvider.getInstance().getModelService().searchOnDimensions(word, level, utdSchema.getId(), getCubeName(), ctx);
	}

	@Override
	public boolean addChilds(OLAPMember memb, IRuntimeContext ctx) throws Exception {
		try {
			if(memb.getMembers() == null || memb.getMembers().size() <= 0) {
				List<Member> submems = UnitedOlapServiceProvider.getInstance().getModelService().getSubMembers(memb.getUniqueName(), utdSchema.getId(), getCubeName(), ctx);
				memb.setSynchro(true);

				for(Member subMem : submems) {

					UnitedOlapMember sub = new UnitedOlapMember(subMem, memb.getHiera());
					memb.addMember(sub);
				}
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}

		return false;
	}

	public void initServer(String groupName, String user, String password, boolean isEncrypted) throws Exception {

		for(Datasource ds : utdSchema.getDatasources()) {
			if(ds.getDatasourceExtensionId().equals("bpm.metadata.birt.oda.runtime")) {
				if(groupName != null && !groupName.equals("")) {
					ds.getPublicProperties().put("GROUP_NAME", groupName);
				}
				if(user != null && !user.equals("")) {
					ds.getPublicProperties().put("USER", user);
				}
				if(password != null && !password.equals("")) {
					ds.getPublicProperties().put("PASSWORD", password);
					ds.getPublicProperties().put("IS_ENCRYPTED", isEncrypted);
				}
			}
		}
	}

	public OLAPResult executeQuery(OLAPCube cube, String buf, boolean showProperties, int limit, IRuntimeContext ctx) {
		OlapResult result = null;
		try {
			result = UnitedOlapServiceProvider.getInstance().getRuntimeService().executeQuery(buf, utdSchema.getId(), getCubeName(), limit, true, ctx);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return parseQuery(result, showProperties, buf, cube);
	}

	@Override
	public OLAPMember getOLAPMember(String uname) {
		for(Dimension d : getDimensions()) {
			for(bpm.fa.api.olap.Hierarchy h : d.getHierarchies()) {
				if(uname.startsWith(h.getUniqueName())) {
					return h.getDefaultMember().findMember(uname);
				}
			}
		}
		return null;
	}

	@Override
	public List<bpm.fa.api.olap.Measure> getAllMeasures() {
		List<bpm.fa.api.olap.Measure> l = new ArrayList<bpm.fa.api.olap.Measure>();
		for(MeasureGroup m : getMeasures()) {
			l.addAll(m.getMeasures());
		}
		return l;
	}

	protected String getUtdSchemaId() {
		return utdSchema.getId();
	}

	public String getCubeName() {
		return cubeName;
	}

	public List<Measure> getUtdMeasures() {
		return utdSchema.getMeasures();
	}

	protected Schema getUtdSchema() {
		return utdSchema;
	}

	public Projection createForecastData(Projection projection, IRuntimeContext ctx) throws Exception {

		bpm.united.olap.api.model.impl.Projection utdProj = createUnitedOlapProjection(projection);

		String fileName = UnitedOlapServiceProvider.getInstance().getRuntimeService().createExtrapolation(utdSchema.getId(), cubeName, utdProj, ctx);

		projection.setProjectionFact(fileName);

		return projection;
	}

	public OLAPResult executeProjectionQuery(UnitedOlapCube unitedOlapCube, String buf, boolean showProperties, boolean stateActive, IRuntimeContext runtimeContext, Projection projection) throws Exception {
		OlapResult result = null;
		try {
			Date d = new Date();
			result = UnitedOlapServiceProvider.getInstance().getRuntimeService().executeQueryForExtrapolationProjection(buf, getUtdSchemaId(), cubeName, new Integer(0), stateActive, runtimeContext, createUnitedOlapProjection(projection));

			if(queryLogger != null) {
				Date e = new Date();
				try {
					queryLogger.storeMdxQuery(buf, d, e.getTime() - d.getTime());

				} catch(Exception ex) {
					Logger.getLogger(getClass()).warn("Fail to log MdxQuery in VanillaPlatform - " + ex.getMessage(), ex);
				}
			}

		} catch(Exception e) {
			throw e;
		}
		return parseQuery(result, showProperties, buf, unitedOlapCube);
	}

	public Member refreshTimeDimension(Projection proj, IRuntimeContext runtimeContext) throws Exception {
		Member rootMember = UnitedOlapServiceProvider.getInstance().getModelService().refreshTimeDimension(getUtdSchemaId(), cubeName, runtimeContext, createUnitedOlapProjection(proj));
		return rootMember;
	}

	public List<Drill> getDrills() {
		return drills;
	}

}
