package bpm.fd.api.core.model;

import org.dom4j.Element;

import bpm.fd.api.core.model.structure.FactoryStructure;

/**
 * should be used by models designed specially for Android clients
 * (for now, no known restrictions under android, but it can change) 
 * @author ludo
 *
 */
public class FdAndroidModel extends FdModel{
	public static final String TYPE_ANDROID_DASHBOARD = "bpm.fd.api.core.model.FdAndroidModel";
	/**
	 * 

	public FdAndroidModel() {
		super();
		
	}
	 */
	/**
	 * @param project
	 * @param name
	 */
	public FdAndroidModel(FactoryStructure factory, FdProject project, String name) {
		super(factory, project, name);
		
	}
	public FdAndroidModel(FactoryStructure factory){
		super(factory);
	}

	/**
	 * @param project
	 */
	public FdAndroidModel(FactoryStructure factory, FdProject project) {
		super(factory, project);
		
	}


	@Override
	public Element getElement() {
		Element e =  super.getElement();
		e.addAttribute("type", TYPE_ANDROID_DASHBOARD);
		
		return e;
	}
}
