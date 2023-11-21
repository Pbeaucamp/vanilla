package bpm.workflow.runtime.model;

public interface IEncrypt {

	public String getPublicKeyPath();
	public void setPublicKeyPath(String publicKeyPath);
	public String getPublicKeyName();
	public void setPublicKeyName(String publicKeyName);
	
	public String getPrivateKeyPath();
	public void setPrivateKeyPath(String privateKeyPath);
	public String getPrivateKeyName();
	public void setPrivateKeyName(String privateKeyName);

	public String getPassword();
	public void setPassword(String password);

	public boolean isEncrypt();
	public void setEncrypt(boolean isEncrypt);
}
