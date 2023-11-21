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
 * A representation of the model object '<em><b>Line</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpm.united.olap.api.result.ResultLine#getCells <em>Cells</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpm.united.olap.api.result.ResultPackage#getResultLine()
 * @model
 * @generated
 */
public interface ResultLine extends EObject, Serializable {
	/**
	 * Returns the value of the '<em><b>Cells</b></em>' reference list.
	 * The list contents are of type {@link bpm.united.olap.api.result.ResultCell}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Cells</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cells</em>' reference list.
	 * @see bpm.united.olap.api.result.ResultPackage#getResultLine_Cells()
	 * @model
	 * @generated
	 */
	List<ResultCell> getCells();

	String getHtml();
} // ResultLine
