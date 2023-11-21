package bpm.fd.api.core.model.datas.filters;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDataFilter;

public class ValueFilter implements IComponentDataFilter{
	public static enum Type{
		SInf("<"), Inf("<="), Eq("="), Diff("!="),
		Sup(">="), SSup(">");
		
		private String label;
		private Type(String label){
			this.label = label;
		}
		public String getType(){
			return label;
		}
	}
	
	private Double value = 0.0;
	private Type type = Type.Eq;
	
	
	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	public Element getElement() {
		Element valFilter = DocumentHelper.createElement("valueFilter");
		valFilter.addAttribute("type", getType().name());
		if (value != null){
			valFilter.addAttribute("value", getValue() + "");
		}
		return valFilter;
	}

	@Override
	public boolean isSatisfied(Object value) {
		
		if (value == null){
			if (getValue() == null && getType() == Type.Eq){
				return true;
			}
			return false;
		}
		switch (type) {
		case Diff:
			return getValue().doubleValue() != (Double.valueOf("" + value));
		case Eq:
			return (Double.valueOf("" + value)).doubleValue() == getValue().doubleValue();
		case Inf:
			return (Double.valueOf("" + value) ).doubleValue() <= getValue().doubleValue() ;
		case SInf:
			return (Double.valueOf("" + value)).doubleValue() < getValue().doubleValue() ;
		case Sup:
			return (Double.valueOf("" + value)).doubleValue() >= getValue().doubleValue();
		case SSup:
			return (Double.valueOf("" + value)).doubleValue() > getValue().doubleValue();
		}
		return false;
	}

	@Override
	public String toString() {

		return type.name() + getValue();
	}
}
