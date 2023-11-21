package bpm.gateway.ui.composites.file;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.i18n.Messages;

public class FileCsvComposite extends AbstractFileComposite {

	private Text separator;
	private Button skipFirstRow;
	private Button checkIsJson;
	private Text txtJsonRootItem;
	private Text txtJsonDepth;

	private FileInputCSV csvTransfo;

	public FileCsvComposite(Composite parent, int style, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(parent, style);

		csvTransfo = new FileInputCSV();

		this.setLayout(new GridLayout(2, false));
		this.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		Label l = widgetFactory.createLabel(this, Messages.FileCSVGeneralSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		separator = widgetFactory.createText(this, ""); //$NON-NLS-1$
		separator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		separator.addModifyListener(listener);

		skipFirstRow = widgetFactory.createButton(this, Messages.FileCSVGeneralSection_3, SWT.CHECK);
		skipFirstRow.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		skipFirstRow.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				csvTransfo.setSkipFirstRow(skipFirstRow.getSelection());
			}
		});
		
		checkIsJson = widgetFactory.createButton(this, "Is json", SWT.CHECK);
		checkIsJson.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		checkIsJson.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				csvTransfo.setJson(checkIsJson.getSelection());
				updateUi();
			}
		});
		
		l = widgetFactory.createLabel(this, "Json root item");
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		txtJsonRootItem = widgetFactory.createText(this, ""); //$NON-NLS-1$
		txtJsonRootItem.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtJsonRootItem.addModifyListener(listener);
		
		l = widgetFactory.createLabel(this, "Depth");
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
				
		txtJsonDepth = widgetFactory.createText(this, ""); //$NON-NLS-1$
		txtJsonDepth.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtJsonDepth.addModifyListener(listener);
	}

	private void updateUi() {
		boolean isJson = checkIsJson.getSelection();
		separator.setEnabled(!isJson);
		skipFirstRow.setEnabled(!isJson);
		txtJsonRootItem.setEnabled(isJson);
		txtJsonDepth.setEnabled(isJson);
	}

	public void setInput(Node node) {
		if (((MdmContractFileInput) node.getGatewayModel()).getFileTransfo() != null) {
			csvTransfo = (FileInputCSV) ((MdmContractFileInput) node.getGatewayModel()).getFileTransfo();
		}
		else if (((D4CInput) node.getGatewayModel()).getFileTransfo() != null) {
			csvTransfo = (FileInputCSV) ((D4CInput) node.getGatewayModel()).getFileTransfo();
		}
		else {
			csvTransfo = new FileInputCSV();
		}

		skipFirstRow.setEnabled(true);
	}

	@Override
	public AbstractTransformation getFileTransformation() {
		return csvTransfo;
	}

	@Override
	public void refresh(Node node) {
		if (node.getGatewayModel() instanceof MdmContractFileInput && ((MdmContractFileInput) node.getGatewayModel()).getFileTransfo() != null) {
			csvTransfo = (FileInputCSV) ((MdmContractFileInput) node.getGatewayModel()).getFileTransfo();
		}
		else if (node.getGatewayModel() instanceof D4CInput && ((D4CInput) node.getGatewayModel()).getFileTransfo() != null) {
			csvTransfo = (FileInputCSV) ((D4CInput) node.getGatewayModel()).getFileTransfo();
		}
		else {
			csvTransfo = new FileInputCSV();
		}

		separator.removeModifyListener(listener);
		separator.setText(csvTransfo.getSeparator() + ""); //$NON-NLS-1$
		separator.addModifyListener(listener);
		if (!(node.getGatewayModel() instanceof FileOutputCSV)) {
			skipFirstRow.setSelection(csvTransfo.isSkipFirstRow());
		}
		
		checkIsJson.setSelection(csvTransfo.isJson());
		
		txtJsonRootItem.removeModifyListener(listener);
		txtJsonRootItem.setText(csvTransfo.getJsonRootItem());
		txtJsonRootItem.addModifyListener(listener);
		
		txtJsonDepth.removeModifyListener(listener);
		txtJsonDepth.setText(csvTransfo.getJsonDepth() + ""); //$NON-NLS-1$
		txtJsonDepth.addModifyListener(listener);
		updateUi();
	}
	
	private ModifyListener listener = new ModifyListener() {
	    
        public void modifyText(ModifyEvent evt) {
        	if (evt.widget == separator){
        		csvTransfo.setSeparator(separator.getText());
        	}
        	else if (evt.widget == txtJsonRootItem){
        		csvTransfo.setJsonRootItem(txtJsonRootItem.getText());
        	}
        	else if (evt.widget == txtJsonDepth){
        		csvTransfo.setJsonDepth(txtJsonDepth.getText());
        	}
        }
    };
}
