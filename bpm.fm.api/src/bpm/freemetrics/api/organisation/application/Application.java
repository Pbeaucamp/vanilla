package bpm.freemetrics.api.organisation.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.freemetrics.api.organisation.group.Group;

public class Application {

	private int security; 

	// Fields    
	private int     id = 0;
	private Integer adCalendarId;    // not used
	private Integer adDsId; 		 // not used (systematically 1)
	private Integer adAlternateDsId; // not used (systematically null)
	private String  adBusinessCode;
	private String name;
	private String  adBusinessDefinition;
	private String  adBusinessComment;
	private String  adBusinessTargetUser;
	private Integer adBusinessRequestorId;  
	private Date    adRequestDate; 
	private Integer adBusinessCreatorId;   
	private Date    adCreationDate;
	private Integer adStatusId;
	private Date    adProductionDate;
	private String  adWithHistoricalData;
	private String  adReleaseNumber;
	private Integer adNumberOfDashboards; 	// incorrect
	private Integer adNumberOfKeyIndicators;
	private String  adImage;
	private Boolean adIsVisible;
	private String  adCustom1;
	private Integer adTypeTerrID;
	private Integer adTypeDecompTerrID;
	private Integer ad_gl_DecompTerrID;
	
	private String ad_gl_Superficie,ad_gl_Population, ad_gl_CodInsee, ad_gl_CodLocalisationX, ad_gl_CodLocalisationY;
	
	private List<Application> children;
	private Application parent;
	private Group parentGroup;
	
	private Date endDate;
	
	private boolean hasChildren;
	
	private String dtmAxeTable;
	private String dtmColName;
	private String dtmColCode;
	private String dtmColVersion;
	
	public String getDtmAxeTable() {
		return dtmAxeTable;
	}


	public void setDtmAxeTable(String dtmAxeTable) {
		this.dtmAxeTable = dtmAxeTable;
	}


	public String getDtmColName() {
		return dtmColName;
	}


	public void setDtmColName(String dtmColName) {
		this.dtmColName = dtmColName;
	}


	public String getDtmColCode() {
		return dtmColCode;
	}


	public void setDtmColCode(String dtmColCode) {
		this.dtmColCode = dtmColCode;
	}


	/**
	 * @return the adTypeTerrID
	 */
	public Integer getAdTypeTerrID() {
		return adTypeTerrID;
	}


	/**
	 * @param adTypeTerrID the adTypeTerrID to set
	 */
	public void setAdTypeTerrID(Integer adTypeTerrID) {
		this.adTypeTerrID = adTypeTerrID;
	}


	/**
	 * @return the adTypeDecompTerrID
	 */
	public Integer getAdTypeDecompTerrID() {
		return adTypeDecompTerrID;
	}


	/**
	 * @param adTypeDecompTerrID the adTypeDecompTerrID to set
	 */
	public void setAdTypeDecompTerrID(Integer adTypeDecompTerrID) {
		this.adTypeDecompTerrID = adTypeDecompTerrID;
	}


	public Application() {}

   
	public int getId() { return this.id; }
	public void setId(int adId ) { this.id = adId; }

	public Integer getAdCalendarId() {
		return this.adCalendarId;
	}
	public void setAdCalendarId(Integer adCalendarId) {
		this.adCalendarId = adCalendarId;
	}
	public Integer getAdDsId() {
		return this.adDsId;
	}
	public void setAdDsId(Integer adDsId) {
		this.adDsId = adDsId;
	}
	public Integer getAdAlternateDsId() {
		return this.adAlternateDsId;
	}
	public void setAdAlternateDsId(Integer adAlternateDsId) {
		this.adAlternateDsId = adAlternateDsId;
	}
	public String getAdBusinessCode() {
		return this.adBusinessCode;
	}
	public void setAdBusinessCode(String adBusinessCode) {
		this.adBusinessCode = adBusinessCode;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String adBusinessName) {
		this.name = adBusinessName;
	}
	public String getAdBusinessDefinition() {
		return this.adBusinessDefinition;
	}
	public void setAdBusinessDefinition(String adBusinessDefinition) {
		this.adBusinessDefinition = adBusinessDefinition;
	}
	public String getAdBusinessComment() {
		return this.adBusinessComment;
	}
	public void setAdBusinessComment(String adBusinessComment) {
		this.adBusinessComment = adBusinessComment;
	}
	public String getAdBusinessTargetUser() {
		return this.adBusinessTargetUser;
	}
	public void setAdBusinessTargetUser(String adBusinessTargetUser) {
		this.adBusinessTargetUser = adBusinessTargetUser;
	}
	public Integer getAdBusinessRequestorId() {
		return this.adBusinessRequestorId;
	}
	public void setAdBusinessRequestorId(Integer adBusinessRequestorId) {
		this.adBusinessRequestorId = adBusinessRequestorId;
	}
	public Date getAdRequestDate() {
		return this.adRequestDate;
	}
	public void setAdRequestDate(Date adRequestDate) {
		this.adRequestDate = adRequestDate;
	}
	public Integer getAdBusinessCreatorId() {
		return this.adBusinessCreatorId;
	}
	public void setAdBusinessCreatorId(Integer adBusinessCreatorId) {
		this.adBusinessCreatorId = adBusinessCreatorId;
	}
	public Date getAdCreationDate() {
		return this.adCreationDate;
	}
	public void setAdCreationDate(Date adCreationDate) {
		this.adCreationDate = adCreationDate;
	}
	public Integer getAdStatusId() {
		return this.adStatusId;
	}
	public void setAdStatusId(Integer adStatusId) {
		this.adStatusId = adStatusId;
	}
	public Date getAdProductionDate() {
		return this.adProductionDate;
	}
	public void setAdProductionDate(Date adProductionDate) {
		this.adProductionDate = adProductionDate;
	}
	public String getAdWithHistoricalData() {
		return this.adWithHistoricalData;
	}
	public void setAdWithHistoricalData(String adWithHistoricalData) {
		this.adWithHistoricalData = adWithHistoricalData;
	}
	public String getAdReleaseNumber() {
		return this.adReleaseNumber;
	}
	public void setAdReleaseNumber(String adReleaseNumber) {
		this.adReleaseNumber = adReleaseNumber;
	}
	public Integer getAdNumberOfDashboards() {
		return this.adNumberOfDashboards;
	}
	public void setAdNumberOfDashboards(Integer adNumberOfDashboards) {
		this.adNumberOfDashboards = adNumberOfDashboards;
	}
	public Integer getAdNumberOfKeyIndicators() {
		return this.adNumberOfKeyIndicators;
	}
	public void setAdNumberOfKeyIndicators(Integer adNumberOfKeyIndicators) {
		this.adNumberOfKeyIndicators = adNumberOfKeyIndicators;
	}
	public String getAdImage() {
		return this.adImage;
	}
	public void setAdImage(String adImage) {
		this.adImage = adImage;
	}
	public Boolean getAdIsVisible() {
		return adIsVisible;
	}
	public void setAdIsVisible(Boolean adIsVisible) {
		this.adIsVisible = adIsVisible;
	}
	public String getAdCustom1() {
		return adCustom1;
	}
	public void setAdCustom1(String adCustom1) {
		this.adCustom1 = adCustom1;
	}

