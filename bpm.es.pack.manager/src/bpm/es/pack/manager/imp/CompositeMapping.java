package bpm.es.pack.manager.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Activator;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.es.pack.manager.I18N.Messages;
import bpm.es.pack.manager.digester.ExportDescriptorDigester;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.workplace.core.model.ExportDetails;
import bpm.vanilla.workplace.core.model.ImportItem;

public class CompositeMapping extends Composite {

	private TableViewer list;
	private TreeViewer tree;
	private CompositeInformation dataSource;
	private Composite composite;
	private HashMap<String, File> map;

	private HashMap<String, Document> docs = new HashMap<String, Document>();
	private Text repositoryTxt, databaseTxt;
	private Text find, replace;
	private Button launch;

	private HashMap<ImportItem, HashMap<Element, Properties>> details = new HashMap<ImportItem, HashMap<Element, Properties>>();
	private HashMap<ImportItem, TreeParent> treeMap = new HashMap<ImportItem, TreeParent>();

	public CompositeMapping(Composite parent, int style, HashMap<String, File> map) {
		super(parent, style);

		buildContent();
		this.map = map;
		try {
			readDescriptor();
		} catch (Exception e) {
			MessageDialog.openError(getShell(), Messages.Client_Import_CompositeMapping_0, e.getMessage());
		}

	}

	private void buildContent() {
		this.setLayout(new GridLayout(3, true));

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		l.setText(Messages.Client_Import_CompositeMapping_1);

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		l2.setText(Messages.Client_Import_CompositeMapping_2);

		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		l3.setText(Messages.Client_Import_CompositeMapping_3);

		list = new TableViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		list.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		list.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				Collection<ImportItem> it = (Collection<ImportItem>) inputElement;
				return it.toArray(new ImportItem[it.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		list.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ImportItem) element).getName();
			}

