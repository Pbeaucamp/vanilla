package bpm.vanilla.portal.client.panels;

import bpm.gwt.commons.shared.repository.DocumentVersionDTO;

public interface IGedCheck {

	public void checkin(DocumentVersionDTO item);
	
	public void checkout(DocumentVersionDTO item);

	public void comeBackToVersion(DocumentVersionDTO item);
	
	public void share(DocumentVersionDTO item);
	
	public void displayPublicUrls(DocumentVersionDTO item);
}
