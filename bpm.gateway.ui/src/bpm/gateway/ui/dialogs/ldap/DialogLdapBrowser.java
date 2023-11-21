package bpm.gateway.ui.dialogs.ldap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.springframework.ldap.core.NameAwareAttribute;
import org.springframework.ldap.core.NameAwareAttributes;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.ldap.LdapConnection;
import bpm.gateway.core.server.ldap.LdapHelper;
import bpm.gateway.core.server.ldap.LdapServer;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.viewer.TreeContentProvider;
import bpm.gateway.ui.viewer.TreeLabelProvider;
import bpm.gateway.ui.viewer.TreeParent;

public class DialogLdapBrowser extends Dialog {

	private TreeViewer tree;
	private TableViewer table;
	private LdapConnection sock;
	private String selectedDn;
	private String selectedAttribute;
	private boolean attributeSelection;

	private TableViewer outputFields;
	private DataStream transfo;
	private LinkedHashMap<String, String> descriptor = new LinkedHashMap<String, String>();

	private void createOutputFieldViewer(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Button del = new Button(main, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		del.setText(Messages.DialogLdapBrowser_0);
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) outputFields.getSelection();
				for (Object o : ss.toList()) {
					((LinkedHashMap<String, String>) outputFields.getInput()).remove(o);
				}
				outputFields.refresh();
			}
		});

		Button refresh = new Button(main, SWT.PUSH);
		refresh.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		refresh.setText(Messages.DialogLdapBrowser_1);
		refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					transfo.setDefinition(selectedDn);
					descriptor = new LinkedHashMap<String, String>();
					for (StreamElement el : LdapHelper.getDescriptor(transfo).getStreamElements()) {
						descriptor.put(new String(el.name), new String(el.name));
					}
					outputFields.setInput(descriptor);
				} catch (ServerException e1) {

					e1.printStackTrace();
				}
			}
		});

		outputFields = new TableViewer(main, SWT.BORDER | SWT.FULL_SELECTION);
		outputFields.getTable().setHeaderVisible(true);
		outputFields.getTable().setLinesVisible(true);
		outputFields.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		outputFields.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				LinkedHashMap<String, String> desc = (LinkedHashMap<String, String>) inputElement;
				return desc.keySet().toArray(new Object[desc.size()]);
			}
		});

		TableViewerColumn fieldName = new TableViewerColumn(outputFields, SWT.NONE);
		fieldName.getColumn().setWidth(250);
		fieldName.getColumn().setText(Messages.DialogLdapBrowser_2);
		fieldName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {

				return ((String) element);
			}
		});

		fieldName.setEditingSupport(new EditingSupport(outputFields) {
			TextCellEditor editor = new TextCellEditor(outputFields.getTable());

			@Override
			protected void setValue(Object element, Object value) {
				descriptor.put((String) element, (String) value);
				outputFields.refresh();
			}

			@Override
			protected Object getValue(Object element) {
				return descriptor.get((String) element);
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		TableViewerColumn attributeName = new TableViewerColumn(outputFields, SWT.NONE);
		attributeName.getColumn().setWidth(250);
		attributeName.getColumn().setText(Messages.DialogLdapBrowser_3);
		attributeName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return descriptor.get((String) element);
			}
		});

		outputFields.setInput(descriptor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout(2, false));

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.DialogLdapBrowser_4);

		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.DialogLdapBrowser_5);

		tree = new TreeViewer(main, SWT.BORDER | SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setContentProvider(new TreeContentProvider());
		tree.setLabelProvider(new TreeLabelProvider());
		tree.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) tree.getSelection();

				if (!attributeSelection) {
					getButton(IDialogConstants.OK_ID).setEnabled(!ss.isEmpty());
				}
				else {
					getButton(IDialogConstants.OK_ID).setEnabled(!ss.isEmpty() && !table.getSelection().isEmpty());
				}

				if (ss.isEmpty()) {
					return;
				}

				if (ss.getFirstElement() instanceof TreeParent) {
					BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
						public void run() {

						}
					});

					// try to load
					TreeParent p = (TreeParent) ss.getFirstElement();

					String dn = p.getName();
					while (p.getParent() != tree.getInput()) {
						p = p.getParent();
						if (dn.equals("")) { //$NON-NLS-1$
							dn = p.getName();
						}
						else {
							dn = dn + ", " + p.getName(); //$NON-NLS-1$
						}
					}
					selectedDn = new String(dn);

					if (((TreeParent) ss.getFirstElement()).getChildren().isEmpty()) {
						try {
							for (String s : sock.getListNode(dn)) {
								TreeParent c = new TreeParent(s);
								((TreeParent) ss.getFirstElement()).addChild(c);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

					Attributes att = sock.getAttributes(dn);
					table.setInput(att);
				}
				tree.refresh();
			}

		});

		table = new TableViewer(main, SWT.BORDER | SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL);
		table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setContentProvider(new IStructuredContentProvider() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
			 */
			public void dispose() {

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
			 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements
			 * (java.lang.Object)
			 */
			public Object[] getElements(Object inputElement) {
				Attributes a = null;
				if (inputElement instanceof NameAwareAttributes) {
					a = ((NameAwareAttributes) inputElement);
				}
				// else if (inputElement instanceof BasicAttribute) {
				// a = ((BasicAttribute) inputElement);
				// }

				// Attributes a = (Attributes)inputElement;
				List<String> l = new ArrayList<String>();

				NamingEnumeration<?> en = a.getAll();
				try {
					while (en.hasMore()) {
						Object item = en.nextElement();
						if (item instanceof NameAwareAttribute) {
							l.add(((NameAwareAttribute) item).getID());
						}
						else if (item instanceof BasicAttribute) {
							l.add(((BasicAttribute) item).getID());
						}
						else {
							l.add("No name attribute");
						}
					}

				} catch (Exception e) {

				}

				return l.toArray(new String[l.size()]);
			}

		});
		table.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (attributeSelection) {
					getButton(IDialogConstants.OK_ID).setEnabled(!table.getSelection().isEmpty() && !tree.getSelection().isEmpty());
				}

				try {
					IStructuredSelection ss = (IStructuredSelection) table.getSelection();
					selectedAttribute = (String) ss.getFirstElement();

				} catch (Exception ex) {

				}

			}
		});

		table.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {

				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 1) {
					try {
						Object o = ((Attributes) table.getInput()).get((String) element).get();
						if (o instanceof String) {
							return (String) o;
						}
						else {
							return ""; //$NON-NLS-1$
						}
					} catch (Exception e) {
						return e.getMessage();
					}

				}
				else {
					return (String) element;
				}

			}

			public void addListener(ILabelProviderListener listener) {

			}

			public void dispose() {

			}

			public boolean isLabelProperty(Object element, String property) {

				return false;
			}

			public void removeListener(ILabelProviderListener listener) {

			}

		});

		TableColumn c = new TableColumn(table.getTable(), SWT.NONE);
		c.setText(Messages.DialogLdapBrowser_9);
		c.setWidth(200);

		TableColumn c2 = new TableColumn(table.getTable(), SWT.NONE);
		c.setText(Messages.DialogLdapBrowser_10);
		c2.setWidth(200);

		createOutputFieldViewer(main);
		return main;
	}

	public DialogLdapBrowser(Shell parentShell, boolean attributeSelection, DataStream transfo) {
		super(parentShell);
		this.sock = (LdapConnection) ((LdapServer) transfo.getServer()).getCurrentConnection(null);
		this.attributeSelection = attributeSelection;
		this.transfo = transfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);

		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		try {
			sock.connect(null);
			for (String s : sock.getListNode("")) { //$NON-NLS-1$
				root.addChild(new TreeParent(s));
			}
			// sock.disconnect();
		} catch (ServerException e) {

			e.printStackTrace();
		}

		tree.setInput(root);

	}

	public String getDn() {
		return selectedDn;
	}

	public String getSelectedAttribute() {
		return selectedAttribute;
	}

	public LinkedHashMap<String, String> getStreamDescriptor() {
		return descriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

}
