package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.EncryptTransformation;
import bpm.gateway.core.transformations.SelectionTransformation;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.composites.labelproviders.DefaultStreamLabelProvider;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class SelectionSection extends AbstractPropertySection {

	private static Color RED = new Color(Display.getCurrent(), 255, 0, 75);
	
	protected Node node;
	protected StreamComposite streamComposite;
	private PropertyChangeListener listenerConnection;
	
	private Label lblPublicKey;
	private Text txtPublicKey;
	private Button btnEncrypt, btnDecrypt;

	private boolean init = false;
	private boolean uncheckOnPurpose = false;
	
	public SelectionSection() {
		listenerConnection = new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP)){
					refresh();
				}
			}
		};
	}

	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());
		
		Composite compositePublicKey = getWidgetFactory().createFlatFormComposite(composite);
		compositePublicKey.setLayout(new GridLayout(2, false));
		compositePublicKey.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		lblPublicKey = getWidgetFactory().createLabel(compositePublicKey, ""); //$NON-NLS-1$
		lblPublicKey.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblPublicKey.setText(Messages.SelectionSection_4);
		
		txtPublicKey = getWidgetFactory().createText(compositePublicKey, ""); //$NON-NLS-1$
		txtPublicKey.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtPublicKey.setToolTipText(Messages.SelectionSection_5);
		txtPublicKey.addModifyListener(publicKeyListener);
		
		btnEncrypt = getWidgetFactory().createButton(compositePublicKey, Messages.SelectionSection_6, SWT.RADIO);
		btnEncrypt.setSelection(true);
		btnEncrypt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectionTransformation transfo = (SelectionTransformation)node.getGatewayModel();
				if(transfo != null && transfo instanceof EncryptTransformation){
					if(btnEncrypt.getSelection()){
						((EncryptTransformation)transfo).setEncrypt(true);
					}
					else {
						((EncryptTransformation)transfo).setEncrypt(false);
					}
				}
			}
		});
		
		btnDecrypt = getWidgetFactory().createButton(compositePublicKey, Messages.SelectionSection_7, SWT.RADIO);
		
		ToolBar toolbar = new ToolBar(composite, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		toolbar.setBackground(composite.getBackground());
		
		ToolItem addSelected = new ToolItem(toolbar, SWT.PUSH);
		addSelected.setText(Messages.SelectionSection_0);
		addSelected.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				uncheckOnPurpose = true;
				
				performAddColumns(streamComposite.getCheckedPosition());
				streamComposite.refresh();
				streamComposite.clearCheck();
				refresh();
			}
			
		});
		
		ToolItem delSelected = new ToolItem(toolbar, SWT.PUSH);
		delSelected.setText(Messages.SelectionSection_1);
		delSelected.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				uncheckOnPurpose = true;
				
				performDelColumns(streamComposite.getCheckedPosition());
				streamComposite.refresh();
				streamComposite.clearCheck();

			}
		});
		
		ToolItem addAll = new ToolItem(toolbar, SWT.PUSH);
		addAll.setText(Messages.SelectionSection_2);
		addAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				uncheckOnPurpose = true;
				
				performAddColumns(streamComposite.getAllElementPositions());
				streamComposite.refresh();
				streamComposite.clearCheck();
				refresh();
			}
			
		});
		
		ToolItem delAllSelected = new ToolItem(toolbar, SWT.PUSH);
		delAllSelected.setText(Messages.SelectionSection_3);
		delAllSelected.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				uncheckOnPurpose = true;
				
				performDelColumns(streamComposite.getAllElementPositions());
				streamComposite.refresh();
				streamComposite.clearCheck();

			}
		});
		streamComposite = new StreamComposite(composite, SWT.NONE, true, false);
		streamComposite.setLabelProvider(new StreamLabelProvider(new LabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		streamComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
        
        this.init = false;
        

		if(node.getGatewayModel() instanceof EncryptTransformation){
			lblPublicKey.setVisible(true);
			txtPublicKey.setVisible(true);
			btnEncrypt.setVisible(true);
			btnDecrypt.setVisible(true);
		}
		else {
			lblPublicKey.setVisible(false);
			txtPublicKey.setVisible(false);
			btnEncrypt.setVisible(false);
			btnDecrypt.setVisible(false);
		}
	}
	
	@Override
	public void refresh() {
		SelectionTransformation transfo = (SelectionTransformation)node.getGatewayModel();
		
			
		try {
			if(transfo != null && transfo instanceof EncryptTransformation){
				if(((EncryptTransformation)transfo).getPublicKey() != null){
					txtPublicKey.setText(((EncryptTransformation)transfo).getPublicKey());
				}
				if(((EncryptTransformation)transfo).isEncrypt()){
					btnEncrypt.setSelection(true);
					btnDecrypt.setSelection(false);
				}
				else {
					btnDecrypt.setSelection(true);
					btnEncrypt.setSelection(false);
				}
			}
			
			List<StreamElement> l = new ArrayList<StreamElement>();
			for(Transformation t : transfo.getInputs()){
				l.addAll(t.getDescriptor(t).getStreamElements());
			}
			streamComposite.fillDatas(l);
			if(!l.isEmpty() && !uncheckOnPurpose && !init && transfo.isInited()){
				performAddColumns(streamComposite.getCheckedAndUnCkeckedPosition());
				streamComposite.refresh();
				streamComposite.clearCheck();
				init = true;
				refresh();
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	private ModifyListener publicKeyListener = new ModifyListener() {
		
		public void modifyText(ModifyEvent e) {
			SelectionTransformation transfo = (SelectionTransformation)node.getGatewayModel();
			if(transfo != null && transfo instanceof EncryptTransformation){
				((EncryptTransformation)transfo).setPublicKey(txtPublicKey.getText());
			}
		}
	};


	@Override
	public void aboutToBeShown() {
		node.addPropertyChangeListener(listenerConnection);
		txtPublicKey.addModifyListener(publicKeyListener);
		super.aboutToBeShown();
	}


	@Override
	public void aboutToBeHidden() {
		if (node != null){
			node.removePropertyChangeListener(listenerConnection);
		}
		try {
			txtPublicKey.removeModifyListener(publicKeyListener);
		} catch (Exception e) {
		}
		super.aboutToBeHidden();
	}
	
	
	protected void performAddColumns(List<Integer>  positions){
		SelectionTransformation tr = (SelectionTransformation)node.getGatewayModel();
		
		if(tr != null){
			for(Integer i : positions) {
				tr.activeStreamElement(i);
			}
		}
	}
	
	protected void performDelColumns(List<Integer>  positions){
		SelectionTransformation tr = (SelectionTransformation)node.getGatewayModel();
		
		if(tr != null){
			for(Integer i : positions){
				tr.desactiveStreamElement(i);
			}
		}
	}
	
	public class StreamLabelProvider extends DefaultStreamLabelProvider{

		public StreamLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
			super(provider, decorator);
		}

		@Override
		public Color getBackground(Object element) {
			SelectionTransformation tr = null;
			
			if (node.getGatewayModel() instanceof SelectionTransformation){
				tr = (SelectionTransformation)node.getGatewayModel();
			}
			
			if (!tr.isOutputed((StreamElement)element)){
				return RED;
			}
			
			return super.getBackground(element);
		}
		
	}
}
