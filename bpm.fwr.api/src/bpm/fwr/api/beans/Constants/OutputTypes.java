package bpm.fwr.api.beans.Constants;

import java.io.Serializable;

public final class OutputTypes implements Serializable {

	public static final String PDF = "PDF";
	public static final String HTML = "HTML";
	public static final String EXCEL = "Excel";
	public static final String EXCEL_PLAIN = "Excel (Plain List)";
	public static final String DOC = "DOC";
	
	public static final String[] getOutputs() {
		return new String[]{PDF, HTML, EXCEL, EXCEL_PLAIN, DOC};
	}
	
}
