package bpm.vanilla.map.design.ui.wizard.map;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.IBuildingFloor;
import bpm.vanilla.map.core.design.ICell;
import bpm.vanilla.map.design.ui.dialogs.DialogCell;
import bpm.vanilla.map.design.ui.dialogs.DialogFloor;
import bpm.vanilla.map.design.ui.viewers.TreeFloorCellContentProvider;
import bpm.vanilla.map.design.ui.viewers.TreeFloorCellLabelProvider;
import bpm.vanilla.platform.core.IRepositoryApi;

public class FloorCellPage extends WizardPage  {
	
	private IBuilding building;
	private List<IBuildingFloor> floors;
	
	private TreeViewer tableFloor;
	
	private IRepositoryApi sock;
	private int repositoryId;
	
	private Button addFloor;
	private Button addCell;
	
	protected FloorCellPage(String pageName){
		super(pageName);		
	}
	
	protected FloorCellPage(String pageName, IBuilding building, IRepositoryApi sock, int repositoryId) {
		super(pageName);
		this.building = building;
		this.sock = sock;
		this.repositoryId = repositoryId;
		floors = building.getFloors();
		if(floors == null){
			floors = new ArrayList<IBuildingFloor>();
		}
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		tableFloor = new TreeViewer(main);
		tableFloor.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		tableFloor.setContentProvider(new TreeFloorCellContentProvider());
		tableFloor.setLabelProvider(new TreeFloorCellLabelProvider());
		
		addFloor = new Button(main, SWT.PUSH);
		addFloor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		addFloor.setText(Messages.FloorCellPage_0);
		addFloor.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IBuildingFloor floor = null;
				try {
					floor = Activator.getDefault().getFactoryMap().createFloor();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
				floor.setLabel(""); //$NON-NLS-1$
				DialogFloor dialFloor = new DialogFloor(getShell(), building, floor, sock, repositoryId, false);
			
				if (dialFloor.open() == Dialog.OK) {
					refreshTree();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		addCell = new Button(main, SWT.PUSH);
		addCell.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		addCell.setText(Messages.FloorCellPage_2);
		addCell.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)tableFloor.getSelection();
				
				if (ss.isEmpty() && !(ss instanceof IBuildingFloor)){
					return; 
				}
				
				IBuildingFloor floor = (IBuildingFloor)ss.getFirstElement();
				ICell cell = null;
				try {
					cell = Activator.getDefault().getFactoryMap().createCell();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
				cell.setLabel(""); //$NON-NLS-1$
				DialogCell dialCell = new DialogCell(getShell(), building, floor, cell, sock, repositoryId, false);
				
				if (dialCell.open() == Dialog.OK) {
					refreshTree();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		tableFloor.setInput(building.getFloors());
		
		// page setting
		setControl(main);
		setPageComplete(false);
	}
	
	protected void refreshTree(){
		//We set the input for the tree
		tableFloor.setInput(building.getFloors());
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}
}
