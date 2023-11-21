package bpm.gateway.ui.gef.mapping.model;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.SimpleMappingTransformation;

public class StreamModel {
	private String name;
	private DefaultStreamDescriptor stream;
	private List<FieldModel> fields = new ArrayList<FieldModel>();
	
	private MappingModel parent;
	
	private int x, y;
	
	public StreamModel(DefaultStreamDescriptor stream, String name, Transformation transformation){
		this.stream = stream;
		this.name = name;
		
		for(StreamElement e : stream.getStreamElements()){
			FieldModel f = new FieldModel(e, transformation);
			f.setParent(this);
			fields.add(f);
		}
		
	}

	
	
	public String getName(){
		return name;
	}
	
	public int getX() {
		return x;
	}



	public void setX(int x) {
		this.x = x;
	}



	public int getY() {
		return y;
	}



	public void setY(int y) {
		this.y = y;
	}



	public List<FieldModel> getFields() {
		return fields;
	}



	public MappingModel getParent() {
		return parent;
	}
	public void setParent(MappingModel parent){
		this.parent = parent;
	}
	
}
