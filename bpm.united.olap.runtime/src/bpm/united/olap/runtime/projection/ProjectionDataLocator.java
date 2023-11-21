package bpm.united.olap.runtime.projection;

import java.util.HashMap;
import java.util.List;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.api.runtime.model.IDataLocator;

/**
 * Only for projections
 * The methods which takes dataobject and dataobjectitem can't be used
 * @author Marc Lanquetin
 *
 */
public class ProjectionDataLocator implements IDataLocator {

	private HashMap<String, Integer> mapping = new HashMap<String, Integer>();
	
	public ProjectionDataLocator(CrossMembersValues v, ICubeInstance cubeInstance, Projection projection) {
		
//		Hierarchy datehiera = null;
//		for(Dimension dim : cubeInstance.getCube().getDimensions()) {
//			if(dim.isDate()) {
//				datehiera = dim.getHierarchies().get(0);
//				break;
//			}
//		}
		
		int index = 0;
		for(String memberUname : v.getMemberUnames()) {
			if(memberUname.startsWith("[Measures]")) {
				mapping.put("[Measures]", index);
			}
			else {
				String[] unamePart = memberUname.split("\\]\\.\\[");
				String hieraName = unamePart[0] + "]";
				mapping.put(hieraName, index);
			}
			index++;
		}
		
	}

	@Override
	public Integer getOrderResultSetIndex(Level level) {
		String hieraUname = level.getParentHierarchy().getUname();
		return mapping.get(hieraUname);
	}

	@Override
	public List<Relation> getPath(DataObject parent, DataObject relatedObject) throws Exception {
		
		return null;
	}

	@Override
	public DataObjectItem getRelatedDataObjectItem(DataObjectItem factForeignKey, Member member) throws Exception {
		
		return null;
	}

	@Override
	public Integer getResultSetIndex(Measure measure) {
		return mapping.get("[Measures]");
	}

	@Override
	public Integer getResultSetIndex(Member member) {
		
		return mapping.get(member.getParentLevel().getParentHierarchy().getUname());
	}

	@Override
	public Integer getResultSetIndex(Member member, int levelIndex) throws Exception {
		return mapping.get(member.getParentLevel().getParentHierarchy().getUname());
	}

	@Override
	public Integer getResultSetIndex(Level level) {
		return mapping.get(level.getParentHierarchy().getUname());
	}

	@Override
	public Integer getResultSetIndexInFactTable(DataObjectItem origin) {
		
		return null;
	}

}
