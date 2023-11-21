package bpm.mdm.ui.session;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.commands.ICommandService;

import bpm.mdm.ui.Activator;
import bpm.mdm.ui.handlers.MdmDesignHandler;

public class SessionSourceProvider extends AbstractSourceProvider {
	public static final String SESSION_STATE = "bpm.mdm.ui.session.sessionState";
	private final static String LOGGED_IN = "loggedIn";
	private final static String LOGGED_OUT = "loggedOut";
	
	private boolean loggedIn = true;
	
	public SessionSourceProvider() {
	}

	@Override
	public void dispose() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map getCurrentState() {
		Map currentState = new HashMap(1);
		String state =  loggedIn ?LOGGED_IN:LOGGED_OUT;
		currentState.put(SESSION_STATE, state);
		return currentState;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] {SESSION_STATE};
	}

	public void setLoggedIn(boolean loggedIn) {
		loggedIn = true;
		if (this.loggedIn == loggedIn){
			return; // no change
		}
			
		this.loggedIn = loggedIn;
		
		
		if (!this.loggedIn){
			ICommandService commandService = (ICommandService)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ICommandService.class);
			try {
				
				Command c = commandService.getCommand(MdmDesignHandler.COMMAND_ID);
				ExecutionEvent event = new ExecutionEvent(c, getCurrentState(), this, null);
				c.executeWithChecks(event);
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (NotDefinedException e) {
				e.printStackTrace();
			} catch (NotEnabledException e) {
				e.printStackTrace();
			} catch (NotHandledException e) {
				e.printStackTrace();
			}
		}
		String currentState = loggedIn ? LOGGED_IN : LOGGED_OUT;
		fireSourceChanged(ISources.WORKBENCH, SESSION_STATE, currentState);
	}
}
