package bpm.workflow.runtime.model.activities.vanilla;

import java.util.Date;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;

public class ResetGedIndexActivity extends AbstractActivity implements IComment {

	private String comment;
	private static int number = 0;
	private boolean rebuildIndex;

	public ResetGedIndexActivity() {
		number++;
	}

	public ResetGedIndexActivity(String name) {
		number++;
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
	}

	@Override
	public IActivity copy() {
		ResetGedIndexActivity copy = new ResetGedIndexActivity();
		copy.setComment(comment);
		copy.setDescription(description);
		copy.setName(name);
		copy.setParent(parent);
		copy.setPositionHeight(height);
		copy.setPositionWidth(width);
		copy.setPositionX(xPos);
		copy.setPositionY(yPos);
		copy.setRelativePositionY(yRel);
		copy.setRebuildIndex(rebuildIndex);
		return copy;
	}

	@Override
	public String getProblems() {
		return "";
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public void setComment(String text) {
		comment = text;
	}

	public void setRebuildIndex(boolean rebuild) {
		this.rebuildIndex = rebuild;
	}

	public boolean isRebuildIndex() {
		return this.rebuildIndex;
	}

	public void setRebuild(String rebuild) {
		this.rebuildIndex = Boolean.parseBoolean(rebuild);
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();

		e.setName("resetgedactivity");
		if(comment != null) {
			e.addElement("comment").setText(comment);
		}

		e.addElement("rebuild").setText(rebuildIndex + "");

		return e;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {

			try {
				IGedComponent ged = new RemoteGedComponent(workflowInstance.getRepositoryApi().getContext().getVanillaContext());
				if(rebuildIndex) {
					ged.rebuildGedIndex();
				}
				else {
					ged.resetGedIndex();
				}

				activityResult = true;
			} catch(Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}

			super.finishActivity();
		}

	}
}
