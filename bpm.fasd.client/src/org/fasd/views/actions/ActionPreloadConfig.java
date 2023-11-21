package org.fasd.views.actions;

import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.fasd.cubewizard.dimension.preload.DialogPreload;
import org.fasd.i18N.LanguageText;
import org.fasd.views.DimensionView;

public class ActionPreloadConfig extends Action {

	private DimensionView view;

	public ActionPreloadConfig(DimensionView view, UndoContext undoCtx) {
		super(LanguageText.ActionPreloadConfig_0);
		this.view = view;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/preload.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	@Override
	public void run() {
		DialogPreload dial = new DialogPreload(view.getSite().getShell());
		if (dial.open() == Dialog.OK) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionPreloadConfig_1, LanguageText.ActionPreloadConfig_2);
		}
	}

}
