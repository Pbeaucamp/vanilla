package bpm.vanillahub.runtime.managers;

import java.util.List;

import bpm.vanilla.platform.core.beans.resources.ListOfValues;

public class ListOfValuesManager extends ResourceManager<ListOfValues> {

	private static final String LOV_FILE_NAME = "lov.xml";

	public ListOfValuesManager(String filePath) {
		super(filePath, LOV_FILE_NAME, "Lov");
	}
	
	@Override
	protected void manageResourceForAdd(ListOfValues resource) { }
	
	@Override
	protected void manageResourceForModification(ListOfValues newResource, ListOfValues oldResource) {
		String name = oldResource.getName();
		List<String> values = oldResource.getValues();
		
		newResource.updateInfo(name, values);
	}
}
