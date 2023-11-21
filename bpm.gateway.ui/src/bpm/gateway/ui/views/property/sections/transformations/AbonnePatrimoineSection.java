package bpm.gateway.ui.views.property.sections.transformations;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.veolia.ConnectorAbonneXML;
import bpm.gateway.core.veolia.ConnectorPatrimoineXML;
import bpm.gateway.core.veolia.ConnectorXML;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class AbonnePatrimoineSection extends AbstractPropertySection {

	private ConnectorXML model;

	private Button btnIsInput, btnIsOds/* , btnBegin, btnEnd */;
	private Label lblQuery, lblQuery2;
	private Text txtQuery, txtQuery2;
	// private Text txtBegin, txtEnd;

//	private String beginDate;
//	private String endDate;

	public AbonnePatrimoineSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

//		final Shell shell = parent.getShell();

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));

		Composite compCheck = getWidgetFactory().createComposite(composite);
		compCheck.setLayout(new GridLayout(2, false));
		compCheck.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblIsInput = getWidgetFactory().createLabel(compCheck, Messages.AbonneSection_0);
		lblIsInput.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		btnIsInput = getWidgetFactory().createButton(compCheck, "", SWT.CHECK); //$NON-NLS-1$
		btnIsInput.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		btnIsInput.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent theEvent) {
				boolean isInput = ((Button) (theEvent.widget)).getSelection();
				model.setInput(isInput);

				updateUi(isInput);
			}
		});

		Label lblIsOds = getWidgetFactory().createLabel(compCheck, "Is ODS ?"); //$NON-NLS-1$
		lblIsOds.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		btnIsOds = getWidgetFactory().createButton(compCheck, "", SWT.CHECK); //$NON-NLS-1$
		btnIsOds.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		btnIsOds.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent theEvent) {
				boolean isODS = ((Button) (theEvent.widget)).getSelection();
				model.setODS(isODS);
			}
		});
		
		lblQuery = getWidgetFactory().createLabel(composite, Messages.AbonnePatrimoineSection_4);
		lblQuery.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));

		txtQuery = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtQuery.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		txtQuery.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (model != null) {
					model.setQuery(txtQuery.getText());
				}
			}
		});

		lblQuery2 = getWidgetFactory().createLabel(composite, Messages.AbonnePatrimoineSection_5);
		lblQuery2.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));

		txtQuery2 = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtQuery2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		txtQuery2.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (model != null) {
					model.setQuery2(txtQuery2.getText());
				}
			}
		});
		// txtQuery.addSelectionListener(new DateSelectionAdapter(shell,
		// txtBegin, false));

		// Label lblBegin = getWidgetFactory().createLabel(composite,
		// Messages.TimeSection_1);
		// lblBegin.setLayoutData(new GridData(GridData.BEGINNING,
		// GridData.BEGINNING, false, false));
		//
		//		btnBegin = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		// btnBegin.setLayoutData(new GridData(GridData.BEGINNING,
		// GridData.BEGINNING, false, false));
		// btnBegin.addSelectionListener (new DateSelectionAdapter(shell,
		// txtBegin, true));
		//
		//
		// Label lblEnd = getWidgetFactory().createLabel(composite,
		// Messages.TimeSection_3);
		// lblEnd.setLayoutData(new GridData(GridData.BEGINNING,
		// GridData.BEGINNING, false, false));
		//
		// txtEnd = getWidgetFactory().createText(composite, endDate,
		// SWT.BORDER);
		// txtEnd.setLayoutData(new GridData(GridData.BEGINNING,
		// GridData.BEGINNING, false, false));
		// txtEnd.addSelectionListener(new DateSelectionAdapter(shell, txtEnd,
		// false));
		//
		//		btnEnd = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		// btnEnd.setLayoutData(new GridData(GridData.BEGINNING,
		// GridData.BEGINNING, false, false));
		// btnEnd.addSelectionListener (new DateSelectionAdapter(shell, txtEnd,
		// false));
	}

	@Override
	public void refresh() {
		btnIsInput.setSelection(model.isInput());
		btnIsOds.setSelection(model.isODS());

//		String dB = model.getBeginDate();
//		String dE = model.getEndDate();
		String query = model.getQuery();
		String query2 = model.getQuery2();

//		if (dB != null && !dB.isEmpty()) {
//			this.beginDate = dB;
			// txtBegin.setText(beginDate);
//		}
//		else {
//			Calendar cal = Calendar.getInstance();
//			String year = cal.get(Calendar.YEAR) + ""; //$NON-NLS-1$
//			String month = cal.get(Calendar.MONTH) + 1 < 10 ? "0" + (cal.get(Calendar.MONTH) + 1) : cal.get(Calendar.MONTH) + 1 + ""; //$NON-NLS-1$ //$NON-NLS-2$
//			String day = cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH) + ""; //$NON-NLS-1$ //$NON-NLS-2$
//			this.beginDate = year + "-" + month + "-" + day; //$NON-NLS-1$ //$NON-NLS-2$
//			model.setBeginDate(beginDate);
			// txtBegin.setText(beginDate);
//		}

//		if (dE != null && !dE.isEmpty()) {
//			this.endDate = dE;
			// txtEnd.setText(endDate);
//		}
//		else {
//			Calendar cal = Calendar.getInstance();
//			String year = cal.get(Calendar.YEAR) + ""; //$NON-NLS-1$
//			String month = cal.get(Calendar.MONTH) + 1 < 10 ? "0" + (cal.get(Calendar.MONTH) + 1) : cal.get(Calendar.MONTH) + 1 + ""; //$NON-NLS-1$ //$NON-NLS-2$
//			String day = cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH) + ""; //$NON-NLS-1$ //$NON-NLS-2$
//			this.endDate = year + "-" + month + "-" + day; //$NON-NLS-1$ //$NON-NLS-2$
//			model.setEndDate(endDate);
			// txtEnd.setText(endDate);
//		}
		
		if (model instanceof ConnectorAbonneXML) {
			lblQuery.setText(Messages.AbonnePatrimoineSection_4);
			lblQuery2.setText(Messages.AbonnePatrimoineSection_5);
			lblQuery2.setVisible(true);
			txtQuery2.setVisible(true);
		}
		else if (model instanceof ConnectorPatrimoineXML) {
			lblQuery.setText(Messages.AbonnePatrimoineSection_6);
			lblQuery2.setVisible(false);
			txtQuery2.setVisible(false);
		}
		
		if (query != null) {
			txtQuery.setText(query);
		}
		
		if (query2 != null) {
			txtQuery2.setText(query2);
		}

		updateUi(model.isInput());
	}

	private void updateUi(boolean isInput) {
		// txtBegin.setEnabled(!isInput);
		// txtEnd.setEnabled(!isInput);
		btnIsOds.setEnabled(!isInput);
		txtQuery.setEnabled(!isInput);
		txtQuery2.setEnabled(!isInput);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.model = (ConnectorXML) ((Node) ((NodePart) input).getModel()).getGatewayModel();
	}

