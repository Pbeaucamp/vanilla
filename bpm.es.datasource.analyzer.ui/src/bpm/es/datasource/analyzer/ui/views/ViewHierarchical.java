package bpm.es.datasource.analyzer.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import bpm.es.datasource.analyzer.ui.Activator;
import bpm.es.datasource.analyzer.ui.Messages;
import bpm.es.datasource.analyzer.ui.gef.model.Diagram;
import bpm.es.datasource.analyzer.ui.gef.model.Link;
import bpm.es.datasource.analyzer.ui.icons.IconsNames;
import bpm.es.datasource.analyzer.ui.views.layouts.TreeLayout;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;

public class ViewHierarchical extends ViewPart implements IZoomableWorkbenchPart {

	public static final String ID = "bpm.es.datasource.analyzer.ui.views.ViewHierarchical"; //$NON-NLS-1$
	private GraphViewer viewer;
	private Diagram graph;
	private Action saveGraph;

	@Override
	public void createPartControl(Composite parent) {

		Composite p = new Composite(parent, SWT.NONE);
		p.setLayout(new GridLayout());
		p.setLayoutData(new GridData(GridData.FILL_BOTH));
		createZestViewer(p);
		createActions();
		fillToolBar();
		getSite().getPage().addPartListener(new IPartListener() {

			public void partOpened(IWorkbenchPart part) { }

			public void partDeactivated(IWorkbenchPart part) { }

			public void partClosed(IWorkbenchPart part) { }

			public void partBroughtToTop(IWorkbenchPart part) { }

			public void partActivated(IWorkbenchPart part) {
				if (part == ViewHierarchical.this) {
					getSite().setSelectionProvider(viewer);
				}
			}
		});
	}

	private void createActions() {
		saveGraph = new Action(Messages.ViewHierarchical_1) {
			public void run() {
				FileDialog fd = new FileDialog(getSite().getShell(), SWT.SAVE);
				fd.setFilterExtensions(new String[] { "*.bmp", "*.tif", "*.png", "*.gif", "*.jpg" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				String filePath = fd.open();

				if (fd == null) {
					return;
				}

				IFigure figure = viewer.getGraphControl().getContents();

				Image img1 = new Image(null, new org.eclipse.swt.graphics.Rectangle(0, 0, figure.getBounds().width, figure.getBounds().height + 1));
				GC gc1 = new GC(img1);

				Graphics grap1 = new SWTGraphics(gc1);
				figure.paint(grap1);

				ImageLoader loader1 = new ImageLoader();
				loader1.data = new ImageData[] { img1.getImageData() };

				if (filePath.endsWith("bmp")) { //$NON-NLS-1$
					if (!filePath.endsWith(".bmp")) { //$NON-NLS-1$
						filePath += ".bmp"; //$NON-NLS-1$
					}
					loader1.save(filePath, SWT.IMAGE_BMP);
				}
				else if (filePath.endsWith("tif")) { //$NON-NLS-1$
					if (!filePath.endsWith(".tif")) { //$NON-NLS-1$
						filePath += ".tif"; //$NON-NLS-1$
					}
					loader1.save(filePath, SWT.IMAGE_TIFF);
				}
				else if (filePath.endsWith("png")) { //$NON-NLS-1$
					if (!filePath.endsWith(".png")) { //$NON-NLS-1$
						filePath += ".png"; //$NON-NLS-1$
					}
					loader1.save(filePath, SWT.IMAGE_PNG);
				}
				else if (filePath.endsWith("gif")) { //$NON-NLS-1$
					if (!filePath.endsWith(".gif")) { //$NON-NLS-1$
						filePath += ".gif"; //$NON-NLS-1$
					}
					loader1.save(filePath, SWT.IMAGE_GIF);
				}
				else if (filePath.endsWith("jpg")) { //$NON-NLS-1$
					if (!filePath.endsWith(".jpg")) { //$NON-NLS-1$
						filePath += ".jpg"; //$NON-NLS-1$
					}
					loader1.save(filePath, SWT.IMAGE_JPEG);
				}
				else {
					img1.dispose();
					gc1.dispose();
					return;
				}

				img1.dispose();
				gc1.dispose();

				MessageDialog.openInformation(getSite().getShell(), Messages.ViewHierarchical_0, Messages.ViewHierarchical_2 + filePath + Messages.ViewHierarchical_3);

			}
		};
		saveGraph.setId(ID + ".savePicture"); //$NON-NLS-1$
		saveGraph.setToolTipText(Messages.ViewHierarchical_26);
		saveGraph.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.PICTURE));
	}

	private void createZestViewer(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.ViewHierarchical_27);

		final Combo cbo = new Combo(c, SWT.READ_ONLY);
		cbo.setLayoutData(new GridData());
		cbo.setItems(new String[] { "TreeLayout", "HorizontalTreeLayout", //$NON-NLS-1$ //$NON-NLS-2$
				"RadialLayout", "GridLayout", "SpringLayout", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"HorizontalShift" }); //$NON-NLS-1$

		cbo.select(0);
		cbo.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				switch (cbo.getSelectionIndex()) {
					case 0:
						viewer.setLayoutAlgorithm(new TreeLayout(), true);
						break;
					case 1:
						viewer.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(), true);
						break;
					case 2:
						viewer.setLayoutAlgorithm(new RadialLayoutAlgorithm(), true);
						break;
					case 3:
						viewer.setLayoutAlgorithm(new GridLayoutAlgorithm(), true);
						break;
					case 4:
						viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);
						break;
					case 5:
						viewer.setLayoutAlgorithm(new HorizontalShift(0), true);
						break;
					case 6:
						// viewer.setLayoutAlgorithm(new CoolLayout(SWT.NONE));
						// viewer.setLayoutAlgorithm(new
						// CompositeLayoutAlgorithm(), true);
						break;

				}
				viewer.applyLayout();
			}
		});

		viewer = new GraphViewer(c, SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setContentProvider(new IGraphEntityContentProvider() {

			public Object[] getConnectedTo(Object entity) {
				return graph.getConnectedTo(entity);
			}

			public Object[] getElements(Object inputElement) {
				List<Object> l = new ArrayList<Object>();
				l.add(((Diagram) inputElement).getDataSource());
				l.addAll(((Diagram) inputElement).getItems());
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		});
		viewer.setLabelProvider(new RepositoryLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DataSource) {
					return ((DataSource) element).getName();
				}
				else if (element instanceof RepositoryItem) {
					return super.getText(element);
				}
				return ""; //$NON-NLS-1$

			}
		});

		viewer.setLayoutAlgorithm(new TreeLayoutAlgorithm(SWT.VERTICAL));
		viewer.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
	}

	@Override
	public void setFocus() { }

	private void fillToolBar() {
		ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(this);

		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(saveGraph);
		bars.getMenuManager().add(toolbarZoomContributionViewItem);

	}

	public void setInput(DataSource dataSource, Collection<RepositoryItem> items, boolean showDependantItems) {
		graph = new Diagram();
		graph.setDataSource(dataSource);

		for (RepositoryItem it : items) {
			graph.addPrimaryDirectoryItem(it);
		}

		if (showDependantItems) {
			for (RepositoryItem it : items) {

				try {
					for (RepositoryItem i : adminbirep.Activator.getDefault().getRepositoryApi().getRepositoryService().getDependantItems(it)) {
						graph.addLink(new Link(i, it));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}	
		viewer.setInput(graph);
	}

	public AbstractZoomableViewer getZoomableViewer() {
		return viewer;
	}

}
