package bpm.fd.design.ui.wizard.fmdt;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.design.ui.wizard.fmdt.FmdDataSetHelper.Options;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;

public class FmdtResourcePage extends WizardPage{

	private CheckboxTableViewer resourceViewer;
	private Button generateFilters;
	private Combo filterRenderer;
	private FmdDataSetHelper helper;
//	private Text desc;
	
	protected FmdtResourcePage(String pageName, FmdDataSetHelper helper) {
		super(pageName);
		this.helper = helper;
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		
		Composite fmdt = new Composite(main, SWT.NONE);
		fmdt.setLayout(new GridLayout());
		fmdt.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		resourceViewer = CheckboxTableViewer.newCheckList(fmdt, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		resourceViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		resourceViewer.setContentProvider(new ArrayContentProvider());
		resourceViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IResource)element).getOutputName(Locale.getDefault());
			}
		});
		resourceViewer.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
		resourceViewer.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return !(element instanceof ListOfValue);
			}
		});
		try {
			resourceViewer.setInput(helper.getFmdtResources());
		} catch (Exception e1) {
			
			e1.printStackTrace();
		};
		
		Composite opts = new Composite(main, SWT.NONE);
		opts.setLayout(new GridLayout(2, false));
		opts.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		generateFilters = new Button(opts, SWT.CHECK);
		generateFilters.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		generateFilters.setText("Generate Filters Components");
		generateFilters.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				filterRenderer.setEnabled(generateFilters.getSelection());
			}
		});
		
		Label l = new Label(opts, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Filter Renderer");
		
		filterRenderer = new Combo(opts, SWT.READ_ONLY);
		filterRenderer.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		filterRenderer.setItems(FilterRenderer.RENDERER_NAMES);
		filterRenderer.select(0);
		filterRenderer.setEnabled(false);
		
		setControl(main);
	}

	@Override
	public boolean isPageComplete() {
		return resourceViewer.getCheckedElements().length > 0;
	}
	
	public void fillViewer(List<IResource> resources){
		resourceViewer.setInput(resources);
	}
	
	public Options getOptions(){
		return new Options(generateFilters.getSelection(), filterRenderer.getSelectionIndex());
	}
	
	public List<IResource> getResources(){
		Object[] o = resourceViewer.getCheckedElements();
		List l = new ArrayList(o.length);
		
		for(Object i : o){
			l.add(i);
		}
		return l;
	}
}
