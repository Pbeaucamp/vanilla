package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.fasd.i18N.LanguageText;

import xmldesigner.internal.TableModel;
import xmldesigner.xpath.Xpath;
import de.kupzog.ktable.KTable;

public class DialogXMLColumnBrowser extends Dialog {

	private Xpath xpath, xpathAll;
	private List<String> list, listAll;
	private String title;

	public DialogXMLColumnBrowser(Shell parentShell, String title, Xpath xpath, List<String> list, Xpath xpathAll, List<String> listAll) {
		super(parentShell);
		this.xpath = xpath;
		this.list = list;
		this.listAll = listAll;
		this.xpathAll = xpathAll;
		this.title = title;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayout(new GridLayout());
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem values = new TabItem(tabFolder, 0);
		values.setText(LanguageText.DialogXMLColumnBrowser_All);
		values.setControl(createValuesViewer(tabFolder));

		TabItem distincts = new TabItem(tabFolder, 1);
		distincts.setText(LanguageText.DialogXMLColumnBrowser_Distinct);
		distincts.setControl(createDistinctViewer(tabFolder));

		return parent;
	}

	private Control createValuesViewer(Composite parent) {
		Composite container = new CompViewXML(parent, SWT.NONE, xpathAll, listAll);

		return container;
	}

	private Control createDistinctViewer(Composite parent) {
		Composite container = new CompViewXML(parent, SWT.NONE, xpath, list);

		return container;
	}

	/**
	 * Create class CompViewXML where is the table
	 * 
	 * @author ANDREANI Emilien
	 * 
	 */
	public class CompViewXML extends Composite {

		private KTable table;
		private ArrayList<String> let = new ArrayList<String>();
		private List<String> list;

		private Xpath xpath;

		public CompViewXML(Composite parent, int style, Xpath xpath, List<String> list) {
			super(parent, style);
			this.list = list;
			this.xpath = xpath;
			createPartControl();
		}

		public void createPartControl() {
			setLayout(new GridLayout(1, true));
			this.setLayoutData(new GridData(GridData.FILL_BOTH));

			table = new KTable(this, SWT.FILL | SWT.V_SCROLL | SWT.MULTI);
			table.setLayoutData(new GridData(GridData.FILL_BOTH));
			try {
				table.setModel(createModel(list));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Creation of table's model
		 * 
		 * @return
		 * @throws Exception
		 */
		public TableModel createModel(List<String> list) throws Exception {
			List<String> listXquery = list;
			int val = listXquery.size() / xpath.getListCol().size();

			TableModel m = new TableModel(xpath.getListCol().size(), val + 1, 20, 50, this);
			for (int i = 0; i < val + 1; i++) {
				for (int j = 0; j < xpath.getListCol().size(); j++) {
					if (i == 0)
						m.setContentAt(j, i, xpath.getListCol().get(j));
					else {
						m.setContentAt(j, i, listXquery.get(j + (xpath.getListCol().size() * (i - 1))));
					}
				}
			}

			return m;
		}

		/**
		 * Update column's list of type integer
		 * 
		 */
		public void modifieLet() {
			for (int i = 0; i < xpath.getListCol().size(); i++) {
				if (!let.contains(xpath.getListCol().get(i))) {
					if (xpath.verifieInteger(xpath.getListCol().get(i)))
						let.add(xpath.getListCol().get(i));

				}
			}
			xpath.setColInteger(let);
		}

		/**
		 * Refresh model's table
		 * 
		 */
		public void refresh() {
			if (xpath.getListCol().size() == 0) {
				table.setModel(null);
				return;
			}

			try {
				table.setModel(null);
				table.setModel(createModel(list));
				modifieLet();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void initializeBounds() {
		this.getShell().setText(title);
		super.initializeBounds();
		this.getShell().setSize(400, 600);
	}

}