			@Override
			public Image getImage(Object element) {
				int type = ((ImportItem) element).getType();
				ImageRegistry reg = Activator.getDefault().getImageRegistry();
				switch (type) {
					case IRepositoryApi.FASD_TYPE:
						return reg.get("fasd"); //$NON-NLS-1$
					case IRepositoryApi.FD_TYPE:
						return reg.get("fd"); //$NON-NLS-1$
					case IRepositoryApi.FD_DICO_TYPE:
						return reg.get("dico"); //$NON-NLS-1$
					case IRepositoryApi.FMDT_TYPE:
						return reg.get("fmdt"); //$NON-NLS-1$
					case IRepositoryApi.GTW_TYPE:
						return reg.get("gtw"); //$NON-NLS-1$
				}
				return reg.get("default"); //$NON-NLS-1$
			}

		});
		list.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) list.getSelection();

				ImportItem it = (ImportItem) ss.getFirstElement();
				if (details.get(it) == null) {
					try {

						details.put(it, parseFile(it));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				tree.setInput(treeMap.get(it));
			}
		});

		tree = new TreeViewer(this, SWT.BORDER | SWT.V_SCROLL);
		tree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tree.setContentProvider(new TreeContentProvider());
		tree.setLabelProvider(new TreeLabelProvider());
		tree.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (dataSource != null && !dataSource.isDisposed()) {
					dataSource.dispose();
				}

				IStructuredSelection ss = (IStructuredSelection) tree.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeElement)) {
					composite.layout();
					return;
				}

				TreeElement el = (TreeElement) ss.getFirstElement();

				Properties p = details.get(((IStructuredSelection) list.getSelection()).getFirstElement()).get(el.key);
				if (p == null) {
					composite.layout();
					return;
				}

				if (p.get("type") != null) { //$NON-NLS-1$
					if (p.get("type").equals("xmla")) { //$NON-NLS-1$ //$NON-NLS-2$
						composite.layout();
						return;
					}
					else {
						dataSource = new CompositeRepository(composite, SWT.NONE);
						dataSource.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
						dataSource.setProperties(p);
						composite.layout();
					}
				}
				else {
					dataSource = new CompositeConnectionSql(composite, SWT.NONE);
					dataSource.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
					dataSource.setProperties(p);
					composite.layout();
				}
			}
		});

		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Group container = new Group(this, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		Button b = new Button(container, SWT.CHECK);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		b.setText(Messages.Client_Import_CompositeMapping_13);
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				databaseTxt.setEnabled(((Button) e.widget).getSelection());
				repositoryTxt.setEnabled(((Button) e.widget).getSelection());
				launch.setEnabled(((Button) e.widget).getSelection());
			}
		});

		Label l4 = new Label(container, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.Client_Import_CompositeMapping_14);

		repositoryTxt = new Text(container, SWT.BORDER);
		repositoryTxt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		repositoryTxt.setEnabled(false);

		Label l5 = new Label(container, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.Client_Import_CompositeMapping_15);

		databaseTxt = new Text(container, SWT.BORDER);
		databaseTxt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		databaseTxt.setEnabled(false);

		launch = new Button(container, SWT.PUSH);
		launch.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false, 2, 1));
		launch.setText(Messages.Client_Import_CompositeMapping_16);
		launch.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) list.getSelection();
				launch((List<ImportItem>) ss.toList());
			}
		});

		// find and repalce UI
		Group findReplace = new Group(this, SWT.NONE);
		findReplace.setLayout(new GridLayout(2, false));
		findReplace.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		Label l6 = new Label(findReplace, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.Client_Import_CompositeMapping_17);

		find = new Text(findReplace, SWT.BORDER);
		find.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l7 = new Label(findReplace, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.Client_Import_CompositeMapping_18);

		replace = new Text(findReplace, SWT.BORDER);
		replace.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Button fr = new Button(findReplace, SWT.PUSH);
		fr.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		fr.setText(Messages.Client_Import_CompositeMapping_19);
		fr.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) list.getSelection();

				findReplace((List<ImportItem>) ss.toList());
			}
		});
	}

	private void readDescriptor() throws FileNotFoundException, Exception {
		File descriptor = null;
		for (String s : map.keySet()) {
			if (s.equals("descriptor")) { //$NON-NLS-1$
				descriptor = map.get(s);
				break;
			}
		}

		ExportDetails ex = new ExportDescriptorDigester(new FileInputStream(descriptor)).getModel();
		list.setInput(ex.getImportItems());
	}

	private HashMap<Element, Properties> parseFile(ImportItem it) throws Exception {
		String key = null;
		for (String s : map.keySet()) {
			if (s.equals(it.getId() + "")) { //$NON-NLS-1$
				key = s;
				break;
			}
		}
		HashMap<Element, Properties> details = new HashMap<Element, Properties>();
		FileInputStream fis = new FileInputStream(map.get(key));
		Document doc = DocumentHelper.parseText(IOUtils.toString(fis, "UTF-8")); //$NON-NLS-1$

		docs.put(key, doc);
		Element root = doc.getRootElement();

		TreeParent t = null;

		switch (it.getType()) {
			case IRepositoryApi.FASD_TYPE:
				t = parseFasd(root, details);
				tree.setInput(t);
				break;
			case IRepositoryApi.FD_TYPE:
				t = parseFd(root, details);
				tree.setInput(t);
				break;
			case IRepositoryApi.FMDT_TYPE:
				t = parseFmdt(root, details);
				tree.setInput(t);
				break;
			case IRepositoryApi.FD_DICO_TYPE:
				t = parseFdDictionary(root, details);
				tree.setInput(t);
				break;
			case IRepositoryApi.GTW_TYPE:
				t = parseGateway(root, details);
				break;
			case IRepositoryApi.BIW_TYPE:
				try {
					t = parseWorkflow(root, details);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case IRepositoryApi.CUST_TYPE:
				t = parseCustom(root, details);
				tree.setInput(t);
				break;
		}

		if (t != null) {
			treeMap.put(it, t);
		}
		else {
			treeMap.put(it, new TreeParent("")); //$NON-NLS-1$
		}

		return details;
	}

	private TreeParent parseWorkflow(Element root, HashMap<Element, Properties> details) {

		TreeParent troot = new TreeParent(""); //$NON-NLS-1$
		Element rr = root;
		// vanillaServer
		// modifi�
		for (Object vs : rr.elements("securityServer")) { //$NON-NLS-1$
			Element e = (Element) vs;
			TreeElement tp = new TreeElement(((Element) vs).element("name").getStringValue(), e); //$NON-NLS-1$

			Properties p = new Properties();
			p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
			details.put(e, p);
			troot.addChild(tp);
		}

		// ajout
		for (Object vs : rr.elements("gatewayServer")) { //$NON-NLS-1$
			Element e = (Element) vs;
			TreeElement tp = new TreeElement(((Element) vs).element("name").getStringValue(), e); //$NON-NLS-1$

			Properties p = new Properties();
			p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
			details.put(e, p);
			troot.addChild(tp);
		}

		// ajout
		for (Object vs : rr.elements("mailServer")) { //$NON-NLS-1$
			Element e = (Element) vs;
			TreeElement tp = new TreeElement(((Element) vs).element("name").getStringValue(), e); //$NON-NLS-1$

			Properties p = new Properties();
			p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
			details.put(e, p);
			troot.addChild(tp);
		}

		// repositoryServer
		// modifi�
		for (Object rs : rr.elements("vanillaRepositoryServer")) { //$NON-NLS-1$
			Element e = (Element) rs;
			TreeElement tp = new TreeElement(((Element) rs).element("name").getStringValue(), (Element) rs); //$NON-NLS-1$

			Properties p = new Properties();
			p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
			details.put(e, p);

			troot.addChild(tp);
		}
		// fileserver
		// ajout
		for (Object ls : rr.elements("fileserver")) { //$NON-NLS-1$
			Element e = (Element) ls;
			TreeElement tp = new TreeElement(((Element) ls).element("name").getStringValue(), (Element) ls); //$NON-NLS-1$
			Properties p = new Properties();
			p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("path", e.element("repertoiredef").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("port", e.element("port").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("type", Messages.Client_Import_CompositeMapping_58); //$NON-NLS-1$
			details.put(e, p);
			troot.addChild(tp);
		}
		// databaseServer
		// modifi�
		for (Object ds : rr.elements("dataBaseServer")) { //$NON-NLS-1$
			Element e = (Element) ds;
			TreeElement tp = new TreeElement(((Element) ds).element("name").getStringValue(), (Element) ds); //$NON-NLS-1$

			Properties p = new Properties();
			p.put("host", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("login", e.element("login").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("password", e.element("password").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("port", e.element("port").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("dataBase", e.element("dataBaseName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("driver", e.element("jdbcDriver").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$

			details.put(e, p);

			troot.addChild(tp);
		}
		// freemetricsServer
		for (Object ds : rr.elements("freemetricsServer")) { //$NON-NLS-1$
			Element e = (Element) ds;
			TreeElement tp = new TreeElement(((Element) ds).element("name").getStringValue(), (Element) ds); //$NON-NLS-1$
			Properties p = new Properties();
			p.put("host", e.element("host").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$

			p.put("login", e.element("login").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("password", e.element("password").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("port", e.element("port").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("dataBase", e.element("dataBaseName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("driver", e.element("driverName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$

			details.put(e, p);
			troot.addChild(tp);
		}
		return troot;
	}

	private TreeParent parseFdDictionary(Element root, HashMap<Element, Properties> details) {
		TreeParent troot = new TreeParent(""); //$NON-NLS-1$

		for (Object o : root.elements("dataSource")) { //$NON-NLS-1$
			Element e = (Element) o;
			TreeElement te = new TreeElement(e.element("name").getStringValue(), e); //$NON-NLS-1$
			Properties p = new Properties();
			p.put("name", e.element("name").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
			if (((Element) o).element("odaExtensionDataSourceId").getStringValue().equals("bpm.metadata.birt.oda.runtime")) { //$NON-NLS-1$ //$NON-NLS-2$

				for (Object _o : e.elements("publicProperty")) { //$NON-NLS-1$
					Element _p = (Element) _o;
					if ("URL".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
						p.put("repositoryUrl", _p.getStringValue()); //$NON-NLS-1$
						p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}

				details.put(e, p);
			}
			else if (((Element) o).element("odaExtensionDataSourceId").getStringValue().equals("org.eclipse.birt.report.data.oda.jdbc")) { //$NON-NLS-1$ //$NON-NLS-2$

				p.put("name", e.element("name").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$

				for (Object _o : e.elements("publicProperty")) { //$NON-NLS-1$
					Element _p = (Element) _o;
					if ("odaDriverClass".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
						p.put("driver", _p.getStringValue()); //$NON-NLS-1$
					}
					if ("odaURL".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
						String url = _p.getStringValue();

						p.put("host", url); //$NON-NLS-1$

					}
					if ("odaPassword".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
						p.put("login", _p.getStringValue()); //$NON-NLS-1$
					}
					if ("odaUser".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
						p.put("password", _p.getStringValue()); //$NON-NLS-1$
					}
				}
			}
			troot.addChild(te);
			details.put(e, p);
		}
		return troot;
	}

	private TreeParent parseGateway(Element root, HashMap<Element, Properties> details) {
		TreeParent troot = new TreeParent(""); //$NON-NLS-1$
		Element rr = root.element("servers"); //$NON-NLS-1$
		// vanillaServer
		for (Object vs : rr.elements("vanillaServer")) { //$NON-NLS-1$
			TreeParent tp = new TreeParent(((Element) vs).element("name").getStringValue()); //$NON-NLS-1$
			for (Object vc : ((Element) vs).elements("vanillaConnection")) { //$NON-NLS-1$
				Element e = (Element) vc;
				TreeElement te = new TreeElement(((Element) vc).element("name").getStringValue(), e); //$NON-NLS-1$
				Properties p = new Properties();
				p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
				details.put(e, p);
				tp.addChild(te);
			}
			troot.addChild(tp);
		}
		// repositoryServer
		for (Object rs : rr.elements("repositoryServer")) { //$NON-NLS-1$
			TreeParent tp = new TreeParent(((Element) rs).element("name").getStringValue()); //$NON-NLS-1$
			for (Object rc : ((Element) rs).elements("repositoryConnection")) { //$NON-NLS-1$
				Element e = (Element) rc;
				TreeElement te = new TreeElement(((Element) rc).element("name").getStringValue(), e); //$NON-NLS-1$
				Properties p = new Properties();
				p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
				details.put(e, p);
				tp.addChild(te);
			}
			troot.addChild(tp);
		}
		// ldapServer
		for (Object ls : rr.elements("ldapServer")) { //$NON-NLS-1$
			TreeParent tp = new TreeParent(((Element) ls).element("name").getStringValue()); //$NON-NLS-1$
			for (Object lc : ((Element) ls).elements("ldapConnection")) { //$NON-NLS-1$
				Element e = (Element) lc;
				TreeElement te = new TreeElement(((Element) lc).element("name").getStringValue(), e); //$NON-NLS-1$
				Properties p = new Properties();
				p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
				details.put(e, p);
				tp.addChild(te);
			}
			troot.addChild(tp);
		}
		// databaseServer
		for (Object ds : rr.elements("dataBaseServer")) { //$NON-NLS-1$
			TreeParent tp = new TreeParent(((Element) ds).element("name").getStringValue()); //$NON-NLS-1$
			for (Object dc : ((Element) ds).elements("dataBaseConnection")) { //$NON-NLS-1$
				Element e = (Element) dc;
				TreeElement te = new TreeElement(((Element) dc).element("name").getStringValue(), e); //$NON-NLS-1$
				Properties p = new Properties();
				p.put("host", e.element("host").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$

				p.put("login", e.element("login").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("password", e.element("password").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("port", e.element("port").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("dataBase", e.element("dataBaseName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("driver", e.element("driverName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$

				details.put(e, p);
				tp.addChild(te);
			}
			troot.addChild(tp);
		}
		// freemetricsServer
		for (Object ds : rr.elements("freemetricsServer")) { //$NON-NLS-1$
			TreeParent tp = new TreeParent(((Element) ds).element("name").getStringValue()); //$NON-NLS-1$
			for (Object dc : ((Element) ds).elements("dataBaseConnection")) { //$NON-NLS-1$
				Element e = (Element) dc;
				TreeElement te = new TreeElement(((Element) dc).element("name").getStringValue(), e); //$NON-NLS-1$
				Properties p = new Properties();
				p.put("host", e.element("host").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$

				p.put("login", e.element("login").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("password", e.element("password").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("port", e.element("port").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("dataBase", e.element("dataBaseName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("driver", e.element("driverName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$

				details.put(e, p);
				tp.addChild(te);
			}
			troot.addChild(tp);
		}

		return troot;
	}

	private TreeParent parseFmdt(Element root, HashMap<Element, Properties> details) {
		TreeParent troot = new TreeParent(""); //$NON-NLS-1$

		for (Object o : root.elements("sqlDataSource")) { //$NON-NLS-1$
			TreeParent tp = new TreeParent(((Element) o).element("name").getText()); //$NON-NLS-1$

			for (Object _o : ((Element) o).elements("sqlConnection")) { //$NON-NLS-1$
				Element e = (Element) _o;
				Properties p = new Properties();
				p.put("name", e.element("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("host", e.element("host").getText()); //$NON-NLS-1$ //$NON-NLS-2$

				p.put("login", e.element("username").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("password", e.element("password").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("port", e.element("portNumber").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("dataBase", e.element("dataBaseName").getText()); //$NON-NLS-1$ //$NON-NLS-2$

				if (e.element("") != null) { //$NON-NLS-1$
					p.put("schemaName", e.element("schemaName")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					p.put("schemaName", ""); //$NON-NLS-1$ //$NON-NLS-2$
				}

				p.put("driver", e.element("driverName").getText()); //$NON-NLS-1$ //$NON-NLS-2$

				details.put(e, p);

				TreeElement te = new TreeElement((String) p.get("name"), e); //$NON-NLS-1$
				tp.addChild(te);

			}
			troot.addChild(tp);

		}
		return troot;
	}

	private TreeParent parseFasd(Element root, HashMap<Element, Properties> details) {
		TreeParent troot = new TreeParent(""); //$NON-NLS-1$

		for (Object o : root.elements("xmla")) { //$NON-NLS-1$

			Element e = ((Element) o).element("XMLAConnection"); //$NON-NLS-1$
			Properties p = new Properties();
			p.put("type", "xmla"); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("name", e.element("schema-name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("host", e.element("url").getText()); //$NON-NLS-1$ //$NON-NLS-2$

			p.put("login", e.element("user").getText()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("password", e.element("password").getText()); //$NON-NLS-1$ //$NON-NLS-2$

			details.put(e, p);

			TreeElement te = new TreeElement((String) p.get("name"), e); //$NON-NLS-1$

			troot.addChild(te);
		}

		for (Object o : root.element("datasources").elements("datasource-item")) { //$NON-NLS-1$ //$NON-NLS-2$

			Element e = ((Element) o).element("connection"); //$NON-NLS-1$
			Properties p = new Properties();
			p.put("name", ((Element) o).element("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$

			String url = e.element("url").getText(); //$NON-NLS-1$

			String h = null;
			try {
				h = url.substring(url.indexOf("://") + 3, url.lastIndexOf(":")); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (Exception ex) {
				h = url.substring(url.indexOf("://") + 3, url.lastIndexOf("/")); //$NON-NLS-1$ //$NON-NLS-2$
			}

			int i = url.lastIndexOf(":"); //$NON-NLS-1$

			String pt = null;
			if (i != url.indexOf(":")) { //$NON-NLS-1$
				pt = ""; //$NON-NLS-1$
			}
			else {
				pt = url.substring(i + 1, url.substring(i + 1).indexOf("/") + i + 1); //$NON-NLS-1$
			}

			String db = url.substring(url.lastIndexOf("/") + 1); //$NON-NLS-1$

			p.put("host", h); //$NON-NLS-1$
			p.put("port", pt); //$NON-NLS-1$
			p.put("dataBase", db); //$NON-NLS-1$
			p.put("driver", e.element("driver").getText()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("login", e.element("user").getText()); //$NON-NLS-1$ //$NON-NLS-2$
			p.put("password", e.element("password").getText()); //$NON-NLS-1$ //$NON-NLS-2$

			details.put(e, p);

			TreeElement te = new TreeElement((String) p.get("name"), e); //$NON-NLS-1$

			troot.addChild(te);
		}

		return troot;
	}

	private TreeParent parseFd(Element root, HashMap<Element, Properties> details) {
		// details.clear();

		List<Element> list = new ArrayList<Element>();

		for (Object o : root.element("dictionary").elements("componentFusionChart")) { //$NON-NLS-1$ //$NON-NLS-2$
			list.add((Element) o);
		}

		for (Object o : root.element("dictionary").elements("componentCombo")) { //$NON-NLS-1$ //$NON-NLS-2$
			Element e = (Element) o;
			if (e.element("filterCombo") != null && e.element("filterCombo").element("data-fmdt") != null) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				list.add(e);
			}
		}

		for (Element e : list) {
			Element _e = null;
			if (e.element("data-fmdt") != null) { //$NON-NLS-1$
				_e = e.element("data-fmdt").element("datasource-fmdt"); //$NON-NLS-1$ //$NON-NLS-2$

			}

			else if (e.element("filterCombo") != null) { //$NON-NLS-1$
				_e = e.element("filterCombo").element("data-fmdt").element("datasource-fmdt"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (_e != null) {
				Properties p = new Properties();
				p.put("name", e.element("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("repositoryUrl", _e.element("repositoryUrl").getText()); //$NON-NLS-1$ //$NON-NLS-2$

				details.put(_e, p);
			}
		}

		// coming from database
		for (Object o : root.element("dictionary").elements("componentCombo")) { //$NON-NLS-1$ //$NON-NLS-2$
			Element e = (Element) o;
			if (e.element("filterSlider") != null && e.element("filterSlider").element("datas").element("datasource-jndi") != null) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

				Element _e = e.element("filterSlider").element("datas").element("datasource-jndi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				String url = _e.element("url").getText(); //$NON-NLS-1$

				String h = url.substring(url.indexOf("://") + 3, url.lastIndexOf("/")); //$NON-NLS-1$ //$NON-NLS-2$
				int i = url.lastIndexOf(":"); //$NON-NLS-1$
				String pt = url.substring(i + 1, url.substring(i + 1).indexOf("/") + i + 1); //$NON-NLS-1$
				String db = url.substring(url.lastIndexOf("/") + 1); //$NON-NLS-1$

				Properties p = new Properties();
				p.put("name", e.element("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("host", h); //$NON-NLS-1$

				p.put("login", _e.element("user").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("password", _e.element("password").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("port", pt); //$NON-NLS-1$
				p.put("dataBase", db); //$NON-NLS-1$

				p.put("schemaName", _e.element("schemaName") != null ? _e.element("schemaName").getText() : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				p.put("driver", _e.element("driver").getText()); //$NON-NLS-1$ //$NON-NLS-2$

				details.put(_e, p);

			}
			if (e.element("filterCombo") != null && e.element("filterCombo").element("datas") != null && e.element("filterCombo").element("datas").element("datasource-jndi") != null) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				Element _e = e.element("filterCombo").element("datas").element("datasource-jndi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				String url = _e.element("url").getText(); //$NON-NLS-1$

				String h = url.substring(url.indexOf("://") + 3, url.lastIndexOf("/")); //$NON-NLS-1$ //$NON-NLS-2$
				int i = url.lastIndexOf(":"); //$NON-NLS-1$
				String pt = url.substring(i + 1, url.substring(i + 1).indexOf("/") + i + 1); //$NON-NLS-1$
				String db = url.substring(url.lastIndexOf("/") + 1); //$NON-NLS-1$

				Properties p = new Properties();
				p.put("name", e.element("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("host", h); //$NON-NLS-1$

				p.put("login", _e.element("user").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("password", _e.element("password").getText()); //$NON-NLS-1$ //$NON-NLS-2$
				p.put("port", pt); //$NON-NLS-1$
				p.put("dataBase", db); //$NON-NLS-1$

				p.put("schemaName", _e.element("schemaName") != null ? _e.element("schemaName").getText() : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				p.put("driver", _e.element("driver").getText()); //$NON-NLS-1$ //$NON-NLS-2$

				details.put(_e, p);

			}
		}

		TreeParent tRoot = new TreeParent(""); //$NON-NLS-1$

		for (Element e : details.keySet()) {
			TreeElement te = new TreeElement(details.get(e).getProperty("name"), e); //$NON-NLS-1$
			tRoot.addChild(te);
		}

		return tRoot;
	}

	private class TreeElement extends TreeParent {
		private Element key;

		public TreeElement(String name, Element e) {
			super(name);
			key = e;
		}
	}

	private TreeParent parseCustom(Element root, HashMap<Element, Properties> details) {
		// details.clear();

		if (!root.getName().equals("report")) { //$NON-NLS-1$
			return null;
		}

		TreeParent troot = new TreeParent(""); //$NON-NLS-1$

		for (Object o : root.element("data-sources").elements("oda-data-source")) { //$NON-NLS-1$ //$NON-NLS-2$

			Element e = (Element) o;

			if (!e.attribute("extensionID").getText().equals("bpm.metadata.birt.oda.runtime")) { //$NON-NLS-1$ //$NON-NLS-2$
				break;
			}

			Properties p = new Properties();
			p.put("name", e.attribute("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$

			for (Object _o : e.elements("property")) { //$NON-NLS-1$
				Element _p = (Element) _o;
				if ("URL".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
					p.put("repositoryUrl", _p.getText()); //$NON-NLS-1$
					p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
					details.put(_p, p);

					TreeElement te = new TreeElement((String) p.get("name"), _p); //$NON-NLS-1$

					troot.addChild(te);

					break;
				}
			}
		}

		return troot;
	}

	public void applyChanges() {
		IStructuredSelection ss = (IStructuredSelection) tree.getSelection();

		if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeElement)) {
			return;
		}

		updateXml((ImportItem) ((IStructuredSelection) list.getSelection()).getFirstElement());
	}

	public void cancelChanges() {
		details.clear();
		IStructuredSelection ss = (IStructuredSelection) tree.getSelection();

		if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeElement)) {
			return;
		}

		dataSource.setProperties(details.get(((IStructuredSelection) list.getSelection()).getFirstElement()).get(((TreeElement) ss.getFirstElement()).key));
	}

	private void updateXml(ImportItem it) {
		for (Element e : details.get(it).keySet()) {
			switch (it.getType()) {
				case IRepositoryApi.FASD_TYPE:
					generateFasd(e, dataSource.getProperties());
					break;
				case IRepositoryApi.FD_TYPE:
					generateFd(e, dataSource.getProperties());
					break;
				case IRepositoryApi.FMDT_TYPE:
					generateFmdt(e, dataSource.getProperties());
					break;
				case IRepositoryApi.FD_DICO_TYPE:
					generateFdDico(e, dataSource.getProperties());
					break;
				case IRepositoryApi.CUST_TYPE:
					generateCust(e, dataSource.getProperties());
					break;
				case IRepositoryApi.GTW_TYPE:
					generateGateway(e, dataSource.getProperties());
					break;
				case IRepositoryApi.BIW_TYPE:
					generateWorkflow(e, dataSource.getProperties());

			}
			details.get(it).put(e, dataSource.getProperties());
		}

		String key = null;
		for (String s : map.keySet()) {
			if (s.equals(it.getId() + "")) { //$NON-NLS-1$
				key = s;
				break;
			}
		}

		;
		try {
			PrintWriter pw = new PrintWriter(map.get(key));
			pw.write(docs.get(key).asXML());
			pw.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

	}

	private void generateWorkflow(Element e, Properties p) {
		Element _e = null;
		if ((_e = e.element("host")) != null && p.getProperty("host") != null) { //$NON-NLS-1$ //$NON-NLS-2$
			_e.setText(p.getProperty("host")); //$NON-NLS-1$
		}
		if ((_e = e.element("port")) != null && p.getProperty("port") != null) { //$NON-NLS-1$ //$NON-NLS-2$
			_e.setText(p.getProperty("port")); //$NON-NLS-1$
		}
		if ((_e = e.element("dataBaseName")) != null && p.getProperty("dataBaseName") != null) { //$NON-NLS-1$ //$NON-NLS-2$
			_e.setText(p.getProperty("dataBase")); //$NON-NLS-1$
		}
		if ((_e = e.element("driverName")) != null && p.getProperty("driverName") != null) { //$NON-NLS-1$ //$NON-NLS-2$
			_e.setText(p.getProperty("driver")); //$NON-NLS-1$
		}
		if ((_e = e.element("login")) != null && p.getProperty("login") != null) { //$NON-NLS-1$ //$NON-NLS-2$
			_e.setText(p.getProperty("login")); //$NON-NLS-1$
		}
		if ((_e = e.element("password")) != null && p.getProperty("password") != null) { //$NON-NLS-1$ //$NON-NLS-2$
			_e.setText(p.getProperty("password")); //$NON-NLS-1$
		}
		if ((_e = e.element("url")) != null && p.getProperty("repositoryUrl") != null) { //$NON-NLS-1$ //$NON-NLS-2$
			_e.setText(p.getProperty("repositoryUrl")); //$NON-NLS-1$
		}
	}

	private void generateFmdt(Element e, Properties p) {
		e.element("host").setText(p.getProperty("host")); //$NON-NLS-1$ //$NON-NLS-2$
		e.element("username").setText(p.getProperty("login")); //$NON-NLS-1$ //$NON-NLS-2$
		e.element("password").setText(p.getProperty("password")); //$NON-NLS-1$ //$NON-NLS-2$
		e.element("portNumber").setText(p.getProperty("port")); //$NON-NLS-1$ //$NON-NLS-2$
		e.element("dataBaseName").setText(p.getProperty("dataBase")); //$NON-NLS-1$ //$NON-NLS-2$

		if (p.getProperty("schemaName") != null) { //$NON-NLS-1$
			if (e.element("schemaName") == null) { //$NON-NLS-1$
				e.addElement("schemaName"); //$NON-NLS-1$
			}
			e.element("schemaName").setText(p.getProperty("schemaName")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		e.element("driverName").setText(p.getProperty("driver")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void generateCust(Element e, Properties p) {
		if (e.getName().equals("property")) { //$NON-NLS-1$
			e.setText(p.getProperty("repositoryUrl")); //$NON-NLS-1$
		}
	}

	private void generateFdDico(Element e, Properties p) {
		if (e.element("odaExtensionDataSourceId").getStringValue().equals("bpm.metadata.birt.oda.runtime")) { //$NON-NLS-1$ //$NON-NLS-2$

			for (Object _o : e.elements("publicProperty")) { //$NON-NLS-1$
				Element _p = (Element) _o;
				if ("URL".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
					_p.setText(p.getProperty("repositoryUrl")); //$NON-NLS-1$
				}
			}
		}
		else if (e.element("odaExtensionDataSourceId").getStringValue().equals("org.eclipse.birt.report.data.oda.jdbc")) { //$NON-NLS-1$ //$NON-NLS-2$
			for (Object _o : e.elements("publicProperty")) { //$NON-NLS-1$
				Element _p = (Element) _o;
				if ("odaDriverClass".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
					_p.setText(p.getProperty("driver")); //$NON-NLS-1$
				}
				if ("odaURL".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
					_p.setText(p.getProperty("host")); //$NON-NLS-1$
				}
				if ("odaPassword".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
					_p.setText(p.getProperty("password")); //$NON-NLS-1$
				}
				if ("odaUser".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
					_p.setText(p.getProperty("login")); //$NON-NLS-1$
				}
			}
		}
	}

	private void generateGateway(Element e, Properties p) {
		Element _e = null;
		if ((_e = e.element("host")) != null) { //$NON-NLS-1$
			_e.setText(p.getProperty("host")); //$NON-NLS-1$
		}
		if ((_e = e.element("port")) != null) { //$NON-NLS-1$
			_e.setText(p.getProperty("port")); //$NON-NLS-1$
		}
		if ((_e = e.element("dataBaseName")) != null) { //$NON-NLS-1$
			_e.setText(p.getProperty("dataBase")); //$NON-NLS-1$
		}
		if ((_e = e.element("driverName")) != null) { //$NON-NLS-1$
			_e.setText(p.getProperty("driver")); //$NON-NLS-1$
		}
		if ((_e = e.element("login")) != null) { //$NON-NLS-1$
			_e.setText(p.getProperty("login")); //$NON-NLS-1$
		}
		if ((_e = e.element("password")) != null) { //$NON-NLS-1$
			_e.setText(p.getProperty("password")); //$NON-NLS-1$
		}
		if ((_e = e.element("url")) != null) { //$NON-NLS-1$
			_e.setText(p.getProperty("repositoryUrl")); //$NON-NLS-1$
		}

	}

	private void generateFd(Element e, Properties p) {
		if (e.getName().equals("datasource-fmdt")) { //$NON-NLS-1$
			e.element("repositoryUrl").setText(p.getProperty("repositoryUrl")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if (e.getName().equals("")) { //$NON-NLS-1$

			String url = CompositeConnectionSql.driverPrefix.get(p.getProperty("driver")) + p.getProperty("host") + ":" + p.getProperty("port") + "/" + p.getProperty("dataBase"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			e.element("user").setText(p.getProperty("login")); //$NON-NLS-1$ //$NON-NLS-2$
			e.element("password").setText(p.getProperty("password")); //$NON-NLS-1$ //$NON-NLS-2$
			e.element("schemaName").setText(p.getProperty("schemaName")); //$NON-NLS-1$ //$NON-NLS-2$
			e.element("driver").setText(p.getProperty("driver")); //$NON-NLS-1$ //$NON-NLS-2$
			e.element("url").setText(url); //$NON-NLS-1$

		}
	}

	private void generateFasd(Element e, Properties p) {
		if (e.getName().equals("XMLAConnection")) { //$NON-NLS-1$
			e.element("url").setText(p.getProperty("host")); //$NON-NLS-1$ //$NON-NLS-2$
			e.element("user").setText(p.getProperty("login")); //$NON-NLS-1$ //$NON-NLS-2$
			e.element("password").setText(p.getProperty("password")); //$NON-NLS-1$ //$NON-NLS-2$

		}
		else {
			e.element("password").setText(p.getProperty("password")); //$NON-NLS-1$ //$NON-NLS-2$
			e.element("user").setText(p.getProperty("login")); //$NON-NLS-1$ //$NON-NLS-2$
			e.element("driver").setText(p.getProperty("driver")); //$NON-NLS-1$ //$NON-NLS-2$
			String url = CompositeConnectionSql.driverPrefix.get(p.getProperty("driver")) + p.getProperty("host") + ":" + p.getProperty("port") + "/" + p.getProperty("dataBase"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			e.element("url").setText(url); //$NON-NLS-1$

		}
	}

	private void launch(List<ImportItem> list) {
		for (ImportItem it : list) {
			try {
				parseFile(it);

				for (Properties p : details.get(it).values()) {
					if ("repository".equals(p.getProperty("type"))) { //$NON-NLS-1$ //$NON-NLS-2$
						p.setProperty("repositoryUrl", repositoryTxt.getText()); //$NON-NLS-1$
					}
					else {
						String url = databaseTxt.getText();

						String h = null;
						try {
							h = url.substring(url.indexOf("://") + 3, url.lastIndexOf(":")); //$NON-NLS-1$ //$NON-NLS-2$
						} catch (Exception ex) {
							h = url.substring(url.indexOf("://") + 3, url.lastIndexOf("/")); //$NON-NLS-1$ //$NON-NLS-2$
						}

						int i = url.lastIndexOf(":"); //$NON-NLS-1$

						String pt = null;
						if (i == url.indexOf(":")) { //$NON-NLS-1$
							pt = ""; //$NON-NLS-1$
						}
						else {
							pt = url.substring(i + 1, url.substring(i + 1).indexOf("/") + i + 1); //$NON-NLS-1$
						}

						String db = url.substring(url.lastIndexOf("/") + 1); //$NON-NLS-1$

						p.setProperty("host", h); //$NON-NLS-1$
						p.setProperty("port", pt); //$NON-NLS-1$
						p.setProperty("dataBase", db); //$NON-NLS-1$

						// get the driverName
						for (String s : CompositeConnectionSql.driverPrefix.keySet()) {
							if (url.startsWith(CompositeConnectionSql.driverPrefix.get(s))) {

								if (it.getType() == IRepositoryApi.FMDT_TYPE) {
									p.setProperty("driver", CompositeConnectionSql.driverName.get(s)); //$NON-NLS-1$
								}
								else {
									p.setProperty("driver", s); //$NON-NLS-1$
								}

								break;
							}
						}

					}
				}
				updateXml(it);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void findReplace(List<ImportItem> list) {
		for (ImportItem it : list) {
			String key = null;
			for (String s : map.keySet()) {
				if (s.equals(it.getId() + "")) { //$NON-NLS-1$
					key = s;
					break;
				}
			}

			File file = map.get(key);
			String txt;
			try {
				txt = IOUtils.toString(new FileInputStream(file), "UTF-8"); //$NON-NLS-1$

				txt = txt.replace(find.getText(), replace.getText());

				DocumentHelper.parseText(txt);

				PrintWriter pw = new PrintWriter(file, "UTF-8"); //$NON-NLS-1$
				pw.write(txt);
				pw.close();

				// reparse the newly generated file
				try {

					details.put(it, parseFile(it));
				} catch (Exception e) {
					
					e.printStackTrace();
				}

				tree.setInput(treeMap.get(it));

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), "Cannot Perform", "Cannot perform this operation, it break the XML model definition for Item " + it.getName() + ". Do it manually."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

		}
	}
}
