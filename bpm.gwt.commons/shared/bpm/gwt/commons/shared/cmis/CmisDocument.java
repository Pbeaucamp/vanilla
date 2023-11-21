package bpm.gwt.commons.shared.cmis;

public class CmisDocument extends CmisItem {

	public CmisDocument() {
		super();
	}
	
	public CmisDocument(String itemId, String name) {
		super(itemId, name, CmisType.DOCUMENT);
	}
}
