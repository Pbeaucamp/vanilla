package bpm.gateway.runtime2.transformations.outputs;

import java.sql.Connection;

import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.core.transformations.outputs.InfoBrightInjector;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

import com.infobright.etl.model.BrighthouseRecord;
import com.infobright.etl.model.DataFormat;
import com.infobright.etl.model.GenericValueConverter;
import com.infobright.io.InfobrightNamedPipeLoader;
import com.infobright.logging.ConsoleEtlLogger;

public class RunInfobright extends RuntimeStep{

	private Connection sqlConnection;
	private InfobrightNamedPipeLoader pipeLoader;
	private BrighthouseRecord brightHouseRecord;

	private GenericValueConverter converter = new GenericValueConverter();
	
	public RunInfobright(InfoBrightInjector transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}
	private void createJdbcResources(DataBaseConnection c) throws Exception{
		sqlConnection = c.getSocket(getTransformation().getDocument());

		pipeLoader = new InfobrightNamedPipeLoader(((DataBaseOutputStream)getTransformation()).getTableName(), sqlConnection, new ConsoleEtlLogger(ConsoleEtlLogger.Level.DEBUG), DataFormat.TXT_VARIABLE);
		brightHouseRecord = pipeLoader.createRecord(false);

		try{
			pipeLoader.start();
			info(" pipeLoaded started");
		}catch(Exception e){
			error(" error when starting pipeLoaded", e);
			throw e;
		}
	}
	
	@Override
	public void init(Object adapter) throws Exception {
//		try{
//			String s = getTransformation().getDocument().getResourceManager().getVariable(ResourceManager.VAR_BIGATEWAY_HOME).getOuputName();
//			System.load(getTransformation().getDocument().getStringParser().getValue(null, s).substring(1) + "resources/infobright_jni.dll");
//			info(" Infobright JNI DLL loaded");
//		}catch(Exception e){
//			error(" unable to load Infobright librairie", e);
//			throw e;
//		}
		
		DataBaseServer server = (DataBaseServer)((DataBaseOutputStream)getTransformation()).getServer();
		DataBaseConnection c = (DataBaseConnection)server.getCurrentConnection(adapter);
		createJdbcResources(c);
		
		isInited = true;
	}
	
	

	@Override
	public void performRow() throws Exception {

		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // restore interrupted status
			}
			return;
		}
		
		Row row = readRow();
		Row newRow =  RowFactory.createRow(this);
		for(int i = 0; i < row.getMeta().getSize(); i++){
			Integer k = ((InfoBrightInjector)getTransformation()).getMappingValueForInputNum(getTransformation().getInputs().get(0), i);
			if (k != null){
				brightHouseRecord.setData(i, row.get(k), converter);
				newRow.set(k, row.get(i));
			}
			
		}
		brightHouseRecord.writeTo(pipeLoader.getOutputStream());
		writeRow(newRow);
	}

	@Override
	public void releaseResources() {
		try{
			pipeLoader.stop();
			info(" pipeLoaded stoped");
		}catch(Exception e){
			error(" error when stoped pipeLoaded", e);
			
		}
		
		try{
			sqlConnection.close();
			info(" connection closed");
		}catch(Exception e){
			
		}
		info(" resources released");
	}

}
