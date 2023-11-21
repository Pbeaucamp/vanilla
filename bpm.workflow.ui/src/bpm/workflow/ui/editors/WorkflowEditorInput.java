package bpm.workflow.ui.editors;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.xml.sax.SAXException;

import bpm.workflow.runtime.model.WorkflowDigester;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;

/**
 * Editor of the workflow
 * @author CHARBONNIER, MARTIN
 *
 */
public class WorkflowEditorInput implements IEditorInput {
	private String fileName;
	private WorkflowModel workflowModel;
	private Integer directoryItemId;

	
	/**
	 * create an Editor from a workflow file
	 * @param file
	 */
	public WorkflowEditorInput(File file) {
		this.fileName = file.getAbsolutePath();
		
		rebuildModel();
	}
	
	public WorkflowEditorInput(String modelXml, Integer directoryItemId) {
		rebuildModel(modelXml);
		this.directoryItemId = directoryItemId;
	}

	private void rebuildModel(String modelXml) {
		try {
			workflowModel = WorkflowDigester.getModel(WorkflowModel.class.getClassLoader(), modelXml);
			
			for(Variable var : workflowModel.getVariables()) {
				ListVariable.getInstance().addVariable(var);
			}
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		};
	}

	/**
	 * create a new WorkflowModel
	 * @param fileName
	 */
	public WorkflowEditorInput(String fileName, WorkflowModel workflowModel) {
		this.fileName = fileName;
		this.workflowModel = workflowModel;
	}
	


	private void rebuildModel() {
		try {
			workflowModel = WorkflowDigester.getModel(WorkflowModel.class.getClassLoader(), new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		};
		
	}

	
	public boolean exists() {
		return fileName != null;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	
	/**
	 * Name of the editor is the name of the file which store the model
	 */
	public String getName() {
		return workflowModel.getName();
	}
	
	public void setFileName(String name) {
		this.fileName = name;
	}
	
	public String getFileName() {
		return this.fileName;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return workflowModel.getName();
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * 
	 * @return the workflowmodel
	 */
	public WorkflowModel getWorkflowModel() {
		return workflowModel;
	}

	/**
	 * Set the workflowmodel
	 * @param workflowModel
	 */
	public void setWorkflowModel(WorkflowModel workflowModel) {
		this.workflowModel = workflowModel;
	}

	public Integer getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(Integer directoryItemId) {
		this.directoryItemId = directoryItemId;
	}
	
	

}
