package org.fasd.views.dialogs;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.ServerConnection;
import org.fasd.security.SecurityGroup;
import org.fasd.security.View;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeMember;
import org.fasd.utils.trees.TreeParent;
import org.freeolap.FreemetricsPlugin;

import xmldesigner.internal.DimensionTree;
import xmldesigner.parse.XMLParser;
import xmldesigner.parse.item.DataXML;
import xmldesigner.xpath.Xpath;
import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;

public class DialogDimSecu extends Dialog {

	private Composite container;
	private Text name, desc;
	private ListViewer list;
	private TreeViewer tree;
	private Combo secuGroups, serverCbo, hierarchy;
	private Button fullAccess, access;
	private HashMap<String, SecurityGroup> group = new HashMap<String, SecurityGroup>();
	private HashMap<String, ServerConnection> servers = new HashMap<String, ServerConnection>();
	private HashMap<String, OLAPHierarchy> hieras = new HashMap<String, OLAPHierarchy>();

	private OLAPDimension dim;
	private View dimView;

	public DialogDimSecu(Shell parentShell, OLAPDimension dim) {
		super(parentShell);
		this.dim = dim;
	}

	public DialogDimSecu(Shell parentShell, View dimView) {
		super(parentShell);
		this.dimView = dimView;
		this.dim = dimView.getParent().getDim();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.DialogDimSecu_Name);

		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.DialogDimSecu_MemBer_Restriction);

		Label l1 = new Label(container, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.DialogDimSecu_Descr);

		desc = new Text(container, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		list = new ListViewer(container, SWT.BORDER);
		list.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 8));

		Label l5 = new Label(container, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.DialogDimSecu_Conn);

		for (ServerConnection c : FreemetricsPlugin.getDefault().getFAModel().getSecurity().getServerConnections()) {
			servers.put(c.getName(), c);
		}

		serverCbo = new Combo(container, SWT.NONE | SWT.READ_ONLY);
		serverCbo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		serverCbo.setItems(servers.keySet().toArray(new String[servers.size()]));
		serverCbo.select(0);

		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.DialogDimSecu_SecurityGP);

		for (SecurityGroup s : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getSecurityGroups()) {
			if (group.get(s.getName()) == null)
				group.put(s.getName(), s);
		}

		secuGroups = new Combo(container, SWT.NONE | SWT.READ_ONLY);
		secuGroups.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		secuGroups.setItems(group.keySet().toArray(new String[group.size()]));
		secuGroups.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				list.getList().removeAll();
			}

		});

		Label l6 = new Label(container, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(LanguageText.DialogDimSecu_Hiera);

		hierarchy = new Combo(container, SWT.NONE | SWT.READ_ONLY);
		hierarchy.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		for (OLAPHierarchy h : dim.getHierarchies()) {
			hieras.put(h.getName(), h);
		}
		hierarchy.setItems(hieras.keySet().toArray(new String[hieras.keySet().size()]));
		hierarchy.select(0);

		fullAccess = new Button(container, SWT.RADIO);
		fullAccess.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		fullAccess.setText(LanguageText.DialogDimSecu_Full_Acces);

		access = new Button(container, SWT.RADIO);
		access.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		access.setText(LanguageText.DialogDimSecu_Allow_Acces);

		Label l0 = new Label(container, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		l0.setText(LanguageText.DialogDimSecu_Dim_Mem);

		tree = new TreeViewer(container, SWT.BORDER);
		tree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tree.setLabelProvider(new TreeLabelProvider());
		tree.setContentProvider(new TreeContentProvider());
		tree.setInput(createModel(dim));

		setDnd();

		if (dimView != null)
			fillData();

		return parent;
	}

	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogDimSecu_Dim_Secu);

		this.getShell().setSize(800, 600);
	}

	private void fillData() {
		name.setText(dimView.getName());
		desc.setText(dimView.getName());

		if (dimView.isAllowFullAccess()) {
			access.setSelection(false);
			fullAccess.setSelection(true);
		} else {
			access.setSelection(true);
			fullAccess.setSelection(false);
		}

		secuGroups.setText(dimView.getGroup().getName());

		for (String s : dimView.getMembers())
			list.add(s);
	}

	private TreeParent createModel(OLAPDimension dim) {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		TreeDim td = new TreeDim(dim);

		for (OLAPHierarchy h : dim.getHierarchies()) {
			if (h.isSnowFlakes()) {
				MessageDialog.openInformation(this.getShell(), LanguageText.DialogDimSecu_Information, LanguageText.DialogDimSecu_DimBrow_No_Snowflakes);
				this.close();
				return null;
			}
			TreeHierarchy th = new TreeHierarchy(h);

			if (h.getLevels().size() > 0) {
				OLAPLevel l0 = h.getLevels().get(0);
				ArrayList<String> list = new ArrayList<String>();

				try {
					if (l0.getItem().getParent().getDataSource().getDriver().getType().equals("XML")) { //$NON-NLS-1$
						DataSourceConnection sock = l0.getItem().getParent().getDataSource().getDriver();
						DataObjectItem item = l0.getItem();

						String url = "file:///" + sock.getTransUrl(); //$NON-NLS-1$

						XMLParser parser = new XMLParser(url);
						parser.parser();
						DataXML dtd = parser.getDataXML();
						Xpath xpath = new Xpath(sock.getTransUrl(), dtd.getRoot().getElement(0).getName());

						DimensionTree model = new DimensionTree(dtd);
						xmldesigner.internal.TreeParent rr = model.createModel();
						xpath.setListHiera(createHiera(rr));
						xpath.addCol(item.getOrigin());

						String distinctQuery = "for $i in distinct-values(doc('" + sock.getTransUrl().replace("\\", "/") + "')//" + item.getOrigin() + ")\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						distinctQuery += "return\n"; //$NON-NLS-1$
						distinctQuery += "<" + dtd.getRoot().getElement(0).getName() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
						distinctQuery += "<" + item.getOrigin() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
						distinctQuery += "{$i}\n"; //$NON-NLS-1$
						distinctQuery += "</" + item.getOrigin() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
						distinctQuery += "</" + dtd.getRoot().getElement(0).getName() + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
						try {
							xpath.executeXquery(distinctQuery);
							xpath.modifieSortie();

							XMLParser pars = new XMLParser("Temp/sortie.xml"); //$NON-NLS-1$
							pars.parser();
							DataXML dtd2 = pars.getDataXML();
							list = xpath.listXquery(dtd2.getRoot());
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						l0.getItem().getParent().getDataSource().getDriver().connectAll();

						VanillaJdbcConnection con = l0.getItem().getParent().getDataSource().getDriver().getConnection().getConnection();
						VanillaPreparedStatement stmt = con.createStatement();
						ResultSet rs = stmt.executeQuery("SELECT DISTINCT " + l0.getItem().getOrigin() + " FROM " + l0.getItem().getParent().getPhysicalName()); //$NON-NLS-1$ //$NON-NLS-2$

						while (rs.next()) {
							if (rs.getString(1) != null)
								list.add(rs.getString(1));
						}
						rs.close();
						stmt.close();
						ConnectionManager.getInstance().returnJdbcConnection(con);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (String s : list) {
					TreeMember tm = new TreeMember(s, l0);
					th.addChild(tm);
					tm.createChilds();
				}

			}

			td.addChild(th);
		}
		root.addChild(td);
		return root;
	}

	private ArrayList<String> createHiera(xmldesigner.internal.TreeParent t) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(t.getName());
		for (int i = 0; i < t.getChildren().length; i++) {
			add(list, createHiera((xmldesigner.internal.TreeParent) t.getChildren(i)));
		}
		return list;
	}

	private ArrayList<String> add(ArrayList<String> list1, ArrayList<String> list2) {
		for (int i = 0; i < list2.size(); i++) {
			list1.add(list2.get(i));
		}
		return list1;
	}

	private String getMemberName(TreeMember m) {
		String buf = "[" + m.getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		boolean isRoot = false;
		TreeParent current = m;

		while (!isRoot) {
			current = current.getParent();
			if (current instanceof TreeDim) {
				isRoot = true;
			}
			if (!(current instanceof TreeHierarchy)) {
				if (!current.getName().trim().equals("")) //$NON-NLS-1$
					buf = "[" + current.getName() + LanguageText.DialogDimSecu_45 + buf; //$NON-NLS-1$
			}

		}

		return buf;
	}

	private void setDnd() {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		tree.addDragSupport(ops, transfers, new DragSourceListener() {

			public void dragFinished(DragSourceEvent event) {

			}

			public void dragSetData(DragSourceEvent event) {
				StructuredSelection ss = (StructuredSelection) tree.getSelection();
				Object o = ss.getFirstElement();
				if (o instanceof TreeMember) {
					event.data = getMemberName((TreeMember) o);
				}

			}

			public void dragStart(DragSourceEvent event) {
			}

		});

		DropTarget dropTable = new DropTarget(list.getList(), ops);
		dropTable.setTransfer(transfers);
		dropTable.addDropListener(new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

			public void dragOver(DropTargetEvent event) {
				//
			}

			public void dragOperationChanged(DropTargetEvent event) {
				// nothing
			}

			public void dragLeave(DropTargetEvent event) {
				// nothing
			}

			public void dropAccept(DropTargetEvent event) {
				// todo
			}

			public void drop(DropTargetEvent event) {
				String buf = ((String) event.data);

				list.add(buf);
			}
		});
	}

	public View getDimensionView() {
		return dimView;
	}

	@Override
	protected void okPressed() {
		if (dimView == null)
			dimView = new View(hieras.get(hierarchy.getText()));

		dimView.setName(name.getText());
		dimView.setDesc(desc.getText());
		dimView.setHierarchy(hieras.get(hierarchy.getText()));
		for (String s : list.getList().getItems()) {
			boolean everIn = false;
			for (String ss : dimView.getMembers()) {
				if (ss.equals(s)) {
					everIn = true;
					break;
				}
			}
			if (!everIn)
				dimView.addMember(s);

		}

		dimView.setAllowAccess(access.getSelection());
		dimView.setAllowFullAccess(fullAccess.getSelection());

		dimView.setGroup(group.get(secuGroups.getText()));

		super.okPressed();
	}

}
