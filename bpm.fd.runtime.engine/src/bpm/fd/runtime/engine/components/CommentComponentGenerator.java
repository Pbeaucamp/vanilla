package bpm.fd.runtime.engine.components;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.comment.CommentOptions;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.api.core.model.components.definition.styledtext.ComponentStyledTextInput;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.fd.runtime.model.DashState;
import bpm.fd.runtime.model.ui.jsp.renderer.FormatedTextRenderer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.Comment;

public class CommentComponentGenerator {
	
	
	public static String generateJspBlock(int offset, ComponentComment comment, String outputParameterName, VanillaProfil vanillaProfil, DashState state)throws Exception{
		
		StringBuffer buf = new StringBuffer();
		
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		
		String event = generateEvents(comment, new HashMap<ElementsEventType, String>(){{put(ElementsEventType.onClick, "setLocation();");}}, false);
		
		buf.append("<div id=\"" + comment.getName() + "\">\n");
		
		if(vanillaProfil != null && vanillaProfil.getDirectoryItemId() != null) {
			
			buf.append(generateAddCommentPart(comment, vanillaProfil));
			
			if(((CommentOptions)comment.getOptions(CommentOptions.class)).isAllowAddComments()) {
			
				generateAddCommentPart(comment, buf, offset, outputParameterName, true, state);
				
				
			}
			buf.append("</div>\n");
			
			if(((CommentOptions)comment.getOptions(CommentOptions.class)).isShowComments()) {
			
				buf.append("<script type=\"text/javascript\">\n");
				buf.append("var comme = new Comment(\"" + vanillaProfil.getVanillaLogin() + "\", \"" + 
						vanillaProfil.getVanillaPassword() + "\", \"" + vanillaProfil.getVanillaGroupId() + 
						"\", " + vanillaProfil.getDirectoryItemId() + ", " + vanillaProfil.getRepositoryId() + ");\n");
							
				VanillaConfiguration cf = ConfigurationManager.getInstance().getVanillaConfiguration();

				int ezd = IRepositoryApi.FD_TYPE;
//				buf.append("<% IRepositoryApi socke = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(new BaseVanillaContext(\""+vanillaProfil.getVanillaUrl()+"\",\""+vanillaProfil.getVanillaLogin()+"\",\""+cf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)+"\"), (new Group).setId("+vanillaProfil.getVanillaGroupId()+"), (new RemoteVanillaPlatform(new BaseVanillaContext(\""+vanillaProfil.getVanillaUrl()+"\",\""+vanillaProfil.getVanillaLogin()+"\",\""+cf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)+"\"))).getVanillaRepositoryManager().getRepositoryById("+vanillaProfil.getRepositoryId()+"))));\n" +
//						"List<IComment> comments = socke.getDatasProviderService().getAll();\n" +
//						"out.write(\" var comms = new Array();\"); int i = 0;\n" +
//						"java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MM-yyyy\");\n" +
////						"bpm.birep.admin.connection.AdminAccess aa = new bpm.birep.admin.connection.AdminAccess(\"" + vanillaProfil.getVanillaUrl() + "\");" +
//						"for(IComment comm : comments) {\n" +
//						"	out.write(\"var com\" + i + \" = new Listcommentpart(\\\"\" + comm.getCreatorName() + \"\\\",\\\"\" + comm.getGroup() + \"\\\",\\\"\" + sdf.format(comm.getBeginDate()) + \"\\\",\\\"\" + comm.getComment().replaceAll(\"[\\r\\n]\", \"\") + \"\\\");\");\n" +
//						"	out.write(\"comms.push(com\" + i + \");\");i++;" +
//						"}\n" +
//						" out.write(\"comme.draw(\\\"" + comment.getName() + "\\\", " + ((CommentOptions)comment.getOptions(CommentOptions.class)).isAllowAddComments() + ", " + ((CommentOptions)comment.getOptions(CommentOptions.class)).isShowComments() + ", comms);\");%>");
				Group g = new Group();
				g.setId(vanillaProfil.getVanillaGroupId());
				bpm.vanilla.platform.core.beans.Repository r = new bpm.vanilla.platform.core.beans.Repository();
				r.setId(vanillaProfil.getRepositoryId());
				IVanillaContext vctx = new BaseVanillaContext(vanillaProfil.getVanillaUrl(), vanillaProfil.getVanillaLogin(), vanillaProfil.getVanillaPassword());
				IRepositoryContext ctx = new BaseRepositoryContext(vctx, 
						g, 
						r);
				IRepositoryApi api = new RemoteRepositoryApi(ctx);
				
				IVanillaAPI vapi = new RemoteVanillaPlatform(vctx);
				
				List<Comment> comments = api.getDocumentationService().getComments(vanillaProfil.getVanillaGroupId(), vanillaProfil.getDirectoryItemId(), Comment.ITEM);
				
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
				
				buf.append("var comms = new Array();\n");
				int i = 0;
				for(Comment comm : comments) {
					buf.append("var com" + i + " = new Listcommentpart('" + 
							vapi.getVanillaSecurityManager().getUserById(comm.getCreatorId()).getLogin() + "','" + "System'" +",'" + sdf.format(comm.getBeginDate()) + "','" + comm.getComment().replaceAll("[\\r\\n]", "") + "');\n");
					buf.append("comms.push(com" + i + ");\n");
					i++;
				}
				
				buf.append("comme.draw('" + comment.getName() + "', " + ((CommentOptions)comment.getOptions(CommentOptions.class)).isAllowAddComments() + ", " + ((CommentOptions)comment.getOptions(CommentOptions.class)).isShowComments() + ", comms);\n");

//						"List<IComment> comments = socke.getDatasProviderService().getAll();\n" +
//						"out.write(\" var comms = new Array();\"); int i = 0;\n" +
//						"java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MM-yyyy\");\n" +
////						"bpm.birep.admin.connection.AdminAccess aa = new bpm.birep.admin.connection.AdminAccess(\"" + vanillaProfil.getVanillaUrl() + "\");" +
//						"for(IComment comm : comments) {\n" +
//						"	out.write(\"var com\" + i + \" = new Listcommentpart(\\\"\" + comm.getCreatorName() + \"\\\",\\\"\" + comm.getGroup() + \"\\\",\\\"\" + sdf.format(comm.getBeginDate()) + \"\\\",\\\"\" + comm.getComment().replaceAll(\"[\\r\\n]\", \"\") + \"\\\");\");\n" +
//						"	out.write(\"comms.push(com\" + i + \");\");i++;" +
//						"}\n" +
//						" out.write(\"comme.draw(\\\"" + comment.getName() + "\\\", " + ((CommentOptions)comment.getOptions(CommentOptions.class)).isAllowAddComments() + ", " + ((CommentOptions)comment.getOptions(CommentOptions.class)).isShowComments() + ", comms);\");%>");
				
				
				buf.append("</script>\n");
			}
		}
		
		else {
			buf.append("Comments are not available. You need to export the dashboard to the repository in order to get comments. <br />\n");
			
			generateAddCommentPart(comment, buf, offset, outputParameterName, false, state);
			
			buf.append("</div>\n");
		}
		
		return buf.toString();
	}
	
