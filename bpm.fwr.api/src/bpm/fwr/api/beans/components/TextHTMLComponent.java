package bpm.fwr.api.beans.components;


public class TextHTMLComponent extends ReportComponent {

	private String textContent;

	public TextHTMLComponent() {
	}

	public TextHTMLComponent(String textContent) {
		this.textContent = textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public String getTextContent() {
		return textContent;
	}
}
