package bpm.gateway.ui.composites;

import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.transformations.SubTransformation;
import bpm.gateway.ui.composites.labelproviders.DefaultStreamLabelProvider;
import bpm.gateway.ui.i18n.Messages;

public class CompositeSubTransfo extends Composite {
	
	
	private CheckboxTableViewer parameters;
	private StreamComposite streamComposite;
	private ISelectionChangedListener stremLst,paramLst;
	
	
	private SubTransformation transfo;
	
	public CompositeSubTransfo(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(3, false));
		this.setBackground(parent.getBackground());
		buildContent();
		
	}
	
	
	private void buildContent(){
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.CompositeSubTransfo_0);
		l.setBackground(getParent().getBackground());
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.CompositeSubTransfo_1);
		l2.setBackground(getParent().getBackground());
		
		streamComposite = new StreamComposite(this, SWT.NONE, false, false);
		streamComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		streamComposite.setBackground(getParent().getBackground());
		streamComposite.setLabelProvider(new DefaultStreamLabelProvider(streamComposite.getLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()){

			@Override
			public Color getForeground(Object element) {
				Integer i = streamComposite.getInput().indexOf(element);
				if (transfo.getMappingFor(i) != null){
					return PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get("bpm.gateway.ui.colors.mappingexists"); //$NON-NLS-1$
				}
				
				
				return super.getForeground(element);
			}
			
		});
		
		
		stremLst = new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				parameters.removeSelectionChangedListener(paramLst);
				Integer i = streamComposite.getSelection();
				
				if ( i == null){
					parameters.setSelection(StructuredSelection.EMPTY);
				}
				
				for(String k : (List<String>)parameters.getInput()){
					if (k.equals(transfo.getMappingFor(i))){
						parameters.setSelection(new StructuredSelection(k));
					}
				}
				parameters.addSelectionChangedListener(paramLst);
			}
			
		};
		streamComposite.addListenerOnViewer(stremLst);
		
		Composite buttonBar = new Composite(this, SWT.NONE);
		buttonBar.setLayout(new GridLayout());
		buttonBar.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, true));
		buttonBar.setBackground(getParent().getBackground());
		
		Button map = new Button(buttonBar, SWT.PUSH | SWT.FLAT);
		map.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		map.setText(Messages.CompositeSubTransfo_3);
		map.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (parameters.getCheckedElements().length <= 0 || streamComposite.getCheckedPosition().isEmpty()){
					return;
				}
				
				Integer i = streamComposite.getCheckedPosition().get(0);
				
				String pName = (String)parameters.getCheckedElements()[0];
				
				transfo.map(pName, i);
				streamComposite.refresh();
				parameters.refresh();
			}
			
		});
		
		
		Button unmap = new Button(buttonBar, SWT.PUSH | SWT.FLAT);
		unmap.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		unmap.setText(Messages.CompositeSubTransfo_4);
		unmap.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (parameters.getCheckedElements().length <= 0){
					return;
				}
					
				
				transfo.unmap((String)parameters.getCheckedElements()[0]);
				
				
				streamComposite.refresh();
				parameters.refresh();
			}
			
		});
		
		parameters = CheckboxTableViewer.newCheckList(this, SWT.BORDER|SWT.FLAT |SWT.V_SCROLL | SWT.H_SCROLL);
		parameters.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		parameters.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<String> l  = (List<String>)inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		
		
		
		DecoratingLabelProvider  p = new DecoratingLabelProvider(
			new LabelProvider(){

				@Override
				public String getText(Object element) {
					return (String)element;
				}
				
				
				
			} ,PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator() ){

				@Override
				public Color getForeground(Object element) {
					if (transfo.getMappingFor((String)element) != null){
						return PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get("bpm.gateway.ui.colors.mappingexists"); //$NON-NLS-1$
					}
					return super.getForeground(element);
				}
			
			
		};
		parameters.setLabelProvider(p);
		parameters.addCheckStateListener(new ICheckStateListener(){
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				List<String> input = (List<String>)parameters.getInput();
				for(String e : input){
					if (event.getChecked() && e != event.getElement()){
						parameters.setChecked(e, false);
					}
				}
				
				if (event.getChecked()){
					parameters.setSelection(new StructuredSelection(event.getElement()));
				}
				
			}
			
		});
		parameters.getControl().setBackground(getParent().getBackground());
		
		paramLst = new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				streamComposite.desactiveListener(true);
				IStructuredSelection ss = (IStructuredSelection)parameters.getSelection();
				if (ss.isEmpty()){
					
				}
				String s = (String)ss.getFirstElement();
				streamComposite.setSelection(transfo.getMappingFor(s));
					
				streamComposite.desactiveListener(false);
					
				
			}
			
		};
		parameters.addSelectionChangedListener(paramLst);
	}

	
	public void fillDatas(SubTransformation transfo, StreamDescriptor inputStream, List<String> params){
		this.transfo = transfo;
		streamComposite.fillDatas(inputStream.getStreamElements());
		parameters.setInput(params);
	}
	
	
	
}
