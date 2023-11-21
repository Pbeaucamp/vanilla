package bpm.united.olap.runtime.tools;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.impl.ValueResultCell;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.HierarchyExtractor;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.api.tools.AlphanumComparator;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.query.FactoryQueryHelper;
import bpm.united.olap.runtime.query.IQueryHelper;
import bpm.united.olap.runtime.query.ProjectionQueryHelper;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * An util class which helps with treatment on dimensions, levels, members, ...
 * @author Marc Lanquetin
 *
 */
public class DimensionUtils {

	private static final String DATE_YEAR = "year";
	private static final String DATE_SEMESTER_NAME = "semester name";
	private static final String DATE_SEMESTER_NUMBER = "semester number";
	private static final String DATE_QUARTER_NAME = "quarter name";
	private static final String DATE_QUARTER_NUMBER = "quarter number";
	private static final String DATE_MONTH_NUMBER = "month number";
	private static final String DATE_MONTH_NAME = "month name";
	private static final String DATE_WEEK_YEAR = "week of year";
	private static final String DATE_WEEK_MONTH = "week of month";
	private static final String DATE_DAY_YEAR = "day of year";
	private static final String DATE_DAY_MONTH = "day of month";
	private static final String DATE_DAY_WEEK = "day of week";
	private static final String DATE_DAY_NAME = "day name";
	private static final String DATE_HOUR = "hour";
	private static final String DATE_MINUTE = "minute";
	private static final String DATE_SECOND = "second";
	
	private static final String[] DATE_CATEGORIES = {"Year", "Semester", "Quarter", "Month", "Week", "Day", "Hour", "Minute", "Second"};
	
	private static final String[][] DATE_SUBCATEGORIES = {
		{DATE_YEAR},
		{DATE_SEMESTER_NAME, DATE_SEMESTER_NUMBER},
		{DATE_QUARTER_NAME,DATE_QUARTER_NUMBER},
		{DATE_MONTH_NUMBER,DATE_MONTH_NAME},
		{DATE_WEEK_YEAR,DATE_WEEK_MONTH},
		{DATE_DAY_YEAR,DATE_DAY_MONTH,DATE_DAY_WEEK,DATE_DAY_NAME},
		{DATE_HOUR},
		{DATE_MINUTE},
		{DATE_SECOND}};
	
	
	private ICubeInstance cubeInstance;
	public DimensionUtils(ICubeInstance cubeInstance){
		this.cubeInstance = cubeInstance;
	}
	
	/**
	 * Search for a word on all hierarchies, levels, members of a dimension
	 * @param word the word to look for
	 * @param level a filter level (search on the whole dimension if it's null)
	 * @param schema
	 * @param server
	 * @param helper
	 * @return A list of uname which correspond to the search
	 * @throws Exception
	 */
	public List<String> searchDimensions(String word, String level, ICacheServer server, IRuntimeContext runtimeCtx) throws Exception {
		
		List<String> results = new ArrayList<String>();
		
		for(Dimension d : cubeInstance.getCube().getDimensions()){
			
			for(Hierarchy h : d.getHierarchies()){
				
				if(level == null || level.startsWith(h.getUname())) {

					HierarchyExtractor extractor = cubeInstance.getHierarchyExtractor(h);
					
					results.addAll(extractor.getMembersWith(word, level, runtimeCtx));
				}

			}
		}

		Collections.sort(results, new AlphanumComparator());
		return results;
	}
	
	/**
	 * Sort members
	 * @param members
	 * @return
	 */
	public List<Member> sortMembers(List<Member> members) {
		
		Collections.sort(members, new MemberComparator());
		return members;
	}
	
	private class MemberComparator implements Comparator<Member> {

		private AlphanumComparator comparator = new AlphanumComparator();
		
		@Override
		public int compare(Member o1, Member o2) {
			return comparator.compare(o1.getUname(), o2.getUname());
		}
	}
	
