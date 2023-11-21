package bpm.gateway.ui.views.property.sections.files.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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

import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.core.server.file.FileXMLHelper;
import bpm.gateway.core.transformations.inputs.FileInputXML;
import bpm.gateway.core.transformations.outputs.FileOutputXML;
import bpm.gateway.core.transformations.utils.DefinitionXSD;
import bpm.gateway.ui.dialogs.utils.xml.DialogXSDDefinition;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.properties.FileXMLProperties;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogPickupConstant;

public class FileXMLGeneralSection extends AbstractPropertySection {

	private Node node;

	private Composite classicComp, xsdComp;

	private Text txtXSD, txtEncoding;
	private Button btnXSDVariable, btnXSDBrowse, btnXSDDefinition;
	private ComboViewer cbXPathsViewer/*, cbEncodingViewer*/;
	private CCombo cbXPaths/*, cbEncoding*/;

	private Button btnClassic, btnXSD;

	private Text rootTag;
	private Text rowTag;
	private Text txtDefaultAttributeName;

	private Text txtPublicKey;
	private Text txtPrivateKey;
	private Text txtPassword;

	private Button truncate;
	private Button btnEncrypt, btnPublicVariable, btnPublicBrowse, btnPrivateVariable, btnPrivateBrowse;

	public FileXMLGeneralSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		Composite optionComp = getWidgetFactory().createComposite(composite);
		optionComp.setLayout(new GridLayout());
		optionComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Label lblEncoding = getWidgetFactory().createLabel(optionComp, Messages.FileXMLGeneralSection_1);
		lblEncoding.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtEncoding = getWidgetFactory().createText(optionComp, ""); //$NON-NLS-1$
		txtEncoding.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtEncoding.addModifyListener(listener);
		
//		cbEncoding = getWidgetFactory().createCCombo(optionComp);
//		cbEncoding.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
//		cbEncoding.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (cbEncodingViewer.getSelection() != null) {
//					String encoding = ((IStructuredSelection) cbEncodingViewer.getSelection()).getFirstElement().toString();
//					((FileXML) node.getGatewayModel()).setEncoding(encoding);
//				}
//			}
//		});
//		cbEncodingViewer = new ComboViewer(cbEncoding);
//		cbEncodingViewer.setLabelProvider(new LabelProvider() {
//			@Override
//			public String getText(Object element) {
//				return element != null ? element.toString() : "";
//			}
//		});
//		cbEncodingViewer.setContentProvider(new ArrayContentProvider());

