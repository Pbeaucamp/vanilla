package bpm.forms.design.ui.views.content;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;
import bpm.forms.design.ui.icons.IconsNames;
import bpm.forms.design.ui.views.content.pages.PageForm;
import bpm.forms.design.ui.views.content.pages.PageFormDefinition;



public class ContentMasterDetailsBlock extends MasterDetailsBlock {

	private static Color DIRTY_COLOR = new Color(Display.getDefault(), 200, 100, 100);
	
	private TreeViewer definedViewer;

	private List<Object> dirtyObjects = new ArrayList<Object>();
	private ToolbarBuilder builder;
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		
		Composite main = toolkit.createTable(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		/*
		 * filters
		 */
//		Section section = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE);
//		section.setText("Filters");
//		section.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
//
//		section.setClient(createFilterComposite(toolkit, section));
		
		
		/*
		 * Defined Forms
		 */
		Section section = toolkit.createSection(main, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		section.setText(Messages.ContentMasterDetailsBlock_0);
		section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		section.setClient(createDefinitionViewer(toolkit, section));
		
		
		
		
		
		toolkit.paintBordersFor(definedViewer.getTree());
		
		
		/*
		 * sections parts
		 */
		final SectionPart sectionPart = new SectionPart(section);
		managedForm.addPart(sectionPart);
		
		definedViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				
//				if (managedForm.isDirty()){
//					System.out.println("Dirty");
//					managedForm.dirtyStateChanged();
//				}
				managedForm.fireSelectionChanged(sectionPart, definedViewer.getSelection());
				managedForm.getForm().layout(true);
				
			}
		});
		
		
		if (builder != null){
			builder.runRefresh();
		}
	}

	public ISelectionProvider getSelectionProvider(){
		return definedViewer;
	}
	
	private Control createDefinitionViewer(FormToolkit toolkit, Section section){
		definedViewer = new TreeViewer(toolkit.createTree(section, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL));
		definedViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		definedViewer.getTree().setLinesVisible(true);
		definedViewer.getTree().setHeaderVisible(true);
		definedViewer.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			@Override
			public void dispose() {
				
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
			
			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof IForm){
					return ((IForm)element).getDefinitionsVersions().size() > 0;
				}else{
					return false;
				}
				
				
			}
			
			@Override
			public Object getParent(Object element) {
				
				return null;
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				List<Object> l = new ArrayList<Object>();
				if (parentElement instanceof IForm){
					l.addAll(((IForm)parentElement).getDefinitionsVersions());
				}
				return l.toArray(new Object[l.size()]);
			}
		});
		
		
		
		
		TreeViewerColumn col = new TreeViewerColumn(definedViewer, SWT.LEFT);
		col.getColumn().setText(Messages.ContentMasterDetailsBlock_1);
		col.getColumn().setWidth(100);
		
