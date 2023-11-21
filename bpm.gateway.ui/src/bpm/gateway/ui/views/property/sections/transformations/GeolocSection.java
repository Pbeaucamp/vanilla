package bpm.gateway.ui.views.property.sections.transformations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.tools.APIBanField;
import bpm.gateway.core.tools.AdresseGeoLocHelper;
import bpm.gateway.core.transformations.Geoloc;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.composites.labelproviders.DefaultStreamLabelProvider;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class GeolocSection extends AbstractPropertySection {

	private static Color BLUE = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get("bpm.gateway.ui.colors.mappingexists"); //$NON-NLS-1$
	
	protected Node node;

	private Button btnOnlyOneColumn, btnOnlyOneColumnOutput;
	
	private ComboViewer libelleViewer, postalCodeViewer;
	
	private Text txtScore, txtFirstColumnName, txtSecondColumnName;
	
	private Composite compositeFields;
	private StreamComposite gridFields;

	private List<String> fields;
	private ISelectionChangedListener libelleListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection) libelleViewer.getSelection();

			if (ss.isEmpty()) {
				((Geoloc) node.getGatewayModel()).setLibelleIndex((StreamElement) null);
			}
			else {
				((Geoloc) node.getGatewayModel()).setLibelleIndex((StreamElement) ss.getFirstElement());
			}
		}
	};

	private ISelectionChangedListener postalCodeListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection) postalCodeViewer.getSelection();

			if (ss.isEmpty()) {
				((Geoloc) node.getGatewayModel()).setPostalCodeIndex((StreamElement) null);
			}
			else {
				((Geoloc) node.getGatewayModel()).setPostalCodeIndex((StreamElement) ss.getFirstElement());
			}
		}
	};

	private SelectionListener sl = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			((Geoloc) node.getGatewayModel()).setOnlyOneColumnAdress(btnOnlyOneColumn.getSelection());
			updateUi();
		}
	};

	private ModifyListener scoreListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			try {
				int i = Integer.parseInt(txtScore.getText());
				((Geoloc) node.getGatewayModel()).setScore(i);
				txtScore.setBackground(null);
			} catch (NumberFormatException ex) {
				ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
				Color color = reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);
				txtScore.setBackground(color);
			}
		}
	};

	private SelectionListener slColumnOutput = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			((Geoloc) node.getGatewayModel()).setOnlyOneColumnOutput(btnOnlyOneColumnOutput.getSelection());
			updateUi();
		}
	};

	private ModifyListener firstColumnName = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			((Geoloc) node.getGatewayModel()).setFirstColunmName(txtFirstColumnName.getText());
			node.getGatewayModel().refreshDescriptor();
		}
	};

	private ModifyListener secondColumnName = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			((Geoloc) node.getGatewayModel()).setSecondColunmName(txtSecondColumnName.getText());
			node.getGatewayModel().refreshDescriptor();
		}
	};

//	private ModifyListener scoreColumnName = new ModifyListener() {
//
//		public void modifyText(ModifyEvent e) {
//			((Geoloc) node.getGatewayModel()).setScoreColumnName(txtScoreColumnName.getText());
//			node.getGatewayModel().refreshDescriptor();
//		}
//	};

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		// parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());

		Composite main = getWidgetFactory().createComposite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout(2, false));

		btnOnlyOneColumn = getWidgetFactory().createButton(main, Messages.GeolocSection_0, SWT.CHECK);
		btnOnlyOneColumn.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l = getWidgetFactory().createLabel(main, Messages.GeolocSection_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		libelleViewer = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		libelleViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		libelleViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}
		});
		libelleViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});

		l = getWidgetFactory().createLabel(main, Messages.GeolocSection_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		postalCodeViewer = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		postalCodeViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		postalCodeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}
		});
		postalCodeViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		
		l = getWidgetFactory().createLabel(main, Messages.GeolocSection_3);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtScore = getWidgetFactory().createText(main, ""); //$NON-NLS-1$
		txtScore.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		btnOnlyOneColumnOutput = getWidgetFactory().createButton(main, Messages.GeolocSection_4, SWT.CHECK);
		btnOnlyOneColumnOutput.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		l = getWidgetFactory().createLabel(main, Messages.GeolocSection_5);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtFirstColumnName = getWidgetFactory().createText(main, ""); //$NON-NLS-1$
		txtFirstColumnName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = getWidgetFactory().createLabel(main, Messages.GeolocSection_6);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtSecondColumnName = getWidgetFactory().createText(main, ""); //$NON-NLS-1$
		txtSecondColumnName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
