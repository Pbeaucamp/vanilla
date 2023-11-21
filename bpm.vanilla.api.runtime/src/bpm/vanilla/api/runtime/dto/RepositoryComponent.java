package bpm.vanilla.api.runtime.dto;

public abstract class RepositoryComponent {
	private int id;
	private String name;
	private boolean isDirectory;
	private String type;

	public RepositoryComponent(int _id, String _name, boolean _isDirectory, String _type) {
		id = _id;
		name = _name;
		isDirectory = _isDirectory;
		type = _type;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public String getType() {
		return type;
	}
	
	

}
