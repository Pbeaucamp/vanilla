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
import bpm.workflow.runtime.model.activities.InterfaceActivity;
import bpm.workflow.runtime.model.activities.KPIActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Composite for the mappings between activities (Forms and other activities)
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class CompositeFormMapping extends Composite {
	private static Color BLUE = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get("bpm.workflow.ui.colors.mappingexists"); //$NON-NLS-1$

	private CheckboxTableViewer actParams, orbeonParams;
	private InterfaceActivity form;
	private IParameters activity;

	private ISelectionChangedListener actLst, orbeonLst;

	/**
	 * Create a mapping composite
	 * 
	 * @param parent
	 *            : the parent composite
	 * @param style
	 */
	public CompositeFormMapping(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.setBackground(parent.getBackground());
		createContent(this);
		createListeners();

	}

	/**
	 * Set the input form and the object with the parameters
	 * 
	 * @param form
	 * @param biObject
	 *            : the object with the parameters (IParameters)
	 */
	public void setInputs(InterfaceActivity form, IParameters biObject) {
		this.form = form;
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
		l2.setText(Messages.CompositeFormMapping_1);
		l2.setBackground(parent.getBackground());

		Label l3 = new Label(serverC, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeFormMapping_2);
		l3.setBackground(parent.getBackground());

		actParams = CheckboxTableViewer.newCheckList(serverC, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		actParams.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		actParams.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

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
		map.setText(Messages.CompositeFormMapping_3);
		map.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(actParams.getCheckedElements().length == 0) {
					return;
				}

				if(orbeonParams.getCheckedElements().length == 0) {
					return;
				}

				String orb = (String) orbeonParams.getCheckedElements()[0];
				String act = (String) actParams.getCheckedElements()[0];

				form.addParameterMapping(orb, act);

				((IParameters) activity).addParameterLink(act, form.getId() + "_param_" + orb); //$NON-NLS-1$

			}

		});

		Button unmap = new Button(buttonBar, SWT.PUSH | SWT.FLAT);
		unmap.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		unmap.setText(Messages.CompositeFormMapping_5);
		unmap.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(orbeonParams.getCheckedElements().length == 0) {
					return;
				}

				String orb = (String) orbeonParams.getCheckedElements()[0];
				String act = (String) actParams.getCheckedElements()[0];

				form.removeParameterMapping(orb, act);
				if(activity instanceof ReportActivity) {
					((ReportActivity) activity).removeParameterLink(act, orb);

				}
				orbeonParams.setChecked(orb, false);
				actParams.setChecked(act, false);
			}

		});

		orbeonParams = CheckboxTableViewer.newCheckList(serverC, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		orbeonParams.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		orbeonParams.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		orbeonParams.setLabelProvider(new MappingLabelProvider((ILabelProvider) orbeonParams.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), orbeonParams));

		orbeonParams.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if(event.getChecked()) {
					orbeonParams.setAllChecked(false);
					orbeonParams.setChecked(event.getElement(), true);
				}

			}

		});

	}

	private void createListeners() {
		actLst = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				orbeonParams.removeSelectionChangedListener(orbeonLst);

				HashMap<String, String> map = form.getMappings();
				String p = (String) ((IStructuredSelection) actParams.getSelection()).getFirstElement();

				if(p != null) {
					for(String s : map.keySet()) {
						if(p.equalsIgnoreCase(map.get(s))) {
							orbeonParams.setSelection(new StructuredSelection(s));
							break;

						}
					}
				}

				orbeonParams.addSelectionChangedListener(orbeonLst);

			}

		};
		actParams.addSelectionChangedListener(actLst);

		orbeonLst = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				actParams.removeSelectionChangedListener(orbeonLst);

				HashMap<String, String> map = form.getMappings();

				String p = (String) ((IStructuredSelection) orbeonParams.getSelection()).getFirstElement();

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

				actParams.addSelectionChangedListener(orbeonLst);

			}

		};
		orbeonParams.addSelectionChangedListener(orbeonLst);
	}

	public void fill() throws Exception {
		if(form != null) {
			orbeonParams.setInput(form.getParameters(Activator.getDefault().getRepositoryConnection()));
		}
		if(activity instanceof KPIActivity) {
			List<String> listeParam = new ArrayList<String>();
			listeParam.add("value"); //$NON-NLS-1$
			listeParam.add("date"); //$NON-NLS-1$
			actParams.setInput(listeParam);
		}
		else {

			try {
				actParams.setInput(activity.getParameters(Activator.getDefault().getRepositoryConnection()));

			} catch(Exception et) {
				et.printStackTrace();
			}
		}
	}

	public void removeListners() {
		actParams.removeSelectionChangedListener(actLst);
		orbeonParams.removeSelectionChangedListener(orbeonLst);

	}

	public void addListners() {
		actParams.addSelectionChangedListener(actLst);
		orbeonParams.addSelectionChangedListener(orbeonLst);

	}

	public class MappingLabelProvider extends DecoratingLabelProvider {
		private CheckboxTableViewer checkbox;

		public MappingLabelProvider(ILabelProvider provider, ILabelDecorator decorator, CheckboxTableViewer checkbox) {
			super(provider, decorator);
			this.checkbox = checkbox;

		}

		@Override
		public Color getForeground(Object element) {
			HashMap<String, String> map = form.getMappings();
			String p = (String) element;
			if(checkbox == orbeonParams) {
				if(p != null) {
					for(String s : map.keySet()) {

						if(s.equalsIgnoreCase(p)) {
							return BLUE;
						}
					}
				}

			}
			else {
				if(p != null) {
					for(String s : map.keySet()) {
						if(p.equalsIgnoreCase(map.get(s))) {
							return BLUE;
						}
					}
				}
			}
			return super.getBackground(element);
		}

	}

}
