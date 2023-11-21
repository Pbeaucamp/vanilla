package bpm.gateway.ui.views.property.sections.mdm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import bpm.data.viz.core.preparation.DataPreparation;
import bpm.gateway.core.server.vanilla.DataPreparationHelper;
import bpm.gateway.core.transformations.inputs.DataPreparationInput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.dialogs.utils.fields.DialogFieldsValues;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.tools.dialogs.DialogBrowseContent;

public class DataPreparationComposite extends Composite{

	private ComboViewer comboDocs;
	
	private DataPreparationInput transfo;
	
	public DataPreparationComposite(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, int style) {
		super(parent, style);

		createContent(parent, widgetFactory);
	}

	private void createContent(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		this.setLayout(new GridLayout());
		
		Composite composite = widgetFactory.createComposite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblDocs = widgetFactory.createLabel(composite, Messages.DataPreparationComposite_0, SWT.NONE);
		lblDocs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		comboDocs = new ComboViewer(composite, SWT.DROP_DOWN | SWT.PUSH);
		comboDocs.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		comboDocs.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				DataPreparation doc = (DataPreparation) element;
				return doc.getName();
			}
		});
		
		comboDocs.setContentProvider(new ArrayContentProvider());
		comboDocs.addSelectionChangedListener(docListener);
		
		Composite compositeBtn = widgetFactory.createComposite(parent, SWT.NONE);
		compositeBtn.setLayout(new GridLayout(2, false));
		compositeBtn.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		
		Button btnBrowse = new Button(compositeBtn, SWT.PUSH);
		btnBrowse.setText(Messages.DataPreparationComposite_1);
		btnBrowse.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.browse_datas_16));
		btnBrowse.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		btnBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getShell();

				try {

					List<List<Object>> values = DataPreparationHelper.getValues(transfo, 0, 100);
					if (values == null) {
						return;
					}
					
					DialogBrowseContent dial = new DialogBrowseContent(sh, values, transfo.getDescriptor(transfo).getStreamElements());
					dial.open();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(sh, Messages.DataPreparationComposite_2, Messages.DataPreparationComposite_3 + e1.getMessage());
				}
			}
		});

		Button bDistcintValues = new Button(compositeBtn, SWT.PUSH);
		bDistcintValues.setText(Messages.DataPreparationComposite_4);
		bDistcintValues.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bDistcintValues.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getShell();

				DialogFieldsValues dial = new DialogFieldsValues(sh, transfo);
				dial.open();
			}
		});
	}

	ISelectionChangedListener docListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			DataPreparation dataprep = (DataPreparation) ((IStructuredSelection) event.getSelection()).getFirstElement();
			transfo.setSelectedDataPrep(dataprep);
		}
	};
	
	public void refresh(List<DataPreparation> dataPreps) {
		
		if (transfo != null) {
			comboDocs.setInput(dataPreps.toArray());
			if (transfo.getSelectedDataPrep() == null) {
				transfo.setSelectedDataPrep(dataPreps.get(0));
			}
			comboDocs.setSelection(new StructuredSelection(transfo.getSelectedDataPrep()));
		}
		else {
			comboDocs.setInput(new ArrayList<DataPreparation>().toArray());
		}
	}
	
	public void setNode(Node node) {
		if (node.getGatewayModel() instanceof DataPreparationInput) {
			this.transfo = (DataPreparationInput) node.getGatewayModel();
		}
	}
}
