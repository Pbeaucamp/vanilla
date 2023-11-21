package bpm.sqldesigner.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.utils.compare.Compare;
import bpm.sqldesigner.api.utils.compare.report.Report;
import bpm.sqldesigner.api.utils.migration.CanApplyChanges;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.view.RequestsView;

public class CompareWizard extends Wizard {

	private class ComparePage extends WizardPage {

		private Report report;
		List<Report> reportList = new ArrayList<Report>();
		Node firstElement;
		Node secondElement;
		private Table tableMigration;
		private ArrayList<CanApplyChanges> listApplyChanges;
		private Node fromNode;
		private Node toNode;

		public ComparePage(String pageName, Node firstElement,
				Node secondElement) {
			super(pageName);
			setTitle(Messages.CompareWizard_0);
			setDescription(Messages.CompareWizard_1);

			this.firstElement = firstElement;
			this.secondElement = secondElement;
			report = Compare.compareNodes(firstElement, secondElement);
			reportList = report.getReportsChanges(reportList);
		}

		
		public void createControl(Composite parent) {
			Composite compositeMain = new Composite(parent, SWT.NONE);
			compositeMain.setLayout(new GridLayout(1, false));

			Group groupReport = new Group(compositeMain, SWT.NONE);
			groupReport.setLayout(new GridLayout(1, false));
			groupReport.setText(Messages.CompareWizard_2);

			Label labelScore = new Label(groupReport, SWT.None);
			labelScore.setText(Messages.CompareWizard_3 + report.getScore() + Messages.CompareWizard_4);
			labelScore.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
					false, false, 1, 1));

			Group groupMigration = new Group(compositeMain, SWT.NONE);
			groupMigration.setLayout(new GridLayout(3, false));
			groupMigration.setLayoutData(new GridData(GridData.FILL_BOTH));
			groupMigration.setText(Messages.CompareWizard_5);

			Label labelTarget = new Label(groupMigration, SWT.NONE);
			labelTarget.setText(Messages.CompareWizard_6);

			final Combo comboTarget = new Combo(groupMigration, SWT.NONE);
			if (!(firstElement instanceof DatabaseCluster)) {
				comboTarget.add(firstElement.getName() + Messages.CompareWizard_7
						+ firstElement.getClusterName());
				comboTarget.add(secondElement.getName() + Messages.CompareWizard_8
						+ secondElement.getClusterName());
			} else {

				comboTarget.add(firstElement.getName());
				comboTarget.add(secondElement.getName());

			}

			Button buttonTarget = new Button(groupMigration, SWT.PUSH);
			buttonTarget.setText(Messages.CompareWizard_9);
			buttonTarget.addSelectionListener(new SelectionListener() {

				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {

					int index = comboTarget.getSelectionIndex();

					if (index != -1) {
						tableMigration.removeAll();
						if (index == 0) {
							toNode = firstElement;
							fromNode = secondElement;
						} else {
							toNode = secondElement;
							fromNode = firstElement;
						}

						listApplyChanges = new ArrayList<CanApplyChanges>();
						for (Report report : reportList) {
							List<RequestStatement> listRequests = report
									.getRequests(toNode, fromNode);
							if (listRequests != null) {
								for (RequestStatement request : listRequests) {
									TableItem item = new TableItem(
											tableMigration, SWT.NONE);
									item.setChecked(true);
									item.setText(new String[] { request
											.getRequestString() });

									listApplyChanges.add(report
											.getApplyChanges());
								}
							} else
								System.err.println(Messages.CompareWizard_10
										+ report.toString()
										+ " / CompareWizard"); //$NON-NLS-1$

						}

						tableMigration.getParent().layout();
						tableMigration.getParent().getParent().layout();
						tableMigration.getParent().getParent().getParent()
								.layout();
					}
				}

			});

			tableMigration = new Table(groupMigration, SWT.BORDER | SWT.CHECK
					| SWT.MULTI | SWT.FULL_SELECTION);
			final GridData gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan = 3;
			tableMigration.setLayoutData(gd);
			tableMigration.setHeaderVisible(true);

			final TableColumn columnRequest = new TableColumn(tableMigration,
					SWT.LEFT);
			columnRequest.setText(Messages.CompareWizard_12);
			columnRequest.setWidth(450);

			Button buttonSelectAll = new Button(groupMigration, SWT.PUSH);
			buttonSelectAll.setText(Messages.CompareWizard_13);
			buttonSelectAll.addSelectionListener(new SelectionListener() {

				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {
					for (TableItem item : tableMigration.getItems()) {
						item.setChecked(true);
					}
				}
			});

			Button buttonDeselectAll = new Button(groupMigration, SWT.PUSH);
			buttonDeselectAll.setText(Messages.CompareWizard_14);
			buttonDeselectAll.addSelectionListener(new SelectionListener() {

				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {
					for (TableItem item : tableMigration.getItems()) {
						item.setChecked(false);
					}
				}
			});

			setControl(compositeMain);
		}

		public List<RequestStatement> getCheckedStatements() {
			List<RequestStatement> listReq = new ArrayList<RequestStatement>();

			int i = 0;
			for (TableItem item : tableMigration.getItems()) {
				if (item.getChecked()) {
					RequestStatement req = new RequestStatement();
					req.setRequestString(item.getText());
					listReq.add(req);
					CanApplyChanges changes = listApplyChanges.get(i);
					if (changes != null)
						changes.applyChanges();
				}
				i++;
			}
			return listReq;
		}
	}

	public CompareWizard(Node firstElement, Node secondElement) {
		addPage(new ComparePage("ComparePage", firstElement, secondElement)); //$NON-NLS-1$
	}

	
	public boolean performFinish() {
		ComparePage page = ((ComparePage) getPage("ComparePage")); //$NON-NLS-1$
		DatabaseCluster cluster = page.fromNode.getCluster();

		if (cluster.getDatabaseConnection() != null) {
			List<RequestStatement> listReq = page.getCheckedStatements();

			RequestsView view = (RequestsView) Activator.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView(RequestsView.ID);

			view.getTab(cluster).addAll(listReq, page.toNode);

			return true;
		} else {
			MessageDialog.openError(getShell(), Messages.CompareWizard_17,
					Messages.CompareWizard_18);

			return false;

		}
	}

}