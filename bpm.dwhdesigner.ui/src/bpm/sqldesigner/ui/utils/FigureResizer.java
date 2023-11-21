package bpm.sqldesigner.ui.utils;

import java.util.TreeMap;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableRootEditPart;

import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;

public class FigureResizer {

	private int row = 0;
	private int col = 0;
	private int nextRow = 0;
	private long sizeX;
	
	public void resize(Schema schema, GraphicalViewer viewer){
		TreeMap<String, Table> hmTables = schema.getTables();
		
		Table t = null;

		double scale = ((ScalableRootEditPart) viewer.getRootEditPart())
				.getZoomManager().getScalableFigure().getScale();
		sizeX = Math.round(viewer.getControl().getSize().x / scale);

		if (sizeX == 0){
			sizeX = 1000;
		}
		
		for (String tableName : hmTables.keySet()) {
			t = hmTables.get(tableName);
			calcLayout(t, viewer);
		}
		if (t != null)
			t.getListeners().firePropertyChange(Node.PROPERTY_LAYOUT,
					t.getLayout(), t.getLayout());
		
		while(((ScalableRootEditPart) viewer.getRootEditPart())
		.getZoomManager().getScalableFigure().getScale() < 1.0){
			((ScalableRootEditPart) viewer.getRootEditPart())
			.getZoomManager().zoomIn();
		}
		
	}
	
	private void calcLayout(Table table, GraphicalViewer viewer) {
		if (table.layoutIsNull()) {
			int width;
			int height;

			int[] result = LayoutUtils.getTableDimension(table);
			width = result[0];
			height = result[1];

			int x;
			int y;
			if (col < sizeX) {
				x = col + 20;
				col = x + width;
				y = row + 20;
				if (height + row > nextRow)
					nextRow = height + row;
			} else {
				x = 20;
				col = x + width;
				row = nextRow + 20;
				y = row + 20;
				nextRow = height + y;
			}
			table.setLayout(x, y, width, height);

			double scale = ((ScalableRootEditPart) viewer.getRootEditPart())
					.getZoomManager().getScalableFigure().getScale();
			int sizeY = viewer.getControl().getSize().y;

			long sizeYMax = Math.round(sizeY / scale);

			if (row + height > sizeYMax) {
//				((ScalableRootEditPart) viewer.getRootEditPart())
//						.getZoomManager().zoomOut();

				if (((ScalableRootEditPart) viewer.getRootEditPart())
						.getZoomManager().getScalableFigure().getScale() == ((ScalableRootEditPart) viewer
						.getRootEditPart()).getZoomManager().getMaxZoom()) {
					col = ((col / sizeY) + 1) * sizeY + 20;
				}
			}
		} else {
			double scale = ((ScalableRootEditPart) viewer.getRootEditPart())
					.getZoomManager().getScalableFigure().getScale();
			int sizeY = viewer.getControl().getSize().y;
			int sizeX = viewer.getControl().getSize().x;

			long sizeYMax = Math.round(sizeY / scale);
			long sizeXMax = Math.round(sizeX / scale);

			int[] layout = table.getLayout();

			if ((layout[1] + layout[3] > sizeYMax)
					|| (layout[0] + layout[2] > sizeXMax)) {
				((ScalableRootEditPart) viewer.getRootEditPart())
						.getZoomManager().zoomOut();
			}
		}

	}
}
