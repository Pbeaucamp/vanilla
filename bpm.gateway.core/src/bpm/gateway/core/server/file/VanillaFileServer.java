package bpm.gateway.core.server.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class VanillaFileServer extends FileSystemServer implements IServerConnection{

	protected String repDefinitionId;
	protected String vanillaUrl;
	protected String vanillaLogin;
	protected String vanillaPasword;
	protected IRepositoryApi repositorySocket;
	protected IVanillaAPI vanillaApi;
	
	public VanillaFileServer(){}
	
	public VanillaFileServer(String name, String description, String url, String login, String password, String repDefId){
		setName(name);
		setDescription(description);
		setVanillaUrl(url);
		setVanillaLogin(login);
		setVanillaPasword(password);
		setRepositoryDefinitionId(repDefId);
	}
	
	public void setRepositoryDefinitionId(String repDefId){
		repDefinitionId = repDefId;
	}
	
	/**
	 * @return the vanillaUrl
	 */
	public String getVanillaUrl() {
		return vanillaUrl;
	}

	/**
	 * @param vanillaUrl the vanillaUrl to set
	 */
	public void setVanillaUrl(String vanillaUrl) {
		this.vanillaUrl = vanillaUrl;
	}

	/**
	 * @return the vanillaLogin
	 */
	public String getVanillaLogin() {
		return vanillaLogin;
	}

	/**
	 * @param vanillaLogin the vanillaLogin to set
	 */
	public void setVanillaLogin(String vanillaLogin) {
		this.vanillaLogin = vanillaLogin;
	}

	/**
	 * @return the vanillaPasword
	 */
	public String getVanillaPasword() {
		return vanillaPasword;
	}

	/**
	 * @param vanillaPasword the vanillaPasword to set
	 */
	public void setVanillaPasword(String vanillaPasword) {
		this.vanillaPasword = vanillaPasword;
	}

	@Override
	public InputStream getInpuStream(DataStream stream) throws Exception {
		
		RepositoryItem dummy = new RepositoryItem();
		dummy.setId(Integer.parseInt(stream.getDefinition()));
		dummy.setType(IRepositoryApi.EXTERNAL_DOCUMENT);
		dummy.setItemName("___dummy");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		repositorySocket.getDocumentationService().importExternalDocument(dummy, bos);
		bos.close();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		
		return bis;
	}
	
	public Server getServer() {
		return this;
	}
	
	public boolean isOpened() {
		return repositorySocket != null;
	}
	
	public boolean isSet() {
		return repositorySocket != null;
	}
	
	
	@Override
	public void disconnect() {
		repositorySocket = null;
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.server.file.AbstractFileServer#connect()
	 */
	@Override
	public void connect() throws ServerException {
		if(vanillaApi == null) {
			vanillaApi = new RemoteVanillaPlatform(vanillaUrl, vanillaLogin, vanillaPasword);
		}
		
//		AdminAccess admin = new AdminAccess(vanillaUrl);
		User user = null;
		
		try{
//			user = vanillaApi.getVanillaSecurityManager().getUserByLogin(vanillaLogin);
			user = vanillaApi.getVanillaSecurityManager().authentify("", vanillaLogin, vanillaPasword, false);
		}catch(Exception ex){
			ex.printStackTrace();
			throw new ServerException("Unable to connect to Vanilla Server, " + ex.getMessage(), this);
		}
		if (user == null || user.getId() == null || user.getId() <= 0){
			throw new ServerException("Bad password", this);
		}
//		if (!user.getPassword().equals(MD5Helper.encode(vanillaPasword))){
//			throw new ServerException("Bad password", this);
//		}
		
		Repository def = null;
		
		try{
			for(Repository d : vanillaApi.getVanillaRepositoryManager().getUserRepositories(user.getLogin())){
				if (d.getId() == Integer.parseInt(repDefinitionId)){
					def = d;
					break;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw new ServerException("Unable to connect to Vanilla Server, " + ex.getMessage(), this);

		}
		
		
		if (def == null){
			throw new ServerException("Unable to find this repository for this user", this);
		}
		
		
		try{
			
			IVanillaContext ctx = new BaseVanillaContext(vanillaUrl, vanillaLogin, vanillaPasword);
			Group grp = new Group();
			grp.setId(-1);
			IRepositoryContext repCtx = new BaseRepositoryContext(ctx, grp, def);
			
			repositorySocket = new RemoteRepositoryApi(repCtx);
		
		}catch(Exception ex){
			ex.printStackTrace();
			repositorySocket = null;
			throw new ServerException("Unable to connnect to repository, " + ex.getMessage(), this);
		}
		
		
		
		
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.server.file.AbstractFileServer#getConnections()
	 */
	@Override
	public List<IServerConnection> getConnections() {
		return super.getConnections();
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.server.file.AbstractFileServer#getCurrentConnection()
	 */
	@Override
	public IServerConnection getCurrentConnection() {
		return this;
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.server.file.AbstractFileServer#getElement()
	 */
	@Override
	public Element getElement() {
		Element el = DocumentHelper.createElement("vanillaFileServer");
		el.addElement("name").setText(getName());
		el.addElement("description").setText(getDescription());
		
		
		Element con = el.addElement("vanillaFileConnection");
		con.addElement("url").setText(getVanillaUrl());
		con.addElement("login").setText(getVanillaLogin());
		con.addElement("password").setText(getVanillaPasword());
		con.addElement("repDefId").setText(repDefinitionId);
		
		
		
		return el;
		
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.server.file.AbstractFileServer#testConnection()
	 */
	@Override
	public boolean testConnection() {
		try{
			repositorySocket.test();
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}

}
