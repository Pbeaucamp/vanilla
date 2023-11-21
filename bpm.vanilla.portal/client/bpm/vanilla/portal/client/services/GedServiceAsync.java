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

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.shared.GedInformations;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.vanilla.portal.shared.FieldDefinitionDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GedServiceAsync {
	
	public void getFieldDefinitions(AsyncCallback<List<FieldDefinitionDTO>> asyncCallback);

	public void sampleSearch(String[] keywords, Boolean allcondition, AsyncCallback<List<DocumentVersionDTO>> callback);

	public void complexSearch(HashMap<FieldDefinitionDTO, String> wanted,
			String[] keywords, Boolean allconditions,
			AsyncCallback<List<DocumentVersionDTO>> callback);

	public void indexFile(GedInformations gedInfos, AsyncCallback<Void> callback);
	
	public void loadDocument(String key, String format, AsyncCallback<String> callback);

	/**
	 * This method check if the specified document has been checkout by the user
	 * If it is true, the user can checkin the document
	 * 
	 * @param docId
	 * @param userId
	 * @param callback
	 */
	public void checkIfItemCanBeCheckin(int docId, int userId, AsyncCallback<Boolean> callback);
	
	/**
	 * This method check if the specified document has not been checkout already by the user or anybody else
	 * If not the document is proposed to download to the user
	 * 
	 * @param docId
	 * @param userId
	 * @param callback
	 */
	public void checkout(int documentId, int userId, String key, String format, AsyncCallback<String> asyncCallback);

	public void getAllDocuments(AsyncCallback<List<DocumentDefinitionDTO>> asyncCallback);

	public void comeBackToVersion(DocumentVersionDTO item, AsyncCallback<Void> asyncCallback);
}
