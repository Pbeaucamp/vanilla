package bpm.gateway.ui.views.property.sections;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.core.transformations.SqlLookup;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.ui.dialogs.database.DialogSqlDesigner;
import bpm.gateway.ui.dialogs.utils.fields.DialogFieldsValues;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.properties.DataStreamProperties;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogBrowseContent;

public class StreamDefinitionSection extends AbstractPropertySection {
	private static final int SAVE_SIZED = 40;

	private Node node;
	
	private Text definitionTxt;
	private boolean first = true;
	
	public StreamDefinitionSection() {
	}

	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		
		Label addressLabel = new Label(main, SWT.NONE);
		addressLabel.setText(Messages.StreamDefinitionSection_11);
		addressLabel.setLayoutData(gridData);
		
		definitionTxt = new Text(main, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		definitionTxt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		definitionTxt.setText(""); //$NON-NLS-1$

		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		
		Composite buttonBar = getWidgetFactory().createComposite(main);
		buttonBar.setLayout(new GridLayout(5, true));
		buttonBar.setLayoutData(gridData);
      
		Button apply = getWidgetFactory().createButton(buttonBar, Messages.StreamDefinitionSection_0, SWT.PUSH);
		apply.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		apply.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				DataStreamProperties properties = (DataStreamProperties) node.getAdapter(IPropertySource.class);
				properties.setPropertyValue(DataStreamProperties.PROPERTY_SQL_DEFINITION, definitionTxt.getText());
			}
		});
      
		Button cancel = getWidgetFactory().createButton(buttonBar, Messages.StreamDefinitionSection_1, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		
        Button queryDesigner = getWidgetFactory().createButton(buttonBar, Messages.StreamDefinitionSection_2, SWT.PUSH);
        queryDesigner.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        queryDesigner.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DataBaseServer server = (DataBaseServer)((DataStream)node.getGatewayModel()).getServer();
				DialogSqlDesigner dial = new DialogSqlDesigner(getPart().getSite().getShell(), server, node.getGatewayModel().getDocument());
				if (dial.open() ==  DialogSqlDesigner.OK){
					definitionTxt.setText(dial.getSqlStatement());
					DataStreamProperties properties = (DataStreamProperties) node.getAdapter(IPropertySource.class);
					properties.setPropertyValue(DataStreamProperties.PROPERTY_SQL_DEFINITION, definitionTxt.getText());
				}
			}
        });
        
		
        Button bBrowse = getWidgetFactory().createButton(buttonBar, Messages.StreamDefinitionSection_3, SWT.PUSH);
		bBrowse.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bBrowse.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DataStream os = (DataStream)node.getGatewayModel();
				Shell sh = getPart().getSite().getShell();
				
				
				try {
					
					List<List<Object>> lst = null;
					
					if (os instanceof DataBaseInputStream || os instanceof DataBaseInputStream || os instanceof SqlLookup){
						lst = DataBaseHelper.getValues(os, 100);
					}
					else if (os instanceof FileCSV){
						lst = FileCSVHelper.getValues((FileCSV)os, 0 , 100);
					}
					else if (os instanceof FileXLS){
						lst = FileXLSHelper.getValues((FileXLS)os, 0, 100);
					}
					
					List<StreamElement> cols = null;
					
					if (os instanceof SqlLookup){
						SqlLookup _l = ((SqlLookup)os);
						cols = _l.getInputs().get(0).getDescriptor(_l).getStreamElements();
					}
					else{
						cols = os.getDescriptor(os).getStreamElements();
					}
					
					DialogBrowseContent dial = new DialogBrowseContent(sh, lst, cols);
					dial.open();
					
				}catch (FileNotFoundException e1){
					MessageDialog.openError(sh, Messages.StreamDefinitionSection_4, Messages.StreamDefinitionSection_5 + e1.getMessage());
				}catch(SQLException e1){
					MessageDialog.openError(sh, Messages.StreamDefinitionSection_6, Messages.StreamDefinitionSection_7 + e1.getMessage());
				
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(sh, Messages.StreamDefinitionSection_8, Messages.StreamDefinitionSection_9 + e1.getMessage());
				}
			}
		});
	
		Button bDistcintValues = getWidgetFactory().createButton(buttonBar, Messages.StreamDefinitionSection_10, SWT.PUSH);
		bDistcintValues.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bDistcintValues.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DataStream os = (DataStream)node.getGatewayModel();
				Shell sh = getPart().getSite().getShell();

				DialogFieldsValues dial = new DialogFieldsValues(sh, os);
				dial.open();
			}
		});
	}
	
	@Override
	public void refresh() {
		DataStreamProperties properties = (DataStreamProperties) node.getAdapter(IPropertySource.class);
		definitionTxt.setText((String)properties.getPropertyValue(DataStreamProperties.PROPERTY_SQL_DEFINITION));
		
//		if(!first){
			((GridData)definitionTxt.getLayoutData()).heightHint = SAVE_SIZED;
			((GridData)definitionTxt.getLayoutData()).widthHint = SAVE_SIZED;
//		}
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
	}
	
	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		if(first){
			first = false;
		}
	}
}
