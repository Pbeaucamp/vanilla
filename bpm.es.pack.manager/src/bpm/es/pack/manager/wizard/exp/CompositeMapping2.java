package bpm.es.pack.manager.wizard.exp;
//package bpm.es.pack.manager.exp;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Properties;
//
//import org.dom4j.Document;
//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;
//import org.eclipse.jface.viewers.ISelectionChangedListener;
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.jface.viewers.SelectionChangedEvent;
//import org.eclipse.jface.viewers.TreeViewer;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Label;
//
//import adminbirep.Activator;
//import adminbirep.Messages;
//import bpm.birep.admin.client.trees.TreeContentProvider;
//import bpm.birep.admin.client.trees.TreeLabelProvider;
//import bpm.birep.admin.client.trees.TreeParent;
//import bpm.es.pack.manager.imp.CompositeConnectionSql;
//import bpm.es.pack.manager.imp.CompositeInformation;
//import bpm.es.pack.manager.imp.CompositeRepository;
//import bpm.vanilla.platform.core.IRepositoryApi;
//import bpm.vanilla.platform.core.beans.pack.old.ImportItem;
//import bpm.vanilla.platform.core.repository.IRepository;
//import bpm.vanilla.platform.core.repository.Repository;
//
//public class CompositeMapping2 extends Composite {
//
//	protected TreeViewer tree;
//	protected CompositeInformation dataSource;
//	protected Composite composite;
//	private ImportItem item;
//
//	private IRepository repository;
//
//	private HashMap<ImportItem, HashMap<Element, Properties>> details = new HashMap<ImportItem, HashMap<Element, Properties>>();
//	private HashMap<ImportItem, TreeParent> treeMap = new HashMap<ImportItem, TreeParent>();
//
//	public CompositeMapping2(Composite parent, int style) {
//		super(parent, style);
//
//		buildContent();
//	}
//
//	private void buildContent() {
//		this.setLayout(new GridLayout(2, true));
//
//		Label l = new Label(this, SWT.NONE);
//		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
//		l.setText(Messages.Client_Import_CompositeMapping2_0);
//
//		Label l3 = new Label(this, SWT.NONE);
//		l3.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
//		l3.setText(Messages.Client_Import_CompositeMapping2_1);
//
//		tree = new TreeViewer(this, SWT.BORDER | SWT.V_SCROLL);
//		tree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//		tree.setContentProvider(new TreeContentProvider());
//		tree.setLabelProvider(new TreeLabelProvider());
//		tree.addSelectionChangedListener(new ISelectionChangedListener() {
//
//			public void selectionChanged(SelectionChangedEvent event) {
//				if (dataSource != null && !dataSource.isDisposed()) {
//					dataSource.dispose();
//				}
//
//				IStructuredSelection ss = (IStructuredSelection) tree.getSelection();
//				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeElement)) {
//					composite.layout();
//					return;
//				}
//
//				TreeElement el = (TreeElement) ss.getFirstElement();
//
//				Properties p = details.get(item).get(el.key);
//				if (p == null) {
//					composite.layout();
//					return;
//				}
//
//				if (p.get("type") != null) { //$NON-NLS-1$
//					if (p.get("type").equals("xmla")) { //$NON-NLS-1$ //$NON-NLS-2$
//						composite.layout();
//						return;
//					}
//					else {
//						dataSource = new CompositeRepository(composite, SWT.NONE);
//						dataSource.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
//						dataSource.setProperties(p);
//						composite.layout();
//					}
//
//				}
//				else {
//					dataSource = new CompositeConnectionSql(composite, SWT.NONE);
//					dataSource.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
//					dataSource.setProperties(p);
//					composite.layout();
//
//				}
//
//			}
//
//		});
//
//		composite = new Composite(this, SWT.NONE);
//		composite.setLayout(new GridLayout());
//		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//
//	}
//
//	public void setImportItem(Repository repsoitory, ImportItem it) {
//		this.item = it;
//		this.repository = repsoitory;
//		if (details.get(item) == null) {
//			try {
//				details.put(item, parseFile(it));
//			} catch (Exception e) {
//				
//				e.printStackTrace();
//			}
//
//		}
//		else {
//			tree.setInput(treeMap.get(it));
//		}
//	}
//
//	private HashMap<Element, Properties> parseFile(ImportItem it) throws Exception {
//
//		HashMap<Element, Properties> details = new HashMap<Element, Properties>();
//		String text = Activator.getDefault().getRepositoryApi().getRepositoryService().loadModel(repository.getItem(item.getId()));
//		Document doc = DocumentHelper.parseText(text);
//		Element root = doc.getRootElement();
//
//		TreeParent t = null;
//
//		switch (it.getType()) {
//			case IRepositoryApi.FASD_TYPE:
//				t = parseFasd(root, details);
//				tree.setInput(t);
//				break;
//			case IRepositoryApi.FD_TYPE:
//				t = parseFd(root, details);
//				tree.setInput(t);
//				break;
//			case IRepositoryApi.FMDT_TYPE:
//				t = parseFmdt(root, details);
//				tree.setInput(t);
//				break;
//			case IRepositoryApi.FD_DICO_TYPE:
//				t = parseFdDictionary(root, details);
//				tree.setInput(t);
//				break;
//			case IRepositoryApi.GTW_TYPE:
//				t = parseGateway(root, details);
//				break;
//			case IRepositoryApi.BIW_TYPE:
//				// t = parseWorkflow();
//				break;
//			case IRepositoryApi.CUST_TYPE:
//				t = parseCustom(root, details);
//				tree.setInput(t);
//				break;
//			case IRepositoryApi.FWR_TYPE:
//				tree.setInput(new TreeParent("")); //$NON-NLS-1$
//				break;
//		}
//
//		if (t != null) {
//			treeMap.put(it, t);
//		}
//		else {
//			treeMap.put(it, new TreeParent("")); //$NON-NLS-1$
//		}
//
//		return details;
//	}
//
//	private TreeParent parseFdDictionary(Element root, HashMap<Element, Properties> details) {
//		TreeParent troot = new TreeParent(""); //$NON-NLS-1$
//
//		for (Object o : root.elements("dataSource")) { //$NON-NLS-1$
//			Element e = (Element) o;
//			TreeElement te = new TreeElement(e.element("name").getStringValue(), e); //$NON-NLS-1$
//			Properties p = new Properties();
//			p.put("name", e.element("name").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//			if (((Element) o).element("odaExtensionDataSourceId").getStringValue().equals("bpm.metadata.birt.oda.runtime")) { //$NON-NLS-1$ //$NON-NLS-2$
//
//				for (Object _o : e.elements("publicProperty")) { //$NON-NLS-1$
//					Element _p = (Element) _o;
//					if ("URL".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
//						p.put("repositoryUrl", _p.getStringValue()); //$NON-NLS-1$
//						p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
//					}
//				}
//
//				details.put(e, p);
//			}
//			else if (((Element) o).element("odaExtensionDataSourceId").getStringValue().equals("org.eclipse.birt.report.data.oda.jdbc")) { //$NON-NLS-1$ //$NON-NLS-2$
//
//				p.put("name", e.element("name").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				for (Object _o : e.elements("publicProperty")) { //$NON-NLS-1$
//					Element _p = (Element) _o;
//					if ("odaDriverClass".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
//						p.put("driver", _p.getStringValue()); //$NON-NLS-1$
//					}
//					if ("odaURL".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
//						String url = _p.getStringValue();
//
//						p.put("host", url); //$NON-NLS-1$
//
//					}
//					if ("odaPassword".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
//						p.put("login", _p.getStringValue()); //$NON-NLS-1$
//					}
//					if ("odaUser".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
//						p.put("password", _p.getStringValue()); //$NON-NLS-1$
//					}
//
//				}
//
//			}
//			troot.addChild(te);
//			details.put(e, p);
//		}
//		return troot;
//	}
//
//	private TreeParent parseGateway(Element root, HashMap<Element, Properties> details) {
//		TreeParent troot = new TreeParent(""); //$NON-NLS-1$
//		Element rr = root.element("servers"); //$NON-NLS-1$
//		// vanillaServer
//		for (Object vs : rr.elements("vanillaServer")) { //$NON-NLS-1$
//			TreeParent tp = new TreeParent(((Element) vs).element("name").getStringValue()); //$NON-NLS-1$
//			for (Object vc : ((Element) vs).elements("vanillaConnection")) { //$NON-NLS-1$
//				Element e = (Element) vc;
//				TreeElement te = new TreeElement(((Element) vc).element("name").getStringValue(), e); //$NON-NLS-1$
//				Properties p = new Properties();
//				p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
//				details.put(e, p);
//				tp.addChild(te);
//			}
//			troot.addChild(tp);
//		}
//		// repositoryServer
//		for (Object rs : rr.elements("repositoryServer")) { //$NON-NLS-1$
//			TreeParent tp = new TreeParent(((Element) rs).element("name").getStringValue()); //$NON-NLS-1$
//			for (Object rc : ((Element) rs).elements("repositoryConnection")) { //$NON-NLS-1$
//				Element e = (Element) rc;
//				TreeElement te = new TreeElement(((Element) rc).element("name").getStringValue(), e); //$NON-NLS-1$
//				Properties p = new Properties();
//				p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
//				details.put(e, p);
//				tp.addChild(te);
//			}
//			troot.addChild(tp);
//		}
//		// ldapServer
//		for (Object ls : rr.elements("ldapServer")) { //$NON-NLS-1$
//			TreeParent tp = new TreeParent(((Element) ls).element("name").getStringValue()); //$NON-NLS-1$
//			for (Object lc : ((Element) ls).elements("ldapConnection")) { //$NON-NLS-1$
//				Element e = (Element) lc;
//				TreeElement te = new TreeElement(((Element) lc).element("name").getStringValue(), e); //$NON-NLS-1$
//				Properties p = new Properties();
//				p.put("repositoryUrl", e.element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
//				details.put(e, p);
//				tp.addChild(te);
//			}
//			troot.addChild(tp);
//		}
//		// databaseServer
//		for (Object ds : rr.elements("dataBaseServer")) { //$NON-NLS-1$
//			TreeParent tp = new TreeParent(((Element) ds).element("name").getStringValue()); //$NON-NLS-1$
//			for (Object dc : ((Element) ds).elements("dataBaseConnection")) { //$NON-NLS-1$
//				Element e = (Element) dc;
//				TreeElement te = new TreeElement(((Element) dc).element("name").getStringValue(), e); //$NON-NLS-1$
//				Properties p = new Properties();
//				p.put("host", e.element("host").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				p.put("login", e.element("login").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("password", e.element("password").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("port", e.element("port").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("dataBase", e.element("dataBaseName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("driver", e.element("driverName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				details.put(e, p);
//				tp.addChild(te);
//			}
//			troot.addChild(tp);
//		}
//		// freemetricsServer
//		for (Object ds : rr.elements("freemetricsServer")) { //$NON-NLS-1$
//			TreeParent tp = new TreeParent(((Element) ds).element("name").getStringValue()); //$NON-NLS-1$
//			for (Object dc : ((Element) ds).elements("dataBaseConnection")) { //$NON-NLS-1$
//				Element e = (Element) dc;
//				TreeElement te = new TreeElement(((Element) dc).element("name").getStringValue(), e); //$NON-NLS-1$
//				Properties p = new Properties();
//				p.put("host", e.element("host").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				p.put("login", e.element("login").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("password", e.element("password").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("port", e.element("port").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("dataBase", e.element("dataBaseName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("driver", e.element("driverName").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				details.put(e, p);
//				tp.addChild(te);
//			}
//			troot.addChild(tp);
//		}
//
//		return troot;
//	}
//
//	private TreeParent parseFmdt(Element root, HashMap<Element, Properties> details) {
//		// details.clear();
//
//		TreeParent troot = new TreeParent(""); //$NON-NLS-1$
//
//		for (Object o : root.elements("sqlDataSource")) { //$NON-NLS-1$
//			TreeParent tp = new TreeParent(((Element) o).element("name").getText()); //$NON-NLS-1$
//
//			for (Object _o : ((Element) o).elements("sqlConnection")) { //$NON-NLS-1$
//				Element e = (Element) _o;
//				Properties p = new Properties();
//				p.put("name", e.element("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("host", e.element("host").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				p.put("login", e.element("username").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("password", e.element("password").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("port", e.element("portNumber").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("dataBase", e.element("dataBaseName").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				if (e.element("") != null) { //$NON-NLS-1$
//					p.put("schemaName", e.element("schemaName")); //$NON-NLS-1$ //$NON-NLS-2$
//				}
//				else {
//					p.put("schemaName", ""); //$NON-NLS-1$ //$NON-NLS-2$
//				}
//
//				p.put("driver", e.element("driverName").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				details.put(e, p);
//
//				TreeElement te = new TreeElement((String) p.get("name"), e); //$NON-NLS-1$
//				tp.addChild(te);
//
//			}
//			troot.addChild(tp);
//
//		}
//		return troot;
//	}
//
//	private TreeParent parseFasd(Element root, HashMap<Element, Properties> details) {
//		// details.clear();
//
//		TreeParent troot = new TreeParent(""); //$NON-NLS-1$
//
//		for (Object o : root.elements("xmla")) { //$NON-NLS-1$
//
//			Element e = ((Element) o).element("XMLAConnection"); //$NON-NLS-1$
//			Properties p = new Properties();
//			p.put("type", "xmla"); //$NON-NLS-1$ //$NON-NLS-2$
//			p.put("name", e.element("schema-name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//			p.put("host", e.element("url").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//			// p.put("driver", e.element("driver").getText());
//
//			p.put("login", e.element("user").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//			p.put("password", e.element("password").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//
//			details.put(e, p);
//
//			TreeElement te = new TreeElement((String) p.get("name"), e); //$NON-NLS-1$
//
//			troot.addChild(te);
//
//		}
//
//		for (Object o : root.element("datasources").elements("datasource-item")) { //$NON-NLS-1$ //$NON-NLS-2$
//
//			Element e = ((Element) o).element("connection"); //$NON-NLS-1$
//			Properties p = new Properties();
//			p.put("name", ((Element) o).element("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//
//			String url = e.element("url").getText(); //$NON-NLS-1$
//
//			String h = null;
//			try {
//				h = url.substring(url.indexOf("://") + 3, url.lastIndexOf(":")); //$NON-NLS-1$ //$NON-NLS-2$
//			} catch (Exception ex) {
//				h = url.substring(url.indexOf("://") + 3, url.lastIndexOf("/")); //$NON-NLS-1$ //$NON-NLS-2$
//			}
//
//			int i = url.lastIndexOf(":"); //$NON-NLS-1$
//
//			String pt = null;
//			if (i != url.indexOf(":")) { //$NON-NLS-1$
//				pt = ""; //$NON-NLS-1$
//			}
//			else {
//				pt = url.substring(i + 1, url.substring(i + 1).indexOf("/") + i + 1); //$NON-NLS-1$
//			}
//
//			String db = url.substring(url.lastIndexOf("/") + 1); //$NON-NLS-1$
//
//			p.put("host", h); //$NON-NLS-1$
//			p.put("port", pt); //$NON-NLS-1$
//			p.put("dataBase", db); //$NON-NLS-1$
//			p.put("driver", e.element("driver").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//			p.put("login", e.element("user").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//			p.put("password", e.element("password").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//
//			details.put(e, p);
//
//			TreeElement te = new TreeElement((String) p.get("name"), e); //$NON-NLS-1$
//
//			troot.addChild(te);
//
//		}
//
//		return troot;
//	}
//
//	private TreeParent parseFd(Element root, HashMap<Element, Properties> details) {
//		// details.clear();
//
//		List<Element> list = new ArrayList<Element>();
//
//		for (Object o : root.element("dictionary").elements("componentFusionChart")) { //$NON-NLS-1$ //$NON-NLS-2$
//			list.add((Element) o);
//		}
//
//		for (Object o : root.element("dictionary").elements("componentCombo")) { //$NON-NLS-1$ //$NON-NLS-2$
//			Element e = (Element) o;
//			if (e.element("filterCombo") != null && e.element("filterCombo").element("data-fmdt") != null) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//				list.add(e);
//			}
//		}
//
//		for (Element e : list) {
//			Element _e = null;
//			if (e.element("data-fmdt") != null) { //$NON-NLS-1$
//				_e = e.element("data-fmdt").element("datasource-fmdt"); //$NON-NLS-1$ //$NON-NLS-2$
//
//			}
//
//			else if (e.element("filterCombo") != null) { //$NON-NLS-1$
//				_e = e.element("filterCombo").element("data-fmdt").element("datasource-fmdt"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			}
//			// System.out.println(e.asXML());
//			if (_e != null) {
//				Properties p = new Properties();
//				p.put("name", e.element("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("repositoryUrl", _e.element("repositoryUrl").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				details.put(_e, p);
//
//			}
//
//		}
//
//		// coming from database
//		for (Object o : root.element("dictionary").elements("componentCombo")) { //$NON-NLS-1$ //$NON-NLS-2$
//			Element e = (Element) o;
//			if (e.element("filterSlider") != null && e.element("filterSlider").element("datas").element("datasource-jndi") != null) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//
//				Element _e = e.element("filterSlider").element("datas").element("datasource-jndi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//
//				String url = _e.element("url").getText(); //$NON-NLS-1$
//
//				String h = url.substring(url.indexOf("://") + 3, url.lastIndexOf("/")); //$NON-NLS-1$ //$NON-NLS-2$
//				int i = url.lastIndexOf(":"); //$NON-NLS-1$
//				String pt = url.substring(i + 1, url.substring(i + 1).indexOf("/") + i + 1); //$NON-NLS-1$
//				String db = url.substring(url.lastIndexOf("/") + 1); //$NON-NLS-1$
//
//				Properties p = new Properties();
//				p.put("name", e.element("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("host", h); //$NON-NLS-1$
//
//				p.put("login", _e.element("user").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("password", _e.element("password").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("port", pt); //$NON-NLS-1$
//				p.put("dataBase", db); //$NON-NLS-1$
//
//				p.put("schemaName", _e.element("schemaName") != null ? _e.element("schemaName").getText() : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//				p.put("driver", _e.element("driver").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				details.put(_e, p);
//
//			}
//			if (e.element("filterCombo") != null && e.element("filterCombo").element("datas") != null && e.element("filterCombo").element("datas").element("datasource-jndi") != null) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
//				Element _e = e.element("filterCombo").element("datas").element("datasource-jndi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//
//				String url = _e.element("url").getText(); //$NON-NLS-1$
//
//				String h = url.substring(url.indexOf("://") + 3, url.lastIndexOf("/")); //$NON-NLS-1$ //$NON-NLS-2$
//				int i = url.lastIndexOf(":"); //$NON-NLS-1$
//				String pt = url.substring(i + 1, url.substring(i + 1).indexOf("/") + i + 1); //$NON-NLS-1$
//				String db = url.substring(url.lastIndexOf("/") + 1); //$NON-NLS-1$
//
//				Properties p = new Properties();
//				p.put("name", e.element("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("host", h); //$NON-NLS-1$
//
//				p.put("login", _e.element("user").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("password", _e.element("password").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//				p.put("port", pt); //$NON-NLS-1$
//				p.put("dataBase", db); //$NON-NLS-1$
//
//				p.put("schemaName", _e.element("schemaName") != null ? _e.element("schemaName").getText() : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//				p.put("driver", _e.element("driver").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//
//				details.put(_e, p);
//
//			}
//		}
//
//		TreeParent tRoot = new TreeParent(""); //$NON-NLS-1$
//
//		for (Element e : details.keySet()) {
//			TreeElement te = new TreeElement(details.get(e).getProperty("name"), e); //$NON-NLS-1$
//			tRoot.addChild(te);
//		}
//
//		return tRoot;
//	}
//
//	private class TreeElement extends TreeParent {
//		private Element key;
//
//		public TreeElement(String name, Element e) {
//			super(name);
//			key = e;
//		}
//	}
//
//	private TreeParent parseCustom(Element root, HashMap<Element, Properties> details) {
//		// details.clear();
//
//		if (!root.getName().equals("report")) { //$NON-NLS-1$
//			return null;
//		}
//
//		TreeParent troot = new TreeParent(""); //$NON-NLS-1$
//
//		for (Object o : root.element("data-sources").elements("oda-data-source")) { //$NON-NLS-1$ //$NON-NLS-2$
//
//			Element e = (Element) o;
//
//			if (!e.attribute("extensionID").getText().equals("bpm.metadata.birt.oda.runtime")) { //$NON-NLS-1$ //$NON-NLS-2$
//				break;
//			}
//
//			Properties p = new Properties();
//			p.put("name", e.attribute("name").getText()); //$NON-NLS-1$ //$NON-NLS-2$
//
//			for (Object _o : e.elements("property")) { //$NON-NLS-1$
//				Element _p = (Element) _o;
//				if ("URL".equals(_p.attribute("name").getText())) { //$NON-NLS-1$ //$NON-NLS-2$
//					p.put("repositoryUrl", _p.getText()); //$NON-NLS-1$
//					p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
//					details.put(_p, p);
//
//					TreeElement te = new TreeElement((String) p.get("name"), _p); //$NON-NLS-1$
//
//					troot.addChild(te);
//
//					break;
//				}
//			}
//
//		}
//
//		return troot;
//	}
//
//}
