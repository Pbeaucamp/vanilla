package bpm.birep.admin.client.dialog;

import org.eclipse.swt.widgets.Shell;

public class DialogSearchReplaceXml extends DialogSearchReplace {

	private String xml;
	private String newXml;

	public DialogSearchReplaceXml(Shell parentShell, String xml) {
		super(parentShell);
		this.xml = xml;
	}

	@Override
	protected void okPressed() {
		this.newXml = xml.replaceAll(searchString.getText(), newString.getText());
		super.okPressed();
	}
	
	public String getNewXml() {
		return newXml;
	}
}
