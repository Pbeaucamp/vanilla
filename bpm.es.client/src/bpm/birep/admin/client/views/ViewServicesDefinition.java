package bpm.birep.admin.client.views;


/**
 * @deprecated : has been migrated within HyperVision
 * @author ludo
 * delete candidate
 *
 */
public class ViewServicesDefinition{}
//extends ViewPart {
//	public static final String ID = "bpm.birep.admin.client.views.ViewServicesDefinition";
//	public static final Font FONT_ACTIVE_CONNECTION = new Font(Display.getCurrent(), "Arial", 8, SWT.BOLD | SWT.ITALIC);
//
//	
//	private TreeParent securityServices = new TreeParent<String>("Identification Services");
//	private TreeParent designerServices = new TreeParent<String>("Vanilla Designer Services");
//	
//	
//	
//	
//	private TreeParent repositoryServices = new TreeParent<String>("Repository Services");
//	private TreeParent gatewayServerUrl = new TreeParent<String>("Gateway Services");
//	private TreeParent schedulerServer = new TreeParent<String>("Scheduling Services");
//
//	
//	private TreeParent viewerServices  = new TreeParent<String>("Restitution Viewer Services");;
//	
//	
//	private TreeParent freeMetricsServer  = new TreeParent<String>("KPI Services");;
//	
//	
//	
//	
//	
//	private TreeViewer treeViewer;
//	private ToolItem delItem;
//	
//	public ViewServicesDefinition() {
//		
//	}
//
//	@Override
//	public void createPartControl(Composite parent) {
//		Composite main = new Composite(parent, SWT.NONE);
//		main.setLayout(new GridLayout(2, false));
//		main.setLayoutData(new GridData(GridData.FILL_BOTH));
//		
//		createToolbar(main);
//		
//		treeViewer = new TreeViewer(main, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
//		treeViewer.setContentProvider(new TreeContentProvider());
//		treeViewer.setLabelProvider(new DecoratingLabelProvider(new TreeLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()){
//
//			@Override
//			public Font getFont(Object element) {
//				if (element instanceof TreeObject && ((TreeObject)element).getData() instanceof Server){
//					
//					if (((TreeObject<Server>)element).getData().getId() == -1){
//						return FONT_ACTIVE_CONNECTION;
//					}
//					
//				}
//				return super.getFont(element);
//			}
//
//			@Override
//			public Image getImage(Object obj) {
//				if (obj instanceof TreeObject && ((TreeObject)obj).getData() instanceof Server){
//					ImageRegistry reg = Activator.getDefault().getImageRegistry();
//					Server s = ((TreeObject<Server>)obj).getData();
//					
//					if (s.getType() == Server.TYPE_FAWEB){
//						return reg.get("fasd");
//					}
//					else if (s.getType() == Server.TYPE_FDWEB){
//						return reg.get("fd");
//					}else if (s.getType() == Server.TYPE_FWR){
//						return reg.get("fwr");
//					}else if (s.getType() == Server.TYPE_GATEWAY){
//						return reg.get("gtw");
//					}
//					else if (s.getType() == Server.TYPE_BIRT){
//						return reg.get("birt");
//					}
//					else if (s.getType() == Server.TYPE_SCHEDULER){
//						return reg.get("sched");
//					}
//					else if (s.getType() == Server.TYPE_SECURITY){
//						return reg.get("secu");
//					}
//				}
//				return super.getImage(obj);
//			}
//			
//		});
//		treeViewer.addSelectionChangedListener(new ISelectionChangedListener(){
//
//			public void selectionChanged(SelectionChangedEvent event) {
//				IStructuredSelection ss = (IStructuredSelection)event.getSelection();
//				
//				if (ss.isEmpty()){
//					delItem.setEnabled(false);
//				}
//				else if (ss.getFirstElement() instanceof TreeObject<?> && ((TreeObject<?>)ss.getFirstElement()).getData() instanceof Server){
//					Server s = ((TreeObject<Server>)ss.getFirstElement()).getData();
//					delItem.setEnabled(s.getId() != -1);
//					
//				}
//				else{
//					delItem.setEnabled(false);
//				}
//				
//			}
//			
//		});
//	
//		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
//		
//		getSite().setSelectionProvider(treeViewer);
//		initViewer();
//	}
//	
//	
//	private void createToolbar(Composite parent){
//		ToolBar bar = new ToolBar(parent, SWT.HORIZONTAL);
//		bar.setLayout(new GridLayout());
//		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,2, 1));
//
//		ImageRegistry reg = Activator.getDefault().getImageRegistry();
//		
//		ToolItem add = new ToolItem(bar, SWT.PUSH);
//		add.setImage(reg.get("add"));
//		add.setToolTipText("Create Server");
//		add.addSelectionListener(new SelectionAdapter(){
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				DialogServer d = new DialogServer(getSite().getShell());
//				if (d.open() == DialogServer.OK){
//					Server srv = d.getServer();
//					switch(srv.getType()){
//					case Server.TYPE_BIRT:
//					case Server.TYPE_FAWEB:
//						viewerServices.addChild(new TreeObject<Server>(srv.getName(), srv));
//						break;
//					case Server.TYPE_FDWEB:
//					case Server.TYPE_FWR:
//						designerServices.addChild(new TreeObject<Server>(srv.getName(), srv));
//						break;
//					case Server.TYPE_GATEWAY:
//						gatewayServerUrl.addChild(new TreeObject<Server>(srv.getName(), srv));
//						break;
//					case Server.TYPE_ORBEON:
//						break;
//					case Server.TYPE_SCHEDULER:
//						schedulerServer.addChild(new TreeObject<Server>(srv.getName(), srv));
//						break;				
//					}
//					treeViewer.refresh();
//				}
//			}
//			
//		});
//		
//		delItem = new ToolItem(bar, SWT.PUSH);
//		delItem.setImage(reg.get("del"));
//		delItem.setToolTipText("Delete Server");
//		delItem.addSelectionListener(new SelectionAdapter(){
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				IStructuredSelection ss = (IStructuredSelection)treeViewer.getSelection();
//				
//				if (ss.getFirstElement() instanceof TreeObject && ((TreeObject)ss.getFirstElement()).getData() instanceof Server){
//					Server s = ((TreeObject<Server>)ss.getFirstElement()).getData();
//					
//					if (s.getId() != -1){
//						try{
//							Activator.getDefault().getManager().deleteServer(s);
//							((TreeObject<?>)ss.getFirstElement()).getParent().removeChild((TreeObject<?>)ss.getFirstElement());
//							treeViewer.refresh();
//						}catch(Exception ex){
//							ex.printStackTrace();
//							MessageDialog.openError(getSite().getShell(), "Unable to delete object", ex.getMessage());
//						}
//						
//					}
//					
//					
//				}
//			}
//			
//		});
//		
//		
//		ToolItem refresh = new ToolItem(bar, SWT.PUSH);
//		refresh.setToolTipText("Refresh");
//		refresh.setImage(reg.get("refresh"));
//		refresh.addSelectionListener(new SelectionAdapter(){
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				initViewer();
//			}
//			
//		});
//	}
//	
//	private void initViewer(){
//		//cleaned the trees entries
//		securityServices.removeAllChild();
//		designerServices.removeAllChild();
//		gatewayServerUrl.removeAllChild();
//		schedulerServer.removeAllChild();
//		viewerServices.removeAllChild();
//		repositoryServices.removeAllChild();
//		
//		VanillaSetup vSetup = null;
//		try {
//			vSetup = Activator.getDefault().getManager().getVanillaSetup();
//		} catch (Exception e2) {
//			e2.printStackTrace();
//			return;
//		}
//		
//		Server s = new Server();
//		s.setName("Vanilla Server");
//		s.setUrl(vSetup.getVanillaServer());
//		s.setId(-1);
//		TreeParent main = new TreeParent("");
//		
//		TreeParent<Server> root = new TreeParent<Server>(s.getName(), s);
//		main.addChild(root);
//		root.addChild(securityServices);
//		root.addChild(designerServices);
//		root.addChild(gatewayServerUrl);
//		root.addChild(schedulerServer);
//		root.addChild(viewerServices);
//		root.addChild(repositoryServices);
//		
//		if (vSetup.getFasdWebServer() != null && !"".equals(vSetup.getFasdWebServer())){
//			s = new Server();
//			s.setName("FreeAnalysisSchemaDesignerWeb");
//			s.setUrl(vSetup.getFasdWebServer());
//			s.setId(-1);
//			s.setType(Server.TYPE_FAWEB);
//			designerServices.addChild(new TreeObject<Server>(s.getName(), s));
//		}
//		
//		if (vSetup.getFreeAnalysisServer() != null && !"".equals(vSetup.getFreeAnalysisServer())){
//			s = new Server();
//			s.setName("FreeAnalysisWeb");
//			s.setUrl(vSetup.getFreeAnalysisServer());
//			s.setId(-1);
//			s.setType(Server.TYPE_FAWEB);
//			viewerServices.addChild(new TreeObject<Server>(s.getName(), s));
//		}
//		
//		if (vSetup.getVanillaRuntimeServersUrl() != null && !"".equals(vSetup.getVanillaRuntimeServersUrl())){
//			s = new Server();
//			s.setName("FreeDashboardWeb");
//			s.setUrl(vSetup.getVanillaRuntimeServersUrl());
//			s.setId(-1);
//			s.setType(Server.TYPE_FDWEB);
//			designerServices.addChild(new TreeObject<Server>(s.getName(), s));
//		}
//		
//		if (vSetup.getFreeWebReportServer() != null && !"".equals(vSetup.getFreeWebReportServer())){
//			s = new Server();
//			s.setName("FreeWebReport");
//			s.setUrl(vSetup.getFreeWebReportServer());
//			s.setId(-1);
//			s.setType(Server.TYPE_FWR);
//			designerServices.addChild(new TreeObject<Server>(s.getName(), s));
//		}
//		
//		if (vSetup.getGatewayServerUrl() != null && !"".equals(vSetup.getGatewayServerUrl())){
//			s = new Server();
//			s.setName("GatewayServer");
//			s.setUrl(vSetup.getGatewayServerUrl());
//			s.setId(-1);
//			s.setType(Server.TYPE_GATEWAY);
//			gatewayServerUrl.addChild(new TreeObject<Server>(s.getName(), s));
//		}
//		
//		if (vSetup.getSchedulerServer() != null && !"".equals(vSetup.getSchedulerServer())){
//			s = new Server();
//			s.setName("SchedulerServer");
//			s.setUrl(vSetup.getSchedulerServer());
//			s.setId(-1);
//			s.setType(Server.TYPE_SCHEDULER);
//			schedulerServer.addChild(new TreeObject<Server>(s.getName(), s));
//		}
//		
//		if (vSetup.getSecurityServer() != null && !"".equals(vSetup.getSecurityServer())){
//			s = new Server();
//			s.setName("VanillaSecurity");
//			s.setUrl(vSetup.getSecurityServer());
//			s.setId(-1);
//			s.setType(Server.TYPE_SECURITY);
//			securityServices.addChild(new TreeObject<Server>(s.getName(), s));
//		}
//		
//		if (vSetup.getBirtViewer() != null && !"".equals(vSetup.getBirtViewer())){
//			s = new Server();
//			s.setName("BirtViewer");
//			s.setUrl(vSetup.getBirtViewer());
//			s.setId(-1);
//			s.setType(Server.TYPE_BIRT);
//			viewerServices.addChild(new TreeObject<Server>(s.getName(), s));
//		}
//		try {
//			for(Repository r : Activator.getDefault().getManager().getRepositories()){
//				s = new Server();
//				s.setName(r.getName());
//				s.setUrl(r.getUrl());
//				s.setId(-1);
//				s.setType(Server.TYPE_REPOSITORY);
//				repositoryServices.addChild(new TreeObject<Server>(s.getName(), s));
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		
//		for (int i=0; i<Server.TYPE_NAMES.length; i++){
//			try{
//				for(Server srv  : Activator.getDefault().getManager().getServer(i)){
//					switch(i){
//						case Server.TYPE_BIRT:
//						case Server.TYPE_FAWEB:
//							viewerServices.addChild(new TreeObject<Server>(srv.getName(), srv));
//							break;
//						case Server.TYPE_FDWEB:
//						case Server.TYPE_FWR:
//							designerServices.addChild(new TreeObject<Server>(srv.getName(), srv));
//							break;
//						case Server.TYPE_GATEWAY:
//							gatewayServerUrl.addChild(new TreeObject<Server>(srv.getName(), srv));
//							break;
//						case Server.TYPE_ORBEON:
//							break;
//						case Server.TYPE_SCHEDULER:
//							schedulerServer.addChild(new TreeObject<Server>(srv.getName(), srv));
//							break;
//					
//					}
//				}
//			}catch(Exception e){
//				
//			}
//				
//		}
//			
//
//		treeViewer.setInput(main);
//		treeViewer.expandAll();
//	}
//	
//
//	@Override
//	public void setFocus() {
//		
//
//	}
//
//}
