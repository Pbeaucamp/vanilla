package bpm.gateway.ui.gef.mapping.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Transformation;

public class MappingModel {
	
	private List<StreamModel> childs = new ArrayList<StreamModel>();
	
	public MappingModel(DefaultStreamDescriptor left, DefaultStreamDescriptor right, HashMap<String, String> maps, Transformation transformation){
		childs.add(new StreamModel(left, left.getTransformationName(0), transformation));
		childs.get(0).setX(50);
		childs.get(0).setY(50);
		childs.get(0).setParent(this);
		
		childs.add(new StreamModel(right, right.getTransformationName(0), transformation));
		childs.get(1).setX(150);
		childs.get(1).setY(50);
		childs.get(1).setParent(this);
		
		for(String key : maps.keySet()){
			if (maps.get(key) != null){
				
				FieldModel input = null;
				FieldModel output = null;
				for(FieldModel field : childs.get(0).getFields()){
					if(key.indexOf("::") > -1){ //$NON-NLS-1$
						String elName = (field.getFieldOrigin() + "::" + field.getFieldName()).replace("\n", "").replace("\r", "");
						if (elName.toLowerCase().equals(key.toLowerCase())){ //$NON-NLS-1$
							input = field;
							break;
						}
					}
					else {
						String elName = field.getFieldName().replace("\n", "").replace("\r", "");
						if (elName.toLowerCase().equals(key.toLowerCase())){
							input = field;
							break;
						}
					}
				}
				
				for(FieldModel field : childs.get(1).getFields()){
					if(field.getFieldName().equals(maps.get(key))){
						output = field;
						break;
					}
				}
				try {
					Relation r = new Relation(input, output,null);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}				
	}
	
		
	public List<StreamModel> getChildren(){
		return childs;
	}
	
}
