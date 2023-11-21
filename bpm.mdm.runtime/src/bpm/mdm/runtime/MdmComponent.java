package bpm.mdm.runtime;

import org.apache.log4j.Logger;
import org.osgi.service.http.HttpService;

import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.serialisation.MdmConfiguration;
import bpm.mdm.runtime.wrapper.ServletData;
import bpm.mdm.runtime.wrapper.ServletModel;

public class MdmComponent {
	private HttpService httpService;
	private MdmRuntime runtime;
	
	public void bind(HttpService service){
		this.httpService = service;
	}
	
	public void unbind(HttpService service){
		this.httpService =  null;
	}
	
	private void registerServlets() throws Exception{
		try{
			this.httpService.registerServlet(IMdmProvider.SERVLET_MODEL, new ServletModel(runtime), null, null);
		}catch(Exception ex){
			throw new Exception("Unable to register servlet " + IMdmProvider.SERVLET_MODEL + " "+ ex.getMessage(), ex);
		}
		
		try{
			this.httpService.registerServlet(IMdmProvider.SERVLET_DATAS, new ServletData(runtime), null, null);
		}catch(Exception ex){
			throw new Exception("Unable to register servlet " + IMdmProvider.SERVLET_DATAS + " "+ ex.getMessage(), ex);
		}
	}
	
	private void unregisterServlets(){
		this.httpService.unregister(IMdmProvider.SERVLET_MODEL);
	}
	
	public void activate(){
		try{
			MdmConfiguration conf = new MdmConfiguration();
			conf.setMdmPersistanceFolderName("data");
			
			this.runtime = new MdmRuntime(conf, null, null);
			registerServlets();
			Logger.getLogger(getClass()).info("Activated");
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Failed to activate " + ex.getMessage(), ex);
		}
	}
	public void desactivate(){
		unregisterServlets();
		Logger.getLogger(getClass()).info("Desactivated");
	}
}
