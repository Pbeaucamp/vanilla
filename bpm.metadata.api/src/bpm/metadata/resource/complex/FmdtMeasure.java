package bpm.metadata.resource.complex;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import bpm.metadata.MetaData;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.complex.measures.IOperand;
import bpm.metadata.resource.complex.measures.impl.Dimension;
import bpm.metadata.resource.complex.measures.impl.DimensionLevel;
import bpm.metadata.resource.complex.measures.impl.Measure;
import bpm.metadata.resource.complex.measures.impl.MeasureParser;

public class FmdtMeasure implements IResource{
	private String name = "newMeasure";
	private SQLDataSource dataSource;
	private String dataSourceName;
	
	private String script ;
	private IOperand operand;
	
	public String getName() {
		return name;
	}
	
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("    <measure>\n");
		buf.append("        <name>" + getName() + "</name>\n");
		buf.append("        <dataSource>" + dataSource.getName() + "</dataSource>\n");
		buf.append("        <script><![CDATA[" + script + "]]></script>\n");
		buf.append("    </measure>\n");
		return buf.toString();
	}
	
	public void setDataSource(SQLDataSource dataSource){
		this.dataSource = dataSource;
	}
	
	public SQLDataSource getDataSource(){
		return dataSource;
	}
	public void setDataSourceName(String dataSourceName){
		this.dataSourceName = dataSourceName;
	}
	public void build(MetaData model){
		try{
			if (dataSource == null && dataSourceName != null){
				setDataSource((SQLDataSource)model.getDataSource(dataSourceName));
				dataSourceName = null;
			}
			
			
			List<Measure> mes = new ArrayList<Measure>();
			List<DimensionLevel> lvls = new ArrayList<DimensionLevel>();
			List<Dimension> dims = new ArrayList<Dimension>();
			
			for(IDataStream t : dataSource.getDataStreams()){
				for(IDataStreamElement e : t.getElements()){
					if (e.getType().getParentType() == IDataStreamElement.Type.MEASURE){
						mes.add(new Measure(e));
					}
				}
			}
			
			
			for(IResource r : model.getResources()){
				if (r instanceof FmdtDimension){
					if (((FmdtDimension)r).getDataSource() == dataSource){
						dims.add(new Dimension((FmdtDimension)r));
						for(int i = 0; i < ((FmdtDimension)r).getLevels().size(); i++){
							lvls.add(new DimensionLevel((FmdtDimension)r, i));
						}
						
					}
				}
			}
			
			MeasureParser parser = new MeasureParser(mes, lvls, dims);
			operand = parser.readChunk(IOUtils.toInputStream(getScript()));
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * @param script the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}
	public IOperand getOperand(){
		return operand;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public boolean isGrantedFor(String groupName) {
		
		return true;
	}

	public String getOutputName() {
		
		return getName();
	}

	public String getOutputName(Locale l) {
		
		return getName();
	}
	
}
