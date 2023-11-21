package bpm.vanilla.web.service;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface IVanillaWebService {

	/**
	 * Used to load data
	 * The data will be treated using the transformation definition
	 * The service return xml or a filePath depending on the output type
	 * @param itemId the service transformation definition
	 * @param user the vanilla user
	 * @param password the vanilla password
	 * @param xml the xml to be parsed by the service
	 * @return
	 */
	String loadData(int itemId, String user, String password, int groupId, int repId, String xml);
	
}
