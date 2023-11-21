package bpm.fa.api.olap.projection;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Projection implements Serializable {
	
	public static final String TYPE_EXTRAPOLATION = "Extrapolation";
	public static final String TYPE_WHATIF = "What if";

	private String author;
	private String xml;
	private Date creationDate;
	private int id;
	private int itemId;
	private String name;
	private Date modificationDate;
	private String version;
	private String icon;
	private String flyover;
	private String desc;
	
	private int fasdId;
	private String cubeName;
	
	private String originalFact;
	private String projectionFact;
	
	private Date endDate;
	private Date startDate;
	
	private String projectionLevel;
	
	private String type;
	
	private List<ProjectionMeasure> projectionMeasures;
	
	private String comment;
	
	public Projection() {}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getFlyover() {
		return flyover;
	}

	public void setFlyover(String flyover) {
		this.flyover = flyover;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getFasdId() {
		return fasdId;
	}

	public void setFasdId(int fasdId) {
		this.fasdId = fasdId;
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	public String getOriginalFact() {
		return originalFact;
	}

	public void setOriginalFact(String originalFact) {
		this.originalFact = originalFact;
	}

	public String getProjectionFact() {
		return projectionFact;
	}

	public void setProjectionFact(String projectionFact) {
		this.projectionFact = projectionFact;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setProjectionMeasures(List<ProjectionMeasure> projectionMeasures) {
		this.projectionMeasures = projectionMeasures;
	}

	public List<ProjectionMeasure> getProjectionMeasures() {
		return projectionMeasures;
	}

	public String getEndDateString() {
		if(endDate != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			return format.format(endDate);
		}
		return "";
	}
	
	public void setEndDateString(String endDateString) throws Exception {
		if(endDateString != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			endDate = format.parse(endDateString);
		}
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}
	
	public String getStartDateString() {
		if(startDate != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			return format.format(startDate);
		}
		return "";
	}
	
	public void setStartDateString(String startDate) throws Exception {
		if(startDate != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			this.startDate = format.parse(startDate);
		}
	}

	public void setProjectionLevel(String projectionLevel) {
		this.projectionLevel = projectionLevel;
	}

	public String getProjectionLevel() {
		return projectionLevel;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}
}
