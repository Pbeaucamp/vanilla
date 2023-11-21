package bpm.gateway.ui.views.property.sections.transformations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.ConsistencyMapping;
import bpm.gateway.core.transformations.ConsitencyTransformation;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.composites.labelproviders.DefaultStreamLabelProvider;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class ConsistencyMappingSection extends AbstractPropertySection {

	private static Color BLUE = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get(Messages.DBOutputMappingSection_0);

	private CCombo inputCombo, masterCombo;

	private Node node;
	private ConsitencyTransformation transfo;
	private StreamComposite masterStreamComposite, mappingStreamComposite;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		
		Composite masterSelectionComp = getWidgetFactory().createComposite(composite);
		masterSelectionComp.setLayout(new GridLayout(2, false));
		masterSelectionComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		Label lm = getWidgetFactory().createLabel(masterSelectionComp, Messages.ConsistencyMappingSection_0);
		lm.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		masterCombo = getWidgetFactory().createCCombo(masterSelectionComp, SWT.READ_ONLY);
		masterCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		masterCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String transfoName = masterCombo.getText();
				if(!transfoName.equals(transfo.getMasterName())) {
					ConsistencyMapping oldMapping = null;
					for(ConsistencyMapping map : transfo.getConsistencyMappings()) {
						if(map.getInputName().equals(transfoName)) {
							oldMapping = map;
							break;
						}
					}
					transfo.getConsistencyMappings().remove(oldMapping);
					
					Transformation oldMaster = transfo.getMasterInput();
					transfo.setMasterInput(oldMapping.getInput());
					
					ConsistencyMapping map = new ConsistencyMapping();
					map.setParent(transfo);
					map.setInput(oldMaster);
					transfo.addMapping(map);
					
					for(ConsistencyMapping m : transfo.getConsistencyMappings()) {
						m.getMappings().clear();
					}
					
					refresh();
					
				}
			}
		});
		
		Label lblEmpty = getWidgetFactory().createLabel(composite, ""); //$NON-NLS-1$
		lblEmpty.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		
		Composite inputSelectionComp = getWidgetFactory().createComposite(composite);
		inputSelectionComp.setLayout(new GridLayout(2, false));
		inputSelectionComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		Label l = getWidgetFactory().createLabel(inputSelectionComp, Messages.ConsistencyMappingSection_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		inputCombo = getWidgetFactory().createCCombo(inputSelectionComp, SWT.READ_ONLY);
		inputCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inputCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(ConsistencyMapping mapping : transfo.getConsistencyMappings()) {
					if(inputCombo.getText().equals(mapping.getInput().getName())) {
						try {
							mappingStreamComposite.fillDatas(mapping.getInput().getDescriptor(null).getStreamElements());
							masterStreamComposite.refresh();
						} catch (ServerException e1) {
							e1.printStackTrace();
						}
						break;
					}
				}
			}
		});
		
		Label lblMaster = getWidgetFactory().createLabel(composite, Messages.ConsistencyMappingSection_3);
		lblMaster.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		Label lblMapping = getWidgetFactory().createLabel(composite, Messages.ConsistencyMappingSection_4);
		lblMapping.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		masterStreamComposite = new StreamComposite(composite, SWT.NONE, false, false);
		masterStreamComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		masterStreamComposite.setLabelProvider(new StreamLabelProvider(masterStreamComposite.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), masterStreamComposite));
		
		Composite buttonBar = getWidgetFactory().createComposite(composite);
		buttonBar.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, true));
		buttonBar.setLayout(new GridLayout());

		Button mapButton = getWidgetFactory().createButton(buttonBar, Messages.DBOutputMappingSection_3, SWT.PUSH);
		mapButton.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		mapButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConsistencyMapping currentMapping = null;
				for(ConsistencyMapping mapping : transfo.getConsistencyMappings()) {
					if(inputCombo.getText().equals(mapping.getInput().getName())) {
						currentMapping = mapping;
						break;
					}
				}
				
				Integer inputIndex = null;
				Integer thisIndex = null;

				try {
					inputIndex = mappingStreamComposite.getCheckedPosition().get(0);
				} catch (IndexOutOfBoundsException ex) {
					MessageDialog.openConfirm(getPart().getSite().getShell(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}

				try {
					thisIndex = masterStreamComposite.getCheckedPosition().get(0);
				} catch (IndexOutOfBoundsException ex) {
					MessageDialog.openConfirm(getPart().getSite().getShell(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
				
				
				try {
					currentMapping.addMapping(thisIndex, inputIndex);
				} catch (ServerException e1) {
					e1.printStackTrace();
				}
				
				mappingStreamComposite.clearCheck();
				masterStreamComposite.clearCheck();

				mappingStreamComposite.refresh();
				masterStreamComposite.refresh();
			}
		});
		
		Button unmapButton = getWidgetFactory().createButton(buttonBar, Messages.DBOutputMappingSection_9, SWT.PUSH);
		unmapButton.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		unmapButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConsistencyMapping currentMapping = null;
				for(ConsistencyMapping mapping : transfo.getConsistencyMappings()) {
					if(inputCombo.getText().equals(mapping.getInput().getName())) {
						currentMapping = mapping;
						break;
					}
				}
				
				Integer inputIndex = null;
				Integer thisIndex = null;

				try {
					inputIndex = mappingStreamComposite.getCheckedPosition().get(0);
				} catch (IndexOutOfBoundsException ex) {
					MessageDialog.openConfirm(getPart().getSite().getShell(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}

				try {
					thisIndex = masterStreamComposite.getCheckedPosition().get(0);
				} catch (IndexOutOfBoundsException ex) {
					MessageDialog.openConfirm(getPart().getSite().getShell(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
				
				
				try {
					currentMapping.removeMapping(thisIndex, inputIndex);
				} catch (ServerException e1) {
					e1.printStackTrace();
				}
				
				mappingStreamComposite.clearCheck();
				masterStreamComposite.clearCheck();

				mappingStreamComposite.refresh();
				masterStreamComposite.refresh();
			}
		});
		
		mappingStreamComposite = new StreamComposite(composite, SWT.NONE, false, false);
		mappingStreamComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		mappingStreamComposite.setLabelProvider(new StreamLabelProvider(mappingStreamComposite.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), mappingStreamComposite));
		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		this.transfo = (ConsitencyTransformation) node.getGatewayModel();
	}
	
	@Override
	public void refresh() {
		List<String> items = new ArrayList<String>();
		ConsistencyMapping first = null;
		for(ConsistencyMapping map : transfo.getConsistencyMappings()) {
			items.add(map.getInput().getName());
			if(first == null) {
				first = map;
			}
		}
		inputCombo.setItems(items.toArray(new String[items.size()]));
		
		if(first != null) {
			inputCombo.setText(first.getInput().getName());
			try {
				mappingStreamComposite.fillDatas(first.getInput().getDescriptor(null).getStreamElements());
			} catch (ServerException e) {
				e.printStackTrace();
			}
		}
		
		items.add(0, transfo.getMasterName());
		
		masterCombo.setItems(items.toArray(new String[items.size()]));
		masterCombo.setText(transfo.getMasterName());
		
		List<StreamElement> masterElements = null;
		masterElements = transfo.getDescriptorWithoutMappings();
		
		masterStreamComposite.fillDatas(masterElements);
	}
	
	public class StreamLabelProvider extends DefaultStreamLabelProvider {

		private StreamComposite composite;

		public StreamLabelProvider(ILabelProvider provider, ILabelDecorator decorator, StreamComposite composite) {
			super(provider, decorator);
			this.composite = composite;
		}

		@Override
		public Color getForeground(Object element) {
			ConsistencyMapping currentMapping = null;
			for(ConsistencyMapping mapping : transfo.getConsistencyMappings()) {
				if(inputCombo.getText().equals(mapping.getInput().getName())) {
					currentMapping = mapping;
					break;
				}
			}

			if (composite == masterStreamComposite) {
				try {
					if (currentMapping.getMappings().keySet().contains(((StreamElement) element).name)) {
						return BLUE;
					}
					else {
						return null;
					}
				} catch (Exception e) {
				}
			}
			else {

				try {
					if (currentMapping.getMappings().values().contains(((StreamElement) element).name)) {
						return BLUE;
					}
					else {
						return null;
					}
				} catch (Exception e) {
				}
			}
			return null;
		}

		@Override
		public Font getFont(Object element) {
			Object o = getLabelProvider();
			if (getLabelProvider() instanceof StreamComposite.MyLabelProvider) {
				return ((StreamComposite.MyLabelProvider) getLabelProvider()).getFont(element);
			}
			return super.getFont(element);
		}

	}
}
