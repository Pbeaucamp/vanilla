package bpm.document.management.core.model;

import java.io.Serializable;

public class InterfaceSetter implements Serializable{

	private static final long serialVersionUID = 1L;

	private String firstInterface="login";
	private String firstInterfaceValue="none";
	private String firstInterfaceEmail="none";
	
	public String getFirstInterfaceValue() {
		return firstInterfaceValue;
	}
	public void setFirstInterfaceValue(String firstInterfaceValue) {
		this.firstInterfaceValue = firstInterfaceValue;
	}
	public String getFirstInterface() {
		return firstInterface;
	}
	public void setFirstInterface(String firstInterface) {
		this.firstInterface = firstInterface;
	}
	public String getFirstInterfaceEmail() {
		return firstInterfaceEmail;
	}
	public void setFirstInterfaceEmail(String firstInterfaceEmail) {
		this.firstInterfaceEmail = firstInterfaceEmail;
	}
}