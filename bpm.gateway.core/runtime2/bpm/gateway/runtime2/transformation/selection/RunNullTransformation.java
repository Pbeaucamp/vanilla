package bpm.gateway.runtime2.transformation.selection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.transformations.NullTransformation;
import bpm.gateway.core.transformations.utils.ConditionNull;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunNullTransformation extends RuntimeStep{
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private HashMap<RuntimeStep, List<ConditionNull>> conditionsByOutput = new HashMap<RuntimeStep, List<ConditionNull>>(); 
	private NullTransformation transfo;
	
	public RunNullTransformation(NullTransformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		transfo = (NullTransformation)getTransformation();
		
		isInited = true;
		
		for(ConditionNull c : transfo.getConditions()){
			for(RuntimeStep s : getOutputs()){
				if (s.getTransformation() == c.getOutput()){
					if (conditionsByOutput.get(s) == null){
						conditionsByOutput.put(s, new ArrayList<ConditionNull>());
					}
					conditionsByOutput.get(s).add(c);
					break;
				}
			}
		}
		info(" inited");
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
			try{
				Thread.sleep(10);
				return;
			}catch(Exception e){
				
			}
		}
		Row row = readRow();

		for(ConditionNull cd : transfo.getConditions()){
			int pos = getPositionFromStreamName(transfo.getDescriptor(transfo), cd.getStreamElementName());
//			int pos = cd.getStreamElementNum();
			Object ch = row.get(pos);
			Class<?> c = row.getMeta().getJavaClasse(pos);
			if(ch == null || String.valueOf(ch).equalsIgnoreCase("null")){
				
				if (Date.class.isAssignableFrom(c)){
					try{
						row.set(pos, sdf.parseObject(cd.getValue()));
					}catch(Exception ex){
					}
				}
				else if (Byte.class.isAssignableFrom(c)){
					row.set(pos, new Byte(cd.getValue()));
				}
				else if (Short.class.isAssignableFrom(c)){
					row.set(pos, new Short(cd.getValue()));
				}
				else if (Integer.class.isAssignableFrom(c)){
					row.set(pos, new Integer(cd.getValue()));
				}
				else if (Long.class.isAssignableFrom(c)){
					row.set(pos, new Long(cd.getValue()));
				}
				else if (BigInteger.class.isAssignableFrom(c)){
					row.set(pos, new BigInteger(cd.getValue()));
				}
				else if (Float.class.isAssignableFrom(c)){
					row.set(pos, new Float(cd.getValue()));
				}
				else if (Double.class.isAssignableFrom(c)){
					row.set(pos, new Double(cd.getValue()));
				}
				else if (BigDecimal.class.isAssignableFrom(c)){
					row.set(pos, new BigDecimal(cd.getValue()));
				}
				else if (String.class.isAssignableFrom(c)){
					row.set(pos, cd.getValue());
				}
				else{
					row.set(pos, cd.getValue());
				}
			}
		}
		
		writeRow(row);
	}

	private int getPositionFromStreamName(StreamDescriptor streamDescriptor, String streamElementName) throws Exception {
		if(streamDescriptor != null){
			for(int i=0; i<streamDescriptor.getStreamElements().size(); i++){
				String columnName = streamElementName;
				if(streamElementName.contains("::")) {
					columnName = streamElementName.split("::")[1];
				}
				if(columnName.equals(streamDescriptor.getStreamElements().get(i).name)){
					return i;
				}
			}
		}
		throw new Exception("The column " + streamElementName + " could not be found in the descriptor.");
	}

	@Override
	public void releaseResources() {
		info("Resources released");
		
	}

	@Override
	protected void writeRow(Row row) throws InterruptedException {
		for(RuntimeStep rs : getOutputs()){
			rs.insertRow(row, this);
		}
		writedRows++;
	}
}
