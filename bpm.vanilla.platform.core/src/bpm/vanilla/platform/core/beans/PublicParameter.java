package bpm.vanilla.platform.core.beans;


//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;

public class PublicParameter {
	private int id;
	private int publicUrlId;
	private String parameterName;
	private String parameterValue;
	
	public PublicParameter() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = new Integer(id);
	}

	public int getPublicUrlId() {
		return publicUrlId;
	}

	public void setPublicUrlId(int publicUrlId) {
		this.publicUrlId = publicUrlId;
	}
	public void setPublicUrlId(String publicUrlId) {
		this.publicUrlId = new Integer(publicUrlId);
	}
	
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
	
//	public Element getElement() {
//		Element publicparameter = DocumentHelper.createElement("publicparameter");
//		publicparameter.addElement("id").setText(id + "");
//		publicparameter.addElement("publicurlid").setText(publicUrlId + "");
//		publicparameter.addElement("parametername").setText(parameterName);
//		publicparameter.addElement("parametervalue").setText(parameterValue);
//		
//		return publicparameter;
//	}
//	
//	public String getXml() {
//		return getElement().asXML();
//	}

}
