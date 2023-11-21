package bpm.workflow.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.activities.Comment;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class CommentTypeSection extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226, 208);

	private Node node;
	private Combo viewer;
	public static final String[] TYPES_NAME = { Messages.CommentTypeSection_0, Messages.CommentTypeSection_1, Messages.CommentTypeSection_2, Messages.CommentTypeSection_3 };

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());

		org.eclipse.swt.widgets.Group general = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		general.setLayout(new GridLayout(1, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setText(Messages.CommentTypeSection_4);

		viewer = new Combo(general, SWT.READ_ONLY);
		viewer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		viewer.setItems(TYPES_NAME);

	}

	@Override
	public void refresh() {

		// String[] comp = new String[4];
		// comp[0] = "<";
		// comp[1] = ">";
		// comp[2] = "=";
		// comp[3] = "";

		viewer.setItems(TYPES_NAME);
		String selectS = ((Comment) node.getWorkflowObject()).getType();
		int index2 = 0;
		for(String s : viewer.getItems()) {

			if(s.equalsIgnoreCase(selectS)) {
				viewer.select(index2);
				break;
			}
			index2++;
		}

	}

	@Override
	public void aboutToBeShown() {

		if(!viewer.isListening(SWT.Selection)) {
			viewer.addSelectionListener(selection);
		}

		super.aboutToBeShown();

	}

	public void aboutToBeHidden() {

		viewer.removeSelectionListener(selection);

		super.aboutToBeHidden();
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

	}

	private SelectionListener selection = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent e) {

		}

		public void widgetSelected(SelectionEvent e) {

			if(viewer.getText().equalsIgnoreCase(Messages.CommentTypeSection_5)) {
				((Comment) node.getWorkflowObject()).setType(0);
				node.setType(0);

			}
			else if(viewer.getText().equalsIgnoreCase(Messages.CommentTypeSection_6)) {
				((Comment) node.getWorkflowObject()).setType(1);
				node.setType(1);
			}
			else if(viewer.getText().equalsIgnoreCase(Messages.CommentTypeSection_7)) {
				((Comment) node.getWorkflowObject()).setType(2);
				node.setType(2);
			}
			else if(viewer.getText().equalsIgnoreCase(Messages.CommentTypeSection_8)) {
				((Comment) node.getWorkflowObject()).setType(3);
				node.setType(3);
			}

		}

	};

}
