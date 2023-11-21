/**
 * 
 */
package bpm.fwr.shared.models;

import java.util.HashMap;

public class IDirectoryItemDTO extends TreeParentDTO {

	private String subType = "";
	private int directoryId;
	private HashMap<String, String> locales = new HashMap<String, String>();
	
	public void addLocales(String key, String value) {
		this.locales.put(key, value);
	}
	
	public HashMap<String, String> getLocales() {
		return locales;
	}

	public void setLocales(HashMap<String, String> locales) {
		this.locales = locales;
	}

	public IDirectoryItemDTO(String name) {
		super(name);
	}

	public IDirectoryItemDTO() {
		super();
	}

	/**
	 * @return the directoryId
	 */
	public int getDirectoryId() {
		return directoryId;
	}

	/**
	 * @param directoryId the directoryId to set
	 */
	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}


	public String getSubType() {

		return subType;
	}

}
