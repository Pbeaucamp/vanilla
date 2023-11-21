package bpm.gateway.ui.views.property.sections.files;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.inputs.FileInputXML;
import bpm.gateway.core.transformations.kml.KMLOutput;
import bpm.gateway.core.transformations.mdm.IMdmContract;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputVCL;
import bpm.gateway.core.transformations.outputs.FileOutputWeka;
import bpm.gateway.core.transformations.outputs.FileOutputXLS;
import bpm.gateway.core.transformations.outputs.FileOutputXML;
import bpm.gateway.core.veolia.ConnectorAbonneXML;
import bpm.gateway.core.veolia.ConnectorXML;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.properties.DataStreamProperties;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogPickupConstant;
import bpm.gateway.ui.views.property.sections.mdm.MdmContractComposite;
import bpm.mdm.model.supplier.Supplier;

public class FileGeneralSection extends AbstractPropertySection {

	private enum TypeFile {
		CLASSIC, URL, MDM, D4C
	}

	private Node node;

	private Text filePath, fileUrl, txtD4cDatasetName;
	
	private Composite compositeUrl, compositeMdm, compositeD4C;
	private GridData gridDataUrl, gridDataMdm, gridDataD4C;
	
	private Button btnFilePath, btnUrl, btnMdm, variable, browse, btnD4C;
	private MdmContractComposite mdmComposite;
	
	private boolean warnConnectedToVanilla = true;

