package bpm.vanillahub.runtime.run;

import java.util.List;

import bpm.vanilla.platform.core.beans.meta.MetaLink;

public class MetaLinksResultInformation implements IResultInformation {

	private List<MetaLink> links;
	
	public MetaLinksResultInformation(List<MetaLink> links) {
		this.links = links;
	}
	
	public List<MetaLink> getLinks() {
		return links;
	}
}
