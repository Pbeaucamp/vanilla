package bpm.vanilla.platform.core.remote.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.ICommentService;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteCommentService implements ICommentService {

	private HttpCommunicator httpCommunicator;
	private static XStream xstream;

	public RemoteCommentService(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	static {
		xstream = new XStream();
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public void addComment(int userId, CommentValue comment, int repId) throws Exception {
		XmlAction action = new XmlAction(createArguments(userId, comment, repId), ICommentService.ActionType.ADD_COMMENT_VALUE);
		httpCommunicator.executeAction(action, xstream.toXML(action), false);
	}

	@Override
	public void addComments(Validation validation, int userId, List<CommentValue> comments, int repId) throws Exception {
		XmlAction action = new XmlAction(createArguments(validation, userId, comments, repId), ICommentService.ActionType.ADD_COMMENT_VALUES);
		httpCommunicator.executeAction(action, xstream.toXML(action), false);
	}

	@Override
	public void modifyComments(Validation validation, int userId, List<CommentValue> comments, int repId, boolean isLastCommentUnvalidate) throws Exception {
		XmlAction action = new XmlAction(createArguments(validation, userId, comments, repId, isLastCommentUnvalidate), ICommentService.ActionType.MODIFY_COMMENT_VALUES);
		httpCommunicator.executeAction(action, xstream.toXML(action), false);
	}

	@Override
	public CommentDefinition getCommentDefinition(int itemId, int repId, String commentName) throws Exception {
		XmlAction action = new XmlAction(createArguments(itemId, repId, commentName), ICommentService.ActionType.GET_COMMENT_DEFINITION);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		if (xml.isEmpty()) {
			return null;
		}
		return (CommentDefinition) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CommentValue> getComments(int itemId, int repId, String commentName, List<CommentParameter> parameters) throws Exception {
		XmlAction action = new XmlAction(createArguments(itemId, repId, commentName, parameters), ICommentService.ActionType.GET_COMMENTS);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		if (xml.isEmpty()) {
			return new ArrayList<CommentValue>();
		}
		return (List<CommentValue>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CommentValue> getComments(int commentDefinitionId, int repId, int userId) throws Exception {
		XmlAction action = new XmlAction(createArguments(commentDefinitionId, repId, userId), ICommentService.ActionType.GET_COMMENTS_FOR_USER);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		if (xml.isEmpty()) {
			return new ArrayList<CommentValue>();
		}
		return (List<CommentValue>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CommentDefinition> getCommentDefinitions(int itemId, int repId) throws Exception {
		XmlAction action = new XmlAction(createArguments(itemId, repId), ICommentService.ActionType.GET_COMMENTS_DEFINITION);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		if (xml.isEmpty()) {
			return new ArrayList<CommentDefinition>();
		}
		return (List<CommentDefinition>) xstream.fromXML(xml);
	}

	@Override
	public CommentValue getCommentNotValidate(int commentDefinitionId, int repId) throws Exception {
		XmlAction action = new XmlAction(createArguments(commentDefinitionId, repId), ICommentService.ActionType.GET_COMMENT_NOT_VALIDATE);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		if (xml.isEmpty()) {
			return null;
		}
		return (CommentValue) xstream.fromXML(xml);
	}

	@Override
	public void validate(Validation validation, int userId, int groupId, int repId) throws Exception {
		XmlAction action = new XmlAction(createArguments(validation, userId, groupId, repId), ICommentService.ActionType.VALIDATE);
		httpCommunicator.executeAction(action, xstream.toXML(action), false);
	}

	@Override
	public void unvalidate(Validation validation, int userId, int groupId, int repId) throws Exception {
		XmlAction action = new XmlAction(createArguments(validation, userId, groupId, repId), ICommentService.ActionType.UNVALIDATE);
		httpCommunicator.executeAction(action, xstream.toXML(action), false);
	}

	@Override
	public void stopValidationProcess(Validation validation, int userId, int groupId, int repId) throws Exception {
		XmlAction action = new XmlAction(createArguments(validation, userId, groupId, repId), ICommentService.ActionType.STOP_VALIDATION_PROCESS);
		httpCommunicator.executeAction(action, xstream.toXML(action), false);
	}
}
