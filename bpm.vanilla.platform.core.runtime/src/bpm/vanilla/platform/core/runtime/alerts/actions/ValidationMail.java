package bpm.vanilla.platform.core.runtime.alerts.actions;

import java.io.InputStream;
import java.util.HashMap;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.validation.UserValidation;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.components.system.MailConfig;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.tools.MailHelper;

public class ValidationMail {

	public static void sendMailToNextCommentator(IVanillaAPI vanillaApi, Validation validation, RepositoryItem item, Integer lastCommentator, boolean isUnvalidate) throws Exception {
		try {
			User user = getNextCommentator(vanillaApi, validation, lastCommentator);
			if (user == null) {
				sendMailToNextValidator(vanillaApi, validation, item, null);
				return;
			}
			
			IMailConfig config = null;
			if (isUnvalidate) {
				config = getMailConfigForCommentatorAfterUnvalidate(validation, item, user);
			}
			else {
				config = getMailConfigForCommentator(validation, item, user);
			}
			MailHelper.sendEmail(config, new HashMap<String, InputStream>());
		} catch (Throwable t) {
			t.printStackTrace();
			//We deactivate the exception if the mail fail
//			throw new Exception("Unable to send a mail to notify the next commentator : " + t.getMessage());
		}
	}

	public static void sendMailToNextValidator(IVanillaAPI vanillaApi, Validation validation, RepositoryItem item, Integer lastCommentator) throws Exception {
		if (validation.getValidators() != null && !validation.getValidators().isEmpty()) {
			for (UserValidation validator : validation.getValidators()) {
				User user = getUser(vanillaApi, validator.getUserId());
				try {
					IMailConfig config = getMailConfigForValidator(validation, item, user);
					MailHelper.sendEmail(config, new HashMap<String, InputStream>());
				} catch (Throwable t) {
					t.printStackTrace();
					throw new Exception("Unable to send a mail to notify the validation is on user '" + user.getLogin() + "' : " + t.getMessage());
				}
			}
		}
		//If there is no validator, we send the mail to the admin of the validation
		else {
			User user = getUser(vanillaApi, validation.getAdminUserId());
			try {
				IMailConfig config = getMailConfigForValidator(validation, item, user);
				MailHelper.sendEmail(config, new HashMap<String, InputStream>());
			} catch (Throwable t) {
				t.printStackTrace();
				throw new Exception("Unable to send a mail to notify the validation is on user '" + user.getLogin() + "' : " + t.getMessage());
			}
		}
	}

	public static void sendFinalMail(IVanillaAPI vanillaApi, Validation validation, RepositoryItem item) throws Exception {
		if (validation.getAdminUserId() > 0) {
			User user = getUser(vanillaApi, validation.getAdminUserId());
			try {
				IMailConfig config = getMailConfigFinalForAdmin(validation, item, user);
				MailHelper.sendEmail(config, new HashMap<String, InputStream>());
			} catch (Throwable t) {
				t.printStackTrace();
				throw new Exception("Unable to send a mail to notify the validation is on user '" + user.getLogin() + "' : " + t.getMessage());
			}
		}
	}

	public static void sendMailErrorToAdmin(IVanillaAPI vanillaApi, Validation validation, RepositoryItem item, boolean firstStep) throws Exception {
		if (validation.getAdminUserId() > 0) {
			User user = getUser(vanillaApi, validation.getAdminUserId());
			try {
				IMailConfig config = getMailConfigErrorForAdmin(validation, item, user, firstStep);
				MailHelper.sendEmail(config, new HashMap<String, InputStream>());
			} catch (Throwable t) {
				t.printStackTrace();
				throw new Exception("Unable to send a mail to notify the validation is on user '" + user.getLogin() + "' : " + t.getMessage());
			}
		}
	}

	private static User getNextCommentator(IVanillaAPI vanillaApi, Validation validation, Integer lastCommentator) throws Exception {
		if (validation.getCommentators() != null) {
			boolean found = false;
			for (UserValidation commentator : validation.getCommentators()) {
				if (lastCommentator == null) {
					return getUser(vanillaApi, commentator.getUserId());
				}

				if (found) {
					return getUser(vanillaApi, commentator.getUserId());
				}

				if (lastCommentator.equals(commentator.getUserId())) {
					found = true;
				}
			}
		}
		return null;
	}

	private static User getUser(IVanillaAPI vanillaApi, int userId) throws Exception {
		return vanillaApi.getVanillaSecurityManager().getUserById(userId);
	}

