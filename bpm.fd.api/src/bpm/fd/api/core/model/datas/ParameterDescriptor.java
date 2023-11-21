package bpm.fd.api.core.model.datas;

import bpm.fd.api.internal.ILabelable;

public class ParameterDescriptor implements ILabelable{
	private int position;
	private int mode;
	private String name;
	private String label;
	private int type;
	private String typeName;
	private DataSet dataSet;
	
	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	 
	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}
	
	public DataSet getDataSet(){
		return dataSet;
	}
	
	protected ParameterDescriptor(DataSet dataSet, int position, int mode, String name, int type, String typeName) {
		super();
		this.dataSet = dataSet;
		this.position = position;
		this.mode = mode;
		this.name = name;
		this.type = type;
		this.typeName = typeName;
	}
	
	public void setLabel(String name){
		this.label = name;
	}
	
	
	
	public String getLabel(){
		if (label == null){
			return getName();
		}
		return label;
	}
}
