package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fa.api.olap.Element;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.fa.api.repository.FaApiHelper;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericNonPieOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.PieGenericOptions;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.olap.FaViewOption;
import bpm.fd.api.core.model.components.definition.report.ReportOptions;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
//import bpm.fd.design.ui.properties.model.editors.ComponentEditor.EventDialogCellEditor;
//import bpm.fd.design.ui.properties.model.editors.VanillaObjectEditor.ItemDialogEditor;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.united.olap.remote.services.RemoteServiceProvider;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class OlapViewEditor extends VanillaObjectEditor{

	private class DimensionsDialogCellEditor extends DialogCellEditor{
		private List<Element> elements=null;
		 protected DimensionsDialogCellEditor(Composite parent) {
		        super(parent);
		        this.elements=elements;
		  }
		@Override
		protected Object openDialogBox(Control cellEditorWindow) {
			
			DialogDimensionsSelection d = new DialogDimensionsSelection(cellEditorWindow.getShell(),(List<Element>)getValue(), getCube());
			
			if (d.open() == DialogDimensionsSelection.OK){
				return d.getContent();
			}
			return null;
		}
		
	}
	
	
	public OlapViewEditor(Composite parent) {
		super(parent);
		
	}

	private  ReportOptions getOptions(){
		return (ReportOptions)((ComponentFaView)getComponentDefinition()).getOptions(ReportOptions.class);
	}
	@Override
	protected int getHeight() {
		
		return getOptions().getHeight();
	}

	@Override
	protected int getWidth() {
		return getOptions().getWidth();
	}

	@Override
	protected void setHeight(Integer height) {
		getOptions().setHeight(height);
		
	}

	@Override
	protected void setItemId(Integer id) {
		((ComponentFaView)getComponentDefinition()).setDirectoryItemId(id);
		
	}

	@Override
	protected void setWidth(Integer width) {
		getOptions().setWidth(width);
		
	}
	
	@Override
	protected void fillBar() {
		// TODO Auto-generated method stub
		super.fillBar();
		createInteractiveContent(getControl());
		
	}
	
	public OLAPCube getCube(){
		OLAPCube cube=null;
		try{
			int directoryItemId =this.getItemRef().getDirectoryItemId();
			IRepositoryContext ctx=  bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection().getContext();
			int repositoryId=  ctx.getRepository().getId();
			

			IRepositoryApi sock = new RemoteRepositoryApi(ctx);
			
			RepositoryItem repositoryItem = null;
						
			repositoryItem = sock.getRepositoryService().getDirectoryItem(directoryItemId);
				
			String itemXML = null;

			itemXML = sock.getRepositoryService().loadModel(repositoryItem);
			
			int start = itemXML.indexOf("<cubename>") + 10;
			int end = itemXML.indexOf("</cubename>");
			String cubeName = itemXML.substring(start, end);
			start = itemXML.indexOf("<fasdid>") + 8;
			end = itemXML.indexOf("</fasdid>");
			String fasdItemId =  itemXML.substring(start, end);	
			
			ObjectIdentifier ident = new ObjectIdentifier(repositoryId, Integer.parseInt(fasdItemId));
			
			IRuntimeContext runtimectx = new RuntimeContext(ctx.getVanillaContext().getLogin(), ctx.getVanillaContext().getPassword(), ctx.getGroup().getName(), ctx.getGroup().getId());
			FaApiHelper apiHelper = new FaApiHelper( ctx.getVanillaContext().getVanillaUrl(), UnitedOlapLoaderFactory.getLoader());

			RemoteServiceProvider remote = new RemoteServiceProvider();
			
			remote.configure(ctx.getVanillaContext());
			
			UnitedOlapServiceProvider.getInstance().init(remote.getRuntimeProvider(), remote.getModelProvider());
			
			
			 cube = apiHelper.getCube(ident, runtimectx, cubeName);		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cube;
	}
	
	
	private ExpandItem item ;
	
	private void createInteractiveContent(ExpandBar parent) {	
		
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);

		item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.VanillaObjectEditor_11);
		
		final Property interactive = new Property(Messages.VanillaObjectEditor_7, new CheckboxCellEditor(viewer.getTree()));
		final PropertyGroup groupInteract = new PropertyGroup(Messages.VanillaObjectEditor_8);
		final Property dimensions = new Property(Messages.VanillaObjectEditor_9, new CheckboxCellEditor(viewer.getTree()));

		
		final Property dimensionFilter = new Property(Messages.VanillaObjectEditor_12, new DimensionsDialogCellEditor(viewer.getTree())); //EventDialogCellEditor editor = new EventDialogCellEditor(eventViewer.getTree());
		
		final Property cubeFunctions = new Property(Messages.VanillaObjectEditor_10, new CheckboxCellEditor(viewer.getTree()));
		groupInteract.add(dimensions);
		groupInteract.add(dimensionFilter);
		groupInteract.add(cubeFunctions);
		
		
		
		colValue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				FaViewOption faOpts = getFaViewOption();
				if (faOpts == null){return "";} //$NON-NLS-1$
				if (element == interactive){return faOpts.isInteractive() + "";} //$NON-NLS-1$
				if (element == dimensions){return faOpts.isShowDimensions() + "";} //$NON-NLS-1$
				if (element == cubeFunctions){return faOpts.isShowCubeFunctions() + "";} //$NON-NLS-1$
				if (element == dimensionFilter){return faOpts.getStringElements();}
				return ""; //$NON-NLS-1$
			}
			
		});
		
		colValue.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				FaViewOption faOpts = getFaViewOption();
				if (faOpts == null){return;} 
				if (element == interactive)
				{
					if (! (boolean)value){
						faOpts.setShowDimensions(false);
						faOpts.setShowCubeFunctions(false);
						faOpts.setElements(null);
						
						//dimensions.getCellEditor().setValidator(validator);
						//dimensions.getCellEditor().getControl().setEnabled(false);
						//cubeFunctions.getCellEditor().deactivate();
					}
					else{
						dimensions.getCellEditor().activate();
						cubeFunctions.getCellEditor().activate();
					}
					faOpts.setInteraction((boolean)value);
				}
				if (element == dimensions)
				{
					if (faOpts.isInteractive())
						faOpts.setShowDimensions((boolean)value);
					if (!(boolean)value)
						dimensionFilter.getCellEditor().getControl().setEnabled(false);
					else 
						dimensionFilter.getCellEditor().getControl().setEnabled(true);
					//dimensionFilter.getCellEditor().
				}
				if (element == cubeFunctions)
				{
					if (faOpts.isInteractive())
						faOpts.setShowCubeFunctions((boolean)value);
				}
				if (element == dimensionFilter)
				{
					if (faOpts.isInteractive())
						faOpts.setElements((List<bpm.fa.api.olap.Element>)value);
				}
				
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				FaViewOption faOpts = getFaViewOption();
				if (faOpts == null){return "";} //$NON-NLS-1$
				if (element == interactive){return faOpts.isInteractive();} //$NON-NLS-1$
				if (element == dimensions){return faOpts.isShowDimensions();} //$NON-NLS-1$
				if (element == cubeFunctions){return faOpts.isShowCubeFunctions();} //$NON-NLS-1$
				if (element == dimensionFilter){return faOpts.getElements();} //$NON-NLS-1$
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return ((Property)element).getCellEditor() != null;
			}
		});
		
		List input = new ArrayList();
		input.add(interactive);
		input.add(groupInteract);
		
		viewer.setInput(input);
	}
	
	
	private FaViewOption getFaViewOption(){
		IComponentDefinition def =getComponentDefinition();
		if (def == null){
			return null;
		}
		FaViewOption opts = (FaViewOption)def.getOptions(FaViewOption.class);
		
		return opts;
	}
	

}
