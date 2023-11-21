package bpm.sqldesigner.ui.wizard;

import java.util.Set;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.ui.i18N.Messages;

public class SearchWizard extends Wizard {

	private class SearchPage extends WizardPage {

		private Schema schema;
		private List list;
		private Set<String> set;

		public SearchPage(String pageName, Schema schema) {
			super(pageName);
			setTitle(Messages.SearchWizard_0);
			setDescription(Messages.SearchWizard_1);
			this.schema = schema;
		}

		
		public void createControl(Composite parent) {
			set = schema.getTables().keySet();

			Composite compositeMain = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout(2, false);
			layout.verticalSpacing = 50;
			compositeMain.setLayout(layout);
			compositeMain.setLayoutData(new GridData(GridData.FILL,
					GridData.FILL, false, false, 1, 1));

			Label label = new Label(compositeMain, SWT.NONE);
			label.setText(Messages.SearchWizard_2);

			final Text text = new Text(compositeMain, SWT.BORDER);
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			list = new List(compositeMain, SWT.BORDER | SWT.V_SCROLL);
			GridData grid = new GridData(GridData.FILL, GridData.FILL, false,
					true, 2, 1);
			list.setLayoutData(grid);

			text.addKeyListener(new KeyListener() {

				
				public void keyPressed(KeyEvent e) {
				}

				
				public void keyReleased(KeyEvent e) {

					list.removeAll();
					for (String tableName : set) {
						if (tableName.startsWith(text.getText()))
							list.add(tableName);
					}
					if (list.getItemCount() > 0) {
						list.select(0);
						list.showSelection();
					}

				}

			});

			setControl(compositeMain);

		}

		public void loadTables() {
			for (String tableName : set) {
				list.add(tableName);
			}
		}
	}

	private SearchPage searchPage;
	private String selection;

	public SearchWizard(Schema schema) {
		searchPage = new SearchPage("SearchTable", schema); //$NON-NLS-1$
		addPage(searchPage);
	}

	
	public boolean performFinish() {
		selection = searchPage.list.getSelection()[0];

		if (selection != null) {
			return true;
		} else
			return false;
	}

	public String getTableSelection() {
		return selection;
	}

	public void loadTables() {
		searchPage.loadTables();
	}

}
