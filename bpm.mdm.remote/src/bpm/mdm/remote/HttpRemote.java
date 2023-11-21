package bpm.mdm.remote;

import bpm.mdm.model.api.IMdmProvider;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;


public class HttpRemote extends HttpCommunicator{

	
	public String executeModelAction(String message) throws Exception{
		return sendMessage(IMdmProvider.SERVLET_MODEL, message);
	}
	public String executeDatasAction(String message) throws Exception{
		return sendMessage(IMdmProvider.SERVLET_DATAS, message);
	}
	
	
}
