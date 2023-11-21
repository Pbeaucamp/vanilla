package bpm.norparena.mapmanager.fusionmap.viewers;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapSpecificationEntity;

public class FusionMapEntityLabelProvider extends ColumnLabelProvider{
	public enum FusionMapEntityAttribute{
		InternalId, LongName, ShortName;
	}
	
	private FusionMapEntityAttribute attribute;
	public FusionMapEntityLabelProvider(FusionMapEntityAttribute attribute){
		this.attribute = attribute;
	}
	
	@Override
	public String getText(Object element) {
		String value = null;
		if(element instanceof IFusionMapSpecificationEntity) {
			IFusionMapSpecificationEntity entity = (IFusionMapSpecificationEntity)element;
			
			switch (attribute) {
			case InternalId:
				value = entity.getFusionMapInternalId();
				break;
			case LongName:
				value = entity.getFusionMapLongName();
				break;
			case  ShortName:
				value = entity.getFusionMapShortName();
				break;
			}
		}
		
		else if(element instanceof IOpenLayersMapSpecificationEntity) {
			IOpenLayersMapSpecificationEntity entity = (IOpenLayersMapSpecificationEntity)element;
			
			switch (attribute) {
			case InternalId:
				value = entity.getInternalId();
				break;
			case LongName:
				value = entity.getLongName();
				break;
			case  ShortName:
				value = entity.getShortName();
				break;
			}
		}
		
		if (value == null){
			return ""; //$NON-NLS-1$
		}
		return value;
	}
}
