package bpm.fd.runtime.model;

import org.eclipse.datatools.connectivity.oda.IResultSet;

/**
 * Responsible to create the UI canvas(JSP, RWT shell or whatever)
 * and return the url that will provide the UI of the dashboard
 * 
 * @author ludo
 *
 */
public interface IRenderer {

	public Object renderComponent(ComponentRuntime component, DashState state, IResultSet resultSet, boolean refresh) throws Exception;
	

	
}
