package bpm.repository.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import bpm.vanilla.platform.core.IRepositoryContext;

public class SessionSourceProvider extends AbstractSourceProvider {
	public static final String CONNECTION_STATE = "bpm.repository.ui.connected";
	public static final String IMPORTED_MODEL_STATE = "bpm.repository.ui.imported";
	public static final String CHECK_IN_STATE = "bpm.repository.ui.checkedIn";
	public static final String MODEL_OPENED_STATE = "bpm.repository.ui.modelOpened";
	
	public static final String enabled = "enabled";
	public static final String disabled = "disabled";
	
	private IRepositoryContext context;
	private Integer directoryItemId;
	private boolean checkedIn = false;
	private boolean modelOpened = false;
	
	/**
	 * @return the context
	 */
	public IRepositoryContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(IRepositoryContext context) {
		this.context = context;
		
		
		String currentState = (String)getCurrentState().get(CONNECTION_STATE);
		fireSourceChanged(ISources.WORKBENCH, CONNECTION_STATE, currentState);
	
	}

	/**
	 * @return the directoryItemId
	 */
	public Integer getDirectoryItemId() {
		return directoryItemId;
	}

	/**
	 * @param directoryItemId the directoryItemId to set
	 */
	public void setDirectoryItemId(Integer directoryItemId) {
		this.directoryItemId = directoryItemId;

		
		String currentState = (String)getCurrentState().get(IMPORTED_MODEL_STATE);
		fireSourceChanged(ISources.WORKBENCH, IMPORTED_MODEL_STATE, currentState);

	}

	public SessionSourceProvider() {
	}

	@Override
	public void dispose() {

	}

	@Override
	public Map getCurrentState() {
		Map currentState = new HashMap(2);
		currentState.put(CONNECTION_STATE, context != null  ? enabled : disabled);
		currentState.put(IMPORTED_MODEL_STATE, directoryItemId != null  ? enabled : disabled);
		currentState.put(CHECK_IN_STATE, checkedIn ? enabled : disabled);
		currentState.put(MODEL_OPENED_STATE, modelOpened ? enabled : disabled);
		return currentState;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] {CONNECTION_STATE, IMPORTED_MODEL_STATE,CHECK_IN_STATE, MODEL_OPENED_STATE};
	}

	/**
	 * @return the checkedIn
	 */
	public boolean isCheckedIn() {
		return checkedIn;
	}

	/**
	 * @param checkedIn the checkedIn to set
	 */
	public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
		String currentState = (String)getCurrentState().get(CHECK_IN_STATE);
		fireSourceChanged(ISources.WORKBENCH, CHECK_IN_STATE, currentState);
	}

	/**
	 * @return the modelOpened
	 */
	public boolean isModelOpened() {
		return modelOpened;
	}

	/**
	 * @param modelOpened the modelOpened to set
	 */
	public void setModelOpened(boolean modelOpened) {
		this.modelOpened = modelOpened;
		String currentState = (String)getCurrentState().get(MODEL_OPENED_STATE);
		fireSourceChanged(ISources.WORKBENCH, MODEL_OPENED_STATE, currentState);
	}

	
}
