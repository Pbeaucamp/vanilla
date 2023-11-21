package bpm.sqldesigner.api.constants.types;

import bpm.sqldesigner.api.constants.types.dbms.DBMSTypes;

public abstract class TypesManager {

	private static DBMSTypes INSTANCE = null;
	private static final String TYPES_PATH = "bpm.sqldesigner.api.constants.types.dbms.";

	private TypesManager() {
	}

	public static DBMSTypes getInstance() {
//		if (INSTANCE == null) {
//			String productName = CurrentDatabaseClusterManager.getInstance()
//					.getProductName();
//
//			try {
//				INSTANCE = (DBMSTypes) Class.forName(
//						TYPES_PATH + productName + "Types").newInstance();
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				System.err.println("DBMS " + productName
//						+ " is not supported for this feature. Class "
//						+ e.getMessage()+" not found.");
//			}
//		} TODO
		return INSTANCE;
	}
}
