package bpm.vanilla.platform.core.runtime.alerts;

import java.math.BigInteger;
import java.sql.Types;
import java.util.Date;

import bpm.dataprovider.odainput.consumer.OdaHelper;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.AlertConstants;
import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.core.beans.alerts.ConditionRepository;
import bpm.vanilla.platform.core.beans.alerts.IAlertRuntime;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.DatasetResultQuery;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.runtime.alerts.conditions.ConditionResult;

public class AlertObjectRuntime implements IAlertRuntime {
	
	private Alert alert;
	private IRepositoryContext repositoryContext;

	public AlertObjectRuntime(Alert alert, IRepositoryContext repositoryContext) {
		this.alert = alert;
		this.repositoryContext = repositoryContext;
	}
	
	@Override
	public boolean checkAlert() throws Exception {
		if(alert.getState() != AlertConstants.ACTIVE){
			return false;
		}
		
		boolean alertIsOk = false;
		boolean first = true;
		int dataProviderId = alert.getDataProviderId();
		
//		IRepositoryApi sock = new RemoteRepositoryApi(repositoryContext);
//		
//		
//		DatasProvider dp = sock.getDatasProviderService().getDatasProvider(dataProviderId);
//		
//		OdaInput input = new OdaInputDigester(dp.getModel()).getOdaInput();
		IVanillaAPI vanilla = new RemoteVanillaPlatform(repositoryContext.getVanillaContext());
		Dataset dataset = vanilla.getVanillaPreferencesManager().getDatasetById(dataProviderId);
		DatasetResultQuery result = vanilla.getVanillaPreferencesManager().getResultQuery(dataset);
				
		
		String operator = alert.getOperator();
		int operatorType = -1;
		if (operator.equalsIgnoreCase("and")) {
			operatorType = 0;
		}
		else if (operator.equalsIgnoreCase("or")) {
			operatorType = 1;
		}
		
		for (Condition c : alert.getConditions()) {
			boolean check = true;
			
			String operand = c.getOperator();
			
			int operandType = -1;
			if (operand.equals(AlertConstants.EQUALS)) {
				operandType = 0;
			}
			else if (operand.equals(AlertConstants.GREATER_THAN)) {
				operandType = 1;
			}
			else if (operand.equals(AlertConstants.LESS_THAN)) {
				operandType = 2;
			}
			
			int columnsSize = result.getResult().get(dataset.getMetacolumns().get(0).getColumnLabel()).size(); //taille de la premiere colonne
			int leftType, rightType;
			leftType = getColumn(dataset, c.getLeftOperand()).getColumnType();
			if(((ConditionRepository)c.getConditionObject()).isRightOperandField()){ //column
				rightType = getColumn(dataset, c.getRightOperand()).getColumnType();
			} else { //value
				rightType = leftType;
			}
			
			for(int i=0; i<columnsSize; i++){
				ConditionResult crLeft = new ConditionResult();
				crLeft.setType(leftType);
				crLeft.setResult(result.getResult().get(c.getLeftOperand()).get(i));
				
				ConditionResult crRight = new ConditionResult();
				if(((ConditionRepository)c.getConditionObject()).isRightOperandField()){ //column
					crRight.setType(rightType);
					crRight.setResult(result.getResult().get(c.getRightOperand()).get(i));
				} else { //value
					crRight.setType(rightType);
					crRight.setResult(c.getRightOperand());
				}

				//Si un des deux est un String tenter le cast
				
				Object leftValue;
				Object rightValue;
				
				if (!OdaHelper.getJavaClassName(leftType).equalsIgnoreCase(OdaHelper.getJavaClassName(rightType))) {
					if (String.class.getName().equals(OdaHelper.getJavaClassName(leftType))) {
						leftValue = cast(crLeft.getResult(), rightType);
						rightValue = crRight.getResult();
					}
					else if (String.class.getName().equals(OdaHelper.getJavaClassName(rightType))) {
						leftValue = crLeft.getResult();
						rightValue = cast(crRight.getResult(), leftType);
					}
					else {
						throw new Exception("Unable to compare " + OdaHelper.getJavaClassName(leftType) + " and " + OdaHelper.getJavaClassName(rightType));
					}
				}
				else {
					leftValue = crLeft.getResult();
					rightValue = crRight.getResult();	
				
				}
				
				check &= getCheck(leftType, operandType, leftValue, rightValue);
				
			}
			

			if (first) {
				alertIsOk = check;
				first = false;
			}
			else {
				switch (operatorType) {
				case 0:
					alertIsOk = alertIsOk && check;
					break;
				case 1:
					alertIsOk = alertIsOk || check;
				default:
					break;
				}
				
			}
	
			
		}
		return alertIsOk;
		
	
	}

