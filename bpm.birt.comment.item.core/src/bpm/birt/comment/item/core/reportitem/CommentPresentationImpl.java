package bpm.birt.comment.item.core.reportitem;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.extension.IBaseResultSet;
import org.eclipse.birt.report.engine.extension.ReportItemPresentationBase;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition.TypeComment;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class CommentPresentationImpl extends ReportItemPresentationBase {

	private CommentItem commentItem;
	// private ExtendedItemHandle handle;

	private IVanillaAPI vanillaApi;

	@Override
	public void setModelObject(ExtendedItemHandle modelHandle) {
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
		String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		if (vanillaUrl != null && !vanillaUrl.isEmpty() && login != null && !login.isEmpty() && password != null && !password.isEmpty()) {
			this.vanillaApi = new RemoteVanillaPlatform(vanillaUrl, login, password);
		}

		try {
			commentItem = (CommentItem) modelHandle.getReportItem();
			// handle = modelHandle;
		} catch (ExtendedElementException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object onRowSets(IBaseResultSet[] results) throws BirtException {
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		int itemId = Integer.parseInt(context.getParameterValue(CommentDefinition.ITEM_ID_PARAMETER).toString());
		int repId = Integer.parseInt(context.getParameterValue(CommentDefinition.REP_ID_PARAMETER).toString());
		boolean displayComments = context.getParameterValue(CommentDefinition.DISPLAY_COMMENTS_PARAMETER).toString().equals("1");
		String commentName = commentItem.getItemHandle().getName();

		StringBuilder buf = new StringBuilder();
		buf.append("	<div>\n");
		if (displayComments) {
			if (itemId > 0 && repId > 0) {
				try {
					CommentDefinition commentDefinition = null;
					if (vanillaApi != null) {
						commentDefinition = vanillaApi.getCommentService().getCommentDefinition(itemId, repId, commentName);
					}
					List<CommentValue> comments = null;
					if (commentDefinition != null) {
						List<CommentParameter> parameters = getParameters(commentDefinition);
	
						comments = vanillaApi.getCommentService().getComments(itemId, repId, commentName, parameters);
					}
	
					boolean limit = commentItem.getLimit();
					int nbCommentToDisplay = commentItem.getNbComment();
					if (comments != null) {
						for (int i = 0; i < comments.size(); i++) {
							if (limit && i >= nbCommentToDisplay) {
								break;
							}
	
							CommentValue com = comments.get(i);
	
							buf.append("		<div style=\"margin-bottom: 15px\">\n");
							buf.append("			" + com.getValue());
							if (commentDefinition.getType() == TypeComment.RESTITUTION) {
								buf.append("			<div style=\"\">" + com.getUserName() + " - " + form.format(com.getCreationDate()) + "</div>\n");
							}
							buf.append("		</div>\n");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				buf.append("		<div style=\"border: 1px solid black;border-radius: 10px;-webkit-border-radius: 10px;-moz-border-radius: 10px;padding: 10px 2%;background: white;width: 92%;margin: 10px 2%;\">\n");
				buf.append("			The report is not saved on the repository <br /> or the comments are not supported.");
				buf.append("		</div>\n");
			}
		}

		buf.append("	</div>\n");

		return buf.toString();
	}

	private List<CommentParameter> getParameters(CommentDefinition commentDefinition) {
		if (commentDefinition.getParameters() != null) {
			for (CommentParameter parameter : commentDefinition.getParameters()) {
				Object parameterValue = context.getParameterValue(parameter.getParameterIdentifier());
				if (parameterValue != null) {
					parameter.setValue(parameterValue.toString());
				}
			}
		}
		return commentDefinition.getParameters();
	}

	public int getOutputType() {
		return OUTPUT_AS_HTML_TEXT;
	}
}
