package bpm.united.olap.runtime.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.ClosureLevel;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.api.runtime.model.IDataLocator;
import bpm.united.olap.api.tools.AlphanumComparator;

public class AdvancedDataMapper implements IDataMapper {

	private DataStorage storage;
	private ICubeInstance cube;
	
	private int maxIndex;
	private int minIndex;
	
	private AlphanumComparator comparator = new AlphanumComparator();
	private IDataLocator dataLocator;
	private IRuntimeContext runtimeCtx; 
	public AdvancedDataMapper(DataStorage storage, ICubeInstance cube, IDataLocator dataLocator, IRuntimeContext runtimeCtx) {
		this.storage = storage;
		this.cube = cube;
		this.dataLocator = dataLocator;
		this.runtimeCtx = runtimeCtx;
	}
	
	@Override
	public DataCell mapData(IResultSet rs, DataCellIdentifier2 possibleId) throws Exception {
		List<Integer> lookedIndex = new ArrayList<Integer>();
		int firstIndex = storage.getDataCells().size() / 2;
		
		maxIndex = storage.getDataCells().size() - 1;
		minIndex = 0;
		
		return lookup(possibleId, rs, lookedIndex, firstIndex);
	}

	private DataCell lookup(DataCellIdentifier2 possibleId, IResultSet rs, List<Integer> lookedIndex, int actualIndex) throws Exception {
		if(lookedIndex.contains(actualIndex) || actualIndex < 0 && actualIndex < storage.getDataCells().size()) {
			return null;
		}
		
		lookedIndex.add(actualIndex);
		DataCell actualCell = storage.getDataCells().get(actualIndex);
		
		for(int i = 0 ; i < possibleId.getIntersections().size() ; i++) {
			
			ElementDefinition possElem = possibleId.getIntersections().get(i);
			ElementDefinition actualElem = actualCell.getIdentifier2().getIntersections().get(i);
			
			int possLenght = possElem.getUname().split("\\]\\.\\[").length;
			int actualLenght = actualElem.getUname().split("\\]\\.\\[").length;
			
			if(possLenght != actualLenght) {
				
				if(possElem instanceof Member && ((Member)possElem).getParentLevel() == null) {
					return lookup(possibleId, rs, lookedIndex, findNextIndex(lookedIndex, actualIndex, true));
				}
				else if(actualElem instanceof Member && ((Member)actualElem).getParentLevel() == null) {
					return lookup(possibleId, rs, lookedIndex, findNextIndex(lookedIndex, actualIndex, false));
				}
				
				else {
					boolean before = isBefore(possElem, actualElem, rs);
					return lookup(possibleId, rs, lookedIndex, findNextIndex(lookedIndex, actualIndex, before));
				}
				
			}
			else {
				if(possElem instanceof Member && actualElem instanceof Member && ((Member)actualElem).getParentLevel() != null) {
					
					Member actualMember = (Member) actualElem;
					
					if(actualMember.getParentLevel() instanceof ClosureLevel) {
						String[] memberParts = actualMember.getUname().split("\\]\\.\\[");
						String[] hieraParts = actualMember.getParentLevel().getParentHierarchy().getUname().split("\\]\\.\\[");
						
						int levelIndex = memberParts.length - hieraParts.length - 2;
						for(int j = 0 ; j < levelIndex + 1 ; j++) {
							int res = compareClosureMemberWithResultSetValue(actualMember, rs, levelIndex);
							if(res < 0) {
								return lookup(possibleId, rs, lookedIndex, findNextIndex(lookedIndex, actualIndex, true));
							}
							else if(res > 0) {
								return lookup(possibleId, rs, lookedIndex, findNextIndex(lookedIndex, actualIndex, false));
							}
						}
					}
					
					else {
						int levelIndex = actualMember.getParentLevel().getParentHierarchy().getLevels().indexOf(actualMember.getParentLevel());
						
						for(int j = 0 ; j < levelIndex + 1 ; j++) {
							int res = compareMemberWithResultSetValue(actualMember, j, rs);
							if(res < 0) {
								return lookup(possibleId, rs, lookedIndex, findNextIndex(lookedIndex, actualIndex, true));
							}
							else if(res > 0) {
								return lookup(possibleId, rs, lookedIndex, findNextIndex(lookedIndex, actualIndex, false));
							}
						}
					}
				}
			}
		}
		Measure possMes = possibleId.getMeasure();
		Measure actualMes = actualCell.getIdentifier2().getMeasure();
		int res = comparator.compare(possMes.getName(),actualMes.getName());
		if(res < 0) {
			if(storage.getIsMeasureRow()) {
				return lookup(possibleId, rs, lookedIndex, actualIndex - storage.getNbCol());
			}
			else {
				return lookup(possibleId, rs, lookedIndex, actualIndex - 1);
			}
		}
		else if(res > 0) {
			if(storage.getIsMeasureRow()) {
				return lookup(possibleId, rs, lookedIndex, actualIndex + storage.getNbCol());
			}
			else {
				return lookup(possibleId, rs, lookedIndex, actualIndex + 1);
			}
		}
		
		
		return actualCell;
	}
	
