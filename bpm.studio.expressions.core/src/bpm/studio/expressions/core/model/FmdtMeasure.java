package bpm.studio.expressions.core.model;

import bpm.studio.expressions.core.measures.IOperand;

public abstract class FmdtMeasure {
	private String name = "newMeasure";
	
	private String script ;
	private IOperand operand;
	
	public String getName() {
		return name;
	}
	
	/**
	 * use MOdel to get all Requested Measure, Dimension and DimensionLevel object
	 * and those lists to Rebuild the IOperand matching the script field
	 * using MeasureParser parser = new MeasureParser(mes, lvls, dims);
	 * @param model
	 */
	public abstract void build(Object model);
//	{
//		try{
//						
//			List<Measure> mes = new ArrayList<Measure>();
//			List<DimensionLevel> lvls = new ArrayList<DimensionLevel>();
//			List<Dimension> dims = new ArrayList<Dimension>();
//			
//			for(IDataStream t : dataSource.getDataStreams()){
//				for(IDataStreamElement e : t.getElements()){
//					if (e.getType() == IDataStreamElement.MEASURE_TYPE){
//						mes.add(new Measure(e));
//					}
//				}
//			}
//			
//			
//			for(IResource r : model.getResources()){
//				if (r instanceof StructureDimension){
//					if (((StructureDimension)r).getDataSource() == dataSource){
//						dims.add(new Dimension((StructureDimension)r));
//						for(int i = 0; i < ((StructureDimension)r).getLevels().size(); i++){
//							lvls.add(new DimensionLevel((StructureDimension)r, i));
//						}
//						
//					}
//				}
//			}
//			
//			MeasureParser parser = new MeasureParser(mes, lvls, dims);
//			operand = parser.readChunk(IOUtils.toInputStream(getScript()));
//			
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}

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
	
	
	
}
