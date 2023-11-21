package bpm.document.management.manager.core;

import java.util.List;

import bpm.document.management.manager.core.model.AklaBoxInstance;
import bpm.document.management.manager.core.model.AklaBoxLicense;
import bpm.document.management.manager.core.model.AklaBoxNotifications;
import bpm.document.management.manager.core.xstream.IXmlActionType;


public interface IVdmAdminManager {
	public enum ActionType implements IXmlActionType {
		SAVE_AKLABOX_INSTANCE, DELETE_AKLABOX_INSTANCE, UPDATE_AKLABOX_INSTANCE, GET_ALL_AKLABOX_INSTANCE,
		SAVE_AKLABOX_LICENSE, DELETE_AKLABOX_LICENSE, UPDATE_AKLABOX_LICENSE, GET_AKLABOX_LICENSE,
		SAVE_AKLABOX_NOTIFICATION, DELETE_AKLABOX_NOTIFICATION, UPDATE_AKLABOX_NOTIFICATION, GET_ALL_AKLABOX_NOTIFICATION
	}
	
	
	public void saveAklaBoxInstance(AklaBoxInstance instance) throws Exception;
	
	public void deleteAklaBoxInstance(AklaBoxInstance instance) throws Exception;
	
	public void updateAklaBoxInstance(AklaBoxInstance instance) throws Exception;
	
	public List<AklaBoxInstance> getAllAklaBoxInstance() throws Exception;
	
	public AklaBoxLicense saveAklaBoxLicense(AklaBoxLicense license) throws Exception;
	
	public void updateAklaBoxLicense(AklaBoxLicense license) throws Exception;
	
	public void deleteAklaBoxLicense(AklaBoxLicense license) throws Exception;
	
	public AklaBoxLicense getAklaBoxLicense(AklaBoxInstance instance) throws Exception;
	
	/*
	 * For Notifications
	 */
	
	public void saveNotification(AklaBoxNotifications noti) throws Exception;
	
	public void updateNotification(AklaBoxNotifications noti) throws Exception;
	
	public void deleteNotification(AklaBoxNotifications noti) throws Exception;
	
	public List<AklaBoxNotifications> getAllNotifications() throws Exception;
	
}