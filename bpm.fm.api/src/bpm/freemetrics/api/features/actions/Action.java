package bpm.freemetrics.api.features.actions;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import bpm.freemetrics.api.digester.beans.FmActions;
import bpm.freemetrics.api.digester.beans.FmDigAction;
import bpm.freemetrics.api.digester.engine.DigestFM_DB;

/**
 * @author Belgarde
 *
 */
public class Action {

	private int id = 0;

	private Date acCreationDate;
	private String name;
	private Integer acType;
	private String acComment;
	private String acDefinition;
	private Integer acAssocId;


	public Action() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the acCreationDate
	 */
	public Date getAcCreationDate() {
		return acCreationDate;
	}

	/**
	 * @param acCreationDate the acCreationDate to set
	 */
	public void setAcCreationDate(Date acCreationDate) {
		this.acCreationDate = acCreationDate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the acType
	 */
	public Integer getAcType() {
		return acType;
	}

	/**
	 * @param acType the acType to set
	 */
	public void setAcType(Integer acType) {
		this.acType = acType;
	}

	/**
	 * @return the acComment
	 */
	public String getAcComment() {
		return acComment;
	}

	/**
	 * @param acComment the acComment to set
	 */
	public void setAcComment(String acComment) {
		this.acComment = acComment;
	}

	/**
	 * @return the acDefinition
	 */
	public String getAcDefinition() {
		return acDefinition;
	}


	public void setAcDefinition(List<String> recipient,String msg,String subject) {

		StringBuffer sBuff = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		sBuff.append("<actions>\n");

		for (String string : recipient) {

			sBuff.append("\t<action>\n");
			sBuff.append("\t\t<component>"+acType+"</component>\n");
			sBuff.append("\t\t<config>\n");
			sBuff.append("\t\t<to>"+string+"</to>\n");
			sBuff.append("\t\t<from>freemetrics plateforme</from>\n");
			sBuff.append("\t\t<subject>"+subject+"</subject>\n");
			sBuff.append("\t\t<text>"+msg+"</text>\n");
			sBuff.append("\t\t</config>\n");
			sBuff.append("\t</action>\n\n");
		}


		sBuff.append("</actions>");

		setAcDefinition(sBuff.toString());
	}


	/**
	 * @param acDefinition the acDefinition to set
	 */
	private void setAcDefinition(String acDefinition) {
		this.acDefinition = acDefinition;
	}

	/**
	 * @return the acMetricsId
	 */
//	public Integer getAcMetricsId() {
//		return acMetricsId;
//	}
//
//	/**
//	 * @param acMetricsId the acMetricsId to set
//	 */
//	public void setAcMetricsId(Integer acMetricsId) {
//		this.acMetricsId = acMetricsId;
//	}



	public Vector<FmDigAction> getActionsToDo() {
				
		FmActions acts = DigestFM_DB.digesterActionsDefinition(acDefinition);
		Vector<FmDigAction> res = (acts != null) ? acts.getActions() : new Vector<FmDigAction>();
		
		return res;
	}

	/**
	 * @return the acAssocId
	 */
	public Integer getAcAssocId() {
		return acAssocId;
	}

	/**
	 * @param acAssocId the acAssocId to set
	 */
	public void setAcAssocId(Integer acAssocId) {
		this.acAssocId = acAssocId;
	}

}