//		new DecoratingLabelProvider(new ColumnLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
		
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof IForm){
					return ((IForm)element).getName();
				}
				return ""; //$NON-NLS-1$
			}
			
			@Override
			public Color getBackground(Object element) {
				if (dirtyObjects.contains(element)){
					return DIRTY_COLOR;
				}
				return super.getBackground(element);
			}
			
		});
		
		col = new TreeViewerColumn(definedViewer, SWT.LEFT);
		col.getColumn().setText(Messages.ContentMasterDetailsBlock_3);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof IFormDefinition){
					return ((IFormDefinition)element).getVersion() + ""; //$NON-NLS-1$
				}
				return ""; //$NON-NLS-1$
			}
			@Override
			public Color getBackground(Object element) {
				if (dirtyObjects.contains(element)){
					return DIRTY_COLOR;
				}
				return super.getBackground(element);
			}
		});
		
		col = new TreeViewerColumn(definedViewer, SWT.LEFT);
		col.getColumn().setText(Messages.ContentMasterDetailsBlock_6);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof IForm){
					return ((IForm)element).getDescription();
				}
				else if (element instanceof IFormDefinition){
					return ((IFormDefinition)element).getDescription();
				}
				return ""; //$NON-NLS-1$
			}
			@Override
			public Color getBackground(Object element) {
				if (dirtyObjects.contains(element)){
					return DIRTY_COLOR;
				}
				return super.getBackground(element);
			}
		});
		
		col = new TreeViewerColumn(definedViewer, SWT.LEFT);
		col.getColumn().setText(Messages.ContentMasterDetailsBlock_8);
		col.getColumn().setWidth(50);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ""; //$NON-NLS-1$
			}
			@Override
			public Image getImage(Object element) {
				if (element instanceof IFormDefinition){
					if (((IFormDefinition)element).isDesigned()){
						return Activator.getDefault().getImageRegistry().get(IconsNames.locked);
					}
				}
				return null;
			}
		});
		
		col = new TreeViewerColumn(definedViewer, SWT.LEFT);
		col.getColumn().setText(Messages.ContentMasterDetailsBlock_10);
		col.getColumn().setWidth(50);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ""; //$NON-NLS-1$
			}
			@Override
			public Image getImage(Object element) {
				if (element instanceof IFormDefinition){
					if (((IFormDefinition)element).isActivated()){
						return Activator.getDefault().getImageRegistry().get(IconsNames.activated);
					}
					else{
						return Activator.getDefault().getImageRegistry().get(IconsNames.disabled);
					}
				}
				return null;
			}
		});

		
		
		col = new TreeViewerColumn(definedViewer, SWT.LEFT);
		col.getColumn().setText(Messages.ContentMasterDetailsBlock_12);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
			@Override
			public String getText(Object element) {
				if (element instanceof IFormDefinition){
					if (((IFormDefinition)element).getStartDate() != null){
						return sdf.format(((IFormDefinition)element).getStartDate());
					}
				}
				return ""; //$NON-NLS-1$
			}
			
		});
		col = new TreeViewerColumn(definedViewer, SWT.LEFT);
		col.getColumn().setText(Messages.ContentMasterDetailsBlock_15);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
			@Override
			public String getText(Object element) {
				if (element instanceof IFormDefinition){
					if (((IFormDefinition)element).getStopDate() != null){
						return sdf.format(((IFormDefinition)element).getStopDate());
					}
				}
				return ""; //$NON-NLS-1$
			}
			
		});
		return definedViewer.getControl();
	}
	
	private Control createFilterComposite(FormToolkit toolkit, Section section) {
		Composite c = toolkit.createComposite(section, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData());
		
		Label l = toolkit.createLabel(c, "dummy"); //$NON-NLS-1$
		l.setLayoutData(new GridData());
		
		return c;
	}
	
	protected List<Object> getDirtyObjects(){
		return dirtyObjects;
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		builder = new ToolbarBuilder(managedForm, definedViewer, this);
		
		managedForm.getForm().setHeadClient(builder.createToolbarManager().createControl(managedForm.getForm().getForm().getHead()));
		
		if (definedViewer != null){
			
				builder.runRefresh();
			
		}
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(IForm.class, new PageForm(this));
		for(Class c : Activator.getDefault().getFactoryModel().getFormImplementors()){
			detailsPart.registerPage(c, new PageForm(this));
		}
		
		detailsPart.registerPage(IFormDefinition.class, new PageFormDefinition(this));
		for(Class c : Activator.getDefault().getFactoryModel().getFormDefinitionImplementors()){
			detailsPart.registerPage(c, new PageFormDefinition(this));
		}
	}

	public void addDirtyObject(Object dirtyObject) {
		if (dirtyObject != null && ! dirtyObjects.contains(dirtyObject)){
			this.dirtyObjects.add(dirtyObject);
			ISelection s = definedViewer.getSelection();
			definedViewer.refresh();
			definedViewer.setSelection(s);
		}
	}
	
	public void removeDirtyObject(Object dirtyObject) {
		this.dirtyObjects.remove(dirtyObject);
		ISelection s = definedViewer.getSelection();
		definedViewer.refresh();
		definedViewer.setSelection(s);
		if (builder != null){
			builder.runRefresh();
		}
	}

}
