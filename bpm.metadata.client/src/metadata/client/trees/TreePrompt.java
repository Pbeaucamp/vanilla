package metadata.client.trees;

import bpm.metadata.resource.Prompt;

public class TreePrompt extends TreeResource {

	private Prompt prompt;

	public TreePrompt(Prompt prompt) {
		super(prompt);
		this.prompt = prompt;
	}

	@Override
	public String toString() {
		return prompt.getName();
	}

	public Prompt getPrompt() {
		return prompt;
	}

	@Override
	public Object getContainedModelObject() {
		return prompt;
	}
}
