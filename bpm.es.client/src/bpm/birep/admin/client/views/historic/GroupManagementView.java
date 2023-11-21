package bpm.birep.admin.client.views.historic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.birep.admin.client.trees.TreeDirectory;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.viewers.GroupViewerFilter;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.GroupProjection;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.repository.services.IRepositoryAdminService;


public class GroupManagementView extends ViewPart implements ISelectionListener {

	public static final String ID = "bpm.birep.admin.client.views.historic.GroupManagementView"; //$NON-NLS-1$
	public static final Color COLOR_DEGEN = new Color(Display.getDefault(), 250, 0, 0);
	public static final Color COLOR_GEN = new Color(Display.getDefault(), 0, 0, 250);
	public static final Font FONT_ACTIVE_CONNECTION = new Font(Display.getCurrent(), "Arial", 8, SWT.BOLD | SWT.ITALIC); //$NON-NLS-1$
	
	protected CheckboxTreeViewer groups;
	protected CheckboxTreeViewer runnableGrps;
	protected CheckboxTreeViewer contribution;
	protected CheckboxTreeViewer groupProjections;
	
	private Button btnApplyToChildren;

	
	private ProgressBar progressBar;
	protected Label currentState;
	private GroupViewerFilter groupFilter;

	/*
	 * filter Widgets
	 */
	private Text filterContent;
	private Button applyFilter;
	
	/*
	 * Datas
	 */
	protected Object selectedObject;
	protected List<Group> currentgroups = new ArrayList<Group>();

	protected ToolItem loadGroups;//, stopLoadGroups;
	
	protected List<GroupProjection> actualGroupProjections = new ArrayList<GroupProjection>();
	
