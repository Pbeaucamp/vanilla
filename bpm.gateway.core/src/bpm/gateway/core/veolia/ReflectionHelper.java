package bpm.gateway.core.veolia;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.ClassUtils;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassField.TypeField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.OperatorClassic;
import bpm.vanilla.platform.core.beans.resources.OperatorDate;
import bpm.vanilla.platform.core.beans.resources.OperatorListe;
import bpm.vanilla.platform.core.beans.resources.OperatorOperation;
import bpm.vanilla.platform.core.beans.resources.RuleClassColumnNull;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparison;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparisonBetweenColumn;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparisonChild;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparisonDate;
import bpm.vanilla.platform.core.beans.resources.RuleColumnDateComparison;
import bpm.vanilla.platform.core.beans.resources.RuleColumnOperation;
import bpm.vanilla.platform.core.beans.resources.RuleDBComparison;
import bpm.vanilla.platform.core.beans.resources.RuleExclusionValue;
import bpm.vanilla.platform.core.beans.resources.RulePatternComparison;
import bpm.vanilla.platform.core.beans.resources.RuleValueComparison;

public class ReflectionHelper {
	
	private static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

	public static ClassDefinition loadClass(String myClass) throws ClassNotFoundException {
		Class<?> classToDefine = Class.forName(myClass);
		Field[] fields = classToDefine.getDeclaredFields();

		ClassDefinition classDef = new ClassDefinition();
		classDef.setIdentifiant(myClass);
		loadFields(classDef, fields);
		return classDef;
	}

	private static void loadFields(ClassDefinition classDef, Field[] fields) {
		if (fields != null) {
			for (Field field : fields) {
				Type fieldType = field.getGenericType();
				if (fieldType instanceof ParameterizedType) {
					Type[] childTypes = ((ParameterizedType) fieldType).getActualTypeArguments();
					if (childTypes != null) {
						for (Type childType : childTypes) {
							if (childType instanceof Class<?>) {
								Class<?> childClass = (Class<?>) childType;
								loadFields(classDef, childClass.getDeclaredFields());
							}
						}
					}
				}
				else if (exclude(field.getType())) {
					ClassField myField = new ClassField(field.getName());
					myField.setType(getType(field.getType()));
					classDef.addField(myField);
				}
				else {
					ClassDefinition childClass = new ClassDefinition(field.getName(), field.getType().getName());
					classDef.addClass(childClass);

					loadFields(childClass, field.getType().getDeclaredFields());
				}
			}
		}
	}

	private static boolean exclude(Class<?> myClass) {
		return ClassUtils.isPrimitiveOrWrapper(myClass) || ClassUtils.isAssignable(myClass, String.class) || ClassUtils.isAssignable(myClass, Enum.class) || ClassUtils.isAssignable(myClass, BigDecimal.class) || ClassUtils.isAssignable(myClass, BigInteger.class) || ClassUtils.isAssignable(myClass, XMLGregorianCalendar.class) || ClassUtils.isAssignable(myClass, Date.class);
	}

	private static TypeField getType(Class<?> myClass) {
		if (ClassUtils.isAssignable(myClass, String.class)) {
			return TypeField.STRING;
		}
		else if (ClassUtils.isAssignable(myClass, Enum.class)) {
			return TypeField.ENUM;
		}
		else if (ClassUtils.isAssignable(myClass, Integer.class) || ClassUtils.isAssignable(myClass, BigDecimal.class) || ClassUtils.isAssignable(myClass, BigInteger.class) || ClassUtils.isAssignable(myClass, Double.class)) {
			return TypeField.NUMERIC;
		}
		else if (ClassUtils.isAssignable(myClass, XMLGregorianCalendar.class) || ClassUtils.isAssignable(myClass, Date.class)) {
			return TypeField.DATE;
		}
		return null;
	}