//		l = getWidgetFactory().createLabel(main, Messages.GeolocSection_7);
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//
//		txtScoreColumnName = getWidgetFactory().createText(main, ""); //$NON-NLS-1$
//		txtScoreColumnName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		compositeFields = getWidgetFactory().createComposite(main, SWT.NONE);
		compositeFields.setLayout(new GridLayout());
		compositeFields.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		ToolBar toolbar = new ToolBar(compositeFields, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		toolbar.setBackground(main.getBackground());
		
		ToolItem addSelected = new ToolItem(toolbar, SWT.PUSH);
		addSelected.setText(Messages.SelectionSection_0);
		addSelected.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				performAddField(gridFields.getCheckedElements());
				gridFields.refresh();
				gridFields.clearCheck();
				try {
					refreshFields();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		ToolItem delSelected = new ToolItem(toolbar, SWT.PUSH);
		delSelected.setText(Messages.SelectionSection_1);
		delSelected.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				performDelField(gridFields.getCheckedElements());
				gridFields.refresh();
				gridFields.clearCheck();
				try {
					refreshFields();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		ToolItem addAll = new ToolItem(toolbar, SWT.PUSH);
		addAll.setText(Messages.SelectionSection_2);
		addAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				performAddField(gridFields.getInput());
				gridFields.refresh();
				gridFields.clearCheck();
				try {
					refreshFields();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		ToolItem delAllSelected = new ToolItem(toolbar, SWT.PUSH);
		delAllSelected.setText(Messages.SelectionSection_3);
		delAllSelected.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				performDelField(gridFields.getInput());
				gridFields.refresh();
				gridFields.clearCheck();
				try {
					refreshFields();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		gridFields = new StreamComposite(compositeFields, SWT.NONE, true, false);
		gridFields.setLabelProvider(new StreamLabelProvider(gridFields.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		gridFields.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	}
	
	protected void performAddField(List<StreamElement> elements){
		if (node.getGatewayModel() != null){
			for(StreamElement i : elements) {
				((Geoloc) node.getGatewayModel()).addField(i.name);
			}
		}
	}

	protected void performDelField(List<StreamElement> elements){
		if (node.getGatewayModel() != null) {
			for(StreamElement i : elements){
				((Geoloc) node.getGatewayModel()).removeField(i.name);
			}
		}
	}
	
	public void refreshFields() {
		String transfoName = ((AbstractTransformation) node.getGatewayModel()).getName();

		this.fields = new ArrayList<String>();
		List<StreamElement> l = new ArrayList<StreamElement>();
		for(APIBanField field : AdresseGeoLocHelper.API_FIELDS){
			fields.add(field.getId());
			l.add(AdresseGeoLocHelper.buildColumn(transfoName, field));
		}
		gridFields.fillDatas(l);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		
		refreshFields();
	}
	
	private void updateUi() {
		boolean onlyOneColumnAdress = btnOnlyOneColumn.getSelection();
		postalCodeViewer.getCCombo().setEnabled(!onlyOneColumnAdress);
		
		boolean onlyOneColumnOutput = btnOnlyOneColumnOutput.getSelection();
		txtSecondColumnName.setEnabled(!onlyOneColumnOutput);
	}

	@Override
	public void refresh() {

		btnOnlyOneColumn.removeSelectionListener(sl);
		libelleViewer.removeSelectionChangedListener(libelleListener);
		postalCodeViewer.removeSelectionChangedListener(postalCodeListener);
		txtScore.removeModifyListener(scoreListener);
		btnOnlyOneColumnOutput.removeSelectionListener(slColumnOutput);
		txtFirstColumnName.removeModifyListener(firstColumnName);
		txtSecondColumnName.removeModifyListener(secondColumnName);
//		txtScoreColumnName.removeModifyListener(scoreColumnName);

		Geoloc tr = ((Geoloc) node.getGatewayModel());
		btnOnlyOneColumn.setSelection(tr.isOnlyOneColumnAdress());
		try {
			libelleViewer.setInput(tr.getDescriptor(tr).getStreamElements());
			try {
				libelleViewer.setSelection(new StructuredSelection(tr.getDescriptor(tr).getStreamElements().get(tr.getLibelleIndex())));
			} catch (Exception ex) {
				libelleViewer.setSelection(new StructuredSelection());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			libelleViewer.setInput(new ArrayList<StreamElement>());
			libelleViewer.setSelection(new StructuredSelection());
		}
		try {
			postalCodeViewer.setInput(tr.getDescriptor(tr).getStreamElements());
			try {
				postalCodeViewer.setSelection(new StructuredSelection(tr.getDescriptor(tr).getStreamElements().get(tr.getPostalCodeIndex())));
			} catch (Exception ex) {
				postalCodeViewer.setSelection(new StructuredSelection());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			postalCodeViewer.setInput(new ArrayList<StreamElement>());
			postalCodeViewer.setSelection(new StructuredSelection());
		}
		
		txtScore.setText(tr.getScore() != null ? String.valueOf(tr.getScore()) : ""); //$NON-NLS-1$

		btnOnlyOneColumnOutput.setSelection(tr.isOnlyOneColumnOutput());
		txtFirstColumnName.setText(tr.getFirstColunmName() != null ? tr.getFirstColunmName() : ""); //$NON-NLS-1$
		txtSecondColumnName.setText(tr.getSecondColunmName() != null ? tr.getSecondColunmName() : ""); //$NON-NLS-1$
//		txtScoreColumnName.setText(tr.getScoreColumnName() != null ? tr.getScoreColumnName() : ""); //$NON-NLS-1$fd
		
		updateUi();
		
		btnOnlyOneColumn.addSelectionListener(sl);
		libelleViewer.addSelectionChangedListener(libelleListener);
		postalCodeViewer.addSelectionChangedListener(postalCodeListener);
		txtScore.addModifyListener(scoreListener);
		btnOnlyOneColumnOutput.addSelectionListener(slColumnOutput);
		txtFirstColumnName.addModifyListener(firstColumnName);
		txtSecondColumnName.addModifyListener(secondColumnName);
//		txtScoreColumnName.addModifyListener(scoreColumnName);
	}
	
	public class StreamLabelProvider extends DefaultStreamLabelProvider{

		public StreamLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
			super(provider, decorator);
		}
		
		@Override
		public Color getForeground(Object element) {
			if (((Geoloc) node.getGatewayModel()).isFieldSelected((StreamElement) element)){
				return BLUE;
			}

			return super.getForeground(element);
		}

	}

}
