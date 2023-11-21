package bpm.norparena.mapmanager.viewers;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import bpm.vanilla.map.core.design.IMapDefinition;

public class MapDefinitionLabelProvider extends ColumnLabelProvider{
	public enum MapDefinitionAttribute{
		Label, Description, FusionMap;
	}
	
	private MapDefinitionAttribute attribute;
	public MapDefinitionLabelProvider(MapDefinitionAttribute attribute){
		this.attribute = attribute;
	}
	
	@Override
	public String getText(Object element) {
		IMapDefinition entity = (IMapDefinition)element;
		String value = null;
		switch (attribute) {
		case Label:
			value = entity.getLabel();
			break;
		case Description:
			value = entity.getDescription();
			break;
		case  FusionMap:
			value = entity.getFusionMapObject().getName();
			break;
		}
		
		if (value == null){
			return ""; //$NON-NLS-1$
		}
		return value;
	}
}
