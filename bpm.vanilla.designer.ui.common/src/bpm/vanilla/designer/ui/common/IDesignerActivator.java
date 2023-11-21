package bpm.vanilla.designer.ui.common;

import java.beans.PropertyChangeListener;
import java.io.InputStream;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * simple interface to be implemented by all Designers Activator's class
 * to be able to share a common repositoryUi connection
 * 
 * 
 * 
 * @author ludo
 *
 */
public interface IDesignerActivator<A> {
	
		public String getApplicationId();
	
		/**
		 * 
		 * @return the actual RepositoryConnection
		 */
		public IRepositoryApi getRepositoryConnection();
		
		/**
		 * set the actual RepositoryConnection with the given connection
		 * @param sock
		 */
		public void setRepositoryContext(IRepositoryContext ctx);
		
		/**
		 * 
		 * @return fale if ggetRepositoryConnection() return null
		 */
		public boolean isRepositoryConnectionDefined();
		
		/**
		 * 
		 * @return the current Active Model Object in the designer
		 */
		public A getCurrentModel();
		
		/**
		 * 
		 * @return the Xml String reprensetation of the currentModel
		 */
		public String getCurrentModelXml();
		
		/**
		 * 
		 * @return the id of the currentModel on the currentConnection or null if it does not come from a repository
		 */
		public Integer getCurrentModelDirectoryItemId();
		
		/**
		 * parse the given xml and set the currentModel with its return
		 * @param modelObjectXmlDefinition
		 * @param directoryItemId
		 * @throws Exception
		 */
		public void setCurrentModel(String modelObjectXmlDefinition, Integer directoryItemId) throws Exception;
		
		/**
		 * parse the given xml and set the currentModel with its return
		 * @param modelObjectXmlDefinition
		 * @param directoryItemId
		 * @throws Exception
		 */
		public void setCurrentModel(InputStream modelObjectXmlDefinition, Integer directoryItemId) throws Exception;
		
		/**
		 * Set the current DirectoryItemId
		 * 
		 * @param directoryItemId
		 * @throws Exception
		 */
		public void setCurrentDirectoryItemId(Integer directoryItemId) throws Exception;
		
		/**
		 * save the current Model and return the File object where it is stored
		 * @return
		 * @throws Exception
		 */
		public Object saveCurrentModel() throws Exception;
		
		/**
		 * set the currentModel with the given object
		 * @param oldModel
		 */
		public void setCurrentModel(Object oldModel);

		/**
		 * 
		 * @return the current file location of the currentModel in the designer or null
		 */
		public String getCurrentModelFileName();

		
		/**
		 * add a listener that should fire an event when a model is closed to perform
		 * some checking if the model have been checkedout and should be released
		 * @param listener
		 */
		public void addLoadModelListener(PropertyChangeListener listener);

				
		
		/**
		 * 
		 * @return The IRepositoryConnection Type constant for managed models
		 */
		public int getRepositoryItemType();

		public IRepositoryContext getRepositoryContext();
}
