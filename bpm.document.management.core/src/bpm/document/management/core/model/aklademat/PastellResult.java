package bpm.document.management.core.model.aklademat;

import java.io.Serializable;

public class PastellResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String status;
	private boolean formulaireOk;
	private String message;
	
	public PastellResult() { }
	
	public PastellResult(String status, boolean formulaireOk, String message) {
		this.status = status;
		this.formulaireOk = formulaireOk;
		this.message = message;
	}
	
	public PastellResult(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isFormulaireOk() {
		return formulaireOk;
	}

	public void setFormulaireOk(boolean formulaireOk) {
		this.formulaireOk = formulaireOk;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
