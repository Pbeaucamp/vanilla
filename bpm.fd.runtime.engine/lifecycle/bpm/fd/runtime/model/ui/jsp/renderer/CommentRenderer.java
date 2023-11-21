package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.comment.CommentOptions;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.DashState;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class CommentRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentComment>{

	private SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
	
	public String getHTML(Rectangle layout, ComponentComment comment,
			DashState state, IResultSet datas, boolean refresh) {
		
		Integer directoryItemId = state.getDashInstance().getDashBoard().getMeta().getIdentifier().getDirectoryItemId();
		
		int repId = state.getDashInstance().getDashBoard().getMeta().getIdentifier().getRepositoryId();
		Group group = state.getDashInstance().getGroup();
		Repository repository = new Repository();
		repository.setId(repId);
		
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		
		IVanillaContext ctx = new BaseVanillaContext(vanillaUrl, state.getDashInstance().getUser().getLogin(), state.getDashInstance().getUser().getPassword());
		
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ctx);
		IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(ctx, group, repository));
		
		StringBuffer buf = new StringBuffer();
		if(!refresh) {
			buf.append(getComponentDefinitionDivStart(layout, comment));
		}
		try {
			
			if (directoryItemId > 0 && repId > 0) {
				try {
					Validation validation = repositoryApi.getRepositoryService().getValidation(directoryItemId);
					
					//We don't display comment if the validation is valid
					if (validation == null || !validation.isValid()) {
						CommentDefinition commentDefinition = vanillaApi.getCommentService().getCommentDefinition(directoryItemId, repId, comment.getName());
						List<CommentValue> comments = null;
						if(commentDefinition != null) {
							List<CommentParameter> parameters = getParameters(commentDefinition, state, comment);
			
							comments = vanillaApi.getCommentService().getComments(directoryItemId, repId, comment.getName(), parameters);
						}
	
						boolean limit = ((CommentOptions)comment.getOptions(CommentOptions.class)).isLimitComment();
						int nbCommentToDisplay = ((CommentOptions)comment.getOptions(CommentOptions.class)).getLimit();
						if(comments != null) {
							for (int i = 0; i < comments.size(); i++) {
								if (limit && i >= nbCommentToDisplay) {
									break;
								}
								
								CommentValue com = comments.get(i);
								
								buf.append("		<div style=\"border: 1px solid black;border-radius: 10px;-webkit-border-radius: 10px;-moz-border-radius: 10px;padding: 10px 2%;background: white;width: 92%;margin: 10px 2%;\">\n");
								buf.append("			" + com.getValue());
								if(!((CommentOptions)comment.getOptions(CommentOptions.class)).isValidation()) {
									buf.append("			<div style=\"\">" + com.getUserName() + " - " + form.format(com.getCreationDate()) + "</div>\n");
								}				
								buf.append("		</div>\n");
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				buf.append("		<div style=\"border: 1px solid black;border-radius: 10px;-webkit-border-radius: 10px;-moz-border-radius: 10px;padding: 10px 2%;background: white;width: 92%;margin: 10px 2%;\">\n");
				buf.append("			Le rapport n'a pas encore été sauvegardé sur le repository, <br /> ou les commentaires ne sont pas supportés.");
				buf.append("		</div>\n");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!refresh) {
			buf.append("</div>");
		}
		
		if(refresh) {
			return buf.toString();
		}
		
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + comment.getName() + "\"]= new FdLabel(\"" + comment.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		
		return buf.toString();
	}

	private List<CommentParameter> getParameters(CommentDefinition commentDefinition, DashState state, ComponentComment comment) {
		if (commentDefinition.getParameters() != null) {
			for (CommentParameter parameter : commentDefinition.getParameters()) {
				
				for(ComponentParameter p : comment.getParameters()) {
					if(p.getName().equals(parameter.getParameterIdentifier())) {
						
						Component provider = (Component)state.getDashInstance().getDashBoard().getDesignParameterProvider(p);
						
						String pValue = state.getComponentValue(provider.getName());
						if (pValue != null) {
							parameter.setValue(pValue.toString());
						}
						break;
					}
				}
				

			}
		}
		return commentDefinition.getParameters();
	}

	@Override
	public String getJavaScriptFdObjectVariable(ComponentComment definition) {
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + definition.getName() + "\"]= new FdLabel(\"" + definition.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}

}
