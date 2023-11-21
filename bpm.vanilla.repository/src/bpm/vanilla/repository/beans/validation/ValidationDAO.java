package bpm.vanilla.repository.beans.validation;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.validation.UserValidation;
import bpm.vanilla.platform.core.beans.validation.UserValidation.UserValidationStatus;
import bpm.vanilla.platform.core.beans.validation.UserValidation.UserValidationType;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.beans.validation.ValidationCircuit;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ValidationDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Validation> getValidations(boolean includeInactive) {
		String queryInactive = includeInactive ? "" : " AND isActif = true";

		List<Validation> validations = (List<Validation>) getHibernateTemplate().find("from Validation" + queryInactive);
		loadValidations(validations);
		return validations;
	}

	@SuppressWarnings("unchecked")
	public Validation findValidation(int itemId) {
		List<Validation> l = getHibernateTemplate().find("from Validation where itemId=" + itemId);
		if (l != null && !l.isEmpty()) {
			loadValidation(l.get(0));
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Validation findByPrimaryKey(int id) {
		List<Validation> l = getHibernateTemplate().find("from Validation where id=" + id);
		if (l != null && !l.isEmpty()) {
			loadValidation(l.get(0));
			return l.get(0);
		}
		return null;
	}
	
	public void updateUserValidation(int validationId, int oldUserId, int newUserId, UserValidationType type) throws Exception {
		Validation validation = findByPrimaryKey(validationId);
		if (validation != null) {
			if (type == UserValidationType.COMMENTATOR) {
				List<UserValidation> users = validation.getCommentators();
				if (users != null) {
					for (UserValidation user : users) {
						if (user.getUserId() == oldUserId) {
							user.setUserId(newUserId);
							getHibernateTemplate().update(user);
							return;
						}
					}
				}
			}
			else if (type == UserValidationType.VALIDATOR) {
				List<UserValidation> users = validation.getValidators();
				if (users != null) {
					for (UserValidation user : users) {
						if (user.getUserId() == oldUserId) {
							user.setUserId(newUserId);
							getHibernateTemplate().update(user);
							return;
						}
					}
				}
			}
		}
		
		throw new Exception("Next user for this validation not found with ID " + oldUserId);
	}

	@SuppressWarnings("unchecked")
	public List<Validation> getValidationByStartEtl(int itemId) {
		List<Validation> validations = (List<Validation>) getHibernateTemplate().find("from Validation where startEtlId=" + itemId);
		loadValidations(validations);
		return validations;
	}

	@SuppressWarnings("unchecked")
	public List<UserValidation> getCommentators(int parentId, UserValidationStatus status) {
		List<UserValidation> userValidations = (List<UserValidation>) getHibernateTemplate().find("from UserValidation where parentId=" + parentId + " and type=" + UserValidationType.COMMENTATOR.getType() + " and status=" + status.getType() + " order by userOrder");
		return userValidations != null ? userValidations : new ArrayList<UserValidation>();
	}

	@SuppressWarnings("unchecked")
	public List<UserValidation> getValidators(int parentId, UserValidationStatus status) {
		List<UserValidation> userValidations = (List<UserValidation>) getHibernateTemplate().find("from UserValidation where parentId=" + parentId + " and type=" + UserValidationType.VALIDATOR.getType() + " and status=" + status.getType());
		return userValidations != null ? userValidations : new ArrayList<UserValidation>();
	}

	@SuppressWarnings("unchecked")
	public List<Validation> getValidationToComment(int userId) {
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT DISTINCT v");
		buf.append(" FROM Validation v, UserValidation uv");
		buf.append(" WHERE v.id = uv.parentId");
		buf.append(" AND v.isValid = false");
		buf.append(" AND v.isActif = true");
		buf.append(" AND uv.type = " + UserValidationType.COMMENTATOR.getType());
		buf.append(" AND uv.status = " + UserValidationStatus.VALIDATION.getType());
		buf.append(" AND uv.userId = " + userId);

		List<Validation> validations = (List<Validation>) getHibernateTemplate().find(buf.toString());
		loadValidations(validations);
		return validations;
	}

	public Validation save(Validation validation) {
		if (validation.getId() > 0) {
			return update(validation);
		}
		else {
			Integer validationId = (Integer) getHibernateTemplate().save(validation);
			validation.setId(validationId);

			addUserValidations(validation.getCommentators(), validationId, UserValidationStatus.VALIDATION);
			addUserValidations(validation.getValidators(), validationId, UserValidationStatus.VALIDATION);

			return validation;
		}
	}

	private Validation update(Validation validation) {
		getHibernateTemplate().update(validation);

		deleteCommentators(validation.getId(), UserValidationStatus.VALIDATION);
		deleteValidators(validation.getId(), UserValidationStatus.VALIDATION);

		addUserValidations(validation.getCommentators(), validation.getId(), UserValidationStatus.VALIDATION);
		addUserValidations(validation.getValidators(), validation.getId(), UserValidationStatus.VALIDATION);

		return validation;
	}

	public void delete(Validation validation, UserValidationStatus status) {
		getHibernateTemplate().delete(validation);

		deleteCommentators(validation.getId(), UserValidationStatus.VALIDATION);
		deleteValidators(validation.getId(), UserValidationStatus.VALIDATION);
	}

	private void addUserValidations(List<UserValidation> userValidations, Integer validationId, UserValidationStatus status) {
		if (userValidations != null) {
			for (UserValidation userValidation : userValidations) {
				userValidation.setParentId(validationId);
				userValidation.setStatus(status);

				Integer userValidationId = (Integer) getHibernateTemplate().save(userValidation);
				userValidation.setId(userValidationId);
			}
		}
	}

	private void deleteUserValidations(List<UserValidation> userValidations) {
		if (userValidations != null) {
			for (UserValidation user : userValidations) {
				getHibernateTemplate().delete(user);
			}
		}
	}

	private void deleteCommentators(int parentId, UserValidationStatus status) {
		List<UserValidation> commentators = getCommentators(parentId, status);
		deleteUserValidations(commentators);
	}

	private void deleteValidators(int parentId, UserValidationStatus status) {
		List<UserValidation> validators = getValidators(parentId, status);
		deleteUserValidations(validators);
	}

	private void loadValidations(List<Validation> validations) {
		if (validations != null) {
			for (Validation validation : validations) {
				loadValidation(validation);
			}
		}
	}

	private void loadValidation(Validation validation) {
		List<UserValidation> commentators = getCommentators(validation.getId(), UserValidationStatus.VALIDATION);
		validation.setCommentators(commentators);

		List<UserValidation> validators = getValidators(validation.getId(), UserValidationStatus.VALIDATION);
		validation.setValidators(validators);
	}

	@SuppressWarnings("unchecked")
	public List<ValidationCircuit> getValidationCircuits() {
		List<ValidationCircuit> circuits = (List<ValidationCircuit>) getHibernateTemplate().find("from ValidationCircuit");
		loadValidationCircuits(circuits);
		return circuits;
	}

	private void loadValidationCircuits(List<ValidationCircuit> circuits) {
		if (circuits != null) {
			for (ValidationCircuit circuit : circuits) {
				loadValidationCircuit(circuit);
			}
		}
	}

	private void loadValidationCircuit(ValidationCircuit circuit) {
		List<UserValidation> commentators = getCommentators(circuit.getId(), UserValidationStatus.CIRCUIT);
		circuit.setCommentators(commentators);

		List<UserValidation> validators = getValidators(circuit.getId(), UserValidationStatus.CIRCUIT);
		circuit.setValidators(validators);
	}

	public ValidationCircuit manageValidationCircuit(ValidationCircuit circuit, ManageAction action) {
		switch (action) {
		case SAVE:
			Integer circuitId = (Integer) getHibernateTemplate().save(circuit);
			circuit.setId(circuitId);

			addUserValidations(circuit.getCommentators(), circuitId, UserValidationStatus.CIRCUIT);
			addUserValidations(circuit.getValidators(), circuitId, UserValidationStatus.CIRCUIT);

			break;
		case SAVE_OR_UPDATE:
			if (circuit.getId() > 0) {
				getHibernateTemplate().update(circuit);
			}
			else {
				circuitId = (Integer) getHibernateTemplate().save(circuit);
				circuit.setId(circuitId);
			}

			deleteCommentators(circuit.getId(), UserValidationStatus.CIRCUIT);
			deleteValidators(circuit.getId(), UserValidationStatus.CIRCUIT);

			addUserValidations(circuit.getCommentators(), circuit.getId(), UserValidationStatus.CIRCUIT);
			addUserValidations(circuit.getValidators(), circuit.getId(), UserValidationStatus.CIRCUIT);

			break;
		case UPDATE:
			getHibernateTemplate().update(circuit);

			deleteCommentators(circuit.getId(), UserValidationStatus.CIRCUIT);
			deleteValidators(circuit.getId(), UserValidationStatus.CIRCUIT);

			addUserValidations(circuit.getCommentators(), circuit.getId(), UserValidationStatus.CIRCUIT);
			addUserValidations(circuit.getValidators(), circuit.getId(), UserValidationStatus.CIRCUIT);

			break;
		case DELETE:
			getHibernateTemplate().delete(circuit);

			deleteCommentators(circuit.getId(), UserValidationStatus.CIRCUIT);
			deleteValidators(circuit.getId(), UserValidationStatus.CIRCUIT);
			break;
		default:
			break;
		}

		return circuit;
	}
}
