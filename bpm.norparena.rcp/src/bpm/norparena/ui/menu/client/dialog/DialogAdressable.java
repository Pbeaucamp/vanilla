//FIXME : Remove this

//package bpm.norparena.ui.menu.client.dialog;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.eclipse.jface.dialogs.Dialog;
//import org.eclipse.jface.viewers.CheckboxTableViewer;
//import org.eclipse.jface.viewers.IStructuredContentProvider;
//import org.eclipse.jface.viewers.LabelProvider;
//import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.Text;
//
//import bpm.norparena.ui.menu.Activator;
//import bpm.repository.api.model.IDirectoryItem;
//import bpm.vanilla.platform.core.beans.Group;
//import bpm.vanilla.platform.core.beans.ObjectAdressable;
//import bpm.vanilla.platform.core.beans.SecuredAdressable;
//
//
//public class DialogAdressable extends Dialog {
//
//	private Composite composite ;
//	private Text nametext;
//	private int repositoryId;
//	private IDirectoryItem item;
//	private ObjectAdressable objadr;
//	private ArrayList<ObjectAdressable> objects;
//	private String[] items;
//	public Boolean ajout;
//	public Shell shell;
//	protected CheckboxTableViewer groups;
//	
//	public DialogAdressable(Shell parentShell, int repositoryId, IDirectoryItem item) {
//		super(parentShell);
//		
//		this.repositoryId = repositoryId;
//		this.item = item;
//		shell = parentShell;
//	}
//	
//	
//	@Override
//	protected void initializeBounds() {
//		getShell().setSize(500, 270);
//	}
//
//	@Override
//	protected Control createDialogArea(Composite parent) {
//		composite = new Composite(parent, SWT.NONE);
//		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
//		composite.setLayout(new GridLayout(2, false));
//		getShell().setText("Create Object Adressable");
//		
//		Label nl = new Label(composite, SWT.NONE);
//		nl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		nl.setText("Name");
//		
//		nametext  = new Text(composite, SWT.BORDER);
//		nametext.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		nametext.setText(item.getName().replace(" ", "_"));
//		nametext.setEditable(false);
//		nametext.setEnabled(false);
//		
//		Label l2 = new Label(composite, SWT.NONE);
//		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
//		l2.setText("Groups");
//		
//		groups = new CheckboxTableViewer(composite, SWT.BORDER | SWT.V_SCROLL);
//		groups.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
//		groups.setContentProvider(new IStructuredContentProvider(){
//
//			public Object[] getElements(Object inputElement) {
//				List<Group> l = (List<Group>)inputElement;
//				return l.toArray(new Group[l.size()]);
//			}
//
//			public void dispose() {
//				
//				
//			}
//
//			public void inputChanged(Viewer viewer, Object oldInput,
//					Object newInput) {
//				
//				
//			}
//			
//		});
//		groups.setLabelProvider(new LabelProvider(){
//
//			@Override
//			public String getText(Object element) {
//				return ((Group)element).getName();
//			}
//			
//		});
//		
//		try {
//			groups.setInput(Activator.getDefault().getRemote().getVanillaSecurityManager().getGroups());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		
//		
//		return composite;
//		
//	}
//	
//
//	@Override
//	protected void okPressed() {
//		objadr = new ObjectAdressable();
//		objadr.setName(nametext.getText());
//		objadr.setIdRepository(repositoryId);
//		objadr.setObjectId(item.getId());
//		
//		try {
//			Activator.getDefault().getRemote().getVanillaExternalAccessibilityManager().addAdressableObject(objadr);
//		
//
//			List<ObjectAdressable> objects = Activator.getDefault().getRemote().getVanillaExternalAccessibilityManager().getAdressableByRepositoryId(this.repositoryId);
//
//			for (ObjectAdressable oa : objects) {
//				if (oa.getName().equals(objadr.getName())) {
//					
//					for (int i = 0; i < groups.getCheckedElements().length; i++) {
////						SecuredAdressable s = new SecuredAdressable();
////						s.setObjectAdressableId(oa.getId());
////						s.setGroupId(((Group) groups.getCheckedElements()[i]).getId());
//						
//						Activator.getDefault().getRemote().getVanillaExternalAccessibilityManager().grantAccessToAdressable(((Group) groups.getCheckedElements()[i]).getId(), oa.getId());
//					}
//					
//					break;
//				}
//			}
//		
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		
//		
//		super.okPressed();
//	}
//
//}
