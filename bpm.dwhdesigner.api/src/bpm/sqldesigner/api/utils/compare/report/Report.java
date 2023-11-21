package bpm.sqldesigner.api.utils.compare.report;

import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.utils.migration.CanApplyChanges;

public abstract class Report {
	protected boolean namesMatch;
	protected int score = -1;
	protected CanApplyChanges applyChanges = null;

	public boolean getNamesMatch() {
		return namesMatch;
	}

	public void setNamesMatch(boolean namesMatch) {
		this.namesMatch = namesMatch;
	}

	public abstract int evaluateMatches();

	public int getScore() {
		return score;
	}

	public List<RequestStatement> getRequests(Node toNode, Node fromNode) {
		return null;
	}

	public List<Report> getReportsChanges(List<Report> report) {
		return null;
	}

	public void applyChanges() {
		if (applyChanges != null) {
			applyChanges.applyChanges();
		}
	}

	public CanApplyChanges getApplyChanges() {
		return applyChanges;
	}
}
