package bpm.fa.ui.composite;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.query.MissingLastTimeDimensionException;
import bpm.fa.api.olap.query.WhereClauseException;
import bpm.fa.ui.Messages;
import bpm.fa.ui.composite.viewers.DimensionContentProvider;
import bpm.fa.ui.composite.viewers.StructureLabelProvider;
import bpm.fa.ui.ktable.CubeExplorerContent;
import bpm.fa.ui.ktable.CubeModel;
import bpm.fa.ui.ktable.KTableCellDoubleClickDrillAdapter;
import bpm.fa.ui.ktable.KTableMenuProvider;
import bpm.united.olap.api.runtime.IRuntimeContext;
import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellDoubleClickListener;
import de.kupzog.ktable.SWTX;


public class CompositeExplore extends Composite{
	private Point drag;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private TreeViewer dimensionViewer;
	private TreeViewer measureViewer;
	
//	private TableViewer  measureViewer;
	private KTable table;
	private KTableCellDoubleClickListener doubleClickDrillListener;
	
	private OLAPCube olapCube;
	
	private TableViewer whereViewer;
	
	private List<String> whereClause = new ArrayList<String>();
	
	private CompositeFilter compositeFilter;
	
	private ExplorationToolbarManager toolBarManager;
	
	public CompositeExplore(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);

		
		Composite composite_1 = formToolkit.createComposite(sashForm, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));

		
		
		
		
		
		/*
		 * Dimension Section		
		 */
		Section dimSection = formToolkit.createSection(composite_1, Section.TWISTIE | Section.TITLE_BAR);
		dimSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.paintBordersFor(dimSection);
		dimSection.setText(Messages.CompositeExplore_0);
		
		Composite queryComposite = formToolkit.createComposite(dimSection);
		queryComposite.setLayout(new GridLayout());
		dimSection.setClient(queryComposite);


		dimensionViewer = new TreeViewer(queryComposite, SWT.BORDER);
		dimensionViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dimensionViewer.setLabelProvider(new StructureLabelProvider());
		dimensionViewer.setContentProvider(new DimensionContentProvider(DimensionContentProvider.TYPE_ONLY_DIMENSION));
		
		
		
		/*
		 *	Measurses 
		 */
		Section mesSection = formToolkit.createSection(composite_1, Section.TWISTIE | Section.TITLE_BAR);
		mesSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.paintBordersFor(dimSection);
		mesSection.setText(Messages.CompositeExplore_1);
		

		measureViewer = new TreeViewer(mesSection, SWT.BORDER);
		measureViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		measureViewer.setLabelProvider(new StructureLabelProvider());
		measureViewer.setContentProvider(new DimensionContentProvider(DimensionContentProvider.TYPE_ONLY_MEASURE));

		mesSection.setClient(measureViewer.getControl());

		
		
		/*
		 * Filters
		 */
		Section filterSection = formToolkit.createSection(composite_1, Section.TWISTIE | Section.TITLE_BAR);
		formToolkit.paintBordersFor(filterSection);
		filterSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		filterSection.setText(Messages.CompositeExplore_2);
		
		
		compositeFilter = new CompositeFilter();
		filterSection.setClient(compositeFilter.createContent(filterSection));
		
		/*
		 * KTable
		 */
		Composite tableComposite = formToolkit.createComposite(sashForm, SWT.NONE);
		tableComposite.setLayout(new GridLayout(1, false));
		
		toolBarManager = new ExplorationToolbarManager();
		Control tb = toolBarManager.createToolbar(tableComposite);
		tb.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		tb.setBackground(formToolkit.getColors().getBackground());
		
		
		
		
		table = new KTable(tableComposite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);// | SWTX.AUTO_SCROLL);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setBackground(formToolkit.getColors().getBackground());
		toolBarManager.init(table);
		
		/*
		 * Where clause
		 */
		Section sctnNewSection = formToolkit.createSection(tableComposite, Section.TWISTIE | Section.TITLE_BAR);
		sctnNewSection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.paintBordersFor(sctnNewSection);
		sctnNewSection.setText(Messages.CompositeExplore_3);
		try {
			sctnNewSection.setExpanded(true);
		} catch (Exception e) {
			
		}
		
		whereViewer = new TableViewer(sctnNewSection, SWT.V_SCROLL | SWT.H_SCROLL |SWT.BORDER | SWT.FULL_SELECTION  | SWT.MULTI);
		Table table_1 = whereViewer.getTable();
		
		formToolkit.paintBordersFor(table_1);
		sctnNewSection.setClient(table_1);
		whereViewer.setContentProvider(new ArrayContentProvider());
		whereViewer.setLabelProvider(new LabelProvider());
		whereViewer.setInput(whereClause);
		
		createWhereMenu();
		
		
		setListener();
		setDnd();
		sashForm.setWeights(new int[] {1, 4});
		KTableMenuProvider menuProvider = new KTableMenuProvider(table);
		menuProvider.createMenu();
		
	}
	
	
	public void setListener(){
		doubleClickDrillListener = new KTableCellDoubleClickDrillAdapter(){

			@Override
			public void fixedCellDoubleClicked(final int col, final int row, int statemask) {
				
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					
					@Override
					public void run() {
						CubeModel model = (CubeModel) table.getModel();
						
						List<List<CubeExplorerContent>> content = model.getContent();
						
						// implement the sorting when clicking on the header.
						if (content.get(row).get(col).getItem() instanceof ItemElement){
							//avoid drilling on measure
							if (((ItemElement)content.get(row).get(col).getItem()).getDataMember().getUniqueName().startsWith(("[Measure"))){ //$NON-NLS-1$
								MessageDialog.openInformation(getShell(), Messages.CompositeExplore_5, Messages.CompositeExplore_6);
								return;
							}
							
							
							if(!((ItemElement)content.get(row).get(col).getItem()).isDrilled()){
								OLAPMember m = ((ItemElement)content.get(row).get(col).getItem()).getDataMember();
//								if (m.getHiera().isParentChild()){
//									return;
//								}
								if (m.getLevel() != null && m.getLevel().getNextLevel() == null && !m.getHiera().isParentChild()){
									Logger.getLogger(getClass()).debug(Messages.CompositeExplore_7 + ((ItemElement)content.get(row).get(col).getItem()).getDataMember().getUniqueName() + Messages.CompositeExplore_8);
									return;
								}
								
								//TODO : chzck if the Hierarchy is parentChild before drillingDown
								model.getOLAPCube().drilldown((ItemElement)content.get(row).get(col).getItem());
								model.performQuery();
							
								table.redraw();
								table.getParent().getParent().layout(true);
								
							}
							else{
								model.getOLAPCube().drillup((ItemElement)content.get(row).get(col).getItem());
								model.performQuery();
								table.redraw();
								table.getParent().getParent().layout(true);
							}
						}
						table.redraw();
						table.getParent().getParent().layout(true);
					}
				});
				
						
			}
			

		};
	}
	
	public void loadKTable(IRuntimeContext runtimeContext, OLAPCube faCube){
		
		table.removeDoubleClickListener(doubleClickDrillListener);
		this.olapCube = faCube;
		
		
		whereClause.clear();
		for(Parameter p : faCube.getParameters()){
			whereClause.add(p.getUname());
		}
		whereViewer.refresh();
		table.setModel(new CubeModel(faCube));
		dimensionViewer.setInput(faCube);
		measureViewer.setInput(faCube);
		table.addCellDoubleClickListener(doubleClickDrillListener);

		compositeFilter.init(table, olapCube, (CubeModel)table.getModel());
		
		toolBarManager.initToolBar();
	}
	
	public CubeModel getModel(){
		return (CubeModel) table.getModel();
	}
	
	private void setDnd(){
		final Transfer[] transferts = new Transfer[]{TextTransfer.getInstance()};
		dimensionViewer.addDragSupport(DND.DROP_COPY, transferts, new DragSourceListener() {
			
			@Override
			public void dragStart(DragSourceEvent event) {}
			
			@Override
			public void dragSetData(DragSourceEvent event) {
				Object dragedObject = ((IStructuredSelection)dimensionViewer.getSelection()).getFirstElement();
				
				//get Item Uname
				try{
					String uniqueName = ItemElementHelper.getItemUniqueName(olapCube, dragedObject);
					
					if (olapCube.getMdx().canMemberBeAdded(uniqueName)){
						event.data = uniqueName;
						event.doit = true;
					}
					else{
						event.doit = false;
					}
				}catch (Exception e) {
					e.printStackTrace();
					event.doit = false;
				}
			}
			
			@Override
			public void dragFinished(DragSourceEvent event) {}
		});
		
		
		
		measureViewer.addDragSupport(DND.DROP_COPY, transferts, new DragSourceListener() {
			
			@Override
			public void dragStart(DragSourceEvent event) {}
			
			@Override
			public void dragSetData(DragSourceEvent event) {
				Object dragedObject = ((IStructuredSelection)measureViewer.getSelection()).getFirstElement();
				
				//get Item Uname
				try{
					String uniqueName = ItemElementHelper.getItemUniqueName(olapCube, dragedObject);
					
					if (olapCube.getMdx().canMemberBeAdded(uniqueName)){
						event.data = uniqueName;
						event.doit = true;
					}
					else{
						event.doit = false;
					}
				}catch (Exception e) {
					e.printStackTrace();
					event.doit = false;
				}
			}
			
			@Override
			public void dragFinished(DragSourceEvent event) {}
		});
		
		
				
		DropTarget targetTable = new DropTarget(table, DND.DROP_COPY | DND.DROP_MOVE);
		targetTable.setTransfer(transferts);
		targetTable.addDropListener(new DropTargetListener() {
			
			@Override
			public void dropAccept(DropTargetEvent event) {
				
				
			}
			
			@Override
			public void drop(final DropTargetEvent event) {
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					
					@Override
					public void run() {
						boolean before = (event.feedback & DND.FEEDBACK_INSERT_BEFORE) != 0;
						boolean after = (event.feedback & DND.FEEDBACK_INSERT_AFTER) != 0;
						String uname = (String)event.data;
						
						org.eclipse.swt.graphics.Point pLocal = table.toControl(event.x, event.y);
						org.eclipse.swt.graphics.Point  p = table.getCellForCoordinates(pLocal.x, pLocal.y);
						CubeExplorerContent o = ((CubeModel)table.getModel()).getModelItemAt(p);
						
						ItemElement e = null;
						OLAPMember m = null;
						try{
							m = olapCube.findOLAPMember(uname);
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
						if (m == null){
							boolean isCol = false;
							if (uname.startsWith("[Measures].")){ //$NON-NLS-1$
								for(String s : olapCube.getMdx().getRows()){
									if (s.startsWith("[Measures].")){ //$NON-NLS-1$
										isCol = false;
										break;
									}
								}
								
								for(String s : olapCube.getMdx().getCols()){
									if (s.startsWith("[Measures].")){ //$NON-NLS-1$
										isCol = true;
										break;
									}
								}
								e = new ItemElement(uname, isCol);
							}
							else{
								return;
							}
							
						}
						
						if (e==null){
							e = new ItemElement(m, ((ItemElement)o.getItem()).isCol(), false);
						}
						
						if (event.operations == DND.DROP_COPY){
							
							try{
								
								
								
								if (before){
									((CubeModel)table.getModel()).getOLAPCube().addBefore(e, (ItemElement)o.getItem());
								}
								else{
									((CubeModel)table.getModel()).getOLAPCube().addAfter(e, (ItemElement)o.getItem());
								}
								compositeFilter.init(table, olapCube, (CubeModel)table.getModel());
								
							}catch(WhereClauseException ex){
								Logger.getLogger(CompositeExplore.class).warn(ex.getMessage());
								if (MessageDialog.openQuestion(getShell(), Messages.CompositeExplore_12, Messages.CompositeExplore_13 + e.getDataMember().getHierarchy() + "?")){ //$NON-NLS-3$
									
									for(String s : ex.getWhereUname()){
										olapCube.getMdx().delWhere(s);
										whereClause.remove(s);
									}
									try{
										if (before){
											((CubeModel)table.getModel()).getOLAPCube().addBefore(e, (ItemElement)o.getItem());
										}
										else{
											((CubeModel)table.getModel()).getOLAPCube().addAfter(e, (ItemElement)o.getItem());
										}
										compositeFilter.init(table, olapCube, (CubeModel)table.getModel());
									}catch(Exception ex2){
										
									}
									
									whereViewer.refresh();
//									refreshWhere();
									
								}
								else{
									return;
								}
							}
							catch(MissingLastTimeDimensionException ex){
								Logger.getLogger(CompositeExplore.class).warn(ex.getMessage());
								MessageDialog.openWarning(getShell(), Messages.CompositeExplore_15, ex.getMessage());
							}
							
						}
						else if (event.operations == DND.DROP_MOVE){
							/*
							 * check if we change the axe of the ItemElement
							 */
							// if measure, swaping axe is not allowed
							if (m == null){
								if (((ItemElement)o.getItem()).isCol()){
									MessageDialog.openInformation(getShell(), Messages.CompositeExplore_16, Messages.CompositeExplore_17);
									return;
								}
							}
							
							if (before){
								((CubeModel)table.getModel()).getOLAPCube().moveBefore(e, (ItemElement)o.getItem());
							}
							else{
								((CubeModel)table.getModel()).getOLAPCube().moveAfter(e, (ItemElement)o.getItem());
							}
						}
						
						
//						((CubeModel)table.getModel()).getOLAPCube().add(e);
						((CubeModel)table.getModel()).performQuery();
						table.redraw();
						table.getParent().getParent().layout(true);
						
					}
				});
				
			}
			
			@Override
			public void dragOver(DropTargetEvent event) {
				
				org.eclipse.swt.graphics.Point pLocal = table.toControl(event.x, event.y);
				org.eclipse.swt.graphics.Point  p = table.getCellForCoordinates(pLocal.x, pLocal.y);
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
				CubeExplorerContent o = null;
				if (p.x >= 0 & p.y>=0){
					o = ((CubeModel)table.getModel()).getModelItemAt(p);
					if (o.getItem() instanceof ItemElement){
						
						Rectangle bounds = table.getCellRect(p.y, p.x);
						if (pLocal.x < bounds.x + bounds.width / 2) {
							event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
						} else {
							event.feedback |= DND.FEEDBACK_INSERT_AFTER;
						}
						event.detail = event.operations;
//						if (((DropTarget)event.getSource()).getControl() == table){
//							event.detail = DND.DROP_MOVE;
//						}
//						else{
							//event.detail = DND.DROP_COPY;
//						}
						
					}
					else{
						 event.detail = DND.DROP_NONE;
						 return;
					}

				}
				else{
//					event.feedback =  DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL| DND.FEEDBACK_NONE;
					// event.detail = DND.DROP_COPY;
					 return;
					
				}
				
				
			}
			
			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				
				
			}
			
			@Override
			public void dragLeave(DropTargetEvent event) {
				
				
			}
			
			@Override
			public void dragEnter(DropTargetEvent event) {
//				event.detail = DND.DROP_COPY;
			}
		});
		
		
		table.addDragDetectListener(new DragDetectListener() {
			
			@Override
			public void dragDetected(DragDetectEvent e) {
				drag = new Point(e.x, e.y);
				
			}
		});
		
		DragSource sourceTable = new DragSource(table, DND.DROP_MOVE);
		sourceTable.setTransfer(transferts);
		sourceTable.addDragListener(new DragSourceListener() {
			
			@Override
			public void dragStart(DragSourceEvent event) {
				
				event.doit = true;
			}
			
			@Override
			public void dragSetData(DragSourceEvent event) {
//				org.eclipse.swt.graphics.Point pLocal = table.toControl(event.x, event.y);
				if (drag == null){
					event.doit = false;
					return;
				}
				org.eclipse.swt.graphics.Point  p = table.getCellForCoordinates(drag.x, drag.y);
				
				CubeExplorerContent o = null;
				if (p.x >= 0 & p.y>=0){
					o = ((CubeModel)table.getModel()).getModelItemAt(p);
					if (o.getItem() instanceof ItemElement){
						event.data = ((ItemElement)o.getItem()).getDataMember().getUniqueName();
//						event.detail = DND.DROP_MOVE;
						event.doit = true;
						return;
					}
				}
				event.doit = false;
			}
			
			
			@Override
			public void dragFinished(DragSourceEvent event) {
				drag = null;
				
			}
		});
		
		/*
		 * where clauses
		 */
		DropTarget targetWhere = new DropTarget(whereViewer.getControl(), DND.DROP_COPY);
		targetWhere.setTransfer(transferts);
		targetWhere.addDropListener(new DropTargetListener() {
			
			@Override
			public void dropAccept(DropTargetEvent event) {
				
				
			}
			
			@Override
			public void drop(final DropTargetEvent event) {
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					
					@Override
					public void run() {
						String uname = (String)event.data;
						
						if (uname.startsWith("[Measures].")){ //$NON-NLS-1$
							return;
						}
						
						//check if the item can be added, if it cant, it cannot be used as a Where clause
						if (!(olapCube.getMdx().canAddToCol(uname) && olapCube.getMdx().canAddToRow(uname))){
							
							MessageDialog.openInformation(getShell(), Messages.CompositeExplore_19, Messages.CompositeExplore_20);
							return;
						}
						
						try{
							olapCube.getMdx().addWhere(uname);

							((CubeModel)table.getModel()).performQuery();
							
							table.redraw();
							table.getParent().layout(true);
							whereClause.add(uname);
//							refreshWhere();
							whereViewer.refresh();
						}catch(WhereClauseException ex){
							Logger.getLogger(CompositeExplore.class).error(ex);
							MessageDialog.openInformation(getShell(), Messages.CompositeExplore_21, ex.getMessage());
						}
						
					}
				});
				
				
			}
			
			@Override
			public void dragOver(DropTargetEvent event) {
				
				event.detail = event.operations;
			}
			
			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				
				
			}
			
			@Override
			public void dragLeave(DropTargetEvent event) {
				
				
			}
			
			@Override
			public void dragEnter(DropTargetEvent event) {
				
				
			}
		});
	}
	
