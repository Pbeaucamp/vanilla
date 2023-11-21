package bpm.fd.design.ui.component.picture.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;

public class PicturePage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.text.pages.PicturePage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.PicturePage_1;
	public static final String PAGE_DESCRIPTION = Messages.PicturePage_2;

	
	
	private ComboViewer labelContent;
	private List<IResource> imageResources = new ArrayList<IResource>();
	
	public PicturePage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected PicturePage(String pageName) {
		super(pageName);
	}
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l.setText(Messages.PicturePage_3);
		
		labelContent = new ComboViewer(main, SWT.BORDER);
		labelContent.setContentProvider(new ListContentProvider<IResource>());
		labelContent.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				
				return ((IResource)element).getName();
			}
			
		});
		labelContent.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		for(IResource r : Activator.getDefault().getProject().getResources()){
			if (r instanceof FileImage){
				imageResources.add(r);
			}
		}
		labelContent.setInput(imageResources);
		
		
		labelContent.getCombo().addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				if (getContainer() != null){
					getContainer().updateButtons();
				}
				
				
			}
			
		});
		labelContent.getCombo().addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}

			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
				
			}
			
		});
		
		
		
		
		setControl(main);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return !labelContent.getCombo().getText().equals(""); //$NON-NLS-1$
	}

	public String getPictureUrl() {
		if (!labelContent.getSelection().isEmpty()){
			return ((FileImage)((IStructuredSelection)labelContent.getSelection()).getFirstElement()).getFile().getName();
		}
		
		
		return labelContent.getCombo().getText();
	}
	
	
}
