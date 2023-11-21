package bpm.fd.design.ui.datas;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.IStatuable;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.api.internal.ILabelable;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.editor.palette.DialogPalette;
import bpm.fd.design.ui.gef.figures.PictureHelper;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.internal.FdComponentType;
import bpm.fd.design.ui.markers.IMarkerConstants;
import bpm.fd.design.ui.project.views.actions.CreateDataSetAction;
import bpm.fd.design.ui.project.views.actions.CreateDataSourceAction;
import bpm.fd.design.ui.project.views.actions.DataSourceMigrationAction;
import bpm.fd.design.ui.project.views.actions.DeleteAction;
import bpm.fd.design.ui.project.views.actions.EditDataSetAction;
import bpm.fd.design.ui.project.views.actions.FmdtResourceConversionAction;

public class ViewDictionary extends ViewPart {

	public static final String ID = "bpm.fd.design.ui.datas.ViewDictionary"; //$NON-NLS-1$

	private static final String NODE_DATASOURCES = Messages.ViewDictionary_1;
	private static final String NODE_DATASETS = Messages.ViewDictionary_2;
	private static final String NODE_COMPONENTS = Messages.ViewDictionary_3;
	private static final String NODE_PAlETTES = "Colors Palettes"; //$NON-NLS-1$

	private static final List<FdComponentType> NODES_COMPONENT_TYPES = FdComponentType.getComponentsTypes();

	private static final String BASE_PART_NAME = Messages.ViewDictionary_4;
	private static final String createDataSourceActionId = "bpm.fd.design.ui.project.views.ProjectView.actions.createDataSource"; //$NON-NLS-1$
	private static final String createDataSetActionId = "bpm.fd.design.ui.project.views.ProjectView.actions.createDataSet"; //$NON-NLS-1$
	private static final String editDatasActionId = "bpm.fd.design.ui.project.views.ProjectView.actions.editDatas"; //$NON-NLS-1$
	private static final String deleteActionId = "bpm.fd.design.ui.project.views.ProjectView.actions.delete"; //$NON-NLS-1$
	private static final String fmdtResourceConversionACtionId = "bpm.fd.design.ui.project.views.ProjectView.actions.createFromFmdt"; //$NON-NLS-1$

	private TreeViewer viewer;
	private MenuManager menuManager;

	private Action edit;
	private FmdtResourceConversionAction fmdtResourceAction;

