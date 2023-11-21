package bpm.sqldesigner.api.utils.compare.report;

import java.util.ArrayList;
import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.Type;
import bpm.sqldesigner.api.utils.migration.CanApplyChanges;
import bpm.sqldesigner.api.utils.migration.TypeUpdateRequestFactory;

public class CompareColumnsReport extends Report {

	public static final int FULLMATCHES = 100;

	protected Column columnA;
	protected Column columnB;

	private boolean typesMatch;
	private boolean defaultValuesMatch;
	private boolean sizesMatch;
	private boolean nullablesMatch;
	private boolean pKMatch;
	private boolean unsignedMatch;
	private boolean targetPKMatch;

	private static final int[] COEFS = { 3, // name
			5, // type
			2, // defaultValue
			1, // size
			1, // nullable
			3, // primaryKey
			1, // unsigned
			3, // targetPrimaryKey
	};

	public CompareColumnsReport(Column columnA, Column columnB) {
		this.columnA = columnA;
		this.columnB = columnB;
	}

	public CompareColumnsReport() {

	}

	@Override
	public int evaluateMatches() {
		score = 0;
		if (namesMatch)
			score += COEFS[0];
		if (typesMatch)
			score += COEFS[1];
		if (defaultValuesMatch)
			score += COEFS[2];
		if (sizesMatch)
			score += COEFS[3];
		if (nullablesMatch)
			score += COEFS[4];
		if (pKMatch)
			score += COEFS[5];
		if (unsignedMatch)
			score += COEFS[6];
		if (targetPKMatch)
			score += COEFS[7];

		return (score * FULLMATCHES) / getSum();
	}

	private int getSum() {
		int sum = 0;
		for (int i : COEFS)
			sum += i;
		return sum;
	}

	public void setTypesMatch(boolean typesMatch) {
		this.typesMatch = typesMatch;
	}

	public void setDefaultValuesMatch(boolean defaultValuesMatch) {
		this.defaultValuesMatch = defaultValuesMatch;
	}

	public void setSizesMatch(boolean sizesMatch) {
		this.sizesMatch = sizesMatch;
	}

	public void setNullablesMatch(boolean nullablesMatch) {
		this.nullablesMatch = nullablesMatch;
	}

	public void setPKMatch(boolean match) {
		pKMatch = match;
	}

	public void setUnsignedMatch(boolean unsignedMatch) {
		this.unsignedMatch = unsignedMatch;
	}

	public void setTargetPKMatch(boolean targetPKMatch) {
		this.targetPKMatch = targetPKMatch;
	}

	@Override
	public List<RequestStatement> getRequests(Node toNode, Node fromNode) {
		if (score == -1)
			evaluateMatches();

		List<RequestStatement> listRequests = new ArrayList<RequestStatement>();
		boolean isTarget = true;

		if (toNode instanceof DatabaseCluster) {
			isTarget = columnA.getClusterName().equals(toNode.getName());
		} else if (toNode instanceof Catalog) {
			isTarget = columnA.getTable().getSchema().getCatalog().equals(
					toNode);
		} else if (toNode instanceof Schema) {
			isTarget = columnA.getTable().getSchema().equals(toNode);
		} else if (toNode instanceof Table) {
			isTarget = columnA.getTable().equals(toNode);
		} else if (toNode instanceof Column) {
			isTarget = columnA.equals(toNode);
		}

		if (!typesMatch || !defaultValuesMatch || !nullablesMatch
				|| !unsignedMatch) {
			if (isTarget) {
				listRequests.add(TypeUpdateRequestFactory.createRequest(
						columnB, columnA.getTypeString()));
				applyChanges = new CanApplyChanges() {

					public void applyChanges() {
						Type type = columnB.getType();
						Type typeA = columnA.getType();
						type.setId(typeA.getId());
						type.setName(typeA.getName());

						columnB.setDefaultValue(columnA.getDefaultValue());

						columnB.setNullable(columnA.isNullable());

						columnB.setUnsigned(columnA.isUnsigned());

					}
				};
			} else {
				listRequests.add(TypeUpdateRequestFactory.createRequest(
						columnA, columnB.getTypeString()));
				applyChanges = new CanApplyChanges() {

					public void applyChanges() {
						Type type = columnA.getType();
						Type typeB = columnB.getType();
						type.setId(typeB.getId());
						type.setName(typeB.getName());

						columnA.setDefaultValue(columnB.getDefaultValue());

						columnA.setNullable(columnB.isNullable());

						columnA.setUnsigned(columnB.isUnsigned());

					}
				};
			}
		}
		// if (!namesMatch) {
		// if (isTarget)
		// listRequests.add(NameUpdateRequestFactory.createRequest(
		// columnB, columnA.getName()));
		// else
		// listRequests.add(NameUpdateRequestFactory.createRequest(
		// columnA, columnB.getName()));
		//
		// }

		return listRequests;
	}

	@Override
	public List<Report> getReportsChanges(List<Report> report) {
		if (score == -1)
			evaluateMatches();

		report.add(this);
		return report;
	}
}