//	private class DateSelectionAdapter extends SelectionAdapter {
//
//		private Shell shell;
//		private Text txtDate;
//		private boolean isDateBegin;
//
//		public DateSelectionAdapter(Shell shell, Text txtDate, boolean isDateBegin) {
//			this.shell = shell;
//			this.txtDate = txtDate;
//			this.isDateBegin = isDateBegin;
//		}
//
//		@Override
//		public void widgetSelected(SelectionEvent e) {
//			final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM);
//			dialog.setLayout(new GridLayout());
//			dialog.setSize(300, 280);
//
//			final DateTime calendar = new DateTime(dialog, SWT.CALENDAR | SWT.BORDER);
//
//			Button ok = new Button(dialog, SWT.PUSH);
//			ok.setText("OK"); //$NON-NLS-1$
//			ok.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//			ok.addSelectionListener(new SelectionAdapter() {
//				public void widgetSelected(SelectionEvent e) {
//					Calendar cal = Calendar.getInstance();
//					cal.set(calendar.getYear(), calendar.getMonth(), calendar.getDay());
//					String year = cal.get(Calendar.YEAR) + ""; //$NON-NLS-1$
//					String month = cal.get(Calendar.MONTH) + 1 < 10 ? "0" + (cal.get(Calendar.MONTH) + 1) : cal.get(Calendar.MONTH) + 1 + ""; //$NON-NLS-1$ //$NON-NLS-2$
//					String day = cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH) + ""; //$NON-NLS-1$ //$NON-NLS-2$
//					String date = year + "-" + month + "-" + day; //$NON-NLS-1$ //$NON-NLS-2$
//					if (isDateBegin) {
//						beginDate = date;
//						model.setBeginDate(beginDate);
//					}
//					else {
//						endDate = date;
//						model.setEndDate(endDate);
//					}
//					txtDate.setText(date);
//					dialog.close();
//				}
//			});
//			dialog.setDefaultButton(ok);
//			dialog.open();
//		}
//	}
}
