package bpm.birep.admin.client.preferences;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.repository.Comment;

public class TableViewerRowColor implements ITableColorProvider, ITableLabelProvider {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public TableViewerRowColor() {
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		int documentId = ((DocumentVersion) element).getId();

		try {
			IRepositoryApi repositoryApi = Activator.getDefault().getRepositoryApi();
			if (repositoryApi.getDocumentationService().canComment(-1, documentId, Comment.DOCUMENT_VERSION)) {
				return new Color(Display.getDefault(), 190, 67, 23);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return ((DocumentVersion) element).getParent().getName() + " - V" + ((DocumentVersion) element).getVersion(); //$NON-NLS-1$
		case 1:
			return ((DocumentVersion) element).getFormat();
		case 2:
			try {
				return sdf.format(((DocumentVersion) element).getModificationDate());
			} catch (Exception e) {
				return Messages.TableViewerRowColor_1;
			}
		case 3:
			return ((DocumentVersion) element).getParent().getCreatedBy() + ""; //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$				
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

}
