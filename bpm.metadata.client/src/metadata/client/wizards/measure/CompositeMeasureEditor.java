package metadata.client.wizards.measure;

import metadataclient.Activator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.complex.FmdtDimension;
import bpm.metadata.resource.complex.measures.IOperator;
import bpm.metadata.resource.complex.measures.impl.DimensionLevel;

public class CompositeMeasureEditor extends Composite{

	private static Color RED = new Color(Display.getDefault(), 255, 0, 0);
	private static Color BLACK = new Color(Display.getDefault(), 0, 0, 0);
	
	private TreeViewer palette;
	private SourceViewer textViewer;
	private Text console;
	private IDocument document ;
	
	
	public CompositeMeasureEditor(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		
		SashForm sashComposite = new SashForm(this, SWT.HORIZONTAL);
		sashComposite.setLayout(new GridLayout(2, false));
		sashComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		buildPalette(sashComposite);
		
		buildViewer(sashComposite);
		
		buildConsole(this);
	}
	
	private void buildPalette(Composite parent){
		palette = new TreeViewer(parent, SWT.BORDER);
		palette.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true));
		palette.setContentProvider(new MeasureContentProvider());
		palette.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof IOperator){
					return ((IOperator)element).getSymbol();
				}
				else if (element instanceof IDataStream){
					return ((IDataStream)element).getName();
				}
				else if (element instanceof FmdtDimension){
					return ((FmdtDimension)element).getName();
				}
				else if (element instanceof IDataStreamElement){
					return ((IDataStreamElement)element).getName();
				}
				else if (element instanceof DimensionLevel){
					return ((DimensionLevel)element).getSymbol();
				}
				return super.getText(element);
			}
		});
		
		
		
		palette.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection)palette.getSelection();
				
				
				if ( ! (ss.getFirstElement() instanceof IOperator)){
					return;
				}
				Point p = textViewer.getSelectedRange();
				try {
					document.replace(p.x, p.y, ((IOperator)ss.getFirstElement()).getTemplate());
				} catch (BadLocationException e) {
					
					e.printStackTrace();
				}
				
			}
		});
	}
	
	private void buildViewer(Composite parent){
		textViewer = new SourceViewer(parent, null, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		textViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	
		
	}
	
	private void buildConsole(Composite parent){
		console = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		console.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
	}

	
	public void setInput(IDocument doc, IDataSource dataSource){
		palette.setInput(dataSource);
		textViewer.configure(new MeasureEditorConfiguration(dataSource));
		if (doc == null){
			this.document = new DocumentMeasure(Activator.getDefault().getModel(), dataSource);
		}
		else{
			this.document = doc;
		}
		textViewer.setDocument(document);
		
		textViewer.getTextWidget().setFont(new Font(Display.getDefault(), "Arial", 10, SWT.NORMAL));
		
		if (document != null && document instanceof DocumentMeasure){
			
			document.addDocumentListener(new IDocumentListener() {
				
				public void documentChanged(DocumentEvent event) {
					String error = ((DocumentMeasure)document).getError();
					String result = ((DocumentMeasure)document).getResult();
					
					if (error != null){
						console.setForeground(RED);
						console.setText(error);
						
					}
					else{
						console.setForeground(BLACK);
						console.setText(result);
					}
					
				}
				
				public void documentAboutToBeChanged(DocumentEvent event) {}
			});
		}
	}
	
	public String getScript(){
		return document.get();
	}
}
