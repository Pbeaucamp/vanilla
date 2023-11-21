package bpm.vanillahub.core.beans.resources;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;

public class FileXSD extends Resource {
	
	private String file;
	
	public FileXSD() {
		super("", TypeResource.XSD);
	}

	public FileXSD(String name) {
		super(name, TypeResource.XSD);
	}
	
	public String getFile() {
		return file != null ? file : "";
	}
	
	public void setFile(String file) {
		this.file = file;
	}

	public void updateInfo(String name, String file) {
		setName(name);
		this.file = file;
	}

	@Override
	public List<Variable> getVariables() {
		return new ArrayList<Variable>();
	}

	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}
}
