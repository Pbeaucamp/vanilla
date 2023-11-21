package bpm.sqldesigner.ui.editor;

import java.util.ArrayList;

import org.eclipse.gef.internal.ui.palette.editparts.ToolEntryEditPart;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.query.SQLDesignerComposite;
import bpm.sqldesigner.query.model.filter.ColumnFilter;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.popup.CanValidate;
import bpm.sqldesigner.ui.popup.ChooseNameShell;
import bpm.sqldesigner.ui.utils.ModelsAdaptor;
import bpm.sqldesigner.ui.view.RequestsView;
import bpm.sqldesigner.ui.view.tab.TabRequests;

public class CreateViewPaletteListener implements ISelectionChangedListener,
		CanValidate {

	private SQLDesignGraphicalEditor editor;
	private PaletteViewer paletteViewer;
	private Text text;
	private Shell shellName;
	private Shell shell;
	private SQLView view;

	public CreateViewPaletteListener(
			SQLDesignGraphicalEditor designGraphicalEditor,
			PaletteViewer paletteViewer) {
		editor = designGraphicalEditor;
		this.paletteViewer = paletteViewer;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		if (!editor.isSchemaLoaded()) {
			MessageDialog.openInformation(editor.getSite().getShell(), Messages.CreateViewPaletteListener_0,
					Messages.CreateViewPaletteListener_1);
			return;
		}
		StructuredSelection selection = (StructuredSelection) event
				.getSelection();

		ToolEntryEditPart toolEntry = null;
		try {
			toolEntry = (ToolEntryEditPart) selection.getFirstElement();
		} catch (Exception e) {
			return;
		}

		PaletteEntry paletteEntry = (PaletteEntry) toolEntry.getModel();

		if (editor.getSchema().getDatabaseConnection() == null
				&& (paletteEntry.getLabel().equals(Messages.CreateViewPaletteListener_7)
						|| paletteEntry.getLabel().equals(Messages.CreateViewPaletteListener_4) || paletteEntry
						.getLabel().equals(Messages.CreateViewPaletteListener_3))) {
			MessageDialog.openInformation(editor.getSite().getShell(), Messages.CreateViewPaletteListener_5,
					Messages.CreateViewPaletteListener_6);
			paletteViewer.deselectAll();
			paletteViewer.setActiveTool(paletteViewer.getPaletteRoot()
					.getDefaultEntry());
			return;
		}

		if (paletteEntry.getLabel().equals(Messages.CreateViewPaletteListener_2)) {

			paletteViewer.deselectAll();
			paletteViewer.setActiveTool(paletteViewer.getPaletteRoot()
					.getDefaultEntry());

			shell = new Shell(Activator.getDefault().getWorkbench()
					.getDisplay());
			final SQLDesignerComposite queryDesigner = new SQLDesignerComposite(
					shell, SWT.NONE, ModelsAdaptor
							.getSchema(editor.getSchema()),
					new ArrayList<ColumnFilter>());

			Composite compoBtn = new Composite(shell, SWT.NONE);
			compoBtn.setLayout(new GridLayout(2, true));
			compoBtn
					.setLayoutData(new GridData(
							GridData.HORIZONTAL_ALIGN_CENTER
									| GridData.FILL_HORIZONTAL));

			Button okButton = new Button(compoBtn, SWT.PUSH);
			okButton.setText(Messages.CreateViewPaletteListener_8);
			okButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			final CreateViewPaletteListener _this = this;
			okButton.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					String query = queryDesigner.getQuery();

					view = new SQLView();
					view.setSchema(editor.getSchema());
					view.setValue(query);
					editor.getSchema().addView(view);

					ChooseNameShell popupName = new ChooseNameShell(shell,
							SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL, _this);

					shellName = popupName.getShell();
					shellName.setText(Messages.CreateViewPaletteListener_9);

					text = popupName.getText();

					shellName.pack();
					shellName.open();
				}
			});

			Button cancelButton = new Button(compoBtn, SWT.PUSH);
			cancelButton.setText(Messages.CreateViewPaletteListener_10);
			cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			cancelButton.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					shell.dispose();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			shell.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
					true, false));
			shell.setLayout(new GridLayout(1, false));
			shell.setSize(800, 700);

			shell.setText(Messages.CreateViewPaletteListener_11);
			shell.open();

		}
	}

	public void validate() {
		if (view.getSchema().getView(text.getText()) == null) {
			view.setCommit(false);
			view.setName(text.getText());
			view.setNotFullLoaded(false);

			TabRequests tab = ((RequestsView) Activator.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView(RequestsView.ID)).getTab(editor.getSchema()
					.getCluster());
			tab.nodeCreated(view);

		} else {
			MessageDialog.openError(shell, Messages.CreateViewPaletteListener_12, Messages.CreateViewPaletteListener_13);
		}
		shell.dispose();
		shellName.dispose();
	}

}
