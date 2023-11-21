package bpm.vanilla.platform.core.beans.ged.constant;

public enum SecurityConstants {
	
	GROUPID(1, "groupid"),
	REPOSITORYID(2, "repositoryid"),
	USERID(3, "userid")
	;
	
	private int id;
	private final String name;
	
	private SecurityConstants(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

}
