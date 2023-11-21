package bpm.vanilla.platform.core.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassField.TypeField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.OperatorClassic;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.RulePatternComparison;
import bpm.vanilla.platform.core.beans.resources.RuleValueComparison;

public class SchemaHelper {

	/**
	 * Load the specified schema
	 * 
	 * @param schemaPath path to the schema
	 * @param schemaIdentifiant id of the schema
	 * @param cleanFields clean the field name like in D4C to match for the data validation
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static ClassDefinition loadSchema(String schemaPath, String schemaIdentifiant, boolean cleanFields) throws FileNotFoundException, IOException, ParseException {
		boolean isAPI = false;
		String apiUrl = null;
		if (schemaPath.contains("api.")) {
			isAPI = true;
			try(FileInputStream fis = new FileInputStream(schemaPath.toString())) {
				apiUrl = IOUtils.toString(fis);
			} catch (Exception e) {
				throw e;
			}
		}
		
		try (InputStream is = isAPI ? new URL(apiUrl).openStream() : new FileInputStream(schemaPath.toString()); 
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			String title = (String) jsonObject.get("title");

			ClassDefinition classSchema = new ClassDefinition();
			classSchema.setIdentifiant(schemaIdentifiant);

			// We add a general field (to define all fields)
			ClassField globalField = new ClassField("Global");
			globalField.setType(TypeField.STRING);
			classSchema.addField(globalField);

			// We go through all fields of the schema
			JSONArray fields = (JSONArray) jsonObject.get("fields");
			for (Object object : fields.toArray()) {
				JSONObject item = (JSONObject) object;

				String fieldName = (String) item.get("name");
				String cleanFieldName = cleanFieldName(fieldName);
				
				List<String> possibleNames = item.get("possibleNames") != null ? getEnumValues((JSONArray) item.get("possibleNames")) : null;
				
				String type = (String) item.get("type");
				String format = item.get("format") != null ? (String) item.get("format") : null;

				JSONObject constraints = (JSONObject) item.get("constraints");

				
				boolean required = constraints != null && constraints.get("required") != null ? (Boolean) constraints.get("required") : false;
				String pattern = constraints != null && constraints.get("pattern") != null ? (String) constraints.get("pattern") : null;
				List<String> enumValues = constraints != null && constraints.get("enum") != null ? getEnumValues((JSONArray) constraints.get("enum")) : null;
				Long minimum = constraints != null && constraints.get("minimum") != null ? (Long) constraints.get("minimum") : null;
				Long maximum = constraints != null && constraints.get("maximum") != null ? (Long) constraints.get("maximum") : null;

				ClassField myField = new ClassField(fieldName);
				myField.setCleanFieldName(cleanFieldName);
				myField.setPossibleNames(possibleNames);
				myField.setType(getType(type));
				myField.setFormat(format);
				myField.setRequired(required);

				if (pattern != null) {
					RulePatternComparison rulePattern = new RulePatternComparison();
					rulePattern.setRegex(pattern);
					
					// If schema is RGPD, we set that errorIfMatch to true
					if (schemaIdentifiant.equals("rgpd_schema")) {
						rulePattern.setErrorIfMatch(true);
					}

					ClassRule rule = new ClassRule();
					rule.setMainClassIdentifiant(title);
					rule.setType(TypeRule.PATTERN);
					rule.setRule(rulePattern);

					myField.addRule(rule);
				}

				if (enumValues != null && enumValues.size() > 0) {
					StringBuffer bufValues = new StringBuffer();
					boolean first = true;
					for (String value : enumValues) {
						if (!first) {
							bufValues.append(";");
						}
						bufValues.append(value);
						first = false;
					}
					
					RuleValueComparison ruleValue = new RuleValueComparison();
					ruleValue.setFirstOperator(OperatorClassic.IN);
					ruleValue.setFirstValue(bufValues.toString());

					ClassRule rule = new ClassRule();
					rule.setMainClassIdentifiant(title);
					rule.setType(TypeRule.VALUE_COMPARAISON);
					rule.setRule(ruleValue);

					myField.addRule(rule);
				}
				
				if (minimum != null) {
					RuleValueComparison ruleValue = new RuleValueComparison();
					ruleValue.setFirstOperator(OperatorClassic.SUP_OR_EQUAL);
					ruleValue.setFirstValue(minimum.toString());

					ClassRule rule = new ClassRule();
					rule.setMainClassIdentifiant(title);
					rule.setType(TypeRule.VALUE_COMPARAISON);
					rule.setRule(ruleValue);

					myField.addRule(rule);
				}
				
				if (maximum != null) {
					RuleValueComparison ruleValue = new RuleValueComparison();
					ruleValue.setFirstOperator(OperatorClassic.INF_OR_EQUAL);
					ruleValue.setFirstValue(maximum.toString());

					ClassRule rule = new ClassRule();
					rule.setMainClassIdentifiant(title);
					rule.setType(TypeRule.VALUE_COMPARAISON);
					rule.setRule(ruleValue);

					myField.addRule(rule);
				}

				classSchema.addField(myField);
			}

			return classSchema;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String cleanFieldName(String fieldName) {
		fieldName = Normalizer.normalize(fieldName, Normalizer.Form.NFD);
		fieldName = fieldName.replaceAll("[^\\p{ASCII}]", "");
		fieldName = fieldName.replaceAll("[^a-zA-Z0-9_]", "_");
		fieldName = fieldName.length() > 63 ? fieldName.substring(0, 62) : fieldName;
		fieldName = fieldName.toLowerCase();
	    return fieldName;
	}

	private static List<String> getEnumValues(JSONArray jsonArray) {
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < jsonArray.size(); i++) {
			values.add((String) jsonArray.get(i));
		}
		return values;
	}

	private static TypeField getType(String type) {
		switch (type) {
		case "string":
			return TypeField.STRING;
		case "number":
			return TypeField.NUMERIC;
		case "date":
			return TypeField.DATE;
		case "boolean":
			return TypeField.BOOLEAN;
		default:
			break;
		}
		return TypeField.STRING;
	}
}
