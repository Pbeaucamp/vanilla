package bpm.freemetrics.api.features.infos;

public class Organisation {
	private int id;
	private String name;
	private String type;
	private String adresse;
	private String tel;
	private String fax;
	private String siteWeb;
	private String comment,code_insee,codelocalisation_x,codelocalisation_y;
	
	/**
	 * @return the code_insee
	 */
	public String getCode_insee() {
		return code_insee;
	}

	/**
	 * @param code_insee the code_insee to set
	 */
	public void setCode_insee(String code_insee) {
		this.code_insee = code_insee;
	}

	/**
	 * @return the codelocalisation_x
	 */
	public String getCodelocalisation_x() {
		return codelocalisation_x;
	}

	/**
	 * @param codelocalisation_x the codelocalisation_x to set
	 */
	public void setCodelocalisation_x(String codelocalisation_x) {
		this.codelocalisation_x = codelocalisation_x;
	}

	/**
	 * @return the codelocalisation_y
	 */
	public String getCodelocalisation_y() {
		return codelocalisation_y;
	}

	/**
	 * @param codelocalisation_y the codelocalisation_y to set
	 */
	public void setCodelocalisation_y(String codelocalisation_y) {
		this.codelocalisation_y = codelocalisation_y;
	}

	public Organisation() {
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getSiteWeb() {
		return siteWeb;
	}

	public void setSiteWeb(String siteWeb) {
		this.siteWeb = siteWeb;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	

}
