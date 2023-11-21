package bpm.mdm.ui.diff;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.DiffResult;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.RowState;
import bpm.mdm.model.runtime.RuntimeFactory;
import bpm.mdm.ui.i18n.Messages;

public class DiffMasterDetails extends MasterDetailsBlock{
	private static final Color newRow = new Color(Display.getDefault(), 0, 255, 0);
	private static final Color updatedRow = new Color(Display.getDefault(), 155, 120, 30);
	private static final Color rejectedRow = new Color(Display.getDefault(), 255, 0, 0);
	
	
	private class ColLabelProvider extends ColumnLabelProvider{
		
		private Attribute attribute;
		public ColLabelProvider(Attribute attribute){this.attribute= attribute;}
		@Override
		public String getText(Object element) {
			return ((Row)element).getValue(attribute) + ""; //$NON-NLS-1$
		}
		
		@Override
		public Color getBackground(Object element) {
			if (RowState.DISCARD.equals(diff.getState((Row)element))){
				return rejectedRow;
			}
			else if (RowState.NEW.equals(diff.getState((Row)element))){
				return newRow;
			}
			else if (RowState.UPDATE.equals(diff.getState((Row)element))){
				return updatedRow;
			}
			return super.getBackground(element);
		}
	}

	private Entity entity;
	
	private TableViewer rowsViewer;
	private FormToolkit toolkit;
	
	private DiffResult diff;
	
	
	private Text read, discarded, toadd, toupdate;
	
	public DiffMasterDetails(Entity entity){
		this.entity = entity;
	}
	
	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		toolkit = managedForm.getToolkit();
		
		Composite main = toolkit.createComposite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Section sctnModel = toolkit.createSection(main, Section.TWISTIE | Section.TITLE_BAR);
		sctnModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		toolkit.paintBordersFor(sctnModel);
		sctnModel.setText(Messages.DiffMasterDetails_1);
		sctnModel.setClient(createStatistics(sctnModel));
		
		sctnModel = toolkit.createSection(main, Section.TWISTIE | Section.TITLE_BAR);
		sctnModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		toolkit.paintBordersFor(sctnModel);
		sctnModel.setText(Messages.DiffMasterDetails_2);
		
		SectionPart masterpart  = new SectionPart(sctnModel);
		managedForm.addPart(masterpart);
		
		rowsViewer = createViewer(sctnModel, managedForm,masterpart);
		toolkit.paintBordersFor(rowsViewer.getTable());
		sctnModel.setClient(rowsViewer.getControl());
		
	}
	
	

	private Control createStatistics(Section sctnModel) {
		Composite main = toolkit.createComposite(sctnModel);
		main.setLayout(new GridLayout(2, false));
		
		Label l = toolkit.createLabel(main, Messages.DiffMasterDetails_3);
		l.setLayoutData(new GridData());
		
		read = toolkit.createText(main, "", SWT.BORDER); //$NON-NLS-1$
		read.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		read.setEditable(false);
		
		
		l = toolkit.createLabel(main, Messages.DiffMasterDetails_5);
		l.setLayoutData(new GridData());
		
		toadd = toolkit.createText(main, "", SWT.BORDER); //$NON-NLS-1$
		toadd.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		toadd.setEditable(false);
		
		
		l = toolkit.createLabel(main, Messages.DiffMasterDetails_7);
		l.setLayoutData(new GridData());
		
		toupdate = toolkit.createText(main, "", SWT.BORDER); //$NON-NLS-1$
		toupdate.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		toupdate.setEditable(false);
		
		
		l = toolkit.createLabel(main, Messages.DiffMasterDetails_9);
		l.setLayoutData(new GridData());
		
		discarded = toolkit.createText(main, "", SWT.BORDER); //$NON-NLS-1$
		discarded.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		discarded.setEditable(false);
		
		return main;
	}

	private TableViewer createViewer(Section sctnModel, final IManagedForm managedForm, final IFormPart part) {
		rowsViewer = new TableViewer(sctnModel, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
		rowsViewer.getTable().setHeaderVisible(true);
		rowsViewer.getTable().setLinesVisible(true);
		rowsViewer.setContentProvider(new ArrayContentProvider());
		rowsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(part, event.getSelection());
				
			}
		});
		
		buildColumns();
		return rowsViewer;
	}


	
	private void buildColumns(){
		for(Attribute a : entity.getAttributes()){
			TableViewerColumn col = new TableViewerColumn(rowsViewer, SWT.NONE);
			col.getColumn().setText(a.getName());
			col.getColumn().setWidth(100);
			col.setLabelProvider(new ColLabelProvider(a));
		}
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		
		
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(RuntimeFactory.eINSTANCE.createRow().getClass(), new DiffComparisonPage(this));
		
	}

	
	public void bind(DiffResult diff){
		this.diff = diff;
		rowsViewer.setInput(diff.getRows());
		
		read.setText("" + diff.getReadNumber()); //$NON-NLS-1$
		toadd.setText("" + diff.getNewNumber()); //$NON-NLS-1$
		toupdate.setText("" + diff.getUpdateNumber()); //$NON-NLS-1$
		discarded.setText("" + diff.getDiscardNumber()); //$NON-NLS-1$
	}
	
	public DiffResult getDiff(){
		return this.diff;
	}

	public Entity getEntity() {
		return entity;
	}
}
