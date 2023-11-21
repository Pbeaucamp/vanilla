package bpm.fd.api.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.structure.Cell;

/**
 * Project using more than one page
 * 
 * @author ludo
 * 
 */
public class MultiPageFdProject extends FdProject {
	public static final String PROPERTY_ADD_MODEL = "bpm.fd.api.core.model.addModel";
	public static final String PROPERTY_REMOVE_MODEL = "bpm.fd.api.core.model.removeModel";

	private List<FdModel> pagesModels = new ArrayList<FdModel>();

	public MultiPageFdProject(FdProjectDescriptor descriptor) {
		super(descriptor);
	}

	/**
	 * @param descriptor
	 * @param model
	 * @param dictionary
	 */
	public MultiPageFdProject(FdProjectDescriptor descriptor, FdModel model, Dictionary dictionary) {
		super(descriptor, model, dictionary);
	}

	/**
	 * @param descriptor
	 * @param model
	 */
	public MultiPageFdProject(FdProjectDescriptor descriptor, FdModel model) {
		super(descriptor, model);
	}

	public List<FdModel> getPagesModels() {
		return new ArrayList<FdModel>(pagesModels);
	}

	/**
	 * add a Model for a page
	 * 
	 * @param model
	 * @throws Exception
	 *             if a model with the same name is already in the projetc
	 */
	public void addPageModel(FdModel model) throws Exception {
		if(model == null) {
			return;
		}
		for(FdModel m : getPagesModels()) {
			if(m.getName().equals(model.getName())) {
				throw new Exception("A modelPage with the same name already exists");
			}
		}

		pagesModels.add(model);
		model.setProject(this);
		firePropertyChange(PROPERTY_ADD_MODEL, null, model);
	}

	/**
	 * remove the givenModel from the model page
	 * 
	 * @param model
	 */
	public void removePageModel(FdModel model) {
		if(pagesModels.remove(model)) {
			firePropertyChange(PROPERTY_REMOVE_MODEL, model, null);
		}
	}

	@Override
	public Collection<IComponentDefinition> getAvailableComponents() {
		HashMap<IComponentDefinition, ComponentConfig> map = getFdModel().getComponents();
		for(FdModel m : getPagesModels()) {
			HashMap<IComponentDefinition, ComponentConfig> map2 = m.getComponents();

			for(IComponentDefinition k : map2.keySet()) {
				if(map2.get(k) != null) {
					map.put(k, map2.get(k));
				}
			}
		}
		List<IComponentDefinition> l = new ArrayList<IComponentDefinition>();
		for(IComponentDefinition def : getDictionary().getComponents()) {
			if(!map.keySet().contains(def) && !l.contains(def)) {
				l.add(def);
			}
		}
		return l;
	}

	@Override
	public Collection<IComponentDefinition> getUsedComponents() {
		List<IComponentDefinition> c = new ArrayList<IComponentDefinition>(getFdModel().getComponents().keySet());
		for(FdModel m : getPagesModels()) {
			for(IComponentDefinition def : m.getComponents().keySet()) {
				c.add(def);
			}

		}
		return c;
	}

	@Override
	public void updateParameterProvider(String oldComponentName, IComponentDefinition def) {
		List<FdModel> l = new ArrayList<FdModel>(getPagesModels());
		l.add(getFdModel());
		for(FdModel m : l) {
			for(Cell c : m.getCellsUsingParameterProviderNames(oldComponentName)) {
				for(ComponentConfig cfg : c.getConfigs()) {
					for(ComponentParameter p : cfg.getParameters()) {
						if(cfg.getComponentNameFor(p).equals(oldComponentName)) {
							cfg.setParameterOrigin(p, def.getName());
						}
					}
				}

			}
		}

	}

	public FdModel getPageModel(String modelPage) {
		for(FdModel m : pagesModels) {
			if(m.getName().equals(modelPage)) {
				return m;
			}
		}
		return null;
	}

	public HashMap<IComponentDefinition, ComponentConfig> getComponents() {
		HashMap<IComponentDefinition, ComponentConfig> map = getFdModel().getComponents();

		for(FdModel m : getPagesModels()) {
			HashMap<IComponentDefinition, ComponentConfig> mm = m.getComponents();
			for(IComponentDefinition d : mm.keySet()) {
				map.put(d, mm.get(d));
			}
		}
		return map;
	}

}
