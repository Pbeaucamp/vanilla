package bpm.vanilla.platform.core.repository.services;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.repository.DatasProvider;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

/**
 * This service is used to manage the DataProvider(ODA definition xml) that
 * will be mapped to an item parameter to generate a Parameter Prompt
 * with provided values.
 * 
 * 
 * @author ludo
 *
 */
public interface IDataProviderService {
	public static enum ActionType implements IXmlActionType{
		UPDATE(Level.DEBUG), LINK(Level.DEBUG), FIND_ITEM_PROVIDERS(Level.DEBUG), FIND_PROVIDER(Level.DEBUG), LIST_PROVIDER(Level.DEBUG), 
		DELETE_PROVIDER(Level.DEBUG), CREATE_PROVIDER(Level.DEBUG), BREAK_LINK(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
		
	}
		/**
		 * create a DatasProvider and return its id 
		 * User should call dp.setId() with the returned value to be synchronized
		 * 	
		 * @param dp
		 * @return the id of the created DatasProvider
		 * @throws Exception
		 */
		public int createDatasProvider(DatasProvider dp) throws Exception;
		
		/**
		 * 
		 * @param id
		 * @return the DatasProvider with the given id
		 * @throws Exception
		 */
		public DatasProvider getDatasProvider(int id) throws Exception;
		
		/**
		 * 
		 * @return all the registered DatasProvider
		 * @throws Exception
		 */
		public List<DatasProvider> getAll() throws Exception;

		/**
		 * Link a DatasProviderId to a directoryItem
		 * @param datasProviderID
		 * @param itemId : directoryItemId
		 * @throws ServerException 
		 * @throws IOException 
		 */
		public String link(int datasProviderID, int itemId) throws Exception ;
		
		
		/**
		 * Return the list of the DatasProvider link to the selected Item (Report)
		 * @param itemId
		 * @return
		 * @throws IOException
		 * @throws ServerException
		 * @throws DocumentException
		 */
		public List<DatasProvider> getForItem(int itemId) throws Exception;

		/**
		 * update a DatasProvider definition and properties
		 * @param element
		 * @throws Exception
		 */
		public void update(DatasProvider element) throws Exception;

		/**
		 * destroy the link between a DataProvider and a directoryItem
		 * @param itemId : directoryItemid
		 * @param datasProviderId
		 * @throws Exception
		 */
		public void breakLink(int itemId, int datasProviderId) throws Exception;

		/**
		 * remove the DatasProvider from the repository
		 * @param dp
		 * @throws Exception
		 */
		public void delete(DatasProvider dp) throws Exception;

}
