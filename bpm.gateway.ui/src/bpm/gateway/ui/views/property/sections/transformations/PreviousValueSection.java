package bpm.gateway.ui.views.property.sections.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.utils.PreviousValueTransformation;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class PreviousValueSection extends AbstractPropertySection {

	private Node node;
	private PreviousValueTransformation transfo;
	private PropertyChangeListener listenerConnection;
	
	private CheckboxTableViewer keyTable;
	private CheckboxTableViewer previousTable;

	public PreviousValueSection() {
		listenerConnection = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)) {
					refresh();
				}
			}
		};
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, true));
		
		Label lblKey = getWidgetFactory().createLabel(composite, ""); //$NON-NLS-1$
		lblKey.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblKey.setText(Messages.PreviousValueSection_0);
		
		Label lblPrevious = getWidgetFactory().createLabel(composite, ""); //$NON-NLS-1$
		lblPrevious.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblPrevious.setText(Messages.PreviousValueSection_1);
		
		keyTable = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.V_SCROLL);
		keyTable.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		keyTable.setContentProvider(new ArrayContentProvider());
		keyTable.setLabelProvider(new ITableLabelProvider() {
			@Override
			public void addListener(ILabelProviderListener listener) {}

			@Override
			public void dispose() {}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				StreamElement elem = (StreamElement) element;
				if(columnIndex == 0) {
					return elem.name;
				}
				else if(columnIndex == 1) {
					return elem.originTransfo;
				}
				else {
					return elem.className;
				}
			}
		});
		
		TableColumn keyTransfo = new TableColumn(keyTable.getTable(), SWT.NONE);
		keyTransfo.setText(Messages.PreviousValueSection_2);
		keyTransfo.setWidth(200);
		
		TableColumn keyClass = new TableColumn(keyTable.getTable(), SWT.NONE);
		keyClass.setText(Messages.PreviousValueSection_3);
		keyClass.setWidth(200);
		
		keyTable.getTable().setHeaderVisible(true);
		keyTable.getTable().setLinesVisible(true);
		
		keyTable.addCheckStateListener(listener);
		
		previousTable = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.V_SCROLL);
		previousTable.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		previousTable.setContentProvider(new IStructuredContentProvider(){
			public Object[] getElements(Object inputElement) {
				List<StreamElement> st = (List<StreamElement>)inputElement;
				return st.toArray(new StreamElement[st.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
		});
		previousTable.setLabelProvider(new ITableLabelProvider() {
			@Override
			public void addListener(ILabelProviderListener listener) {}

			@Override
			public void dispose() {}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				StreamElement elem = (StreamElement) element;
				if(columnIndex == 0) {
					return elem.name;
				}
				else if(columnIndex == 1) {
					return elem.originTransfo;
				}
				else {
					return elem.className;
				}
			}
		});
		
		TableColumn previousTransfo = new TableColumn(previousTable.getTable(), SWT.NONE);
		previousTransfo.setText(Messages.PreviousValueSection_4);
		previousTransfo.setWidth(200);
		
		TableColumn previousClass = new TableColumn(previousTable.getTable(), SWT.NONE);
		previousClass.setText(Messages.PreviousValueSection_5);
		previousClass.setWidth(200);
		
		previousTable.getTable().setHeaderVisible(true);
		previousTable.getTable().setLinesVisible(true);
		
		previousTable.addCheckStateListener(listener);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		this.transfo = (PreviousValueTransformation) node.getGatewayModel();
	}

	@Override
	public void refresh() {
		
		List<StreamElement> input = null;
		try {
			input = transfo.getOriginalDescriptor().getStreamElements();
		} catch (ServerException e) {
			e.printStackTrace();
		}
		List<StreamElement> keys = transfo.getKeyElements(input);
		List<StreamElement> previous = transfo.getPreviousElements(input);
		
		keyTable.setInput(input);
		previousTable.setInput(input);
		
		keyTable.setAllChecked(false);
		previousTable.setAllChecked(false);
		
		keyTable.setCheckedElements(keys.toArray());
		previousTable.setCheckedElements(previous.toArray());
	}

	@Override
	public void aboutToBeShown() {
		node.addPropertyChangeListener(listenerConnection);
		super.aboutToBeShown();
	}

	@Override
	public void aboutToBeHidden() {
		if (node != null) {
			node.removePropertyChangeListener(listenerConnection);
		}
		super.aboutToBeHidden();
	}

	private ICheckStateListener listener = new ICheckStateListener() {	
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			StreamElement elem = (StreamElement) event.getElement();
			if(event.getSource() == keyTable) {
				if(event.getChecked()) {
					transfo.addKey(elem.name);
				}
				else {
					transfo.removeKey(elem.name);
				}
			}
			else {
				if(event.getChecked()) {
					transfo.addPrevious(elem.name);
				}
				else {
					transfo.removePrevious(elem.name);
				}
			}
		}
	};
	
}
