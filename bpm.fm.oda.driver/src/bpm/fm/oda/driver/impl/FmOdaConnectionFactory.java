package bpm.fm.oda.driver.impl;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionFactory;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.oda.profile.OdaConnectionWrapper;

public class FmOdaConnectionFactory implements IConnectionFactory {

	public IConnection createConnection(IConnectionProfile profile) {
		OdaConnectionWrapper wrapped = new OdaConnectionWrapper(profile);
		
		return wrapped;
	}

	public IConnection createConnection(IConnectionProfile profile, String uid,
			String pwd) {
		OdaConnectionWrapper wrapped = new OdaConnectionWrapper(profile);
		
		
		return wrapped;
	}

}
