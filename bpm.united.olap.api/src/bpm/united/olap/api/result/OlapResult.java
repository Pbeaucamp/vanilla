/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.result;

import java.io.Serializable;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Olap Result</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.result.OlapResult#getLines <em>Lines</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.result.ResultPackage#getOlapResult()
 * @model
 * @generated
 */
public interface OlapResult extends EObject, Serializable{

	/**
	 * Returns the value of the '<em><b>Lines</b></em>' reference list.
	 * The list contents are of type {@link bpm.united.olap.api.result.ResultLine}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Lines</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lines</em>' reference list.
	 * @see bpm.united.olap.api.result.ResultPackage#getOlapResult_Lines()
	 * @model
	 * @generated
	 */
	List<ResultLine> getLines();
	
	String getHtml();
	
	void setNbMeasures(int nbMeasures);
	
	int getNbMeasures();
} // OlapResult
