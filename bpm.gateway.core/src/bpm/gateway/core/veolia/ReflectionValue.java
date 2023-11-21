package bpm.gateway.core.veolia;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import bpm.vanilla.platform.core.beans.resources.ClassField.TypeField;

public class ReflectionValue {

	private TypeField type;
	private Object value;
	
	public ReflectionValue(TypeField type, Object value) {
		this.type = type;
		this.value = value;
	}

	public TypeField getType() {
		return type;
	}

	public Date getValueAsDate() {
		if (value instanceof Date) {
			return (Date) value;
		}
		return VEHelper.toDate((XMLGregorianCalendar) value);
	}
	
	public String getValueAsString() {
		return value != null ? value.toString() : "";
	}
	
	public double getValueAsDouble() {
		return Double.parseDouble(value.toString());
	}

	public boolean isDefine() {
		return value != null && !value.toString().isEmpty();
	}
}
