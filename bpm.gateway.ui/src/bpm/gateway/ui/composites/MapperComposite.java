package bpm.gateway.ui.composites;

import java.awt.Point;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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

public class MapperComposite extends Composite{
	private static Color BLUE = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get("bpm.gateway.ui.colors.mappingexists"); //$NON-NLS-1$
	private static Color RED = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);

	private SimpleMappingTransformation transfo;
	private StreamComposite input0;
	private StreamComposite input1;
	
	private ToolItem lookForNameMatching;
	private Button input0Master, input1Master;
	
	
	public MapperComposite(Composite parent, int type) {
		super(parent, type);
		this.setLayout(new GridLayout());
		this.setBackground(parent.getBackground());
		
		buildContent();

	}
	
	private void buildContent(){
		Composite container = new Composite(this, SWT.NONE);
		container.setLayout(new GridLayout(2, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setBackground(this.getBackground());
		
		ToolBar toolbar = new ToolBar(container, SWT.NONE);
		toolbar.setLayout(new GridLayout());
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		toolbar.setBackground(this.getBackground());
		
		
		ToolItem associate = new ToolItem(toolbar, SWT.PUSH);
		associate.setText(Messages.MapperComposite_1);
		associate.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					Integer a = input0.getCheckedPosition().get(0);
					Integer b = input1.getCheckedPosition().get(0);
					transfo.createMapping(a, b);
					
					input0.clearCheck();
					input1.clearCheck();
					
					input0.refresh();
					input1.refresh();
				}catch(Exception ex){
					MessageDialog.openInformation(getShell(), Messages.DBOutputMappingSection_8, ex.getMessage());
				}
				
			}
			
		});
		
		
		ToolItem dissociate = new ToolItem(toolbar, SWT.PUSH);
		dissociate.setText(Messages.MapperComposite_2);
		dissociate.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					Integer a = input0.getCheckedPosition().get(0);
					Integer b = input1.getCheckedPosition().get(0);
					transfo.deleteMapping(a, b);
					
					input0.clearCheck();
					input1.clearCheck();
					input0.refresh();
					input1.refresh();
				}catch(Exception ex){
					
				}
			}
			
		});
		
		
		
		lookForNameMatching = new ToolItem(toolbar, SWT.PUSH);
		lookForNameMatching.setText(Messages.MapperComposite_3);
		lookForNameMatching.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					DialogMatchingFields d = new DialogMatchingFields(getShell(), 
							transfo.getInputs().get(0).getDescriptor(transfo),
							transfo.getInputs().get(1).getDescriptor(transfo));
					if(d.open() == Dialog.OK){
						for(Point p : d.getChecked()){
							try {
								transfo.createMapping(p.x, p.y);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						input0.refresh();
						input1.refresh();
					}
				}catch(ServerException ex){
					
				}
				
			}
			
		});
		
		
		ToolItem unmapAll = new ToolItem(toolbar, SWT.PUSH);
		unmapAll.setText(Messages.MapperComposite_4);
		unmapAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					for(String input : transfo.getMappings().keySet()){
						transfo.deleteMapping(input, transfo.getMappings().get(input));
					}
					
					input0.clearCheck();
					input1.clearCheck();
					input0.refresh();
					input1.refresh();
				}catch(Exception ex){
					
				}
			}
			
		});
		
		Group g = new Group(container, SWT.NONE);
		g.setBackground(getBackground());
		
		g.setText(Messages.MapperComposite_5);
		g.setLayout(new GridLayout(2, true));
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		input0Master = new Button(g, SWT.RADIO);
		input0Master.setText(Messages.MapperComposite_6);
		input0Master.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		input0Master.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				transfo.setAsMaster(transfo.getInputs().get(0));
			}
			
		});
		input0Master.setEnabled(false);
		input0Master.setBackground(this.getBackground());
		
		input1Master = new Button(g, SWT.RADIO);
		input1Master.setText(Messages.MapperComposite_7);
		input1Master.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		input1Master.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				transfo.setAsMaster(transfo.getInputs().get(1));
			}
		});
		input1Master.setEnabled(false);
		input1Master.setBackground(this.getBackground());
		
		input0 = new StreamComposite(container, SWT.NONE, false, false);
		input0.setLayoutData(new GridData(GridData.FILL_BOTH));
		input0.setLabelProvider(new StreamLabelProvider(input0.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), input0));
	
		input1 = new StreamComposite(container, SWT.NONE, false, false);
		input1.setLayoutData(new GridData(GridData.FILL_BOTH));
		input1.setLabelProvider(new StreamLabelProvider(input1.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), input1));
				
		input0.addListenerOnViewer(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				input1.desactiveListener(true);
				Integer selectedIndex = input0.getSelection();
				
				
				if (selectedIndex != null){
					input1.setSelection(transfo.getMappingValueForInputNum(selectedIndex));
				}
				input1.desactiveListener(false);
			}
		});
		
		
		input1.addListenerOnViewer(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				input0.desactiveListener(true);
				Integer selectedIndex = input1.getSelection();
				
				
				if (selectedIndex != null){
					input0.setSelection(transfo.getMappingValueForThisNum(selectedIndex));
				}
				input0.desactiveListener(false);
			}
		});


	}
	
	
	
	public void fillDatas(SimpleMappingTransformation mapping){
		this.transfo = mapping;
		
		lookForNameMatching.setEnabled(mapping.getInputs().size() >= 2);
		
		try{
			input0.fillDatas(transfo.getInputs().get(0).getDescriptor(transfo).getStreamElements());
			input0Master.setEnabled(true);
			input0Master.setSelection(transfo.isMaster(transfo.getInputs().get(0)));
		}catch(Exception e){
			e.printStackTrace();
			input0.fillDatas(new ArrayList<StreamElement>());
			input0Master.setEnabled(false);
			input0Master.setSelection(false);
		}
		
		try{
			input1.fillDatas(transfo.getInputs().get(1).getDescriptor(transfo).getStreamElements());
			input1Master.setEnabled(true);
			input1Master.setSelection(transfo.isMaster(transfo.getInputs().get(1)));
		}catch(Exception e){
			e.printStackTrace();
			input1.fillDatas(new ArrayList<StreamElement>());
			input1Master.setEnabled(false);
			input1Master.setSelection(false);
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
			if (composite == input0){
				int elementPos = input0.getInput().indexOf(element);
				Integer outputNum = transfo.getMappingValueForInputNum(elementPos);
				
				if(outputNum != null){
					if (!(input1 == null || input1.isDisposed() || input1.getInput() == null)){
						if(outputNum > input1.getInput().size()){
							return RED;
						}
						else if(outputNum > -1) {
							return BLUE;
						}
					}
					else if(outputNum > -1) {
						return BLUE;
					}
				}
			}
			else if (composite == input1){
				int elementPos = input1.getInput().indexOf(element);

				Integer inputNum = transfo.getMappingValueForThisNum(elementPos);

				if(inputNum != null){
					if (!(input0 == null || input0.isDisposed() || input0.getInput() == null)){
						if(inputNum > input0.getInput().size()){
							return RED;
						}
						else if(inputNum > -1){
							return BLUE;
						}
					}
					else if(inputNum > -1) {
						return BLUE;
					}
				}
			}
			
			return super.getBackground(element);
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
