package bpm.vanilla.platform.core.beans.data;

public class DatasourceHBase implements IDatasourceObject {

	private static final long serialVersionUID = 1L;

	private String url;
	private String port;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public boolean equals(Object o) {
		return url.equals(((DatasourceHBase)o).getUrl()) && port.equals(((DatasourceHBase)o).getPort());
	}

	
}