	private static void generateAddCommentPart(ComponentComment comment, StringBuffer buf, int offset, String outputParameterName, boolean enable, DashState state) throws Exception {
		buf.append("	<input type=\"submit\" value=\"Add a comment\" " +
				"onClick=\"if(document.getElementById('" + comment.getName() + "_add').style.visibility == 'hidden') {\ndocument.getElementById('" + comment.getName() + "_add').style.visibility = 'visible';\ndocument.getElementById('" + comment.getName() + "_add').style.position = 'relative';\n} \nelse {\ndocument.getElementById('" + comment.getName() + "_add').style.visibility = 'hidden';\ndocument.getElementById('" + comment.getName() + "_add').style.position = 'absolute';}\"/>\n");
		
		buf.append("	<div id=\"" + comment.getName() + "_add\" style=\"visibility:hidden; position:absolute;\">\n");
		
		buf.append("<form method=\"POST\" id=\"" + comment.getName() + "_form\" action=\"\">\n");
		
		buf.append("<input type=\"hidden\" name=\"_commenttext_\" id=\"_commenttext_\"/>");
		buf.append("<input type=\"hidden\" name=\"_addcom_\" id=\"_addcom_\" value=\"true\"/>");
		
		ComponentStyledTextInput styledText = new ComponentStyledTextInput(comment.getName() + "_stText", null);
		
		FormatedTextRenderer t = new FormatedTextRenderer();
		
		buf.append(t.getHTML(new Rectangle(600, 400), styledText, state, null, false));
		
		if(enable) {
			buf.append("	<input type=\"submit\" value=\"Valid comment\" " +
					"onClick=\"addComment();\"/>\n");
			
			buf.append("<script type=\"text/javascript\">\n");
			buf.append("function addComment() {\n");
			buf.append("document.getElementById('" + comment.getName() + "_form').action = location.href;\n");
			buf.append("var ed = CKEDITOR.instances." + styledText.getId() + ".getData();\n");
			buf.append("document.getElementById('_commenttext_').value = ed;\n");
			buf.append("document.getElementById('" + comment.getName() + "_form').submit();\n");
			buf.append("}\n");
			buf.append("</script>\n");
		}
		else {
			buf.append("	<input type=\"submit\" value=\"Valid comment\" disabled=\"disabled\"/>\n");
		}
		
		buf.append("</form>\n");
		
		buf.append("	</div>\n");
	}

