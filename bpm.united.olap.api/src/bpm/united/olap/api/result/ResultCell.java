/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.united.olap.api.result;

import java.io.Serializable;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see bpm.united.olap.api.result.ResultPackage#getResultCell()
 * @model abstract="true"
 * @generated
 */
public interface ResultCell extends EObject, Serializable {
	
	String getHtml();
	
	String getType();
	
	 
} // ResultCell
