package bpm.gateway.ui.views.property.sections.database;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import bpm.gateway.core.transformations.MappingException;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.core.transformations.outputs.IOutput;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.composites.labelproviders.DefaultStreamLabelProvider;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogMatchingFields;

public class DBOutputMappingSection extends AbstractPropertySection {

	private static Color BLUE = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get(Messages.DBOutputMappingSection_0);

	private CCombo inputCombo;
	private Button nameMatching, unmatchAll;
	private StreamComposite inputStreamComposite, outputStreamComposite;
	private PropertyChangeListener listenerConnection;

	/*
	 * models
	 */
	private List<Transformation> inputs = new ArrayList<Transformation>();
	private Node node;

	private IOutput transfo;
	private Transformation selectedInput;;

	public DBOutputMappingSection() {
		listenerConnection = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP) || evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)) {
					refresh();
				}
				else if (evt.getPropertyName().equals(DataBaseOutputStream.PROPERTY_DEFINITION_CHANGED)) {
					fillOutputs();
				}

			}

		};
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout());

		/*
		 * for choosing the input transformation
		 */
		Composite inputChoice = getWidgetFactory().createComposite(composite);
		inputChoice.setLayout(new GridLayout(2, false));
		inputChoice.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l = getWidgetFactory().createLabel(inputChoice, Messages.DBOutputMappingSection_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		inputCombo = getWidgetFactory().createCCombo(inputChoice, SWT.READ_ONLY);
		inputCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inputCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				nameMatching.setEnabled(false);
				for (Transformation t : inputs) {
					if (t.getName().equals(inputCombo.getText())) {
						try {
							selectedInput = t;
							inputStreamComposite.fillDatas(t.getDescriptor(transfo).getStreamElements());
							nameMatching.setEnabled(true);
						} catch (ServerException e1) {

							e1.printStackTrace();
						}
						break;
					}
				}
			}

		});

		/*
		 * for define the mapping between inputs and output transformations
		 */

		Composite mappingComposite = getWidgetFactory().createComposite(composite);
		mappingComposite.setLayout(new GridLayout(3, false));
		mappingComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		nameMatching = getWidgetFactory().createButton(mappingComposite, Messages.DBOutputMappingSection_2, SWT.PUSH);
		nameMatching.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		nameMatching.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					DialogMatchingFields d = new DialogMatchingFields(getPart().getSite().getShell(), selectedInput.getDescriptor(transfo), transfo.getDescriptor(transfo));
					if (d.open() == Dialog.OK) {
						for (Point p : d.getChecked()) {
							try {
								transfo.createMapping(selectedInput, p.x, p.y);
							} catch (MappingException e1) {
								e1.printStackTrace();
							}

						}
						inputStreamComposite.refresh();
						outputStreamComposite.refresh();
					}
				} catch (ServerException ex) {

				}
			}

		});
		nameMatching.setEnabled(false);

		unmatchAll = getWidgetFactory().createButton(mappingComposite, Messages.DBOutputMappingSection_4, SWT.PUSH);
		unmatchAll.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		unmatchAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				transfo.clearMapping(selectedInput);
				outputStreamComposite.clearCheck();
				inputStreamComposite.clearCheck();
				inputStreamComposite.refresh();
				outputStreamComposite.refresh();
			}
		});

		inputStreamComposite = new StreamComposite(mappingComposite, SWT.NONE, false, false);
		inputStreamComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		inputStreamComposite.setLabelProvider(new StreamLabelProvider(inputStreamComposite.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), inputStreamComposite));

		Composite buttonBar = getWidgetFactory().createComposite(mappingComposite);
		buttonBar.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, true));
		buttonBar.setLayout(new GridLayout());

		Button mapButton = getWidgetFactory().createButton(buttonBar, Messages.DBOutputMappingSection_3, SWT.PUSH);
		mapButton.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		mapButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Integer inputIndex = null;
				Integer thisIndex = null;

				try {
					inputIndex = inputStreamComposite.getCheckedPosition().get(0);
				} catch (IndexOutOfBoundsException ex) {
					MessageDialog.openConfirm(getPart().getSite().getShell(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}

				try {
					thisIndex = outputStreamComposite.getCheckedPosition().get(0);
				} catch (IndexOutOfBoundsException ex) {
					MessageDialog.openConfirm(getPart().getSite().getShell(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}

				try {
					transfo.createMapping(selectedInput, inputIndex, thisIndex);
					outputStreamComposite.clearCheck();
					inputStreamComposite.clearCheck();

					inputStreamComposite.refresh();
					outputStreamComposite.refresh();

				} catch (MappingException e1) {
					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputMappingSection_8, e1.getMessage());
				}
			}

		});

		Button unmapButton = getWidgetFactory().createButton(buttonBar, Messages.DBOutputMappingSection_9, SWT.PUSH);
		unmapButton.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		unmapButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Integer inputIndex = null;
				Integer thisIndex = null;

				try {
					inputIndex = inputStreamComposite.getCheckedPosition().get(0);
				} catch (IndexOutOfBoundsException ex) {
					MessageDialog.openConfirm(getPart().getSite().getShell(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}

				try {
					thisIndex = outputStreamComposite.getCheckedPosition().get(0);
				} catch (IndexOutOfBoundsException ex) {
					MessageDialog.openConfirm(getPart().getSite().getShell(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}

				transfo.deleteMapping(selectedInput, inputIndex, thisIndex);
				outputStreamComposite.clearCheck();
				inputStreamComposite.clearCheck();
			}

		});

		outputStreamComposite = new StreamComposite(mappingComposite, SWT.NONE, false, false);
		outputStreamComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		outputStreamComposite.setLabelProvider(new StreamLabelProvider(outputStreamComposite.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), outputStreamComposite));

		/*
		 * register Listener
		 */
		inputStreamComposite.addListenerOnViewer(new MyListener(inputStreamComposite, outputStreamComposite));
		outputStreamComposite.addListenerOnViewer(new MyListener(outputStreamComposite, inputStreamComposite));

	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		this.transfo = (IOutput) node.getGatewayModel();
	}

	@Override
	public void refresh() {
		IOutput transfo = (IOutput) node.getGatewayModel();

		/*
		 * fill the inputs
		 */
		inputs.clear();
		outputStreamComposite.fillDatas(new ArrayList<StreamElement>());
		inputStreamComposite.fillDatas(new ArrayList<StreamElement>());

		for (Transformation t : transfo.getInputs()) {
			inputs.add(t);
		}

		String[] names = new String[inputs.size()];
		int i = 0;

		for (Transformation t : inputs) {
			names[i++] = t.getName();
		}

		inputCombo.setItems(names);

		fillOutputs();

	}

	private void fillOutputs() {
		if (selectedInput == null) {
			try {
				selectedInput = inputs.get(0);
				inputCombo.select(0);
				inputStreamComposite.fillDatas(selectedInput.getDescriptor(transfo).getStreamElements());
				nameMatching.setEnabled(true);
			} catch (Exception e) {

			}
		}
		else {
			try {
				int index = 0;
				if (inputs != null) {
					for (int i = 0; i < inputs.size(); i++) {
						if (inputs.get(i).equals(selectedInput)) {
							index = i;
						}
					}
				}
	
				inputCombo.select(index);
				inputStreamComposite.fillDatas(selectedInput.getDescriptor(transfo).getStreamElements());
			} catch (Exception e) {
	
			}
		}

		/*
		 * fill the outputs
		 */
		try {
			outputStreamComposite.fillDatas(node.getGatewayModel().getDescriptor(transfo).getStreamElements());
		} catch (Exception e) {
			e.printStackTrace();
			outputStreamComposite.fillDatas(new ArrayList<StreamElement>());
		}
	}

	@Override
	public void aboutToBeShown() {
		node.addPropertyChangeListener(listenerConnection);
		super.aboutToBeShown();
	}

	@Override
	public void aboutToBeHidden() {
		if (node != null) {
			node.removePropertyChangeListener(listenerConnection);
		}
		super.aboutToBeHidden();
	}

	public class MyListener implements ISelectionChangedListener {

		private StreamComposite target;
		private StreamComposite source;

		public MyListener(StreamComposite source, StreamComposite target) {
			this.target = target;
			this.source = source;
		}

		public void selectionChanged(SelectionChangedEvent event) {
			if (selectedInput == null) {
				return;
			}
			IStructuredSelection ss = (IStructuredSelection) event.getSelection();
			target.desactiveListener(true);
			if (ss.isEmpty()) {
				target.setSelection(null);
			}

			else {
				if (source == inputStreamComposite) {
					if (source.getSelection() != null) {
						target.setSelection(transfo.getMappingValueForInputNum(selectedInput, source.getSelection()));
					}

				}
				else {
					if (source.getSelection() != null) {
						target.setSelection(transfo.getMappingValueForThisNum(selectedInput, source.getSelection()));
					}

				}

			}
			target.desactiveListener(false);
		}

	}

	public class StreamLabelProvider extends DefaultStreamLabelProvider {

		private StreamComposite composite;

		public StreamLabelProvider(ILabelProvider provider, ILabelDecorator decorator, StreamComposite composite) {
			super(provider, decorator);
			this.composite = composite;
		}

		@Override
		public Color getForeground(Object element) {

			IOutput tr = (IOutput) node.getGatewayModel();

			if (composite == outputStreamComposite) {
				if (((StreamElement) element).transfoName.equals(tr.getName())) {
					if (tr.isMapped(((StreamElement) element).name)) {
						return BLUE;
					}
					else {
						return null;
					}
				}
			}
			else {

				if (((StreamElement) element).transfoName.equals(selectedInput.getName())) {
					if (tr.isMapped(selectedInput, ((StreamElement) element).originTransfo + "::" + ((StreamElement) element).name)) { //$NON-NLS-1$
						return BLUE;
					}
					else {
						return null;
					}
				}

			}
			return super.getBackground(element);
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
