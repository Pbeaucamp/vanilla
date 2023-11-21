package bpm.united.olap.runtime.data.cache;

import bpm.united.olap.runtime.cache.ICacheConfiguration;

public class MemCachedConfiguation implements ICacheConfiguration {

	private String hostname;
	private int portNumber;
	private int expiration = 1000 * 60 * 60; //30 minutes for cells
	private int memoryLimit;
	private int maxCachedElements = 1000000;
	private int mainExpirationLimit = 1000 * 60 * 60 * 12; //default value at 5 hours
	
//	private IVanillaLogger logger;
	
	public MemCachedConfiguation(String hostname, int portNumber/*, IVanillaLogger logger*/) {
		this(hostname, portNumber, 600, 64, Integer.MAX_VALUE/*, logger*/);
	}

	public MemCachedConfiguation(String hostname, int portNumber, int expiration/*, IVanillaLogger logger*/) {
		this(hostname, portNumber, expiration, 64, Integer.MAX_VALUE/*, logger*/);
	}

	public MemCachedConfiguation(String hostname, int portNumber, int expiration, int memoryLimit/*, IVanillaLogger logger*/) {
		this(hostname, portNumber, expiration, memoryLimit, Integer.MAX_VALUE/*, logger*/);
	}
	
	public MemCachedConfiguation(String hostname, int portNumber, int expiration, int memoryLimit, int maxElements/*, IVanillaLogger logger*/) {
		super();
		this.hostname = hostname;
		this.portNumber = portNumber;
		if (expiration > this.expiration){
			this.expiration = expiration;
		}
		
		this.memoryLimit = memoryLimit;
		if (maxElements > this.maxCachedElements){
			this.maxCachedElements = maxElements;
		}
		
//		this.logger = logger;
	}

	@Override
	public int getCacheExpirationTime() {
		return expiration;
	}

	@Override
	public int getCacheMemoryLimit() {
		return memoryLimit;
	}

	@Override
	public String getHostName() {
		return hostname;
	}

	@Override
	public int getPort() {
		return portNumber;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public void setExpiration(int expiration) {
		this.expiration = expiration;
	}

	public void setMemoryLimit(int memoryLimit) {
		this.memoryLimit = memoryLimit;
	}

	public void setMaxCachedElements(int maxCachedElements) {
//		this.maxCachedElements = maxCachedElements;
	}

	@Override
	public int getMaxCachedElements() {
		return maxCachedElements;
	}

	@Override
	public int getMainDatasCacheExpirationTime() {
		return mainExpirationLimit;
	}

//	public void setLogger(IVanillaLogger logger) {
//		this.logger = logger;
//	}
//
//	@Override
//	public IVanillaLogger getLogger() {
//		return logger;
//	}

}
