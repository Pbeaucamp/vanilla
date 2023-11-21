package bpm.workflow.ui.views;

import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import bpm.workflow.ui.Messages;
import bpm.workflow.ui.editors.WorkflowMultiEditorPart;
import bpm.workflow.ui.editors.WorkflowPaletteRoot;

public class ViewPalette extends ViewPart {
	public static final int TOOL_ENTRY_SELECT = 1;
	public static final int TOOL_ENTRY_LINK = 2;

	private ToolEntry selectionToolEntry, linkToolEntry;

	public static final String ID = "bpm.workflow.ui.views.ViewPalette"; //$NON-NLS-1$
	private PaletteViewer viewer;

	public ViewPalette() {}

	@Override
	public void createPartControl(Composite parent) {

		Composite f = new Composite(parent, SWT.NONE);

		f.setLayoutData(new GridData(GridData.FILL_BOTH));
		f.setLayout(new GridLayout());

		CLabel l = new CLabel(f, SWT.NONE);
		l.setText(Messages.ViewPalette_1);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setBackground(new Color[] { parent.getDisplay().getDefault().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT), parent.getDisplay().getDefault().getSystemColor(SWT.COLOR_BLUE), parent.getDisplay().getDefault().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT) }, new int[] { 90, 100 });

		viewer = new PaletteViewer();

		viewer.createControl(f);
		f.setBackground(viewer.getControl().getBackground());
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setPaletteRoot(getPaletteRoot());

		viewer.getControl().addMouseTrackListener(new MouseTrackListener() {

			public void mouseEnter(MouseEvent e) {
				if(viewer.getActiveTool() instanceof ConnectionCreationToolEntry) {
					viewer.setActiveTool(viewer.getPaletteRoot().getDefaultEntry());
				}

			}

			public void mouseExit(MouseEvent e) {}

			public void mouseHover(MouseEvent e) {}

		});
		getSite().getWorkbenchWindow().getActivePage().addPartListener(new IPartListener() {

			public void partActivated(IWorkbenchPart part) {
				if(part instanceof WorkflowMultiEditorPart) {
					if(viewer.getEditDomain() != ((WorkflowMultiEditorPart) part).getDomain()) {
						try {

							viewer.setActiveTool(null);
							viewer.setEditDomain(((WorkflowMultiEditorPart) part).getDomain());
							((WorkflowMultiEditorPart) part).setPaletteViewer(viewer);
							((WorkflowMultiEditorPart) part).getDomain().setPaletteRoot(viewer.getPaletteRoot());
							((WorkflowMultiEditorPart) part).getDomain().setPaletteViewer(viewer);

						} catch(Exception e) {
							e.printStackTrace();
						}
					}

				}

			}

			public void partBroughtToTop(IWorkbenchPart part) {
				if(part instanceof WorkflowMultiEditorPart) {
					if(viewer.getEditDomain() != ((WorkflowMultiEditorPart) part).getDomain()) {
						try {
							viewer.setEditDomain(((WorkflowMultiEditorPart) part).getDomain());
							((WorkflowMultiEditorPart) part).setPaletteViewer(viewer);
							((WorkflowMultiEditorPart) part).getDomain().setPaletteRoot(viewer.getPaletteRoot());
							((WorkflowMultiEditorPart) part).getDomain().setPaletteViewer(viewer);

						} catch(Exception e) {
							e.printStackTrace();
						}
					}

				}

			}

			public void partClosed(IWorkbenchPart part) {

			}

			public void partDeactivated(IWorkbenchPart part) {

			}

			public void partOpened(IWorkbenchPart part) {

			}

		});

	}

	@Override
	public void setFocus() {}

	public void activateToolEntry(int id) {
		switch(id) {
			case TOOL_ENTRY_LINK:
				viewer.setActiveTool(linkToolEntry);
				break;
			case TOOL_ENTRY_SELECT:
				viewer.setActiveTool(selectionToolEntry);
				break;
		}
	}

	protected PaletteRoot getPaletteRoot() {
		WorkflowPaletteRoot root = new WorkflowPaletteRoot();

		root.setDefaultEntry(selectionToolEntry);
		return root;
	}

	public PaletteViewer getPaletteViewer() {
		return viewer;
	}
}