	public FileGeneralSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout());

		Composite file = getWidgetFactory().createFlatFormComposite(composite);
		file.setLayout(new GridLayout(4, false));
		file.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		btnFilePath = getWidgetFactory().createButton(file, Messages.FileGeneralSection_0, SWT.RADIO);
		btnFilePath.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		btnFilePath.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update(TypeFile.CLASSIC);

				btnUrl.setSelection(false);
				btnMdm.setSelection(false);
				btnD4C.setSelection(false);
			}
		});
		btnFilePath.setSelection(true);

		filePath = getWidgetFactory().createText(file, ""); //$NON-NLS-1$
		filePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filePath.addModifyListener(listener);

		variable = getWidgetFactory().createButton(file, Messages.FileGeneralSection_2, SWT.PUSH);
		variable.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		variable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickupConstant d = new DialogPickupConstant(getPart().getSite().getShell());

				if (d.open() == Dialog.OK) {

					filePath.setText(d.getVariable().getOuputName());

					Event ev = new Event();
					ev.widget = filePath;
					listener.modifyText(new ModifyEvent(ev));
				}

			}
		});

		browse = getWidgetFactory().createButton(file, Messages.FileGeneralSection_3, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getPart().getSite().getShell());

				String varName = null;
				Variable v = null;
				String filterPath = null;
				try {

					if (filePath.getText().startsWith("{$")) { //$NON-NLS-1$

						varName = filePath.getText().substring(0, filePath.getText().indexOf("}") + 1); //$NON-NLS-1$

						v = ResourceManager.getInstance().getVariableFromOutputName(varName);
						String h = v.getValueAsString();
						fd.setFilterPath(h.startsWith("/") && h.contains(":") ? h.substring(1) : h); //$NON-NLS-1$ //$NON-NLS-2$
						filterPath = fd.getFilterPath();
					}

				} catch (Exception e1) {

				}
				String path = fd.open();

				if (path != null) {

					if (varName != null) {

						path = v.getOuputName() + path.substring(filterPath.length());
					}

					String extension = ""; //$NON-NLS-1$
					if (node.getGatewayModel() instanceof FileOutputCSV) {
						if (!path.contains(".csv")) { //$NON-NLS-1$
							extension = ".csv"; //$NON-NLS-1$
						}
					}
					else if (node.getGatewayModel() instanceof FileOutputWeka) {
						if (!path.contains(".arff")) { //$NON-NLS-1$
							extension = ".arff"; //$NON-NLS-1$
						}
					}
					else if (node.getGatewayModel() instanceof FileOutputVCL) {
						if (!path.contains(".vcl")) { //$NON-NLS-1$
							extension = ".vcl"; //$NON-NLS-1$
						}
					}
					else if (node.getGatewayModel() instanceof FileOutputXML) {
						if (!path.contains(".xml")) { //$NON-NLS-1$
							extension = ".xml"; //$NON-NLS-1$
						}
					}
					else if (node.getGatewayModel() instanceof FileOutputXLS) {
						if (!path.contains(".xls")) { //$NON-NLS-1$
							extension = ".xls"; //$NON-NLS-1$
						}
					}
					else if (node.getGatewayModel() instanceof KMLOutput) {
						if (!path.contains(".kml")) { //$NON-NLS-1$
							extension = ".kml"; //$NON-NLS-1$
						}
					}
					path = path + extension;

					filePath.setText(path);
					Event ev = new Event();
					ev.widget = filePath;
					listener.modifyText(new ModifyEvent(ev));
				}

			}

		});

		compositeUrl = getWidgetFactory().createFlatFormComposite(file);
		compositeUrl.setLayout(new GridLayout(2, false));
		gridDataUrl = new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1);
		compositeUrl.setLayoutData(gridDataUrl);

		btnUrl = getWidgetFactory().createButton(compositeUrl, Messages.FileGeneralSection_1, SWT.RADIO);
		btnUrl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		btnUrl.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update(TypeFile.URL);

				btnFilePath.setSelection(false);
				btnMdm.setSelection(false);
				btnD4C.setSelection(false);
			}
		});

		fileUrl = getWidgetFactory().createText(compositeUrl, ""); //$NON-NLS-1$
		fileUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fileUrl.addModifyListener(urlListener);
		fileUrl.setEnabled(false);

		compositeMdm = getWidgetFactory().createFlatFormComposite(file);
		compositeMdm.setLayout(new GridLayout(2, false));
		gridDataMdm = new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1);
		compositeMdm.setLayoutData(gridDataMdm);

		btnMdm = getWidgetFactory().createButton(compositeMdm, Messages.FileGeneralSection_4, SWT.RADIO);
		btnMdm.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		btnMdm.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update(TypeFile.MDM);

				btnFilePath.setSelection(false);
				btnUrl.setSelection(false);
				btnD4C.setSelection(false);
			}
		});

		this.mdmComposite = new MdmContractComposite(compositeMdm, getWidgetFactory(), SWT.NONE, true);

		compositeD4C = getWidgetFactory().createFlatFormComposite(file);
		compositeD4C.setLayout(new GridLayout(2, false));
		gridDataD4C = new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1);
		compositeD4C.setLayoutData(gridDataD4C);

		btnD4C = getWidgetFactory().createButton(compositeMdm, "Data4Citizen", SWT.RADIO);
		btnD4C.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		btnD4C.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update(TypeFile.D4C);

				btnFilePath.setSelection(false);
				btnUrl.setSelection(false);
				btnMdm.setSelection(false);
			}
		});

		Label lblDatasetId = getWidgetFactory().createLabel(compositeD4C, "Identifiant du jeu de données", SWT.NONE);
		lblDatasetId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		txtD4cDatasetName = getWidgetFactory().createText(compositeD4C, ""); //$NON-NLS-1$
		txtD4cDatasetName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtD4cDatasetName.addModifyListener(listener);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

		mdmComposite.setNode(node);
		
		initUi();
	}

	private void initUi() {
		boolean canUseURL = node.getGatewayModel() instanceof FileInputXML;
		boolean canUseMDM = node.getGatewayModel() instanceof ConnectorXML || node.getGatewayModel() instanceof FileOutputCSV || node.getGatewayModel() instanceof FileOutputXLS;

		gridDataUrl.exclude = !canUseURL;
		compositeUrl.setVisible(canUseURL);

		gridDataMdm.exclude = !canUseMDM;
		compositeMdm.setVisible(canUseMDM);
	}

	private void update(TypeFile typeFile) {
		filePath.setEnabled(typeFile == TypeFile.CLASSIC);
		variable.setEnabled(typeFile == TypeFile.CLASSIC);
		browse.setEnabled(typeFile == TypeFile.CLASSIC);

		fileUrl.setEnabled(typeFile == TypeFile.URL);
		mdmComposite.setEnabled(typeFile == TypeFile.MDM);
		txtD4cDatasetName.setEnabled(typeFile == TypeFile.D4C);

		if (node.getGatewayModel() instanceof FileInputXML) {
			((FileInputXML) node.getGatewayModel()).setFromUrl(typeFile == TypeFile.URL);
		}
		if (node.getGatewayModel() instanceof IMdmContract) {
			((IMdmContract) node.getGatewayModel()).setUseMdm(typeFile == TypeFile.MDM);
		}
		if (node.getGatewayModel() instanceof FileOutputCSV) {
			((FileOutputCSV) node.getGatewayModel()).setUseD4C(typeFile == TypeFile.D4C);
		}
	}

	@Override
	public void refresh() {
		DataStreamProperties properties = (DataStreamProperties) node.getAdapter(IPropertySource.class);
		filePath.removeModifyListener(listener);
		fileUrl.removeModifyListener(urlListener);
		txtD4cDatasetName.removeModifyListener(d4cDatasetNameListener);

		TypeFile typeFile = getTypeFile();

		String definition = (String) properties.getPropertyValue(DataStreamProperties.PROPERTY_SQL_DEFINITION);
		if (definition != null) {
			if (typeFile == TypeFile.URL) {
				fileUrl.setText((String) properties.getPropertyValue(DataStreamProperties.PROPERTY_SQL_DEFINITION));
				btnUrl.setSelection(true);
				btnFilePath.setSelection(false);

				update(TypeFile.URL);
			}
			else if (typeFile == TypeFile.CLASSIC) {
				filePath.setText((String) properties.getPropertyValue(DataStreamProperties.PROPERTY_SQL_DEFINITION));
			}
			else if (typeFile == TypeFile.D4C) {
				txtD4cDatasetName.setText((String) properties.getPropertyValue(DataStreamProperties.PROPERTY_SQL_DEFINITION));
			}
			else {
				fileUrl.setText(""); //$NON-NLS-1$
				filePath.setText(""); //$NON-NLS-1$
				txtD4cDatasetName.setText(""); //$NON-NLS-1$
			}
		}
		else {
			fileUrl.setText(""); //$NON-NLS-1$
			filePath.setText(""); //$NON-NLS-1$
			txtD4cDatasetName.setText(""); //$NON-NLS-1$
		}

		if (!(node.getGatewayModel() instanceof FileInputXML)) {
			btnUrl.setEnabled(false);

			update(TypeFile.CLASSIC);
		}

		if (typeFile == TypeFile.MDM) {
			btnMdm.setSelection(true);
			btnFilePath.setSelection(false);
			btnUrl.setSelection(false);
			btnD4C.setSelection(false);

			update(TypeFile.MDM);
		}
		else if (typeFile == TypeFile.D4C) {
			btnD4C.setSelection(true);
			btnMdm.setSelection(false);
			btnFilePath.setSelection(false);
			btnUrl.setSelection(false);

			update(TypeFile.D4C);
		}

		if (!(node.getGatewayModel() instanceof IMdmContract) || (node.getGatewayModel() instanceof ConnectorAbonneXML && !((ConnectorAbonneXML) node.getGatewayModel()).isInput())) {
			btnMdm.setEnabled(false);
			mdmComposite.setVisible(false);

			update(TypeFile.CLASSIC);
		}
		else {
			try {
				List<Supplier> suppliers = node.getGatewayModel().getDocument().getMdmHelper().getMdmSuppliers();
				mdmComposite.refresh(suppliers);

				btnMdm.setEnabled(true);
				mdmComposite.setVisible(true);

				if (typeFile == TypeFile.MDM) {
					update(TypeFile.MDM);
				}
			} catch(Exception e) {
				btnMdm.setEnabled(false);
				
				e.printStackTrace();
				if (warnConnectedToVanilla) {
//					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.FileGeneralSection_5, Messages.FileGeneralSection_6);
					
					warnConnectedToVanilla = false;
				}
			}
		}

		filePath.addModifyListener(listener);
		fileUrl.addModifyListener(urlListener);
		txtD4cDatasetName.addModifyListener(d4cDatasetNameListener);
	}

	private TypeFile getTypeFile() {
		boolean fromUrl = node.getGatewayModel() instanceof FileInputXML && ((FileInputXML) node.getGatewayModel()).isFromUrl();
		boolean useMdm = node.getGatewayModel() instanceof IMdmContract && ((IMdmContract) node.getGatewayModel()).useMdm();
		boolean useD4C = node.getGatewayModel() instanceof FileOutputCSV && ((FileOutputCSV) node.getGatewayModel()).useD4C();

		if (fromUrl) {
			return TypeFile.URL;
		}
		else if (useMdm) {
			return TypeFile.MDM;
		}
		else if (useD4C) {
			return TypeFile.D4C;
		}
		else {
			return TypeFile.CLASSIC;
		}
	}

	@Override
	public void aboutToBeHidden() {
		if (filePath != null && !filePath.isDisposed()) {
			filePath.removeModifyListener(listener);
		}

		if (fileUrl != null && !fileUrl.isDisposed()) {
			fileUrl.removeModifyListener(urlListener);
		}

		mdmComposite.aboutToBeHidden();

		super.aboutToBeHidden();
	}

	@Override
	public void aboutToBeShown() {
		filePath.addModifyListener(listener);
		fileUrl.addModifyListener(urlListener);
		super.aboutToBeShown();
	}

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			DataStreamProperties properties = (DataStreamProperties) node.getAdapter(IPropertySource.class);

			if (evt.widget == filePath) {
				properties.setPropertyValue(DataStreamProperties.PROPERTY_SQL_DEFINITION, filePath.getText());
			}

		}
	};

	private ModifyListener urlListener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			DataStreamProperties properties = (DataStreamProperties) node.getAdapter(IPropertySource.class);

			if (evt.widget == fileUrl) {
				properties.setPropertyValue(DataStreamProperties.PROPERTY_SQL_DEFINITION, fileUrl.getText());
			}
		}
	};

	private ModifyListener d4cDatasetNameListener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			DataStreamProperties properties = (DataStreamProperties) node.getAdapter(IPropertySource.class);

			if (evt.widget == txtD4cDatasetName) {
				properties.setPropertyValue(DataStreamProperties.PROPERTY_SQL_DEFINITION, txtD4cDatasetName.getText());
			}
		}
	};
}
