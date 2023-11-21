package bpm.gateway.core.transformations.webservice;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunWebServiceInput;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.service.IService;

public class WebServiceInput extends AbstractTransformation {

	private String webServiceUrl;
	private String methodName;
	private String login;
	private String password;

	private DefaultStreamDescriptor descriptor;

	private List<IService> parameters = new ArrayList<IService>();

	public WebServiceInput() {
		addPropertyChangeListener(this);
	}

	@Override
	public void initDescriptor() {
		try {
			descriptor = (DefaultStreamDescriptor) WebServiceHelper.getDescriptor(this);
			setInited();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("webServiceInput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		if (webServiceUrl != null) {
			e.addElement("webServiceUrl").setText(webServiceUrl);
		}

		if (methodName != null) {
			e.addElement("methodName").setText(methodName);
		}

		if (login != null) {
			e.addElement("login").setText(login);
		}

		if (password != null) {
			e.addElement("password").setText(password);
		}

		if (parameters != null) {
			Element paramsElem = e.addElement("parameters");
			for (IService param : parameters) {
				Element paramElem = paramsElem.addElement("parameter");
				paramElem.addElement("name").setText(param.getName());
				paramElem.addElement("type").setText(String.valueOf(param.getType()));
				paramElem.addElement("value").setText(param.getValue());
			}
		}

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunWebServiceInput(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}

	}

	public Transformation copy() {
		WebServiceInput copy = new WebServiceInput();
		copy.setName("copy of " + name);
		copy.setDescription(description);
		copy.setWebServiceUrl(webServiceUrl);
		copy.setMethodName(methodName);
		copy.setLogin(login);
		copy.setPassword(password);

		return copy;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Web Service Url : " + webServiceUrl + "\n");
		buf.append("Method Name : " + methodName + "\n");
		buf.append("Login : " + login + "\n");
		buf.append("Password : ******\n");

		return buf.toString();
	}

	public String getWebServiceUrl() {
		return webServiceUrl;
	}

	public void setWebServiceUrl(String webServiceUrl) {
		this.webServiceUrl = webServiceUrl;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
		try {
			descriptor = (DefaultStreamDescriptor) WebServiceHelper.getDescriptor(this);
			fireProperty(PROPERTY_INPUT_CHANGED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<IService> getParameters() {
		return parameters;
	}

	public void setParameters(List<IService> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(WebServiceParameter param) {
		if (parameters == null) {
			parameters = new ArrayList<IService>();
		}
		parameters.add(param);
	}
}
