package bpm.fd.design.ui.editors;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.ui.IEditorPart;

public interface IEditDomainProvider extends IEditorPart{
	public EditDomain getDomain();
	public void setPaletteViewer(PaletteViewer viewer);
	public ZoomManager getZoomManager();
}
