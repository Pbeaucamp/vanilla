package bpm.vanilla.platform.core.wrapper.servlets40.accessibility;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.PublicUrl;

public interface IPublicUrlRunner {
	public String runObject(HttpServletResponse resp, PublicUrl publicUrl, IObjectIdentifier identifier, HashMap<String, String> parameters) throws Exception;

}
