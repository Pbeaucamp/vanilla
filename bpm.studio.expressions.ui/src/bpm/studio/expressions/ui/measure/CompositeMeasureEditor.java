package bpm.studio.expressions.ui.measure;



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

import bpm.studio.expressions.core.measures.IOperator;
import bpm.studio.expressions.core.measures.impl.DimensionLevel;
import bpm.studio.expressions.core.measures.impl.MeasureParser;
import bpm.studio.expressions.core.model.IDimensionProvider;
import bpm.studio.expressions.core.model.IField;
import bpm.studio.expressions.core.model.IFieldProvider;
import bpm.studio.expressions.core.model.StructureDimension;



public class CompositeMeasureEditor extends Composite{

	private static Color RED = new Color(Display.getDefault(), 255, 0, 0);
	private static Color BLACK = new Color(Display.getDefault(), 0, 0, 0);
	
	private TreeViewer palette;
	private SourceViewer textViewer;
	private Text console;
	private IDocument document ;
	
	private MeasureContentProvider measureContentProvider;
	
	public CompositeMeasureEditor(Composite parent, int style, MeasureContentProvider measureContentProvider) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.measureContentProvider = measureContentProvider;
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
		palette.setContentProvider(measureContentProvider);
		palette.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof IOperator){
					return ((IOperator)element).getSymbol();
				}
				else if (element instanceof IField){
					return ((IField)element).getName();
				}
				else if (element instanceof StructureDimension){
					return ((StructureDimension)element).getName();
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

	
	protected void configureEditor(MeasureEditorConfiguration configuration){
		textViewer.configure(configuration);
	}
	
	public void setInput(Object input, MeasureParser parser, IDocument doc, IDimensionProvider dimensionProvider, IFieldProvider fieldProvider){
		palette.setInput(input);
		configureEditor(new MeasureEditorConfiguration(fieldProvider, dimensionProvider));	
		if (doc == null){
			this.document = new DocumentMeasure(parser, dimensionProvider, fieldProvider);
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
						
						if (result == null){
							console.setText("Bad expression");
						}else{
							console.setText(result);
						}
						
					}
					
				}
				
				public void documentAboutToBeChanged(DocumentEvent event) {
					
					
				}
			});
		}
	}
	
	public String getScript(){
		return document.get();
	}
}
