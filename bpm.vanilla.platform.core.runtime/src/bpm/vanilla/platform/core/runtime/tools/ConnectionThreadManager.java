package bpm.vanilla.platform.core.runtime.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.runtime.components.VanillaSecurityManager;

public class ConnectionThreadManager extends Thread {
	
	private Logger logger = Logger.getLogger(this.getClass());

	private static final int DEFAULT_TIME_CHECK = 180000;
	
	private VanillaSecurityManager securityManager;
	private Calendar calendar = Calendar.getInstance();
	
	public ConnectionThreadManager(VanillaSecurityManager securityManager) {
		this.securityManager = securityManager;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(DEFAULT_TIME_CHECK);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		boolean running = true;
		while(running) {
			try {
				checkConnection();
			
				Thread.sleep(DEFAULT_TIME_CHECK);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
		}
	}
	
	private void checkConnection() {
		logger.trace("Checking connections");
		
		calendar.setTime(new Date());
		
		List<Connection> connections = securityManager.getConnections();
		
		for(Connection connection : connections) {
			
			if(connection.isMaxTryReached()) {
				long timelapse = calendar.getTimeInMillis() - connection.getMaxTryReachedTime();
				if(timelapse >= connection.getInactivationDelay()) {
					connection.resetNbConnection();
				}
			}
		}
	}
}