	private Object cast(Object result, int typeCode) {
		if (!(result instanceof String)){
			return result;
		}
		Object o = null;
		switch(typeCode){
		case Types.BIGINT:
			return o = new BigInteger((String) result);
		case Types.INTEGER:
		case Types.SMALLINT:
			return o = new Integer((String) result);

		case Types.BOOLEAN:
			return o = new Boolean((String) result);
	
			
		case Types.FLOAT:
			return o = new Float((String) result);

		case Types.REAL:	
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.NUMERIC:
			return o = new Double((String) result);
			
		
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return o = new Date((String) result);
		}
		return o;
	}
	
	private boolean getCheck(int leftType, int operandType, Object leftValue, Object rightValue) {
		boolean check = false;
		switch(leftType){
		case Types.BIGINT:
			BigInteger v1 = (BigInteger) leftValue;
			BigInteger v2 = (BigInteger) rightValue;
			switch (operandType) {
			case 0:
				check =  v1.compareTo(v2) == 0;
				break;
			case  1:
				check =  v1.compareTo(v2) > 0;
				break;
				
			case 2:
				check =  v1.compareTo(v2) < 0;
				break;
			default:
				break;
			}
			 break;
		case Types.INTEGER:
		case Types.SMALLINT:
			Integer sv1 = (Integer) leftValue;
			Integer sv2;
			try {
				sv2 = (Integer) rightValue;
			} catch (Exception e1) {
				sv2 = new Integer((String)rightValue);
			}
			switch (operandType) {
			case 0:
				check =  sv1.compareTo(sv2) == 0;
				break;
			case  1:
				check =  sv1.compareTo(sv2) > 0;
				break;
				
			case 2:
				check =  sv1.compareTo(sv2) < 0;
				break;
			default:
				break;
			} 
			break;
		case Types.BOOLEAN:
			 break;
			
		case Types.CHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
		case Types.VARBINARY:
			String str1 = (String) leftValue;
			String str2 = (String) rightValue;
			switch (operandType) {
			case 0:
				check =  str1.compareTo(str2) == 0;
				break;
			case 1:
				check =  str1.compareTo(str2) > 0;
				break;
				
			case 2:
				check =  str1.compareTo(str2) < 0;
				break;
			default:
				break;
			}
			break;
			
		case Types.FLOAT:
			Float fv1 = (Float) leftValue;
			Float fv2 = (Float) rightValue;
			switch (operandType) {
			case 0:
				check =  fv1.compareTo(fv2) == 0;
				break;
			case  1:
				check =  fv1.compareTo(fv2) > 0;
				break;
				
			case 2:
				check =  fv1.compareTo(fv2) < 0;
				break;
			default:
				break;
			}
			break;
		case Types.REAL:	
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.NUMERIC:
			Double dv1 = (Double) leftValue;
			Double dv2;
			try {
				dv2 = (Double) rightValue;
			} catch (Exception e) {
				dv2 = new Double((String)rightValue);
			}
			switch (operandType) {
			case 0:
				check =  dv1.compareTo(dv2) == 0;
				break;
			case  1:
				check =  dv1.compareTo(dv2) > 0;
				break;
				
			case 2:
				check =  dv1.compareTo(dv2) < 0;
				break;
			default:
				break;
			}
			break;
		
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			Date _dv1 = (Date) leftValue;
			Date _dv2 = (Date) rightValue;
			switch (operandType) {
			case 0:
				check =  _dv1.compareTo(_dv2) == 0;
				break;
			case  1:
				check =  _dv1.compareTo(_dv2) > 0;
				break;
				
			case 2:
				check =  _dv1.compareTo(_dv2) < 0;
				break;
			default:
				break;
			}
			break;
		}
		return check;
	}
	
	private DataColumn getColumn(Dataset dataset, String name) {
		for(DataColumn col : dataset.getMetacolumns()){
			if(col.getColumnLabel().equals(name)){
				return col;
			}
		}
		return null;
	}

}
