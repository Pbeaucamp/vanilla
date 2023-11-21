package bpm.vanilla.portal.client.services;

import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.viewer.FormsDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("PerFormsService")
public interface PerFormsService extends RemoteService {
	
	public static class Connect{
		
		private static PerFormsServiceAsync instance;
		
		public static PerFormsServiceAsync getInstance(){
			if (instance == null) {
				instance = (PerFormsServiceAsync) GWT.create(PerFormsService.class);
			}
			return instance;
		}
	}
	
	
	public String submit(FormsDTO form) throws ServiceException ;
	
	public String validate(FormsDTO form) throws ServiceException ;
	
	public List<FormsDTO> getForms() throws ServiceException ;
}
