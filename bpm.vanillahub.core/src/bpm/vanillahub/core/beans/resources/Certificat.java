package bpm.vanillahub.core.beans.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class Certificat extends Resource {

	public enum TypeCertificat {
		OPEN_PGP(0), 
		PRIVATE_KEY(1);
		
		private int type;

		private static Map<Integer, TypeCertificat> map = new HashMap<Integer, TypeCertificat>();
		static {
	        for (TypeCertificat serverType : TypeCertificat.values()) {
	            map.put(serverType.getType(), serverType);
	        }
	    }
		
		private TypeCertificat(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static TypeCertificat valueOf(int serverType) {
	        return map.get(serverType);
	    }
	}

	private TypeCertificat typeCertificat;
	
	//Open PGP Options
	private String file;
	private VariableString technicalName = new VariableString();
	
	//Private Key Options
	private String password;
	
	public Certificat() {
		super("", TypeResource.CERTIFICAT);
	}

	public Certificat(String name) {
		super(name, TypeResource.CERTIFICAT);
		this.typeCertificat = TypeCertificat.OPEN_PGP;
	}

	public TypeCertificat getTypeCertificat() {
		return typeCertificat;
	}
	
	public void setTypeCertificat(TypeCertificat typeCertificat) {
		this.typeCertificat = typeCertificat;
	}
	
	public String getFile() {
		return file != null ? file : "";
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	
	public VariableString getTechnicalNameVS() {
		return technicalName;
	}
	
	public String getTechnicalNameDisplay() {
		return technicalName.getStringForTextbox();
	}
	
	public void setTechnicalName(VariableString technicalName) {
		this.technicalName = technicalName;
	}
	
//	public void addVariableToTechnicalName(Variable variable) {
//		this.technicalName.addVariable(variable);
//	}
	
	public String getPassword() {
		return password != null ? password : "";
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void updateInfo(String name, TypeCertificat type, String file, VariableString technicalName, String password) {
		setName(name);
		this.typeCertificat = type;
		this.file = file;
		this.technicalName = technicalName;
		this.password = password;
	}

	@Override
	public List<Variable> getVariables() {
		return technicalName.getVariables();
	}

	@Override
	public List<Parameter> getParameters() {
		return technicalName.getParameters();
	}
}
