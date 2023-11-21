package bpm.document.management.core.model.aklademat;

import bpm.document.management.core.model.ILog;

public class ChorusLog implements ILog {

	private static final long serialVersionUID = 1L;

	private int foundBills;
	private int nbNew;
	private int nbModify;
	
	public ChorusLog() { }
	
	public ChorusLog(int foundBills, int nbNew, int nbModify) {
		this.foundBills = foundBills;
		this.nbNew = nbNew;
		this.nbModify = nbModify;
	}

	public int getFoundBills() {
		return foundBills;
	}

	public void setFoundBills(int foundBills) {
		this.foundBills = foundBills;
	}

	public int getNbNew() {
		return nbNew;
	}

	public void setNbNew(int nbNew) {
		this.nbNew = nbNew;
	}

	public int getNbModify() {
		return nbModify;
	}

	public void setNbModify(int nbModify) {
		this.nbModify = nbModify;
	}

}
