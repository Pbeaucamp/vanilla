package bpm.fwr.api.birt;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ReportDesignHandle;

import bpm.birt.comment.item.core.reportitem.CommentItem;
import bpm.fwr.api.beans.components.BirtCommentComponents;

public class BirtCommentComponentCreator implements IComponentCreator<BirtCommentComponents> {

	@Override
	public DesignElementHandle createComponent(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, BirtCommentComponents component, String outputFormat) throws Exception {
		CommentItem it = new CommentItem(designFactory.newExtendedItem(component.getTitle(), CommentItem.EXTENSION_NAME));
		return it.getItemHandle();
	}

}
