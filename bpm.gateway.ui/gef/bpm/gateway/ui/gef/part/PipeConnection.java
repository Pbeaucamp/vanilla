package bpm.gateway.ui.gef.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**

* @author scheglov_ke

*/

public class PipeConnection extends PolylineConnection {

	private int radius = 10;

	public Rectangle getBounds() {
		return super.getBounds().getCopy().expand(10, 10);
	}

	protected void outlineShape(Graphics g) {
		ArrayList connections = collectConnections();

		PointList ps = getPoints();

		if (ps.size() == 0) {
			return;
		}

		Point pp = ps.getPoint(0);
		
		for (int i = 1; i < ps.size(); i++) {
			Point p = ps.getPoint(i);
			Point ppo = ps.getPoint(i-1);
			Point p1 = p.getCopy();
			Point p2 = p.getCopy();

			if (i == ps.size() - 1) {
				drawLine(g, pp, p, connections);
				continue;
			}

			Point pn = ps.getPoint(i + 1).getCopy();
			int dx = min0(Math.abs(ppo.x - p.x), Math.abs(p.x - pn.x));
			int dy = min0(Math.abs(ppo.y - p.y), Math.abs(p.y - pn.y));
			int d = min0(dx, dy);
			int r = Math.min(radius, d / 2);
			int r2 = 2 * r;

			if ((pp.y == p.y) && (p.x == pn.x) && r2 != 0) {
				if ((pp.x < p.x) && (p.y < pn.y)) {
					p1.x -= r;
					p2.y += r;
					g.drawArc(p.x - r2, p.y, r2, r2, 0, 90);
				} 
				else if ((pp.x > p.x) && (p.y < pn.y)) {
					p1.x += r;
					p2.y += r;
					g.drawArc(p.x, p.y, r2, r2, 90, 90);
				} 
				else if ((pp.x < p.x) && (p.y > pn.y)) {
					p1.x -= r;
					p2.y -= r;
					g.drawArc(p.x - r2, p.y - r2, r2, r2, 270, 90);
				} 
				else if ((pp.x > p.x) && (p.y > pn.y)) {
					p1.x += r;
					p2.y -= r;
					g.drawArc(p.x, p.y - r2, r2, r2, 180, 90);
				}
			}

			if ((pp.x == p.x) && (p.y == pn.y) && r2 != 0) {
				if ((pp.y < p.y) && (p.x < pn.x)) {
					p1.y -= r;
					p2.x += r;
					g.drawArc(p.x, p.y - r2, r2, r2, 180, 90);
				} 
				else if ((pp.y < p.y) && (p.x > pn.x)) {
					p1.y -= r;
					p2.x -= r;
					g.drawArc(p.x - r2, p.y - r2, r2, r2, 270, 90);
				} 
				else if ((pp.y > p.y) && (p.x < pn.x)) {
					p1.y += r;
					p2.x += r;
					g.drawArc(p.x, p.y, r2, r2, 90, 90);
				} 
				else if ((pp.y > p.y) && (p.x > pn.x)) {
					p1.y += r;
					p2.x -= r;
					g.drawArc(p.x - r2, p.y, r2, r2, 0, 90);
				}
			}

			drawLine(g, pp, p1, connections);
			pp = p2;
		}
	}

	private void drawLine(Graphics g, Point pp, Point p1, ArrayList connections) {

		if (pp.x == p1.x) {

			ArrayList segments = new ArrayList();

			for (Iterator I = connections.iterator(); I.hasNext();) {

				PipeConnection conn = (PipeConnection) I.next();
				PointList cps = conn.getPoints();

				for (int j = 0; j < cps.size() - 1; j++) {

					Point cp1 = cps.getPoint(j);
					Point cp2 = cps.getPoint(j + 1);

					if ((cp1.y == cp2.y) && ((cp1.x < pp.x && cp2.x > pp.x) || (cp1.x > pp.x && cp2.x < pp.x))
							&& ((pp.y < cp1.y && p1.y > cp1.y) || (pp.y > cp1.y && p1.y < cp1.y)))

						segments.add(new Integer(cp1.y));
				}
			}

			if (segments.size() > 0) {
				int r = 5;
				Collections.sort(segments);
				int y1 = Math.min(pp.y, p1.y);
				int y2 = Math.max(pp.y, p1.y);
				int y = y1;

				for (Iterator I = segments.iterator(); I.hasNext();) {

					int y0 = ((Integer) I.next()).intValue();
					g.drawLine(pp.x, y, pp.x, y0 - r);

					if (y2 - y0 > r)
						g.drawArc(pp.x - r, y0 - r, 2 * r, 2 * r, 90, 180);
					else
						g.drawLine(pp.x, y, pp.x, y2);
					y = y0 + r;
				}
				g.drawLine(pp.x, y, pp.x, y2);
			} 
			else {
				g.drawLine(pp, p1);
			}
		} 
		else {
			g.drawLine(pp, p1);
		}
	}

	private ArrayList collectConnections() {
		ArrayList result = new ArrayList();
		collectConnections(getRoot(), result);
		return result;
	}

	private void collectConnections(IFigure figure, ArrayList list) {

		for (Iterator I = figure.getChildren().iterator(); I.hasNext();) {

			IFigure child = (IFigure) I.next();
			collectConnections(child, list);
			if (!(child instanceof PipeConnection))
				continue;
			list.add(child);
		}
	}

	private IFigure getRoot() {

		IFigure figure = this;
		while (figure.getParent() != null)
			figure = figure.getParent();
		return figure;
	}

	private int min0(int a, int b) {

		if (a == 0)
			return b;
		if (b == 0)
			return a;
		return Math.min(a, b);

	}

}
