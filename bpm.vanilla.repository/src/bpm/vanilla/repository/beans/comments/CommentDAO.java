package bpm.vanilla.repository.beans.comments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition.TypeComment;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.comments.CommentValue.CommentStatus;
import bpm.vanilla.platform.core.beans.comments.CommentValueParameter;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class CommentDAO extends HibernateDaoSupport {

	public CommentDefinition save(CommentDefinition comment) {
		// if (comment.getId() > 0) {
		// // return update(comment);
		// }
		// else {
		Integer commentId = (Integer) getHibernateTemplate().save(comment);
		comment.setId(commentId);

		addCommentParameters(comment.getParameters(), comment.getId());

		return comment;
		// }
	}

	private void addCommentParameters(List<CommentParameter> parameters, int commentDefinitionId) {
		if (parameters != null) {
			for (CommentParameter parameter : parameters) {
				parameter.setCommentDefinitionId(commentDefinitionId);

				Integer parameterId = (Integer) getHibernateTemplate().save(parameter);
				parameter.setId(parameterId);
			}
		}
	}

	public void delete(CommentDefinition value) {
		getHibernateTemplate().delete(value);

		deleteCommentParameters(value.getId());
	}

	private void deleteCommentParameters(int commentValueId) {
		List<CommentParameter> parameters = getCommentParameters(commentValueId);
		if (parameters != null) {
			for (CommentParameter param : parameters) {
				getHibernateTemplate().delete(param);
			}
		}
	}

	public CommentValue save(CommentValue value) {
		if (value.getId() > 0) {
			return update(value);
		}
		else {
			Integer commentId = (Integer) getHibernateTemplate().save(value);
			value.setId(commentId);

			addCommentParameterValues(value.getParameterValues(), value.getId());

			CommentDefinition comDef = getCommentDefinition(value.getCommentId());
			if (comDef != null && comDef.getType() == TypeComment.VALIDATION) {
				CommentValue commentToUpdate = getCommentNotValidate(comDef.getId());
				if (commentToUpdate != null && commentToUpdate.getId() != commentId) {
					commentToUpdate.setStatus(CommentStatus.OLD);
					save(commentToUpdate);
				}
			}

			return value;
		}
	}

	public CommentValue update(CommentValue value) {
		getHibernateTemplate().update(value);

		deleteCommentParameterValues(value.getId());
		addCommentParameterValues(value.getParameterValues(), value.getId());

		return value;
	}

	private void addCommentParameterValues(List<CommentValueParameter> parameterValues, int commentValueId) {
		if (parameterValues != null) {
			for (CommentValueParameter parameterValue : parameterValues) {
				parameterValue.setCommentValueId(commentValueId);
				;
				Integer parameterValueId = (Integer) getHibernateTemplate().save(parameterValue);
				parameterValue.setId(parameterValueId);
			}
		}
	}

	public void delete(CommentValue value) {
		getHibernateTemplate().delete(value);

		deleteCommentParameterValues(value.getId());
	}

	private void deleteCommentParameterValues(int commentValueId) {
		List<CommentValueParameter> parameterValues = getCommentParameterValues(commentValueId);
		if (parameterValues != null) {
			for (CommentValueParameter paramValue : parameterValues) {
				getHibernateTemplate().delete(paramValue);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public CommentDefinition getCommentDefinition(int itemId, String commentName) {
		List<CommentDefinition> definitions = (List<CommentDefinition>) getHibernateTemplate().find("from CommentDefinition where itemId=" + itemId + " and name='" + commentName + "'");
		if (definitions != null && !definitions.isEmpty()) {
			loadCommentDefinition(definitions.get(0));
			return definitions.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<CommentDefinition> getCommentDefinitions(int itemId) {
		List<CommentDefinition> definitions = (List<CommentDefinition>) getHibernateTemplate().find("from CommentDefinition where itemId=" + itemId);
		loadCommentDefinitions(definitions);
		return definitions != null ? definitions : new ArrayList<CommentDefinition>();
	}

	@SuppressWarnings("unchecked")
	public CommentDefinition getCommentDefinition(int commentId) {
		List<CommentDefinition> definitions = (List<CommentDefinition>) getHibernateTemplate().find("from CommentDefinition where id=" + commentId);
		loadCommentDefinitions(definitions);
		return definitions != null && !definitions.isEmpty() ? definitions.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public List<CommentParameter> getCommentParameters(int commentDefinitionId) {
		List<CommentParameter> parameters = (List<CommentParameter>) getHibernateTemplate().find("from CommentParameter where commentDefinitionId=" + commentDefinitionId);
		return parameters != null ? parameters : new ArrayList<CommentParameter>();
	}

	@SuppressWarnings("unchecked")
	public CommentValue getCommentNotValidate(int commentDefinitionId) {
		List<CommentValue> values = (List<CommentValue>) getHibernateTemplate().find("from CommentValue where commentId=" + commentDefinitionId + " and (status=" + CommentStatus.NOT_VALIDATE.getType() + " or status=" + CommentStatus.UNVALIDATE.getType() + ")");
		if (values != null && !values.isEmpty()) {
			loadCommentValue(values.get(0));
			return values.get(0);
		}
		return null;
	}

//	@SuppressWarnings("unchecked")
//	public List<CommentValue> getCommentNotValidateByUser(int userId) {
//		
//		SELECT rpy_comment_value.*, rpy_validation.id 
//		FROM rpy_comment_value 
//		INNER JOIN rpy_comment_definition
//		INNER JOIN rpy_validation
//		WHERE rpy_validation.item_id = rpy_comment_definition.itemId
//		AND rpy_validation.is_actif = true
//		AND rpy_validation.is_valid = false
//		AND rpy_comment_definition.id = rpy_comment_value.comment_id 
//		AND rpy_comment_definition.type = 0 
//		AND (rpy_comment_value.status = 1 OR rpy_comment_value.status = 3)
//		AND rpy_comment_value.user_id != 2;
//		
//		StringBuffer buf = new StringBuffer();
//		buf.append("SELECT c ");
//		buf.append("FROM CommentValue c, CommentDefinition d, Validation v ");
//		buf.append("WHERE v.itemId = d.itemId ");
//		buf.append("AND v.isActif = true ");
//		buf.append("AND v.isValid = false ");
//		buf.append("AND d.id = c.commentId ");
//		buf.append("AND d.type = " + TypeComment.VALIDATION.getType());
//		buf.append("AND (c.status = " + CommentStatus.NOT_VALIDATE.getType() + " or c.status = " + CommentStatus.UNVALIDATE.getType() + ") ");
//		buf.append("AND c.userId != " + userId);
//		
//		return (List<CommentValue>) getHibernateTemplate().find(buf.toString());
//	}

	@SuppressWarnings("unchecked")
	public List<CommentValue> getComments(int itemId, String commentName, List<CommentParameter> parameters) {
		CommentDefinition commentDefinition = getCommentDefinition(itemId, commentName);
		if (commentDefinition != null) {
			// String parameterQuery = prepareParameterQuery(commentDefinition,
			// parameters);

			List<CommentValue> values = (List<CommentValue>) getHibernateTemplate().find("from CommentValue cv where cv.commentId=" + commentDefinition.getId() + " and (cv.status=" + CommentStatus.VALIDATE.getType() + " or cv.status=" + CommentStatus.NOT_VALIDATE.getType() + " or cv.status=" + CommentStatus.UNVALIDATE.getType() + ")");
			loadCommentValues(values);
			values = filterWithParameters(commentDefinition, parameters, values);
			return values != null ? values : new ArrayList<CommentValue>();
		}
		return new ArrayList<CommentValue>();
	}

	public List<CommentValue> getComments(int commentDefinitionId, int repId, int userId) {
		CommentDefinition commentDefinition = getCommentDefinition(commentDefinitionId);
		if (commentDefinition != null) {
			List<CommentValue> values = (List<CommentValue>) getHibernateTemplate().find("from CommentValue cv WHERE cv.commentId=" + commentDefinition.getId() + " and cv.userId=" + userId + " and cv.status=" + CommentStatus.VALIDATE.getType());
			sortComments(values);
			loadCommentValues(values);
			return values != null ? values : new ArrayList<CommentValue>();
		}
		return new ArrayList<CommentValue>();
	}

	private void sortComments(List<CommentValue> values) {
		if (values != null) {
			Collections.sort(values, new Comparator<CommentValue>() {
				@Override
				public int compare(CommentValue comment1, CommentValue comment2) {
					return comment2.getCreationDate().compareTo(comment1.getCreationDate());
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	public List<CommentValueParameter> getCommentParameterValues(int commentValueId) {
		List<CommentValueParameter> parameterValues = (List<CommentValueParameter>) getHibernateTemplate().find("from CommentValueParameter where commentValueId=" + commentValueId);
		return parameterValues != null ? parameterValues : new ArrayList<CommentValueParameter>();
	}

	private List<CommentValue> filterWithParameters(CommentDefinition commentDefinition, List<CommentParameter> parameters, List<CommentValue> values) {
		List<CommentValue> valuesToReturn = new ArrayList<CommentValue>();
		if (parameters != null) {
			for (CommentValue value : values) {
				boolean isValid = true;
				for (CommentParameter param : parameters) {

					boolean found = false;
					for (CommentValueParameter valueParameter : value.getParameterValues()) {
						if (param != null && valueParameter != null && param.getId() == valueParameter.getCommentParamId() && param.getValue().equals(valueParameter.getValue())) {
							found = true;
							break;
						}
					}

					if (!found) {
						isValid = false;
						break;
					}
				}

				if (isValid) {
					valuesToReturn.add(value);
				}
			}
		}
		else {
			valuesToReturn.addAll(values);
		}
		return valuesToReturn;
	}

	private void loadCommentDefinition(CommentDefinition commentDefinition) {
		List<CommentParameter> parameters = getCommentParameters(commentDefinition.getId());
		commentDefinition.setParameters(parameters);
	}

	private void loadCommentDefinitions(List<CommentDefinition> commentDefinitions) {
		if (commentDefinitions != null) {
			for (CommentDefinition commentDefinition : commentDefinitions) {
				loadCommentDefinition(commentDefinition);
			}
		}
	}

	private void loadCommentValue(CommentValue commentValue) {
		List<CommentValueParameter> parameterValues = getCommentParameterValues(commentValue.getId());
		commentValue.setParameterValues(parameterValues);
	}

	private void loadCommentValues(List<CommentValue> commentValues) {
		if (commentValues != null) {
			for (CommentValue commentValue : commentValues) {
				loadCommentValue(commentValue);
			}
		}
	}

	public void updateAll(List<CommentDefinition> commentDefinitions, int itemId) {

		List<CommentDefinition> existing = getCommentDefinitions(itemId);

		List<CommentDefinition> toRemove = new ArrayList<CommentDefinition>();

		LOOK: for (CommentDefinition com : existing) {
			for (CommentDefinition newCom : commentDefinitions) {
				if (com.getName().equals(newCom.getName())) {
					newCom.setId(com.getId());
					continue LOOK;
				}
			}
			toRemove.add(com);
		}

		deleteAll(toRemove);

		for (CommentDefinition newComment : commentDefinitions) {
			List<CommentParameter> newParams = newComment.getParameters();

			List<CommentParameter> oldParams = getCommentParameters(newComment.getId());

			if (!hasSameParameters(newParams, oldParams)) {

				delete(newComment);
				deleteAll(oldParams);

				newComment.setId(0);
				save(newComment);
			}

		}

	}

	private boolean hasSameParameters(List<CommentParameter> newParams, List<CommentParameter> oldParams) {
		if (newParams.size() != oldParams.size()) {
			return false;
		}

		for (CommentParameter n : newParams) {
			boolean found = false;
			for (CommentParameter o : oldParams) {
				if (n.getParameterIdentifier().equals(o.getParameterIdentifier())) {
					found = true;
					break;
				}
			}

			if (!found) {
				return false;
			}
		}

		return true;
	}
}