	public static void buildClassDefinitionWithRules(IVanillaAPI vanillaApi, ClassDefinition classDef, List<ClassRule> classRules, boolean loadData) throws Exception {
		if (classRules != null) {
			for (ClassRule rule : classRules) {
				if (loadData && rule.getType() == TypeRule.LISTE_DB_COMPARAISON) {
					RuleDBComparison ruleModel = (RuleDBComparison) rule.getRule();
					ruleModel.setValues(loadValues(vanillaApi, ruleModel));
				}

				String parentPath = rule.getParentPath();
				String[] paths = parentPath.split("/");

				ClassDefinition parent = classDef;
				for (int i = 0; i < paths.length; i++) {
					String path = paths[i];
					if (path.equals(classDef.getName())) {
						continue;
					}

					ClassDefinition classFound = findClassDefinition(path, parent);
					if (classFound == null) {
						ClassField field = findClassField(path, parent);
						if (field == null) {
							throwException(rule, "Impossible de trouver le champ avec le chemin '" + parentPath + "'");
						}
						
						// Checking if a rule exist in the ClassDefinition, mainly from schema that have pattern in constraints
						ClassRule existingRule = getExistingRule(field, rule);
						if (existingRule == null) {
							field.addRule(rule);
						}
						else {
							// If the rule exist, we load the modification from the user
							existingRule.copyDefinition(rule);
						}
						break;
					}
					else if (i == paths.length - 1) {
						classFound.addRule(rule);
					}
					else {
						parent = classFound;
					}

				}
			}
		}
	}
	
	private static ClassRule getExistingRule(ClassField field, ClassRule rule) {
		if (field.getRules() != null) {
			for (ClassRule existingRule : field.getRules()) {
				if (existingRule.match(rule)) {
					return existingRule;
				}
			}
		}
		return null;
	}

