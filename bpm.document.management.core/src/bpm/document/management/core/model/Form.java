package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Form implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public enum FormType {
		CLASSIC(0),
		METADATA(1),
		ORBEON(2);
		
		private int type;

		private static Map<Integer, FormType> map = new HashMap<Integer, FormType>();
		static {
	        for (FormType type : FormType.values()) {
	            map.put(type.getType(), type);
	        }
	    }
		
		private FormType(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static FormType valueOf(int type) {
	        return map.get(type);
	    }
		
		public static List<FormType> getTypesCreation() {
			List<FormType> types = new ArrayList<FormType>();
			types.add(CLASSIC);
			types.add(METADATA);
			types.add(ORBEON);
			return types;
		}
	}
	
	private int id;
	private String formName;
	private String formComment;
	private String formType = "";
	private String formBackground;
	private String formLogo;
	private Date creationDate = new Date();
	private String fileExtension;
	
	private String orbeonApp;
	private String orbeonName;

	
	private FormType type = FormType.CLASSIC;
	
	private int idSourceConnection = 0;
	private String sourceRequest = "";

	private List<FormField> fields = new ArrayList<FormField>();
	private List<OrbeonFormSection> sections = new ArrayList<OrbeonFormSection>();
	
	public Form() {
	}

	public Form(String formName, String formComment, String formType, String formBackground, String formLogo) {
		super();
		this.formName = formName;
		this.formComment = formComment;
		this.formType = formType;
		this.formBackground = formBackground;
		this.formLogo = formLogo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getFormComment() {
		return formComment;
	}

	public void setFormComment(String formComment) {
		this.formComment = formComment;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getFormBackground() {
		return formBackground;
	}

	public void setFormBackground(String formBackground) {
		this.formBackground = formBackground;
	}

	public String getFormLogo() {
		return formLogo;
	}

	public void setFormLogo(String formLogo) {
		this.formLogo = formLogo;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	public FormType getType() {
		return type;
	}
	
	public void setType(FormType type) {
		this.type = type;
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((Form)obj).getId();
	}
	
	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}
	
	public List<FormField> getFields() {
		return fields;
	}
	
	@Override
	public String toString() {
		return formName;
	}

	public String getOrbeonApp() {
		return orbeonApp;
	}

	public String getOrbeonName() {
		return orbeonName;
	}

	public void setOrbeonApp(String orbeonApp) {
		this.orbeonApp = orbeonApp;
	}

	public void setOrbeonName(String orbeonName) {
		this.orbeonName = orbeonName;
	}

	public List<OrbeonFormSection> getSections() {
		return sections;
	}

	public void setSections(List<OrbeonFormSection> sections) {
		this.sections = sections;
	}

	public int getIdSourceConnection() {
		return idSourceConnection;
	}

	public void setIdSourceConnection(int idSourceConnection) {
		this.idSourceConnection = idSourceConnection;
	}

	public String getSourceRequest() {
		return sourceRequest;
	}

	public void setSourceRequest(String sourceRequest) {
		this.sourceRequest = sourceRequest;
	}
	
	
}