	public Member findLastMember(List<ElementDefinition> elements, boolean isLast, String dimension, String level, IQueryHelper helper, IRuntimeContext runtimeCtx) throws Exception {
		for(ElementDefinition def : elements) {
			if(def instanceof Member) {
				Member mem = (Member) def;
				if(mem.getParentLevel().getParentHierarchy().getParentDimension().getName().equals(dimension)) {
					
					Level lastLevel = null;
					for(Level lvl : mem.getParentLevel().getParentHierarchy().getLevels()) {
						if(level.equals(lvl.getUname())) {
							lastLevel = lvl;
							break;
						}
					}
					
					Level actualLevel = mem.getParentLevel().getSubLevel();
					List<Member> members = null;
//					List<String> parentMembers = new ArrayList<String>();
//					String[] unameParts = mem.getUname().replace("[","").replace("]","").split("\\.");
//					for(int i = 2 ; i < unameParts.length ; i++) {
//						parentMembers.add(unameParts[i]);
//					}
					
					while(actualLevel != lastLevel && actualLevel != null) {
						members = new ArrayList<Member>(cubeInstance.getHierarchyExtractor(actualLevel.getParentHierarchy()).getChilds(mem, runtimeCtx));
//						members = helper.findMembers(actualLevel, null, parentMembers);
						if(members == null || members.size() == 0) {
							break;
						}
						
						Member lastMem = null;
						if(isLast) {
							lastMem = members.get(members.size() - 1);
						}
						else {
							lastMem = members.get(0);
						}
//						parentMembers.add(lastMem.getName());
						actualLevel = actualLevel.getSubLevel();
					}
					
					if(actualLevel == null) {
						continue;
					}
					
//					members = helper.findMembers(actualLevel, null, parentMembers);
					members = new ArrayList<Member>(cubeInstance.getHierarchyExtractor(actualLevel.getParentHierarchy()).getChilds(mem, runtimeCtx));
					if (members != null && members.size() != 0) {
						Member lastMem = null;
						
						if (isLast) {
							lastMem = members.get(members.size() - 1);
						} 
						else {
							lastMem = members.get(0);
						}
						
						lastMem.setIsVisible(false);
						
						return lastMem;
						
					}
				}
			}
		}
		return null;
	}

	/**
	 * Drillthrough
	 * @param schema
	 * @param cell the selected cell
	 * @param logger
	 * @param cacheServer
	 * @return an OlapResult with the first as column names and next lines as values
	 * @throws Exception 
	 */
	public OlapResult drillthrough(Schema schema, ValueResultCell cell, IVanillaLogger logger, ICacheServer cacheServer, IRuntimeContext runtimeContext) throws Exception {
		
		IQueryHelper helper = FactoryQueryHelper.getQueryHelper(logger, runtimeContext, cubeInstance.getFactTable().getParent().getDatasourceExtensionId());
		helper.setCacheServer(cacheServer);
		return helper.advancedDrillthrough(cubeInstance, cell.getDataCellId());
	}

//	/**
//	 * Create all node elements from an unique date column
//	 * @param dateItem
//	 * @param levels
//	 * @return
//	 */
//	public List<NodeId> findOneColumnDateNodes(Date dateItem, List<Level> levels) {
//		List<NodeId> nodes = new ArrayList<NodeId>();
//		
//		GregorianCalendar calendar = new GregorianCalendar();
//		calendar.setTime(dateItem);
//		
//		for(Level lvl : levels) {
//			if(lvl instanceof DateLevel) {
//				DateLevel dateLvl = (DateLevel) lvl;
//				String part = dateLvl.getDatePart();
//				String value = findDatePart(part, calendar);
//				
//				NodeId node = RuntimeFactory.eINSTANCE.createNodeId();
//				node.setId(part);
//				node.setValue(value);
//				nodes.add(node);
//				
//				if(dateLvl.getDateOrder() !=null && !dateLvl.getDateOrder().equals("")) {
//					NodeId nodeO = RuntimeFactory.eINSTANCE.createNodeId();
//					nodeO.setId(dateLvl.getDateOrder());
//					nodeO.setValue(findDatePart(dateLvl.getDateOrder(), calendar));
//					nodes.add(nodeO);
//				}
//			}
//		}
//		
//		return nodes;
//	}

