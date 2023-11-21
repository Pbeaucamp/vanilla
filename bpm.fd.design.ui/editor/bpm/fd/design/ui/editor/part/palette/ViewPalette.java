package bpm.fd.design.ui.editor.part.palette;

import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.fd.design.ui.editors.FdProjectEditorInput;
import bpm.fd.design.ui.editors.IEditDomainProvider;

public class ViewPalette extends ViewPart {
	public static final int TOOL_ENTRY_SELECT = 1;
	public static final int TOOL_ENTRY_LINK = 2;

	public static final String ID = "bpm.fd.design.ui.editor.part.palette.ViewPalette"; //$NON-NLS-1$
	private PaletteViewer viewer;

	public ViewPalette() {}

	public void activateForInput(IEditDomainProvider part, FdProjectEditorInput input) {
		if(part == null || viewer == null || viewer.getControl() == null || viewer.getControl().isDisposed()) {
			return;
		}
		if(viewer.getPaletteRoot() != null) {
			if(((PaletteBuilder) viewer.getPaletteRoot()).getDictionary() == input.getModel().getProject().getDictionary()) {
				viewer.setEditDomain(part.getDomain());
				part.setPaletteViewer(viewer);
				part.getDomain().setPaletteRoot(viewer.getPaletteRoot());
				part.getDomain().setPaletteViewer(viewer);
				((PaletteBuilder) viewer.getPaletteRoot()).refresh(input.getModel());
				viewer.getContents().refresh();
				return;
			}
		}
		try {
			viewer.setPaletteRoot(new PaletteBuilder(viewer, input.getModel().getProject().getDictionary(), input.getModel()));
			viewer.setEditDomain(part.getDomain());
			part.setPaletteViewer(viewer);
			part.getDomain().setPaletteRoot(viewer.getPaletteRoot());
			part.getDomain().setPaletteViewer(viewer);
			((PaletteBuilder) viewer.getPaletteRoot()).refresh(input.getModel());
			viewer.getContents().refresh();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		Composite f = new Composite(parent, SWT.NONE);
		f.setLayoutData(new GridData(GridData.FILL_BOTH));
		f.setLayout(new GridLayout(2, true));

		viewer = new PaletteViewer();

		viewer.createControl(f);
		f.setBackground(viewer.getControl().getBackground());
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		try {
			IEditDomainProvider part = ((FdEditor) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()).getStructureEditor();

			if(part != null) {
				FdProjectEditorInput input = (FdProjectEditorInput) part.getEditorInput();
				activateForInput(part, input);
			}
		} catch(Exception e) {
		}

	}

	@Override
	public void setFocus() {}
}
