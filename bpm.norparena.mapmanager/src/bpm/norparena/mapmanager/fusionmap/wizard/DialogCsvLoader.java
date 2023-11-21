package bpm.norparena.mapmanager.fusionmap.wizard;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.fusionmap.IFactoryFusionMap;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;

public class DialogCsvLoader extends Dialog{

	
	private class DialogValues extends Dialog{
		private List<List<Object>> values;
		protected DialogValues(Shell parentShell,List<List<Object>> values) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
			this.values = values;
			
		}
		@Override
		protected Control createDialogArea(Composite parent) {
			TableViewer viewer = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
			viewer.getTable().setLinesVisible(true);
			viewer.getTable().setHeaderVisible(true);
			viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
			viewer.setContentProvider(new ArrayContentProvider());
			
			for(int i = 0; i < values.get(0).size();i++){
				TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
				col.getColumn().setText(values.get(0)  + ""); //$NON-NLS-1$
				col.getColumn().setWidth(100);
				
				final int k = i;
				col.setLabelProvider(new ColumnLabelProvider(){
					@Override
					public String getText(Object element) {
						List e = (List)element;
						if (e.get(k) == null){
							return ""; //$NON-NLS-1$
						}
						return e.get(k) + ""; //$NON-NLS-1$
					}
				});
			}
			
			viewer.setInput(values);
			
			return viewer.getTable();
		}

		
		@Override
		protected void initializeBounds() {
			getShell().setText(Messages.DialogCsvLoader_3);
			getShell().setSize(800, 600);
		}
	}
	
	
	private FileInputCSV tr;
	
	private Text file;
	private Text separator;
	private Combo encoding;
	
	private Combo  internalIdentifier;
	private Combo  shortName;
	private Combo  longName;
	
	private List<IFusionMapSpecificationEntity> result;
	
	public DialogCsvLoader(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogCsvLoader_4);
		
		file = new Text(main, SWT.BORDER);
		file.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		file.setEnabled(false);
		
		
		Button b = new Button(main, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[]{"*.csv", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
				
				String f = fd.open();
				if (f != null){
					file.setText(f);
				}
			}
		});
		
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogCsvLoader_8);
		
		separator = new Text(main, SWT.BORDER);
		separator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		separator.setText(";"); //$NON-NLS-1$
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogCsvLoader_10);
		
		encoding = new Combo(main, SWT.BORDER);
		encoding.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		for(String s : Charset.availableCharsets().keySet()){
			encoding.add(s);
		}
		encoding.setText(Messages.DialogCsvLoader_11);
		
		Composite but = new Composite(main, SWT.NONE);
		but.setLayout(new GridLayout(2, true));
		but.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		
		Button loadFile = new Button(but, SWT.PUSH);
		loadFile.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		loadFile.setText(Messages.DialogCsvLoader_12);
		loadFile.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadFile();
			}
		});
		
		Button browseFile = new Button(but, SWT.PUSH);
		browseFile.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		browseFile.setText(Messages.DialogCsvLoader_13);
		browseFile.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseFile();
			}
		});
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogCsvLoader_14);
		
		internalIdentifier = new Combo(main, SWT.READ_ONLY);
		internalIdentifier.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogCsvLoader_15);
		
		longName = new Combo(main, SWT.READ_ONLY);
		longName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogCsvLoader_16);
		
		shortName = new Combo(main, SWT.READ_ONLY);
		shortName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		
		return main;
	}


	
	public void browseFile(){
		if (tr == null){
			MessageDialog.openInformation(getShell(), Messages.DialogCsvLoader_17, Messages.DialogCsvLoader_18);
			return;
		}
		
		try{
			DialogValues d = new DialogValues(getShell(), FileCSVHelper.getValues(tr, 0, 100));
			d.open();
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogCsvLoader_19, Messages.DialogCsvLoader_20 + ex.getMessage());
		}
	}
	
	private void loadFile(){
		tr = new FileInputCSV();
		tr.setDefinition(file.getText());
		tr.setEncoding(encoding.getText());
		tr.setSeparator(separator.getText().trim());
		StreamDescriptor desc = null;
		try{
			FileCSVHelper.createStreamDescriptor(tr, 2);
			desc = tr.getDescriptor(tr);
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogCsvLoader_21, Messages.DialogCsvLoader_22 + ex.getMessage());
			tr = null;
			return;
		}
		
		String[] fields = new String[desc.getColumnCount()];
		
		for(int i=0; i < desc.getColumnCount(); i++ ){
			fields[i] = desc.getColumnName(i);
		}
		
		internalIdentifier.setItems(fields);
		longName.setItems(fields);
		shortName.setItems(fields);
	}


	public void getEntities() throws Exception{
		result = new ArrayList<IFusionMapSpecificationEntity> ();
		
			IFactoryFusionMap fact = Activator.getDefault().getFactoryFusionMap();
		for(List<Object> l: FileCSVHelper.getValues(tr, 0, 100000)){
			IFusionMapSpecificationEntity e = fact.createFusionMapSpecificationEntity();
			
			e.setFusionMapInternalId(l.get(internalIdentifier.getSelectionIndex()) + ""); //$NON-NLS-1$
			e.setFusionMapShortName(l.get(shortName.getSelectionIndex()) + ""); //$NON-NLS-1$
			e.setFusionMapLongName(l.get(longName.getSelectionIndex()) + ""); //$NON-NLS-1$
			result.add(e);
		}

		
		
	}
	
	@Override
	protected void okPressed() {
		try{
			getEntities();
		}catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogCsvLoader_26, e.getMessage());
			return;
		}
		super.okPressed();
	}
	
	public List<IFusionMapSpecificationEntity> getResult(){
		return result;
	}
}
