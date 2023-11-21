package bpm.gateway.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Comment;
import bpm.gateway.ui.gef.part.CommentPart;
import bpm.gateway.ui.i18n.Messages;

public class CommentSection extends AbstractPropertySection {
	private static final String[] TYPE_NAMES = new String[]{"Simple", "Warning"}; //$NON-NLS-1$ //$NON-NLS-2$
	private Text content;
	private CCombo type;
	
	private Comment comment;
	
	private SelectionListener typeLst = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			comment.setTypeNote(type.getSelectionIndex());
		}
	};
	
	private ModifyListener contentLst = new ModifyListener() {
		
		public void modifyText(ModifyEvent e) {
			comment.setContent(content.getText());
			
		}
	};
	
	public CommentSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
//		parent.setLayout(new FillLayout());
//		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
//		composite.setLayoutData(new GridData(GridData.FILL_BOTH));;
		
		Label l = getWidgetFactory().createLabel(composite, Messages.CommentSection_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		type = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setItems(TYPE_NAMES);
		
		l = getWidgetFactory().createLabel(composite, Messages.CommentSection_3);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		content = getWidgetFactory().createText(composite, "", SWT.BORDER | SWT.MULTI | SWT.WRAP); //$NON-NLS-1$
		content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
	}

	
	
	
	@Override
	public void refresh() {
		content.removeModifyListener(contentLst);
		type.removeSelectionListener(typeLst);
		
		type.select(comment.getTypeNote());
		content.setText(comment.getContent());
		
		content.addModifyListener(contentLst);
		type.addSelectionListener(typeLst);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof CommentPart);
        this.comment = (Comment)((CommentPart) input).getModel();
	}

	
}