	private static Object generateAddCommentPart(ComponentComment comment, VanillaProfil vanillaProfil) {
		StringBuffer buf = new StringBuffer();
		
		VanillaConfiguration cf = ConfigurationManager.getInstance().getVanillaConfiguration();
//		buf.append("<% if(request.getParameter(\"_addcom_\") != null) {\n" +
//				"" +"IRepositoryApi socke = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(new BaseVanillaContext(\""+vanillaProfil.getVanillaUrl()+"\",\""+vanillaProfil.getVanillaLogin()+"\",\""+cf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)+"\"), (new Group).setId("+vanillaProfil.getVanillaGroupId()+"), (new RemoteVanillaPlatform(new BaseVanillaContext(new BaseVanillaContext(\""+vanillaProfil.getVanillaUrl()+"\",\""+vanillaProfil.getVanillaLogin()+"\",\""+cf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)+"\")).getVanillaRepositoryManager().getRepositoryById("+vanillaProfil.getRepositoryId()+"))));\n" +
//						"java.util.Date end = new java.util.Date();end.setYear(end.getYear() + 1);\n");
//		buf.append("socke.getDocumentationService().addAnnoteOnItem(IRepositoryApi.FD_TYPE, true, socke.getRepositoryService().getDirectoryItem("+vanillaProfil.getDirectoryItemId()+"), \"" + comment.getName() + "\", new java.util.Date(), end);}%>\n");
		
		return buf.toString();
	}

	/**
	 * generate the STring for events
	 * 
	 * @param element
	 * @param defaultEvents : contains the default behavior for the given Type
	 * @return
	 */
	private static String generateEvents(IBaseElement element, HashMap<ElementsEventType, String> defaultEvents, boolean pureHtml){
		StringBuffer buf = new StringBuffer();
		
		for(ElementsEventType type : element.getEventsType()){
			String sc = element.getJavaScript(type);
			if (sc != null && !"".equals(sc.trim())){
				buf.append(" " + type.name() + (pureHtml ? "=\"" : "=\\\"") + sc.replace("\r\n", "").replace("\n", ""));
				
				if (defaultEvents == null || defaultEvents.get(type) == null){
					buf.append((pureHtml ? "\"" : "\\\"") + " ");
					continue;
				}
				
			}
			if (defaultEvents != null && defaultEvents.get(type) != null){
				if (sc != null && !"".equals(sc.trim())){
					buf.append(";" );
				}
				else{
					buf.append(" " + type.name() + (pureHtml ? "=\"" : "=\\\""));
				}
				buf.append(defaultEvents.get(type));
				buf.append((pureHtml ? "\"" : "\\\"") + " ");
			}
			
		}
		
		return buf.toString();
	}
}
