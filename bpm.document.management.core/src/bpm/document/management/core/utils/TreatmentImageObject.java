package bpm.document.management.core.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TreatmentImageObject implements Serializable{
	

	private static final long serialVersionUID = 1L;
	
	public enum Type{
		GRAYFSCALE,
		BRIGHTNESS,
		CONTRAST,
		ROTATE
	}
	
	private Map<Type, Double> treatments = new HashMap<Type, Double>();
	

	
	public Map<Type, Double> getTreatments() {
		return treatments;
	}

	public void setTreatments(Map<Type, Double> treatments) {
		this.treatments = treatments;
	}

	public void addTreatment(Type type, double value) {
		if(type == Type.GRAYFSCALE){
			if(treatments.containsKey(type)){
				treatments.remove(type);
			} else {
				treatments.put(type, 100.0);
			}
		} else {
			treatments.put(type, value);
		}
		
	}
	
	public String renderCssFilterProperty(){
		return "grayscale("+ ((treatments.containsKey(Type.GRAYFSCALE))?treatments.get(Type.GRAYFSCALE) : 0) + //de 0 à 100
				"%) brightness("+ ((treatments.containsKey(Type.BRIGHTNESS))?treatments.get(Type.BRIGHTNESS)*2 : 100) + //de 0 à 200
				"%) contrast(" + ((treatments.containsKey(Type.CONTRAST))?treatments.get(Type.CONTRAST)*2 : 100) + "%)"; //de 0 à 200
		
	}
	
	public String renderCssTransformProperty(){
		return "rotateZ("+ ((treatments.containsKey(Type.ROTATE))?treatments.get(Type.ROTATE) : 0) + "deg"; //de -180 à 180
		
	}
	
	public void clear(){
		treatments.clear();
	}
}