	private static List<String> loadValues(IVanillaAPI vanillaApi, RuleDBComparison ruleModel) throws Exception {
		try {
			Datasource datasource = vanillaApi.getVanillaPreferencesManager().getDatasourceById(ruleModel.getDatasourceId());
			Dataset dataset = vanillaApi.getVanillaPreferencesManager().getDatasetById(ruleModel.getDatasetId());
			String column = ruleModel.getColumnName();

			DatasourceJdbc jdbcSource = (DatasourceJdbc) datasource.getObject();
			String query = dataset.getRequest();

			VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);
			VanillaPreparedStatement rs = jdbcConnection.prepareQuery(query);
			ResultSet jdbcResult = rs.executeQuery(query);

			List<String> values = new ArrayList<>();
			while (jdbcResult.next()) {
				String value = jdbcResult.getString(column);
				values.add(value);
			}

			ConnectionManager.getInstance().returnJdbcConnection(jdbcConnection);
			return values;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Impossible de charger les données pour la règle: " + e.getMessage());
		}
	}

	private static ClassDefinition findClassDefinition(String className, ClassDefinition classParent) {
		if (classParent.getClasses() != null) {
			for (ClassDefinition classDef : classParent.getClasses()) {
				if (classDef.getName().equals(className)) {
					return classDef;
				}
			}
		}

		return null;
	}

	private static ClassField findClassField(String className, ClassDefinition classParent) {
		if (classParent == null || classParent.getFields() == null) {
			return null;
		}

		for (ClassField classField : classParent.getFields()) {
			if (classField.getName().equals(className)) {
				return classField;
			}
		}

		return null;
	}

	public static ClassDefinition getSelectedClassDefinition(String className, ClassDefinition mainClass) {
		if (mainClass.getClasses() != null) {
			for (ClassDefinition classDef : mainClass.getClasses()) {
				if (classDef.getIdentifiant().equals(className)) {
					return classDef;
				}

				ClassDefinition classFound = getSelectedClassDefinition(className, classDef);
				if (classFound != null) {
					return classFound;
				}
			}
		}

		return null;
	}

	public static void testRules(ClassDefinition classDefinition, Object item) throws Exception {
		if (classDefinition.getRules() != null) {
			for (ClassRule rule : classDefinition.getRules()) {
				checkRule(null, rule, item);
			}
		}

		if (classDefinition.getFields() != null) {
			for (ClassField classField : classDefinition.getFields()) {
				if (classField.getRules() != null) {
					for (ClassRule rule : classField.getRules()) {
						if (rule.isEnabled()) {
							String fieldName = ((ClassField) rule.getParent()).getName();

							ReflectionValue value = getValue(fieldName, item);
							checkRule(value, rule, item);
						}
					}
				}
			}
		}
	}

	private static void checkRule(ReflectionValue value, ClassRule rule, Object item) throws Exception {
		switch (rule.getType()) {
		case VALUE_COMPARAISON:
			checkValueComparison(value, rule);
			return;
		case COLUMN_COMPARAISON:
			checkColumnComparison(value, rule, item);
			return;
		case COLUMN_DATE_COMPARAISON:
			checkColumnDateComparison(value, rule, item);
			return;
		case LISTE_DB_COMPARAISON:
			checkListeDBComparison(value, rule);
			return;
		case PATTERN:
			checkPattern(value, rule, item);
			return;
		case CLASS_COLUMN_NULL:
			checkClassColumnNull(rule, item);
			return;
		case COLUMN_COMPARAISON_DATE:
			checkColumnComparisonDate(value, rule, item);
			return;
		case COLUMN_OPERATION:
			checkColumnOperation(value, rule, item);
			return;
		case COLUMN_COMPARAISON_BETWEEN_COLUMN:
			checkColumnComparisonBetweenColumn(value, rule, item);
			return;

		default:
			break;
		}

		throwException(rule, "La règle du type '" + rule.getType() + "' n'est pas supportée.");
	}

	private static void checkValueComparison(ReflectionValue value, ClassRule rule) throws Exception {
		RuleValueComparison ruleModel = (RuleValueComparison) rule.getRule();

		if (checkExclusionValue(ruleModel, value)) {
			return;
		}

		if (!checkValue(rule, value, ruleModel.getFirstOperator(), ruleModel.getFirstValue())) {
			throwException(rule, value.getValueAsString() + " " + ruleModel.getFirstOperator() + " " + ruleModel.getFirstValue() + " non respecté.");
		}

		if (ruleModel.hasLastValue() && !checkValue(rule, value, ruleModel.getLastOperator(), ruleModel.getLastValue())) {
			throwException(rule, value.getValueAsString() + " " + ruleModel.getLastOperator() + " " + ruleModel.getLastValue() + " non respecté.");
		}
	}

	private static void checkColumnComparison(ReflectionValue value, ClassRule rule, Object item) throws Exception {
		RuleColumnComparison ruleModel = (RuleColumnComparison) rule.getRule();

		if (ruleModel.getChilds() != null) {
			boolean conditionIsMet = true;
			String message = "";

			for (RuleColumnComparisonChild child : ruleModel.getChilds()) {
				ReflectionValue valueToCheck = getValue(child.getFieldName(), item);
				OperatorClassic operator = child.getOperator();
				String childValue = child.getValue();

				if (valueToCheck.isDefine() && checkValue(rule, valueToCheck, operator, childValue)) {
					if (!message.isEmpty()) {
						message += " et ";
					}
					
					message += valueToCheck.getValueAsString() + " " + operator + " " + childValue;
				}
				else {
					conditionIsMet = false;
					break;
				}
			}

			if (conditionIsMet) {
				if (ruleModel.isEqualToValue()) {
					// If we have multiple values
					if (ruleModel.getMainValue().contains(";")) {
						if (!checkINValue(value, ruleModel.getMainValue())) {
							throwException(rule, message + ". Les conditions sont respectées donc la valeur doit être incluse dans '" + ruleModel.getMainValue() + "'.");
						}
					}
					else if (!checkValue(rule, value, OperatorClassic.EQUAL, ruleModel.getMainValue())) {
						throwException(rule, message + ". Les conditions sont respectées donc la valeur doit être égale à '" + ruleModel.getMainValue() + "'.");
					}
				}
				else if (!value.isDefine()) {
					throwException(rule, message + ". Les conditions sont respectées donc la valeur ne doit pas être vide ou doit être incluse dans la liste de valeurs correspondantes.");
				}
			}
		}
	}

	private static void checkColumnDateComparison(ReflectionValue value, ClassRule rule, Object item) throws Exception {
		RuleColumnDateComparison ruleModel = (RuleColumnDateComparison) rule.getRule();

		if (value.getType() != TypeField.DATE) {
			throwException(rule, "La colonne n'est pas de type Date. La règle n'est pas valide.");
		}

		if (checkExclusionValue(ruleModel, value)) {
			return;
		}

		ReflectionValue valueToCheck = getValue(ruleModel.getFieldName(), item);
		if (valueToCheck.getType() != TypeField.DATE) {
			throwException(rule, "La colonne '" + ruleModel.getFieldName() + "' n'est pas de type Date. La règle n'est pas valide.");
		}

		if (value.isDefine() && valueToCheck.isDefine() && !checkValue(rule, value, ruleModel.getOperator(), valueToCheck)) {
			throwException(rule, value.getValueAsString() + " " + ruleModel.getOperator() + " " + valueToCheck.getValueAsString() + " non respecté.");
		}
	}

	private static boolean checkExclusionValue(RuleExclusionValue ruleModel, ReflectionValue value) {
		if (ruleModel.hasExclusionValue() && ruleModel.getExclusionValue() != null) {
			if (value.getType() == TypeField.NUMERIC) {
				try {
					double myValueDouble = value.getValueAsDouble();
					double valueDouble = Double.parseDouble(ruleModel.getExclusionValue());

					return myValueDouble == valueDouble;
				} catch (Exception e) {
				}
			}
			else if (value.getType() == TypeField.DATE) {
				try {
					String myValueDateAsString = value.getValueAsString();
					
					if (myValueDateAsString == null || myValueDateAsString.length() > 4) {
						myValueDateAsString = format1.format(value.getValueAsDate());
					}
					String valueDateAsString = ruleModel.getExclusionValue();

					return valueDateAsString.equals(myValueDateAsString);
				} catch (Exception e) {
				}
			}

			return ruleModel.getExclusionValue().equals(value.getValueAsString());
		}
		return false;
	}

	private static void checkListeDBComparison(ReflectionValue value, ClassRule rule) throws Exception {
		RuleDBComparison ruleModel = (RuleDBComparison) rule.getRule();

		List<String> values = ruleModel.getValues();
		if (!checkValue(rule, value, ruleModel.getOperator(), values)) {
			throwException(rule, value.getValueAsString() + " " + ruleModel.getOperator() + " la liste de valeurs n'est pas respecté.");
		}
	}

	private static void checkPattern(ReflectionValue value, ClassRule rule, Object item) throws Exception {
		RulePatternComparison ruleModel = (RulePatternComparison) rule.getRule();

		if (!checkValue(rule, value, ruleModel.getRegex())) {
			throwException(rule, value.getValueAsString() + " ne correspond pas à la regex '" + ruleModel.getRegex() + "'.");
		}
	}

	private static void checkClassColumnNull(ClassRule rule, Object item) throws Exception {
		RuleClassColumnNull ruleModel = (RuleClassColumnNull) rule.getRule();

		List<String> fieldNames = ruleModel.getFieldNames();
		if (fieldNames != null) {
			StringBuffer buf = new StringBuffer();

			boolean oneValueDefine = false;
			for (String fieldName : fieldNames) {
				buf.append("'" + fieldName + "' ");

				ReflectionValue valueToCheck = getValue(fieldName, item);
				if (valueToCheck.isDefine()) {
					oneValueDefine = true;
					break;
				}
			}

			if (!oneValueDefine) {
				throwException(rule, "Une colonne parmis les suivantes doit contenir une valeur " + buf.toString() + ". La règle n'est pas respectée.");
			}
		}
	}

	private static void checkColumnComparisonDate(ReflectionValue value, ClassRule rule, Object item) throws Exception {
		RuleColumnComparisonDate ruleModel = (RuleColumnComparisonDate) rule.getRule();

		Date dateValue = ruleModel.getValue();

		ReflectionValue valueToCheck = getValue(ruleModel.getFieldName(), item);

		if (checkExclusionValue(ruleModel, valueToCheck)) {
			return;
		}

		if (valueToCheck.getType() != TypeField.DATE) {
			throwException(rule, "La colonne '" + ruleModel.getFieldName() + "' n'est pas de type Date. La règle n'est pas valide.");
		}

		if (checkValue(rule, valueToCheck, ruleModel.getOperator(), dateValue) && !value.isDefine()) {
			throwException(rule, valueToCheck.getValueAsString() + " " + ruleModel.getOperator() + " " + dateValue + " est respecté donc la valeur ne peut pas être vide.");
		}
	}

	private static void checkColumnOperation(ReflectionValue value, ClassRule rule, Object item) throws Exception {
		RuleColumnOperation ruleModel = (RuleColumnOperation) rule.getRule();

		double valueToCheck = calculValue(rule, ruleModel.getOperatorOperation(), ruleModel.getFieldNames(), item);

		if (!checkValue(rule, value, ruleModel.getOperator(), valueToCheck)) {
			throwException(rule, value.getValueAsString() + " " + ruleModel.getOperator() + " " + valueToCheck + " non respecté.");
		}
	}

	private static void checkColumnComparisonBetweenColumn(ReflectionValue value, ClassRule rule, Object item) throws Exception {
		RuleColumnComparisonBetweenColumn ruleModel = (RuleColumnComparisonBetweenColumn) rule.getRule();

		if (checkExclusionValue(ruleModel, value)) {
			return;
		}

		ReflectionValue valueToCheck = getValue(ruleModel.getFieldName(), item);
		if (!checkValue(rule, value, ruleModel.getOperator(), valueToCheck)) {
			throwException(rule, value.getValueAsString() + " " + ruleModel.getOperator() + " à la valeur de " + ruleModel.getFieldName() + " (" + valueToCheck.getValueAsString() + ") non respecté.");
		}
	}

	private static ReflectionValue getValue(String fieldName, Object item) throws Exception {
		Field field = item.getClass().getDeclaredField(fieldName);
		if (field == null) {
			return null;
		}
		field.setAccessible(true);

		TypeField type = getType(field.getType());
		if (type == TypeField.ENUM) {
			try {
				try {
					Field valueField = field.getType().getDeclaredField("value");
					valueField.setAccessible(true);

					return new ReflectionValue(TypeField.STRING, valueField.get(field.get(item)));
				} catch (Exception e) {
					throw new Exception("Unable to get ValueField for '" + fieldName + "'", e);
				}
			} catch (Exception e) {
				return new ReflectionValue(TypeField.STRING, null);
			}
		}

		return new ReflectionValue(type, field.get(item));
	}

	private static double calculValue(ClassRule rule, OperatorOperation operatorOperation, List<String> fieldNames, Object item) throws Exception {
		double result = 0;
		if (fieldNames != null) {
			for (String fieldName : fieldNames) {
				ReflectionValue valueToCheck = getValue(fieldName, item);
				if (valueToCheck.isDefine() && valueToCheck.getType() == TypeField.NUMERIC) {
					double value = valueToCheck.getValueAsDouble();

					switch (operatorOperation) {
					case MULTIPLY:
						result = result * value;
						break;
					case SUM:
						result = result + value;
						break;

					default:
						break;
					}
				}
				// else {
				// throwException(rule, "La valeur de '" + fieldName +
				// "' est vide ou n'est pas un champ numérique. La règle n'est pas valide.");
				// }
			}
		}
		return result;
	}

	private static boolean checkValue(ClassRule rule, ReflectionValue myValue, OperatorClassic operator, String value) throws Exception {
		if (!myValue.isDefine()) {
			throwException(rule, "La valeur ne peut pas être vide.");
		}

		if (operator == OperatorClassic.IN) {
			return checkINValue(myValue, value);
		}

		boolean isNumeric = false;

		double myValueDouble = 0;
		double valueDouble = 0;

		String myValueString = "";
		String valueString = "";
		if (myValue.getType() == TypeField.NUMERIC) {
			try {
				isNumeric = true;

				myValueDouble = myValue.getValueAsDouble();
				valueDouble = Double.parseDouble(value.toString());
			} catch (Exception e) {
				isNumeric = false;
			}
		}
		else if (myValue.getType() == TypeField.DATE) {
			try {
				myValueString = myValue.getValueAsString().substring(0, 4);
			} catch (Exception e) {
				throwException(rule, "La valeur doit être de type année.");
			}
		}

		myValueString = myValue.getValueAsString();
		valueString = value.toString();

		return checkValue(operator, isNumeric, myValueDouble, valueDouble, myValueString, valueString);
	}

	private static boolean checkINValue(ReflectionValue myValue, String value) throws Exception {
		String[] values = value.split(";");
		if (values != null) {
			for (String val : values) {
				if (myValue.getValueAsString().equals(val)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean checkValue(ClassRule rule, ReflectionValue myValue, OperatorDate operator, ReflectionValue valueToCheck) throws Exception {
		return checkDate(operator, myValue.getValueAsDate(), valueToCheck.getValueAsDate());
	}

	private static boolean checkValue(ClassRule rule, ReflectionValue myValue, OperatorClassic operator, ReflectionValue valueToCheck) throws Exception {
		if (!myValue.isDefine()) {
			throwException(rule, "La valeur ne peut pas être vide.");
		}

		boolean isNumeric = false;

		double myValueDouble = 0;
		double valueDouble = 0;

		String myValueString = "";
		String valueString = "";
		if (myValue.getType() == TypeField.NUMERIC) {
			try {
				isNumeric = true;

				myValueDouble = myValue.getValueAsDouble();
				valueDouble = valueToCheck.getValueAsDouble();
			} catch (Exception e) {
				isNumeric = false;
			}
		}

		if (myValue.getType() == TypeField.DATE) {
			try {
				myValueString = myValue.getValueAsString().substring(0, 4);

				try {
					valueString = valueToCheck.getValueAsString().substring(0, 4);
				} catch (Exception ex) {
					valueString = valueToCheck.getValueAsString();
				}
			} catch (Exception e) {
				throwException(rule, "La valeur doit être de type année.");
				myValueString = myValue.getValueAsString();
				valueString = valueToCheck.getValueAsString();
			}
		}
		else {
			myValueString = myValue.getValueAsString();
			valueString = valueToCheck.getValueAsString();
		}

		if (operator == OperatorClassic.IN) {
			return checkINValue(myValue, valueString);
		}

		return checkValue(operator, isNumeric, myValueDouble, valueDouble, myValueString, valueString);
	}

	private static boolean checkValue(OperatorClassic operator, boolean isNumeric, double myValueDouble, double valueDouble, String myValueString, String valueString) throws Exception {
		switch (operator) {
		case INF:
			return isNumeric ? myValueDouble < valueDouble : myValueString.compareTo(valueString) < 0;
		case INF_OR_EQUAL:
			return isNumeric ? myValueDouble <= valueDouble : myValueString.compareTo(valueString) <= 0;
		case EQUAL:
			return isNumeric ? myValueDouble == valueDouble : myValueString.compareTo(valueString) == 0;
		case NOT_EQUAL:
			return isNumeric ? myValueDouble != valueDouble : checkNotEqualValue(myValueString, valueString);
		case SUP_OR_EQUAL:
			return isNumeric ? myValueDouble >= valueDouble : myValueString.compareTo(valueString) >= 0;
		case SUP:
			return isNumeric ? myValueDouble >= valueDouble : myValueString.compareTo(valueString) >= 0;
		case IN:
			return false;
		case CONTAINS:
			return myValueString.contains(valueString);
		case REGEX:
			return checkValue(myValueString, valueString);
		default:
			break;
		}
		return false;
	}

	private static boolean checkNotEqualValue(String myValue, String value) throws Exception {
		String[] values = value.split(";");
		if (values != null) {
			for (String val : values) {
				if (myValue.equals(val)) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkValue(ClassRule rule, ReflectionValue myValue, String regex) throws Exception {
		if (!myValue.isDefine()) {
			throwException(rule, "La valeur ne peut pas être vide.");
		}

		return checkValue(myValue.getValueAsString(), regex);
	}

	private static boolean checkValue(String myValue, String regex) {
		return myValue.matches(regex);
	}

	private static boolean checkValue(ClassRule rule, ReflectionValue myValue, OperatorListe operator, List<String> values) throws Exception {
		if (!myValue.isDefine()) {
			return true;
		}

		String myValueString = myValue.getValueAsString();

		switch (operator) {
		case IN:
			if (values != null) {
				for (String val : values) {
					if (val.equals(myValueString)) {
						return true;
					}
				}
			}
			return false;
		case OUT:
			if (values != null) {
				for (String val : values) {
					if (val.equals(myValueString)) {
						return false;
					}
				}
			}
			return true;
		default:
			break;
		}
		return false;
	}

	private static boolean checkValue(ClassRule rule, ReflectionValue myValue, OperatorDate operator, Date value) throws Exception {
		if (!myValue.isDefine()) {
			throwException(rule, "La valeur ne peut pas être vide.");
		}

		return checkDate(operator, myValue.getValueAsDate(), value);
	}

	private static boolean checkDate(OperatorDate operator, Date myValue, Date value) {
		switch (operator) {
		case BEFORE:
			return myValue.before(value);
		case EQUAL:
			return myValue.equals(value);
		case AFTER:
			return myValue.after(value);
		case BEFORE_OR_EQUAL:
			return myValue.before(value) || myValue.equals(value);
		case AFTER_OR_EQUAL:
			return myValue.after(value) || myValue.equals(value);
		default:
			break;
		}
		return false;
	}

	private static boolean checkValue(ClassRule rule, ReflectionValue myValue, OperatorClassic operator, double value) throws Exception {
		if (!myValue.isDefine()) {
			throwException(rule, "La valeur ne peut pas être vide.");
		}

		if (myValue.getType() != TypeField.NUMERIC) {
			throwException(rule, "La valeur doit être un champ numérique.");
		}

		double myValueDouble = 0;
		try {
			myValueDouble = myValue.getValueAsDouble();
		} catch (Exception e) {
			e.printStackTrace();
			throwException(rule, "La valeur doit être un champ numérique.");
		}

		switch (operator) {
		case INF:
			return myValueDouble < value;
		case INF_OR_EQUAL:
			return myValueDouble <= value;
		case EQUAL:
			return myValueDouble == value;
		case NOT_EQUAL:
			return myValueDouble != value;
		case SUP_OR_EQUAL:
			return myValueDouble >= value;
		case SUP:
			return myValueDouble >= value;
		case IN:
		case CONTAINS:
		case REGEX:
			return false;
		default:
			break;
		}
		return false;
	}

	private static void throwException(ClassRule rule, String message) throws Exception {
		throw new Exception(rule.getParentName() + ": " + message);
	}
}
