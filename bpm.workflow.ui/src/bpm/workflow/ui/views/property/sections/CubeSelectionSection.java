package bpm.workflow.ui.views.property.sections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.workflow.runtime.model.activities.vanilla.ResetUolapCacheActivity;
import bpm.workflow.runtime.resources.BiRepositoryObject;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class CubeSelectionSection extends AbstractPropertySection {

	private Node node;
	private ResetUolapCacheActivity activity;

	private ComboViewer comboCubes;

	private HashMap<BiRepositoryObject, List<String>> cubeNames = new HashMap<BiRepositoryObject, List<String>>();

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lbl = new Label(parent, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText(Messages.CubeSelectionSection_0);

		comboCubes = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboCubes.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		comboCubes.setContentProvider(new ArrayContentProvider());
		comboCubes.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return super.getText(element);
			}
		});

		comboCubes.getCombo().addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {
				setInput();

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});

		comboCubes.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				String cubeName = (String) ((IStructuredSelection) event.getSelection()).getFirstElement();
				activity.setCubeName(cubeName);
			}
		});

		setInput();

	}

	private void setInput() {
		if(activity != null && activity.getItem() != null) {
			if(cubeNames.get(activity.getItem()) != null) {
				comboCubes.setInput(cubeNames.get(activity.getItem()).toArray());
				activity.setCubeName(cubeNames.get(activity.getItem()).get(0));
			}
			else {
				try {
					IObjectIdentifier identifier = new ObjectIdentifier(Activator.getDefault().getRepositoryContext().getRepository().getId(), activity.getItem().getId());

					IRepositoryContext repCtx = Activator.getDefault().getRepositoryContext();
					IRuntimeContext ctx = new RuntimeContext(repCtx.getVanillaContext().getLogin(), repCtx.getVanillaContext().getPassword(), repCtx.getGroup().getName(), repCtx.getGroup().getId());

					List<String> names = new ArrayList<String>();
					names.addAll(Activator.getDefault().getFaApiHelper().getCubeNames(identifier, ctx));

					cubeNames.put(activity.getItem(), names);
					comboCubes.setInput(names.toArray());
					activity.setCubeName(names.get(0));
				} catch(Exception e) {
					e.printStackTrace();
					MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.CubeSelectionSection_1, Messages.CubeSelectionSection_2);
				}
			}

			if(activity.getCubeName() != null) {
				comboCubes.getCombo().setText(activity.getCubeName());
			}
		}
	}

	@Override
	public void refresh() {
		setInput();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		Assert.isTrue(node.getWorkflowObject() instanceof ResetUolapCacheActivity);
		this.activity = (ResetUolapCacheActivity) node.getWorkflowObject();
	}

}