	/**
	 * Find a part in a date
	 * @param part
	 * @param calendar
	 * @return
	 */
	public static String findDatePart(String part, GregorianCalendar calendar) {
		if(part.equals(DATE_YEAR)) {
			return calendar.get(GregorianCalendar.YEAR)+"";
		}
		
		
		//Semester
		else if(part.equals(DATE_SEMESTER_NAME) || part.equals(DATE_SEMESTER_NUMBER)) {
			if((calendar.get(GregorianCalendar.MONTH) + 1) <= 6) {
				if(part.equals(DATE_SEMESTER_NAME)) {
					return "1st semester";
				}
				else if(part.equals(DATE_SEMESTER_NUMBER)) {
					return "1";
				}
			}
			else {
				if(part.equals(DATE_SEMESTER_NAME)) {
					return "2nd semester";
				}
				else if(part.equals(DATE_SEMESTER_NUMBER)) {
					return "2";
				}
			}
		}
		
		//Quarter
		else if(part.equals(DATE_QUARTER_NUMBER) || part.equals(DATE_QUARTER_NAME)) {
			if((calendar.get(GregorianCalendar.MONTH) + 1) <= 3) {
				if(part.equals(DATE_QUARTER_NUMBER)) {
					return "1";
				}
				else if(part.equals(DATE_QUARTER_NAME)) {
					return "1st Quarter";
				}
			}
			else if((calendar.get(GregorianCalendar.MONTH) + 1) <= 6) {
				if(part.equals(DATE_QUARTER_NUMBER)) {
					return "2";
				}
				else if(part.equals(DATE_QUARTER_NAME)) {
					return "2nd Quarter";
				}
			}
			else if((calendar.get(GregorianCalendar.MONTH) + 1) <= 9) {
				if(part.equals(DATE_QUARTER_NUMBER)) {
					return "3";
				}
				else if(part.equals(DATE_QUARTER_NAME)) {
					return "3rd Quarter";
				}
			}
			else {
				if(part.equals(DATE_QUARTER_NUMBER)) {
					return "4";
				}
				else if(part.equals(DATE_QUARTER_NAME)) {
					return "4th Quarter";
				}
			}
		}
		
		//Month
		else if(part.equals(DATE_MONTH_NAME) || part.equals(DATE_MONTH_NUMBER)) {
			if(part.equals(DATE_MONTH_NAME)) {
				return new DateFormatSymbols().getMonths()[calendar.get(GregorianCalendar.MONTH)];
			}
			else if(part.equals(DATE_MONTH_NUMBER)) {
				int m = calendar.get(GregorianCalendar.MONTH) + 1;
				if(m < 10) {
					return "0" + m;
				}
				return m+"";
			}
		}
		
		//week
		else if(part.equals(DATE_WEEK_YEAR) || part.equals(DATE_WEEK_MONTH)) {
			if(part.equals(DATE_WEEK_YEAR)) {
				return calendar.get(GregorianCalendar.WEEK_OF_YEAR) + "";
			}
			else if(part.equals(DATE_WEEK_MONTH)) {
				return calendar.get(GregorianCalendar.WEEK_OF_MONTH) + "";
			}
		}
		
		//day
		else if(part.equals(DATE_DAY_MONTH) || part.equals(DATE_DAY_NAME) || part.equals(DATE_DAY_WEEK) || part.equals(DATE_DAY_YEAR)) {
			if(part.equals(DATE_DAY_MONTH)) {
				return calendar.get(GregorianCalendar.DAY_OF_MONTH)+"";
			}
			else if(part.equals(DATE_DAY_WEEK)) {
				return calendar.get(GregorianCalendar.DAY_OF_WEEK)+"";
			} 
			else if(part.equals(DATE_DAY_YEAR)) {
				return calendar.get(GregorianCalendar.DAY_OF_YEAR)+"";
			}
			else if(part.equals(DATE_DAY_NAME)) {
				return new DateFormatSymbols().getWeekdays()[calendar.get(GregorianCalendar.DAY_OF_WEEK)];
			}
		}
		
		//hour
		else if(part.equals(DATE_HOUR)) {
			return calendar.get(GregorianCalendar.HOUR_OF_DAY)+"";	
		}
		
		//minute
		else if(part.equals(DATE_MINUTE)) {
			return calendar.get(GregorianCalendar.MINUTE)+"";
		}
		
		//second
		else if(part.equals(DATE_SECOND)) {
			return calendar.get(GregorianCalendar.SECOND)+"";
		}
		return null;
	}

	public OlapResult drillthrough(Schema schema, ValueResultCell cell, IVanillaLogger logger, ICacheServer cacheServer, IRuntimeContext runtimeContext, Projection projection) throws Exception {
		IQueryHelper helper = FactoryQueryHelper.getQueryHelper(logger, runtimeContext, cubeInstance.getFactTable().getParent().getDatasourceExtensionId());
		helper.setCacheServer(cacheServer);
		
		if(projection.getType().equals(Projection.TYPE_EXTRAPOLATION)) {
			ProjectionQueryHelper pHelper = new ProjectionQueryHelper(logger, runtimeContext, projection, helper);
			pHelper.setCacheServer(cacheServer);
			return pHelper.advancedDrillthrough(cubeInstance, cell.getDataCellId(), projection);
		}
		
		return helper.advancedDrillthrough(cubeInstance, cell.getDataCellId(), projection);
	}

	
//	/**
//	 * Find a dateLevel for a node
//	 * @param node
//	 * @param usedDimensions
//	 * @param dataObjectName 
//	 * @return
//	 */
//	public DateLevel findLevelForDateItem(NodeId node, List<Dimension> usedDimensions) {
//		
//		for(Dimension dim : usedDimensions) {
//			for(Hierarchy hiera : dim.getHierarchies()) {
//				for(Level lvl : hiera.getLevels()) {
//					if(lvl.getItem() != null) {
//						if(lvl instanceof DateLevel) {
//							if(lvl.getItem().getName().equals(node.getDateColumnName()) && ((DateLevel)lvl).getDatePart().equals(node.getId())) {
//								return (DateLevel)lvl;
//							}
//						}
//					}
//				}
//			}
//		}
//		
//		return null;
//	}
//
//	public String findOneColumnDateNode(Date dateItem, NodeId node) {
//		
//		GregorianCalendar calendar = new GregorianCalendar();
//		calendar.setTime(dateItem);
//		
//		return findDatePart(node.getId(), calendar);
//	}
}
