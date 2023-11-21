package groupviewer;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import adminbirep.Activator;


/**
 * Load DirectoryItem data in three Lists :
 * 
 * - A full DirectoryItem List : Containing all the items
 * - A Depends List by DirectoryItem : Containing all the depends DirectoryItem list for one DirectoryItem.
 * - A Needs List by DirectoryItem : Containing all the needed DirectoryItem list for one DirectoryItem.
 * 
 * Possible errors codes:
 *  
 *   0 : OK
 *   1 : Not Set.
 * 	 2 : Failed to Load DirectoryItem list.
 * 	 3 : Failed to Load DirectoryItem depends list.
 * 	 4 : Failed to Load DirectoryItem needs list. 	
 * 
 * @author admin
 *
 */
public class CrossRefDataLoader {
	private Map<Integer,RepositoryItem> fullList;
	private Map<RepositoryItem,Collection<RepositoryItem>> depandsList;
	private Map<RepositoryItem,Collection<RepositoryItem>> needsList;
	private int errorCode = 1;
	private Date loadDate;
	
	private static Map<Integer,String> errorMess = new HashMap<Integer,String>();
	static {
		errorMess.put(1, "Not set"); //$NON-NLS-1$
		errorMess.put(2, Messages.CrossRefDataLoader_1);
		errorMess.put(3, Messages.CrossRefDataLoader_2);
		errorMess.put(4, Messages.CrossRefDataLoader_3);
		errorMess.put(9, Messages.CrossRefDataLoader_4);
	}
	
	class Loader extends Thread{
		/**
		 * A thread which loads all depends, needs & DirectoryItems.
		 * Set steps errors code.
		 */
		@Override
		public void run() {
			try {
				// Load the full List
				setErrorCode(2);
				Repository rep = null;
				rep = new Repository(Activator.getDefault().getRepositoryApi());
				setFullItemList(rep.getAllItems());
				setErrorCode(3);
				Map<RepositoryItem,Collection<RepositoryItem>> depands = new HashMap<RepositoryItem, Collection<RepositoryItem>>();
				for (RepositoryItem item : fullList.values()) {	
					List<RepositoryItem> depend = Activator.getDefault().getRepositoryApi().getRepositoryService().getDependantItems(item);
					depands.put(item, depend);
				}
				setDepandsItemList(depands);
				setErrorCode(4);
				Map<RepositoryItem,Collection<RepositoryItem>> needs = new HashMap<RepositoryItem, Collection<RepositoryItem>>();
				for (RepositoryItem item : fullList.values()) {
					List<RepositoryItem> depend = Activator.getDefault().getRepositoryApi().getRepositoryService().getNeededItems(item.getId());
					needs.put(item, depend);
				}
				setNeedsItemList(needs);
				// Set last update date.
				setLoadDate(new Date());
				setErrorCode(0);
				
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Cross References Loader
	 * Launch a new thread witch loads all directory's items.
	 * Make needed list.
	 * Make depends list.
	 */
	public CrossRefDataLoader(){
		Loader loader = new Loader();
		loader.start();
		try {
			loader.join(); // wait for the load thread to die.
		} catch (Throwable e) {
			e.printStackTrace();
			setErrorCode(9);
		}
	}
	
	/*
	 * FullItemList.
	 */
	private void setFullItemList(Collection<RepositoryItem> IdirList) {
		if (fullList == null)
			fullList = new HashMap<Integer, RepositoryItem>();
		for (RepositoryItem item : IdirList){
			fullList.put(item.getId(), item);
		}
	}
	/**
	 * Get the full DirectoryItem list.
	 * @return - The full List.
	 */
	public Collection<RepositoryItem> getFullItemList() {
		return (errorCode == 0)? fullList.values() : null;
	}
	/**
	 * Get a DirectoryItem by his id.
	 * @param id
	 * @return
	 */
	public RepositoryItem getItemByID(int id){
		if (fullList.containsKey(id))
			return fullList.get(id);
		return null;
	}
	/*
	 * DepandsItemList.
	 */
	private void setDepandsItemList(Map<RepositoryItem,Collection<RepositoryItem>> depandsList) {
		this.depandsList = depandsList;
	}
	/**
	 * Get the Depend DirectoryItem Map group by DirectoryItem.
	 * @return - The Depends Map.
	 */
	public Map<RepositoryItem,Collection<RepositoryItem>> getDepandsItemList() {
		return (errorCode == 0)? depandsList : null;
	}
	/**
	 * Get the List of depends DirectoryItem for one item.
	 * @param item - The key item.
	 * @return - the List of depends.
	 */
	public Collection<RepositoryItem> getDepandsItemList(RepositoryItem item){
		return getDepandsItemList().get(item);
	}
	/*
	 * NeedsItemList.
	 */
	private void setNeedsItemList(Map<RepositoryItem,Collection<RepositoryItem>> needsList) {
		this.needsList = needsList;
	}
	/**
	 * Get the Needed DirectoryItem Map group by DirectoryItem.
	 * @return - The Needed Map.
	 */
	public Map<RepositoryItem,Collection<RepositoryItem>> getNeedsItemList() {
		return (errorCode == 0)? needsList : null;
	}
	/**
	 * Get the List of needs DirectoryItem for one item.
	 * @param item - The key item.
	 * @return - the List of needs.
	 */
	public Collection<RepositoryItem> getNeedsItemList(RepositoryItem item){
		return getNeedsItemList().get(item);
	}
	/*
	 * ErrorCode.
	 */
	private void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * Get Errors Code generate during data loading.
	 * 
	 * Possible errors codes:
	 *   0 : OK
	 *   1 : Not Set.
	 * 	 2 : Failed to Load DirectoryItem list.
	 * 	 3 : Failed to Load DirectoryItem depends list.
	 * 	 4 : Failed to Load DirectoryItem needs list. 	
	 * @return - The error code.
	 */
	public int getErrorCode() {
		return errorCode;
	}
	public String getErrorMessage(){
		return (getErrorCode() > 0)? errorMess.get(getErrorCode()):""; //$NON-NLS-1$
	}
	/*
	 * LoadDate.
	 */
	private void setLoadDate(Date loadDate) {
		this.loadDate = loadDate;
	}
	/**
	 * Get the last update date.
	 * @return - Date.
	 */
	public Date getLoadDate() {
		return loadDate;
	}
	/*
	 * Other.
	 */
	/**
	 * Return the size of all items.
	 * @return - the list size
	 */
	public int getFullListSize(){
		return ( getFullItemList() != null )? getFullItemList().size() : 0 ;
	}
}
