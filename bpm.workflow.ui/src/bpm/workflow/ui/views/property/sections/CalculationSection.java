package bpm.workflow.ui.views.property.sections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Properties;

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

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.CalculationActivity;
import bpm.workflow.runtime.resources.Script;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogCalculationEditor;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;
import bpm.workflow.ui.icons.IconsNames;
import bpm.workflow.ui.views.ResourceViewPart;

/**
 * Section for the calculation activity
 * 
 * @author CAMUS, MARTIN
 * 
 */
public class CalculationSection extends AbstractPropertySection {

	protected Node node;
	private CalculationActivity calculationActivity;
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
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ADDCROSS));
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Properties prop = new Properties();
				prop.setProperty("name", "new Calculated Variable"); //$NON-NLS-1$ //$NON-NLS-2$
				prop.setProperty("default", "calculated"); //$NON-NLS-1$ //$NON-NLS-2$
				prop.setProperty("type", 1 + ""); //$NON-NLS-1$ //$NON-NLS-2$

				try {
					Variable v = new Variable(prop);
					ListVariable.getInstance().addVariable(v);
					((WorkflowModel) Activator.getDefault().getCurrentModel()).addResource(ListVariable.getInstance().getVariable(v.getName()));
					ResourceViewPart view = (ResourceViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ResourceViewPart.ID);

					if(view != null) {
						view.refresh();
					}
					Script s = new Script();
					s.setName("new Calculated Variable"); //$NON-NLS-1$
					s.setType(1);
					calculationActivity.addScript(s);
					newFields.setInput(calculationActivity.getScripts());

					DialogCalculationEditor d = new DialogCalculationEditor(getPart().getSite().getShell(), calculationActivity, s);
					if(d.open() == Dialog.OK) {
						newFields.setInput(calculationActivity.getScripts());

					}

				} catch(Exception _e) {

				}

			}

		});

		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.CalculationSection_8);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.DELCROSS));
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(newFields.getSelection().isEmpty()) {
					return;
				}

				for(Object o : ((IStructuredSelection) newFields.getSelection()).toList()) {
					calculationActivity.removeScript((Script) o);
				}

				newFields.setInput(calculationActivity.getScripts());
			}

		});

		ToolItem edit = new ToolItem(toolbar, SWT.PUSH);
		edit.setToolTipText(Messages.CalculationSection_9);
		edit.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.LOUPE));
		edit.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(newFields.getSelection().isEmpty()) {
					return;
				}

				Script sc = (Script) ((IStructuredSelection) newFields.getSelection()).getFirstElement();

				DialogCalculationEditor d = new DialogCalculationEditor(getPart().getSite().getShell(), calculationActivity, sc);
				if(d.open() == Dialog.OK) {
					newFields.setInput(calculationActivity.getScripts());

				}

			}

		});

		newFields = new TableViewer(composite, SWT.BORDER | SWT.V_SCROLL);
		newFields.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		newFields.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List l = calculationActivity.getScripts();
				return l.toArray(new Script[l.size()]);
			}

			public void dispose() {}

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
		this.calculationActivity = (CalculationActivity) node.getWorkflowObject();
	}

	@Override
	public void refresh() {
		if(newFields != null && !newFields.getTable().isDisposed()) {
			newFields.setInput(calculationActivity.getScripts());
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
