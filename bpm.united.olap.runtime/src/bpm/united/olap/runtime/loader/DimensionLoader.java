package bpm.united.olap.runtime.loader;

import java.util.List;

import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.runtime.engine.UnitedOlapContentManager;

public abstract class DimensionLoader implements IDimensionLoader {

	private static IDimensionLoader instance;
	
//	@Override
//	public List<Member> getChilds(Member member, Integer schemaId) throws Exception {
//		return UnitedOlapContentManager.getInstance().getSubmembers(member.getMemberRelationsUname(), schemaId);
//	}

	@Override
	public Dimension loadDimension(Schema schema, String dimensionName) {
		for(Dimension dim : schema.getDimensions()) {
			if(dim.getName().equals(dimensionName)) {
				return dim;
			}
		}
		return null;
	}

//	public static IDimensionLoader getDimensionLoader() {
//		if(instance == null) {
//			instance = new DimensionLoader();
//		}
//		return instance;
//	}
//	
	private DimensionLoader() {
		
	}
}
