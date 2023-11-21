package metadata.client.trees;

import bpm.metadata.layer.logical.ICalculatedElement;

public class TreeFormula extends TreeDataStreamElement {
	private ICalculatedElement formula;
	
	public TreeFormula(ICalculatedElement formula){
		super(formula);
		this.formula = formula;
	}

	public ICalculatedElement getFormula(){
		return formula;
	}
	
	@Override
	public String toString() {
		return formula.getName();
	}
	@Override
	public Object getContainedModelObject() {
		return formula;
	}
}
