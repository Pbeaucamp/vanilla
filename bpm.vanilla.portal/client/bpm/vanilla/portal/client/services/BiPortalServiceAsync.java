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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BiPortalServiceAsync {

	public void runBIRTFromRep(int itemId, AsyncCallback<String> callback);

	public void getMyWatchList(AsyncCallback<PortailRepositoryDirectory> callback);

	public void getLastConsulted(String group, AsyncCallback<PortailRepositoryDirectory> callback);

	public void addToMyWatchList(int directoryItemId, int type, AsyncCallback<Boolean> callback);

	public void addOpenItem(int itemId, AsyncCallback<Void> callback);

	public void getOpenItems(AsyncCallback<PortailRepositoryDirectory> callback);

	public void removeOpenItem(int itemId, AsyncCallback<Void> callback);

	public void removeToMyWatchList(int id, AsyncCallback<String> callback);

	public void getItemDto(int itemId, AsyncCallback<PortailRepositoryItem> callback);

	public void getRepositoryContents(AsyncCallback<PortailRepositoryDirectory> callback);

	public void getRepositoryContent(int typeRepository, AsyncCallback<PortailRepositoryDirectory> callback);

	public void getDirectoryContent(PortailRepositoryDirectory dir, AsyncCallback<List<IRepositoryObject>> callback);

	public void purgeLastUsed(AsyncCallback<Void> callback);

	public void addPersoDialog(int parentId, String name, String comment, AsyncCallback<Void> asyncCallback);

	public void getWidgets(AsyncCallback<List<Widgets>> asyncCallback);

	public void getComments(int objectId, int type, AsyncCallback<List<Comment>> callback);

	public void addOrEditJob(Job job, AsyncCallback<Void> callback);

	public void deleteJob(Job job, AsyncCallback<Void> callback);

	public void getJobs(AsyncCallback<List<Job>> callback);

	public void launchJob(Job job, AsyncCallback<Void> callback);

	public void getJobHistoric(Job job, AsyncCallback<List<JobInstance>> callback);

	public void setShowAllRepository(boolean showAllRepository, AsyncCallback<Void> callback);

	public void showAllRepository(AsyncCallback<Boolean> callback);

	public void subscribeToItem(PortailRepositoryItem item, AsyncCallback<Boolean> callback);

	public void createDisconnectedPackage(String packageName, int limitRows, List<RepositoryItem> items, AsyncCallback<String> callback);

	public void deleteItem(int itemId, AsyncCallback<Void> asyncCallback);

	public void searchRepository(String search, AsyncCallback<PortailRepositoryDirectory> asyncCallback);

	public void getMaps(AsyncCallback<List<MapVanilla>> callback);

	public void addOrEditMap(MapVanilla map, AsyncCallback<Void> callback);

	public void deleteMap(MapVanilla selectedMap, AsyncCallback<Void> asyncCallback);

	public void getLesDrivers(AsyncCallback<List<String>> callback);

	public void getMapsDataSource(AsyncCallback<List<MapDataSource>> callback);

	public void saveDataSet(MapDataSet dtS, AsyncCallback<Void> callback);

	public void getDataSourceByName(String name, AsyncCallback<List<MapDataSource>> callback);

	public void deleteMapDataSet(MapDataSet mapDataSet, AsyncCallback<Void> callback);

	public void getDataSetMetaData(String login, String password, String url, String driver, String query, AsyncCallback<ArrayList<MapColumns>> callback);

	public void getOsmValues(MapVanilla selectedMap, AsyncCallback<List<MapFeatures>> callback);
	
	public void getOsmValuesbyDataSet(MapVanilla selectedMap, MapDataSet selectedDataSet, AsyncCallback<List<MapFeatures>> callback);

	public void addOrEditAlert(Alert alert, AsyncCallback<Void> callback);

	public void deleteAlert(Alert alert, AsyncCallback<Void> callback);

	public void deleteSubscriber(Subscriber subscriber, AsyncCallback<Void> callback);

	public void saveWidgets(List<Widgets> widgets, AsyncCallback<Void> asyncCallback);

	public void getCommentsbyAllItems(AsyncCallback<Map<RepositoryItem, List<Comment>>> callback);

	public void getAllRepositoryTypes(AsyncCallback<List<String>> callback);

	public void loadFmdtQueries(int id, AsyncCallback<List<PortailItemFmdtDriller>> asyncCallback);

	public void getMapZone(MapVanilla map, AsyncCallback<Map<String, MapZone>> asyncCallback);

	public void saveMapMetadataMappings(ArrayList<MapZone> zones, MapVanilla map, AsyncCallback<Void> asyncCallback);
}
