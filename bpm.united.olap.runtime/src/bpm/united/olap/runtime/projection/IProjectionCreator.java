package bpm.united.olap.runtime.projection;

import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;

public interface IProjectionCreator {

	public ProjectionDescriptor createProjection(ICubeInstance cubeInstance, IRuntimeContext runtimeContext) throws Exception;
	
}
