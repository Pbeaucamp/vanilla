package bpm.freemetrics.api.organisation.metrics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Metric {

	private int id;
	private String name;

	private Integer mdSecurityId;
	private Integer mdGroupId;
	private Integer mdTypeId;  // 1 = Imported , 2 = Computed
	private String  mdBusinessCode;
	private String  mdBusinessDescription;
	private String  mdOrigin;

	private Integer mdTrendBegin= new Integer(0);
	private Integer mdTrendEnd= new Integer(0);
	
	private String  mdToleranceLimit;
	private String  mdCalculationType;
	private Integer mdDashboardSortNumber;
	private String  mdRepresentation;
	private String  mdCalculationTimeFrame;
	private Integer mdOwnerId;
	private String  mdComment;
	private Integer mdSourceId;
	private Integer mdWidgetSetId;
	private Integer mdTrendReportId;
	private Date    mdCreationDate;
	private String  mdImage;
	private Boolean mdIsRealTime;
	private Boolean mdIsDimAware;
	private Boolean mdIsDimChild;
	private String  mdFormat,mdGlUtilite;
	private String  target;
	private Boolean mdCanChangeGoal,mdGlIsCompteur,mdGlRespect,mdGlIsModif;
	private Integer mdGlSubThemeId,mdGlThemeId,mdGlOrganisationId,mdUnitId,mdGlTypeIndicateurId;
	
	private Integer minValue;
	private Float minSeuil;
	private Integer maxValue;
	private Float maxSeuil;
	private Integer tolerance;
	
	private String dtmTable;
	private String dtmColumnValue;
	private String dtmColumnId;
	
	private List<MetricDataProperty> properties;
	
	private List<Metric> children;

	public String getDtmTable() {
		return dtmTable;
	}

	public void setDtmTable(String dtmTable) {
		this.dtmTable = dtmTable;
	}

	public String getDtmColumnValue() {
		return dtmColumnValue;
	}

	public void setDtmColumnValue(String dtmColumnValue) {
		this.dtmColumnValue = dtmColumnValue;
	}

	public String getDtmColumnId() {
		return dtmColumnId;
	}

	public void setDtmColumnId(String dtmColumnId) {
		this.dtmColumnId = dtmColumnId;
	}

	public Metric() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the mdSecurityId
	 */
	public Integer getMdSecurityId() {
		return mdSecurityId;
	}

	/**
	 * @param mdSecurityId the mdSecurityId to set
	 */
	public void setMdSecurityId(Integer mdSecurityId) {
		this.mdSecurityId = mdSecurityId;
	}

	/**
	 * @return the mdGroupId
	 */
	public Integer getMdGroupId() {
		return mdGroupId;
	}

	/**
	 * @param mdGroupId the mdGroupId to set
	 */
	public void setMdGroupId(Integer mdGroupId) {
		this.mdGroupId = mdGroupId;
	}

	/**
	 * @return the mdTypeId
	 */
	public Integer getMdTypeId() {
		return mdTypeId;
	}

	/**
	 * @param mdTypeId the mdTypeId to set
	 */
	public void setMdTypeId(Integer mdTypeId) {
		this.mdTypeId = mdTypeId;
	}

	/**
	 * @return the mdBusinessCode
	 */
	public String getMdBusinessCode() {
		return mdBusinessCode;
	}

	/**
	 * @param mdBusinessCode the mdBusinessCode to set
	 */
	public void setMdBusinessCode(String mdBusinessCode) {
		this.mdBusinessCode = mdBusinessCode;
	}

	/**
	 * @return the mdBusinessDescription
	 */
	public String getMdBusinessDescription() {
		return mdBusinessDescription;
	}

	/**
	 * @param mdBusinessDescription the mdBusinessDescription to set
	 */
	public void setMdBusinessDescription(String mdBusinessDescription) {
		this.mdBusinessDescription = mdBusinessDescription;
	}

	/**
	 * @return the mdOrigin
	 */
	public String getMdOrigin() {
		return mdOrigin;
	}

	/**
	 * @param mdOrigin the mdOrigin to set
	 */
	public void setMdOrigin(String mdOrigin) {
		this.mdOrigin = mdOrigin;
	}

	
	/**
	 * @return the mdTrendBegin
	 */
	public Integer getMdTrendBegin() {
		return mdTrendBegin;
	}

	/**
	 * @param mdTrendBegin the mdTrendBegin to set
	 */
	public void setMdTrendBegin(Integer mdTrendBegin) {
		if(mdTrendBegin!= null)
			this.mdTrendBegin = mdTrendBegin;
	}

	/**
	 * @return the mdTrendEnd
	 */
	public Integer getMdTrendEnd() {
		return mdTrendEnd;
	}

	/**
	 * @param mdTrendEnd the mdTrendEnd to set
	 */
	public void setMdTrendEnd(Integer mdTrendEnd) {
		if(mdTrendBegin!= null)
			this.mdTrendEnd = mdTrendEnd;
	}

	/**
	 * @return the mdToleranceLimit
	 */
	public String getMdToleranceLimit() {
		return mdToleranceLimit;
	}

	/**
	 * @param mdToleranceLimit the mdToleranceLimit to set
	 */
	public void setMdToleranceLimit(String mdToleranceLimit) {
		this.mdToleranceLimit = mdToleranceLimit;
	}

	/**
	 * @return the mdCalculationType
	 */
	public String getMdCalculationType() {
		return mdCalculationType;
	}

	/**
	 * @param mdCalculationType the mdCalculationType to set
	 */
	public void setMdCalculationType(String mdCalculationType) {
		this.mdCalculationType = mdCalculationType;
	}

	/**
	 * @return the mdDashboardSortNumber
	 */
	public Integer getMdDashboardSortNumber() {
		return mdDashboardSortNumber;
	}

	/**
	 * @param mdDashboardSortNumber the mdDashboardSortNumber to set
	 */
	public void setMdDashboardSortNumber(Integer mdDashboardSortNumber) {
		this.mdDashboardSortNumber = mdDashboardSortNumber;
	}

	/**
	 * @return the mdRepresentation
	 */
	public String getMdRepresentation() {
		return mdRepresentation;
	}

	/**
	 * @param mdRepresentation the mdRepresentation to set
	 */
	public void setMdRepresentation(String mdRepresentation) {
		this.mdRepresentation = mdRepresentation;
	}

	/**
	 * @return the mdCalculationTimeFrame
	 */
	public String getMdCalculationTimeFrame() {
		return mdCalculationTimeFrame;
	}

	/**
	 * @param mdCalculationTimeFrame the mdCalculationTimeFrame to set
	 */
	public void setMdCalculationTimeFrame(String mdCalculationTimeFrame) {
		this.mdCalculationTimeFrame = mdCalculationTimeFrame;
	}

	/**
	 * @return the mdOwnerId
	 */
	public Integer getMdOwnerId() {
		return mdOwnerId;
	}

	/**
	 * @param mdOwnerId the mdOwnerId to set
	 */
	public void setMdOwnerId(Integer mdOwnerId) {
		this.mdOwnerId = mdOwnerId;
	}

	/**
	 * @return the mdComment
	 */
	public String getMdComment() {
		return mdComment;
	}

	/**
	 * @param mdComment the mdComment to set
	 */
	public void setMdComment(String mdComment) {
		this.mdComment = mdComment;
	}

	/**
	 * @return the mdSourceId
	 */
	public Integer getMdSourceId() {
		return mdSourceId;
	}

	/**
	 * @param mdSourceId the mdSourceId to set
	 */
	public void setMdSourceId(Integer mdSourceId) {
		this.mdSourceId = mdSourceId;
	}

	/**
	 * @return the mdWidgetSetId
	 */
	public Integer getMdWidgetSetId() {
		return mdWidgetSetId;
	}

	/**
	 * @param mdWidgetSetId the mdWidgetSetId to set
	 */
	public void setMdWidgetSetId(Integer mdWidgetSetId) {
		this.mdWidgetSetId = mdWidgetSetId;
	}

	/**
	 * @return the mdTrendReportId
	 */
	public Integer getMdTrendReportId() {
		return mdTrendReportId;
	}

	/**
	 * @param mdTrendReportId the mdTrendReportId to set
	 */
	public void setMdTrendReportId(Integer mdTrendReportId) {
		this.mdTrendReportId = mdTrendReportId;
	}

	/**
	 * @return the mdCreationDate
	 */
	public Date getMdCreationDate() {
		return mdCreationDate;
	}

	/**
	 * @param mdCreationDate the mdCreationDate to set
	 */
	public void setMdCreationDate(Date mdCreationDate) {
		this.mdCreationDate = mdCreationDate;
	}

	/**
	 * @return the mdImage
	 */
	public String getMdImage() {
		return mdImage;
	}

	/**
	 * @param mdImage the mdImage to set
	 */
	public void setMdImage(String mdImage) {
		this.mdImage = mdImage;
	}

	/**
	 * @return the mdIsRealTime
	 */
	public Boolean getMdIsRealTime() {
		return mdIsRealTime;
	}

	/**
	 * @param mdIsRealTime the mdIsRealTime to set
	 */
	public void setMdIsRealTime(Boolean mdIsRealTime) {
		this.mdIsRealTime = mdIsRealTime;
	}

	/**
	 * @return the mdIsDimAware
	 */
	public Boolean getMdIsDimAware() {
		return mdIsDimAware;
	}

	/**
	 * @param mdIsDimAware the mdIsDimAware to set
	 */
	public void setMdIsDimAware(Boolean mdIsDimAware) {
		this.mdIsDimAware = mdIsDimAware;
	}

	/**
	 * @return the mdIsDimChild
	 */
	public Boolean getMdIsDimChild() {
		return mdIsDimChild;
	}

	/**
	 * @param mdIsDimChild the mdIsDimChild to set
	 */
	public void setMdIsDimChild(Boolean mdIsDimChild) {
		this.mdIsDimChild = mdIsDimChild;
	}

	/**
	 * @return the mdFormat
	 */
	public String getMdFormat() {
		return mdFormat;
	}

	/**
	 * @param mdFormat the mdFormat to set
	 */
	public void setMdFormat(String mdFormat) {
		this.mdFormat = mdFormat;
	}

	/**
	 * @return the mdGlTypeIndicateur
	 */
	public Integer getMdGlTypeIndicateurId() {
		return mdGlTypeIndicateurId;
	}

	/**
	 * @param mdGlTypeIndicateur the mdGlTypeIndicateur to set
	 */
	public void setMdGlTypeIndicateurId(Integer mdGlTypeIndicateur) {
		this.mdGlTypeIndicateurId = mdGlTypeIndicateur;
	}

	/**
	 * @return the mdGlUtilite
	 */
	public String getMdGlUtilite() {
		return mdGlUtilite;
	}

	/**
	 * @param mdGlUtilite the mdGlUtilite to set
	 */
	public void setMdGlUtilite(String mdGlUtilite) {
		this.mdGlUtilite = mdGlUtilite;
	}


	/**
	 * use getTarget() instead
	 * @return
	 */
	@Deprecated 
	public String getMdCustom1() {
		return target;
	}
	
	/**
	 * use setTarget() instead
	 * @param mdCustom1
	 */
	@Deprecated
	public void setMdCustom1(String mdCustom1) {
		this.target = mdCustom1;
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the mdCanChangeGoal
	 */
	public Boolean getMdCanChangeGoal() {
		return mdCanChangeGoal;
	}

	/**
	 * @param mdCanChangeGoal the mdCanChangeGoal to set
	 */
	public void setMdCanChangeGoal(Boolean mdCanChangeGoal) {
		this.mdCanChangeGoal = mdCanChangeGoal;
	}

	/**
	 * @return the mdGlIsCompteur
	 */
	public Boolean getMdGlIsCompteur() {
		return mdGlIsCompteur;
	}

	/**
	 * @param mdGlIsCompteur the mdGlIsCompteur to set
	 */
	public void setMdGlIsCompteur(Boolean mdGlIsCompteur) {
		this.mdGlIsCompteur = mdGlIsCompteur;
	}

	/**
	 * @return the mdGlRespect
	 */
	public Boolean getMdGlRespect() {
		return mdGlRespect;
	}

	/**
	 * @param mdGlRespect the mdGlRespect to set
	 */
	public void setMdGlRespect(Boolean mdGlRespect) {
		this.mdGlRespect = mdGlRespect;
	}

	/**
	 * @return the mdGlIsModif
	 */
	public Boolean getMdGlIsModif() {
		return mdGlIsModif;
	}

	/**
	 * @param mdGlIsModif the mdGlIsModif to set
	 */
	public void setMdGlIsModif(Boolean mdGlIsModif) {
		this.mdGlIsModif = mdGlIsModif;
	}

	/**
	 * @return the mdGlSubThemeId
	 */
	public Integer getMdGlSubThemeId() {
		return mdGlSubThemeId;
	}

	/**
	 * @param mdGlSubThemeId the mdGlSubThemeId to set
	 */
	public void setMdGlSubThemeId(Integer mdGlSubThemeId) {
		this.mdGlSubThemeId = mdGlSubThemeId;
	}

	/**
	 * @return the mdGlThemeId
	 */
	public Integer getMdGlThemeId() {
		return mdGlThemeId;
	}

	/**
	 * @param mdGlThemeId the mdGlThemeId to set
	 */
	public void setMdGlThemeId(Integer mdGlThemeId) {
		this.mdGlThemeId = mdGlThemeId;
	}

	/**
	 * @return the mdGlOrganisationId
	 */
	public Integer getMdGlOrganisationId() {
		return mdGlOrganisationId;
	}

	/**
	 * @param mdGlOrganisationId the mdGlOrganisationId to set
	 */
	public void setMdGlOrganisationId(Integer mdGlOrganisationId) {
		this.mdGlOrganisationId = mdGlOrganisationId;
	}

	/**
	 * @return the mdUnitId
	 */
	public Integer getMdUnitId() {
		return mdUnitId;
	}

	/**
	 * @param mdUnitId the mdUnitId to set
	 */
	public void setMdUnitId(Integer mdUnitId) {
		this.mdUnitId = mdUnitId;
	}

	/**
	 * Use only for an indicateur
	 * The DEFAULT Minimum value for an indicateur
	 * This value can be override with FmLoaderWeb
	 * @return
	 */
	public Integer getMinValue() {
		return minValue;
	}

	/**
	 * Use only for an indicateur
	 * The DEFAULT Minimum value for an indicateur
	 * This value can be override with FmLoaderWeb
	 * @param
	 */
	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	/**
	 * Use only for an indicateur
	 * The DEFAULT Minimum Seuil for an indicateur
	 * This value can be override with FmLoaderWeb
	 * @return
	 */
	public Float getMinSeuil() {
		return minSeuil;
	}

	/**
	 * Use only for an indicateur
	 * The DEFAULT Minimum Seuil for an indicateur
	 * This value can be override with FmLoaderWeb
	 * @param
	 */
	public void setMinSeuil(Float minSeuil) {
		this.minSeuil = minSeuil;
	}

	/**
	 * Use only for an indicateur
	 * The DEFAULT Maximum value for an indicateur
	 * This value can be override with FmLoaderWeb
	 * @return
	 */
	public Integer getMaxValue() {
		return maxValue;
	}

	/**
	 * Use only for an indicateur
	 * The DEFAULT Maximum value for an indicateur
	 * This value can be override with FmLoaderWeb
	 * @param
	 */
	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * Use only for an indicateur
	 * The DEFAULT Maximum Seuil for an indicateur
	 * This value can be override with FmLoaderWeb
	 * @return
	 */
	public Float getMaxSeuil() {
		return maxSeuil;
	}

	/**
	 * Use only for an indicateur
	 * The DEFAULT Maximum Seuil for an indicateur
	 * This value can be override with FmLoaderWeb
	 * @param
	 */
	public void setMaxSeuil(Float maxSeuil) {
		this.maxSeuil = maxSeuil;
	}

	/**
	 * Use only for an indicateur
	 * The DEFAULT Tolerance for an indicateur
	 * This value can be override with FmLoaderWeb
	 * @return
	 */
	public Integer getTolerance() {
		return tolerance;
	}

	/**
	 * Use only for an indicateur
	 * The DEFAULT Tolerance Seuil for an indicateur
	 * This value can be override with FmLoaderWeb
	 * @param
	 */
	public void setTolerance(String tolerance) {
		this.tolerance = new Integer(tolerance);
	}
	
	public void setTolerance(Integer tolerance) {
		this.tolerance = tolerance;
	}

	public void setProperties(List<MetricDataProperty> properties) {
		this.properties = properties;
	}

	public List<MetricDataProperty> getProperties() {
		if(properties == null) {
			properties = new ArrayList<MetricDataProperty>();
		}
		return properties;
	}

	public void addProperty(MetricDataProperty property) {
		if(properties == null) {
			properties = new ArrayList<MetricDataProperty>();
		}
		properties.add(property);
	}

	public void setChildren(List<Metric> children) {
		this.children = children;
	}

	public List<Metric> getChildren() {
		return children;
	}

	@Override
	public boolean equals(Object obj) {
		return this.getId() == ((Metric)obj).getId();
	}
}
