package bpm.birt.wms.map.core.reportitem;

import java.io.Serializable;

/**
 * A Range of values to apply Color on zone in a FusionMap render
 * @author ludo
 *
 */
public class ColorRange implements Serializable {

	private String name;
	private String hex = "0000000";
	private Integer min;
	private Integer max;
	
	public ColorRange(){
		
	}
	
	public ColorRange(String n, String h, Integer mi, Integer ma){
		this.name = n;
		this.hex = h;
		this.min = mi;
		this.max = ma;
	}
	
	public String getName() {
		if (name == null){
			return "";
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHex() {
		return hex;
	}

	public void setHex(String hex) {
		this.hex = hex;
	}

	public Integer getMin() {
		if (min == null){
			return Integer.MIN_VALUE;
		}
		return min;
	}

	public void setMin(Integer min) {
		
		this.min = min;
	}

	public Integer getMax() {
		if (max == null){
			return Integer.MAX_VALUE;
		}
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getName());
		if (min == null){
			buf.append("]inf");
		}
		else{
			buf.append("[" + getMin());
		}
		buf.append(",");
		if (min == null){
			buf.append("inf[");
		}
		else{
			buf.append( getMax() + "]");
		}
				
		return buf.toString();
	}
}
