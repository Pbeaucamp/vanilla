package bpm.freemetrics.api.organisation.dashOrTheme;

import java.util.Date;

public class Dashboard {

	 private int id = 0;

	 private Integer ddApplicationId;
     private Integer ddSecurityId;
     private String  ddBusinessCode;
     private String  name;
     private Integer ddOwnerId;
     private Integer ddSortNumber;
     private Integer ddNumberOfIndicators;
     private Integer ddParentId;
     private Integer ddNumberOfDashboards;
     private Integer ddNumberOfDocuments;
     private String  ddComment;
     private String  ddBusinessComment;
     private Date    ddCreationDate;
     private String  ddimage;
     private Boolean ddIsDashTech;
     private String  ddCustom1;

    
    public Dashboard() {
    	super();
    }

    public Integer getId() { return id; }
	public void setId(int ddId) { this.id = ddId; }

    public Integer getDdSecurityId() {
        return this.ddSecurityId;
    }
    public void setDdSecurityId(Integer ddSecurityId) {
        this.ddSecurityId = ddSecurityId;
    }

    public String getDdBusinessCode() {
        return this.ddBusinessCode;
    }
    
    public void setDdBusinessCode(String ddBusinessCode) {
        this.ddBusinessCode = ddBusinessCode;
    }

    public String getName() {
        return this.name;
    }
    
    public void setName(String ddBusinessName) {
        this.name = ddBusinessName;
    }

    public Integer getDdOwnerId() {
        return this.ddOwnerId;
    }
    
    public void setDdOwnerId(Integer ddOwnerId) {
        this.ddOwnerId = ddOwnerId;
    }

    public Integer getDdSortNumber() {
        return this.ddSortNumber;
    }
    
    public void setDdSortNumber(Integer ddSortNumber) {
        this.ddSortNumber = ddSortNumber;
    }

    public Integer getDdNumberOfIndicators() {
        return this.ddNumberOfIndicators;
    }
    
    public void setDdNumberOfIndicators(Integer ddNumberOfIndicators) {
        this.ddNumberOfIndicators = ddNumberOfIndicators;
    }

    public Integer getDdParentId() {
        return this.ddParentId;
    }
    
    public void setDdParentId(Integer ddParentId) {
        this.ddParentId = ddParentId;
    }

    public Integer getDdNumberOfDashboards() {
        return this.ddNumberOfDashboards;
    }
    
    public void setDdNumberOfDashboards(Integer ddNumberOfDashboards) {
        this.ddNumberOfDashboards = ddNumberOfDashboards;
    }

    public Integer getDdNumberOfDocuments() {
        return this.ddNumberOfDocuments;
    }
    
    public void setDdNumberOfDocuments(Integer ddNumberOfDocuments) {
        this.ddNumberOfDocuments = ddNumberOfDocuments;
    }

    public String getDdComment() {
        return this.ddComment;
    }
    
    public void setDdComment(String ddComment) {
        this.ddComment = ddComment;
    }

    public String getDdBusinessComment() {
        return this.ddBusinessComment;
    }
    
    public void setDdBusinessComment(String ddBusinessComment) {
        this.ddBusinessComment = ddBusinessComment;
    }

    public Date getDdCreationDate() {
        return this.ddCreationDate;
    }
    
    public void setDdCreationDate(Date ddCreationDate) {
        this.ddCreationDate = ddCreationDate;
    }

	public String getDdImage() {
		return ddimage;
	}

	public void setDdImage(String ddimage) {
		this.ddimage = ddimage;
	}

	public Integer getDdApplicationId() {
		return ddApplicationId;
	}

	public void setDdApplicationId(Integer ddApplicationId) {
		this.ddApplicationId = ddApplicationId;
	}

	public String getDdimage() {
		return ddimage;
	}
	public void setDdimage(String ddimage) {
		this.ddimage = ddimage;
	}

	public Boolean getDdIsDashTech() {
		return ddIsDashTech;
	}
	public void setDdIsDashTech(Boolean ddIsDashTech) {
		this.ddIsDashTech = ddIsDashTech;
	}

	public String getDdCustom1() {
		return ddCustom1;
	}
	public void setDdCustom1(String ddCustom1) {
		this.ddCustom1 = ddCustom1;
	}

	public String toString(){
		return name;
	}
    
}
