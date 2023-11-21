package bpm.document.management.core.model.aklademat;

import bpm.document.management.core.model.ILog;

public class AkladematLog implements ILog {

	private static final long serialVersionUID = 1L;

	private int akladematEntityId;
	private String pastellId;
	private String cegidId;
	private int aklaboxId;
	private String siret;

	public AkladematLog() {
	}

	public int getAkladematEntityId() {
		return akladematEntityId;
	}

	public void setAkladematEntityId(int akladematEntityId) {
		this.akladematEntityId = akladematEntityId;
	}

	public String getPastellId() {
		return pastellId;
	}

	public void setPastellId(String pastellId) {
		this.pastellId = pastellId;
	}

	public String getCegidId() {
		return cegidId;
	}

	public void setCegidId(String cegidId) {
		this.cegidId = cegidId;
	}

	public int getAklaboxId() {
		return aklaboxId;
	}

	public void setAklaboxId(int aklaboxId) {
		this.aklaboxId = aklaboxId;
	}

	public String getSiret() {
		return siret;
	}

	public void setSiret(String siret) {
		this.siret = siret;
	}

}
