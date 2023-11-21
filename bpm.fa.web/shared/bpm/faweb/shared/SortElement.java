package bpm.faweb.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SortElement implements IsSerializable {

	public static final String TYPE_ASC = "Asc";
	public static final String TYPE_DESC = "Desc";
	
	public static final String TYPE_MEASURE_8020 = "80/20";
	public static final String TYPE_MEASURE_TOP5 = "Top 5";
	
	
	public static final List<String> TYPES_MEASURES = new ArrayList<String>();
	static {
		TYPES_MEASURES.add(TYPE_ASC);
		TYPES_MEASURES.add(TYPE_DESC);
		TYPES_MEASURES.add(TYPE_MEASURE_8020);
		TYPES_MEASURES.add(TYPE_MEASURE_TOP5);
	}
	
	public static final List<String> TYPES = new ArrayList<String>();
	static{
		TYPES.add(TYPE_ASC);
		TYPES.add(TYPE_DESC);
	}
	
	private String uname;
	private String type;

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public boolean equals(Object obj) {
		return uname.compareTo(((SortElement)obj).getUname()) == 0;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
}
