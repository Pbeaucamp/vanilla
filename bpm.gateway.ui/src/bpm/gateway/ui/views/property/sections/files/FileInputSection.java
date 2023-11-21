package bpm.gateway.ui.views.property.sections.files;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.OracleXmlViewHelper;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.core.server.file.FileShape;
import bpm.gateway.core.server.file.FileShapeHelper;
import bpm.gateway.core.server.file.FileVCL;
import bpm.gateway.core.server.file.FileVCLHelper;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.core.server.file.FileXMLHelper;
import bpm.gateway.core.transformations.files.FileFolderHelper;
import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.DataPreparationInput;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.inputs.FileInputShape;
import bpm.gateway.core.transformations.inputs.FileInputVCL;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.core.transformations.inputs.FileInputXML;
import bpm.gateway.core.transformations.inputs.OracleXmlView;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.dialogs.utils.fields.DialogFieldsValues;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.tools.dialogs.DialogBrowseContent;

public class FileInputSection extends AbstractPropertySection {

	private Node node;

	public FileInputSection() {
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
	public void createControls(Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));

		Button b = getWidgetFactory().createButton(composite, Messages.FileInputSection_0, SWT.PUSH);
		b.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.structure_16));
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				try {

					if (node.getGatewayModel() instanceof FileInputXLS) {
						FileXLSHelper.createStreamDescriptor((FileInputXLS) node.getGatewayModel());
					}
					else if (node.getGatewayModel() instanceof FileInputCSV) {
						FileCSVHelper.createStreamDescriptor((FileInputCSV) node.getGatewayModel(), 100);
					}
					else if (node.getGatewayModel() instanceof FileInputXML) {
						FileXMLHelper.createStreamDescriptor((FileInputXML) node.getGatewayModel(), 100);
					}
					else if (node.getGatewayModel() instanceof OracleXmlView) {
						OracleXmlViewHelper.createStreamDescriptor((OracleXmlView) node.getGatewayModel(), 100);
					}
					else if (node.getGatewayModel() instanceof FileInputVCL) {
						FileVCLHelper.createStreamDescriptor((FileInputVCL) node.getGatewayModel(), 100);
					}
					else if (node.getGatewayModel() instanceof FileInputShape) {
						FileShapeHelper.createStreamDescriptor((FileInputShape) node.getGatewayModel(), 100);
					}
					else if (node.getGatewayModel() instanceof FileFolderReader) {
						try {
							FileFolderHelper.createStreamDescriptor((FileFolderReader) node.getGatewayModel(), 100);
							MessageDialog.openInformation(getPart().getSite().getShell(), Messages.FileInputSection_6, Messages.FileInputSection_7);
						} catch (Exception ex) {
							ex.printStackTrace();
							MessageDialog.openError(getPart().getSite().getShell(), Messages.FileInputSection_8, Messages.FileInputSection_9 + ex.getMessage());
						}
					}
					else if (node.getGatewayModel() instanceof MdmContractFileInput) {
						((MdmContractFileInput) node.getGatewayModel()).initDescriptor();
					}
					else if (node.getGatewayModel() instanceof DataPreparationInput) {
						((DataPreparationInput) node.getGatewayModel()).initDescriptor();
					}
					else if (node.getGatewayModel() instanceof D4CInput) {
						((D4CInput) node.getGatewayModel()).initDescriptor();
					}
					for (ISection s : tabbedPropertySheetPage.getCurrentTab().getSections()) {
						s.refresh();
					}
					refresh();
				} catch (Throwable e1) {
					e1.printStackTrace();
					MessageDialog.openError(getPart().getSite().getShell(), Messages.FileInputSection_1, e1.getMessage());
				}

			}

		});

		Button bBrowse = getWidgetFactory().createButton(composite, Messages.FileInputSection_2, SWT.PUSH);
		bBrowse.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.browse_datas_16));
		bBrowse.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Transformation os = node.getGatewayModel();
				Shell sh = getPart().getSite().getShell();

				try {

					List<List<Object>> lst = null;

					if (os instanceof MdmContractFileInput) {
						os = ((MdmContractFileInput) os).getFileTransfo();
					}

					if (os instanceof DataPreparationInput) {
						os = ((DataPreparationInput) os).getFileTransfo();
					}

					if (os instanceof DataBaseInputStream) {
						lst = DataBaseHelper.getValues((DataStream) os, 100);
					}
					else if (os instanceof FileCSV) {
						lst = FileCSVHelper.getValues((FileCSV) os, 0, 100);
					}
					else if (os instanceof FileXLS) {
						lst = FileXLSHelper.getValues((FileXLS) os, 0, 100);
					}
					else if (os instanceof D4CInput) {
						if (((D4CInput) os).getFileTransfo() instanceof FileCSV) {
							lst = FileCSVHelper.getValues((D4CInput) os, 0, 100);
						}
						else if (((D4CInput) os).getFileTransfo() instanceof FileXLS) {
							lst = FileXLSHelper.getValues((D4CInput) os, 0, 100);
						}
					}
					else if (os instanceof FileXML) {
						lst = FileXMLHelper.getValues((FileXML) os, 0, 100);
					}
					else if (os instanceof FileVCL) {
						lst = FileVCLHelper.getValues((FileVCL) os, 0, 100);
					}
					else if (os instanceof FileFolderReader) {
						lst = FileFolderHelper.getValues((FileFolderReader) os, 0, 100);
					}
					else if (os instanceof FileShape) {
						lst = FileShapeHelper.getValues((FileShape) os, 0, 100);
					}
					else if (os instanceof OracleXmlView) {
						lst = OracleXmlViewHelper.getValues((OracleXmlView) os, 0, 100);
					}

					DialogBrowseContent dial = new DialogBrowseContent(sh, lst, os.getDescriptor(os).getStreamElements());
					dial.open();

				} catch (Throwable e1) {
					e1.printStackTrace();
					MessageDialog.openError(sh, Messages.FileInputSection_3, Messages.FileInputSection_4 + e1.getMessage());
				}

			}

		});

		Button bDistcintValues = getWidgetFactory().createButton(composite, Messages.FileInputSection_5, SWT.PUSH);
		bDistcintValues.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bDistcintValues.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DataStream os = null;
				if (node.getGatewayModel() instanceof MdmContractFileInput) {
					os = (DataStream) ((MdmContractFileInput) node.getGatewayModel()).getFileTransfo();
				}
				else if (node.getGatewayModel() instanceof DataPreparationInput) {
					os = (DataStream) ((DataPreparationInput) node.getGatewayModel()).getFileTransfo();
				}
				else {
					os = (DataStream) node.getGatewayModel();
				}

				Shell sh = getPart().getSite().getShell();

				DialogFieldsValues dial = new DialogFieldsValues(sh, os);
				dial.open();

			}

		});

	}

	@Override
	public void refresh() {

	}

}
