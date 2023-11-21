package bpm.united.olap.runtime.projection;

import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class ProjectionCreatorFactory {

	public static IProjectionCreator getProjectionCreator(Projection projection, IVanillaLogger logger, ProjectionMeasure projectionMeasure) {
		
		if(projectionMeasure.getFormula().equals(Projection.EXTRAPOLATION_LINEAR)) {
			return new LinearRegressionProjectionCreator(projection, logger, projectionMeasure);
		}
		else if(projectionMeasure.getFormula().equals(Projection.EXTRAPOLATION_LAGRANGE)) {
			return new PolynomialRegressionProjectionCreator(projection, logger, projectionMeasure);
		}
		
		return null;
	}
	
}