	public String toString(){
		return name;
	}


	/**
	 * @return the ad_gl_Superficie
	 */
	public String getAd_gl_Superficie() {
		return ad_gl_Superficie;
	}


	/**
	 * @param ad_gl_Superficie the ad_gl_Superficie to set
	 */
	public void setAd_gl_Superficie(String ad_gl_Superficie) {
		this.ad_gl_Superficie = ad_gl_Superficie;
	}


	/**
	 * @return the ad_gl_Population
	 */
	public String getAd_gl_Population() {
		return ad_gl_Population;
	}


	/**
	 * @param ad_gl_Population the ad_gl_Population to set
	 */
	public void setAd_gl_Population(String ad_gl_Population) {
		this.ad_gl_Population = ad_gl_Population;
	}


	/**
	 * @return the ad_gl_CodInsee
	 */
	public String getAd_gl_CodInsee() {
		return ad_gl_CodInsee;
	}


	/**
	 * @param ad_gl_CodInsee the ad_gl_CodInsee to set
	 */
	public void setAd_gl_CodInsee(String ad_gl_CodInsee) {
		this.ad_gl_CodInsee = ad_gl_CodInsee;
	}


	/**
	 * @return the ad_gl_CodLocalisationX
	 */
	public String getAd_gl_CodLocalisationX() {
		return ad_gl_CodLocalisationX;
	}


	/**
	 * @param ad_gl_CodLocalisationX the ad_gl_CodLocalisationX to set
	 */
	public void setAd_gl_CodLocalisationX(String ad_gl_CodLocalisationX) {
		this.ad_gl_CodLocalisationX = ad_gl_CodLocalisationX;
	}


	/**
	 * @return the ad_gl_CodLocalisationY
	 */
	public String getAd_gl_CodLocalisationY() {
		return ad_gl_CodLocalisationY;
	}


	/**
	 * @param ad_gl_CodLocalisationY the ad_gl_CodLocalisationY to set
	 */
	public void setAd_gl_CodLocalisationY(String ad_gl_CodLocalisationY) {
		this.ad_gl_CodLocalisationY = ad_gl_CodLocalisationY;
	}


	/**
	 * @return the ad_gl_DecompTerrID
	 */
	public Integer getAd_gl_DecompTerrID() {
		return ad_gl_DecompTerrID;
	}


	/**
	 * @param ad_gl_DecompTerrID the ad_gl_DecompTerrID to set
	 */
	public void setAd_gl_DecompTerrID(Integer ad_gl_DecompTerrID) {
		this.ad_gl_DecompTerrID = ad_gl_DecompTerrID;
	}


	/**
	 * @return the security
	 */
	public int getSecurity() {
		return security;
	}


	/**
	 * @param security the security to set
	 */
	public void setSecurity(int security) {
		this.security = security;
	}


	public void setChildren(List<Application> children) {
		this.children = children;
	}


	public List<Application> getChildren() {
		return children;
	} 
	
	public void addChild(Application child) {
		/**
		 * A trick to avoid null values in the list.
		 * Yes because there's null values in the list !!!!
		 */
		if(child == null) {
			return;
		}
		if(children == null) {
			children = new ArrayList<Application>();
		}
		children.add(child);
	}


	public void setParent(Application parent) {
		this.parent = parent;
	}


	public Application getParent() {
		return parent;
	}


	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}


	public boolean isHasChildren() {
		return hasChildren;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setParentGroup(Group parentGroup) {
		this.parentGroup = parentGroup;
	}


	public Group getParentGroup() {
		return parentGroup;
	}


	public void setDtmColVersion(String dtmColVersion) {
		this.dtmColVersion = dtmColVersion;
	}


	public String getDtmColVersion() {
		return dtmColVersion;
	}
    
}
