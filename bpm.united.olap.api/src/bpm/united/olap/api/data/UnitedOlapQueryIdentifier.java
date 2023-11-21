package bpm.united.olap.api.data;

import java.util.ArrayList;
import java.util.List;

import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;

public class UnitedOlapQueryIdentifier {

	private List<ElementDefinition> selects;
	private List<Member> wheres;
	
	public UnitedOlapQueryIdentifier(){}
	
	public UnitedOlapQueryIdentifier(IExternalQueryIdentifier externalId, ICubeInstance cubeInstance, IRuntimeContext runtimeContext) throws Exception {
		
		for(String select : externalId.getSelectElements().keySet()) {
			
			if(select.startsWith("[Measures]")) {
				for(Measure mes : cubeInstance.getCube().getMeasures()) {
					if(mes.getUname().equals(select)) {
						addSelect(mes);
					}
				}
			}
			else {
				LOOK:for(Dimension dim : cubeInstance.getCube().getDimensions()) {
					for(Hierarchy hiera : dim.getHierarchies()) {
						if(select.startsWith(hiera.getUname())) {
							for(Level lvl : hiera.getLevels()) {
								if(lvl.getUname().equals(select)) {
									addSelect(lvl);
									break LOOK;
								}
							}
							
						}
					}
				}
			}
		}
		
		if(externalId.getWhereElements() != null && !externalId.getWhereElements().isEmpty()) {
			for(String where : externalId.getWhereElements()) {
				LOOK:for(Dimension dim : cubeInstance.getCube().getDimensions()) {
					for(Hierarchy hiera : dim.getHierarchies()) {
						if(where.startsWith(hiera.getUname())) {
							Member mem = cubeInstance.getHierarchyExtractor(hiera).getMember(where, runtimeContext);
							addWhere(mem);
							continue LOOK;
						}
					}
				}
			}
		}
		
	}
	
	public void setSelects(List<ElementDefinition> selects) {
		this.selects = selects;
	}
	
	public List<ElementDefinition> getSelects() {
		return selects;
	}

	public void setWheres(List<Member> wheres) {
		this.wheres = wheres;
	}

	public List<Member> getWheres() {
		return wheres;
	}
	
	public void addSelect(ElementDefinition elem) {
		if(selects == null) {
			selects = new ArrayList<ElementDefinition>();
		}
		selects.add(elem);
	}
	
	public void addWhere(Member mem) {
		if(wheres == null) {
			wheres = new ArrayList<Member>();
		}
		wheres.add(mem);
	}
}
