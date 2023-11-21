package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.api.runtime.utils.Constants;

public class CubeResultComponent {

	private String name;
	private CubeLevel level;
	private String id;
	private List<CubeResultComponent> children;
	private CubeResultComponent memberContent;
	private List<CubeResultData> datas;

	public CubeResultComponent(List<List<CubeCell>> rawResult, List<String> measList, String axis, Position2D posMember, Position2D posValue) {

		if (rawResult.get(posMember.getX()).get(posMember.getY()).isOLAPMember()) {
			CubeElementCell memCell = (CubeElementCell) rawResult.get(posMember.getX()).get(posMember.getY());
			name = memCell.getName();
			id = memCell.getId();
			level = memCell.getCubeMember().getCubeLvl();
			if (memCell.isHasMember() && memCell.isDrilled()) {
				loadChildMembers(rawResult, measList, axis, posMember, posValue);
			}
		}

		// we trying to find the position(posnextMemX,posnextMemY) of the next
		// dimension member item
		int posnextMemX = posMember.getX();
		int posnextMemY = posMember.getY();

		switch (axis) {
		case Constants.AXIS_X:
			posnextMemX++;
			while ((posnextMemX < rawResult.size()) && (rawResult.get(posnextMemX).get(posnextMemY) instanceof CubeNullCell)) {
				posnextMemX++;
			}
			break;
		case Constants.AXIS_Y:
			posnextMemY++;
			while ((posnextMemY < rawResult.get(0).size()) && (rawResult.get(posnextMemX).get(posnextMemY) instanceof CubeNullCell)) {
				posnextMemY++;
			}
			break;
		}

		// Case 1 : there is a dimension member in the axis after the current
		// one (except its members) => we go to the next one
		if ((posnextMemX < rawResult.size()) && (posnextMemY < rawResult.get(0).size()) && (rawResult.get(posnextMemX).get(posnextMemY).isOLAPMember())) {
			memberContent = new CubeResultComponent(rawResult, measList, axis, new Position2D(posnextMemX, posnextMemY), posValue);
		}
		// Case 2 : the current axis is axis_y => we switch to the axis_x and
		// choice the first dimension member
		else if (axis == Constants.AXIS_Y) {
			posnextMemX = 0;
			posnextMemY = posValue.getY();
			while (!rawResult.get(posnextMemX).get(posnextMemY).isOLAPMember()) {
				posnextMemX++;
			}
			memberContent = new CubeResultComponent(rawResult, measList, Constants.AXIS_X, new Position2D(posnextMemX, posnextMemY), posValue);
		}
		// Case 3 : we get the data in the posValue position
		else {
			loadResultDatas(rawResult, measList, posValue);
		}

	}

	public String getName() {
		return name;
	}

	public CubeLevel getLevel() {
		return level;
	}

	public String getId() {
		return id;
	}

	public List<CubeResultComponent> getChildren() {
		return children;
	}

	public CubeResultComponent getMemberContent() {
		return memberContent;
	}

	public List<CubeResultData> getDatas() {
		return datas;
	}

	public void loadResultDatas(List<List<CubeCell>> rawResult, List<String> measList, Position2D pos) {
		datas = new ArrayList<>();

		int posx = pos.getX();
		int posy = pos.getY();

		for (int i = 0; i < measList.size(); i++) {
			if (rawResult.get(posx).get(posy) instanceof CubeValueCell) {
				CubeValueCell cellVal = ((CubeValueCell) rawResult.get(posx).get(posy));
				datas.add(new CubeResultData(measList.get(i), cellVal));
				posx++;
			}
		}
	}

	public void loadChildMembers(List<List<CubeCell>> rawResult, List<String> measList, String axis, Position2D posMember, Position2D posValue) {
		children = new ArrayList<>();

		int posNextMemX = posMember.getX() + 1;
		int posNextMemY = posMember.getY() + 1;

		switch (axis) {

		// for axis_x we go through the next line
		case Constants.AXIS_X:
			while ((rawResult.get(0).size() > posNextMemY) && (!rawResult.get(posNextMemX - 1).get(posNextMemY).isOLAPMember())) {
				if (rawResult.get(posNextMemX).get(posNextMemY).isOLAPMember()) {
					Position2D posnextMember = new Position2D(posNextMemX, posNextMemY);
					Position2D posnextValue = new Position2D(posValue.getX(), posNextMemY);
					children.add(new CubeResultComponent(rawResult, measList, axis, posnextMember, posnextValue));
				}
				posNextMemY++;
			}
			break;

		// for axis_y we go through the next column
		case Constants.AXIS_Y:
			while ((rawResult.size() > posNextMemX) && (!rawResult.get(posNextMemX).get(posNextMemY - 1).isOLAPMember())) {
				if (rawResult.get(posNextMemX).get(posNextMemY).isOLAPMember()) {
					Position2D posnextMember = new Position2D(posNextMemX, posNextMemY);
					Position2D posnextValue = new Position2D(posNextMemX, posValue.getY());
					children.add(new CubeResultComponent(rawResult, measList, axis, posnextMember, posnextValue));
				}
				posNextMemX++;
			}
			break;
		}
	}

}
