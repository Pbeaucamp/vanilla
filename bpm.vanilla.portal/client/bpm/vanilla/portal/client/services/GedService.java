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

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.GedInformations;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.vanilla.portal.shared.FieldDefinitionDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("GedService")
public interface GedService extends RemoteService {
	public static class Connect {
		private static GedServiceAsync instance;
		public static GedServiceAsync getInstance(){
			if(instance == null){
				instance = (GedServiceAsync) GWT.create(GedService.class);
			}
			return instance;
		}
	}

	public List<FieldDefinitionDTO> getFieldDefinitions() throws ServiceException;
	
	public List<DocumentVersionDTO> sampleSearch(String[] keywords, Boolean allcondition) throws ServiceException;
	
	public List<DocumentVersionDTO> complexSearch(HashMap<FieldDefinitionDTO, String> wanted, 
			String[] keywords, Boolean allconditions) throws ServiceException;

	public void indexFile(GedInformations gedInfos) throws ServiceException;
	
	public String loadDocument(String key, String format);

	public boolean checkIfItemCanBeCheckin(int docId, int userId) throws ServiceException;
	
	public String checkout(int documentId, int userId, String key, String format) throws ServiceException;
	
	public List<DocumentDefinitionDTO> getAllDocuments() throws ServiceException;

	public void comeBackToVersion(DocumentVersionDTO item) throws ServiceException;
}