	private PropertyChangeListener listener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent evt) {
			TreePath[] tp = viewer.getExpandedTreePaths();
			viewer.refresh();
			viewer.setExpandedTreePaths(tp);
			updateMarkers();
		}

	};

	private Action createDatasourceAction;

	private Action createDatasetAction;

	private Action color;

	private Action deleteAction;

	public ViewDictionary() {

	}

	private void updateMarkers() {
		try {
			ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(IMarkerConstants.ID, true, IResource.DEPTH_ONE);

		} catch(CoreException e) {
			e.printStackTrace();
		}

		Dictionary model = (Dictionary) viewer.getInput();

		if(model == null) {
			return;
		}

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for(DataSet ds : model.getDatasets()) {
			if(ds.getStatus() != IStatuable.OK) {
				for(Exception e : ds.getProblems()) {
					try {
						IMarker mk = root.createMarker(IMarkerConstants.ID);
						mk.setAttribute(IMarkerConstants.DICTIONARY_NAME, model.getName());
						mk.setAttribute(IMarkerConstants.DATASET_NAME, ds.getName());
						mk.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
						mk.setAttribute(IMarker.MESSAGE, e.getMessage());

					} catch(Exception ex) {
						ex.printStackTrace();
					}

				}

			}
		}

		for(IComponentDefinition def : model.getComponents()) {
			if(def instanceof IStatuable) {
				if(((IStatuable) def).getStatus() != IStatuable.OK) {
					for(Exception e : ((IStatuable) def).getProblems()) {
						try {
							IMarker mk = root.createMarker(IMarkerConstants.ID);
							mk.setAttribute(IMarkerConstants.DICTIONARY_NAME, model.getName());
							mk.setAttribute(IMarkerConstants.COMPONENT_NAME, def.getName());
							mk.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
							mk.setAttribute(IMarker.MESSAGE, e.getMessage());
							mk.setAttribute(IMarker.LOCATION, Activator.getDefault().getResourceProject().getFile(Activator.getDefault().getProject().getProjectDescriptor().getDictionaryName()));
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTreeViewer(main);

		createMenu();

	}

	private void createTreeViewer(Composite parent) {
		viewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
				List<Object> l = new ArrayList<Object>();
				if(viewer.getInput() != null) {
					if(parentElement == NODE_DATASOURCES) {
						l.addAll(((Dictionary) viewer.getInput()).getDatasources());
					}
					else if(parentElement == NODE_DATASETS) {
						l.addAll(((Dictionary) viewer.getInput()).getDatasets());
					}
					else if(parentElement == NODE_COMPONENTS) {
						l.addAll(NODES_COMPONENT_TYPES);
					}
					else if(parentElement == NODE_PAlETTES) {
						l.addAll(((Dictionary) viewer.getInput()).getPalettes());
					}
					else if(parentElement instanceof FdComponentType) {
						l.addAll(((Dictionary) viewer.getInput()).getComponents(((FdComponentType) parentElement).getComponentClass()));
					}
					else if(parentElement instanceof DataSet) {
						l.addAll(((DataSet) parentElement).getDataSetDescriptor().getColumnsDescriptors());
						l.addAll(((DataSet) parentElement).getDataSetDescriptor().getParametersDescriptors());
					}
					else if(parentElement instanceof IComponentDefinition) {
						l.addAll(((IComponentDefinition) parentElement).getParameters());
					}
				}

				return l.toArray(new Object[l.size()]);
			}

			public Object getParent(Object element) {
				if(element instanceof DataSource) {
					return NODE_DATASOURCES;
				}
				else if(element instanceof IComponentDefinition) {

					for(FdComponentType t : NODES_COMPONENT_TYPES) {
						if(t.getComponentClass() == element.getClass()) {
							return t;
						}
					}
				}
				else if(element instanceof DataSet) {
					return NODE_DATASETS;
				}
				else if(element instanceof ColumnDescriptor) {
					return ((ColumnDescriptor) element).getDataSet();
				}
				else if(element instanceof ParameterDescriptor) {
					return ((ParameterDescriptor) element).getDataSet();
				}

				return null;
			}

			public boolean hasChildren(Object element) {
				if(element == null) {
					return false;
				}
				if(element instanceof Dictionary) {
					return true;
				}
				else if(element == NODE_DATASOURCES) {
					return !((Dictionary) viewer.getInput()).getDatasources().isEmpty();
				}
				else if(element == NODE_DATASETS) {
					return !((Dictionary) viewer.getInput()).getDatasets().isEmpty();
				}
				else if(element == NODE_PAlETTES) {
					return !((Dictionary) viewer.getInput()).getPalettes().isEmpty();
				}
				else if(element == NODE_COMPONENTS) {
					return true;
				}
				else if(element instanceof FdComponentType) {
					return !((Dictionary) viewer.getInput()).getComponents(((FdComponentType) element).getComponentClass()).isEmpty();
				}
				else if(element instanceof DataSet) {
					return ((DataSet) element).getDataSetDescriptor() != null;
				}
				else if(element instanceof IComponentDefinition) {
					return !((IComponentDefinition) element).getParameters().isEmpty();
				}
				return false;
			}

			public Object[] getElements(Object inputElement) {
				Object[] o = new Object[] { NODE_DATASOURCES, NODE_DATASETS, NODE_COMPONENTS, NODE_PAlETTES };
				return o;
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});

		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();

		viewer.setLabelProvider(new DecoratingLabelProvider(new LabelProvider() {

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java .lang.Object)
			 */
			@Override
			public Image getImage(Object element) {
				if(element == NODE_DATASOURCES) {
					return Activator.getDefault().getImageRegistry().get(Icons.serverDataBase);
				}
				if(element == NODE_DATASOURCES) {
					return Activator.getDefault().getImageRegistry().get(Icons.dataSource);
				}
				if(element == NODE_COMPONENTS) {
					return Activator.getDefault().getImageRegistry().get(Icons.fd_16);
				}
				if(element == NODE_DATASETS) {
					return Activator.getDefault().getImageRegistry().get(Icons.table);
				}
				if(element == NODE_PAlETTES) {
					return Activator.getDefault().getImageRegistry().get(Icons.PALETTES);
				}

				if(element instanceof FdComponentType) {
					if(((FdComponentType) element).getImage() != null) {
						return ((FdComponentType) element).getImage().createImage();
					}

				}
				return PictureHelper.getIcons(element);
			}

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java. lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if(element instanceof Palette) {
					return ((Palette) element).getName();
				}
				if(element instanceof DataSource) {
					return ((DataSource) element).getName();
				}
				else if(element instanceof DataSet) {
					return ((DataSet) element).getName();
				}
				else if(element instanceof IComponentDefinition) {
					return ((IComponentDefinition) element).getName();
				}
				else if(element instanceof ComponentParameter) {
					return ((ComponentParameter) element).getName();
				}
				else if(element instanceof ParameterDescriptor) {
					return ((ParameterDescriptor) element).getLabel();
				}
				else if(element instanceof ILabelable) {
					return ((ILabelable) element).getLabel();
				}
				return super.getText(element);
			}

		}, decorator));

		getSite().setSelectionProvider(viewer);
	}

	private void createMenu() {
		menuManager = new MenuManager("root", "ViewDictionary.contextMenu"); //$NON-NLS-1$ //$NON-NLS-2$
		
		createDatasourceAction = new CreateDataSourceAction(Messages.ViewDictionary_11, createDataSourceActionId);
		createDatasetAction = new CreateDataSetAction(Messages.ViewDictionary_12, createDataSetActionId);
		
		menuManager.add(createDatasourceAction);
		menuManager.add(createDatasetAction);

		menuManager.add(edit = new EditDataSetAction(Messages.ViewDictionary_13, editDatasActionId, viewer));

		color = new Action(Messages.ViewDictionary_5) {
			public void run() {

				Palette palette = new Palette();

				palette.setName("newColorsPalette"); //$NON-NLS-1$
				((Dictionary) viewer.getInput()).addPalette(palette);

				DialogPalette d = new DialogPalette(getSite().getShell(), palette);
				d.open();
				viewer.refresh();
			}
		};
		menuManager.add(new Separator());
		menuManager.add(color);

		menuManager.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				Object o = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				enableDisableButtons(o);
			}

		});
		deleteAction = new DeleteAction(Messages.ViewDictionary_14, deleteActionId, viewer);
		
		menuManager.add(deleteAction);
		menuManager.add(new Separator());
		menuManager.add(fmdtResourceAction = new FmdtResourceConversionAction(viewer, Messages.ViewDictionary_7, fmdtResourceConversionACtionId));

		Menu m = menuManager.createContextMenu(viewer.getControl());

		viewer.getTree().setMenu(m);
		getSite().registerContextMenu("bpm.fd.design.ui.datas.ViewDictionary.menu", menuManager, viewer); //$NON-NLS-1$
		menuManager.add(new Separator());
		menuManager.add(new DataSourceMigrationAction(Messages.ViewDictionary_16, "migrate", viewer)); //$NON-NLS-2$ //$NON-NLS-1$
	}

	private void enableDisableButtons(Object o) {
		if(!viewer.getSelection().isEmpty() && ((o instanceof DataSet) || (o instanceof DataSource) || (o instanceof Palette))) {
			edit.setEnabled(true);
		}
		else {
			edit.setEnabled(false);
		}

		fmdtResourceAction.setEnabled((o instanceof DataSource) && "bpm.metadata.birt.oda.runtime".equals(((DataSource) o).getOdaExtensionDataSourceId())); //$NON-NLS-1$

		createDatasourceAction.setEnabled(o == NODE_DATASOURCES);
		createDatasetAction.setEnabled(o == NODE_DATASETS);
		color.setEnabled(o == NODE_PAlETTES);
		deleteAction.setEnabled(o != NODE_DATASOURCES && o != NODE_DATASETS && o != NODE_PAlETTES && o != NODE_COMPONENTS);		
	}

	@Override
	public void setFocus() {

	}

	public void setContent(FdProject project) {
		if(viewer.getInput() != null) {
			((Dictionary) viewer.getInput()).removePropertyChangeListener(listener);
		}
		if(project != null) {
			project.getDictionary().addPropertyChangeListener(listener);
			if(viewer.getInput() != project.getDictionary()) {
				viewer.setInput(project.getDictionary());

				this.setPartName(BASE_PART_NAME + " " + project.getProjectDescriptor().getDictionaryName()); //$NON-NLS-1$
			}

		}
		else {
			viewer.setInput(null);
			this.setPartName(BASE_PART_NAME);
		}
		updateMarkers();
	}

}
