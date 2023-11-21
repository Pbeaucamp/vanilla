package bpm.es.dndserver.views.composites;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.es.dndserver.Activator;
import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.DNDOProject;
import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.es.dndserver.api.repository.RepositoryWrapper;
import bpm.es.dndserver.icons.IconsName;
import bpm.es.dndserver.preferences.PreferenceConstants;
import bpm.es.dndserver.tools.OurLogger;
import bpm.es.dndserver.tools.RepositoryObjectTypeAuthorizer;
import bpm.es.dndserver.views.dialogs.DialogCreateDirectory;
import bpm.es.dndserver.views.providers.RepositoryContentProvider;
import bpm.es.dndserver.views.providers.RepositoryLabelProvider;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class RepositoryViewer extends Composite {

	public static int TYPE_SOURCE = 0;
	public static int TYPE_TARGET = 1;
	
	private FormToolkit toolkit;
	
	private DNDOProject project;
	
	private RepositoryWrapper currentRepository;
	
	private int currentType = 0;
	
	private TreeViewer viewer;
	
	private Text tuser, tpass, turl;
	
	private Combo cRepo;
	private Combo groupName;
	
	private Button connect;
	
	private boolean isShown = false;
	
	//temp storage
	private List<Repository> definitions;
	private String defaultRepository;
	/**
	 * 
	 * @param parent
	 * @param style
	 * @param type see TYPE_SOURCE and TYPE_TARGET
	 * @param toolkit
	 */
	public RepositoryViewer(Composite parent, int style, int type, FormToolkit toolkit) {
		super(parent, style);
		
		setLayoutData(new GridData(GridData.FILL_BOTH));
		setLayout(new GridLayout());
		this.currentType = type;
		
		
		
		this.toolkit = toolkit;
		toolkit.paintBordersFor(this);
		buildContent();
	}

	private void buildContent() {
		
		Section section = toolkit.createSection(this, Section.TITLE_BAR);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setLayout(new GridLayout());
		if (currentType == TYPE_SOURCE)
			section.setText(Messages.RepositoryViewer_0);
		else 
			section.setText(Messages.RepositoryViewer_1);



		Composite main = toolkit.createComposite(section);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());
		
		createInfoBar(main);
		createTree(main);

		toolkit.paintBordersFor(main);
		
		section.setClient(main);
		
		if (TYPE_TARGET == this.currentType){
			setupTargetDND();
		}
		else{
			setupSourceDND();
		}
	}

	private void createInfoBar(Composite main) {
		Section infoBar = toolkit.createSection(main, 
				Section.EXPANDED |  
				Section.SHORT_TITLE_BAR |
				Section.TWISTIE);
		infoBar.setText(Messages.RepositoryViewer_2);
		infoBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		infoBar.setLayout(new GridLayout());
		
		Composite compositeBar = toolkit.createComposite(infoBar);
		compositeBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeBar.setLayout(new GridLayout(2, false));	
		
		Label l = new Label(compositeBar, SWT.NONE);
		l.setText(Messages.RepositoryViewer_3);

		tuser = new Text(compositeBar, SWT.BORDER);
		tuser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l1 = new Label(compositeBar, SWT.NONE);
		l1.setText(Messages.RepositoryViewer_4);
		
		tpass = new Text(compositeBar, SWT.BORDER | SWT.PASSWORD);
		tpass.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l2 = new Label(compositeBar, SWT.NONE);
		l2.setText(Messages.RepositoryViewer_5);
		
		turl = new Text(compositeBar, SWT.BORDER);
		turl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if (currentType == TYPE_TARGET) {
			Label bidon = new Label(compositeBar, SWT.NULL);
			
			connect = new Button(compositeBar, SWT.PUSH);
			connect.setText(Messages.RepositoryViewer_6);
			connect.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {

					try {
						String user = tuser.getText();
						String pass = tpass.getText();
						String url = turl.getText();
						
						//save new prefs
						IPreferenceStore store = Activator.getDefault().getPreferenceStore();
						
						store.setDefault(PreferenceConstants.TARGET_VANILLA_SERVER, url);
						store.setDefault(PreferenceConstants.TARGET_VANILLA_USER, user);
						store.setDefault(PreferenceConstants.TARGET_VANILLA_PASSWORD, pass);
						
						definitions = project.getRepositories(user, pass, url);
						String[] reps = prepareDataForCombo(definitions);
						//cRepo.setItems(prepareDataForCombo(definitions));
						cRepo.setItems(reps);
						cRepo.select(0);
						if (!defaultRepository.equals("")) { //$NON-NLS-1$
							int index = 0;
							for (String rep : reps) {
								if (rep.equals(defaultRepository)) {
									cRepo.select(index);
								}
								index++;
							}
						}
					} catch (Exception ex) {
						OurLogger.error(Messages.RepositoryViewer_8, ex);
						MessageDialog.openError(Display.getDefault().getActiveShell(), 
								Messages.RepositoryViewer_9, Messages.RepositoryViewer_10 
								+ ex.getMessage());
					}
				}
			});
			
			//connect.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			
			
		}
		
		Label l3 = new Label(compositeBar, SWT.NONE);
		l3.setText(Messages.RepositoryViewer_11);
		
		cRepo = new Combo(compositeBar, SWT.READ_ONLY);
		cRepo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cRepo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					int index = cRepo.getSelectionIndex();
					Repository def = definitions.get(index);
					
					String url = turl.getText();
					String user = tuser.getText();
					String pass = tpass.getText();
					
					IPreferenceStore store = Activator.getDefault().getPreferenceStore();
					
					store.setDefault(PreferenceConstants.TARGET_REPOSITORY_NAME, def.getName());
					
					
					if (currentType == TYPE_TARGET) {
						project.connectToOutputRepository(url, user, pass, def);
						viewer.setInput(project.getOutputRepository());
						currentRepository = project.getOutputRepository();
						
						List<Group> grops = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
						List<String> c = new ArrayList<String>(grops.size());
						for(Group g : grops){
							c.add(g.getName());
						}
						groupName.setItems(c.toArray(new String[c.size()]));
						if (!c.isEmpty()){
							groupName.select(0);
							
							project.getOutputRepository().setGroupName(groupName.getText());
						}
					}
					else{
						project.connectToInputRepository(user, pass, def);
						viewer.setInput(project.getInputRepository());
						currentRepository = project.getInputRepository();
					}
					
					
					
					
					
					
				} catch (Exception ex) {
					OurLogger.error(Messages.RepositoryViewer_12, ex);
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							Messages.RepositoryViewer_13, Messages.RepositoryViewer_14 
							+ ex.getMessage());
				}
			}
		});
		
		
		if (currentType == TYPE_TARGET){
			l3 = new Label(compositeBar, SWT.NONE);
			l3.setText(Messages.RepositoryViewer_15);
			
			groupName = new Combo(compositeBar, SWT.READ_ONLY);
			groupName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			groupName.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					project.getOutputRepository().setGroupName(groupName.getText());
				}
			});
		}
		
		infoBar.setClient(compositeBar);
	}
	
	private void createTree(Composite main) {
		Section treeSection = toolkit.createSection(main, 
				Section.EXPANDED |  
				Section.SHORT_TITLE_BAR |
				Section.TWISTIE);
		treeSection.setText(Messages.RepositoryViewer_16);
		treeSection.setLayoutData(new GridData(GridData.FILL_BOTH));
		treeSection.setLayout(new GridLayout());
		
		viewer = new TreeViewer(treeSection, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		viewer.getTree().setLayout(new GridLayout());
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setContentProvider(new RepositoryContentProvider());
		viewer.setLabelProvider(new RepositoryLabelProvider());		
		
		createRepositoryToolBar(treeSection);
		
		treeSection.setClient(viewer.getControl());
	}
	
	public void setInput(DNDOProject project) throws Exception {
		if (project != this.project){
			this.project = project;
			
			if (currentType == TYPE_TARGET) {
				showTargetRepository(project.getOutputRepository());
			}
			else if (currentType == TYPE_SOURCE){
				showHomeRepository(project.getInputRepository());
			}
		}
		
	}
	
	/**
	 * Automatically show data, show as dnd emitter
	 * @param repository
	 * @throws Exception 
	 */
	private void showHomeRepository(RepositoryWrapper repository) throws Exception {
		
		currentRepository = repository;
//		isShown = false;
//		if (repository == null) {
//			isShown = false;
//			throw new Exception("Source Repository is not initialized, please connect to it\n" +
//					"through the welcome screen");
//		}
		
		bpm.vanilla.server.client.ui.clustering.menu.Activator activator 
			= bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault();
	
		if (activator.getVanillaApi() == null) {
			isShown = false;
			throw new Exception(Messages.RepositoryViewer_17 +
					Messages.RepositoryViewer_18);
		}
		
		
		
		//project.connectToOutputRepository(vanillaLogin, vanillaPassword, def);
		//viewer.setInput(project.getOutputRepository());
		
		viewer.setInput(repository);
		
		tuser.setText(activator.getVanillaContext().getLogin());
		tuser.setEditable(false);
		
		tpass.setText(activator.getVanillaContext().getPassword());
		tpass.setEditable(false);
		
		turl.setText(activator.getVanillaContext().getVanillaUrl());
		turl.setEditable(false);
		
//		String[] repositories = new String[] {repository.getRepositoryName()};
//		cRepo.setItems(repositories);
//		cRepo.select(0);
		
		definitions = project.getRepositories(activator.getVanillaContext().getLogin(), activator.getVanillaContext().getPassword(), activator.getVanillaContext().getVanillaUrl());
		String[] reps = prepareDataForCombo(definitions);
		//cRepo.setItems(prepareDataForCombo(definitions));
		cRepo.setItems(reps);
		cRepo.select(0);
		
		project.connectToInputRepository(activator.getVanillaContext().getLogin(), activator.getVanillaContext().getPassword(), definitions.get(0));
		
		viewer.setInput(project.getInputRepository());
		
		isShown = true;
		
		
		
//		setupSourceDND();
	}
	
	/**
	 * let people choose repository, set as dnd receiver
	 * @param repository
	 */
	private void showTargetRepository(RepositoryWrapper repository) {
		
		
		if (repository == null){
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			
			tuser.setText(store.getString(PreferenceConstants.TARGET_VANILLA_USER));
			tpass.setText(store.getString(PreferenceConstants.TARGET_VANILLA_PASSWORD));
			turl.setText(store.getString(PreferenceConstants.TARGET_VANILLA_SERVER));
			
			defaultRepository = store.getString(PreferenceConstants.TARGET_REPOSITORY_NAME);
		}
		else if (currentRepository != repository){
			
			tuser.setText(repository.getSock().getContext().getVanillaContext().getLogin());
			tpass.setText(repository.getSock().getContext().getVanillaContext().getPassword());
			turl.setText(repository.getSock().getContext().getVanillaContext().getVanillaUrl());
			cRepo.setItems(new String[]{});
		}
		
		currentRepository = repository;
		
		
		
		
		
		
		if (repository != null){
			try {
				refreshRepository();
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
		}
	}
	
	private void createRepositoryToolBar(Section section) {
		ToolBar tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);

		ToolItem tRefresh = new ToolItem(tbar, SWT.NULL);
		tRefresh.setImage(Activator.getDefault().getImageRegistry().get(IconsName.APP_REFRESH));
		tRefresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					refreshRepository();
				} catch (Exception ex) {
					OurLogger.error(Messages.RepositoryViewer_19, ex);
					MessageDialog.openError(Display.getCurrent().getActiveShell(), 
							Messages.RepositoryViewer_20, Messages.RepositoryViewer_21 + ex.getMessage());
				}
			}
		});
		ToolItem tAdd = new ToolItem(tbar, SWT.NULL);
		tAdd.setImage(Activator.getDefault().getImageRegistry().get(IconsName.APP_FOLDER_ADD));
		tAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IRepositoryApi client = currentRepository.getRepositoryClient();
				
				try{
					List<Group> grops = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
					List<String> c = new ArrayList<String>(grops.size());
					for(Group g : grops){
						c.add(g.getName());
					}
					
					DialogCreateDirectory dCreate = new DialogCreateDirectory(
							Display.getCurrent().getActiveShell(), Messages.RepositoryViewer_22, c);
					
					if (dCreate.OK == dCreate.open()) {
						OurLogger.info("creating a directory named '" + dCreate.getName() + Messages.RepositoryViewer_24 + //$NON-NLS-1$
								Messages.RepositoryViewer_25 + dCreate.getSelectedGroup() + "'."); //$NON-NLS-1$
						String dirName = dCreate.getName();
						String dirOwner = dCreate.getSelectedGroup();
						
						Integer dirOwnerId = null;
						for(Group g : grops){
							if (g.getName().equals(dirOwner)){
								dirOwnerId = g.getId();
								break;
							}
						}
						
						try {
							RepositoryDirectory dir = new RepositoryDirectory();
							dir.setComment(""); //$NON-NLS-1$
							dir.setName(dirName);
							dir.setOwnerId(dirOwnerId);
							client.getRepositoryService().addDirectory(dirName, "", dir); //$NON-NLS-1$

							viewer.refresh();
						} catch (Exception e1) {
							OurLogger.error(Messages.RepositoryViewer_28, e1);
							MessageDialog.openError(Display.getCurrent().getActiveShell(), 
									Messages.RepositoryViewer_29, Messages.RepositoryViewer_30 + e1.getMessage());
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.RepositoryViewer_31, Messages.RepositoryViewer_32 + ex.getMessage());
				}
				
				
			}
		});
		
		ToolItem tDel = new ToolItem(tbar, SWT.NULL);
		tDel.setImage(Activator.getDefault().getImageRegistry().get(IconsName.APP_DELETE));
		tDel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IRepositoryApi client = currentRepository.getRepositoryClient();
				
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				
				if (ss.isEmpty())
					return;
				
				Iterator it = ss.iterator();
				
				while (it.hasNext()) {
					Object o = it.next();
					try {
						if (o instanceof RepositoryDirectory) {
							client.getRepositoryService().delete((RepositoryDirectory)o);
						}
						else if (o instanceof RepositoryDirectory) {
							client.getRepositoryService().delete((RepositoryDirectory)o);
						}
						else if (o instanceof AxisDirectoryItemWrapper) {
							currentRepository
								.deleteTemporaryElement((AxisDirectoryItemWrapper)o);
						}
						else {
							OurLogger.info(Messages.RepositoryViewer_33 + o.toString());
						}
					} catch (Exception ex) {
						OurLogger.error(Messages.RepositoryViewer_34, ex);
						MessageDialog.openError(Display.getCurrent().getActiveShell(), 
								Messages.RepositoryViewer_35, Messages.RepositoryViewer_36 + ex.getMessage());
					}
					try {
						refreshRepository();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		section.setTextClient(tbar);
	}
	
	private void setupSourceDND() {
	    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
	    DragSource source = new DragSource(viewer.getControl(), DND.DROP_COPY);
	    source.setTransfer(types);
	    
	    source.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				
				for(Object o : ss.toArray()){
					if (o instanceof RepositoryItem){
						event.doit = true;
						return;
					}
				}
				event.doit = false;
			}
			
	        public void dragSetData(DragSourceEvent event) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				
				List<AxisDirectoryItemWrapper> dnd = new ArrayList<AxisDirectoryItemWrapper>();

				Iterator<Object> it = ss.iterator();
				while (it.hasNext()) {
					//AxisDirectoryItem && AxisDirectory
					Object o = it.next();
					if (o instanceof RepositoryItem) { 
							//|| o instanceof AxisDirectory) {
						AxisDirectoryItemWrapper wrapper = 	new AxisDirectoryItemWrapper((RepositoryItem)o);
						dnd.add(wrapper);
					}
				}
				if (!dnd.isEmpty()) {
					event.data = Messages.RepositoryViewer_37;
					Activator.getDefault().setDndObjects(dnd);
				}
		    }
			
			public void dragFinished(DragSourceEvent event) {
				event.detail = DND.DROP_NONE;
			}
	    });
	}
	
	private void setupTargetDND() {
	    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
	    
	    DropTarget target = new DropTarget(viewer.getControl(), DND.DROP_COPY);
	    target.setTransfer(types);
	    target.addDropListener(new DropTargetAdapter() {
		    public void dragEnter(DropTargetEvent event) {
		    	event.detail = DND.DROP_COPY;
		    }
	
		    public void dragOver(DropTargetEvent event) {
		    	
		    }
	      
		    public void drop(DropTargetEvent event) {
		    	if (project.getOutputRepository() == null) {
		    		MessageDialog.openError(Display.getCurrent().getActiveShell(), 
		    				Messages.RepositoryViewer_38, Messages.RepositoryViewer_39);
		    	}
		    	
		    	System.out.println("got dnd objects #" + Activator.getDefault().getDndObjects().size()); //$NON-NLS-1$
		    	
		    	List<AxisDirectoryItemWrapper> objects = Activator.getDefault().getDndObjects();
		    	
		    	boolean supported = true;
		    	
		    	for (AxisDirectoryItemWrapper wrap : objects) {
		    		supported = RepositoryObjectTypeAuthorizer.isSupported(wrap);
		    	}
		    	
		    	if (!supported) {
		    		MessageDialog.openError(Display.getCurrent().getActiveShell(), 
		    				Messages.RepositoryViewer_41, Messages.RepositoryViewer_42);
		    		return;
		    	}
		    	
		    	RepositoryDirectory directory = null;
		    	
		    	if (event.item.getData() instanceof RepositoryDirectory) {
		    		directory = (RepositoryDirectory) event.item.getData();
		    	}
		    	
		    	for (AxisDirectoryItemWrapper wrap : objects) {
		    		wrap.setDirectory(directory);
		    	}
		    	
		    	project.getOutputRepository().addTemporaryElements(objects, directory);
		    	viewer.setInput(project.getOutputRepository());
		    	
		    	notifyListeners(SWT.Modify, new Event());
		    }
	    });
	}
	
	private void refreshRepository() throws Exception {		
		if (currentType == TYPE_SOURCE) {
			int index = cRepo.getSelectionIndex();
			Repository def = definitions.get(index);
			
			String user = tuser.getText();
			String pass = tpass.getText();
			
			project.connectToInputRepository(user, pass, def);
			
			viewer.setInput(project.getInputRepository());
		}
		else if (currentType == TYPE_TARGET) {
			int index = cRepo.getSelectionIndex();
			Repository def = definitions.get(index);
			
//			int groupIndex = c
			
			String url = turl.getText();
			String user = tuser.getText();
			String pass = tpass.getText();
			
			project.connectToOutputRepository(url, user, pass, def);
			viewer.setInput(project.getOutputRepository());
		}
	}
	
	private String[] prepareDataForCombo(List<Repository> defs) {
		List<String> strs = new ArrayList<String>();
		
		for (Repository def : defs) {
			strs.add(def.getName());
		}
		
		return strs.toArray(new String[strs.size()]);
	}
	
	
	
	@Override
	public boolean setFocus() {
//		if (isShown && currentRepository != null && (currentType == TYPE_SOURCE))
//			MessageDialog.openInformation(getShell(), "Error", "");
		bpm.vanilla.server.client.ui.clustering.menu.Activator activator 
			= bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault();
		
		if (!isShown && (currentType == TYPE_SOURCE) && activator.getVanillaApi() != null) {
			try {
				showHomeRepository(currentRepository);
			} catch (Exception e) {
				MessageDialog.openInformation(getShell(), Messages.RepositoryViewer_43, 
						Messages.RepositoryViewer_44 + e.getMessage());
				OurLogger.error(Messages.RepositoryViewer_45, e);
			}
		}
		
		return false;
	}
}