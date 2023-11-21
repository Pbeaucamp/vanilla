package bpm.gateway.ui.views;

import java.beans.PropertyChangeEvent;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.runtime2.GatewayRuntimeException;
import bpm.gateway.runtime2.tools.RuntimeAppender;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.composites.labelproviders.RemoteTransformationRuntimeContentProvider;
import bpm.gateway.ui.composites.labelproviders.RemoteTransformationRuntimeLabelProvider;
import bpm.gateway.ui.composites.labelproviders.StepRuntimeContentProvider;
import bpm.gateway.ui.composites.labelproviders.StepRuntimeLabelProvider;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState.StepInfos;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunIdentifier;

public class RuntimeViewHelper {
	
	private RuntimeConsoleViewPart viewPart;
	private IRunIdentifier runtimeInstanceId = null;
	private boolean isOn = false;
	private Thread refresher;
	public RuntimeViewHelper(RuntimeConsoleViewPart part){
		this.viewPart = part;
	}
	
	private void initUI(IContentProvider contentProvider, ITableLabelProvider labelProvider){
		viewPart.tableViewer.setContentProvider(contentProvider);
		viewPart.tableViewer.setLabelProvider(labelProvider);
	}
	
	public void initEngine(final RuntimeConfig config){
		
		GatewayEditorPart editor = (GatewayEditorPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		GatewayEditorInput editorInput = (GatewayEditorInput)editor.getEditorInput(); 
		DocumentGateway doc = (editorInput).getDocumentGateway();

		
		if (config.getType() == RuntimeConfig.LOCAL_RUN){
			try {
				runtimeInstanceId = null;
				try{
					viewPart.tableViewer.setInput(null);
				}catch(Exception e){
					
				}
				initUI(new StepRuntimeContentProvider(), new StepRuntimeLabelProvider(new LabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
				
				OutputStream os = null;
				for(IConsole c : ConsolePlugin.getDefault().getConsoleManager().getConsoles()){
					if (c instanceof ConsoleRuntime){
						os = ((ConsoleRuntime)c).getOutputStream();
					}
				}
				Activator.localRuntimeEngine.init(Activator.getDefault().getRepositoryContext(), doc, viewPart, os);
				
				viewPart.tableViewer.setInput(Activator.localRuntimeEngine.getAllRuns());

				//initParameter
				for(Parameter p : doc.getParameters()){
					for(Object t : config.getParameters().keySet()){
						if (((String)t).equals(p.getName())){
							p.setValue(config.getParameters().getProperty((String)t));
						}
					}
					
				}
				
			} catch (GatewayRuntimeException ex) {
				ex.printStackTrace();
				MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.RuntimeViewHelper_0, ex.getMessage());
			}
			
			
			
		}
		else{
			initUI(new RemoteTransformationRuntimeContentProvider(), new RemoteTransformationRuntimeLabelProvider(new LabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
			
			IRepositoryApi sock = Activator.getDefault().getRepositoryConnection();
			
			try {
				IRepositoryContext ctx = Activator.getDefault().getRepositoryContext();
				
				final RemoteGatewayComponent srv = new RemoteGatewayComponent(ctx.getVanillaContext().getVanillaUrl(), 
						ctx.getVanillaContext().getLogin(), 
						ctx.getVanillaContext().getPassword()); //$NON-NLS-1$

				
				
				List<VanillaGroupParameter> params = new ArrayList<VanillaGroupParameter>();
				
				if (config.getParameters() != null){
					
					for(Object s : config.getParameters().keySet()){
						VanillaGroupParameter g = new VanillaGroupParameter();
						VanillaParameter p = new VanillaParameter();
						p.setName(s.toString());
						p.setSelectedValues(new ArrayList<String>());
						p.getSelectedValues().add(config.getParameters().getProperty(s.toString()));
						g.addParameter(p);
						params.add(g);
					}
				}
				
				User user = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserByLogin(Activator.getDefault().getVanillaContext().getLogin());
				
				GatewayRuntimeConfiguration runConf = new GatewayRuntimeConfiguration(
						new ObjectIdentifier(
								Activator.getDefault().getRepositoryContext().getRepository().getId(), 
								Activator.getDefault().getCurrentInput().getDirectoryItem().getId()),
						params,
						1
								);
				runConf.setParameters(params);
				runtimeInstanceId = srv.runGatewayAsynch(runConf, user);
				
				Thread th = new Thread(){
					GatewayRuntimeState info = null;
					public void run(){
						try {
							Thread waiter = new Thread(){
								private boolean queuing = true;
								public void run(){
									
									Display.getDefault().asyncExec(new Runnable(){
										public void run(){
											viewPart.tableViewer.setInput(new ArrayList<GatewayStepLog>());
										}
									});
									
									String engineState = "preparing"; //$NON-NLS-1$
									viewPart.propertyChange(new PropertyChangeEvent(RuntimeViewHelper.this, RuntimeAppender.ENGINE_STATE, engineState, engineState));
									
									while(queuing){
										
										try {
											info = srv.getRunState(new SimpleRunIdentifier(runtimeInstanceId.getKey()));
											queuing = info.getState() == ActivityState.WAITING;
											PropertyChangeEvent evt = new PropertyChangeEvent(RuntimeViewHelper.this, RuntimeAppender.ENGINE_STATE, engineState, "queuing"); //$NON-NLS-1$
											viewPart.propertyChange(evt);
										} catch (Exception e) {
											
										}
										
										
										try {
											sleep(10);
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
									}
									
								}
							};
							

							waiter.start();
							try{
								waiter.join();
							}catch(Exception e){
								
							}
							try {
								sleep(2000);
							} catch (InterruptedException e) {
								
							}
							
							viewPart.propertyChange(new PropertyChangeEvent(RuntimeViewHelper.this, RuntimeAppender.ENGINE_STATE, "running", "running")); //$NON-NLS-1$ //$NON-NLS-2$
							do{
								
								try{
									try {
										info = srv.getRunState(new SimpleRunIdentifier(runtimeInstanceId.getKey()));
									} catch (Exception e) {
										e.printStackTrace();
										break;
									}
									
													
									
									for(StepInfos stepInfo : info.getStepsInfo()){
										GatewayStepLog l = null;
										for(GatewayStepLog _l : (List<GatewayStepLog>)viewPart.tableViewer.getInput()){
											if (_l.getStepName().equals(stepInfo.getStepName())){ //$NON-NLS-1$
												l = _l;
												break;
											}
										}
										if (l == null){
											l = new GatewayStepLog();
											l.setStepName(stepInfo.getStepName()); //$NON-NLS-1$
										}
										l.setBuffered(stepInfo.getBufferedRows() + ""); //$NON-NLS-1$
										l.setDuration(stepInfo.getDuration() + ""); //$NON-NLS-1$
										l.setProcessed(stepInfo.getProcessedRows() + ""); //$NON-NLS-1$
										l.setReaded(stepInfo.getReadRows() + ""); //$NON-NLS-1$
										l.setStart(stepInfo.getStartTime() + ""); //$NON-NLS-1$
										l.setState(stepInfo.getState() + ""); //$NON-NLS-1$
										
										l.setStop(stepInfo.getStopTime() + ""); //$NON-NLS-1$
										
										String ll = stepInfo.getLogs(); //$NON-NLS-1$
										String[] y = ll.split("\\{"); //$NON-NLS-1$
										
										for(String s : y){
											if (s.contains(":")){ //$NON-NLS-1$
												String lvl = s.substring(0, s.indexOf(":")); //$NON-NLS-1$
												String m = s.substring(lvl.length()  +1).replace(l.getStepName() + " : ", ""); //$NON-NLS-1$ //$NON-NLS-2$
												
												boolean messageAlreadyExists = false;
												for(String a : l.getMessage()){
													if (a.equals(m)){
														messageAlreadyExists = true;
														break;
													}
												}
												
												if (!messageAlreadyExists){
													l.addMessage(Level.toLevel(lvl).toInt(), m);
												}
												
											}
										}
										if (!((List<GatewayStepLog>)viewPart.tableViewer.getInput()).contains(l)){
											((List<GatewayStepLog>)viewPart.tableViewer.getInput()).add(l);
										}
										
									}

									
									Display.getDefault().asyncExec(new Runnable(){
										public void run(){
											
											viewPart.tableViewer.refresh();
										}
									});
								}catch(Exception e){
									e.printStackTrace();
								}
								
								
								try {
									sleep(2000);
								} catch (InterruptedException e) {
									
								}
							}while(info.getState() != ActivityState.ENDED);
							
							viewPart.propertyChange(new PropertyChangeEvent(RuntimeViewHelper.this, RuntimeAppender.ENGINE_STATE, "Ended", "Ended")); //$NON-NLS-1$ //$NON-NLS-2$
						}catch(Exception ex){
							ex.printStackTrace();
							final String s = ex.getMessage();
							viewPart.propertyChange(new PropertyChangeEvent(RuntimeViewHelper.this, RuntimeAppender.ENGINE_STATE, "Interrupted By Error", "Interrupted By Error")); //$NON-NLS-1$ //$NON-NLS-2$
							Display.getDefault().asyncExec(new Runnable(){
								public void run(){
									MessageDialog.openError(viewPart.getSite().getShell(), Messages.RuntimeViewHelper_43, s);
								}
							});
						}
						
					}
				};
				
				th.start();
				
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(viewPart.getSite().getShell(), Messages.RuntimeViewHelper_44, e.getMessage());
			}
			
			
			
		}
	}
	
	public void startEngine(RuntimeConfig config) throws GatewayRuntimeException{
		if (config.getType() == RuntimeConfig.LOCAL_RUN){
			refresher = new Thread(){
				public void run(){
					while(isOn){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Display.getDefault().asyncExec(new Runnable(){
							public void run(){
								if (viewPart != null && viewPart.tableViewer != null && !viewPart.tableViewer.getControl().isDisposed()){
									viewPart.tableViewer.refresh();
								}
								
							}
						});
					}
					
				}
			};
			isOn = true;
			refresher.start();
			Activator.localRuntimeEngine.run(config.getLogLevel());
			
			
		}
	}
	
	
	public void stopEngine(RuntimeConfig config) throws GatewayRuntimeException{
		if (config.getType() == RuntimeConfig.LOCAL_RUN){
			
			Activator.localRuntimeEngine.interrupt();
			isOn = false;
			try {
				refresher.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
