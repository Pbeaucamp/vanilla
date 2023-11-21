package bpm.vanilla.platform.core.remote.internal;

import java.net.HttpURLConnection;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;

public class RepositoryHttpCommunicator extends HttpCommunicator{
	private IRepositoryContext ctx;
	private String servletTarget;
	public RepositoryHttpCommunicator(IRepositoryContext ctx, String servletTarget){
		this.ctx = ctx;
		this.servletTarget = servletTarget;
		init(ctx.getVanillaContext().getVanillaUrl(), ctx.getVanillaContext().getLogin(), ctx.getVanillaContext().getPassword());
	}
	
	@Override
	protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
		super.writeAdditionalHttpHeader(sock);
		//write Repository ComponentHeader
		sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_REPOSITORY);
		//write Group id header
		if (ctx.getGroup() != null){
			sock.setRequestProperty(IRepositoryApi.HTTP_HEADER_GROUP_ID, ctx.getGroup().getId() + "");
		}
		
		//write RepositoryDefinitionHeader
		sock.setRequestProperty(IRepositoryApi.HTTP_HEADER_REPOSITORY_ID, ctx.getRepository().getId() + "");
		sock.setRequestProperty(IRepositoryApi.HTTP_HEADER_TARGET_SERVLET, servletTarget);
	}
}
