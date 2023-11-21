package org.fasd.cubewizard.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;

import xmldesigner.internal.DimensionTree;
import xmldesigner.internal.TreeArg;
import xmldesigner.internal.TreeObject;
import xmldesigner.internal.TreeParent;
import xmldesigner.parse.item.DataXML;
import xmldesigner.xpath.Xpath;

public class CompositeXMLDesign extends Composite {

	private TreeViewer viewer;
	private ListViewer list;

	private ArrayList<String> listHiera = null;
	private TreeParent depart = null;
	private DataXML dtd = null;
	private Xpath xpath = null;
	private Text name;

	private DataObject table = new DataObject();

	public CompositeXMLDesign(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2, true));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.CompositeXMLDesign_TableName);

		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		name.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				table.setName(name.getText());

			}

		});
		viewer = new TreeViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setUseHashlookup(true);

		viewer.expandToLevel(2);

		viewer.getTree().addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				// debug?
				if (!viewer.getSelection().isEmpty()) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
					Object obj = selection.getFirstElement();

					String tmp = getDataString((TreeParent) obj);
					if (tmp != null)
						CompositeXMLDesign.this.xpath.pushState();

					StringTokenizer st = new StringTokenizer(tmp, "|"); //$NON-NLS-1$
					while (st.hasMoreElements()) {
						String ch = st.nextToken();
						if (ch.endsWith(".arg")) { //$NON-NLS-1$
							ch = ch.substring(0, ch.length() - 4);
							CompositeXMLDesign.this.xpath.addArg(ch);
						} else if (ch.endsWith(".val")) { //$NON-NLS-1$
							ch = ch.substring(0, ch.length() - 4);
							CompositeXMLDesign.this.xpath.addCol(ch);
						}
					}

					CompositeXMLDesign.this.xpath.refresh();

				}
			}

			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}
		});

		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };
		viewer.addDragSupport(ops, transfers, new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();

				if (ss.getFirstElement() instanceof TreeParent) {
				} else if (ss.getFirstElement() instanceof TreeArg) {
				} else
					event.doit = false;
			}

			public void dragSetData(DragSourceEvent event) {

				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				event.data = getDataString((TreeObject) ss.getFirstElement());
			}

			public void dragFinished(DragSourceEvent event) {
			}
		});

		viewer.addDropSupport(ops, transfers, new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;

			}

			public void dragOver(DropTargetEvent event) {

			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dropAccept(DropTargetEvent event) {
			}

			public void drop(DropTargetEvent event) {

				String tmp = (String) event.data;

				if (tmp != null)
					CompositeXMLDesign.this.xpath.pushState();
				CompositeXMLDesign.this.xpath.delCol(tmp);

				CompositeXMLDesign.this.xpath.refresh();
			}

		});

		list = new ListViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		list.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		list.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return ((String[]) inputElement);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		list.setLabelProvider(new LabelProvider());

		list.addDropSupport(ops, transfers, new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;

			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
			}

			public void drop(DropTargetEvent event) {
				String tmp = (String) event.data;

				CompositeXMLDesign.this.xpath.pushState();

				StringTokenizer st = new StringTokenizer(tmp, "|"); //$NON-NLS-1$
				while (st.hasMoreElements()) {
					String ch = st.nextToken();
					if (ch.endsWith(".val")) { //$NON-NLS-1$
						ch = ch.substring(0, ch.length() - 4);
						CompositeXMLDesign.this.xpath.addCol(ch);
						list.add(ch);

						if (table.findItemNamed(ch) == null) {
							DataObjectItem it = new DataObjectItem();
							it.setName(ch);
							it.setOrigin(ch);
							it.setType("physical"); //$NON-NLS-1$
							it.setClasse("java.lang.Object"); //$NON-NLS-1$
							table.addDataObjectItem(it);
						}
					}
				}
				CompositeXMLDesign.this.xpath.refresh();
				String requete = CompositeXMLDesign.this.xpath.getRequete();
				requete = requete.replace("'{XML_DOCUMENT}'", CompositeXMLDesign.this.xpath.getPath()); //$NON-NLS-1$

				table.setSelectStatement(requete);

				refreshView();

			}

			public void dropAccept(DropTargetEvent event) {
			}

		});
	}

	public void fill(DataXML dtd, Xpath xpath) {
		this.dtd = dtd;
		this.xpath = xpath;

		viewer.setInput(createModel(dtd));
		this.xpath.setListHiera(listHiera);
		viewer.refresh();
	}

	private TreeParent createModel(DataXML dtd) {
		DimensionTree model = new DimensionTree(dtd);
		TreeParent root = model.createModel();
		listHiera = createHiera(root);
		depart = root;
		return root;
	}

	/**
	 * Creation a list hierarchic of tags of file xml
	 * 
	 * @param t
	 * @return
	 */
	private ArrayList<String> createHiera(TreeParent t) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(t.getName());
		for (int i = 0; i < t.getChildren().length; i++) {
			add(list, createHiera((TreeParent) t.getChildren(i)));
		}
		return list;
	}

	private List<String> add(List<String> list1, List<String> list2) {
		for (int i = 0; i < list2.size(); i++) {
			list1.add(list2.get(i));
		}
		return list1;
	}

	/**
	 * Get the parent of name's tag
	 * 
	 * @param noeud
	 * @return
	 */
	public String getParent(String noeud) {
		TreeParent root = depart;
		for (int i = 0; i < root.getChildren().length;) {
			if (root.getChildren(i).getName().equals(noeud))
				return root.getChildren(i).getParent().getName();
			else
				return getParent(noeud, (TreeParent) root.getChildren(i));
		}
		return ""; //$NON-NLS-1$
	}

	public String getParent(String noeud, TreeParent t) {
		String retour = ""; //$NON-NLS-1$
		for (int i = 0; i < t.getChildren().length; i++) {
			if (t.getChildren(i).getName().equals(noeud)) {
				retour = t.getName();
				break;
			} else {
				retour = getParent(noeud, (TreeParent) t.getChildren(i));
			}
		}
		return retour;
	}

	public TreeObject getSelectedObject() {
		if (!viewer.getSelection().isEmpty()) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			TreeObject selectedDomainObject = (TreeObject) selection.getFirstElement();
			return selectedDomainObject;
		}
		return new TreeObject("null"); //$NON-NLS-1$
	}

	/**
	 * Get data to transfert into the drag and drop
	 * 
	 * @param t
	 * @return
	 */
	public String getDataString(TreeObject t) {
		String tmp = ""; //$NON-NLS-1$
		if (t instanceof TreeArg) {
			tmp = t.getParent().getName() + "/" + t.getName() + ".arg"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (t instanceof TreeParent) {

			tmp = getDataTreeParent((TreeParent) t);

		}
		return tmp;
	}

	public String getDataTreeParent(TreeParent t) {
		String tmp = ""; //$NON-NLS-1$

		if (!existFilsBal(t))
			tmp = t.getName() + ".val|"; //$NON-NLS-1$

		for (int i = 0; i < t.getChildren().length; i++) {
			tmp = tmp.concat(getDataString(t.getChildren(i)));
			tmp = tmp.concat("|"); //$NON-NLS-1$
		}
		tmp = tmp.substring(0, tmp.length() - 1);

		return tmp;
	}

	/**
	 * Return if exist one childreen of one name's tag
	 * 
	 * @param t
	 * @return
	 */
	public boolean existFilsBal(TreeParent t) {
		for (int i = 0; i < t.getChildren().length; i++) {
			if (!(t.getChildren(i) instanceof TreeArg))
				return true;
		}
		return false;
	}

	public List<String> getHiera() {
		return listHiera;
	}

	public void refreshView() {
		viewer.setInput(createModel(dtd));
		viewer.expandToLevel(2);
		viewer.refresh();
	}

	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {

				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {

			return obj.toString().toLowerCase();
		}
	}

	public DataObject getDataObject() {

		return table;
	}
}
