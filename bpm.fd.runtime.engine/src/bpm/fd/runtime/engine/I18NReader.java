package bpm.fd.runtime.engine;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.runtime.model.ComponentRuntime;
import bpm.fd.runtime.model.DashState;

public class I18NReader {

	private HashMap<String, Properties> files;

	public I18NReader(FdProject project) {
		files = new HashMap<String, Properties>();
		for(IResource r : project.getResources(FileProperties.class)) {
			String fName = r.getFile().getName();
			String localName = fName.replace("_components.properties", "");

			Properties p = new Properties();
			try {
				p.load(new FileInputStream(r.getFile()));

				files.put(localName, p);
				Logger.getLogger(getClass()).info("Loaded i18n File" + r.getFile().getAbsolutePath());
			} catch(Exception ex) {
				Logger.getLogger(getClass()).error("Could not load i18n File " + r.getFile().getName() + ":" + ex.getMessage(), ex);
			}
		}
	}

	public I18NReader(HashMap<String, Properties> files) {
		this.files = files;
	}

	public String getLabel(DashState state, String componentName, String key) {
		String val = null;
		for(String s : files.keySet()) {
			if(state.getDashInstance().getLocale() != null && state.getDashInstance().getLocale().toString().equals(s)) {
				val = files.get(s).getProperty(componentName + "." + key);
				break;
			}
		}
		// no i18N file
		if(val == null) {
			try {
				val = files.get("components.properties").getProperty(componentName + "." + key);
			} catch (Exception e) {
				return "empty";
			}
		}

		if(val == null) {
			return null;
		}
		int curr = 0;
		String currVal = new String(val);
		while(curr >= 0) {
			curr = currVal.indexOf("{$", curr);
			if(curr >= 0) {
				String varName = currVal.substring(curr + 2, currVal.indexOf("}", curr));
				String value = state.getComponentValue(varName) + "";

				try {
					ComponentRuntime component = state.getDashInstance().getDashBoard().getComponent(componentName);

					for(ComponentParameter p : ((IComponentDefinition) component.getElement()).getParameters()) {
						if(p.getName().equals(varName)) {
							value = state.getComponentValue(state.getDashInstance().getDashBoard().getDesignParameterProvider(p).getElement().getName());
							break;
						}
					}

					currVal = currVal.replace("{$" + varName + "}", value);
				} catch(Exception ex) {
					Logger.getLogger(getClass()).error(ex.getMessage(), ex);
					return null;
				}

			}

		}

		return currVal;
	}

}
