package bpm.vanilla.wopi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.json.JSONException;
import org.json.JSONObject;

@XmlAccessorType(XmlAccessType.FIELD)
public class WopiFileInfo {

	// required
	@XmlElement(name = "BaseFileName")
	private String baseFileName;
	@XmlElement(name = "OwnerId")
	private String ownerId;
	@XmlElement(name = "Size")
	private long size;
	@XmlElement(name = "UserId")
	private String userId;
	@XmlElement(name = "Version")
	private String version;
	@XmlElement(name = "SHA256")
	private String sha256;
	@XmlElement(name = "UserCanWrite")
	private boolean canWrite = true;
//	private String watermarkText = "";
//	private boolean hidePrintOption;
//	private boolean hideSaveOption;
//	private boolean hideExportOption;
//	private boolean enableOwnerTermination;
//	private boolean disablePrint;
//	private boolean disableExport;
//	private boolean disableCopy;
//	private boolean disableInactiveMessages;

	public String getBaseFileName() {
		return baseFileName;
	}

	public void setBaseFileName(String baseFileName) {
		this.baseFileName = baseFileName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
//	public String generateJson() {
//		try {
//			JSONObject result = new JSONObject();
//			result.put("BaseFileName", "doc5.odt");
//			result.put("OwnerId", ownerId);
//			result.put("UserId", userId);
//			result.put("Size", size);
//			result.put("Version", version);
//			result.put("SHA256", sha256);
//			
////			result.put("WatermarkText", watermarkText);
////			result.put("HidePrintOption", hidePrintOption);
////			result.put("HideSaveOption", hideSaveOption);
////			result.put("HideExportOption", hideExportOption);
////			result.put("EnableOwnerTermination", enableOwnerTermination);
////			result.put("DisablePrint", disablePrint);
////			result.put("DisableExport", disableExport);
////			result.put("DisableCopy", disableCopy);
////			result.put("DisableInactiveMessages", disableInactiveMessages);
//			
//			
//			String a = "{\"DisableCopy\":false,\"EnableOwnerTermination\":false,\"HideExportOption\":false,\"OwnerId\":\"system\",\"HidePrintOption\":false,\"UserId\":\"system\",\"DisableInactiveMessages\":false,\"HideSaveOption\":false,\"DisableExport\":false,\"DisablePrint\":false,\"WatermarkText\":\"\",\"Version\":\"6\",\"BaseFileName\":\"doc5.odt\",\"Size\":290969}";
//			
//			return a;
//		} catch(JSONException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	public String getSha256() {
		return sha256;
	}

	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}

}
