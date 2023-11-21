package bpm.fd.design.ui.properties.model.editors;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import bpm.fa.api.olap.Element;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;

public class DialogDimensionsSelection extends Dialog {
	
	private List<Element> content;

	private Table table;
	private CheckboxTableViewer checkboxTableViewer;
	private OLAPCube cube;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public DialogDimensionsSelection(Shell parentShell, List<Element> content, OLAPCube cube) {
		super(parentShell);
		this.content = content;
		this.cube=cube;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		//Composite container = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = checkboxTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		initializeTree();

		return container;
	}
	
@Override
protected void okPressed() {
	try{
		Object[] elements = checkboxTableViewer.getCheckedElements();
		if (elements !=null && elements.length>0){
			content= new ArrayList<Element>();
			for (Object element : elements){
				content.add((Element)element);
			}
		} else 
			content =null;	
	}
	catch(Exception ex) {
		ex.printStackTrace();
		content =null;
	}					
	super.okPressed();
}
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
	
	
	

	public void initializeTree(){
		try{			
			//ContentProvider cp = new ContentProvider();
			//checkboxTableViewer.setContentProvider(cp);

			PerspectiveProvider provider= new PerspectiveProvider();
			checkboxTableViewer.setContentProvider(provider);
			
			checkboxTableViewer.setLabelProvider(new LabelProvider(){
				@Override
				public Image getImage(Object element) {
					ImageRegistry reg = Activator.getDefault().getImageRegistry();
					//if (element instanceof Dimension){
				//		return reg.get(Icons.DIMENSION);
			//		}
					if (element instanceof Measure){
						return reg.get(Icons.MEASURE);
					}
					return reg.get(Icons.DIMENSION);
				}
				@Override
				public String getText(Object element) {
					return ((Element)element).getName();
				}
				
			});
						
			checkboxTableViewer.setInput(initElement());
			if (content!=null)
				for(Element element : content){
					
					for(int i=0; i<checkboxTableViewer.getTable().getItemCount();i++){
						Element elmt=(Element)checkboxTableViewer.getElementAt(i);
						if ( elmt.getUniqueName().equals(element.getUniqueName()))
							checkboxTableViewer.setChecked(elmt, true);
					}
					//checkboxTableViewer.setChecked(element, true);
				}
					
		}catch(Exception e) {
			e.printStackTrace();
			MessageDialog.openError(new Shell(SWT.OK | SWT.APPLICATION_MODAL), "Error", e.getMessage());
		//	logger.error("InitializeTree failed", e);
		}		
	}
	
	
	public List<Element> initElement(){
		List<Element> elements = new ArrayList<Element>();
		elements.addAll(cube.getDimensions());
		for (MeasureGroup measuregroup : cube.getMeasures())
			elements.addAll(measuregroup.getMeasures());
		//elements.addAll(cube.getMeasures().getMeasures());
		return elements;
	}
	
	/*
	private Image newImage(String name){	
		InputStream input = AklaboxSynchroConst.class.getResourceAsStream(FOLDER_ICON+name);
		Image imageIcon = new Image(Display.getDefault(), input);
		Image imageResized= new Image (Display.getDefault(),imageIcon.getImageData().scaledTo(16, 16));
		return imageResized;
	}
	*/
	
	/*
	private void checkTreeViewer(CheckboxTreeViewer checkboxTreeViewer, List<Node> listNode){
		try{
			if (listNode!=null){
				for (Node node : listNode){
					if (node.isChecked()){
						checkboxTreeViewer.setChecked((Object)node, true);
						checkboxTreeViewer.setExpandedState((Object)node, true);
					}		
					checkTreeViewer(checkboxTreeViewer, node.getChildren());
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("checkTreeViewer function failed", e);
		}		
	}
	*/
	
	private static class PerspectiveProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			List<Element> list = (List<Element>) inputElement; 
			return list.toArray();
		}
		
	}
	
	public List<Element> getContent() {
		return content;
	}
	
	/*
	private static class ContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return ((Element) parentElement).getChildren().toArray();
		}

		@Override
		public Object getParent(Object element) {
			return ((Node) element).getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			return !((Node) element).getChildren().isEmpty();
		}

	}
*/
	
	
}
