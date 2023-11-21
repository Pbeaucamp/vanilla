package bpm.united.olap.remote.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.ICacheEntry;
import bpm.united.olap.api.cache.IUnitedOlapCacheManager;
import bpm.united.olap.api.cache.impl.CacheEntry;
import bpm.united.olap.api.cache.impl.DiskCacheStatistics;
import bpm.united.olap.api.cache.impl.MemoryCacheStatistics;
import bpm.united.olap.remote.internal.CacheHttpCommunicator;
import bpm.vanilla.platform.core.components.UnitedOlapComponent.ActionTypes;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteUnitedOlapCacheManager implements IUnitedOlapCacheManager{
//	private String url;
	private XStream xstream; 	
	private CacheHttpCommunicator httpCommunicator;
	
	public RemoteUnitedOlapCacheManager() {
		httpCommunicator = new CacheHttpCommunicator("");
		init();
	}
	
	public void init(String url, String login, String password) {
		httpCommunicator.init(url, login, password);
	}
	private void init() {
		xstream = new XStream();
	}

	@Override
	public void clearDiskCache() throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		XmlAction op = new XmlAction(args, ActionTypes.CLEAR_DISK_CACHE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public void clearMemoryCache() throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		XmlAction op = new XmlAction(args, ActionTypes.CLEAR_MEMORY_CACHE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public ICacheEntry getDiskCacheEntry(CacheKey key) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(key);
		XmlAction op = new XmlAction(args, ActionTypes.DISK_ENTRY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ICacheEntry)xstream.fromXML(xml);
	}

	@Override
	public List<CacheKey> getDiskCacheKeys() throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		XmlAction op = new XmlAction(args, ActionTypes.DISK_KEYS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List)xstream.fromXML(xml);
	}

	@Override
	public DiskCacheStatistics getDiskCacheStatistics() throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		XmlAction op = new XmlAction(args, ActionTypes.DISK_STATS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (DiskCacheStatistics)xstream.fromXML(xml);
	}

	@Override
	public MemoryCacheStatistics getMemoryCacheStatistics() throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		XmlAction op = new XmlAction(args, ActionTypes.MEMORY_STATS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (MemoryCacheStatistics)xstream.fromXML(xml);
	}

	@Override
	public void removeFromCacheDisk(CacheKey key) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(key);
		XmlAction op = new XmlAction(args, ActionTypes.REMOVE_FROM_CACHEDISK);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

		
	}

	@Override
	public void persistCacheDisk() throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		XmlAction op = new XmlAction(args, ActionTypes.PERSIST_CACHEDISK);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

		
	}

	@Override
	public void appendToCacheDisk(InputStream zipStream,
			boolean override) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		
		
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(zipStream, bos, true, true);
		args.addArgument(bos.toByteArray());
		args.addArgument(override);
		
		XmlAction op = new XmlAction(args, ActionTypes.APPEND_TO_CACHE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

		
	}

	@Override
	public InputStream loadCacheEntry(CacheKey key) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		args.addArgument(key);
		XmlAction op = new XmlAction(args, ActionTypes.LOAD_CACHE_ENTRY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		byte[] bytes = (byte[])xstream.fromXML(xml);
		return new ByteArrayInputStream(bytes);
	}
}
