package bpm.document.management.core.utils;

import java.util.List;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.FormField;
import bpm.document.management.core.model.FormFieldValue;

public class DocumentMetadata {

	private Documents doc;
	private List<FormField> fields;
	private List<FormFieldValue> values;

	public Documents getDoc() {
		return doc;
	}

	public void setDoc(Documents doc) {
		this.doc = doc;
	}

	public List<FormField> getFields() {
		return fields;
	}

	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}

	public List<FormFieldValue> getValues() {
		return values;
	}

	public void setValues(List<FormFieldValue> values) {
		this.values = values;
	}

}
