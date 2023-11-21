package bpm.gateway.ui.views.property.sections.files;

import java.util.Arrays;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.core.transformations.files.FileFolderReader.FileType;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class FileFolderReaderSection extends AbstractPropertySection {
	private FileFolderReader transfo;
	
	private Text folderPath;
	private ComboViewer fileType;
	private Text fileNamePattern;
	
	@Override
	public void createControls(Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		parent.setLayout(new GridLayout());
		
		Composite main = getWidgetFactory().createComposite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		Label l = getWidgetFactory().createLabel(main, Messages.FileFolderReaderSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		folderPath = getWidgetFactory().createText(main, ""); //$NON-NLS-1$
		folderPath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		folderPath.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				transfo.setFolderPath(folderPath.getText());
				
			}
		});
		
		Button browse = getWidgetFactory().createButton(main, "...", SWT.PUSH); //$NON-NLS-1$
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(getPart().getSite().getShell());
				String varName = null;
				Variable v = null;
				String filterPath = null;
				try {

					if (folderPath.getText().startsWith("{$")){ //$NON-NLS-1$
						
						varName = folderPath.getText().substring(0, folderPath.getText().indexOf("}" ) + 1); //$NON-NLS-1$
						
						v = ResourceManager.getInstance().getVariableFromOutputName(varName);
						String h = v.getValueAsString();
						dd.setFilterPath(h.startsWith("/") && h.contains(":") ?  h.substring(1) : h); //$NON-NLS-1$ //$NON-NLS-2$
						filterPath = dd.getFilterPath();
					}
					
					
				} catch (Exception e1) {
					
				}
				String path = dd.open();
				
							
				
				if (path != null){
					
					if (varName != null){
						path = v.getOuputName() + path.substring(filterPath.length() ); 
					}

					
					folderPath.setText(path);
					transfo.setFolderPath(path);
				}
				
			}
		});
		
		l = getWidgetFactory().createLabel(main, Messages.FileFolderReaderSection_3);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		fileType = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		fileType.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		fileType.setContentProvider(new ObservableListContentProvider());
		fileType.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((FileType)element).name();
			}
		});
		fileType.setInput(new WritableList(Arrays.asList(new FileType[]{FileType.CSV, FileType.XLS, FileType.XML}), FileType.class));
		fileType.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				transfo.setFileType((FileType)((IStructuredSelection)fileType.getSelection()).getFirstElement());
				
			}
		});
		
		l = getWidgetFactory().createLabel(main, Messages.FileFolderReaderSection_4);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		fileNamePattern = getWidgetFactory().createText(main, "*"); //$NON-NLS-1$
		fileNamePattern.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		fileNamePattern.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				transfo.setFileNamePattern(fileNamePattern.getText());
				
			}
		});
		
		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = (FileFolderReader) ((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	@Override
	public void refresh() {
		fileType.setSelection(new StructuredSelection(transfo.getFileType()));
		if (transfo.getFileNamePattern() != null){
			fileNamePattern.setText(transfo.getFileNamePattern());
		}
		if (transfo.getFolderPath() != null){
			folderPath.setText(transfo.getFolderPath());
		}
		
		
	}
}