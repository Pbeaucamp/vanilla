package bpm.vanilla.api.runtime.dto;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.repository.KpiTheme;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortalViewerItem extends ViewerItem {

	private int portalID;
	
	public PortalViewerItem(RepositoryItem it, String portalXml) throws Exception {
		super(it,"PORTAL");
        XStream xstream = new XStream();
        KpiTheme theme = (KpiTheme) xstream.fromXML(portalXml);

        portalID = theme.getThemeId();
	}

	public int getPortalID() {
		return portalID;
	}

	
	
}
