package bpm.vanilla.workplace.remote.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.workplace.core.IPackage;
import bpm.vanilla.workplace.core.IProject;
import bpm.vanilla.workplace.core.IUser;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.PlacePackage;
import bpm.vanilla.workplace.core.model.PlaceProject;
import bpm.vanilla.workplace.core.model.PlaceUser;
import bpm.vanilla.workplace.core.services.VanillaPlaceService;
import bpm.vanilla.workplace.core.xstream.IXmlActionType;
import bpm.vanilla.workplace.core.xstream.XmlAction;
import bpm.vanilla.workplace.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.workplace.remote.internal.HttpCommunicator;

import com.thoughtworks.xstream.XStream;

public class RemotePlaceService implements VanillaPlaceService {

	private HttpCommunicator httpCommunicator;
	private XStream xstream;
	
	public RemotePlaceService(String workplaceUrl) {
		this.httpCommunicator = new HttpCommunicator();
		httpCommunicator.init(workplaceUrl);
		init();
	}
	
	private void init(){
		xstream = new XStream();
		xstream.alias("action", XmlAction.class);
		xstream.alias("actionType",  IXmlActionType.class, VanillaPlaceService.ActionType.class);
		xstream.alias("argumentsHolder", XmlArgumentsHolder.class);
		xstream.alias("user", PlaceUser.class);
		xstream.alias("project", PlaceProject.class);
		xstream.alias("package", PlacePackage.class);
		xstream.alias("directory", PlaceImportDirectory.class);
		xstream.alias("item", PlaceImportItem.class);
	}
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public IUser authentifyUser(String name, String password) throws Exception {
		XmlAction op = new XmlAction(createArguments(name, password), VanillaPlaceService.ActionType.CONNECT);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (PlaceUser)xstream.fromXML(xml);
	}

	@Override
	public boolean createUserAndSendMail(IUser user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), VanillaPlaceService.ActionType.CREATE_USER);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Boolean)xstream.fromXML(xml);
	}

	@Override
	public List<IProject> getAvailableProjects(Integer userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), VanillaPlaceService.ActionType.GET_PACKAGES);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IProject>)xstream.fromXML(xml);
	}

	@Override
	public List<IProject> exportPackage(int userId, IProject project, IPackage vanillaPackage, 
			InputStream zipFile) throws Exception {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while((sz = zipFile.read(buf)) >= 0){
			bos.write(buf, 0, sz);
		}
		zipFile.close();
		
		byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());
		
		XmlAction op = new XmlAction(createArguments(userId, project, vanillaPackage, streamDatas), 
				VanillaPlaceService.ActionType.EXPORT_PACKAGE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IProject>)xstream.fromXML(xml);
	}

	@Override
	public InputStream importPackage(int userId, IPackage packageToImport) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, packageToImport), 
				VanillaPlaceService.ActionType.IMPORT_PACKAGE);

		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		byte[] encoded = (byte[])xstream.fromXML(xml);
		
		return new ByteArrayInputStream(Base64.decodeBase64(encoded));
	}

}
