/*
 * Copyright 2007 BPM-conseil.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package bpm.vanilla.portal.client.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.repository.PortailItemFmdtDriller;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapDataSource;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.MapZone;
import bpm.vanilla.platform.core.beans.Widgets;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.shared.MapColumns;
import bpm.vanilla.portal.shared.MapFeatures;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Belgarde
 *
 */
@RemoteServiceRelativePath("BiPortalService")
public interface BiPortalService extends RemoteService {
	
	public static class Connect{
		private static BiPortalServiceAsync instance;
		public static BiPortalServiceAsync getInstance(){
			if(instance == null){
				instance = (BiPortalServiceAsync) GWT.create(BiPortalService.class);
			}
			return instance;
		}
	}
	
	public String runBIRTFromRep(int itemId) throws ServiceException;

	public PortailRepositoryDirectory getMyWatchList() throws ServiceException;

	public PortailRepositoryDirectory getLastConsulted(String group) throws ServiceException;

	public boolean addToMyWatchList(int directoryItemId, int type) throws ServiceException;
	
	public void addOpenItem(int itemId) throws ServiceException;
	
	public PortailRepositoryDirectory getOpenItems() throws ServiceException;
	
	public void removeOpenItem(int itemId) throws ServiceException;
	
	public String removeToMyWatchList(int id) throws ServiceException;
	
	public PortailRepositoryItem getItemDto(int itemId) throws ServiceException;
	
	/**
	 * This method return the first level of the repository instead of getting all items in the repository
	 * (Better performance)
	 * @return
	 * @throws ServiceException
	 */
	public PortailRepositoryDirectory getRepositoryContents() throws ServiceException;
	
	/**
	 * This method return the all the content of the repository for a given type
	 * The loading can be long sometimes
	 * @return
	 * @throws ServiceException 
	 */
	public PortailRepositoryDirectory getRepositoryContent(int typeRepository) throws ServiceException;
	
	public List<IRepositoryObject> getDirectoryContent(PortailRepositoryDirectory dir) throws ServiceException;
	
	public void purgeLastUsed() throws ServiceException;

	public void addPersoDialog(int parentId, String name, String comment) throws Exception;

	public List<Widgets> getWidgets() throws Exception;
	
	public List<Comment> getComments(int objectId, int type) throws ServiceException;
	
	public void addOrEditJob(Job job) throws ServiceException;
	
	public void deleteJob(Job job) throws ServiceException;
	
	public List<Job> getJobs() throws ServiceException;

	public void launchJob(Job job) throws ServiceException;

	public List<JobInstance> getJobHistoric(Job job) throws ServiceException;
	
	public void setShowAllRepository(boolean showAllRepository) throws ServiceException;
	
	public boolean showAllRepository() throws ServiceException;
	
	public boolean subscribeToItem(PortailRepositoryItem item) throws ServiceException;
	
	public String createDisconnectedPackage(String packageName, int limitRows, List<RepositoryItem> items) throws ServiceException;

	public void deleteItem(int itemId) throws ServiceException;

	public PortailRepositoryDirectory searchRepository(String search) throws ServiceException;

	public List<MapVanilla> getMaps() throws ServiceException;

	public void addOrEditMap(MapVanilla map) throws ServiceException;

	void deleteMap(MapVanilla selectedMap) throws ServiceException;
	
	public List<String> getLesDrivers() throws ServiceException, Exception;

	List<MapDataSource> getMapsDataSource() throws ServiceException;

	public void saveDataSet(MapDataSet dtS) throws ServiceException;

	List<MapDataSource> getDataSourceByName(String name)
			throws ServiceException;

	void deleteMapDataSet(MapDataSet mapDataSet) throws ServiceException;
	
	public   ArrayList<MapColumns> getDataSetMetaData(String login, String password, String url, String driver, String query) throws ServiceException;

	public List<MapFeatures> getOsmValues(MapVanilla selectedMap) throws Exception;
	
	public List<MapFeatures> getOsmValuesbyDataSet(MapVanilla selectedMap, MapDataSet selectedDataSet) throws Exception;

	public void addOrEditAlert(Alert alert) throws ServiceException;

	public void deleteAlert(Alert alert) throws ServiceException;

	public void deleteSubscriber(Subscriber subscriber) throws ServiceException;

	public void saveWidgets(List<Widgets> widgets) throws Exception;

	public Map<RepositoryItem, List<Comment>> getCommentsbyAllItems() throws ServiceException;

	public List<String> getAllRepositoryTypes() throws Exception;

	public List<PortailItemFmdtDriller> loadFmdtQueries(int id) throws ServiceException;

	public Map<String, MapZone> getMapZone(MapVanilla map) throws ServiceException;

	public void saveMapMetadataMappings(ArrayList<MapZone> zones, MapVanilla map) throws ServiceException;
}
