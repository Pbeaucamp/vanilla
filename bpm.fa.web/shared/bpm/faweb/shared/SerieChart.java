package bpm.faweb.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SerieChart implements IsSerializable {

	private String name;
	private String value;
	
	public SerieChart(){ }
	
	public SerieChart(String name, String value){
		this.setName(name);
		this.setValue(value);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setValue(String value) {
		try {
			Double val = Double.valueOf(value);
			this.value = String.valueOf(val);
		} 
		catch (Exception e) {
			byte[] b = value.getBytes();
			for(int i=0; i < b.length; i++){
				if(b[i] < 0){
					b[i] = ' ';
				}
			}
			Double val = Double.valueOf(new String(b).replace(" ", "").replace(",", ""));
			this.value = String.valueOf(val);
		}
	}

	public String getValue() {
		return value;
	}
	
	public double getDoubleValue(){
		return Double.parseDouble(value);
	}
}
