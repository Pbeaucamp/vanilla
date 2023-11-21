package bpm.fasd.expressions.ui.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPMeasure;

import bpm.fasd.expressions.api.model.FasdDimension;
import bpm.fasd.expressions.api.model.FasdMeasure;
import bpm.fasd.expressions.api.model.FasdParser;
import bpm.fasd.expressions.api.model.FormulaElementProvider;
import bpm.studio.expressions.core.measures.impl.Dimension;
import bpm.studio.expressions.core.measures.impl.DimensionLevel;
import bpm.studio.expressions.core.measures.impl.Measure;
import bpm.studio.expressions.core.measures.impl.MeasureParser;
import bpm.studio.expressions.ui.measure.CompositeMeasureEditor;
import bpm.studio.expressions.ui.measure.DocumentMeasure;

public class DialogColorFormula extends Dialog{
	private CompositeMeasureEditor editor;
	private FormulaElementProvider provider ;
	
	private FAModel fasdModel;
	
	private String rules;
	
	public DialogColorFormula(Shell parentShell, FAModel fasdModel) {
		super(parentShell);
		setShellStyle(getShellStyle() |  SWT.RESIZE);
		this.fasdModel = fasdModel;
		provider = new FormulaElementProvider(this.fasdModel);
	}
	
	public DialogColorFormula(Shell parentShell, FAModel fasdModel, String rule) {
		this(parentShell, fasdModel);
		this.rules = rule;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		editor = new CompositeMeasureEditor(parent, SWT.NONE, new FasdMeasureContentProvider(fasdModel.getOLAPSchema()));
		editor.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		List<Dimension> dims = new ArrayList<Dimension>();
		List<Measure> mes = new ArrayList<Measure>();
		for(OLAPDimension dim : fasdModel.getOLAPSchema().getDimensions()){
			dims.add(new Dimension(new FasdDimension(dim)));
		}
		
		for(OLAPMeasure f : fasdModel.getOLAPSchema().getMeasures()){
			mes.add(new FasdMeasure(f));
		}
		
		
		FasdParser parser = new FasdParser(mes, new ArrayList<DimensionLevel>(), dims);

		
		if (rules == null){
			editor.setInput(fasdModel, parser,null, provider, provider);	
		}
		else{
			
			
			IDocument doc = new DocumentMeasure(parser, provider, provider); 
			doc.set(rules);
			editor.setInput(fasdModel, parser, doc, provider, provider);

		}
		
		
		
		return editor;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
	}
	
	public String getRule(){
		return rules;
	}
	
	@Override
	protected void okPressed() {
		rules = editor.getScript();
		super.okPressed();
	}
}
