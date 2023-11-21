package bpm.gateway.runtime2.transformations.inputs;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.freemetrics.api.utils.Log;
import bpm.gateway.core.transformations.inputs.OdaInput;
import bpm.gateway.core.transformations.inputs.OdaInputWithParameters;
import bpm.gateway.core.transformations.inputs.odaconsumer.OdaHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunOdaInputWithParameters extends RuntimeStep{

	private IQuery query;
	private IResultSet resultSet;
	private boolean hasParameter = false;
	
	public RunOdaInputWithParameters(OdaInputWithParameters transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception{
		
		OdaInputWithParameters in = (OdaInputWithParameters)getTransformation();
		hasParameter = in.getParameterNames().size() > 0;
		
		
		query = QueryHelper.buildquery(OdaHelper.convert(in));
		
		
		
		if (hasParameter){
			info("Check Parameters mapping");
			int i = in.getInputs().get(0).getDescriptor(getTransformation()).getColumnCount();
			Log.info("ere: found " + i + " inputs");
			for(String s : in.getParameterNames()){
				if (in.getParameterValue(s) == null){
					throw new Exception("Oda Input " + s+ " parameter undefined" );
				}
				if (in.getParameterValue(s) >= i){
					info("Error: Parameter " + s + " is mapped on " + in.getParameterValue(s) + ", only " + i + " columns available");
					throw new Exception("Parameter " + s + " is mapped on a column that do not exists in the input");
				}
				info("Checked Parameter " + s + ", OK.");
			}
			info("Parameters checked");
		}
		else{
			info("Run query");
			resultSet = query.executeQuery();
		}
		
		isInited = true;

	}
	
	
	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputEmpty() && ( (!hasParameter && resultSet == null) || hasParameter)){
				setEnd();
			}
			
		}
		
		if (isEnd() && inputEmpty()&& ((!hasParameter && resultSet == null) || hasParameter)){
			return;
		}
		
		if (!isEnd() && inputEmpty()&& ((!hasParameter && resultSet == null) || hasParameter)){
			try{
				Thread.sleep(10);
				return;
			}catch(InterruptedException e){
				
			}
		}
		
		if (hasParameter){
			Row row = readRow();
			
			List<Object> parameters = new ArrayList<Object>();
			
			int k = 0;
			query.clearInParameters();
			for(String pname : ((OdaInput)getTransformation()).getParameterNames()){
				Integer i = ((OdaInput)getTransformation()).getParameterValue(pname);
				query.setString(++k, row.get(i).toString());
				
				parameters.add(row.get(i));
			}
			
			resultSet = query.executeQuery();
			
			while(resultSet.next()){
				Row newRow = RowFactory.createRow(this);
				
				int posParam = 0;
				
				for(int i = 0; i < resultSet.getMetaData().getColumnCount(); i++){
					newRow.set(i,get(i + 1, resultSet.getMetaData().getColumnType(i + 1)));
					posParam = i;
				}
				
				if (posParam != 0) {
					for (Object param : parameters) {
						newRow.set(++posParam, param);
					}
				}
				
				writeRow(newRow);
			}
			resultSet.close();
			resultSet = null;
			info("Closed ResultSet");
		
			
		}
		else{
			if (resultSet.next()){
				Row newRow = RowFactory.createRow(this);
				
				for(int i = 0; i < resultSet.getMetaData().getColumnCount(); i++){
					newRow.set(i,get(i + 1, resultSet.getMetaData().getColumnType(i + 1)));
				}
				
				writeRow(newRow);
			}
			else{
				resultSet.close();
				resultSet = null;
				info("Closed ResultSet");
				setEnd();
			}
		}
		
		
		
		
	}

	

	@Override
	public void releaseResources() {
		if (resultSet != null){
			
			try {
				resultSet.close();
				resultSet = null;
				info(" closed resultSet");
			} catch (Exception e) {
				error(" error when closing resultSet", e);
			}
		}
		
		if (query != null){
			try {
				query.close();
				QueryHelper.removeQuery(query);
				QueryHelper.closeConnectionFor(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	

	public Object get(int pos, int streamElementType) throws Exception {
		switch(streamElementType){
		case Types.BIGINT:
			
		case Types.INTEGER:
		case Types.SMALLINT:
			return resultSet.getInt(pos);
			
		case Types.BOOLEAN:
			return resultSet.getBoolean(pos);
			
		case Types.CHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
		case Types.VARBINARY:
			return resultSet.getString(pos);
			
		case Types.FLOAT:
		case Types.REAL:	
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.NUMERIC:
			return resultSet.getDouble(pos);
			
		
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return resultSet.getDate(pos);

		}
		return resultSet.getString(pos);
	}
	
}
