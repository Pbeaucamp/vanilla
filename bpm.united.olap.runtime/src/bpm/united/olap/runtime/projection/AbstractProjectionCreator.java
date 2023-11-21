package bpm.united.olap.runtime.projection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.CalculatedMeasure;
import bpm.united.olap.api.model.impl.DateLevel;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.model.impl.ProjectionMeasureCondition;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.RuntimeFactory;
import bpm.united.olap.api.runtime.calculation.ICalculation;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.parser.MdxEvaluator;
import bpm.united.olap.runtime.parser.calculation.CalculationHelper;
import bpm.united.olap.runtime.query.OdaQueryRunner;
import bpm.united.olap.runtime.query.QueryHelper;
import bpm.united.olap.runtime.tools.DimensionUtils;
import bpm.united.olap.runtime.tools.OdaInputOverrider;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.logging.IVanillaLogger;

import com.ibm.icu.text.SimpleDateFormat;

public abstract class AbstractProjectionCreator implements IProjectionCreator {

	protected Projection projection;
	protected OdaInput input;
	protected IVanillaLogger logger;
	protected ICubeInstance cubeInstance;
	protected IRuntimeContext runtimeContext;
	protected ProjectionMeasure measure;
	protected int fileSize;
	
	protected Dimension timeDimension;
	protected Level projectionLevel;
	protected Date minimumExistingDate;
	protected Date maximumExistingDate;
	
	@Override
	public ProjectionDescriptor createProjection(ICubeInstance cubeInstance, IRuntimeContext runtimeContext) throws Exception {
		this.cubeInstance = cubeInstance;
		this.runtimeContext = runtimeContext;
		
		String s = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.united.olap.runtime.projection.file.bufferSize");
		try {
			fileSize = Integer.parseInt(s);
		} catch (Exception e1) {
			logger.warn("The fileBuffer is not defined or is not an integer. It will be 3000", e1);
			fileSize = 3000;
		}
		
		List<CrossMembersValues> crossMembers = createCrossMembersValues();
		
		
		//for each element of the matrix, find the linear coefficients
		logger.debug("calculate coeffs");
		for(CrossMembersValues val : crossMembers) {

			findArraysForCoeffsCalcul(val, projectionLevel, minimumExistingDate, maximumExistingDate);
			
			calculAndSetCoeffs(val);
//			double[] results = findLinearCoeffs(val);
//			val.setCoefficients(results);
			val.clearArrays();
		}
		
		//for each new dates, calculate the extrapolated value
		Date startDate = projection.getStartDate();
		Date endDate = projection.getEndDate();
		
		double start = calculDoubleDate(startDate, projectionLevel);
		double end = calculDoubleDate(endDate, projectionLevel);
		
		logger.debug("calcul new values");
		
		List<CrossMembersValues> resultValues = new ArrayList<CrossMembersValues>();
		
		
		int nbNewLines = 0;
		int nbFile = 1;
		for(CrossMembersValues val : crossMembers) {
			double actualDate = start;
			
			for( ; actualDate <= end ; actualDate = incrementeDate(actualDate, projectionLevel)) {
				
				//create the new date member
				Date date = new Date((long) (actualDate * 1000 * 3600 * 24 - (3600 * 1000)));
				String newDateMember = createNewDateMember(date, timeDimension);
				
				List<String> crossMembs = new ArrayList<String>(val.getMemberUnames());
				crossMembs.add(0,newDateMember);
				
				CrossMembersValues resultVal = new CrossMembersValues(crossMembs);
				
				//calcul the new value
				if(val.getOnlyOneValue() != null) {
					double value = val.getOnlyOneValue(); 
					
					resultVal.addValue(date, value);
					resultVal.setHasOnlyOneValue();
				}
				else {
					double value = calculNewValue(val, actualDate);
					
					resultVal.addValue(date, value);
					resultVal.setHasOnlyOneValue();
				}
				
				resultValues.add(resultVal);
			}
			
			if(nbNewLines % 100 == 0) {
				logger.debug("treated cross : " + nbNewLines);
			}
			
			nbNewLines++;
			
			//We serialize the data every "fileSize" lines
			if(resultValues.size() >= fileSize) {
				ProjectionSerializationHelper.serialize(resultValues, projection, logger, nbFile, measure.getUname());
				resultValues.clear();
				nbFile++;
			}
		}
		
		logger.debug("Serialize the result");
		
		//serialize the new values
		ProjectionDescriptor desc = ProjectionSerializationHelper.serialize(resultValues, projection, logger, nbFile, measure.getUname());
		
		return desc;
	}
	
