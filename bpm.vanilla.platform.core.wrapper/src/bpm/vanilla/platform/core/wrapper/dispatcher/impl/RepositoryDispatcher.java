package bpm.vanilla.platform.core.wrapper.dispatcher.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaDispatcher;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.wrapper.dispatcher.IDispatcher;

public class RepositoryDispatcher extends AbstractVanillaDispatcher implements IDispatcher{ 
	private IVanillaComponentProvider component;
	public RepositoryDispatcher(IVanillaComponentProvider component){
		this.component = component;
	}

	@Override
	public void dispatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String group = request.getHeader(IRepositoryApi.HTTP_HEADER_GROUP_ID);
		if (group == null){
			throw new Exception("Missing HTTP header " + IRepositoryApi.HTTP_HEADER_GROUP_ID);
		}
		
		String repository = request.getHeader(IRepositoryApi.HTTP_HEADER_REPOSITORY_ID);
		if (repository == null){
			throw new Exception("Missing HTTP header " + IRepositoryApi.HTTP_HEADER_REPOSITORY_ID);
		}
		int repositoryId = Integer.valueOf(repository);
		
		Repository repDef = component.getRepositoryManager().getRepositoryById(repositoryId);
		
		StringBuffer repUrl = new StringBuffer();
		repUrl.append(repDef.getUrl());
		repUrl.append(request.getHeader(IRepositoryApi.HTTP_HEADER_TARGET_SERVLET));
		
		sendCopy(request, response, repUrl.toString());
		
	}

	@Override
	public boolean needAuthentication() {
		return true;
	}

	@Override
	public void canBeRun(IRuntimeConfig conf) throws VanillaException,
			Exception {
		
	}

}
