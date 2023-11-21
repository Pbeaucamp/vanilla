package bpm.workflow.runtime.resources;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;

/**
 * Interface for an object taken from the repository
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class BiRepositoryObject {

	private int id;
	protected List<String> parameters;
	private String itemName = "";

	protected void setParameterNames() {
	};

	/**
	 * do not use, only for parsing XML
	 */
	public BiRepositoryObject() {
	}

	/**
	 * Create an object taken from the BiRepository
	 * 
	 * @param id
	 *            == directoryItemId
	 * @param repository
	 *            Server
	 */
	public BiRepositoryObject(int id) {
		this.id = id;

	}

	/**
	 * 
	 * @return the ID of the directory item linked to this object
	 */
	public int getId() {
		return id;
	}

	/**
	 * do not use, only for Digester
	 * 
	 * @param id
	 */
	public void setId(String id) {
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {

		}
	}

	/**
	 * 
	 * @return the name of the item
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Set the name of the item
	 * 
	 * @param itemName
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Add a parameter
	 * 
	 * @param p
	 */
	public void addParameter(String p) {
		if (parameters == null) {
			parameters = new ArrayList<String>();
		}
		parameters.add(p);
	}

	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("biRepositoryObject");
		e.addElement("id").setText(id + "");

		if (!itemName.equalsIgnoreCase("")) {
			e.addElement("itemname").setText(itemName);
		}
		if (parameters != null && !parameters.isEmpty()) {
			e.addElement("parameters");
			for (String p : parameters) {
				e.element("parameters").addElement("parameter").setText(p);
			}
		}
		return e;
	}

	/**
	 * 
	 * @return all the parameters of the object
	 * @throws Exception
	 */
	public List<String> getParameters(IRepositoryApi sock) throws Exception {
		if (parameters == null || parameters.isEmpty()) {
			parameters = new ArrayList<String>();

			RepositoryItem item = sock.getRepositoryService().getDirectoryItem(id);
			if (this instanceof ReportObject) {
				RemoteVanillaParameterComponent remote = new RemoteVanillaParameterComponent(sock.getContext().getVanillaContext());
				List<VanillaGroupParameter> params = remote.getParameters(new ReportRuntimeConfig(new ObjectIdentifier(sock.getContext().getRepository().getId(), id), null, sock.getContext().getGroup().getId()));
				if (parameters != null && params.size() > 0) {
					for (VanillaGroupParameter gp : params) {
						for (VanillaParameter p : gp.getParameters()) {
							parameters.add(p.getName());
						}
					}
				}
			}
			else {
				for (bpm.vanilla.platform.core.repository.Parameter p : sock.getRepositoryService().getParameters(item)) {
					parameters.add(p.getName());
				}
			}
		}

		return parameters;
	}

}
