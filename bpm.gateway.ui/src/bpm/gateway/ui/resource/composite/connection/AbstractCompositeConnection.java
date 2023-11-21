package bpm.gateway.ui.resource.composite.connection;

import org.eclipse.swt.widgets.Composite;

import bpm.gateway.core.server.database.DataBaseConnection;

/**
 * This class is an abstraction to build ConnectionComposite
 * 
 * should be used to define UI extension point in the future as base class
 * 
 * 
 * @author LCA
 *
 */
public abstract class AbstractCompositeConnection extends Composite {

	
	/*
	 * model
	 */
	protected DataBaseConnection socket;
	
	
	
	public AbstractCompositeConnection(Composite parent, int style) {
		super(parent, style);
		socket = new DataBaseConnection();
	}

	/**
	 * test the connection
	 * @throws Exception 
	 */
	protected abstract void testConnection() throws Exception;
	
	/**
	 * return true if all required fields are set
	 * to be used inside Wizard
	 * @return
	 */
	public abstract boolean isFilled();
	
	
	/**
	 * set the DataBaseConnection with the field content
	 */
	public abstract void performChanges();
	
	
	/**
	 * 
	 * @return the current DataBaseConnection
	 * 
	 */
	public DataBaseConnection getConnection(){
		return socket;
	}
}