//	private void refreshWhere(){
//		for(String s : whereClause){
//			boolean checkd = false;
//			for(String k : olapCube.getMdx().getWhere()){
//				if (k.equals(s)){
//					whereViewer.setChecked(s, true);
//					whereViewer.setGrayed(s, false);
//					checkd = true;
//					break;
//				}
//			}
//			
//			if (!checkd){
//				//make it gray if the dimension is present in the query otherwise uncheck it
//				if (olapCube.getMdx().isUsingOutsideWhere(s)){
//					whereViewer.setGrayed(s, true);
//					whereViewer.setChecked(s, false);
//				}
//				else{
//					whereViewer.setChecked(s, false);
//					whereViewer.setGrayed(s, false);
//				}
//			}
//		}
//		
//		whereViewer.refresh();
//	}
	
	
	private void createWhereMenu(){
		MenuManager mgr = new MenuManager();
		
		final Action delete = new Action(Messages.CompositeExplore_22){
			@Override
			public void run() {
				IStructuredSelection ss = (IStructuredSelection)whereViewer.getSelection();
				
				for(Object o : ss.toList()){
					olapCube.getMdx().delWhere((String)o);
					whereClause.remove((String)o);
				}
				getModel().performQuery();
				getModel().performQuery();
				
				table.redraw();
				table.getParent().layout(true);
				whereViewer.refresh();
			}
		};
	
		mgr.add(delete);
		
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				delete.setEnabled(!whereViewer.getSelection().isEmpty());
				
			}
		});
		
		whereViewer.getTable().setMenu(mgr.createContextMenu(whereViewer.getControl()));
	}

}
