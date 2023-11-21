package bpm.united.olap.runtime.projection;

import java.util.List;

import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * Create a projection using a linear regression
 * @author Marc Lanquetin
 *
 */
public class LinearRegressionProjectionCreator extends AbstractProjectionCreator {
	
	public LinearRegressionProjectionCreator(Projection projection, IVanillaLogger logger, ProjectionMeasure measure) {
		this.projection = projection;
		this.logger = logger;
		this.measure = measure;
	}
	
//	@Override
//	public ProjectionDescriptor createProjection(ICubeInstance cubeInstance, IRuntimeContext runtimeContext) throws Exception {
//		
//		this.cubeInstance = cubeInstance;
//		this.runtimeContext = runtimeContext;
//		
//		String s = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.united.olap.runtime.projection.file.bufferSize");
//		try {
//			fileSize = Integer.parseInt(s);
//		} catch (Exception e1) {
//			logger.warn("The fileBuffer is not defined or is not an integer. It will be 3000", e1);
//			fileSize = 3000;
//		}
//		
//		List<CrossMembersValues> crossMembers = createCrossMembersValues();
//		
//		
//		//for each element of the matrix, find the linear coefficients
//		logger.debug("calculate coeffs");
//		for(CrossMembersValues val : crossMembers) {
//
//			findArraysForCoeffsCalcul(val, projectionLevel, minimumExistingDate, maximumExistingDate);
//			
//			double[] results = findLinearCoeffs(val);
//			val.setCoefficients(results);
//			val.clearArrays();
//		}
//		
//		//for each new dates, calculate the extrapolated value
//		Date startDate = projection.getStartDate();
//		Date endDate = projection.getEndDate();
//		
//		double start = calculDoubleDate(startDate, projectionLevel);
//		double end = calculDoubleDate(endDate, projectionLevel);
//		
//		logger.debug("calcul new values");
//		
//		List<CrossMembersValues> resultValues = new ArrayList<CrossMembersValues>();
//		
//		
//		int nbNewLines = 0;
//		int nbFile = 1;
//		for(CrossMembersValues val : crossMembers) {
//			double actualDate = start;
//			
//			for( ; actualDate <= end ; actualDate = incrementeDate(actualDate, projectionLevel)) {
//				
//				//create the new date member
//				Date date = new Date((long) (actualDate * 1000 * 3600 * 24 - (3600 * 1000)));
//				String newDateMember = createNewDateMember(date, timeDimension);
//				
//				List<String> crossMembs = new ArrayList<String>(val.getMemberUnames());
//				crossMembs.add(0,newDateMember);
//				
//				CrossMembersValues resultVal = new CrossMembersValues(crossMembs);
//				
//				//calcul the new value
//				if(val.getOnlyOneValue() != null) {
//					double value = val.getOnlyOneValue(); 
//					
//					resultVal.addValue(date, value);
//					resultVal.setHasOnlyOneValue();
//				}
//				else {
//					double value = val.getCoeffa() + (val.getCoeffb() * actualDate);
//					
//					resultVal.addValue(date, value);
//					resultVal.setHasOnlyOneValue();
//				}
//				
//				resultValues.add(resultVal);
//			}
//			
//			if(nbNewLines % 100 == 0) {
//				logger.debug("treated cross : " + nbNewLines);
//			}
//			
//			nbNewLines++;
//			
//			//We serialize the data every "fileSize" lines
//			if(resultValues.size() >= fileSize) {
//				ProjectionSerializationHelper.serialize(resultValues, projection, logger, nbFile, measure.getUname());
//				resultValues.clear();
//				nbFile++;
//			}
//		}
//		
//		logger.debug("Serialize the result");
//		
//		//serialize the new values
//		ProjectionDescriptor desc = ProjectionSerializationHelper.serialize(resultValues, projection, logger, nbFile, measure.getUname());
//		
//		return desc;
//	}
	
	@Override
	protected void calculAndSetCoeffs(CrossMembersValues val) throws Exception {
		val.setCoefficients(findLinearCoeffs(val));
	}
	
	@Override
	protected double calculNewValue(CrossMembersValues val, double actualDate) throws Exception {
		return val.getCoeffa() + (val.getCoeffb() * actualDate);
	}
	
	private double[] findLinearCoeffs(CrossMembersValues val) throws Exception {
		return findLinearCoeffs(val.getDatesAsArray(), val.getValuesAsArray());
	}

	/**
	 * For debug purpose only
	 * @param crossMembers
	 */
	public void dumpResult(List<CrossMembersValues> crossMembers) {
		
		logger.debug("------------Dump forecasted values-----------------");
		
		for(CrossMembersValues cross : crossMembers) {
			logger.debug(cross.dump());
		}
		
	}
	
	public double[] findLinearCoeffs(double[] xi, double[] yi) throws Exception {
		  if(xi.length != yi.length) {
			  throw new Exception("The two tabs don't have the same size !");
		  }
		  
		  //the coefficents for the equation like : a + bx
		  
		  double a = 0.0;
		  double b = 0.0;
		  
		  double size = xi.length;
		  
		  double sumx = 0.0;
		  double sumy = 0.0;
		  double sumxcarre = 0.0;
		  double sumxy = 0.0;
		  
		  //calcul the coefficients
		  for(int i = 0 ; i < size ; i++) {
			  sumx += xi[i];
			  sumy += yi[i];
			  
			  sumxcarre += (xi[i] * xi[i]);
			  
			  sumxy += (xi[i] * yi[i]);
		  }
		  
//		  b = ((size * sumxy) - (sumx * sumy)) / ((size * sumxcarre) - (sumx * sumx));
//		  a = (sumy - b * (sumx)) / size;
		  
		  b = ((size * sumxy) - (sumx * sumy)) / ((size * sumxcarre) - (sumx * sumx));
		  a = (sumy - b * (sumx)) / size;
		  
		  return new double[]{a,b};
	}
	
	

}
