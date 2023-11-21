package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.api.runtime.utils.Constants;

public class CubeResult {
	private List<CubeOLAPMember> axisX;
	private List<CubeOLAPMember> axisY;
	private CubeResultComponent content;

	public CubeResult(List<List<CubeCell>> rawResult) {
		Position2D firstMemberPosition = getFirstMemberPosition(rawResult);
		Position2D firstValuePosition = getFirstValuePosition(rawResult, firstMemberPosition);
		List<String> measList = getMeasureNames(rawResult, firstValuePosition);
		axisX = loadCubeAxis(rawResult, Constants.AXIS_X, firstValuePosition);
		axisY = loadCubeAxis(rawResult, Constants.AXIS_Y, firstValuePosition);
		content = new CubeResultComponent(rawResult, measList, Constants.AXIS_Y, firstMemberPosition, firstValuePosition);
	}

	public List<CubeOLAPMember> getAxisX() {
		return axisX;
	}

	public List<CubeOLAPMember> getAxisY() {
		return axisY;
	}

	public CubeResultComponent getContent() {
		return content;
	}

	public Position2D getFirstMemberPosition(List<List<CubeCell>> rawResult) {
		int x = 0;

		while (!rawResult.get(x).get(0).isOLAPMember()) {
			x++;
		}

		return new Position2D(x, 0);
	}

	public Position2D getFirstValuePosition(List<List<CubeCell>> rawResult, Position2D firstMemberPosition) {
		int x = firstMemberPosition.getX();
		int y = firstMemberPosition.getY();

		while (!(rawResult.get(x).get(y) instanceof CubeValueCell)) {
			y++;
		}

		return new Position2D(x, y);
	}

	public List<String> getMeasureNames(List<List<CubeCell>> rawResult, Position2D firstValuePosition) {
		List<String> measNames = new ArrayList<>();

		int measX = firstValuePosition.getX();
		int measY = firstValuePosition.getY() - 1;

		while ((measX < rawResult.size()) && (rawResult.get(measX).get(measY) instanceof CubeElementCell) && (!measNames.contains(rawResult.get(measX).get(measY).getName()))) {
			measNames.add(rawResult.get(measX).get(measY).getName());
			measX++;
		}

		return measNames;
	}

	private List<CubeOLAPMember> loadCubeAxis(List<List<CubeCell>> rawResult, String axis, Position2D firstValuePosition) {
		List<CubeOLAPMember> axisMembers = new ArrayList<>();
		int dimPosx = firstValuePosition.getX();
		int dimPosy = firstValuePosition.getY();

		switch (axis) {
		case Constants.AXIS_X:
			dimPosx = 0;
			break;
		case Constants.AXIS_Y:
			dimPosy = 0;
			break;
		}

		while (dimPosx < firstValuePosition.getX() || dimPosy < firstValuePosition.getY()) {
			if (rawResult.get(dimPosx).get(dimPosy).isOLAPMember()) {
				CubeElementCell elemCell = (CubeElementCell) rawResult.get(dimPosx).get(dimPosy);
				axisMembers.add(elemCell.getCubeMember());
			}

			switch (axis) {
			case Constants.AXIS_X:
				dimPosx++;
				break;
			case Constants.AXIS_Y:
				dimPosy++;
				break;
			}
		}

		return axisMembers;
	}

}
