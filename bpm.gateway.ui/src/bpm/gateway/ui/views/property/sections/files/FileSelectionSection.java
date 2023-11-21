package bpm.gateway.ui.views.property.sections.files;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.inputs.FileInputShape;
import bpm.gateway.core.transformations.inputs.FileInputXML;
import bpm.gateway.core.transformations.kml.KMLInput;
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
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogPickupConstant;
import bpm.gateway.ui.views.property.sections.mdm.MdmContractComposite;
import bpm.mdm.model.supplier.Supplier;

public class FileSelectionSection extends Composite {

	private enum TypeFile {
		CLASSIC, URL, MDM
	}

	private Node node;

	private Text filePath, fileUrl;

	private Composite compositeUrl, compositeMdm;
	private GridData gridDataUrl, gridDataMdm;

	private Button btnFilePath, btnUrl, btnMdm, variable, browse;
	private MdmContractComposite mdmComposite;

	private boolean warnConnectedToVanilla = true;

	public FileSelectionSection(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, int style) {
		super(parent, style);

		createContent(parent, widgetFactory);
	}

	private void createContent(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		this.setLayout(new GridLayout());

		Composite file = widgetFactory.createComposite(parent, SWT.NONE);
		file.setLayout(new GridLayout(4, false));
		file.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		btnFilePath = widgetFactory.createButton(file, Messages.FileGeneralSection_0, SWT.RADIO);
		btnFilePath.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		btnFilePath.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update(TypeFile.CLASSIC);

				btnUrl.setSelection(false);
				btnMdm.setSelection(false);
			}
		});
		btnFilePath.setSelection(true);

		filePath = widgetFactory.createText(file, ""); //$NON-NLS-1$
		filePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filePath.addModifyListener(listener);

		variable = widgetFactory.createButton(file, Messages.FileGeneralSection_2, SWT.PUSH);
		variable.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		variable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getShell();
				DialogPickupConstant d = new DialogPickupConstant(sh);
				if (d.open() == Dialog.OK) {

					filePath.setText(d.getVariable().getOuputName());

					Event ev = new Event();
					ev.widget = filePath;
					listener.modifyText(new ModifyEvent(ev));
				}

			}
		});

		browse = widgetFactory.createButton(file, Messages.FileGeneralSection_3, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getShell();
				FileDialog fd = new FileDialog(sh);

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

		compositeUrl = widgetFactory.createFlatFormComposite(file);
		compositeUrl.setLayout(new GridLayout(2, false));
		gridDataUrl = new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1);
		compositeUrl.setLayoutData(gridDataUrl);

		btnUrl = widgetFactory.createButton(compositeUrl, Messages.FileGeneralSection_1, SWT.RADIO);
		btnUrl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		btnUrl.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update(TypeFile.URL);

				btnFilePath.setSelection(false);
				btnMdm.setSelection(false);
			}
		});

		fileUrl = widgetFactory.createText(compositeUrl, ""); //$NON-NLS-1$
		fileUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fileUrl.addModifyListener(urlListener);
		fileUrl.setEnabled(false);

		compositeMdm = widgetFactory.createFlatFormComposite(file);
		compositeMdm.setLayout(new GridLayout(2, false));
		gridDataMdm = new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1);
		compositeMdm.setLayoutData(gridDataMdm);

		btnMdm = widgetFactory.createButton(compositeMdm, Messages.FileGeneralSection_4, SWT.RADIO);
		btnMdm.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		btnMdm.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update(TypeFile.MDM);

				btnFilePath.setSelection(false);
				btnUrl.setSelection(false);
			}
		});

		this.mdmComposite = new MdmContractComposite(compositeMdm, widgetFactory, SWT.NONE, false);
	}

	public void setNode(Node node) {
		this.node = node;
		mdmComposite.setNode(node);

		initUi();
	}

	private void initUi() {
		boolean canUseURL = node.getGatewayModel() instanceof FileInputXML;
		boolean canUseMDM = node.getGatewayModel() instanceof ConnectorXML || node.getGatewayModel() instanceof FileOutputCSV || node.getGatewayModel() instanceof FileOutputXLS
				 || node.getGatewayModel() instanceof KMLInput || node.getGatewayModel() instanceof FileInputShape;

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

		if (node.getGatewayModel() instanceof FileInputXML) {
			((FileInputXML) node.getGatewayModel()).setFromUrl(typeFile == TypeFile.URL);
		}
		else if (node.getGatewayModel() instanceof IMdmContract) {
			((IMdmContract) node.getGatewayModel()).setUseMdm(typeFile == TypeFile.MDM);
		}
	}

	public void refresh() {
		DataStreamProperties properties = (DataStreamProperties) node.getAdapter(IPropertySource.class);
		filePath.removeModifyListener(listener);
		fileUrl.removeModifyListener(urlListener);

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
			else {
				fileUrl.setText(""); //$NON-NLS-1$
				filePath.setText(""); //$NON-NLS-1$
			}
		}
		else {
			fileUrl.setText(""); //$NON-NLS-1$
			filePath.setText(""); //$NON-NLS-1$
		}

		if (!(node.getGatewayModel() instanceof FileInputXML)) {
			btnUrl.setEnabled(false);

			update(TypeFile.CLASSIC);
		}

		if (typeFile == TypeFile.MDM) {
			btnMdm.setSelection(true);
			btnFilePath.setSelection(false);

			update(TypeFile.MDM);
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
			} catch (Exception e) {
				btnMdm.setEnabled(false);

				e.printStackTrace();
				if (warnConnectedToVanilla) {
					// MessageDialog.openInformation(getPart().getSite().getShell(),
					// Messages.FileGeneralSection_5,
					// Messages.FileGeneralSection_6);

					warnConnectedToVanilla = false;
				}
			}
		}

		filePath.addModifyListener(listener);
		fileUrl.addModifyListener(urlListener);
	}

	private TypeFile getTypeFile() {
		boolean fromUrl = node.getGatewayModel() instanceof FileInputXML && ((FileInputXML) node.getGatewayModel()).isFromUrl();
		boolean useMdm = node.getGatewayModel() instanceof IMdmContract && ((IMdmContract) node.getGatewayModel()).useMdm();

		if (fromUrl) {
			return TypeFile.URL;
		}
		else if (useMdm) {
			return TypeFile.MDM;
		}
		else {
			return TypeFile.CLASSIC;
		}
	}

	public void aboutToBeHidden() {
		if (filePath != null && !filePath.isDisposed()) {
			filePath.removeModifyListener(listener);
		}

		if (fileUrl != null && !fileUrl.isDisposed()) {
			fileUrl.removeModifyListener(urlListener);
		}

		mdmComposite.aboutToBeHidden();
	}

	public void aboutToBeShown() {
		filePath.addModifyListener(listener);
		fileUrl.addModifyListener(urlListener);
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
}
