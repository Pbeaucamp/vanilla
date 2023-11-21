package bpm.vanilla.server.client.rcp;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "bpm.vanilla.server.client.rcp.messages"; //$NON-NLS-1$
	
	public static String ApplicationActionBarAdvisor_0;

	public static String ApplicationActionBarAdvisor_1;
	public static String ApplicationActionBarAdvisor_2;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
