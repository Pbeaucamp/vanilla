package bpm.fwr.api.beans.components;


public class BirtCommentComponents extends ReportComponent {

	private String title, numberDisplayed, name, vanillaUrl;

	public BirtCommentComponents() {
		super();
	}

	public BirtCommentComponents(String title, String numberDisplayed, String name, String url) {
		super();
		int hash = new Object().hashCode();
		this.vanillaUrl = url;
		this.title = title;
		this.numberDisplayed = numberDisplayed;
		this.name = "Comment_" + hash;

	}

	public String getVanillaUrl() {
		return vanillaUrl;
	}

	public void setVanillaUrl(String vanillaUrl) {
		this.vanillaUrl = vanillaUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNumberDisplayed() {
		return numberDisplayed;
	}

	public void setNumberDisplayed(String numberDisplayed) {
		this.numberDisplayed = numberDisplayed;
	}
}
