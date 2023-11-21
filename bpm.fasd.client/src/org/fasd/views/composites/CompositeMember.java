package org.fasd.views.composites;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.security.SecurityDim;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;

public class CompositeMember extends Composite {

	private Combo hiera;
	private List<OLAPHierarchy> list = new ArrayList<OLAPHierarchy>();
	private List<Combo> levels;
	private SecurityDim secuDim;
	private String member;

	public CompositeMember(Composite parent, int style, SecurityDim sd) {
		super(parent, style);
		this.secuDim = sd;
		this.setLayout(new GridLayout(2, false));
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.CompositeMember_0);

		hiera = new Combo(this, SWT.BORDER);
		hiera.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (OLAPHierarchy h : secuDim.getDim().getHierarchies()) {
			list.add(h);
			hiera.add(h.getName());
		}

		hiera.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				levels = new ArrayList<Combo>();
				member = "[" + hiera.getText() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
				for (OLAPLevel lvl : list.get(hiera.getSelectionIndex()).getLevels()) {
					Label l = new Label(CompositeMember.this, SWT.NONE);
					l.setLayoutData(new GridData());
					l.setText(lvl.getName());

					final Combo cbo = new Combo(CompositeMember.this, SWT.BORDER);
					cbo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					cbo.setItems(getLevelValues(lvl));
					cbo.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							member += ".[" + cbo.getText() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
						}
					});
					levels.add(cbo);
				}
				CompositeMember.this.getParent().getParent().pack();
			}

			private String[] getLevelValues(OLAPLevel l) {
				List<String> result = new ArrayList<String>();
				DataObjectItem column = l.getItem();
				DataSourceConnection sock = column.getParent().getDataSource().getDriver();
				String query = "SELECT DISTINCT " + column.getName() + " FROM " + column.getParent().getPhysicalName() + ";"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				VanillaPreparedStatement stmt;
				try {
					if (sock.getConnection() == null) {
						sock.connectAll();
					}

					stmt = sock.getConnection().getConnection().createStatement();
					ResultSet rs = stmt.executeQuery(query);

					while (rs.next()) {
						result.add(rs.getString(1));
					}
					rs.close();
					stmt.close();
					ConnectionManager.getInstance().returnJdbcConnection(sock.getConnection().getConnection());
				} catch (SQLException e) {
					MessageDialog.openError(CompositeMember.this.getShell(), LanguageText.CompositeMember_8, LanguageText.CompositeMember_9);
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					MessageDialog.openError(CompositeMember.this.getShell(), LanguageText.CompositeMember_10, LanguageText.CompositeMember_11);
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					MessageDialog.openError(CompositeMember.this.getShell(), LanguageText.CompositeMember_12, LanguageText.CompositeMember_13);
					e.printStackTrace();
				} catch (Exception e) {
					MessageDialog.openError(CompositeMember.this.getShell(), LanguageText.CompositeMember_14, ""); //$NON-NLS-1$
					e.printStackTrace();
				}

				return result.toArray(new String[result.size()]);
			}

		});

	}

	public String getMember() {
		return member;
	}
}
