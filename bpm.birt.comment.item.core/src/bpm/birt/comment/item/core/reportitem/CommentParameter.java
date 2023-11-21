package bpm.birt.comment.item.core.reportitem;

public class CommentParameter {
	
	private String name;
	private String defaultValue;
	private String prompt;
	
	public CommentParameter(String name, String defaultValue, String prompt) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.prompt = prompt;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public String getPrompt() {
		return prompt;
	}
}
