package bpm.fd.design.ui.component.comment;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.design.ui.component.Messages;

public class CommentPage extends WizardPage {

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.comment.CommentPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.CommentPage_1;
	public static final String PAGE_DESCRIPTION = Messages.CommentPage_2;
	
	private Button chkLimitComments;
	private Button chkValidation;
	private Text txtLimit;
	
	private TableViewer tableParam;
	
	private List<ComponentParameter> selectedParameters = new ArrayList<ComponentParameter>();
	
	public CommentPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected CommentPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2,false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		chkLimitComments = new Button(mainComposite, SWT.CHECK);
		chkLimitComments.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		chkLimitComments.setText("Limit comment number");
		chkLimitComments.setSelection(false);
		
		chkLimitComments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(chkLimitComments.getSelection()) {
					txtLimit.setEnabled(true);
				}
				else {
					txtLimit.setEnabled(false);
				}
				super.widgetSelected(e);
			}
		});
		
		Label lblLimit = new Label(mainComposite, SWT.NONE);
		lblLimit.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
		lblLimit.setText("Comment number");
		
		txtLimit = new Text(mainComposite, SWT.BORDER);
		txtLimit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtLimit.setEnabled(false);
		
		chkValidation = new Button(mainComposite, SWT.CHECK);
		chkValidation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		chkValidation.setText("Is validation");
		chkValidation.setSelection(true);
		
		ToolBar toolbarParams = new ToolBar(mainComposite, SWT.NONE);
		toolbarParams.setLayout(new GridLayout());

		ToolItem btnAddCommentators = new ToolItem(toolbarParams, SWT.PUSH);
		btnAddCommentators.setToolTipText("Add parameter");
		btnAddCommentators.setText("Add");
		//btnAddCommentators.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD)); //$NON-NLS-1$
		btnAddCommentators.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				AddCommentParameterDialog dial = new AddCommentParameterDialog(getShell());
				if(dial.open() == AddCommentParameterDialog.OK) { 
					selectedParameters.add(dial.getParameter());

					tableParam.setInput(selectedParameters);
				}
				

			}
		});

		ToolItem btnRemoveCommentors = new ToolItem(toolbarParams, SWT.PUSH);
		btnRemoveCommentors.setToolTipText("Remove parameter");
		btnRemoveCommentors.setText("Remove");
		//btnRemoveCommentors.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL)); //$NON-NLS-1$
		btnRemoveCommentors.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) tableParam.getSelection();
				if (!ss.isEmpty()) {
					for (Object obj : ss.toList()) {
						if (obj instanceof String) {
							selectedParameters.remove(obj);
						}
					}

					tableParam.setInput(selectedParameters);
				}
			}
		});

		Table tableCommentators = new Table(mainComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableCommentators.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		tableParam = new TableViewer(tableCommentators);
		tableParam.getTable().setHeaderVisible(true);
		tableParam.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<ComponentParameter> parameters = (List<ComponentParameter>) inputElement;
				return parameters.toArray(new ComponentParameter[parameters.size()]);
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}
		});
		tableParam.setLabelProvider(new ITableLabelProvider() {

			@Override
			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return ((ComponentParameter) element).getName();
				case 1:
					return ((ComponentParameter) element).getLabel();
				case 2:
					return ((ComponentParameter) element).getDefaultValue();
				}
				return ""; //$NON-NLS-1$
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void dispose() {
			}

			@Override
			public void addListener(ILabelProviderListener listener) {
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});

		TableColumn name = new TableColumn(tableParam.getTable(), SWT.NONE);
		name.setText("Parameter name");
		name.setWidth(150);

		TableColumn promptValue = new TableColumn(tableParam.getTable(), SWT.NONE);
		promptValue.setText("Label");
		promptValue.setWidth(200);

		TableColumn defaultValue = new TableColumn(tableParam.getTable(), SWT.NONE);
		defaultValue.setText("Default value");
		defaultValue.setWidth(100);
		
		setControl(mainComposite);
	}

	public List<ComponentParameter> getParameters() {
		return selectedParameters;
	}
	
	public boolean asLimit() {
		return chkLimitComments.getSelection();
	}
	
	public int getLimit() {
		try {
			return Integer.parseInt(txtLimit.getText());
		} catch (NumberFormatException e) {
			return 5;
		}
	}
	
	public boolean isValidation() {
		return chkValidation.getSelection();
	}
}