	private static IMailConfig getMailConfigForCommentator(Validation validation, RepositoryItem item, User user) {

		String subject = "Rapport '" + item.getName() + "' disponible pour commentaire";

		StringBuffer buf = new StringBuffer();
		buf.append("Bonjour,<br/><br/>");
		buf.append("Le rapport '" + item.getName() + "' est disponible pour commentaire. Vous pouvez y acc�der depuis le portail.<br/><br/>");
		buf.append("N�oubliez pas qu�une fois vos commentaires ajout�s vous ne pourrez plus les modifier, sauf invalidation des utilisateurs responsables de la validation de cette restitution.<br/><br/>");
		buf.append("Cordialement,<br/>");
		buf.append("Vanilla");

		return new MailConfig(user.getBusinessMail(), "no-reply", buf.toString(), subject, true);
	}

	private static IMailConfig getMailConfigForCommentatorAfterUnvalidate(Validation validation, RepositoryItem item, User user) {

		String subject = "Rapport '" + item.getName() + "' disponible pour commentaire";

		StringBuffer buf = new StringBuffer();
		buf.append("Bonjour,<br/><br/>");
		buf.append("suite � une invalidation d'un ou plusieurs commentaires pr�sents dans la restitution '" + item.getName() + "', nous vous invitons � saisir � nouveau les commentaires depuis le portail sur ce rapport.<br/><br/>");
		buf.append("Si l'invalidation ne concerne pas vos commentaires, veuillez simplement revalider vos commentaires sans les modifier depuis le portail.<br/><br/>");
		buf.append("Cordialement,<br/>");
		buf.append("Vanilla");

		return new MailConfig(user.getBusinessMail(), "no-reply", buf.toString(), subject, true);
	}

	private static IMailConfig getMailConfigForValidator(Validation validation, RepositoryItem item, User user) {
		String subject = "Rapport '" + item.getName() + "' disponible pour validation";

		StringBuffer buf = new StringBuffer();
		buf.append("Bonjour,<br/><br/>");
		buf.append("Des commentaires ont �t� ajout�s sur le rapport '" + item.getName() + "' dont vous avez la responsabilit� de validateur. Vous pouvez y acc�der depuis le portail.<br/><br/>");
		buf.append("Votre validation concernera tous les commentaires du rapport et rendra disponibles la version � jour de cette restitution pour l�ensemble des personnes y ayant acc�s.<br/><br/>");
		buf.append("Cordialement,<br/>");
		buf.append("Vanilla");
		return new MailConfig(user.getBusinessMail(), "no-reply", buf.toString(), subject, true);
	}

	private static IMailConfig getMailConfigErrorForAdmin(Validation validation, RepositoryItem item, User user, boolean firstStep) {

		String subject = "Run error";
		StringBuffer buf = new StringBuffer();
		if (firstStep) {
			buf.append("Bonjour,<br/><br/>");
			buf.append("une erreur est survenue lors de la mise � jour des donn�es concernant le rapport '" + item.getName() + "', emp�chant l'initialisation du cycle de validation de cette restitution.<br/><br/>");
			buf.append("Nous vous invitons � contacter l'�quipe technique afin de r�soudre ce probl�me.<br/><br/>");
			buf.append("Cordialement,<br/>");
			buf.append("Vanilla");
		}
		else {
			buf.append("Bonjour,<br/><br/>");
			buf.append("une erreur est survenue lors de la validation des donn�es concernant le rapport '" + item.getName() + "', emp�chant la mise � disposition de la derni�re version de cette restitution � l'ensemble des utilisateurs concern�s.<br/><br/>");
			buf.append("Nous vous invitons � contacter l'�quipe technique afin de r�soudre ce probl�me.<br/><br/>");
			buf.append("Cordialement,<br/>");
			buf.append("Vanilla");
		}

		return new MailConfig(user.getBusinessMail(), "no-reply", buf.toString(), subject, true);
	}

	private static IMailConfig getMailConfigFinalForAdmin(Validation validation, RepositoryItem item, User user) {

		String subject = "Version � jour du rapport '" + item.getName() + "' disponible";

		StringBuffer buf = new StringBuffer();
		buf.append("Bonjour,<br/><br/>");
		buf.append("Suite � votre validation la version � jour du rapport '" + item.getName() + "' est maintenant disponible pour l�ensemble des personnes ayant acc�s � cette restitution.<br/><br/>");
		buf.append("Cordialement,<br/>");
		buf.append("Vanilla");

		return new MailConfig(user.getBusinessMail(), "no-reply", buf.toString(), subject, true);
	}
}
