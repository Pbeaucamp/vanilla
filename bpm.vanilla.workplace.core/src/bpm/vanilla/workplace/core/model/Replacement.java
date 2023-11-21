package bpm.vanilla.workplace.core.model;

public class Replacement {
	
	private String originalString = "oldString";
	private String replacementString = "newString";
	private ImportItem item;

	public Replacement(ImportItem it) {
		this.item = it;
	}

	public ImportItem getItem() {
		return item;
	}

	public void setItem(ImportItem item) {
		this.item = item;
	}

	/**
	 * @return the originalString
	 */
	public String getOriginalString() {
		return originalString;
	}

	/**
	 * @param originalString
	 *            the originalString to set
	 */
	public void setOriginalString(String originalString) {
		this.originalString = originalString;
	}

	/**
	 * @return the replacementString
	 */
	public String getReplacementString() {
		return replacementString;
	}

	/**
	 * @param replacementString
	 *            the replacementString to set
	 */
	public void setReplacementString(String replacementString) {
		this.replacementString = replacementString;
	}

}
