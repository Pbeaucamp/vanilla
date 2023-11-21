package bpm.vanilla.map.design.ui.dialogs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.mapmanager.Activator;
import bpm.vanilla.map.core.design.IOpenGisMapService;
import bpm.vanilla.map.core.design.opengis.IOpenGisDatasource;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapEntity;
import bpm.vanilla.map.core.design.opengis.OpenGisMapObject;
import bpm.vanilla.map.core.design.opengis.OpenGisShapeFileDatasource;
import bpm.vanilla.map.core.design.opengis.ShapeFileParser;

public class DialogAddOpenGisMap extends Dialog {

	private Text txtFilePath;
	private Button btnFilePath;
	
	private Text txtName;
	
	private String fileName;
	private TableViewer entityTable;
	private Button btnLoadEntities;
	
	private List<IOpenGisMapEntity> entities;
	
	
	public DialogAddOpenGisMap(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		parent.setLayout(new GridLayout(3,false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblName = new Label(parent, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		lblName.setText("Name");
		
		txtName = new Text(parent, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		Label lblFilePath = new Label(parent, SWT.NONE);
		lblFilePath.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		lblFilePath.setText("Shape file");
		
		txtFilePath = new Text(parent, SWT.BORDER);
		txtFilePath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		txtFilePath.setEnabled(false);
		
		btnFilePath = new Button(parent, SWT.PUSH);
		btnFilePath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		btnFilePath.setText("...");
		
		btnLoadEntities = new Button(parent, SWT.PUSH);
		btnLoadEntities.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		btnLoadEntities.setText("Load entities from selected ShapeFile");
		
		entityTable = new TableViewer(parent);
		entityTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		createTableColumnsAndProviders();
		
		createHandlers();
		
		return parent;
	}

	private void createTableColumnsAndProviders() {
		TableColumn colName = new TableColumn(entityTable.getTable(), SWT.NONE);
		colName.setText("Name");
		colName.setWidth(200);
		
		TableColumn colCoord = new TableColumn(entityTable.getTable(), SWT.NONE);
		colCoord.setText("Entity id");
		colCoord.setWidth(400);
		
		entityTable.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}
			
			@Override
			public void dispose() {

			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return ((List)inputElement).toArray();
			}
		});
		
		entityTable.setLabelProvider(new ITableLabelProvider() {
			
			@Override
			public void removeListener(ILabelProviderListener listener) {

			}
			
			@Override
			public boolean isLabelProperty(Object element, String property) {

				return false;
			}
			
			@Override
			public void dispose() {
				
			}
			
			@Override
			public void addListener(ILabelProviderListener listener) {
				
			}
			
			@Override
			public String getColumnText(Object element, int columnIndex) {
				
				IOpenGisMapEntity entity = (IOpenGisMapEntity) element;
				switch(columnIndex) {
					case 0 : return entity.getName();
					case 1 : return entity.getEntityId();
				}
				return null;
			}
			
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}
		});
		entityTable.getTable().setHeaderVisible(true);
		entityTable.getTable().setLinesVisible(true);
	}

	private void createHandlers() {
		txtName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateDialogButtons();
			}
		});
		
		btnFilePath.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dial = new FileDialog(getShell());
				fileName = dial.open();
				txtFilePath.setText(fileName);
				
			};
		});
		
		btnLoadEntities.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					entities = ShapeFileParser.parseShapeFile(txtFilePath.getText(), null);
					entityTable.setInput(entities);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void updateDialogButtons() {
//		if(txtName.getText() != null && !txtName.getText().equals("") && txtFilePath.getText() != null && !txtFilePath.getText().equals("")) {
			this.getButton(IDialogConstants.OK_ID).setEnabled(true);
//		}
//		else {
//			this.getButton(IDialogConstants.OK_ID).setEnabled(false);
//		}
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 500);
	}
	
	@Override
	protected void okPressed() {
		IOpenGisMapService service = Activator.getDefault().getOpenGisService();
		
		OpenGisMapObject map = new OpenGisMapObject();
		
		map.setName(txtName.getText());
		map.setEntities(entities);
		
		OpenGisShapeFileDatasource ds = new OpenGisShapeFileDatasource();
		ds.setFormat("shp");
		ds.setType(IOpenGisDatasource.SHAPE_FILE);
		
		map.setDatasource(ds);
		
		InputStream fileShape = null;
		try {
			fileShape = new FileInputStream(txtFilePath.getText());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			service.addOpenGisMap(map, fileShape);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.okPressed();
	}
}
