package bpm.metadata.birt.oda.ui.trees;

import bpm.metadata.resource.Prompt;

public class TreePrompt extends TreeObject {
	private Prompt prompt;
	
	public TreePrompt(Prompt prompt) {
		super(prompt.getOutputName());
		this.prompt = prompt;
	}

	@Override
	public String toString() {
		return prompt.getOutputName();
	}
	
	public Prompt getPrompt(){
		return prompt;
	}
}
