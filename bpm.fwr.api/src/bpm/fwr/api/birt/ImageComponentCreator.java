package bpm.fwr.api.birt;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ImageHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.EmbeddedImage;

import bpm.fwr.api.beans.components.ImageComponent;

public class ImageComponentCreator implements IComponentCreator<ImageComponent> {

	@Override
	public DesignElementHandle createComponent(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, ImageComponent component, String outputFormat) throws Exception {
		if (component.getImageData() != null && !component.getImageData().isEmpty()) {
			if(component.getType().equalsIgnoreCase("svg")) {
				EmbeddedImage embeddedImage = StructureFactory.createEmbeddedImage();
				embeddedImage.setType(DesignChoiceConstants.IMAGE_TYPE_IMAGE_SVG);
				embeddedImage.setData(component.getImageData().getBytes());
				embeddedImage.setName(component.getName());
				designHandle.addImage(embeddedImage);
			}
			else {
				EmbeddedImage embeddedImage = StructureFactory.createEmbeddedImage();
				embeddedImage.setType(DesignChoiceConstants.IMAGE_TYPE_IMAGE_PNG);
				embeddedImage.setData(Base64.decodeBase64(component.getImageData()));
				embeddedImage.setName(component.getName());
				designHandle.addImage(embeddedImage);
			}

			ImageHandle imageHandle = designFactory.newImage("Image_" + new Object().hashCode());
			imageHandle.setImageName(component.getName());
			imageHandle.setSize(DesignChoiceConstants.IMAGE_SIZE_SCALE_TO_ITEM);
			return imageHandle;
		}
		else {
			ImageHandle imageHandle = designFactory.newImage("Image_" + new Object().hashCode());
			imageHandle.setSource(DesignChoiceConstants.IMAGE_REF_TYPE_URL);
			imageHandle.setURL("\"" + component.getUrl() + "\"");
			imageHandle.setSize(DesignChoiceConstants.IMAGE_SIZE_SCALE_TO_ITEM);
			return imageHandle;
		}
	}

}
