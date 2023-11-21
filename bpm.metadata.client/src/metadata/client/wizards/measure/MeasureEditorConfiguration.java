package metadata.client.wizards.measure;

import java.util.ArrayList;
import java.util.List;

import metadataclient.Activator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.complex.FmdtDimension;

public class MeasureEditorConfiguration extends SourceViewerConfiguration{
	private ITextDoubleClickStrategy click = new DefaultTextDoubleClickStrategy() {
		
		public void doubleClicked(ITextViewer viewer) {
			super.doubleClicked(viewer);
			
		}
		
		@Override
		protected IRegion findExtendedDoubleClickSelection(IDocument document, int offset) {
			
			try {
				char c = document.getChar(offset);
				int start = -1;
				int end = -1;
				if (Character.isLetterOrDigit(c)){
					int pos = offset + 1;
					char cur;
					while( (cur = document.getChar(pos)) != '>' && cur != '}' && cur != ']'){
						if (cur == ' ' || cur == '(' || cur == ')'){
							return super.findExtendedDoubleClickSelection(document, offset);
						}
						pos++;
					}
					if (cur == ']'){
						if (document.getChar(pos) == '}'){
							pos++;
						}
					}
					end = pos;
					
					pos = offset - 1;
					
					while( (cur = document.getChar(pos)) != '<' && cur != '{' && cur != '[' ){
						if (cur == ' ' || cur == '(' || cur == ')'){
							return super.findExtendedDoubleClickSelection(document, offset);
						}
						pos--;
					}
					if (cur == '['){
						if (document.getChar(pos) == '{'){
							pos--;
						}
					}
					
					start = pos;
				}
				else if (c == '<'){
					if (document.getChar(offset+1) == '='){
						start = offset;
						end = offset+2;
					}
					else{
						start = offset;
						int pos = offset + 1;
						char cur;
						while( (cur = document.getChar(pos)) != '>'){
							if (cur == ' ' || cur == '(' || cur == ')'){
								return super.findExtendedDoubleClickSelection(document, offset);
							}
							pos++;
						}
						
						end = pos;
					}
					
					
					
				}
				else if (c == '>'){
					if (document.getChar(offset+1) == '='){
						start = offset;
						end = offset+2;
					}
					else{
						end = offset;
						int pos = offset - 1;
						char cur;
						while( (cur = document.getChar(pos)) != '<'){
							if (cur == ' ' || cur == '(' || cur == ')'){
								return super.findExtendedDoubleClickSelection(document, offset);
							}
							pos--;
						}
						
						start = pos;
					}
					
				}
				else if (c == 'i'){
					if (document.getChar(offset - 1) == ' ' && document.getChar(offset + 1) == 'f' && document.getChar(offset + 2) == ' '){
						start = offset;
						end = offset + 2;
					}
				}
				else if (c == 'f'){
					if (document.getChar(offset - 1) == 'i' && document.getChar(offset + 1) == ' ' && document.getChar(offset - 2) == ' '){
						start = offset - 2;
						end = offset;
					}
				}
				else if (c == '='){
					if (document.getChar(offset - 1) == '='){
						start = offset - 2;
						end = offset;
					}
					if ( document.getChar(offset + 1) == '=' ){
						start = offset;
						end = offset + 2;
					}
				}
				
				
				return new Region(start, end - start + 1);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
				
			
			return super.findExtendedDoubleClickSelection(document, offset);
		}
	};
	private List<IDataStreamElement> measuresCandidate = new ArrayList<IDataStreamElement>();
	
	
	public MeasureEditorConfiguration(IDataSource dataSource){
		for(IDataStream t : dataSource.getDataStreams()){
			for(IDataStreamElement c : t.getElements()){
				if (c.getType().getParentType() == IDataStreamElement.Type.MEASURE){
					measuresCandidate.add(c);
				}
			}
		}
		
	}
	
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler pr = new PresentationReconciler();
		List<FmdtDimension> dims = new ArrayList<FmdtDimension>();
		
		for(IResource r : Activator.getDefault().getModel().getResources()){
			if (r instanceof FmdtDimension){
				dims.add((FmdtDimension)r);
			}
		}
		DefaultDamagerRepairer ddr = new DefaultDamagerRepairer(new	MeasureScanner(measuresCandidate, dims));
		pr.setDamager(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		pr.setRepairer(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		return pr;
	}
	
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		return click;
	}
	
}
