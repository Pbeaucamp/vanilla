package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.calcul.Calculation;
import bpm.gateway.core.transformations.calcul.Script;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.tools.dialogs.DialogCalculationEditor;

public class CalculationSection extends AbstractPropertySection {

	protected Node node;
	private Calculation transfo;
	private PropertyChangeListener listenerConnection;

	private TableViewer newFields;

	public CalculationSection() {
		listenerConnection = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)) {
					refresh();
				}

			}

		};

	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		ToolBar toolbar = new ToolBar(composite, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.CalculationSection_0);
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				DialogCalculationEditor d = new DialogCalculationEditor(getPart().getSite().getShell(), transfo, null);
				if(d.open() == Dialog.OK) {
					newFields.setInput(transfo.getScripts());

				}
			}

		});

		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.CalculationSection_2);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.del_16));
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(newFields.getSelection().isEmpty()) {
					return;
				}

				for(Object o : ((IStructuredSelection) newFields.getSelection()).toList()) {
					transfo.removeScript((Script) o);
				}

				newFields.setInput(transfo.getScripts());
			}

		});

		ToolItem edit = new ToolItem(toolbar, SWT.PUSH);
		edit.setToolTipText(Messages.CalculationSection_3);
		edit.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.calc_16));
		edit.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(newFields.getSelection().isEmpty()) {
					return;
				}

				Script sc = (Script) ((IStructuredSelection) newFields.getSelection()).getFirstElement();

				DialogCalculationEditor d = new DialogCalculationEditor(getPart().getSite().getShell(), transfo, sc);
				if(d.open() == Dialog.OK) {
					newFields.setInput(transfo.getScripts());

				}

			}

		});

		newFields = new TableViewer(composite, SWT.BORDER | SWT.V_SCROLL);
		newFields.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		newFields.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List l = transfo.getScripts();
				return l.toArray(new Script[l.size()]);
			}

			public void dispose() {
			

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			

			}

		});
		newFields.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Script) element).getName();
			}

		});
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		this.transfo = (Calculation) node.getGatewayModel();
	}

	@Override
	public void refresh() {
		if(newFields != null && !newFields.getTable().isDisposed()) {
			newFields.setInput(transfo.getScripts());
		}

	}

	@Override
	public void aboutToBeShown() {
		node.addPropertyChangeListener(listenerConnection);
		super.aboutToBeShown();
	}

	@Override
	public void aboutToBeHidden() {
		if(node != null) {
			node.removePropertyChangeListener(listenerConnection);
		}
		super.aboutToBeHidden();
	}

}
