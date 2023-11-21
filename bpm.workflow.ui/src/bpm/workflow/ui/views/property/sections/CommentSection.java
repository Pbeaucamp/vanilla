package bpm.workflow.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IComment;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the comments
 * 
 * @author Charles MARTIN
 * 
 */
public class CommentSection extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226, 208);

	private Text selectH;

	private Node node;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());

		org.eclipse.swt.widgets.Group general = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		general.setLayout(new GridLayout(1, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setText(Messages.CommentSection_0);

		selectH = getWidgetFactory().createText(general, ""); //$NON-NLS-1$
		selectH.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

	}

	@Override
	public void refresh() {

		if(((IComment) node.getWorkflowObject()).getComment() != null) {

			selectH.setText(((IComment) node.getWorkflowObject()).getComment());
		}
		else {
			selectH.setText(""); //$NON-NLS-1$
		}

	}

	@Override
	public void aboutToBeShown() {

		if(!selectH.isListening(SWT.Selection)) {
			selectH.addModifyListener(listener);
		}

		super.aboutToBeShown();

	}

	public void aboutToBeHidden() {

		selectH.removeModifyListener(listener);

		super.aboutToBeHidden();
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

	}

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {

		 	((IComment) node.getWorkflowObject()).setComment(selectH.getText());

		}
	};

}
