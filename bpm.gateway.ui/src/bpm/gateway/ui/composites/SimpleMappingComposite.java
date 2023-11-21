package bpm.gateway.ui.composites;

import java.awt.Point;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.composites.labelproviders.DefaultStreamLabelProvider;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogMatchingFields;

public class SimpleMappingComposite extends Composite {
	private static Color BLUE = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get("bpm.gateway.ui.colors.mappingexists"); //$NON-NLS-1$
	private static Color RED = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);

	private SimpleMappingTransformation mapping;
	private StreamComposite inputComposite;
	private StreamComposite outputComposite;
	private ToolItem lookForNameMatching;
	
	private Button b, b2;
	

	
	public SimpleMappingComposite(Composite parent, int type) {
		super(parent, type);
		this.setLayout(new GridLayout());
		this.setBackground(parent.getBackground());
		
		builContent();
				 
		
		
	}

	protected void builContent() {
		Composite container = new Composite(this, SWT.NONE);
		container.setLayout(new GridLayout(2, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setBackground(this.getBackground());
		
		ToolBar toolbar = new ToolBar(container, SWT.NONE);
		toolbar.setLayout(new GridLayout());
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		toolbar.setBackground(this.getBackground());
		
		
		ToolItem associate = new ToolItem(toolbar, SWT.PUSH);
		associate.setText(Messages.SimpleMappingComposite_1);
		associate.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Integer inputEl = inputComposite.getCheckedPosition().get(0);
				Integer outputEl = outputComposite.getCheckedPosition().get(0);
				
				if (inputEl == null){
					MessageDialog.openInformation(getShell(), Messages.SimpleMappingComposite_2, Messages.SimpleMappingComposite_3);
					return;
				}
				if (outputEl == null){
					MessageDialog.openInformation(getShell(), Messages.SimpleMappingComposite_4, Messages.SimpleMappingComposite_5);
					return;
				}
				
				
				try {
					mapping.createMapping(inputEl, outputEl);
					inputComposite.clearCheck();
					outputComposite.clearCheck();
					
					inputComposite.refresh();
					outputComposite.refresh();
				} catch (Exception e1) {
					MessageDialog.openInformation(getShell(), Messages.DBOutputMappingSection_8, e1.getMessage());
				}
			}
			
		});
		
		
		ToolItem dissociate = new ToolItem(toolbar, SWT.PUSH);
		dissociate.setText(Messages.SimpleMappingComposite_6);
		dissociate.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Integer inputEl = inputComposite.getCheckedPosition().get(0);
				Integer outputEl = outputComposite.getCheckedPosition().get(0);
				
				if (inputEl == null){
					MessageDialog.openInformation(getShell(), Messages.SimpleMappingComposite_7, Messages.SimpleMappingComposite_8);
					return;
				}
				if (outputEl == null){
					MessageDialog.openInformation(getShell(), Messages.SimpleMappingComposite_9, Messages.SimpleMappingComposite_10);
					return;
				}
				
				
				mapping.deleteMapping(inputEl, outputEl);
				inputComposite.clearCheck();
				outputComposite.clearCheck();
				
				inputComposite.refresh();
				outputComposite.refresh();
			}
			
		});
		
		
		
		lookForNameMatching = new ToolItem(toolbar, SWT.PUSH);
		lookForNameMatching.setText(Messages.SimpleMappingComposite_11);
		lookForNameMatching.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					DialogMatchingFields d = new DialogMatchingFields(getShell(), 
							mapping.getInputs().get(0).getDescriptor(mapping),
							mapping.getInputs().get(1).getDescriptor(mapping));
					if(d.open() == Dialog.OK){
						for(Point p : d.getChecked()){
							try {
								mapping.createMapping(p.x, p.y);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						inputComposite.refresh();
						outputComposite.refresh();
					}
				}catch(ServerException ex){
					
				}
				
			}
			
		});
		
		Group g = new Group(container, SWT.NONE);
		g.setBackground(getBackground());
		
		g.setText(Messages.SimpleMappingComposite_12);
		g.setLayout(new GridLayout(2, true));
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		b = new Button(g, SWT.RADIO);
		b.setText(Messages.SimpleMappingComposite_13);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				mapping.setAsMaster(mapping.getInputs().get(0));
			}
			
		});
		b.setEnabled(false);
		b.setBackground(this.getBackground());
		
		b2 = new Button(g, SWT.RADIO);
		b2.setText(Messages.SimpleMappingComposite_14);
		b2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b2.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapping.setAsMaster(mapping.getInputs().get(1));
			}
		});
		b2.setEnabled(false);
		b2.setBackground(this.getBackground());
		
		inputComposite = new StreamComposite(container, SWT.NONE, false, false);
		inputComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		inputComposite.setLabelProvider(new StreamLabelProvider(inputComposite.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), inputComposite));
	
		outputComposite = new StreamComposite(container, SWT.NONE, false, false);
		outputComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		outputComposite.setLabelProvider(new StreamLabelProvider(outputComposite.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), outputComposite));
				
		inputComposite.addListenerOnViewer(new MyListener(inputComposite, outputComposite));
		outputComposite.addListenerOnViewer(new MyListener(outputComposite, inputComposite));
		
	}
	
	public void fillDatas(SimpleMappingTransformation mapping){
		this.mapping = mapping;
		
		try {
			
			if (!mapping.getInputs().isEmpty()){
				inputComposite.fillDatas(mapping.getInputs().get(0).getDescriptor(mapping).getStreamElements());
				b.setEnabled(true);
				if (mapping.isMaster(mapping.getInputs().get(0))){
					b.setSelection(true);
					b2.setSelection(false);
				}
			}
			else{
				inputComposite.fillDatas(new ArrayList<StreamElement>());
				b.setEnabled(false);

			}

		} catch (ServerException e) {
			
			e.printStackTrace();
		}
		try {
			

			if (mapping.getInputs().size() > 1){
				outputComposite.fillDatas(mapping.getInputs().get(1).getDescriptor(mapping).getStreamElements());
				b2.setEnabled(true);
				if (mapping.isMaster(mapping.getInputs().get(1))){
					b2.setSelection(true);
					b.setSelection(false);
				}
			}
			else{
				outputComposite.fillDatas(new ArrayList<StreamElement>());
				b2.setEnabled(false);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		lookForNameMatching.setEnabled(mapping.getInputs().size() >= 2);

	}

	
	public class MyListener implements ISelectionChangedListener{

		private StreamComposite target; 
		private StreamComposite source;
		
		public MyListener(StreamComposite source, StreamComposite target){
			this.target = target;
			this.source = source;
		}
		
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection)event.getSelection();
			target.desactiveListener(true);
			if (ss.isEmpty()){
				target.setSelection(null);
			}
			
			else{
				if (source == inputComposite){
					if (source.getSelection() != null){
						target.setSelection(mapping.getMappingValueForInputNum(source.getSelection()));
					}
				}
				else{
					if (source.getSelection() != null){
						target.setSelection(mapping.getMappingValueForThisNum(source.getSelection()));
					}
				}
			}
			target.desactiveListener(false);
		}
		
	}
	
	public class StreamLabelProvider extends DefaultStreamLabelProvider{

		private StreamComposite composite;
		
		public StreamLabelProvider(ILabelProvider provider,
				ILabelDecorator decorator, StreamComposite composite) {
			super(provider, decorator);
			this.composite = composite;
		}

		@Override
		public Color getForeground(Object element) {

			if (composite.getElementInError().contains(element)){
				return RED;
			}
			
			String colName = ((StreamElement)element).name;
			
			if (composite == outputComposite){
				if(mapping.isOutputMapped(colName)){
					return BLUE;
				}
				else {
					return null;
				}
			}
			else{
				if (mapping.isInputMapped(colName)){
					return BLUE;
				}
				else {
					return null;
				}
			}
		}

		@Override
		public Font getFont(Object element) {
			if (getLabelProvider() instanceof StreamComposite.MyLabelProvider){
				return ((StreamComposite.MyLabelProvider)getLabelProvider()).getFont(element); 
			}
			return super.getFont(element);
		}
		
		
	}
	
}
