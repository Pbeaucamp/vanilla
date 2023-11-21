package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.cubewizard.DialogSelectRepositoryItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.DrillReport;
import org.fasd.olap.DrillReportParameter;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.freeolap.FreemetricsPlugin;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogDrillReport extends Dialog {

	private OLAPCube cube;
	
	private Text txtName;
	private Text txtSelectedReport;
	private Button btnBrowseReport;
	
	private RepositoryItem selectedReport;
	
	private TableViewer tableParameters;
	
	private String[] dimensions = new String[0];
	private String[] levels = new String[0];
	
	private List<DrillReportParameter> parameters = new ArrayList<DrillReportParameter>();
	
	private ComboBoxCellEditor levelEditor;
	
	@Override
	protected void initializeBounds() {
		getShell().setText(LanguageText.DialogDrillElement_0);
		
	}
	
	public DialogDrillReport(Shell parentShell, OLAPCube cube) {
		super(parentShell);
		this.cube = cube;
		
		dimensions = new String[cube.getDims().size()];
		for(int i = 0 ; i < cube.getDims().size() ; i++) {
			dimensions[i] = cube.getDims().get(i).getName();
		}
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(3, false));
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblName = new Label(c, SWT.NONE);
		lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblName.setText(LanguageText.DialogDrillReport_0);
		
		txtName = new Text(c, SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Label lblFasd = new Label(c, SWT.NONE);
		lblFasd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		lblFasd.setText(LanguageText.DialogDrillReport_1);
		
		txtSelectedReport = new Text(c, SWT.BORDER);
		txtSelectedReport.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtSelectedReport.setEnabled(false);
		
		btnBrowseReport = new Button(c, SWT.PUSH);
		btnBrowseReport.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		btnBrowseReport.setText("..."); //$NON-NLS-1$
		btnBrowseReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSelectRepositoryItem dial = new DialogSelectRepositoryItem(getParentShell(), IRepositoryApi.CUST_TYPE, IRepositoryApi.BIRT_REPORT_SUBTYPE);
				if(dial.open() == Dialog.OK) {
					selectedReport = dial.getItem();
					txtSelectedReport.setText(selectedReport.getItemName());
					
					try {
						
						IReportRuntimeConfig runtimeConfig = new ReportRuntimeConfig(
								new ObjectIdentifier(FreemetricsPlugin.getDefault().getRepositoryContext().getRepository().getId(), 
										selectedReport.getId()), 
								null, 
								FreemetricsPlugin.getDefault().getRepositoryContext().getGroup().getId());
						
						List<VanillaGroupParameter> params = FreemetricsPlugin.getDefault().getReportRuntime().getParameters(runtimeConfig);
						
						parameters.clear();
						
						for(VanillaGroupParameter param : params) {
							for(VanillaParameter pa : param.getParameters()) {
								DrillReportParameter p = new DrillReportParameter();
								p.setName(pa.getName());
								if(parameters == null) {
									parameters = new ArrayList<DrillReportParameter>();
								}
								parameters.add(p);
							}
						}
						
						
						tableParameters.setInput(parameters.toArray());
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		tableParameters = new TableViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		tableParameters.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		tableParameters.setContentProvider(new ArrayContentProvider());
		tableParameters.getTable().setHeaderVisible(true);
		tableParameters.getTable().setLinesVisible(true);
		
		TableViewerColumn colParamName = new TableViewerColumn(tableParameters, SWT.NONE);
		colParamName.getColumn().setText(LanguageText.DialogDrillReport_3);
		colParamName.getColumn().setWidth(150);
		colParamName.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				DrillReportParameter param = (DrillReportParameter) element;
				return param.getName();
			}
		});
		
		TableViewerColumn colParamDimension = new TableViewerColumn(tableParameters, SWT.NONE);
		colParamDimension.getColumn().setText(LanguageText.DialogDrillReport_4);
		colParamDimension.getColumn().setWidth(150);
		colParamDimension.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				DrillReportParameter param = (DrillReportParameter) element;
				return param.getDimension();
			}
		});
		colParamDimension.setEditingSupport(new EditingSupport(tableParameters) {
			ComboBoxCellEditor editor = new ComboBoxCellEditor(tableParameters.getTable(), dimensions, SWT.READ_ONLY);
			@Override
			protected void setValue(Object element, Object value) {
				DrillReportParameter param = (DrillReportParameter) element;
				param.setDimension(dimensions[(Integer) value]);
				
				//fill the level list
				for(OLAPDimension dim : cube.getDims()) {
					if(dim.getName().equals(dimensions[(Integer) value])) {
						levels = new String[dim.getHierarchies().get(0).getLevels().size()];
						for(int i = 0 ; i < dim.getHierarchies().get(0).getLevels().size() ; i++) {
							levels[i] = dim.getHierarchies().get(0).getLevels().get(i).getName();
						}
						break;
					}
				}
				
				levelEditor.setItems(levels);

				tableParameters.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				DrillReportParameter param = (DrillReportParameter) element;
				int index = ArrayUtils.indexOf(dimensions, param.getDimension()); 
				return index;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
		TableViewerColumn colParamLevel = new TableViewerColumn(tableParameters, SWT.NONE);
		colParamLevel.getColumn().setText(LanguageText.DialogDrillReport_5);
		colParamLevel.getColumn().setWidth(150);
		colParamLevel.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				DrillReportParameter param = (DrillReportParameter) element;
				return param.getLevel();
			}
		});
		colParamLevel.setEditingSupport(new EditingSupport(tableParameters) {
			@Override
			protected void setValue(Object element, Object value) {
				DrillReportParameter param = (DrillReportParameter) element;
				param.setLevel(levels[(Integer) value]);
				
				tableParameters.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				DrillReportParameter param = (DrillReportParameter) element;
				return ArrayUtils.indexOf(levels, param.getLevel());
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return levelEditor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
		levelEditor = new ComboBoxCellEditor(tableParameters.getTable(), levels, SWT.READ_ONLY);
		
		return c;
	}
	
	@Override
	protected void okPressed() {
		
		DrillReport d = new DrillReport();
		d.setDrillName(txtName.getText());
		d.setItemId(selectedReport.getId());
		d.setName(selectedReport.getItemName());
		
		d.setParameters(parameters);
		
		cube.addDrill(d);
		
		super.okPressed();
	}

}
