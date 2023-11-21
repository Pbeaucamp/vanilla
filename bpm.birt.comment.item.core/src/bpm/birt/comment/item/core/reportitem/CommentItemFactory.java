package bpm.birt.comment.item.core.reportitem;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.extension.IMessages;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.birt.report.model.api.extension.ReportItemFactory;

import bpm.vanilla.platform.core.beans.comments.CommentDefinition;

public class CommentItemFactory extends ReportItemFactory {

	@Override
	public IMessages getMessages() {
		return null;
	}

	@Override
	public IReportItem newReportItem(DesignElementHandle modelHanlde) {
		if (modelHanlde instanceof ExtendedItemHandle && CommentItem.EXTENSION_NAME.equals(((ExtendedItemHandle) modelHanlde).getExtensionName())) {
			boolean exists = false;
			for (int i = 0; i < modelHanlde.getModuleHandle().getParameters().getCount(); i++) {
				if (modelHanlde.getModuleHandle().getParameters().get(i) instanceof ScalarParameterHandle) {
					ScalarParameterHandle han = (ScalarParameterHandle) modelHanlde.getModuleHandle().getParameters().get(i);
					if (han.getName().equals(CommentDefinition.ITEM_ID_PARAMETER) || han.getName().equals(CommentDefinition.REP_ID_PARAMETER) || han.getName().equals(CommentDefinition.DISPLAY_COMMENTS_PARAMETER)) {
						exists = true;
						break;
					}
				}
			}
			if (!exists) {
				ScalarParameterHandle paramItemId = modelHanlde.getModuleHandle().getElementFactory().newScalarParameter(CommentDefinition.ITEM_ID_PARAMETER);
				ScalarParameterHandle paramRepId = modelHanlde.getModuleHandle().getElementFactory().newScalarParameter(CommentDefinition.REP_ID_PARAMETER);
				ScalarParameterHandle paramDisplayComments = modelHanlde.getModuleHandle().getElementFactory().newScalarParameter(CommentDefinition.DISPLAY_COMMENTS_PARAMETER);
				try {
					paramItemId.setHidden(true);
					paramRepId.setHidden(true);
					paramDisplayComments.setHidden(true);
					paramItemId.setDefaultValue("-1");
					paramRepId.setDefaultValue("-1");
					paramDisplayComments.setDefaultValue("1");
					modelHanlde.getModuleHandle().getParameters().add(paramItemId);
					modelHanlde.getModuleHandle().getParameters().add(paramRepId);
					modelHanlde.getModuleHandle().getParameters().add(paramDisplayComments);
				} catch (SemanticException e) {
					e.printStackTrace();
				}
			}

			return new CommentItem((ExtendedItemHandle) modelHanlde);
		}
		return null;
	}

}
