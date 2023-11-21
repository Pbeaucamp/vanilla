/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.result.impl;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.ResultLine;
import bpm.united.olap.api.result.ResultPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Olap Result</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpm.united.olap.api.result.impl.OlapResultImpl#getLines <em>Lines</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OlapResultImpl extends EObjectImpl implements OlapResult {
	/**
	 * The cached value of the '{@link #getLines() <em>Lines</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLines()
	 * @generated
	 * @ordered
	 */
	protected EList<ResultLine> lines;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected OlapResultImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ResultPackage.Literals.OLAP_RESULT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<ResultLine> getLines() {
		if (lines == null) {
			lines = new EObjectResolvingEList<ResultLine>(ResultLine.class, this, ResultPackage.OLAP_RESULT__LINES);
		}
		return lines;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ResultPackage.OLAP_RESULT__LINES:
				return getLines();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ResultPackage.OLAP_RESULT__LINES:
				getLines().clear();
				getLines().addAll((Collection<? extends ResultLine>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ResultPackage.OLAP_RESULT__LINES:
				getLines().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ResultPackage.OLAP_RESULT__LINES:
				return lines != null && !lines.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	@Override
	public String getHtml() {
		String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">";
		html += "<html>\n";
		html += "<head>\n";
//		html += "	<link rel=\"stylesheet\" href=\"grid.css\">\n";
		html += getCss();
		html += "</head>\n";
		html += "<body>\n";
		html += "<table class=\"cubeView\">\n";
		html += "	<tbody>\n";
		for(ResultLine line : lines) {
			html += "		" + line.getHtml();
		}
		html += "	</tbody>\n";
		html += "</table>\n";
		html += "</body>\n";
		html += "</html>\n";
		return html;
	}

	private String getCss() {
		String css = "   <style>\n";
		css += "	.cubeView {\n";
		css += "		vertical-align: middle;\n";
		css += "		table-layout: fixed;\n";
		css += "		border-collapse: collapse;\n";
		css += "	}\n";
		css += "	.gridItem {\n";
		css += "		display: table-cell;\n";
		css += "		font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n";
		css += "		font-size: 11px;\n";
		css += "		text-align: center;\n";
		css += "		vertical-align: middle;\n";
		css += "		overflow: hidden;\n";
		css += "		white-space: nowrap;\n";
		css += "		width: 100px;\n";
		css += "		height: 20px;\n";
		css += "		border: 2px groove;\n";
		css += "	}\n";
		css += "	.gridItemValue {\n";
		css += "		background-color: white;\n";
		css += "		cursor: pointer;\n";
		css += "	}\n";
		css += "	.gridItemElement {\n";
		css += "		table-layout: fixed;\n";
		css += "		margin: auto;\n";
		css += "		background-color: #C3DAF9;\n";
		css += "	}\n";
		css+= "   </style>\n";
		return css;
	}

	private int nbMeasures;
	
	@Override
	public int getNbMeasures() {
		return nbMeasures;
	}

	@Override
	public void setNbMeasures(int nbMeasures) {
		this.nbMeasures = nbMeasures;
	}

} //OlapResultImpl