		btnClassic = getWidgetFactory().createButton(optionComp, Messages.FileXMLGeneralSection_3, SWT.RADIO);
		btnClassic.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FileXML) node.getGatewayModel()).setFromXSD(false);
				refreshOptionPart(true);
			}
		});

		classicComp = getWidgetFactory().createComposite(optionComp);
		classicComp.setLayout(new GridLayout(2, false));
		classicComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Label l = getWidgetFactory().createLabel(classicComp, Messages.FileXMLGeneralSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		rootTag = getWidgetFactory().createText(classicComp, ""); //$NON-NLS-1$
		rootTag.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rootTag.addModifyListener(listener);

		Label l3 = getWidgetFactory().createLabel(classicComp, Messages.FileXMLGeneralSection_2);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		rowTag = getWidgetFactory().createText(classicComp, ""); //$NON-NLS-1$
		rowTag.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rowTag.addModifyListener(listener);

		Label l4 = getWidgetFactory().createLabel(classicComp, "Default attribute name (if value is set inside an attribute)");
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtDefaultAttributeName = getWidgetFactory().createText(classicComp, ""); //$NON-NLS-1$
		txtDefaultAttributeName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtDefaultAttributeName.addModifyListener(listener);

		btnXSD = getWidgetFactory().createButton(optionComp, Messages.FileXMLGeneralSection_5, SWT.RADIO);
		btnXSD.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FileXML) node.getGatewayModel()).setFromXSD(true);
				refreshOptionPart(false);
			}
		});

		xsdComp = getWidgetFactory().createComposite(optionComp);
		xsdComp.setLayout(new GridLayout(4, false));
		xsdComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Label lblXSD = getWidgetFactory().createLabel(xsdComp, Messages.FileXMLGeneralSection_6);
		lblXSD.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		txtXSD = getWidgetFactory().createText(xsdComp, ""); //$NON-NLS-1$
		txtXSD.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		btnXSDVariable = getWidgetFactory().createButton(xsdComp, Messages.FileGeneralSection_2, SWT.PUSH);
		btnXSDVariable.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnXSDVariable.addSelectionListener(variableAdapter);

		btnXSDBrowse = getWidgetFactory().createButton(xsdComp, Messages.FileGeneralSection_3, SWT.PUSH);
		btnXSDBrowse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnXSDBrowse.addSelectionListener(browseAdapter);

		Label lblXPath = getWidgetFactory().createLabel(xsdComp, Messages.FileXMLGeneralSection_7);
		lblXPath.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		cbXPaths = getWidgetFactory().createCCombo(xsdComp);
		cbXPaths.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		cbXPaths.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object selection = ((IStructuredSelection) cbXPathsViewer.getSelection()).getFirstElement();

				if (selection instanceof DefinitionXSD) {
					DefinitionXSD definition = (DefinitionXSD) selection;
					definition.setType(DefinitionXSD.ITERABLE);
					((FileXML) node.getGatewayModel()).setRootElement(definition);
					try {
						FileXMLHelper.getRootXSD((FileXML) node.getGatewayModel(), false);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		cbXPathsViewer = new ComboViewer(cbXPaths);
		cbXPathsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DefinitionXSD) {
					return ((DefinitionXSD) element).getElementPath();
				}
				return ""; //$NON-NLS-1$
			}
		});
		cbXPathsViewer.setContentProvider(new ArrayContentProvider());

		btnXSDDefinition = getWidgetFactory().createButton(xsdComp, Messages.FileXMLGeneralSection_9, SWT.PUSH);
		btnXSDDefinition.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1));
		btnXSDDefinition.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (node.getGatewayModel() instanceof FileOutputXML) {
					try {
						FileXMLHelper.getRootXSD((FileXML) node.getGatewayModel(), true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				DialogXSDDefinition dial = new DialogXSDDefinition(getPart().getSite().getShell(), (FileXML) node.getGatewayModel());
				dial.open();
			}
		});

		truncate = getWidgetFactory().createButton(composite, Messages.FileXMLGeneralSection_4, SWT.CHECK);
		truncate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		truncate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FileOutputXML) node.getGatewayModel()).setDelete(truncate.getSelection());
			}
		});

		btnEncrypt = getWidgetFactory().createButton(composite, Messages.FileXMLGeneralSection_10, SWT.CHECK);
		btnEncrypt.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		btnEncrypt.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FileXML) node.getGatewayModel()).setEncrypting(btnEncrypt.getSelection());

				if (btnEncrypt.getSelection() && node.getGatewayModel() instanceof FileOutputXML) {
					truncate.setSelection(true);
					truncate.setEnabled(false);
					((FileOutputXML) node.getGatewayModel()).setDelete(true);
				}
				else {
					truncate.setEnabled(true);
				}

				refreshKeyPart((FileXML) node.getGatewayModel(), btnEncrypt.getSelection());
			}
		});

		Composite encryptComp = getWidgetFactory().createComposite(composite);
		encryptComp.setLayout(new GridLayout(4, false));
		encryptComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Label lblPublicKey = getWidgetFactory().createLabel(encryptComp, Messages.FileXMLGeneralSection_11);
		lblPublicKey.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		txtPublicKey = getWidgetFactory().createText(encryptComp, ""); //$NON-NLS-1$
		txtPublicKey.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		btnPublicVariable = getWidgetFactory().createButton(encryptComp, Messages.FileGeneralSection_2, SWT.PUSH);
		btnPublicVariable.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnPublicVariable.addSelectionListener(variableAdapter);

		btnPublicBrowse = getWidgetFactory().createButton(encryptComp, Messages.FileGeneralSection_3, SWT.PUSH);
		btnPublicBrowse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnPublicBrowse.addSelectionListener(browseAdapter);

		Label lblPrivateKey = getWidgetFactory().createLabel(encryptComp, Messages.FileXMLGeneralSection_12);
		lblPrivateKey.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		txtPrivateKey = getWidgetFactory().createText(encryptComp, ""); //$NON-NLS-1$
		txtPrivateKey.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		btnPrivateVariable = getWidgetFactory().createButton(encryptComp, Messages.FileGeneralSection_2, SWT.PUSH);
		btnPrivateVariable.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnPrivateVariable.addSelectionListener(variableAdapter);

		btnPrivateBrowse = getWidgetFactory().createButton(encryptComp, Messages.FileGeneralSection_3, SWT.PUSH);
		btnPrivateBrowse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnPrivateBrowse.addSelectionListener(browseAdapter);

		Label lblPassword = getWidgetFactory().createLabel(encryptComp, Messages.FileXMLGeneralSection_13);
		lblPassword.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		txtPassword = getWidgetFactory().createText(encryptComp, "", SWT.PASSWORD); //$NON-NLS-1$
		txtPassword.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
	}

	private void refreshOptionPart(boolean isClassic) {
		classicComp.setEnabled(isClassic);
		rootTag.setEnabled(isClassic);
		rowTag.setEnabled(isClassic);
		txtDefaultAttributeName.setEnabled(isClassic);

		btnClassic.setSelection(isClassic);

		xsdComp.setEnabled(!isClassic);
		cbXPaths.setEnabled(!isClassic);
		txtXSD.setEnabled(!isClassic);
		btnXSDVariable.setEnabled(!isClassic);
		btnXSDBrowse.setEnabled(!isClassic);
		btnXSDDefinition.setEnabled(!isClassic);

		btnXSD.setSelection(!isClassic);

		if (node.getGatewayModel() instanceof FileOutputXML) {
			txtEncoding.setEnabled(false);
			cbXPaths.setEnabled(false);
		}
	}

	private void refreshKeyPart(FileXML model, boolean selection) {
		if (selection) {
			selection = model instanceof FileOutputXML;
			
			txtPublicKey.setEnabled(selection);
			btnPublicBrowse.setEnabled(selection);
			btnPublicVariable.setEnabled(selection);

			txtPrivateKey.setEnabled(!selection);
			btnPrivateBrowse.setEnabled(!selection);
			btnPrivateVariable.setEnabled(!selection);
			txtPassword.setEnabled(!selection);
		}
		else {
			txtPublicKey.setEnabled(false);
			btnPublicBrowse.setEnabled(false);
			btnPublicVariable.setEnabled(false);

			txtPrivateKey.setEnabled(false);
			btnPrivateBrowse.setEnabled(false);
			btnPrivateVariable.setEnabled(false);
			txtPassword.setEnabled(false);
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

	@Override
	public void refresh() {
		FileXML fileXML = (FileXML) node.getGatewayModel();
		FileXMLProperties properties = (FileXMLProperties) node.getAdapter(IPropertySource.class);

		txtXSD.removeModifyListener(listener);
		txtXSD.setText(fileXML.getXsdFilePath() != null ? fileXML.getXsdFilePath() : ""); //$NON-NLS-1$
		txtXSD.addModifyListener(listener);

		if (fileXML.isFromXSD()) {
			refreshCbXPath(fileXML);
		}
//		refreshEncoding(fileXML);

		rootTag.removeModifyListener(listener);
		rootTag.setText("" + properties.getPropertyValue(FileXMLProperties.PROPERTY_ROOT_TAG)); //$NON-NLS-1$
		rootTag.addModifyListener(listener);

		rowTag.removeModifyListener(listener);
		rowTag.setText("" + properties.getPropertyValue(FileXMLProperties.PROPERTY_ROW_TAG)); //$NON-NLS-1$
		rowTag.addModifyListener(listener);
		
		if (fileXML instanceof FileInputXML) {
			txtEncoding.removeModifyListener(listener);
			txtEncoding.setText(fileXML.getEncoding() != null ? fileXML.getEncoding() : "UTF-8"); //$NON-NLS-1$
			txtEncoding.addModifyListener(listener);
			
			txtPrivateKey.removeModifyListener(listener);
			txtPrivateKey.setText(((FileInputXML) fileXML).getPrivateKeyPath() != null ? ((FileInputXML) fileXML).getPrivateKeyPath() : ""); //$NON-NLS-1$
			txtPrivateKey.addModifyListener(listener);

			txtPassword.removeModifyListener(listener);
			txtPassword.setText(((FileInputXML) fileXML).getPassword() != null ? ((FileInputXML) fileXML).getPassword() : ""); //$NON-NLS-1$
			txtPassword.addModifyListener(listener);

			txtDefaultAttributeName.removeModifyListener(listener);
			txtDefaultAttributeName.setText(((FileInputXML) fileXML).getDefaultAttributeName() != null ? ((FileInputXML) fileXML).getDefaultAttributeName() : ""); //$NON-NLS-1$
			txtDefaultAttributeName.addModifyListener(listener);

			truncate.setVisible(false);

			btnEncrypt.setText(Messages.FileXMLGeneralSection_15);
		}
		else if (fileXML instanceof FileOutputXML) {
			truncate.setVisible(true);
			truncate.setSelection(((FileOutputXML) node.getGatewayModel()).getDelete());

			txtPublicKey.removeModifyListener(listener);
			txtPublicKey.setText(((FileOutputXML) fileXML).getPublicKeyPath() != null ? ((FileOutputXML) fileXML).getPublicKeyPath() : ""); //$NON-NLS-1$
			txtPublicKey.addModifyListener(listener);

			btnEncrypt.setText(Messages.FileXMLGeneralSection_16);
		}
		else {
			truncate.setVisible(false);
		}

		if (fileXML.isEncrypting()) {
			btnEncrypt.setSelection(true);
			truncate.setSelection(true);
			truncate.setEnabled(false);
		}
		else {
			truncate.setEnabled(true);
			btnEncrypt.setSelection(false);
		}

		refreshKeyPart(fileXML, fileXML.isEncrypting());
		refreshOptionPart(!fileXML.isFromXSD());
	}

	private void refreshCbXPath(FileXML fileXML) {
		if (!(node.getGatewayModel() instanceof FileOutputXML)) {
			try {
				List<DefinitionXSD> xsdDefs = FileXMLHelper.getXPaths(fileXML);
				DefinitionXSD selectedDef = null;
				if (xsdDefs != null && fileXML.getRootElement() != null) {
					for (DefinitionXSD def : xsdDefs) {
						if (def.getElementPath().equals(fileXML.getRootElement().getElementPath())) {
							selectedDef = def;
							break;
						}
					}
				}

				cbXPathsViewer.setInput(xsdDefs.toArray(new DefinitionXSD[xsdDefs.size()]));

				if (selectedDef != null) {
					cbXPathsViewer.setSelection(new StructuredSelection(selectedDef));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//	private void refreshEncoding(FileXML fileXML) {
//		if (!(node.getGatewayModel() instanceof FileOutputXML)) {
//			try {
//				List<String> encodings = new ArrayList<String>();
//				
//				
//				String selectedEncoding = null;
//				if (fileXML.getEncoding() != null) {
//					for (String encoding : encodings) {
//						if (fileXML.getEncoding().equals(encoding)) {
//							selectedEncoding = encoding;
//							break;
//						}
//					}
//				}
//
//				cbEncodingViewer.setInput(encodings.toArray(new String[encodings.size()]));
//
//				if (selectedEncoding != null) {
//					cbXPathsViewer.setSelection(new StructuredSelection(selectedEncoding));
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			FileXMLProperties properties = (FileXMLProperties) node.getAdapter(IPropertySource.class);

			if (evt.widget == txtEncoding) {
				((FileInputXML) node.getGatewayModel()).setEncoding(txtEncoding.getText());
			}
			else if (evt.widget == rootTag) {
				properties.setPropertyValue(FileXMLProperties.PROPERTY_ROOT_TAG, rootTag.getText());
			}
			else if (evt.widget == rowTag) {
				properties.setPropertyValue(FileXMLProperties.PROPERTY_ROW_TAG, rowTag.getText());
			}
			else if (evt.widget == txtDefaultAttributeName) {
				((FileInputXML) node.getGatewayModel()).setDefaultAttributeName(txtDefaultAttributeName.getText());
			}
			else if (evt.widget == txtPublicKey) {
				((FileOutputXML) node.getGatewayModel()).setPublicKeyPath(txtPublicKey.getText());
			}
			else if (evt.widget == txtPrivateKey) {
				((FileInputXML) node.getGatewayModel()).setPrivateKeyPath(txtPrivateKey.getText());
			}
			else if (evt.widget == txtPassword) {
				((FileInputXML) node.getGatewayModel()).setPassword(txtPassword.getText());
			}
			else if (evt.widget == txtXSD) {
				((FileXML) node.getGatewayModel()).setXsdFilePath(txtXSD.getText());
				refreshCbXPath((FileXML) node.getGatewayModel());
			}
		}
	};

	private SelectionAdapter variableAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			DialogPickupConstant d = new DialogPickupConstant(getPart().getSite().getShell());
			if (d.open() == Dialog.OK) {
				if (e.getSource() == btnPublicVariable) {
					txtPublicKey.setText(d.getVariable().getOuputName());

					Event ev = new Event();
					ev.widget = txtPublicKey;
					listener.modifyText(new ModifyEvent(ev));
				}
				else if (e.getSource() == btnPrivateVariable) {
					txtPrivateKey.setText(d.getVariable().getOuputName());

					Event ev = new Event();
					ev.widget = txtPublicKey;
					listener.modifyText(new ModifyEvent(ev));
				}
				else if (e.getSource() == btnXSDVariable) {
					txtXSD.setText(d.getVariable().getOuputName());

					Event ev = new Event();
					ev.widget = txtXSD;
					listener.modifyText(new ModifyEvent(ev));
				}
			}
		}
	};

	private SelectionAdapter browseAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog fd = new FileDialog(getPart().getSite().getShell());

			String path = fd.open();
			if (path != null) {
				if (e.getSource() == btnPublicBrowse) {
					txtPublicKey.setText(path);

					Event ev = new Event();
					ev.widget = txtPublicKey;
					listener.modifyText(new ModifyEvent(ev));
				}
				else if (e.getSource() == btnPrivateBrowse) {
					txtPrivateKey.setText(path);

					Event ev = new Event();
					ev.widget = txtPublicKey;
					listener.modifyText(new ModifyEvent(ev));
				}
				else if (e.getSource() == btnXSDBrowse) {
					txtXSD.setText(path);

					Event ev = new Event();
					ev.widget = txtXSD;
					listener.modifyText(new ModifyEvent(ev));
				}
			}
		}
	};
}
