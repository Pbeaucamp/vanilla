package bpm.gateway.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.ViewPart;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.runtime2.GatewayRuntimeException;
import bpm.gateway.runtime2.tools.RuntimeAppender;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.preferences.PreferencesConstants;

public class RuntimeConsoleViewPart extends ViewPart implements PropertyChangeListener{

	private HashMap<Text, Parameter> params = new HashMap<Text, Parameter>();
	
	private DocumentGateway gatewayModel;
	
	public  static final String ID = "bpm.gateway.ui.views.RuntimeConsoleViewPart"; //$NON-NLS-1$
	protected TableViewer tableViewer;
	private Combo logLevel;
	private String currentLevel;
	
	private Text engineState, bufferSize;
	private ToolItem start, stop, init;
	private Composite main;
	
	private TabFolder folder;
	
	
	private Button bLocal, bRemote;
//	private Combo runServer;
	
	private RuntimeViewHelper helper = new RuntimeViewHelper(this);
	
	public RuntimeConsoleViewPart() {
		Activator.localRuntimeEngine.MAX_ROWS = Activator.getDefault().getPreferenceStore().getInt(PreferencesConstants.P_MAX_ROW_PER_CHUNK);
		
	}

	@Override
	public void createPartControl(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ToolBar toolbar = new ToolBar(main, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		createRuntimeEnvironment(main);
		
		init = new ToolItem(toolbar, SWT.PUSH);
		init.setToolTipText(Messages.RuntimeConsoleViewPart_1);
		init.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
        		IEditorInput in = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
        		if (in instanceof GatewayEditorInput){
        			setInput(((GatewayEditorInput)in).getDocumentGateway());
        		}
//				helper.initEngine(getRuntimeConfig());
					
				
			}
			
		});
		init.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.prepare_16));
		
		start = new ToolItem(toolbar, SWT.PUSH);
		start.setToolTipText(Messages.RuntimeConsoleViewPart_2);
		start.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.start_16));
		start.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (!parametersAreSet()){
					MessageDialog.openInformation(getSite().getShell(), Messages.RuntimeConsoleViewPart_3, Messages.RuntimeConsoleViewPart_4);
				}
				
				try {
					helper.startEngine(getRuntimeConfig());

				} catch (GatewayRuntimeException e1) {
					e1.printStackTrace();
					MessageDialog.openInformation(getSite().getShell(), Messages.RuntimeConsoleViewPart_5, e1.getMessage());
				}
			}
			
		});
		
		
		stop = new ToolItem(toolbar, SWT.PUSH);
		stop.setToolTipText(Messages.RuntimeConsoleViewPart_6);
		stop.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.stop_16));
		stop.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					helper.stopEngine(getRuntimeConfig());
				} catch (GatewayRuntimeException e1) {
					e1.printStackTrace();
					MessageDialog.openInformation(getSite().getShell(), Messages.RuntimeConsoleViewPart_7, e1.getMessage());
				}
			}
		});

		
	}
	
	
	
	private void createRuntimeEnvironment(Composite parent){
		Group g = new Group(parent, SWT.NONE);
		g.setLayout(new GridLayout(2, false));
		g.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		g.setText(Messages.RuntimeConsoleViewPart_8);
		
		bLocal = new Button(g, SWT.RADIO);
		bLocal.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		bLocal.setText(Messages.RuntimeConsoleViewPart_9);
		bLocal.setSelection(true);
				
		bRemote = new Button(g, SWT.RADIO);
		bRemote.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		bRemote.setText(Messages.RuntimeConsoleViewPart_10);
		
		
//		Label l = new Label(g, SWT.NONE);
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l.setText(Messages.RuntimeConsoleViewPart_11);
//		
//		runServer = new Combo(g, SWT.NONE);
//		runServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
//		runServer.setEnabled(false);
//		
//		
//		
//		//load Servers
//		List<String> serverRun = new ArrayList<String>();
//		for( Server s : ResourceManager.getInstance().getServers(GatewayServer.class)){
//			serverRun.add(s.getName());
//		}
//		runServer.setItems(serverRun.toArray(new String[serverRun.size()]));
		
		
		bLocal.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (bLocal.getSelection()){
//					runServer.setEnabled(false);
					start.setEnabled(true);
				}
				else{
//					runServer.setEnabled(true);
					start.setEnabled(false);
				}
			}
			
		});

		
		bRemote.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (bLocal.getSelection()){
//					runServer.setEnabled(false);
					start.setEnabled(false);
				}
				else{
//					runServer.setEnabled(true);
					start.setEnabled(true);
				}
			}
			
		});

		
		
	}
	
	
	private void createFolder(Composite container){
		if (folder != null && !folder.isDisposed()){
			folder.dispose();
		}
		
		folder = new TabFolder(container, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		TabItem runtime = new TabItem(folder, SWT.NONE);
		runtime.setText(Messages.RuntimeConsoleViewPart_12);
		runtime.setControl(createRuntimePanel(folder));
		
		
		TabItem parameters = new TabItem(folder, SWT.NONE);
		parameters.setText(Messages.RuntimeConsoleViewPart_13);
		
		
		parameters.setControl(createParameterPanel(folder));
	}
	
	
	private Control createRuntimePanel(Composite container){
		Composite c = new Composite(container, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.RuntimeConsoleViewPart_14);
		
		engineState = new Text(c, SWT.BORDER);
		engineState.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		engineState.setEditable(false);
		engineState.setText(Activator.localRuntimeEngine.getState());
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RuntimeConsoleViewPart_15);
		
		logLevel = new Combo(c, SWT.READ_ONLY);
		logLevel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		logLevel.setItems(new String[]{RuntimeAppender.DEBUG, RuntimeAppender.INFO, RuntimeAppender.WARN, RuntimeAppender.ERROR});
		logLevel.select(1);
		logLevel.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				currentLevel = logLevel.getText();
			}
			
		});
		
		
		
		Label l4 = new Label(c, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.RuntimeConsoleViewPart_16);
		
		bufferSize = new Text(c, SWT.BORDER);
		bufferSize.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bufferSize.setText(Activator.localRuntimeEngine.MAX_ROWS + ""); //$NON-NLS-1$
		bufferSize.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				try{
					int i = Integer.parseInt(bufferSize.getText());
					Activator.localRuntimeEngine.MAX_ROWS = i;
				}catch(NumberFormatException ex){
					bufferSize.setText(Activator.localRuntimeEngine.MAX_ROWS + ""); //$NON-NLS-1$
				}
				
			}
			
		});
		
		
		
		tableViewer = new TableViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
				
		
		TableColumn transfoName = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		transfoName.setText(Messages.RuntimeConsoleViewPart_19);
		transfoName.setWidth(100);
		
		TableColumn transfoState = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		transfoState.setText(Messages.RuntimeConsoleViewPart_20);
		transfoState.setWidth(100);
		
		TableColumn transfoStartTime = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		transfoStartTime.setText(Messages.RuntimeConsoleViewPart_21);
		transfoStartTime.setWidth(100);
		
		
		TableColumn transfoStopTime = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		transfoStopTime.setText(Messages.RuntimeConsoleViewPart_22);
		transfoStopTime.setWidth(100);
		
		
		TableColumn readedRows = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		readedRows.setText(Messages.RuntimeConsoleViewPart_23);
		readedRows.setWidth(100);
		
		TableColumn bufferedRows= new TableColumn(tableViewer.getTable(), SWT.LEFT);
		bufferedRows.setText(Messages.RuntimeConsoleViewPart_24);
		bufferedRows.setWidth(100);
		
		TableColumn processedRows = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		processedRows.setText(Messages.RuntimeConsoleViewPart_25);
		processedRows.setWidth(100);
		
		TableColumn transfoTime = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		transfoTime.setText(Messages.RuntimeConsoleViewPart_26);
		transfoTime.setWidth(100);
		
		TableColumn log = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		log.setText(Messages.RuntimeConsoleViewPart_27);
		log.setWidth(200);

		
		tableViewer.getTable().setHeaderVisible(true);

		return c;
	}
	

	
	private Control createParameterPanel(Composite container){
		Composite c = new Composite(container, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		params.clear();
		
		if (gatewayModel.getParameters().size() == 0){
			Label l = new Label(c, SWT.NONE);
			l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			l.setText(Messages.RuntimeConsoleViewPart_28);
			return c;
		}
		
		
		for(Parameter p : gatewayModel.getParameters()){
			Label l = new Label(c, SWT.NONE);
			l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			l.setText(p.getName() + "(" + Parameter.PARAMETER_TYPES[p.getType()] + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			
			
			
			final Text t = new Text(c, SWT.BORDER);
			t.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			t.setText(p.getValueAsString());
			
			params.put(t, p);
			
			t.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					params.get(t).setValue(t.getText());
					
				}
				
			});
			

		}
		
		Button checkParameters = new Button(c, SWT.PUSH);
		checkParameters.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false, 2, 1));
		checkParameters.setText(Messages.RuntimeConsoleViewPart_31);
		checkParameters.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				StringBuffer errors = new StringBuffer();
				for(Text t : params.keySet()){

					try {
						if ( params.get(t).getValue() == null){
							errors.append(Messages.RuntimeConsoleViewPart_32 + params.get(t) + Messages.RuntimeConsoleViewPart_33  + "\r\n"); //$NON-NLS-1$
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						errors.append(Messages.RuntimeConsoleViewPart_35 + params.get(t) + ": " + e1.getMessage() + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				
				if (errors.toString().length() > 0){
					MessageDialog.openError(getSite().getShell(), Messages.RuntimeConsoleViewPart_38, Messages.RuntimeConsoleViewPart_39 + errors.toString());
				}else{
					MessageDialog.openInformation(getSite().getShell(),Messages.RuntimeConsoleViewPart_40 , Messages.RuntimeConsoleViewPart_41);
				}
			}
			
		});
		
		
		
		return c;

	}
	
	
	
	private boolean parametersAreSet(){
		for(Text t : params.keySet()){

			try {
				if ( params.get(t).getValue() == null){
					return false;
				}
			} catch (Exception e1) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void setFocus() {
		
		IEditorPart p = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (p == null){
			bRemote.setEnabled(false);
			bLocal.setEnabled(false);
			start.setEnabled(false);
			stop.setEnabled(false);
			init.setEnabled(false);
			return;
		}
		else{
			init.setEnabled(true);
			bLocal.setEnabled(true);
			start.setEnabled(true);
		}
		
		
		GatewayEditorInput in = (GatewayEditorInput) p.getEditorInput();
		
		if (in.getDirectoryItem() == null){
			bRemote.setEnabled(false);
			bRemote.setSelection(false);
			bLocal.setSelection(true);
//			runServer.setEnabled(false);
			
		}
		else{
			bRemote.setEnabled(true);
			if (bRemote.getSelection()){
//				runServer.setEnabled(true);
			}
		}
		
		
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (tableViewer == null || tableViewer.getControl().isDisposed()){
			return;
		}
		final PropertyChangeEvent evnt = evt;
		Display d = Display.getDefault();
		
		d.asyncExec(new Runnable(){
			public void run(){
				
				
				tableViewer.refresh();
				if (evnt.getPropertyName().equals(RuntimeAppender.ENGINE_STATE)){
					
					if (bLocal.getSelection()){
						engineState.setText(Activator.localRuntimeEngine.getState());
						
						if (Activator.localRuntimeEngine.isRunning()){
							start.setEnabled(false);
							stop.setEnabled(true);
							init.setEnabled(false);
						}
						else if (Activator.localRuntimeEngine.isReady()){
							start.setEnabled(true);
							stop.setEnabled(false);
							init.setEnabled(true);
						}
						else if (Activator.localRuntimeEngine.isAvailable()){
							start.setEnabled(false);
							stop.setEnabled(false);
							init.setEnabled(true);
						}
					}
					else{
						engineState.setText((String)evnt.getNewValue());
						if (engineState.getText().equals("preparing")){ //$NON-NLS-1$
							start.setEnabled(false);
							stop.setEnabled(false);
							init.setEnabled(false);
						}
						else if (engineState.getText().equals("running")){ //$NON-NLS-1$
							start.setEnabled(false);
							stop.setEnabled(true);
							init.setEnabled(false);
						} 
						else if (engineState.getText().equals("stopped") ||  //$NON-NLS-1$
								engineState.getText().equals("Ended") || //$NON-NLS-1$
								engineState.getText().equals("Interrupted By Error")){ //$NON-NLS-1$
							start.setEnabled(false);
							stop.setEnabled(false);
							init.setEnabled(true);
						}
						
					}
					
					
					
				}
				else{
				
					
				}
				
				
			}
		});
		
		
	}

	
	public void setInput(DocumentGateway gatewayModel){
		this.gatewayModel = gatewayModel;
		createFolder(main);
		helper.initEngine(getRuntimeConfig());
		main.layout();
	}
	
	
	protected RuntimeConfig getRuntimeConfig(){
		RuntimeConfig cfg = new RuntimeConfig();
		
		Integer lvl = null;
		String l = logLevel.getText();
		
		
		if (l.equals(RuntimeAppender.DEBUG)){
			lvl = Level.DEBUG_INT;
		}else if (l.equals(RuntimeAppender.INFO)){
			lvl = Level.INFO_INT;
		}else if (l.equals(RuntimeAppender.WARN)){
			lvl = Level.WARN_INT;
		}else if (l.equals(RuntimeAppender.ERROR)){
			lvl = Level.ERROR_INT;
		}
		cfg.setLogLevel(lvl);
		
		
		if (bLocal.getSelection()){
			cfg.setType(RuntimeConfig.LOCAL_RUN);
		}
		else{
			cfg.setType(RuntimeConfig.REMOTE_RUN);
		}
		
		
		Properties prms = new Properties();
		for(Text t : params.keySet()){
			prms.setProperty(params.get(t).getName(), t.getText());
		}
		
		cfg.setParameters(prms);
		
		return cfg;
	}
}
