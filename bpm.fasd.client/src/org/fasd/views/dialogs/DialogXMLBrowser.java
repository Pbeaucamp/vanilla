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
import org.fasd.i18N.LanguageText;

import xmldesigner.internal.TableModel;
import xmldesigner.xpath.Xpath;
import de.kupzog.ktable.KTable;

public class DialogXMLBrowser extends Dialog {

	@Override
	protected Control createDialogArea(Composite parent) {
		new CompViewXML(parent, SWT.NULL);

		return parent;
	}

	private Xpath xpath;
	private List<String> list;

	public DialogXMLBrowser(Shell parentShell, Xpath xpath, List<String> list) {
		super(parentShell);
		this.xpath = xpath;
		this.list = list;
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

		public CompViewXML(Composite parent, int style) {
			super(parent, style);
			createPartControl();
		}

		public void createPartControl() {
			setLayout(new GridLayout(1, true));
			this.setLayoutData(new GridData(GridData.FILL_BOTH));

			table = new KTable(this, SWT.FILL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.HIDE_SELECTION);
			table.setLayoutData(new GridData(GridData.FILL_BOTH));
			try {
				table.setModel(createModel());
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
		public TableModel createModel() throws Exception {
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
				table.setModel(createModel());
				modifieLet();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogXMLBrowser_Tbl_Brow);
		this.getShell().setSize(800, 600);
	}

}
