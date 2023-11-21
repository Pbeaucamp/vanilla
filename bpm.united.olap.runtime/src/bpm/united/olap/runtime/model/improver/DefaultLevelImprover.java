package bpm.united.olap.runtime.model.improver;

import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;

import bpm.united.olap.api.model.Level;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class DefaultLevelImprover extends LevelImprover{

	public DefaultLevelImprover(long cacheDataTimeout) {
		super(cacheDataTimeout);
	}

	@Override
	protected List<Object> improve(Level lvl, IQuery query, OdaInput odaInput,
			int mbNameIndex, int mbParentNameIndex, int mbOrderIndex,
			List<Integer> propertiesIndexes, int foreignKeyIndex, int labelIndex)
			throws Exception {
		return null;
	}

}
