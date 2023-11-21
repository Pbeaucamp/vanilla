package bpm.metadata.client.security.blocks;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.metadata.MetaData;
import bpm.metadata.client.security.blocks.detailspages.BusinessTablePage;
import bpm.metadata.client.security.blocks.detailspages.ConnectionPage;
import bpm.metadata.client.security.blocks.detailspages.DataStreamElementPage;
import bpm.metadata.client.security.blocks.detailspages.DataStreamPage;
import bpm.metadata.client.security.blocks.detailspages.SecurizablePage;
import bpm.metadata.client.security.viewers.FMDTLabelProvider;
import bpm.metadata.client.security.viewers.FMDTTreeContentProvider;
import bpm.metadata.client.security.viewers.FMDTViewerComparator;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.logical.sql.SQLDataStreamElement;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;


public class SecurityMasterDetailBlock extends MasterDetailsBlock{
	private static final Color securedColor = new Color(Display.getDefault(), 80, 190, 33);
	public static final Font securedFont = new Font(Display.getCurrent(), "Arial", 8, SWT.BOLD | SWT.ITALIC);
	
	private TreeViewer modelViewer;
	
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();

		Section section = toolkit.createSection(parent, Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText("FreeMetaData Security Design");
		section.setDescription("Show the FreeMetaData Model from the user view");
		
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite main = toolkit.createComposite(section, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		modelViewer = new TreeViewer(toolkit.createTree(main, SWT.BORDER | SWT.VIRTUAL));
		modelViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		modelViewer.setContentProvider(new FMDTTreeContentProvider());
		modelViewer.setLabelProvider(new DecoratingLabelProvider(new FMDTLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()){
			@Override
			public Color getForeground(Object element) {
				if (element instanceof IDataStream){
					if (!((IDataStream)element).getFilters().isEmpty()){
						return securedColor;
					}
				}
				else if (element instanceof IBusinessTable){
					if (!((IBusinessTable)element).getFilters().isEmpty()){
						return securedColor;
					}
				}
				return super.getForeground(element);
			}
			
			@Override
			public Font getFont(Object element) {
				if (element instanceof IDataStream){
					if (!((IDataStream)element).getFilters().isEmpty()){
						return securedFont;
					}
				}
				else if (element instanceof IBusinessTable){
					if (!((IBusinessTable)element).getFilters().isEmpty()){
						return securedFont;
					}
				}
				return super.getFont(element);
			}
		});
		modelViewer.setAutoExpandLevel(3);
		modelViewer.setComparator(new FMDTViewerComparator());
		
		
		
		
		section.setClient(main);
		
		toolkit.paintBordersFor(modelViewer.getTree());
		final SectionPart sectionPart = new SectionPart(section);
		managedForm.addPart(sectionPart);
		modelViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(sectionPart, modelViewer.getSelection());
				managedForm.getForm().layout(true);
				
			}
		});
			
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		
		
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(BusinessModel.class, new SecurizablePage());
		detailsPart.registerPage(BusinessPackage.class, new SecurizablePage());
		
		detailsPart.registerPage(ListOfValue.class, new SecurizablePage());
		detailsPart.registerPage(SqlQueryFilter.class, new SecurizablePage());
		detailsPart.registerPage(Filter.class, new SecurizablePage());
		detailsPart.registerPage(ComplexFilter.class, new SecurizablePage());
		detailsPart.registerPage(Prompt.class, new SecurizablePage());
		
		detailsPart.registerPage(SQLDataStreamElement.class, new DataStreamElementPage());
		
		detailsPart.registerPage(SQLDataStream.class, new DataStreamPage());
		detailsPart.registerPage(SQLBusinessTable.class, new BusinessTablePage());
		
		detailsPart.registerPage(SQLConnection.class, new ConnectionPage());
	}

	public void setFmdt(MetaData model) {
		if (modelViewer.getInput() != model){
			modelViewer.setInput(model);
		}
		else{
			modelViewer.refresh();
		}
		
		
	}

	public ISelectionProvider getViewer() {
		return modelViewer;
	}

}
