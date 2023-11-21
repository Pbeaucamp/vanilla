package bpm.vanilla.platform.core.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MarkdownType implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int idProject;
	private int idScript;
	
	private String type;
	
	private List<Parameter> parameters = new ArrayList<Parameter>();
	
	public MarkdownType() { }
	
	public MarkdownType(int idProject, int idScript, String type) {
		this.idProject = idProject;
		this.idScript = idScript;
		this.type = type;
	}

	public int getIdProject() {
		return idProject;
	}

	public void setIdProject(int idProject) {
		this.idProject = idProject;
	}

	public int getIdScript() {
		return idScript;
	}

	public void setIdScript(int idScript) {
		this.idScript = idScript;
	}
	
	public Parameter getParameter(String parameterName) {
		for (Parameter p : parameters) {
			if (p.getName().equals(parameterName)) {
				return p;
			}
		}
		return null;
	}
	
	public void addParameter(Parameter p) throws Exception {
		for (Parameter _p : parameters) {
			if (_p.getName().equals(p.getName())) {
				throw new Exception("A parameter named " + p.getName() + " already exists.");
			}
		}
		parameters.add(p);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
}
