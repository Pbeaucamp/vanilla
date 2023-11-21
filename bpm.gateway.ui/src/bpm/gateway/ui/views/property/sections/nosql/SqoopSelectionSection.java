package bpm.gateway.ui.views.property.sections.nosql;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.nosql.SqoopTransformation;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class SqoopSelectionSection  extends AbstractPropertySection{

	private SqoopTransformation sqoopTransfo;
	
	private ComboViewer cbPartitionColumn;
	private StreamComposite composite;
	
	private ICheckStateListener listener = new ICheckStateListener() {

		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			sqoopTransfo.clearColumns();
			for(StreamElement e : composite.getCheckedElements()){
				sqoopTransfo.addSelectedColumn(e);
			}
		}
	};
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite mainComp = getWidgetFactory().createComposite(parent);
		mainComp.setLayout(new GridLayout(2, false));
//		mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblPartition = getWidgetFactory().createLabel(mainComp, Messages.SqoopSelectionSection_0);
		lblPartition.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		
		cbPartitionColumn = new ComboViewer(mainComp, SWT.READ_ONLY | SWT.BORDER | SWT.PUSH);
		cbPartitionColumn.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cbPartitionColumn.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

			@Override
			public void dispose() { }

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<StreamElement> l = (List<StreamElement>) inputElement;
				return l.toArray(new StreamElement[l.size()]);
			}
		});
		cbPartitionColumn.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}
		});
		cbPartitionColumn.addSelectionChangedListener(changePartition);
		
		composite = new StreamComposite(mainComp, SWT.NONE, true, false);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	}
	
	@Override
	public void refresh() {
		composite.removeCheckListener(listener);
		try{
			if(sqoopTransfo.getDescriptor(sqoopTransfo) != null) {
				composite.fillDatas(sqoopTransfo.getDescriptor(sqoopTransfo).getStreamElements());
				composite.setChecked(sqoopTransfo.getColumns());
			}
		}catch(Exception ex){
			composite.fillDatas(new ArrayList<StreamElement>());
		}
		composite.addCheckListener(listener);
		
		cbPartitionColumn.removeSelectionChangedListener(changePartition);
		try {
			if(sqoopTransfo.getDescriptor(sqoopTransfo) != null) {
				cbPartitionColumn.setInput(sqoopTransfo.getDescriptor(sqoopTransfo).getStreamElements());
			}
		} catch (ServerException e) {
			cbPartitionColumn.setInput(new ArrayList<StreamElement>());
		}
		if(sqoopTransfo.getPartitionColumnStream() != null) {
			cbPartitionColumn.setSelection(new StructuredSelection(sqoopTransfo.getPartitionColumnStream()));
		}
		cbPartitionColumn.addSelectionChangedListener(changePartition);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.sqoopTransfo = (SqoopTransformation) ((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	private ISelectionChangedListener changePartition = new ISelectionChangedListener() {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection)cbPartitionColumn.getSelection();

			if (!ss.isEmpty() && ss.getFirstElement() instanceof StreamElement){
				sqoopTransfo.setPartitionColumn(((StreamElement)ss.getFirstElement()).getFullName());
			}
		}
	};
}
