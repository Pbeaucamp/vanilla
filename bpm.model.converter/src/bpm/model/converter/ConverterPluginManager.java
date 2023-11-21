package bpm.model.converter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import bpm.model.converter.core.IModelConverterFactory;
import bpm.model.converter.core.impl.ModelConverterFactory;

public class ConverterPluginManager {

	
	public static List<IModelConverterFactory> getConverterImplementers(){
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(Activator.PLUGIN_ID);

		List<IModelConverterFactory> l = new ArrayList<IModelConverterFactory>();
		for (IConfigurationElement e : config) {
			l.add(new ModelConverterFactory(e));
		}
		return l;
	}
	
	public static IModelConverterFactory getConverterImplementer(Class<?> targetClass) {
		
		for (IModelConverterFactory e : getConverterImplementers()) {
			if (e.getTargetClassName().equals(targetClass.getName())){
				return e;
			}
		}
		return null;
	}
}
