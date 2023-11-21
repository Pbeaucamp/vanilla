package bpm.vanilla.platform.core.components;

import bpm.vanilla.platform.core.listeners.IVanillaEvent;

/**
 * 
 * ere, 27/12/11 : added getStatus
 * 
 * @author ludo
 *
 */
public interface IVanillaComponent {
	
	//for start/stop operations
	public static String SERVICE_ADMIN_SERVLET = "/adminService"; 
	
	
	
	/**
	 * these are JMX operations
	 *
	 */
	public enum Operations {
		START ("Start", "Start a component"),
		STOP ("Stop", "Stop a component");
		
		private String opName;
		private String opDesc;
		private Operations(String opName, String opDesc) {
			this.opName = opName;
			this.opDesc = opDesc;
		}
		public String getOpName() {
			return opName;
		}
		public String getOpDesc() {
			return opDesc;
		}
	}
	
	public enum Status {
		UNDEFINED ("STATUS_UNDEFINED", -2),
		ERROR ("STATUS_WITH_ERROR", -1),
		STOPPED ("STATUS_DOWN", 0),
		STOPPING ("STATUS_STOPPING", 1),
		STARTING ("STATUS_STARTING", 2),
		STARTED ("STATUS_RUNNING", 3);
		
		private String status;
		private Integer value;
		
		private Status(String status, Integer value) {
			this.status = status;
			this.value = value;
		}
		
		/**
		 * gets the string name of the status
		 * @return
		 */
		public String getStatus() {
			return status;
		}
		
		/**
		 * gets the int value of the status (notably for nagios, maybe others too)
		 * @return
		 */
		public Integer getValue() {
			return value;
		}
	}
	
	public IVanillaComponentIdentifier getIdentifier();
	
	/**
	 * Returns a status describing the current state of the component
	 * 
	 * @return a {@link Status}
	 */
	public Status getStatus();
	
	/**
	 * Tries to start the component
	 * @throws Exception
	 */
	public void start() throws Exception;
	
	/**
	 * Tries to stop the component
	 * @throws Exception
	 */
	public void stop() throws Exception;
	
//	not needed	
//	/**
//	 * Doubling with {@link IVanillaComponentIdentifier}.getComponentNature()
//	 * but we need the nature before having an identifier
//	 * @return
//	 */
//	public String getComponentNature();
	
	/**
	 * an event is sent from the vanillaPlatform
	 */
	public void notify(IVanillaEvent event);
}
