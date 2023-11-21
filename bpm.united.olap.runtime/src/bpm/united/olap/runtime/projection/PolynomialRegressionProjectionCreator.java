package bpm.united.olap.runtime.projection;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * Create a projection usting the Lagrange polynomial regression
 * @author Marc Lanquetin
 *
 */
public class PolynomialRegressionProjectionCreator extends AbstractProjectionCreator {
	
	public PolynomialRegressionProjectionCreator(Projection projection, IVanillaLogger logger, ProjectionMeasure measure) {
		this.projection = projection;
		this.logger = logger;
		this.measure = measure;
	}

	@Override
	protected void calculAndSetCoeffs(CrossMembersValues val) throws Exception {
		PolynomialFunctionLagrangeForm lagrange = new PolynomialFunctionLagrangeForm(val.getDatesAsArray(), val.getValuesAsArray());
		
		val.setLagrangePolynom(lagrange);
	}
	
	@Override
	protected double calculNewValue(CrossMembersValues val, double actualDate) throws Exception {
		return val.getLagrangePolynom().value(actualDate);
	}

}
