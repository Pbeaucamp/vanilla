package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PastellData implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ObjectType {
		STRING(0), DATE(1), BOOLEAN(2), INT(3), DOUBLE(4);

		private int type;

		private static Map<Integer, ObjectType> map = new HashMap<Integer, ObjectType>();
		static {
			for (ObjectType docType : ObjectType.values()) {
				map.put(docType.getType(), docType);
			}
		}

		private ObjectType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static ObjectType valueOf(int type) {
			return map.get(type);
		}
	}

	private ObjectType type;
	private Serializable value;

	public PastellData() {
	}

	public PastellData(ObjectType type, Serializable value) {
		this.type = type;
		this.value = value;
	}

	public ObjectType getType() {
		return type;
	}

	public String getValueAsString() {
		return value != null ? value.toString() : "";
	}

	public Integer getValueAsInt() {
		if (type == ObjectType.INT) {
			return value != null ? (Integer) value : null;
		}

		try {
			return Integer.parseInt(getValueAsString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Date getValueAsDate() throws Exception {
		if (type == ObjectType.DATE) {
			return value != null ? (Date) value : null;
		}
		

		throw new Exception("Not a date type");
	}

	public Double getValueAsDouble() {
		if (type == ObjectType.DOUBLE) {
			return value != null ? (Double) value : null;
		}

		try {
			return Double.parseDouble(getValueAsString());
		} catch (Exception e) {
			
			try {
				//consoleLog(getValueAsString());
				String val = getValueAsString().replace("eur", "").replace("€", "").replace("?", "").replace("$", "").trim();
				//consoleLog(val);
				//DecimalFormat df = new DecimalFormat(); 
				//DecimalFormatSymbols sfs = new DecimalFormatSymbols();
				if(val.contains(",")){
					
					if(val.contains(".")){
						val = val.replace(".", "");
					}
					val = val.replace(',','.'); 
				}
				if(val.contains(" ")){
					val = val.replace(" ",""); 
				}
				//df.setDecimalFormatSymbols(sfs);
				return Double.parseDouble(val);
			} catch (Exception e1) {
				e.printStackTrace();
				return null;
			}
			
		}
	}
	
//	native void consoleLog( String message) /*-{
//	    console.log( "me:" + message );
//	}-*/;
}