package bpm.united.olap.runtime.query.improver;

import org.eclipse.datatools.connectivity.oda.IQuery;

import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public interface IQueryImprover {

	void improveQuery(OdaInput odaInput, IQuery query, DataStorage storage, ICubeInstance cubeInstance, DataCellIdentifier2 possibleId) throws QueryImproverException, Exception;
	
}
