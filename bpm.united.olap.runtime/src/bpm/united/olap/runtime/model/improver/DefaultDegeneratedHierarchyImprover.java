package bpm.united.olap.runtime.model.improver;

import org.eclipse.datatools.connectivity.oda.IQuery;

import bpm.vanilla.platform.core.beans.data.OdaInput;

public class DefaultDegeneratedHierarchyImprover extends DegeneratedHierarchyLevelImprover{

	@Override
	public IQuery improveQuery(IQuery query, OdaInput input) throws Exception {
		return query;
	}

}
