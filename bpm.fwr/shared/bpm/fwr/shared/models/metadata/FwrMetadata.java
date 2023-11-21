package bpm.fwr.shared.models.metadata;

import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FwrMetadata extends FwrBusinessObject implements IsSerializable {
	private int id;
	private boolean isBrowsed = false;
	private boolean isOlap = false;
	
	private LinkedHashMap<String, String> locales;
	
	private String previousLanguage;
	private List<FwrBusinessModel> businessModels;
	
	public FwrMetadata(){ 
		super();
	}
	
	public FwrMetadata(int id){
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setBrowsed(boolean isBrowsed) {
		this.isBrowsed = isBrowsed;
	}

	public boolean isBrowsed() {
		return isBrowsed;
	}

	public void setLocales(LinkedHashMap<String, String> locales) {
		this.locales = locales;
		if(locales.size() > 0) {
			for(String key : locales.keySet()) {
				this.previousLanguage = key;
				break;
			}
		}
	}

	public LinkedHashMap<String, String> getLocales() {
		return locales;
	}

	public void setPreviousLanguage(String previousLanguage) {
		this.previousLanguage = previousLanguage;
	}

	public String getPreviousLanguage() {
		return previousLanguage;
	}

	public void setOlap(boolean isOlap) {
		this.isOlap = isOlap;
	}

	public boolean isOlap() {
		return isOlap;
	}

	public void setBusinessModels(List<FwrBusinessModel> businessModels) {
		this.businessModels = businessModels;
	}

	public List<FwrBusinessModel> getBusinessModels() {
		return businessModels;
	}
}
