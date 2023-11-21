package bpm.birt.comment.item.ui.builder;

import org.eclipse.birt.report.designer.ui.extensions.ReportItemBuilderUI;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import bpm.birt.comment.item.core.reportitem.CommentItem;
import bpm.birt.comment.item.ui.wizard.WizardEditor;

public class CommentItemBuilder extends ReportItemBuilderUI {

	@Override
	public int open(ExtendedItemHandle handle) {
		try {
			IReportItem item = handle.getReportItem();
			if (item instanceof CommentItem) {
				WizardEditor wiz = new WizardEditor((CommentItem) item, handle);
				
				WizardDialog dial = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
				dial.setTitle("Comment item");
				dial.setMinimumPageSize(700, 300);
				dial.create();

				return dial.open();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Window.CANCEL;
	}

}