	public GroupManagementView() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(this);
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}
	
	protected void createGroupsViewers(Composite main){
		Label l0 = new Label(main, SWT.NONE);;
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(Messages.Client_Views_GroupManagementView_2);
		
		Label l15 = new Label(main, SWT.NONE);;
		l15.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l15.setText(Messages.Client_Views_GroupManagementView_3);
		
		Composite compGroups = new Composite(main, SWT.NONE);
		compGroups.setLayout(new GridLayout(2, false));
		compGroups.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		final Button btnChkGroups = new Button(compGroups, SWT.CHECK);
		btnChkGroups.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		btnChkGroups.setSelection(true);
		btnChkGroups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				for(Group g : currentgroups) {
					if (selectedObject instanceof RepositoryDirectory){
						try {
							Activator.getDefault().getRepositoryApi().getAdminService().addGroupForDirectory(g.getId(), ((RepositoryDirectory)selectedObject).getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else if (selectedObject instanceof RepositoryItem){
						try {
							Activator.getDefault().getRepositoryApi().getAdminService().addGroupForItem(g.getId(), ((RepositoryItem)selectedObject).getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				groups.setAllChecked(true);
				btnChkGroups.setSelection(true);
			}
		});
		
		final Button btnUnChkGroups = new Button(compGroups, SWT.CHECK);
		btnUnChkGroups.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		btnUnChkGroups.setSelection(false);
		btnUnChkGroups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				for(Group g : currentgroups) {
					if (selectedObject instanceof RepositoryDirectory){
						try {
							Activator.getDefault().getRepositoryApi().getAdminService().removeGroupForDirectory(g.getId(), ((RepositoryDirectory)selectedObject).getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else if (selectedObject instanceof RepositoryItem){
						try {
							Activator.getDefault().getRepositoryApi().getAdminService().removeGroupForItem(g.getId(), ((RepositoryItem)selectedObject).getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				groups.setAllChecked(false);
				btnUnChkGroups.setSelection(false);
			}
		});
		
		Composite compRnGroups = new Composite(main, SWT.NONE);
		compRnGroups.setLayout(new GridLayout(2, false));
		compRnGroups.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		final Button btnChkRnGroups = new Button(compRnGroups, SWT.CHECK);
		btnChkRnGroups.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		btnChkRnGroups.setSelection(true);
		btnChkRnGroups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				for(Group g : currentgroups) {
					IRepositoryAdminService mgr = Activator.getDefault().getRepositoryApi().getAdminService();
					if (selectedObject instanceof RepositoryItem){
						try {
							mgr.setObjectRunnableForGroup(g.getId(), (RepositoryItem)selectedObject);
						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openError(getSite().getShell(), Messages.Client_Views_GroupManagementView_4, e.getMessage());
						}
					}
				}
				runnableGrps.setAllChecked(true);
				btnChkRnGroups.setSelection(true);
			}
		});
		
		final Button btnUnChkRnGroups = new Button(compRnGroups, SWT.CHECK);
		btnUnChkRnGroups.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		btnUnChkRnGroups.setSelection(false);
		btnUnChkRnGroups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				for(Group g : currentgroups) {
					IRepositoryAdminService mgr = Activator.getDefault().getRepositoryApi().getAdminService();
					if (selectedObject instanceof RepositoryItem){
						try {
							mgr.unsetObjectRunnableForGroup(g, (RepositoryItem)selectedObject);
						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openError(getSite().getShell(), Messages.Client_Views_GroupManagementView_5, e.getMessage());
						}
					}
				}
				runnableGrps.setAllChecked(false);
				btnUnChkRnGroups.setSelection(false);
			}
		});
		
		groups = new CheckboxTreeViewer(main, SWT.H_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL  | SWT.BORDER);
		groups.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		groups.setContentProvider(new ITreeContentProvider(){

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getChildren(Object parentElement) {
				if (parentElement == currentgroups){
					List<Group> l = new ArrayList<Group>((List<Group>)parentElement);
					
					List<Group> toRemove = new ArrayList<Group>();
					for(Group g : l ){
						if (g.getParentId() != null){
							toRemove.add(g);
						}
					}
					l.removeAll(toRemove);
					
					return l.toArray(new Group[l.size()]);
				}
				else{
					List<Group> l = new ArrayList<Group>();
					for(Group g : currentgroups){
						if (((Group)parentElement).getId().equals(g.getParentId())){
							l.add(g);
						}
					}
					if (l.size() == 0){
						return null;
					}
					return l.toArray(new Group[l.size()]);
				}
				
			}

			@Override
			public Object getParent(Object element) {
				if (element instanceof Group && ((Group)element).getParentId() == null){
					return currentgroups;
				}
				else {
					for(Group g : currentgroups){
						if (g.getId().equals(((Group)element).getParentId())){
							return g;
						}
					}
				}
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				for(Group g : currentgroups){
					if (((Group)element).getId().equals(g.getParentId())){
						return true;
					}
				}
				return false;
			}
			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Group> l = new ArrayList<Group>((List<Group>)inputElement);
				
				List<Group> toRemove = new ArrayList<Group>();
				for(Group g : l ){
					if (g.getParentId() != null){
						toRemove.add(g);
					}
				}
				l.removeAll(toRemove);
				
				return l.toArray(new Group[l.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
		});
		groups.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
			
		});
		groups.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()){
					if (selectedObject instanceof RepositoryDirectory){
						try {
							Activator.getDefault().getRepositoryApi().getAdminService().addGroupForDirectory(((Group)event.getElement()).getId(), ((RepositoryDirectory)selectedObject).getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else if (selectedObject instanceof RepositoryItem){
						try {
							Activator.getDefault().getRepositoryApi().getAdminService().addGroupForItem(((Group)event.getElement()).getId(), ((RepositoryItem)selectedObject).getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				else{
					if (selectedObject instanceof RepositoryDirectory){
						try {
							Activator.getDefault().getRepositoryApi().getAdminService().removeGroupForDirectory(((Group)event.getElement()).getId(), ((RepositoryDirectory)selectedObject).getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else if (selectedObject instanceof RepositoryItem){
						try {
							Activator.getDefault().getRepositoryApi().getAdminService().removeGroupForItem(((Group)event.getElement()).getId(), ((RepositoryItem)selectedObject).getId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			}
			
		});
		groups.setInput(currentgroups);

		runnableGrps = new CheckboxTreeViewer(main, SWT.H_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL  | SWT.BORDER);
		runnableGrps.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		runnableGrps.setContentProvider(new ITreeContentProvider(){

			public Object[] getChildren(Object parentElement) {
				if (parentElement == currentgroups){
					List<Group> l = new ArrayList<Group>((List<Group>)parentElement);
					
					List<Group> toRemove = new ArrayList<Group>();
					for(Group g : l ){
						if (g.getParentId() != null){
							toRemove.add(g);
						}
					}
					l.removeAll(toRemove);
					
					return l.toArray(new Group[l.size()]);
				}
				else{
					List<Group> l = new ArrayList<Group>();
					for(Group g : currentgroups){
						if (((Group)parentElement).getId().equals(g.getParentId())){
							l.add(g);
						}
					}
					if (l.size() == 0){
						return null;
					}
					return l.toArray(new Group[l.size()]);
				}
				
			}

			public Object getParent(Object element) {
				if (element instanceof Group && ((Group)element).getParentId() == null){
					return currentgroups;
				}
				else {
					for(Group g : currentgroups){
						if (g.getId().equals(((Group)element).getParentId())){
							return g;
						}
					}
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				for(Group g : currentgroups){
					if (((Group)element).getId().equals(g.getParentId())){
						return true;
					}
				}
				return false;
			}

			public Object[] getElements(Object inputElement) {
				List<Group> l = new ArrayList<Group>((List<Group>)inputElement);
				
				List<Group> toRemove = new ArrayList<Group>();
				for(Group g : l ){
					if (g.getParentId() != null){
						toRemove.add(g);
					}
				}
				l.removeAll(toRemove);
				
				return l.toArray(new Group[l.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
		});
		runnableGrps.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
			
		});
		runnableGrps.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				IRepositoryAdminService mgr = Activator.getDefault().getRepositoryApi().getAdminService();
				if (event.getChecked()){
					if (selectedObject instanceof RepositoryItem){
						try {
							mgr.setObjectRunnableForGroup(((Group)event.getElement()).getId(), (RepositoryItem)selectedObject);
						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openError(getSite().getShell(), Messages.Client_Views_GroupManagementView_4, e.getMessage());
						}
					}
				}
				else{
					if (selectedObject instanceof RepositoryItem){
						try {
							mgr.unsetObjectRunnableForGroup((Group)event.getElement(), (RepositoryItem)selectedObject);
						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openError(getSite().getShell(), Messages.Client_Views_GroupManagementView_5, e.getMessage());
						}
					}
				}
			}
		});
		runnableGrps.setInput(currentgroups);

		Label l141 = new Label(main, SWT.NONE);;
		l141.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l141.setText(Messages.GroupManagementView_2);
		
		Label l14 = new Label(main, SWT.NONE);;
		l14.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l14.setText(Messages.GroupManagementView_3);
		
		Composite compGroupsCont = new Composite(main, SWT.NONE);
		compGroupsCont.setLayout(new GridLayout(2, false));
		compGroupsCont.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		final Button btnChkGroupsCont = new Button(compGroupsCont, SWT.CHECK);
		btnChkGroupsCont.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		btnChkGroupsCont.setSelection(true);
		btnChkGroupsCont.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				for(Group g : currentgroups) {
					IDocumentationService manager = Activator.getDefault().getRepositoryApi().getDocumentationService();
					if(selectedObject instanceof RepositoryItem) {
						RepositoryItem item = (RepositoryItem)selectedObject;	

						SecuredCommentObject secObject = new SecuredCommentObject();
						secObject.setGroupId(g.getId());
						secObject.setObjectId(item.getId());
						secObject.setType(Comment.ITEM);
	
						try {
							manager.addSecuredCommentObject(secObject);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					else if(selectedObject instanceof RepositoryDirectory) {
						RepositoryDirectory item = (RepositoryDirectory) selectedObject;
						SecuredCommentObject secObject = new SecuredCommentObject();
						secObject.setGroupId(g.getId());
						secObject.setObjectId(item.getId());
						secObject.setType(Comment.DIRECTORY);
	
						try {
							manager.addSecuredCommentObject(secObject);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
				contribution.setAllChecked(true);
				btnChkGroupsCont.setSelection(true);
			}
		});
		
		final Button btnUnChkGroupsCont = new Button(compGroupsCont, SWT.CHECK);
		btnUnChkGroupsCont.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		btnUnChkGroupsCont.setSelection(false);
		btnUnChkGroupsCont.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				for(Group g : currentgroups) {
					IDocumentationService manager = Activator.getDefault().getRepositoryApi().getDocumentationService();
					if(selectedObject instanceof RepositoryItem) {
						RepositoryItem item = (RepositoryItem)selectedObject;

						try {
							manager.removeSecuredCommentObject(g.getId(), item.getId(), Comment.ITEM);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					else if(selectedObject instanceof RepositoryDirectory) {
						RepositoryDirectory item = (RepositoryDirectory) selectedObject;

						try {
							manager.removeSecuredCommentObject(g.getId(), item.getId(), Comment.DIRECTORY);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
				contribution.setAllChecked(false);
				btnUnChkGroupsCont.setSelection(false);
			}
		});
		
		Composite compGroupsProj = new Composite(main, SWT.NONE);
		compGroupsProj.setLayout(new GridLayout(2, false));
		compGroupsProj.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		final Button btnChkRnGroupsPrj = new Button(compGroupsProj, SWT.CHECK);
		btnChkRnGroupsPrj.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		btnChkRnGroupsPrj.setSelection(true);
		btnChkRnGroupsPrj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				for(Group g : currentgroups) {
					IVanillaAPI vanillaApi = Activator.getDefault().getVanillaApi();
					if (selectedObject instanceof RepositoryItem){
						int grpId = g.getId();
						int itemId = ((RepositoryItem)selectedObject).getId();
						GroupProjection gp = new GroupProjection();
						gp.setFasdId(itemId);
						gp.setGroupId(grpId);
						try {
							int id = vanillaApi.getVanillaSecurityManager().addGroupProjection(gp);
							gp.setId(id);
							actualGroupProjections.add(gp);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				groupProjections.setAllChecked(true);
				btnChkRnGroupsPrj.setSelection(true);
			}
		});
		
		final Button btnUnChkRnGroupsPrj = new Button(compGroupsProj, SWT.CHECK);
		btnUnChkRnGroupsPrj.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		btnUnChkRnGroupsPrj.setSelection(false);
		btnUnChkRnGroupsPrj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev) {
				for(Group g : currentgroups) {
					IVanillaAPI vanillaApi = Activator.getDefault().getVanillaApi();
					if (selectedObject instanceof RepositoryItem){
						int grpId = g.getId();
						int itemId = ((RepositoryItem)selectedObject).getId();
						GroupProjection gp = null;
						for(GroupProjection gg : actualGroupProjections) {
							if(gg.getFasdId() == itemId && gg.getGroupId() == grpId) {
								gp = gg;
								break;
							}
						}
						try {
							vanillaApi.getVanillaSecurityManager().deleteGroupProjection(gp);
							actualGroupProjections.remove(gp);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
				groupProjections.setAllChecked(false);
				btnUnChkRnGroupsPrj.setSelection(false);
			}
		});
		
		
		
		

		contribution = new CheckboxTreeViewer(main, SWT.H_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL  | SWT.BORDER);
		contribution.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		contribution.setContentProvider(new ITreeContentProvider(){

			public Object[] getChildren(Object parentElement) {
				if (parentElement == currentgroups){
					List<Group> l = new ArrayList<Group>((List<Group>)parentElement);
					
					List<Group> toRemove = new ArrayList<Group>();
					for(Group g : l ){
						if (g.getParentId() != null){
							toRemove.add(g);
						}
					}
					l.removeAll(toRemove);
					
					return l.toArray(new Group[l.size()]);
				}
				else{
					List<Group> l = new ArrayList<Group>();
					for(Group g : currentgroups){
						if (((Group)parentElement).getId().equals(g.getParentId())){
							l.add(g);
						}
					}
					if (l.size() == 0){
						return null;
					}
					return l.toArray(new Group[l.size()]);
				}
				
			}

			public Object getParent(Object element) {
				if (element instanceof Group && ((Group)element).getParentId() == null){
					return currentgroups;
				}
				else {
					for(Group g : currentgroups){
						if (g.getId().equals(((Group)element).getParentId())){
							return g;
						}
					}
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				for(Group g : currentgroups){
					if (((Group)element).getId().equals(g.getParentId())){
						return true;
					}
				}
				return false;
			}

			public Object[] getElements(Object inputElement) {
				List<Group> l = new ArrayList<Group>((List<Group>)inputElement);
				
				List<Group> toRemove = new ArrayList<Group>();
				for(Group g : l ){
					if (g.getParentId() != null){
						toRemove.add(g);
					}
				}
				l.removeAll(toRemove);
				
				return l.toArray(new Group[l.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		});
		
		contribution.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
			
		});
		contribution.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				IDocumentationService manager = Activator.getDefault().getRepositoryApi().getDocumentationService();
				if(selectedObject instanceof RepositoryItem) {
					RepositoryItem item = (RepositoryItem)selectedObject;	
				
					if (event.getChecked()) {
						SecuredCommentObject secObject = new SecuredCommentObject();
						secObject.setGroupId(((Group)event.getElement()).getId());
						secObject.setObjectId(item.getId());
						secObject.setType(Comment.ITEM);
	
						try {
							manager.addSecuredCommentObject(secObject);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else {
						try {
							manager.removeSecuredCommentObject(((Group)event.getElement()).getId(), item.getId(), Comment.ITEM);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				else if(selectedObject instanceof RepositoryDirectory) {
					RepositoryDirectory item = (RepositoryDirectory) selectedObject;
					if (event.getChecked()) {
						SecuredCommentObject secObject = new SecuredCommentObject();
						secObject.setGroupId(((Group)event.getElement()).getId());
						secObject.setObjectId(item.getId());
						secObject.setType(Comment.DIRECTORY);
	
						try {
							manager.addSecuredCommentObject(secObject);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else {
						try {
							manager.removeSecuredCommentObject(((Group)event.getElement()).getId(), item.getId(), Comment.DIRECTORY);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			
		});
		contribution.setInput(currentgroups);
		

		groupProjections = new CheckboxTreeViewer(main, SWT.BORDER | SWT.V_SCROLL | SWT.VIRTUAL);
		groupProjections.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		groupProjections.setContentProvider(new ITreeContentProvider(){
			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement == currentgroups){
					List<Group> l = new ArrayList<Group>((List<Group>)parentElement);
					
					List<Group> toRemove = new ArrayList<Group>();
					for(Group g : l ){
						if (g.getParentId() != null){
							toRemove.add(g);
						}
					}
					l.removeAll(toRemove);
					
					return l.toArray(new Group[l.size()]);
				}
				else{
					List<Group> l = new ArrayList<Group>();
					for(Group g : currentgroups){
						if (((Group)parentElement).getId().equals(g.getParentId())){
							l.add(g);
						}
					}
					if (l.size() == 0){
						return null;
					}
					return l.toArray(new Group[l.size()]);
				}
				
			}

			public Object getParent(Object element) {
				if (element instanceof Group && ((Group)element).getParentId() == null){
					return currentgroups;
				}
				else {
					for(Group g : currentgroups){
						if (g.getId().equals(((Group)element).getParentId())){
							return g;
						}
					}
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				for(Group g : currentgroups){
					if (((Group)element).getId().equals(g.getParentId())){
						return true;
					}
				}
				return false;
			}

			public Object[] getElements(Object inputElement) {
				List<Group> l = new ArrayList<Group>((List<Group>)inputElement);
				
				List<Group> toRemove = new ArrayList<Group>();
				for(Group g : l ){
					if (g.getParentId() != null){
						toRemove.add(g);
					}
				}
				l.removeAll(toRemove);
				
				return l.toArray(new Group[l.size()]);
			}

			@Override
			public void dispose() { }

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
		});
		groupProjections.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
			
		groupProjections.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				IVanillaAPI vanillaApi = Activator.getDefault().getVanillaApi();
				if (event.getChecked()){
					if (selectedObject instanceof RepositoryItem){
						int grpId = ((Group)event.getElement()).getId();
						int itemId = ((RepositoryItem)selectedObject).getId();
						GroupProjection gp = new GroupProjection();
						gp.setFasdId(itemId);
						gp.setGroupId(grpId);
						try {
							int id = vanillaApi.getVanillaSecurityManager().addGroupProjection(gp);
							gp.setId(id);
							actualGroupProjections.add(gp);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				else {
					if (selectedObject instanceof RepositoryItem){
						int grpId = ((Group)event.getElement()).getId();
						int itemId = ((RepositoryItem)selectedObject).getId();
						GroupProjection gp = null;
						for(GroupProjection g : actualGroupProjections) {
							if(g.getFasdId() == itemId && g.getGroupId() == grpId) {
								gp = g;
								break;
							}
						}
						try {
							vanillaApi.getVanillaSecurityManager().deleteGroupProjection(gp);
							actualGroupProjections.remove(gp);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		});
		groupProjections.setInput(currentgroups);
		groupProjections.getControl().setEnabled(false);
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		createToolbar(main);
		createFilter(main);
		createGroupsViewers(main);

		
		currentState = new Label(main, SWT.NONE);
		currentState.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		btnApplyToChildren = new Button(main, SWT.PUSH);
		btnApplyToChildren.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		btnApplyToChildren.setText(Messages.GroupManagementView_6);
		btnApplyToChildren.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RepositoryDirectory dir = ((TreeDirectory)ViewTree.selectedObject).getDirectory();
				try {
					setSecurityToChildren(dir);
					MessageDialog.openInformation(GroupManagementView.this.getSite().getShell(), Messages.GroupManagementView_7, Messages.GroupManagementView_8);
				} catch(Throwable e1) {
					MessageDialog.openError(GroupManagementView.this.getSite().getShell(), Messages.GroupManagementView_9, e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		
		if (ViewTree.selectedObject != null) {
			
			if(ViewTree.selectedObject instanceof TreeDirectory) {
				if(!btnApplyToChildren.isVisible()) {
					btnApplyToChildren.setVisible(true);
				}
			}
			else {
				if(btnApplyToChildren.isVisible()) {
					btnApplyToChildren.setVisible(false);
				}
			}
			
			update(ViewTree.selectedObject);
			
			
		}
		
		loadGroupsFromVanilla();
		groups.setInput(currentgroups);
	
		if(runnableGrps != null){
			runnableGrps.setInput(currentgroups);
		}
		
		if(currentState != null){
			currentState.setText(""); //$NON-NLS-1$
		}
		
		if(contribution != null) {
			contribution.setInput(currentgroups);
		}
		
		if(groupProjections != null) {
			groupProjections.setInput(currentgroups);
		}
	
	}

	
	protected void setSecurityToChildren(RepositoryDirectory dir) throws Exception {
		List<IRepositoryObject> objects = new ArrayList<IRepositoryObject>();
		
		findChildren(dir, objects);
		List<Group> securityGroup = new ArrayList<Group>();
		Object[] checked = groups.getCheckedElements();
		for(Object o : checked) {
			securityGroup.add((Group) o);
		}
		List<Group> commentableGroup = new ArrayList<Group>();
		Object[] checked2 = contribution.getCheckedElements();
		for(Object o : checked2) {
			commentableGroup.add((Group) o);
		}

		List<Group> runnableGroups = securityGroup;
		List<Group> projectionGroup = securityGroup;
		

		Activator.getDefault().getRepositoryApi().getAdminService().setSecurityForElements(objects, securityGroup, runnableGroups, commentableGroup, projectionGroup);

		
	}

	private void findChildren(RepositoryDirectory dir, List<IRepositoryObject> objects) throws Exception {
		
		for(IRepositoryObject ob : new Repository(Activator.getDefault().getRepositoryApi()).getDirectoryContent(dir)) {
			objects.add(ob);
			if(ob instanceof RepositoryDirectory) {
				findChildren((RepositoryDirectory) ob, objects);
			}
		}
		
	}

	private void loadGroupsFromVanilla(){
		
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			
			@Override
			public void run() {
				currentgroups.clear();
				try{
					currentgroups.addAll(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups());
					initGroups();
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.GroupManagementView_4, Messages.GroupManagementView_5 + ex.getMessage());
				}
				
			}
		});
		
	}
	
	private void createFilter(Composite parent){
		Composite filterBar = new Composite(parent, SWT.NONE);
		filterBar.setLayout(new GridLayout(2, false));
		filterBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		applyFilter = new Button(filterBar, SWT.TOGGLE);
		applyFilter.setToolTipText(Messages.Client_Views_GroupManagementView_9);
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		applyFilter.setImage(reg.get("filter")); //$NON-NLS-1$
		applyFilter.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		applyFilter.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
					public void run(){
						if (applyFilter.getSelection()){
							groupFilter = new GroupViewerFilter(filterContent.getText());
							groups.addFilter(groupFilter);
							contribution.addFilter(groupFilter);
						}
						else{
							groups.removeFilter(groupFilter);
							contribution.remove(groupFilter);
						}
					}
				});
			}
			
		});
		
		filterContent = new Text(filterBar, SWT.BORDER);
		filterContent.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filterContent.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
					public void run(){
						if (groupFilter != null){
							groupFilter.setValue(filterContent.getText());
							groups.refresh();
							contribution.refresh();
						}
					}
				});
				
				
			}
			
		});
	}

	private void createToolbar(Composite parent){
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		ToolBar bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		bar.setLayout(new GridLayout());
		
		loadGroups = new ToolItem(bar, SWT.PUSH);
		loadGroups.setToolTipText(Messages.Client_Views_GroupManagementView_11);
		loadGroups.setImage(reg.get("long_load")); //$NON-NLS-1$
		loadGroups.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				loadGroupsFromVanilla();
			}
			
		});

		progressBar = new ProgressBar(parent, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
	}

	@Override
	public void setFocus() {

	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection) selection;
		
		if (ss.isEmpty()){
			selectedObject = null;
			if(runnableGrps != null){
				runnableGrps.getControl().setEnabled(false);
			}
			else if(contribution != null){
				contribution.getControl().setEnabled(false);
			}
			return;
		}
		ViewTree.selectedObject = ss.getFirstElement();
		update(ss.getFirstElement());
	}


	private void update(Object object) {
		if (object instanceof TreeItem){
			selectedObject = ((TreeItem)object).getItem();
			if (selectedObject instanceof RepositoryItem && ((RepositoryItem)selectedObject).isExecutable()){
				if(runnableGrps != null){
					runnableGrps.getControl().setEnabled(true);
				}
				
				if(contribution != null){
					contribution.getControl().setEnabled(true);
				}
				
				if(((RepositoryItem)selectedObject).getType() == IRepositoryApi.FASD_TYPE){
					if(groupProjections != null){
						groupProjections.getControl().setEnabled(true);
					}
				}
				else {
					if(groupProjections != null){
						groupProjections.getControl().setEnabled(false);
					}
				}
			}
			else{
				if(runnableGrps != null){
					runnableGrps.getControl().setEnabled(false);
				}
				if(contribution != null){
					contribution.getControl().setEnabled(true);
				}
				if(groupProjections != null){
					groupProjections.getControl().setEnabled(false);
				}
			}
			
		}
		else if (object instanceof TreeDirectory){
			selectedObject = ((TreeDirectory)object).getDirectory();
			if(runnableGrps != null){
				runnableGrps.getControl().setEnabled(false);
			}
			if(contribution != null){
				contribution.getControl().setEnabled(true);
			}
			if(groupProjections != null){
				groupProjections.getControl().setEnabled(false);
			}
		}
		else{
			selectedObject = null;
			if(runnableGrps != null){
				runnableGrps.getControl().setEnabled(false);
			}
			if(contribution != null){
				contribution.getControl().setEnabled(false);
			}
			if(groupProjections != null){
				groupProjections.getControl().setEnabled(false);
			}
			return;
		}
		
		BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
			@Override
			public void run() {
				try {
					initGroups();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}
	
	protected void initGroups() throws Exception{
		int step = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_ROWS_BY_CHUNK);			
		if (selectedObject != null){
		
			try{
				List<Group> authorizedGroups = new ArrayList<Group>();
				List<SecuredCommentObject> secObjects = new ArrayList<SecuredCommentObject>();	
				
				if (selectedObject instanceof RepositoryItem){
					RepositoryItem item = (RepositoryItem)selectedObject;
					
					secObjects = Activator.getDefault().getRepositoryApi().getDocumentationService().getSecuredCommentObjects(item.getId(), Comment.ITEM);
					authorizedGroups = Activator.getDefault().getRepositoryApi().getAdminService().getGroupsForItemId(((RepositoryItem)selectedObject).getId());
				}
				else if (selectedObject instanceof RepositoryDirectory){
					RepositoryDirectory item = (RepositoryDirectory)selectedObject;
					secObjects = Activator.getDefault().getRepositoryApi().getDocumentationService().getSecuredCommentObjects(item.getId(), Comment.DIRECTORY);
					authorizedGroups = Activator.getDefault().getRepositoryApi().getAdminService().getGroupsForDirectory((RepositoryDirectory)selectedObject);
				}
				
				for(Group i : currentgroups){
					boolean match = false;
					boolean isCommentable = false;
					for(Group g : authorizedGroups){
						if (g.getId().equals(i.getId())){
							match = true;
							break;
						}
					}
					
					for(SecuredCommentObject gr : secObjects) {
						if (gr.getGroupId().equals(i.getId())) {
							isCommentable = true;
							break;
						}
					}
					
					contribution.setChecked(i, isCommentable);
					groups.setChecked(i, match);	
				}

				contribution.refresh();
			}catch(Exception e){
				e.printStackTrace();
				MessageDialog.openError(getSite().getShell(), Messages.Client_Views_GroupManagementView_48, e.getMessage());

			}
			groups.refresh();
			

			if (selectedObject instanceof RepositoryItem){
				IRepositoryAdminService mg = Activator.getDefault().getRepositoryApi().getAdminService();
				
				for(Group g : currentgroups){
					try{
						boolean flag = mg.canRun(((RepositoryItem)selectedObject).getId(), g.getId());
						runnableGrps.setChecked(g, flag);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				runnableGrps.refresh();
			}
			
			if(selectedObject instanceof RepositoryItem && ((RepositoryItem)selectedObject).getType() == IRepositoryApi.FASD_TYPE) {
				
				actualGroupProjections = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupProjectionsByFasdId(((RepositoryItem)selectedObject).getId());
				
				for (Group g : currentgroups) {
					boolean finded = false;
					for (GroupProjection gp : actualGroupProjections) {
						if(g.getId().intValue() == gp.getGroupId()) {
							finded = true;
							groupProjections.setChecked(g, true);
							break;
						}
					}
					if(!finded) {
						groupProjections.setChecked(g, false);
					}
				}
				groupProjections.refresh();
			}
		}

		currentState.setText("Only " + step + " firsts Groups loaded. Use the Load Button to get all."); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