	private boolean isBefore(ElementDefinition possElem, ElementDefinition actualElem, IResultSet rs) throws Exception {
		
		int possLenght = possElem.getUname().split("\\]\\.\\[").length;
		int actualLenght = actualElem.getUname().split("\\]\\.\\[").length;

		Member mem = null;
		Member actualMember = (Member) actualElem;
		
		if(possLenght > actualLenght) {
			mem = (Member) actualElem;
		}
		else {
			mem = (Member) possElem;
		}
		
		if(actualMember.getParentLevel() instanceof ClosureLevel) {
			String[] memberParts = actualMember.getUname().split("\\]\\.\\[");
			String[] hieraParts = actualMember.getParentLevel().getParentHierarchy().getUname().split("\\]\\.\\[");
			
			int levelIndex = memberParts.length - hieraParts.length - 2;
			for(int j = 0 ; j < levelIndex + 1 ; j++) {
				int res = compareClosureMemberWithResultSetValue(actualMember, rs, levelIndex);
				if(res < 0) {
					return true;
				}
				else if(res > 0) {
					return false;
				}
			}
		}
		
		else {
			int levelIndex = mem.getParentLevel().getParentHierarchy().getLevels().indexOf(mem.getParentLevel());
			
			for(int i = 0 ; i < levelIndex + 1 ; i++) {
				int res = compareMemberWithResultSetValue(actualMember, i, rs);
				if(res < 0) {
					return true;
				}
				else if(res > 0) {
					return false;
				}
			}
		}
		
		return possLenght < actualLenght;
	}
	
	@Override
	public int compareMemberWithResultSetValue(Member actualMember, int levelIndex, IResultSet rs) throws Exception {
		Level lvl = actualMember.getParentLevel().getParentHierarchy().getLevels().get(levelIndex);
		
		Member member = actualMember;
		while(lvl != member.getParentLevel()) {
			member = member.getParentMember();
		}
		
		int fkIndex = dataLocator.getResultSetIndex(member);
		Object fk = rs.getString(fkIndex + 1);
		if(member.getOrderValue() != null && !member.getOrderValue().equals(member.getName())) {
			try {
				String mbName = cube.getHierarchyExtractor(actualMember.getParentLevel().getParentHierarchy()).getMemberOrderValue(fk, levelIndex, runtimeCtx);
				return comparator.compare(mbName, member.getOrderValue());
			} catch(Exception e) {
				String mbName = cube.getHierarchyExtractor(actualMember.getParentLevel().getParentHierarchy()).getMemberName(fk, levelIndex, runtimeCtx);
				return comparator.compare(mbName, member.getName());
			}
		}
		else {
			String mbName = cube.getHierarchyExtractor(actualMember.getParentLevel().getParentHierarchy()).getMemberName(fk, levelIndex, runtimeCtx);
			return comparator.compare(mbName, member.getName());
		}
	}
	
	@Override
	public int compareClosureMemberWithResultSetValue(Member actualMember, IResultSet rs, int actualMemberLvl) throws Exception {
		int fkIndex = dataLocator.getResultSetIndex(actualMember);
		Object fk = rs.getString(fkIndex + 1);
		
		String mbName = null;
		if(actualMember.getOrderValue() != null && !actualMember.getOrderValue().equals(actualMember.getName())) {
			mbName = cube.getHierarchyExtractor(actualMember.getParentLevel().getParentHierarchy()).getMemberOrderValue(fk, actualMemberLvl, runtimeCtx);
		}
		else {
			mbName = cube.getHierarchyExtractor(actualMember.getParentLevel().getParentHierarchy()).getMemberName(fk, actualMemberLvl, runtimeCtx);
		}
		
		Member member = actualMember;
		
		
		if(actualMember.getOrderValue() != null) {
			return comparator.compare(mbName, member.getOrderValue());
		}
		else {
			return comparator.compare(mbName, member.getName());
		}
	}

	/**
	 * Find the next index of the datastorage's cells collection
	 * @param lookedIndex
	 * @param actualIndex
	 * @param before
	 * @return
	 */
	private Integer findNextIndex(List<Integer> lookedIndex, Integer actualIndex, boolean before) {
		
		if(before) {
			maxIndex = actualIndex;
			if(actualIndex == 1 && !lookedIndex.contains(0)) {
				return 0;
			}
			if(minIndex < actualIndex) {
				return (minIndex + actualIndex) / 2;
			}
			else {
				return actualIndex / 2;
			}
		}
		else {
			minIndex = actualIndex;
			
			if(actualIndex == maxIndex - 1) {
				return maxIndex;
			}
			
			return (maxIndex + actualIndex) / 2;
		}
		
	}

	@Override
	public boolean checkWhereClause(IResultSet rs, List<Member> wheres) throws OdaException, Exception {
		
//		if(wheres == null || wheres.size() <= 0) {
//			return true;
//		}
//		
//		int wheresize = wheres.size();
//		int found = 0;
//		
//		for(Member where : wheres) {
//			int rsFk = dataLocator.getResultSetIndex(where);
//			String mbName = cube.getHierarchyExtractor(where.getParentLevel().getParentHierarchy()).getMemberName(rs.getString(rsFk + 1), where.getParentLevel().getParentHierarchy().getLevels().indexOf(where.getParentLevel()), runtimeCtx);
//				
//			if(mbName.equals(where.getName())) {
//				found++;
////				break;
//			}
//		}
//		
//		if(wheresize == found) {
//			return true;
//		}
//		
//		return false;
		
		if(wheres == null || wheres.size() <= 0) {
			return true;
		}
		
		for(Member where : wheres) {
			int rsFk = dataLocator.getResultSetIndex(where);
			String mbName = cube.getHierarchyExtractor(where.getParentLevel().getParentHierarchy()).getMemberName(rs.getString(rsFk + 1), where.getParentLevel().getParentHierarchy().getLevels().indexOf(where.getParentLevel()), runtimeCtx);
				
			if(mbName.equals(where.getName())) {
				return true;
			}
		}
		
		
		return false;
		
	}

}
