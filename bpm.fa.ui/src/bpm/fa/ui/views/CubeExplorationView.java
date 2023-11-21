package bpm.fa.ui.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPStructure;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.fa.api.utils.parse.DigesterCubeView;
import bpm.fa.ui.Messages;
import bpm.fa.ui.composite.CompositeExplore;
import bpm.fa.ui.dialogs.DialogContext;
import bpm.fa.ui.dialogs.DialogOpen;
import bpm.fa.ui.dialogs.DialogViewParameter;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.repository.ui.Activator;

public class CubeExplorationView extends ViewPart{

	public static final String ID = CubeExplorationView.class.getName();

	private CompositeExplore compositeExplore;
	private UnitedOlapLoader loader = UnitedOlapLoaderFactory.getLoader();
//	private IRuntimeContext runtimeContext = new RuntimeContext("system", "system", "System", 1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private IRuntimeContext runtimeContext; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	private OLAPStructure faStructure;
	
	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
//		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Button test = new Button(main, SWT.PUSH);
		test.setText(Messages.CubeExplorationView_3);
		test.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		test.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (runtimeContext == null){
					DialogContext dial = new DialogContext(getSite().getShell());
					if (dial.open() == DialogContext.OK){
						runtimeContext = dial.getContext();
					}
					//TODO
//					IRepositoryApi sock = FreemetricsPlugin.getDefault().getRepositoryConnection();
//					IVanillaAPI api = FreemetricsPlugin.getDefault().getVanillaApi();
//					
//					ctx = new RuntimeContext(sock.getContext().getVanillaContext().getLogin(), sock.getContext().getVanillaContext().getPassword(), 
//							api.getVanillaSecurityManager().getGroupById(sock.getContext().getGroup().getId()).getName(), sock.getContext().getGroup().getId());
				}
				
				final DialogOpen dial = new DialogOpen(getSite().getShell(), loader, runtimeContext);
				if (dial.open() == DialogOpen.OK){
					BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
						
						@Override
						public void run() {
							try{
								faStructure = dial.getCube();
								OLAPCube olapCube = faStructure.createCube(runtimeContext);
								
								if (dial.getCubeView() != null){
									
									
									/*
									 * loading the CubeView
									 */
									String xml = null;
									try{
										xml = Activator.getDefault().getDesignerActivator().getRepositoryConnection().getRepositoryService().loadModel(dial.getCubeView());
									}catch(Exception ex){
										ex.printStackTrace();
										MessageDialog.openWarning(getSite().getShell(), Messages.CubeExplorationView_4, Messages.CubeExplorationView_5 + ex.getMessage());
									}
									
									DigesterCubeView dig = null;
									try{
										dig = new DigesterCubeView(xml);
									}catch(Throwable ex){
										ex.printStackTrace();
										MessageDialog.openWarning(getSite().getShell(), Messages.CubeExplorationView_6, Messages.CubeExplorationView_7 + ex.getMessage());
									}
									
									if (dig != null){
										
										
										if (!dig.getCubeView().getParameters().isEmpty()){
											DialogViewParameter dial = new DialogViewParameter(getSite().getShell(), olapCube, dig.getCubeView());
											dial.open();
										}
									}
									
									olapCube.setView(dig.getCubeView());
								}
								
								
								compositeExplore.loadKTable(runtimeContext, olapCube);
							}catch(Exception ex){
								ex.printStackTrace();
								MessageDialog.openError(getSite().getShell(), Messages.CubeExplorationView_8, Messages.CubeExplorationView_9 + ex.getMessage());
							}
							
						}
					});
					
				}
				
				
			}
		});
		
		Button context = new Button(main, SWT.PUSH);
		context.setText(Messages.CubeExplorationView_10);
		context.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		context.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				DialogContext d = new DialogContext(getSite().getShell());
				if (d.open() == DialogContext.OK){
					runtimeContext = d.getContext();
					
					try{
						OLAPCube c = faStructure.createCube(runtimeContext);
						compositeExplore.loadKTable(runtimeContext, c);
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.CubeExplorationView_11, Messages.CubeExplorationView_12);
					}
					
				}
				
			}
		});
		
		compositeExplore = new CompositeExplore(main, SWT.NONE);
		compositeExplore.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
	}

	@Override
	public void setFocus() {
		
		
	}

}
