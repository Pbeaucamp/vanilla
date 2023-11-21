package bpm.united.olap.wrapper.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.impl.CacheEntry;
import bpm.united.olap.api.cache.impl.DiskCacheStatistics;
import bpm.united.olap.api.cache.impl.MemoryCacheStatistics;
import bpm.united.olap.wrapper.UnitedOlapWrapperComponent;
import bpm.vanilla.platform.core.components.UnitedOlapComponent.ActionTypes;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class CacheManagementServlet extends HttpServlet {

	private XStream xstream;
	private UnitedOlapWrapperComponent component;
	
	public CacheManagementServlet(UnitedOlapWrapperComponent component) {
		this.component = component;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			Object actionResult = null;
			ActionTypes type = (ActionTypes)action.getActionType();
			switch(type) {
			case CLEAR_DISK_CACHE:
				clearDiskCache(args);
				break;
			case CLEAR_MEMORY_CACHE:
				clearMemoryCache(args);
				break;
			case DISK_STATS:
				actionResult = getCacheDiskStatistics(args);
				break;
			case MEMORY_STATS:
				actionResult = getMemoryCacheStatistics(args);
				break;
			case DISK_ENTRY:
				actionResult = getDiskCacheEntry(args);
				break;
			case DISK_KEYS:
				actionResult = getDiskCacheKeys(args);
				break;
			case REMOVE_FROM_CACHEDISK:
				removeFromCacheDisk(args);
				break;
			case PERSIST_CACHEDISK:
				persistCacheDisk();
				break;
			case APPEND_TO_CACHE:
				appendToCache(args);
				break;
			case LOAD_CACHE_ENTRY:
				actionResult = loadCacheEntry(args);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	

	
	

	

	

	@Override
	public void init() throws ServletException {
		xstream = new XStream();
		super.init();
	}
	
	
	private void clearDiskCache(XmlArgumentsHolder args) throws Exception{
		component.getCacheManager().clearDiskCache();
		
	}
	
	private void clearMemoryCache(XmlArgumentsHolder args) throws Exception{
		component.getCacheManager().clearMemoryCache();
		
	}
	
	private void removeFromCacheDisk(XmlArgumentsHolder args) throws Exception{
		
		component.getCacheManager().removeFromCacheDisk((CacheKey)args.getArguments().get(0));
		
	}
	private void appendToCache(XmlArgumentsHolder args) throws Exception{
		byte[] b = (byte[])args.getArguments().get(0);
		ByteArrayInputStream bis = new ByteArrayInputStream(b);
		component.getCacheManager().appendToCacheDisk(bis, (Boolean)args.getArguments().get(1));
	}
	
	private Object getCacheDiskStatistics(XmlArgumentsHolder args) throws Exception{
		return component.getCacheManager().getDiskCacheStatistics();
	}
	private Object getMemoryCacheStatistics(XmlArgumentsHolder args) throws Exception{
		return component.getCacheManager().getMemoryCacheStatistics();
	}
	
	private Object getDiskCacheKeys(XmlArgumentsHolder args) throws Exception{
		return component.getCacheManager().getDiskCacheKeys();
	}

	private Object getDiskCacheEntry(XmlArgumentsHolder args) throws Exception{
		try{
			return component.getCacheManager().getDiskCacheEntry((CacheKey)args.getArguments().get(0));
		}catch(IndexOutOfBoundsException ex){
			throw new Exception("Missing CacheKey argument");
		}catch(ClassCastException ex){
			throw new Exception("The argument is not an instance of " + CacheKey.class.getName());
		}
	}
	
	private void persistCacheDisk() throws Exception{
		component.getCacheManager().persistCacheDisk();
	}
	
	private Object loadCacheEntry(XmlArgumentsHolder args) throws Exception{
		InputStream is =  component.getCacheManager().loadCacheEntry((CacheKey)args.getArguments().get(0));
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(is, bos, true, true);
		return bos.toByteArray();
	}
	
}
