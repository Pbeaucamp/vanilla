package bpm.gateway.ui.views.property.sections.transformations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.outputs.GeoJsonOutput;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogPickupConstant;

public class GeoJsonSection extends AbstractPropertySection {
	protected Node node;
	protected NodePart nodePart;
	
	private Text filePath;
	private Button variable, browse;
	
	private ComboViewer latViewer, longViewer, typeViewer;
	
	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			((GeoJsonOutput)node.getGatewayModel()).setFilePath(filePath.getText());

		}
	};
	private ISelectionChangedListener latListener = new ISelectionChangedListener() {	
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			String lat = (String) ((IStructuredSelection) event.getSelection()).getFirstElement();
			((GeoJsonOutput)node.getGatewayModel()).setLatitudeColumn(lat);
		}
	};
	
	private ISelectionChangedListener longListener = new ISelectionChangedListener() {	
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			String lat = (String) ((IStructuredSelection) event.getSelection()).getFirstElement();
			((GeoJsonOutput)node.getGatewayModel()).setLongitudeColumn(lat);
		}
	};
	
	private ISelectionChangedListener typeListener = new ISelectionChangedListener() {	
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			String lat = (String) ((IStructuredSelection) event.getSelection()).getFirstElement();
			((GeoJsonOutput)node.getGatewayModel()).setGeometryType(lat);
		}
	};
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(4, false));
		
		Label lblFile = getWidgetFactory().createLabel(composite, Messages.GeoJsonSection_0, SWT.NONE);
		lblFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		filePath = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		filePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filePath.addModifyListener(listener);

		variable = getWidgetFactory().createButton(composite, Messages.FileGeneralSection_2, SWT.PUSH);
		variable.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		variable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickupConstant d = new DialogPickupConstant(getPart().getSite().getShell());

				if (d.open() == Dialog.OK) {

					filePath.setText(d.getVariable().getOuputName());

					Event ev = new Event();
					ev.widget = filePath;
					listener.modifyText(new ModifyEvent(ev));
				}

			}
		});

		browse = getWidgetFactory().createButton(composite, Messages.FileGeneralSection_3, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getPart().getSite().getShell());

				String varName = null;
				Variable v = null;
				String filterPath = null;
				try {

					if (filePath.getText().startsWith("{$")) { //$NON-NLS-1$

						varName = filePath.getText().substring(0, filePath.getText().indexOf("}") + 1); //$NON-NLS-1$

						v = ResourceManager.getInstance().getVariableFromOutputName(varName);
						String h = v.getValueAsString();
						fd.setFilterPath(h.startsWith("/") && h.contains(":") ? h.substring(1) : h); //$NON-NLS-1$ //$NON-NLS-2$
						filterPath = fd.getFilterPath();
					}

				} catch (Exception e1) {

				}
				String path = fd.open();

				if (path != null) {

					if (varName != null) {

						path = v.getOuputName() + path.substring(filterPath.length());
					}
					path = path + ".geojson"; //$NON-NLS-1$

					filePath.setText(path);
					Event ev = new Event();
					ev.widget = filePath;
					listener.modifyText(new ModifyEvent(ev));
				}

			}

		});

		Label lblLatitude = getWidgetFactory().createLabel(composite, Messages.GeoJsonSection_2, SWT.NONE);
		lblLatitude.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
		
		latViewer = new ComboViewer(composite, SWT.DROP_DOWN | SWT.PUSH);
		latViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		latViewer.setLabelProvider(new LabelProvider());

		latViewer.setContentProvider(new ArrayContentProvider());
		latViewer.addSelectionChangedListener(latListener);
		
		Label lblLongitude = getWidgetFactory().createLabel(composite, Messages.GeoJsonSection_3, SWT.NONE);
		lblLongitude.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		longViewer = new ComboViewer(composite, SWT.DROP_DOWN | SWT.PUSH);
		longViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		longViewer.setLabelProvider(new LabelProvider());

		longViewer.setContentProvider(new ArrayContentProvider());
		longViewer.addSelectionChangedListener(longListener);
		
		Label lblType = getWidgetFactory().createLabel(composite, Messages.GeoJsonSection_4, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		typeViewer = new ComboViewer(composite, SWT.DROP_DOWN | SWT.PUSH);
		typeViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		typeViewer.setLabelProvider(new LabelProvider());

		typeViewer.setContentProvider(new ArrayContentProvider());
		typeViewer.addSelectionChangedListener(typeListener);
		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        this.nodePart = (NodePart) input;

	}
	
	@Override
	public void refresh() {
		
		try {
			List<String> inputFieldsNames = new ArrayList<String>();
			
			
			if (!((GeoJsonOutput)node.getGatewayModel()).getInputs().isEmpty()){
				
				try {
					for(StreamElement e : ((GeoJsonOutput)node.getGatewayModel()).getInputs().get(0).getDescriptor(((GeoJsonOutput)node.getGatewayModel())).getStreamElements()){
						inputFieldsNames.add(e.name);
					}
				} catch (ServerException e) {
					
					e.printStackTrace();
				}
				latViewer.setInput(inputFieldsNames.toArray(new String[inputFieldsNames.size()]));
				if(((GeoJsonOutput)node.getGatewayModel()).getLatitudeColumn() != null && !((GeoJsonOutput)node.getGatewayModel()).getLatitudeColumn().isEmpty()) {
					latViewer.setSelection(new StructuredSelection(((GeoJsonOutput)node.getGatewayModel()).getLatitudeColumn()));
				}
				longViewer.setInput(inputFieldsNames.toArray(new String[inputFieldsNames.size()]));
				if(((GeoJsonOutput)node.getGatewayModel()).getLongitudeColumn() != null && !((GeoJsonOutput)node.getGatewayModel()).getLongitudeColumn().isEmpty()) {
					longViewer.setSelection(new StructuredSelection(((GeoJsonOutput)node.getGatewayModel()).getLongitudeColumn()));
				}
			}
			
			typeViewer.setInput(new String[] {GeoJsonOutput.TYPE_POINT, GeoJsonOutput.TYPE_LINESTRING, GeoJsonOutput.TYPE_POLYGON, GeoJsonOutput.TYPE_MULTIPOINT});
			if(((GeoJsonOutput)node.getGatewayModel()).getGeometryType() != null && !((GeoJsonOutput)node.getGatewayModel()).getGeometryType().isEmpty()) {
				typeViewer.setSelection(new StructuredSelection(((GeoJsonOutput)node.getGatewayModel()).getGeometryType()));
			}
			
			filePath.setText(((GeoJsonOutput)node.getGatewayModel()).getFilePath());
		} catch(Exception e) {
		}
	}


	@Override
	public void aboutToBeShown() {
		
		super.aboutToBeShown();
	}


	@Override
	public void aboutToBeHidden() {
		
		super.aboutToBeHidden();
	}
}
