package bpm.vanilla.repository.beans.log;

import java.util.Date;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaLoggingManager;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;

public class ServerLoger {

	public static final String[] OPERATION_TYPE = new String[] { "CREATE", "DELETE", "BROWSE", "UPDATE", "IMPORT", "CHECKIN", "CHECKOUT", "REVERT TO OLDER REVISION", "UNLOCK", "SHARE" };

	public static final int CREATE_OPERATION = 0;
	public static final int DELETE_OPERATION = 1;
	public static final int BROWSE_OPERATION = 2;
	public static final int UPDATE_OPERATION = 3;
	public static final int IMPORT_OPERATION = 4;

	public static final int CHECKIN = 5;
	public static final int CHECKOUT = 6;
	public static final int REVERT_TO_REVISION = 7;
	public static final int UNLOCK = 8;
	public static final int SHARE = 9;
	
	private static IVanillaLoggingManager logger;

	public static void init(IVanillaLoggingManager vanillaLogger) {
		logger = vanillaLogger;
	}
	
	private ServerLoger() {
	}

	/**
	 * 
	 * @param appType
	 * @param operationType
	 * @param item
	 * @param clientIp
	 * @param user
	 * @param group
	 * @param delay
	 *            in millisec
	 * @return
	 * @throws Exception 
	 */
	public static void log(String ipClient, int appType, int operationType, int objectId, User user, Integer groupId, long delay, int repositoryId) throws Exception {
		
		VanillaLogs log = new VanillaLogs(getActionTypeForOperation(operationType), "bpm.vanilla.repository", OPERATION_TYPE[operationType], new Date(), user.getId(), ipClient);
		log.setDelay(delay);
		log.setGroupId(groupId);
		log.setDirectoryItemId(objectId);
		log.setRepositoryId(repositoryId);
		
		String typeName = IRepositoryApi.TYPES_NAMES[appType];
		log.setObjectType(typeName);
		
		logger.addVanillaLog(log);
		
//		RepositoryLog log = new RepositoryLog();
//		if (appType >= 0 && appType < IRepositoryApi.TYPES_NAMES.length) {
//			log.setAppName(IRepositoryApi.TYPES_NAMES[appType]);
//		}
//		else {
//			log.setAppName("Unknown");
//		}
//
//		log.setClientIp(ipClient);
//
//		log.setTime(new Date());
//		log.setDelay(delay);
//
//		log.setGroupId(groupId);
//
//		log.setObjectId(objectId);
//		log.setOperation(OPERATION_TYPE[operationType]);
//		log.setUserId(user.getId());
//
//		logDao.save(log);
	}

	private static Level getActionTypeForOperation(int operationType) {
		switch(operationType) {
		case BROWSE_OPERATION:
			return Level.DEBUG;
		case CHECKIN:
			return Level.INFO;
		case CHECKOUT:
			return Level.INFO;
		case CREATE_OPERATION:
			return Level.INFO;
		case DELETE_OPERATION:
			return Level.INFO;
		case IMPORT_OPERATION:
			return Level.DEBUG;
		case REVERT_TO_REVISION:
			return Level.INFO;
		case SHARE:
			return Level.INFO;
		case UNLOCK:
			return Level.INFO;
		case UPDATE_OPERATION:
			return Level.INFO;
		}
		return Level.DEBUG;
	}
}
