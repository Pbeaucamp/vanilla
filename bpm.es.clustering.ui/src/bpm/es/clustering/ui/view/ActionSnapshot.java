package bpm.es.clustering.ui.view;

import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;

import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.composites.VisualMappingComposite;

public class ActionSnapshot extends Action {
	private Shell shell;
	private VisualMappingComposite graphComp;

	public ActionSnapshot(Shell shell, VisualMappingComposite graphComp) {
		this.shell = shell;
		this.graphComp = graphComp;
	}

	public void run() {
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setFilterExtensions(new String[] { "*.bmp", "*.tif", "*.png", "*.gif", "*.jpg" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		String filePath = fd.open();

		if (fd == null) {
			return;
		}
		
		Graph graph = graphComp.getViewer();

		Rectangle bounds = graph.getContents().getBounds();
		Point size = new Point(graph.getContents().getSize().width, graph.getContents().getSize().height);
		org.eclipse.draw2d.geometry.Point viewLocation = graph.getViewport().getViewLocation();
		final Image image = new Image(null, size.x, size.y);
		GC gc = new GC(image);
		SWTGraphics swtGraphics = new SWTGraphics(gc);

		swtGraphics.translate(-1 * bounds.x + viewLocation.x, -1 * bounds.y + viewLocation.y);
		graph.getViewport().paint(swtGraphics);
		gc.copyArea(image, 0, 0);
		gc.dispose();

		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };

		if (filePath.endsWith("bmp")) { //$NON-NLS-1$
			if (!filePath.endsWith(".bmp")) { //$NON-NLS-1$
				filePath += ".bmp"; //$NON-NLS-1$
			}
			loader.save(filePath, SWT.IMAGE_BMP);
		}
		else if (filePath.endsWith("tif")) { //$NON-NLS-1$
			if (!filePath.endsWith(".tif")) { //$NON-NLS-1$
				filePath += ".tif"; //$NON-NLS-1$
			}
			loader.save(filePath, SWT.IMAGE_TIFF);
		}
		else if (filePath.endsWith("png")) { //$NON-NLS-1$
			if (!filePath.endsWith(".png")) { //$NON-NLS-1$
				filePath += ".png"; //$NON-NLS-1$
			}
			loader.save(filePath, SWT.IMAGE_PNG);
		}
		else if (filePath.endsWith("gif")) { //$NON-NLS-1$
			if (!filePath.endsWith(".gif")) { //$NON-NLS-1$
				filePath += ".gif"; //$NON-NLS-1$
			}
			loader.save(filePath, SWT.IMAGE_GIF);
		}
		else if (filePath.endsWith("jpg")) { //$NON-NLS-1$
			if (!filePath.endsWith(".jpg")) { //$NON-NLS-1$
				filePath += ".jpg"; //$NON-NLS-1$
			}
			loader.save(filePath, SWT.IMAGE_JPEG);
		}
		else {
			image.dispose();
			gc.dispose();
			return;
		}

		image.dispose();
		gc.dispose();

		MessageDialog.openInformation(shell, Messages.ActionSnapshot_20, Messages.ActionSnapshot_21 + filePath + Messages.ActionSnapshot_22);

	}

}
