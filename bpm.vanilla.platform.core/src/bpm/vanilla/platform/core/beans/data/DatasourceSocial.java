package bpm.vanilla.platform.core.beans.data;


public class DatasourceSocial implements IDatasourceObject {

	private static final long serialVersionUID = 1L;

	public enum SocialType {
		TWITTER("Twitter");//, FACEBOOK("Facebook");
	
		private String type;

		private SocialType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

	}
	
	public enum Function {
		TWITTER_USERS("Get Users"), 
		TWITTER_TRENDS("Get Trends"),
		TWITTER_SEARCH("Search"),
		TWITTER_TIMELINE("Timeline");
	
		private String type;

		private Function(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

	}
	
	private SocialType type;
	private Function function;
	private String params; // suite de params séparés par '&&'
	
	public SocialType getType() {
		return type;
	}
	public void setType(SocialType type) {
		this.type = type;
	}
	public Function getFunction() {
		return function;
	}
	public void setFunction(Function function) {
		this.function = function;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	@Override
	public boolean equals(Object o) {
		return type == ((DatasourceSocial)o).getType() && function == ((DatasourceSocial)o).getFunction() && params.equals(((DatasourceSocial)o).getParams());
	}
	
}
