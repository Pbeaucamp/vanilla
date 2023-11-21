package bpm.workflow.ui.composites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import bpm.workflow.runtime.model.IParameters;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.WorkfowModelParameter;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Composite for the mappings between the variables and the activities
 * 
 * @author Charles MARTIN
 * 
 */
public class CompositeVariablesMapping extends Composite {
	private static Color BLUE = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get("bpm.workflow.ui.colors.mappingexists"); //$NON-NLS-1$

	private CheckboxTableViewer actParams, varsParams;

	private IParameters activity;

	private ISelectionChangedListener actLst, varsLst;

	/**
	 * Create a variable mapping composite
	 * 
	 * @param parent
	 *            : the parent container
	 * @param style
	 */
	public CompositeVariablesMapping(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.setBackground(parent.getBackground());
		createContent(this);
		createListeners();

	}

	/**
	 * Set the input activity
	 * 
	 * @param biObject
	 *            : the activity with parameters (IParameters)
	 */
	public void setInputs(IParameters biObject) {
		this.activity = biObject;
	}

	private void createContent(Composite parent) {

		Composite serverC = new Composite(parent, SWT.NONE);
		serverC.setLayout(new GridLayout(3, false));
		serverC.setLayoutData(new GridData(GridData.FILL_BOTH));
		serverC.setBackground(parent.getBackground());

		Composite c = new Composite(serverC, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		c.setBackground(parent.getBackground());

		Label l2 = new Label(serverC, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		l2.setText(Messages.CompositeVariablesMapping_1);
		l2.setBackground(parent.getBackground());

		Label l3 = new Label(serverC, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeVariablesMapping_2);
		l3.setBackground(parent.getBackground());

		actParams = CheckboxTableViewer.newCheckList(serverC, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		actParams.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		actParams.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

		});
		actParams.setLabelProvider(new MappingLabelProvider((ILabelProvider) actParams.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), actParams));

		actParams.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if(event.getChecked()) {
					actParams.setAllChecked(false);
					actParams.setChecked(event.getElement(), true);
				}

			}

		});

		Composite buttonBar = new Composite(serverC, SWT.NONE);
		buttonBar.setLayout(new GridLayout());
		buttonBar.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, true));
		buttonBar.setBackground(parent.getBackground());

		Button map = new Button(buttonBar, SWT.PUSH | SWT.FLAT);
		map.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		map.setText(Messages.CompositeVariablesMapping_3);
		map.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(actParams.getCheckedElements().length == 0) {
					return;
				}

				if(varsParams.getCheckedElements().length == 0) {
					return;
				}

				String orb = (String) varsParams.getCheckedElements()[0];
				String act = (String) actParams.getCheckedElements()[0];

				if(activity != null) {
					activity.addParameterLink(act, orb);
				}

				// wttf??????
				Variable v = ListVariable.getInstance().getVariable(orb);
				if(v != null) {
					((WorkflowModel) Activator.getDefault().getCurrentModel()).addResource(v);
				}
				actParams.refresh();
				varsParams.refresh();
			}

		});

		Button unmap = new Button(buttonBar, SWT.PUSH | SWT.FLAT);
		unmap.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		unmap.setText(Messages.CompositeVariablesMapping_4);
		unmap.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(varsParams.getCheckedElements().length == 0) {
					return;
				}

				String orb = (String) varsParams.getCheckedElements()[0];
				String act = (String) actParams.getCheckedElements()[0];

				if(activity != null) {
					activity.removeParameterLink(act, orb);
				}

				((WorkflowModel) Activator.getDefault().getCurrentModel()).removeResource(ListVariable.getInstance().getVariable(orb).getId());
				varsParams.setChecked(orb, false);
				actParams.setChecked(act, false);
				actParams.refresh();
				varsParams.refresh();
			}

		});

		varsParams = CheckboxTableViewer.newCheckList(serverC, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		varsParams.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		varsParams.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		varsParams.setLabelProvider(new MappingLabelProvider((ILabelProvider) varsParams.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), varsParams));

		varsParams.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if(event.getChecked()) {
					varsParams.setAllChecked(false);
					varsParams.setChecked(event.getElement(), true);
				}

			}

		});

	}

	private void createListeners() {
		actLst = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				varsParams.removeSelectionChangedListener(varsLst);
				HashMap<String, String> map = new HashMap<String, String>();
				if(activity != null) {
					map = activity.getMappings();
				}

				String p = (String) ((IStructuredSelection) actParams.getSelection()).getFirstElement();

				if(p != null) {
					for(String s : map.keySet()) {
						if(p.equalsIgnoreCase(map.get(s))) {
							varsParams.setSelection(new StructuredSelection(s));
							break;

						}
					}
				}

				varsParams.addSelectionChangedListener(varsLst);

			}

		};
		actParams.addSelectionChangedListener(actLst);

		varsLst = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				actParams.removeSelectionChangedListener(varsLst);

				HashMap<String, String> map = new HashMap<String, String>();
				if(activity != null) {
					map = activity.getMappings();
				}

				String p = (String) ((IStructuredSelection) varsParams.getSelection()).getFirstElement();

				if(p != null) {
					for(String s : map.keySet()) {
						if(s.equalsIgnoreCase(p)) {

							for(String _p : (List<String>) actParams.getInput()) {
								if(_p.equalsIgnoreCase(map.get(s))) {
									actParams.setSelection(new StructuredSelection(_p));
									break;
								}
								else {
									actParams.setSelection(new StructuredSelection());
								}
							}

						}
					}
				}

				actParams.addSelectionChangedListener(varsLst);

			}

		};
		varsParams.addSelectionChangedListener(varsLst);
	}

	public void fill() throws Exception {

		List<String> paramNames = new ArrayList<String>();
		for(String param : ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())) {
			if(param.equalsIgnoreCase("{$VANILLA_FILES") || param.equalsIgnoreCase("{$VANILLA_HOME}")) { //$NON-NLS-1$ //$NON-NLS-2$

			}
			else {
				paramNames.add(param);
			}
		}

//		for(WorkfowModelParameter p : ((WorkflowModel) Activator.getDefault().getCurrentModel()).getParameters()) {
//			paramNames.add(p.getName());
//		}
		varsParams.setInput(paramNames);
		varsParams.refresh();

		if(activity != null) {
			actParams.setInput(activity.getParameters(Activator.getDefault().getRepositoryConnection()));
		}

		actParams.refresh();

	}

	public void removeListners() {
		actParams.removeSelectionChangedListener(actLst);
		varsParams.removeSelectionChangedListener(varsLst);

	}

	public void addListners() {
		actParams.addSelectionChangedListener(actLst);
		varsParams.addSelectionChangedListener(varsLst);

	}

	public class MappingLabelProvider extends DecoratingLabelProvider {
		private CheckboxTableViewer checkbox;

		public MappingLabelProvider(ILabelProvider provider, ILabelDecorator decorator, CheckboxTableViewer checkbox) {
			super(provider, decorator);
			this.checkbox = checkbox;

		}

		@Override
		public Color getForeground(Object element) {
			HashMap<String, String> map = new HashMap<String, String>();
			if(activity != null) {
				map = activity.getMappings();
			}

			String p = (String) element;
			if(checkbox == varsParams) {
				if(p != null) {
					for(String s : map.keySet()) {

						if(map.get(s).equalsIgnoreCase(p)) {

							return BLUE;
						}
					}
				}

			}
			else {
				if(p != null) {
					for(String s : map.keySet()) {
						if(p.equalsIgnoreCase(s)) {
							return BLUE;
						}
					}
				}
			}
			return super.getBackground(element);
		}

	}

}
