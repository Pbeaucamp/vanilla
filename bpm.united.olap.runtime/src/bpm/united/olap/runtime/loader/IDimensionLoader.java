package bpm.united.olap.runtime.loader;

import java.util.List;

import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.Schema;

public interface IDimensionLoader {

	List<Member> getChilds(Member member, Integer schemaId) throws Exception;
	
	Dimension loadDimension(Schema schema, String dimensionName);
}
