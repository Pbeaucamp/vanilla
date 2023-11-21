package bpm.united.olap.api.preload;

import java.beans.PropertyChangeListener;

import bpm.united.olap.api.communication.IModelService;

public interface IPreloader {
	public static final String LOADING_HIERARCHY = "bpm.united.olap.api.preload.loading";
	public static final String LOADED_HIERARCHY = "bpm.united.olap.api.preload.loaded";
	public static final String LOADING_ERROR = "bpm.united.olap.api.preload.error";

	public void preload(IModelService modelService, IPreloadConfig config) throws Exception;
	
	public void addPreloadListener(PropertyChangeListener listener);
	public void removePreloadListener(PropertyChangeListener listener);
	
}
