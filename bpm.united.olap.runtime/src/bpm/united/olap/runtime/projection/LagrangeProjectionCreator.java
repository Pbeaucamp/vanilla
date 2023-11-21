package bpm.united.olap.runtime.projection;

import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;

public class LagrangeProjectionCreator implements IProjectionCreator {

	private Projection projection;
	
	public LagrangeProjectionCreator(Projection projection) {
		this.projection = projection;
	}

	@Override
	public ProjectionDescriptor createProjection(ICubeInstance cubeInstance, IRuntimeContext runtimeContext) {
		
		return null;
	}

}
