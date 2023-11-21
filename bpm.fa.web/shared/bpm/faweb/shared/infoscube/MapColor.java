package bpm.faweb.shared.infoscube;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MapColor implements IsSerializable {

	private String name;
	private String hexa;
	private int min;
	private int max;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHexa() {
		return hexa;
	}

	public void setHexa(String hexa) {
		this.hexa = hexa;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

}
