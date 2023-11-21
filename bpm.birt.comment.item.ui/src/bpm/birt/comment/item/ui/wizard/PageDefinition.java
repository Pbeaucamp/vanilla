package bpm.birt.comment.item.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.core.IModuleModel;
import org.eclipse.birt.report.model.api.metadata.IElementPropertyDefn;
import org.eclipse.birt.report.model.core.ContainerSlot;
import org.eclipse.birt.report.model.core.DesignElement;
import org.eclipse.birt.report.model.elements.CascadingParameterGroup;
import org.eclipse.birt.report.model.elements.Parameter;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import bpm.birt.comment.item.core.reportitem.CommentItem;
import bpm.birt.comment.item.core.reportitem.CommentParameter;
import bpm.birt.comment.item.ui.Activator;
import bpm.birt.comment.item.ui.dialogs.DialogPickupParameter;
import bpm.birt.comment.item.ui.icons.Icons;

public class PageDefinition extends WizardPage {
	private CommentItem commentItem;

	private Button btnLimit, radioNormal, radioValidation;
	private Text txtLabel, txtShowAll;
	private TableViewer viewerParams;

	private List<String> parameters = new ArrayList<String>();
	private List<CommentParameter> selectedParameters;

	public PageDefinition(String pageName, CommentItem commentItem, ExtendedItemHandle handle) {
		super(pageName);
		this.commentItem = commentItem;

		int index = handle.getContainer().getModule().getSlotIndex(IModuleModel.PARAMETER_SLOT);
		if (index >= 0) {
			ContainerSlot slot = handle.getContainer().getModule().getSlot(1);
			if (slot != null) {
				for (DesignElement el : slot.getContents()) {
					if (el instanceof Parameter) {
						parameters.add(el.getName());
					}
					else if (el instanceof CascadingParameterGroup) {

						CascadingParameterGroup gr = (CascadingParameterGroup) el;
						if (gr.getSlot(0) != null) {
							ContainerSlot grSlot = gr.getSlot(0);
							for (DesignElement grEl : grSlot.getContents()) {
								if (grEl instanceof Parameter) {
									parameters.add(grEl.getName());
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblLabel = new Label(main, SWT.NONE);
		lblLabel.setText("Label");
		lblLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));

		txtLabel = new Text(main, SWT.BORDER);
		txtLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtLabel.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
		});

		btnLimit = new Button(main, SWT.CHECK);
		btnLimit.setText("Limiter le nombre de commentaires");
		btnLimit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		btnLimit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtShowAll.setEnabled(btnLimit.getSelection());
			}
		});

		Label lblShow = new Label(main, SWT.NONE);
		lblShow.setText("Nombre de commentaires (affichés)");
		lblShow.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));

		txtShowAll = new Text(main, SWT.BORDER);
		txtShowAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtShowAll.setText("5");
		txtShowAll.setEnabled(false);

		radioNormal = new Button(main, SWT.RADIO);
		radioNormal.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		radioNormal.setText("Normal comment");
		radioNormal.setSelection(true);

		radioValidation = new Button(main, SWT.RADIO);
		radioValidation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		radioValidation.setText("Validation comment");

		ToolBar toolbarParams = new ToolBar(main, SWT.NONE);
		toolbarParams.setLayout(new GridLayout());

		ToolItem btnAddCommentators = new ToolItem(toolbarParams, SWT.PUSH);
		btnAddCommentators.setToolTipText("Add commentators");
		btnAddCommentators.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD)); //$NON-NLS-1$
		btnAddCommentators.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickupParameter d = new DialogPickupParameter(getShell(), parameters);
				if (d.open() == DialogPickupParameter.OK) {
					if (selectedParameters == null) {
						selectedParameters = new ArrayList<CommentParameter>();
					}

					String paramName = d.getSelectedParameter();
					String defaultValue = d.getSelectedDefaultValue();
					String prompt = d.getSelectedPrompt();
					selectedParameters.add(new CommentParameter(paramName, defaultValue, prompt));

					viewerParams.setInput(selectedParameters);
				}
			}
		});

		ToolItem btnRemoveCommentors = new ToolItem(toolbarParams, SWT.PUSH);
		btnRemoveCommentors.setToolTipText("Remove commentators");
		btnRemoveCommentors.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL)); //$NON-NLS-1$
		btnRemoveCommentors.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewerParams.getSelection();
				if (!ss.isEmpty()) {
					for (Object obj : ss.toList()) {
						if (obj instanceof String) {
							selectedParameters.remove(obj);
						}
					}

					viewerParams.setInput(selectedParameters);
				}
			}
		});

		Table tableCommentators = new Table(main, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableCommentators.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		viewerParams = new TableViewer(tableCommentators);
		viewerParams.getTable().setHeaderVisible(true);
		viewerParams.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<CommentParameter> parameters = (List<CommentParameter>) inputElement;
				return parameters.toArray(new CommentParameter[parameters.size()]);
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}
		});
		viewerParams.setLabelProvider(new ITableLabelProvider() {

			@Override
			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return ((CommentParameter) element).getName();
				case 1:
					return ((CommentParameter) element).getPrompt();
				case 2:
					return ((CommentParameter) element).getDefaultValue();
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

		TableColumn name = new TableColumn(viewerParams.getTable(), SWT.NONE);
		name.setText("Parameter name");
		name.setWidth(150);

		TableColumn promptValue = new TableColumn(viewerParams.getTable(), SWT.NONE);
		promptValue.setText("Prompt");
		promptValue.setWidth(200);

		TableColumn defaultValue = new TableColumn(viewerParams.getTable(), SWT.NONE);
		defaultValue.setText("Default value");
		defaultValue.setWidth(100);

		if (commentItem != null) {
			txtLabel.setText(commentItem.getLabel() != null ? commentItem.getLabel() : "");
			btnLimit.setSelection(commentItem.getLimit());
			txtShowAll.setText(String.valueOf(commentItem.getNbComment()));
			txtShowAll.setEnabled(commentItem.getLimit());

			radioNormal.setSelection(commentItem.getTypeComment() != 0);
			radioValidation.setSelection(commentItem.getTypeComment() == 0);

			List<CommentParameter> parameters = commentItem.getParametersList();
			viewerParams.setInput(parameters != null ? parameters : new ArrayList<CommentParameter>());
		}

		if (parameters == null || parameters.isEmpty()) {
			toolbarParams.setVisible(false);
			viewerParams.getTable().setVisible(false);
		}

		setControl(main);
	}

	@Override
	public boolean isPageComplete() {
		return !txtLabel.getText().isEmpty();
	}

	public void defineCommentItem() {
		String label = txtLabel.getText();
		boolean limit = btnLimit.getSelection();
		int nbComment = 0;
		try {
			nbComment = Integer.parseInt(txtShowAll.getText());
		} catch (Exception e) {
			nbComment = 0;
		}
		boolean isValidation = radioValidation.getSelection();

		try {
			commentItem.setLabel(label);
			commentItem.setLimit(limit);
			commentItem.setNbComment(nbComment);
			commentItem.setTypeComment(isValidation ? 0 : 1);
			commentItem.setParametersList(selectedParameters);
		} catch (SemanticException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
