package bpm.freemetrics.api.olap;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.fasd.datasource.DataObject;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPRelation;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeMember;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeObject;

import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.dashOrTheme.Theme;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.organisation.relations.appl_group.FmApplication_Groups;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.utils.IConstants;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FasdHelper {

	private static FasdHelper instance;
	private IRepositoryApi sock;
	private IVanillaSecurityManager mng;
	private IRepository repFasd;
	private List<Group> lstGroups;
	private FAModel fasdModel;
	private OLAPCube cube;
	private IManager manager;
	
	private List<TreeMember> actualMembers = new ArrayList<TreeMember>();
	private HashMap<TreeMes, Integer> addedMeasures = new HashMap<TreeMes, Integer>();
	
	private String dateField;
	
	private int fmGroupId;
	
	private boolean addEndWell = true;
	
	private Connection con;
	private Statement stmt;
	private ResultSet resultSet;
	
	private boolean updateValues;
	
	private FasdHelper() {
		
	}
	
	public static FasdHelper getInstance() {
		if(instance == null) {
			instance = new FasdHelper();
		}
		return instance;
	}
	
	public boolean connect(String vanillaUrl, String user, String password, String path, int fmGroupId, IManager manager, Repository rep) {
		
		this.fmGroupId = fmGroupId;
		
		IVanillaContext ctx = new BaseVanillaContext(vanillaUrl, user, password);
		
		IRepositoryContext repCtx = new BaseRepositoryContext(ctx, null, rep);
		
		sock = new RemoteRepositoryApi(repCtx);
		
		this.mng = new RemoteVanillaPlatform(ctx).getVanillaSecurityManager();
		
		this.manager = manager;
		
		
		return true;
		
	}
	
	public List<Group> getGroups() {
		if(sock == null) {
			return null;
		}
		
		lstGroups = new ArrayList<Group>();
		try{
			lstGroups.addAll(mng.getGroups());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return lstGroups;
	}
	
	public List<String> getGroupNames() {
		if(sock == null) {
			return null;
		}
		
		lstGroups = new ArrayList<Group>();
		try{
			lstGroups.addAll(mng.getGroups());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		List<String> groupNames = new ArrayList<String>();
		for(Group gr : lstGroups){
			groupNames.add(gr.getName());
		}
 		
		return groupNames;
	}

	@Deprecated
	public IRepository getRepository() {
		return repFasd;
	}
	
	public IRepository getRepository(String group) {
		IRepository repFasd = new bpm.vanilla.platform.core.repository.Repository(sock, IRepositoryApi.FASD_TYPE);
		return repFasd;
	} 
	
	/**
	 * a method who return cube names in the selected olap schema
	 * @param fasd
	 * @return
	 * @throws Exception
	 */
	public List<String> getCubeNames(RepositoryItem fasd) throws Exception {
		String fasdXml = null;
		try {
			fasdXml = sock.getRepositoryService().loadModel(fasd);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error when loading FASD model from Repository:" + e.getMessage(), e);
		}
		
				
		DigesterFasd dig = null;
		try {
			dig = new DigesterFasd(IOUtils.toInputStream(fasdXml, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error when parsing FASD xml :" + e.getMessage(), e);
		}
		FAModel model = dig.getFAModel();
		this.fasdModel = model;
		
		List<String> res = new ArrayList<String>();
		for(OLAPCube cube : model.getOLAPSchema().getCubes()) {
			res.add(cube.getName());
		}
		
		return res;
	}
	
	/**
	 * A method who return the selected cube
	 * @param cubeName
	 * @return
	 */
	public OLAPCube getCube(String cubeName) {
		
		List dimsMes = new ArrayList();
		
		for(OLAPCube cube : fasdModel.getOLAPSchema().getCubes()) {
			if(cube.getName().equalsIgnoreCase(cubeName)) {
				this.cube = cube;
				break;
			}
		}
		
		return cube;
	}
	
	/**
	 * create entries in the database
	 * @param theme 
	 * @param b 
	 * @return
	 * @throws Exception 
	 */
	public boolean createDatabaseEntries(List<TreeDim> dimensions, List<TreeObject> selectedItems, HashMap<TreeMes, String> selectedMetrics, Theme theme, String dateField, boolean updateValues) throws Exception {
		this.dateField = dateField;	
		this.updateValues = updateValues;
		
		//create the connection
		if (cube == null){
			throw new Exception ("Cube not found in model");
		}
		
		try {
			con = cube.getDataSource().getDriver().getConnection().getConnection().getJdbcConnection();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		if (con == null){
			throw new Exception("Unable to create SQL Connection");
		}
		
		//create the statement
		stmt = con.createStatement();
		stmt.setFetchSize(100);
		
		exploreDimension(dimensions, selectedItems, selectedMetrics, theme, 0);
		
		stmt.close();
		con.close();
		
		return addEndWell;
		
	}

	/**
	 * Explore the dimensions to find members to add
	 * @param dimensions
	 * @param selectedItems
	 * @param selectedMetrics
	 * @param theme
	 * @param index
	 */
	private void exploreDimension(List<TreeDim> dimensions, List<TreeObject> selectedItems, HashMap<TreeMes, String> selectedMetrics, Theme theme, int index) {
		TreeDim dim = dimensions.get(index);
		
		for(Object o : dim.getChildren()) {
			
			if(o instanceof TreeHierarchy) {
				
				TreeHierarchy hier = (TreeHierarchy) o;
				
				for(Object obj : hier.getChildren()) {
					
					if(obj instanceof TreeMember) {
						
						if(actualMembers.size() >= index + 1) {
							actualMembers.remove(index);
						}
						
						TreeMember member = (TreeMember) obj;
						
						//look if member has children
						if(!getMemberChildren(member, dimensions, selectedItems, selectedMetrics, theme, index)) {
							
							//if its not the last dimension
							if(index != dimensions.size() - 1) {
								if(selectedItems.contains(obj)) {
									actualMembers.add(member);
									exploreDimension(dimensions, selectedItems, selectedMetrics, theme, index + 1);
								}
							}
							
							else if(selectedItems.contains(obj)){
								actualMembers.add(member);
								//ajout
								try {
									addInDatabase(selectedMetrics, actualMembers, theme);
								} catch (Exception e) {
									addEndWell = false;
									e.printStackTrace();
								}
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
	}
	
	/**
	 * Look in member children
	 * @param topmember
	 * @param dimensions
	 * @param selectedItems
	 * @param selectedMetrics
	 * @param theme
	 * @param index
	 * @return
	 */
	private boolean getMemberChildren(TreeMember topmember, List<TreeDim> dimensions, List<TreeObject> selectedItems, HashMap<TreeMes, String> selectedMetrics, Theme theme, int index) {
		
		boolean res = false;
		
		for(Object obj : topmember.getChildren()) {
			
			if(obj instanceof TreeMember) {
				
				if(actualMembers.size() >= index + 1) {
					actualMembers.remove(index);
				}
				
				TreeMember member = (TreeMember) obj;
				
				boolean childExist = getMemberChildren(member, dimensions, selectedItems, selectedMetrics, theme, index);
				
				if(!childExist) {
					
					if(index != dimensions.size() - 1) {
						if(selectedItems.contains(obj)) {
							actualMembers.add(member);
							exploreDimension(dimensions, selectedItems, selectedMetrics, theme, index + 1);
							res = true;
						}
					}
					
					else if(selectedItems.contains(obj)){
						actualMembers.add(member);
						//ajout
						try {
							addInDatabase(selectedMetrics, actualMembers, theme);
						} catch (Exception e) {
							addEndWell = false;
							e.printStackTrace();
						}
						res = true;
					}
					
				}
				else {
					res = true;
				}
			}
		}
		
		return res;
	}
	
	private void addInDatabase(HashMap<TreeMes, String> selectedMetrics, List<TreeMember> members, Theme theme) throws Exception {
		//create the application
		int appId = -1;
		Application app = new Application();
		
		String name = getApplicationName(members);
		app.setName(name);
		app.setAdCreationDate(new Date());
		app.setAdBusinessComment("");
		
		if(manager.getApplicationByName(name) == null) {
			appId = manager.addApplication(app);
			app.setId(appId);
			
			FmApplication_Groups appGr = new FmApplication_Groups();
			appGr.setApplicationId(appId);
			appGr.setGroupId(fmGroupId);
			appGr.setCreationDate(new Date());
			
			manager.addApplicationGroup(appGr);
		}
		else {
			appId = manager.getApplicationByName(name).getId();
		}
		
		for(TreeMes measure : selectedMetrics.keySet()) {
		
			//create the metric
			Metric metric = null;
			if(!addedMeasures.keySet().contains(measure)) {
				int mesId = -1;
				metric = new Metric();
				metric.setName(measure.getName());
				metric.setMdBusinessDescription(measure.getName());
				metric.setMdGlIsCompteur(true);
				metric.setMdGlThemeId(theme.getId());
				metric.setMdCalculationType("Imported");
				metric.setMdTypeId(1);
				metric.setMdCalculationTimeFrame(selectedMetrics.get(measure));
				metric.setMdGlIsModif(true);
				
				Metric created = manager.getMetricByName(measure.getName()); 
				if(created == null) {
					mesId = manager.addMetric(metric);
				}
				else {
					mesId = created.getId();
				}
				
				addedMeasures.put(measure, mesId);
			}
			else {
				metric = manager.getMetricById(addedMeasures.get(measure));
			}
			
			int mesId = addedMeasures.get(measure);
			metric.setId(mesId);
			
			//create the association between application and metric			
			int assoId = -1;
			Assoc_Application_Metric asso = new Assoc_Application_Metric();
			asso.setApp_ID(appId);
			asso.setMetr_ID(mesId);
			asso.setName(name + "_" + measure.getName());
			
			Assoc_Application_Metric createdAsso = null;//manager.getAssoMetricAppByMetricAndAppIds(appId, mesId);
			if(createdAsso == null) {
				try {
					assoId = manager.addAssoc_Territoire_Metric(asso);
				} catch (Exception e) {
					addEndWell = false;
					e.printStackTrace();
				}
			}
			
			else {
				assoId = createdAsso.getId();
			}
			
			asso.setId(assoId);
			
			//create values
			
			try {
				createValues(app, metric, asso, members, selectedMetrics);
			} catch (Exception e) {
				addEndWell = false;
				e.printStackTrace();
			}
		}

		
		
		
		
	}

	private void createValues(Application app, Metric metric, Assoc_Application_Metric asso, List<TreeMember> members, HashMap<TreeMes, String> selectedMetrics) throws Exception {
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		
		//create the query
		OLAPMeasure measure = null;
		for(TreeMes mes : selectedMetrics.keySet()) {
			if(mes.getName().equals(metric.getName())) {
				measure = mes.getOLAPMeasure();
				break;
			}
		}
		
		String query = getQuery(measure, members, metric);
		
		//execute the query
		resultSet =  stmt.executeQuery(query);
		
		ResultSetMetaData rsmd = resultSet.getMetaData();
		
		while(resultSet.next()){
			List<Object> r = new ArrayList<Object>();
			
			for(int i = 0; i < rsmd.getColumnCount(); i++){
				r.add(resultSet.getObject(i + 1));
			}
			
			values.add(r);
		}
		resultSet.close();
		
		Date firstDate = null;
		Float previousValue = null;
		
		for(List<Object> value : values) {
			//create a metric value
			
			Float metricval = null;
			Date date = null;
			
			if(value.get(0) instanceof Timestamp) {
				date = (Date) value.get(0);
				metricval = Float.parseFloat(value.get(1).toString());
			}
			else {
				date = (Date) value.get(1);
				metricval = (Float.parseFloat(value.get(0).toString()));
			}
			
			if(firstDate == null) {
				firstDate = date;
				previousValue = metricval;
			}
			
			//test if the value is in the same period than the previous value
			else {
				if(isInSamePeriod(metric, firstDate, date)) {
					previousValue += metricval;
					
				}
				
				//add the previous value if the period change
				else {
					MetricValues val = new MetricValues();
					val.setMvGlAssoc_ID(asso.getId());
					val.setMvPeriodDate(firstDate);
					val.setMvValue(previousValue);
					val.setMvCreationDate(new Date());
					
					//test if the value already exists and if the user want to update it or not
					MetricValues createdValue = manager.getValuesForAssocIdPeridDate(asso.getId(), firstDate);
					if(createdValue != null && updateValues) {
						createdValue.setMvValue(previousValue);
						manager.updateMetricValue(createdValue);
					}
					
					else {
						manager.addMetricValue(val);
					}
					
					firstDate = date;
					previousValue = metricval;
				}
			}
			
			
		}
		
		//for the last value
		if(previousValue != null && firstDate != null) {
			MetricValues val = new MetricValues();
			val.setMvGlAssoc_ID(asso.getId());
			val.setMvPeriodDate(firstDate);
			val.setMvValue(previousValue);
			val.setMvCreationDate(new Date());
			
			manager.addMetricValue(val);
		}
		
	}

	/**
	 * Return true if dates are in the same period
	 * @param metric
	 * @param firstDate
	 * @param date
	 * @return
	 */
	private boolean isInSamePeriod(Metric metric, Date firstDate, Date date) {
		
		//if its a yearly period
		if(metric.getMdCalculationTimeFrame().equals(IConstants.PERIODS[IConstants.PERIOD_YEARLY])) {
			
			GregorianCalendar firstCal = new GregorianCalendar();
			firstCal.setTime(firstDate);
			GregorianCalendar actCal = new GregorianCalendar();
			actCal.setTime(date);
			
			if(firstCal.get(GregorianCalendar.YEAR) == actCal.get(GregorianCalendar.YEAR)) {
				return true;
			}
		}
		
		//if its a biannual period
		else if(metric.getMdCalculationTimeFrame().equals(IConstants.PERIODS[IConstants.PERIOD_BIANNUAL])) {
			
			GregorianCalendar firstCal = new GregorianCalendar();
			firstCal.setTime(firstDate);
			GregorianCalendar actCal = new GregorianCalendar();
			actCal.setTime(date);
			
			if(firstCal.get(GregorianCalendar.YEAR) == actCal.get(GregorianCalendar.YEAR)) {
				if((firstCal.get(GregorianCalendar.MONTH) < 6 && actCal.get(GregorianCalendar.MONTH) < 6) || (firstCal.get(GregorianCalendar.MONTH) > 5 && actCal.get(GregorianCalendar.MONTH) > 5)) {
					return true;
				}
			}
		}
		
		//if its a quarterly period
		else if(metric.getMdCalculationTimeFrame().equals(IConstants.PERIODS[IConstants.PERIOD_QUARTERLY])) {
			
			GregorianCalendar firstCal = new GregorianCalendar();
			firstCal.setTime(firstDate);
			GregorianCalendar actCal = new GregorianCalendar();
			actCal.setTime(date);
			
			if(firstCal.get(GregorianCalendar.YEAR) == actCal.get(GregorianCalendar.YEAR)) {
				if(firstCal.get(GregorianCalendar.MONTH) < 3 && actCal.get(GregorianCalendar.MONTH) < 3) {
					return true;
				}
				else if((firstCal.get(GregorianCalendar.MONTH) < 6 && actCal.get(GregorianCalendar.MONTH) < 6) && (firstCal.get(GregorianCalendar.MONTH) > 2 && actCal.get(GregorianCalendar.MONTH) > 2)) {
					return true;
				}
				else if((firstCal.get(GregorianCalendar.MONTH) < 9 && actCal.get(GregorianCalendar.MONTH) < 9) && (firstCal.get(GregorianCalendar.MONTH) > 5 && actCal.get(GregorianCalendar.MONTH) > 5)) {
					return true;	
				}
				else if(firstCal.get(GregorianCalendar.MONTH) > 8 && actCal.get(GregorianCalendar.MONTH) > 8) {
					return true;
				}
			}
		}
		
		//if its a monthly period
		else if(metric.getMdCalculationTimeFrame().equals(IConstants.PERIODS[IConstants.PERIOD_MONTHLY])) {
			
			GregorianCalendar firstCal = new GregorianCalendar();
			firstCal.setTime(firstDate);
			GregorianCalendar actCal = new GregorianCalendar();
			actCal.setTime(date);
			
			if(firstCal.get(GregorianCalendar.YEAR) == actCal.get(GregorianCalendar.YEAR)) {
				if(firstCal.get(GregorianCalendar.MONTH) == actCal.get(GregorianCalendar.MONTH)) {
					return true;
				}
			}
		}
		
		//if its a weekly period
		else if(metric.getMdCalculationTimeFrame().equals(IConstants.PERIODS[IConstants.PERIOD_WEEKLY])) {
			
			GregorianCalendar firstCal = new GregorianCalendar();
			firstCal.setTime(firstDate);
			GregorianCalendar actCal = new GregorianCalendar();
			actCal.setTime(date);
			
			if(firstCal.get(GregorianCalendar.YEAR) == actCal.get(GregorianCalendar.YEAR) && firstCal.get(GregorianCalendar.WEEK_OF_YEAR) == actCal.get(GregorianCalendar.WEEK_OF_YEAR)) {
				return true;
			}
		}
		
		//if its a daily period
		else if(metric.getMdCalculationTimeFrame().equals(IConstants.PERIODS[IConstants.PERIOD_DAYLY])) {
			
			GregorianCalendar firstCal = new GregorianCalendar();
			firstCal.setTime(firstDate);
			GregorianCalendar actCal = new GregorianCalendar();
			actCal.setTime(date);
			
			if((firstCal.get(GregorianCalendar.YEAR) == actCal.get(GregorianCalendar.YEAR)) && (firstCal.get(GregorianCalendar.MONTH) == actCal.get(GregorianCalendar.MONTH)) && (firstCal.get(GregorianCalendar.DATE) == actCal.get(GregorianCalendar.DATE))) {
				return true;
			}
		}
		
		return false;
	}

	private String getQuery(OLAPMeasure measure, List<TreeMember> members, Metric metric) {
		
		String select = "Select " + measure.getAggregator() + "(" + measure.getOrigin().getParent().getName() + "." + measure.getOrigin().getName() + ")";
		select += "," + cube.getFactDataObject().getName() + "." + this.dateField;
		
		String from = "";
		
		for(DataObject obj : cube.getDataSource().getDataObjects()) {
			from += "," + obj.getName();
		}
		
		from = "From " + from.substring(1);
		
		String where = "";
		
		for(OLAPRelation relation : fasdModel.getRelations()) {
			where += " and " + relation.getLeftObject().getName() + "." + relation.getLeftObjectItem().getName() + relation.getOperator() + relation.getRightObject().getName() + "." + relation.getRightObjectItem().getName();
		}
		
		where = "Where" + where.substring(4);
		
		for(TreeMember mem : members) {
			where += " and " + mem.getLevel().getItem().getParent().getName() + "." + mem.getLevel().getItem().getName() + " = '" + mem.getName() + "'";
		}
		
		String groupBy = getQueryGroupBy(metric);
		
		String query = select + " " + from + " " + where + " " + groupBy;
		
		return query;
 	} 
	
	private String getQueryGroupBy(Metric metric) {
		return "group by " + cube.getFactDataObject().getName() + "." + this.dateField;
	}

	private String getApplicationName(List<TreeMember> members) {
		String name = "";
		
		for(TreeMember member : members) {
			
			if(!name.equalsIgnoreCase("")) {
				name += "_";
			}
			
			name += getMemberName(member, "");
			
		}
		
		return name;
	}
	
	public String getMemberName(TreeMember member, String name) {
		if(member.getParent() instanceof TreeMember) {
			return getMemberName((TreeMember) member.getParent(), name) + "_" + member.getName();
		}
		else {
			return member.getName();
		}
	}

	public List<String> getFields() throws Exception {
		//create the connection
		Connection con = null;
		ResultSet resultSet;
		
		if (cube == null){
			throw new Exception ("Cube not found in model");
		}
		
		try {
			con = cube.getDataSource().getDriver().getConnection().getConnection().getJdbcConnection();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (con == null){
			throw new Exception("Unable to create SQL Connection");
		}
		
		resultSet = con.getMetaData().getColumns(null, null, cube.getFactDataObject().getName(), null);
		
		List<String> names = new ArrayList<String>();
		while(resultSet.next()) {
			names.add(resultSet.getString("COLUMN_NAME"));
		}
		
		resultSet.close();
		con.close();
		
		return names;
	}

	
	
	
}