	protected abstract double calculNewValue(CrossMembersValues val, double actualDate) throws Exception;
	protected abstract void calculAndSetCoeffs(CrossMembersValues val) throws Exception;

	protected List<CrossMembersValues> createCrossMembersValues() throws Exception {
		logger.debug("---------Create forecast data------------");
		
		//find the date dimension
		for(Dimension d : cubeInstance.getCube().getDimensions()) {
			if(d.isDate()) {
				timeDimension = d;
				for(Hierarchy hiera : d.getHierarchies()) {
					for(Level l : hiera.getLevels()) {
						if(l.getUname().equals(projection.getProjectionLevel())) {
							projectionLevel = l;
							break;
						}
					}
				}
				break;
			}
		}
		if(timeDimension == null || projectionLevel == null) {
			throw new Exception("The cube doesn't have any date dimension or the projectionLevel couldn't be found. Impossible to extrapolate data without one.");
		}
		
		Date minimumDate = null;
		Date maximumDate = null;
		if(measure.getConditions() != null && measure.getConditions().size() > 0) {
			for(ProjectionMeasureCondition cond : measure.getConditions()) {
				Date conditionDate = null;

				Member mem = cond.getMembers().get(0);
				mem.setParentHierarchy(timeDimension.getHierarchies().get(0));
				mem.setParentLevel(projectionLevel);
				
				conditionDate = createDateFromMember(mem, timeDimension, cubeInstance, runtimeContext, null);
				
				if(cond.getFormula().equals(Projection.EXTRAPOLATION_START_MEMBER)) {
					minimumDate = conditionDate;
				}
				else if(cond.getFormula().equals(Projection.EXTRAPOLATION_END_MEMBER)) {
					maximumDate = conditionDate;
				}
			}
		}
		
		//find the last level for each dimensions
		HashMap<Level, List<Member>> lastLevels = new HashMap<Level, List<Member>>();
		for(Dimension dim : cubeInstance.getCube().getDimensions()) {
			for(Hierarchy hiera : dim.getHierarchies()) {
				Level last = hiera.getLevels().get(hiera.getLevels().size() - 1);
				if(!last.getUname().startsWith("[PROJECTION_")) {
					lastLevels.put(last, cubeInstance.getHierarchyExtractor(hiera).getLevelMembers(last, runtimeContext));
				}
			}
		}
		
		//execute the query
		
		QueryHelper helper = new QueryHelper(logger, runtimeContext);
		List<CrossMembersValues> crossMembers = new ArrayList<CrossMembersValues>();
		
		IQuery query = getOdaQuery(cubeInstance.getFactTable().getParent(), cubeInstance.getFactTable().getQueryText(), runtimeContext);
		IResultSet rs = OdaQueryRunner.runQuery(query);
		
		logger.debug("Query executed");
		
		long lastMemberTime = 0;
		long calculTime = 0;
		long crossTime = 0;
		
		int nbLine = 0;
		
		LOOK:while(rs.next()) {
			
			List<ElementDefinition> members = new ArrayList<ElementDefinition>();
			
			Date d = new Date();
			//find the last members
			for(Level lvl : lastLevels.keySet()) {
				int lvlIndex = cubeInstance.getDataLocator().getResultSetIndex(lvl);
				
				Object fk = rs.getString(lvlIndex + 1);
				
				int levelIndex = lvl.getParentHierarchy().getLevels().indexOf(lvl);
				
				//find the member uname
				String memberUname = "";
				while(levelIndex > -1) {
					String memberName = cubeInstance.getHierarchyExtractor(lvl.getParentHierarchy()).getMemberName(fk, levelIndex, runtimeContext);
					memberUname = "[" + memberName + "]." + memberUname;
					levelIndex--;
				}
				
				memberUname = cubeInstance.getHierarchyExtractor(lvl.getParentHierarchy()).getRootMember().getUname() + "." + memberUname;
				memberUname = memberUname.substring(0, memberUname.length() - 1);
				
				//find the member by his uname
				Member finded = cubeInstance.getHierarchyExtractor(lvl.getParentHierarchy()).getMember(memberUname, runtimeContext);
				
				if(finded == null) {
					logger.warn("Not finded for : " + memberUname);
				}
				
				members.add(finded);
				
			}
			
			lastMemberTime += new Date().getTime() - d.getTime();
			
			//for each measure get the value and create a cross member element
			for(Measure mes : cubeInstance.getCube().getMeasures()) {
				
				if(mes.getUname().equals(measure.getUname())) {
				
					d = new Date();
					//find the value
					DataCell cell = createFakeDatacell(mes, members);
					HashMap<String, Double> values = helper.findMeasureValues(mes, cell, rs, cubeInstance, cubeInstance.getDataLocator());
					if(values != null) {
						for(String uname : values.keySet()) {
							cell.addValue(uname, values.get(uname));
						}
						ICalculation c = CalculationHelper.getCalculation(cell, mes, members, logger);
						c.makeCalculDuringQuery(false);
						c.makeCalcul();
					}
					
					Double val = cell.getResultValue();
					calculTime += new Date().getTime() - d.getTime();
					//find the date
					Date date = null;
					
					for(ElementDefinition def : members) {
						if(def instanceof Member) {
							Member mem = (Member) def;
							if(mem.getParentHierarchy().getParentDimension().getUname().equals(timeDimension.getUname())) {
								date = createDateFromMember(mem, timeDimension, cubeInstance, runtimeContext, projectionLevel);
								
								//FIXME : check if there's filters and if the member is ok
								boolean memberOK = false;
								if(minimumDate != null) {
									if(minimumDate.compareTo(date) <= 0) {
										memberOK = true;
									}
								}
								else {
									memberOK = true;
								}
								if(maximumDate != null) {
									if(date.compareTo(maximumDate) <= 0) {
										memberOK = true;
									}
									else {
										memberOK = false;
									}
								}
								if(!memberOK) {
									continue LOOK;
								}
								
								if(minimumExistingDate == null) {
									minimumExistingDate = date;
								}
								else {
									if(minimumExistingDate.compareTo(date) > 0) {
										minimumExistingDate = date;
									}
								}
								if(maximumExistingDate == null) {
									maximumExistingDate = date;
								}
								else {
									if(maximumExistingDate.compareTo(date) < 0) {
										maximumExistingDate = date;
									}
								}
								break;
							}
						}
					}
	
					d = new Date();
					
					CrossMembersValues cross = lookForCrossMember(members, mes, crossMembers, timeDimension);
					cross.addValue(date, val);
					
					crossTime += new Date().getTime() - d.getTime();
				}
			}
			
			if(nbLine % 100 == 0) {
				logger.debug(nbLine + " treated");
				
				logger.debug("members : " + lastMemberTime);
				logger.debug("calculs : " + calculTime);
				logger.debug("cross : " + crossTime);
				lastMemberTime = 0;
				calculTime = 0;
				crossTime = 0;
			}
			
			nbLine++;
		}
		try {
			rs.close();
			query.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return crossMembers;
	}
	
	protected void findArraysForCoeffsCalcul(CrossMembersValues val, Level projectionLevel, Date minimumExistingDate, Date maximumExistingDate) {
		
		List<Date> dates = val.getDates();
		
		Collections.sort(dates);
		
		List<Double> resDates = new ArrayList<Double>();
		List<Double> resValues = new ArrayList<Double>();
		
		if(projectionLevel instanceof DateLevel) {
			//FIXME:
		}
		else {
			int levelIndex = projectionLevel.getParentHierarchy().getLevels().indexOf(projectionLevel);
			Date actual = new Date(minimumExistingDate.getTime());
			
			for( ; actual.compareTo(maximumExistingDate) <= 0 ; ) {
				
				int index = dates.indexOf(actual);
				
				double dblDate = actual.getTime();
				
				resDates.add(calculDoubleDate(new Date((long)dblDate), projectionLevel));
				
				if(index > - 1) {
					resValues.add(val.getValues().get(index));
				}
				else {
					resValues.add(0.0);
				}
				
				if(levelIndex == 0) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(actual);
					cal.add(Calendar.YEAR, 1);
					cal.set(Calendar.MONTH, 0);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					actual = cal.getTime();
				}
				else if(levelIndex == 1) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(actual);
					cal.add(Calendar.MONTH, 1);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					actual = cal.getTime();
				}
				else if(levelIndex == 2) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(actual);
					cal.add(Calendar.DAY_OF_MONTH, 1);
					actual = cal.getTime();
				}
			}
		}
		
		double[] dateAsArray = new double[(resDates.size())];
		for(int i = 0 ; i < resDates.size() ; i++) {
			dateAsArray[i] = resDates.get(i);
		}		
		
		double[] valuesAsArray = new double[(resValues.size())];
		for(int i = 0 ; i < resValues.size() ; i++) {
			valuesAsArray[i] = resValues.get(i);
		}
		
		val.setArrays(dateAsArray, valuesAsArray);
	}
	
	protected double calculDoubleDate(Date date, Level projectionLevel) {
		double doubleDate = 0;
		//FIXME : Look for the timeZone here
		doubleDate = (date.getTime() + (3600 * 1000)) / 1000 / 3600 /24;
		
		//only keep days from the date
		doubleDate = (Math.round(doubleDate * Math.pow(10, 0))) / (Math.pow(10, 0));
		return doubleDate;
	}
	
	protected IQuery getOdaQuery(Datasource parent, String queryText, IRuntimeContext runtimeContext) throws Exception {
		initOdaInput(parent, queryText);
		input = OdaInputOverrider.override(input, runtimeContext);
		input.setQueryText(queryText);
		IQuery queryOda = bpm.dataprovider.odainput.consumer.QueryHelper.buildquery(input);
			
		if(input.getOdaExtensionDataSourceId().equals("org.eclipse.birt.report.data.oda.jdbc")) {
			if(((String)input.getDatasourcePublicProperties().get("odaURL")).contains("mysql")) {
				queryOda.setProperty("rowFetchSize", 10000 + "");
			}
			else {
				queryOda.setProperty("rowFetchSize", 10000 + "");
			}
		}
			
		return queryOda;
	
	}
	
	protected void initOdaInput(Datasource datasource, String queryText){
		
		input = new OdaInput();
		input.setDatasourcePublicProperties(datasource.getPublicProperties());
		input.setDatasourcePrivateProperties(datasource.getPrivateProperties());
		input.setOdaExtensionDataSourceId(datasource.getDatasourceExtensionId());
		input.setName(datasource.getDatasourceExtensionId());
		input.setQueryText(queryText);
		
	}

	
	protected DataCell createFakeDatacell(Measure mes, List<ElementDefinition> members) throws Exception {
		DataCell cell = RuntimeFactory.eINSTANCE.createDataCell();
		DataCellIdentifier2 identifier = RuntimeFactory.eINSTANCE.createDataCellIdentifier2();
		identifier.setIntersections(members);
		
		if(mes instanceof CalculatedMeasure) {
			MdxEvaluator eval = new MdxEvaluator(null, cubeInstance, logger, runtimeContext);
			eval.findCalculatedMeasureElements(mes);
		}
		
		identifier.setMeasure(mes);
		cell.setIdentifier2(identifier);
		return cell;
	}
	
	protected double[] findDateArray(List<Date> dates, Level projectionLevel) {
		double[] result = new double[dates.size()];
		Collections.sort(dates);
		
		int i = 0;
		for(Date date : dates) {
			result[i] = calculDoubleDate(date, projectionLevel);
			i++;
		}
		
		return result;
	}
	
	protected CrossMembersValues lookForCrossMember(List<ElementDefinition> defs, Measure mes, List<CrossMembersValues> existings, Dimension timeDimension) {
		CrossMembersValues result = null;
		
		List<String> members = new ArrayList<String>();
		for(ElementDefinition def : defs ) {
			if(!timeDimension.getUname().equals(((Member)def).getParentHierarchy().getParentDimension().getUname())) {
				members.add(def.getUname());				
			}
		}
		members.add(mes.getUname());
		
		for(CrossMembersValues c : existings) {
			if(c.isEquals(members)) {
				result = c;
				break;
			}
		}
		
		if(result == null) {
			result = new CrossMembersValues(members);
			existings.add(result);
		}
		
		return result;
	}
	
	protected Date createDateFromMember(Member mem, Dimension timeDimension, ICubeInstance cubeInstance, IRuntimeContext runtimeContext, Level projectionLevel) throws Exception {
		
		//FIXME : Try to find a date using the projectionLevel
		
		if(timeDimension.isIsOneColumnDate()) {
			List<Object> fks = cubeInstance.getHierarchyExtractor(mem.getParentHierarchy()).getMemberForeignKeys(mem, runtimeContext);
			if(fks != null && fks.size() > 0) {
				String pattern = ((DateLevel)mem.getParentLevel()).getDatePattern();
				SimpleDateFormat form = new SimpleDateFormat(pattern);
				return form.parse(fks.toString());
			}
		}
		else {
			if(timeDimension.getHierarchies().get(0).getLevels().size() == 3) {
				int levelIndex = 0;
				if(projectionLevel != null) {
					levelIndex = projectionLevel.getParentHierarchy().getLevels().indexOf(projectionLevel);
				}
				else {
					levelIndex = timeDimension.getHierarchies().get(0).getLevels().size() - 1;
				}
				String[] patternPart = new String[] {"yyyy","/MM","/dd"};
				String[] memberPart = mem.getUname().split("\\]\\.\\[");
				String pattern = "";
				String date = "";
				
				for(int i = 0; i <= levelIndex ; i++) {
					date += memberPart[i+2].replace("[", "").replace("]", "") + "/";
					pattern += patternPart[i];
				}
				
				SimpleDateFormat form = new SimpleDateFormat(pattern);
				String res = date.substring(0, date.length() - 1);
				return form.parse(res);
			}
		}
		
		return null;
	}
	
	protected String createNewDateMember(Date date, Dimension timeDimension) {
		if(timeDimension.isIsOneColumnDate()) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			String mbUname = "";
			for(Level lvl : timeDimension.getHierarchies().get(0).getLevels()) {
				DateLevel l = (DateLevel) lvl;
				
				String part = DimensionUtils.findDatePart(l.getDatePart(), cal);
				mbUname = "[" + part + "]." + mbUname;
			}
			mbUname = cubeInstance.getHierarchyExtractor(timeDimension.getHierarchies().get(0)).getRootMember().getUname() + "." + mbUname;
			mbUname = mbUname.substring(0,mbUname.length() - 1);
//			cubeInstance.getHierarchyExtractor(timeDimension.getHierarchies().get(0)).getm
			return mbUname;
		}
		else if(timeDimension.getHierarchies().get(0).getLevels().size() == 3) {
			SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd");
			String stringDate = form.format(date);
			String[] part = stringDate.split("/");
			
			String mbUname =  cubeInstance.getHierarchyExtractor(timeDimension.getHierarchies().get(0)).getRootMember().getUname() + 
				".[" + part[0] + "].[" + part[1] + "].[" + part[2] + "]";
			return mbUname;
		}
		return null;
	}
	
	/**
	 * Increment the date by using the projection
	 * (Add one month if the projectionLevel correspond to month) 
	 * @param date
	 * @param projectionLevel
	 * @return
	 */
	protected double incrementeDate(double date, Level projectionLevel) {
		Calendar cal = Calendar.getInstance();
		Date d = new Date((long) (date * 1000 * 3600 * 24 - (3600 * 1000)));
		cal.setTimeInMillis(d.getTime());
		
		int index = projectionLevel.getParentHierarchy().getLevels().indexOf(projectionLevel);
		if(index == 0) {
			cal.add(Calendar.YEAR, 1);
		}
		else if(index == 1) {
			cal.add(Calendar.MONTH, 1);
		}
		else if(index == 2) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return calculDoubleDate(cal.getTime(), projectionLevel);
	}
}
