package org.fasd.utils;

import org.eclipse.swt.graphics.Point;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeMember;
import org.fasd.utils.trees.TreeObject;
import org.fasd.utils.trees.TreeParent;

public class SVGGenerator {
	private int legend;
	private Point first = new Point(50, 300 / 2);
	private StringBuffer svg = new StringBuffer();
	private TreeParent model;

	public String getSvgContent(TreeParent model) {
		this.model = model;
		draw();
		return svg.toString();
	}

	private void draw() {

		svg.append("<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">\n"); //$NON-NLS-1$
		svg.append("<script type=\"text/ecmascript\"> <![CDATA[\n"); //$NON-NLS-1$

		svg.append("function select(evt) {\n"); //$NON-NLS-1$
		svg.append("      var circle = evt.target;\n"); //$NON-NLS-1$
		svg.append("	    circle.setAttribute(\"fill\", \"yellow\");\n"); //$NON-NLS-1$
		svg.append("		MoveToTop(circle);\n"); //$NON-NLS-1$
		svg.append("    }\n"); //$NON-NLS-1$
		svg.append("function unselect(evt){\n"); //$NON-NLS-1$
		svg.append("	var circle = evt.target;\n"); //$NON-NLS-1$
		svg.append("	    circle.setAttribute(\"fill\", \"white\");\n"); //$NON-NLS-1$
		svg.append("		MoveToTop(circle);\n"); //$NON-NLS-1$
		svg.append("}\n"); //$NON-NLS-1$

		svg.append("function MoveToTop( svgNode ){\n"); //$NON-NLS-1$

		svg.append("	parent = svgNode.parentNode;\n"); //$NON-NLS-1$

		svg.append("	parent.parentNode.appendChild(parent);\n"); //$NON-NLS-1$

		svg.append("	}\n"); //$NON-NLS-1$
		svg.append("]]>\n"); //$NON-NLS-1$
		svg.append("</script>"); //$NON-NLS-1$
		legend = first.y * 2 - 20;
		Point currsz = drawRectangle(model.getName(), first, null);
		drawRectangle("Dimension", new Point(first.x, legend), null); // draw //$NON-NLS-1$
																		// legend

		for (int i = 0; i < model.getChildren().length; i++) {
			Point curr = new Point(0, 0);
			curr.x = first.x + 125;
			curr.y = (first.y * (i + 1)) / model.getChildren().length;

			Point nextsz = drawRectangle(((TreeParent) (model.getChildren()[i])).getName(), curr, null);

			drawRectangle("Hierarchy", new Point(curr.x, legend), null); // draw //$NON-NLS-1$
																			// legend

			drawArrow(first.x + (currsz.x / 2) + 2, first.y + (currsz.y / 2) + 1, curr.x - (nextsz.x / 2) - 2, curr.y + (nextsz.y / 2) + 1);

			curr.y = curr.y * (2 * model.getChildren().length);

			// recursive
			drawRank(new Point(curr.x, curr.y / 2), nextsz, curr.y * 2, (TreeParent) model.getChildren()[i]);
		}

		svg.append("</svg>\n"); //$NON-NLS-1$

	}

	private void drawRank(Point curr, Point currsz, int width, TreeParent tp) {
		/*
		 * _ sub mb1 -member curr.x/y width sub mb2 _
		 * 
		 * we must divide width by (nbelem + 1) from curr get exact point for
		 * "fleche" start
		 */
		int offset = width / (tp.getChildren().length + 1) + 1;
		int start = curr.y - width / 2;

		int acc = offset;

		Point big = new Point(0, 0);
		for (int i = 0; i < tp.getChildren().length; i++) {
			Point sz = new Point(((TreeObject) (tp.getChildren()[i])).getName().length() * 12, 20);
			if (sz.x > big.x)
				big = sz;
		}

		if (tp.getParent() != null && (tp.getParent() instanceof TreeMember || tp.getParent() instanceof TreeHierarchy)) {

			for (int j = 0; j < tp.getParent().getChildren().length; j++) {
				if (tp.getParent().getChildren()[j] != tp) {

					TreeParent o = (TreeParent) tp.getParent().getChildren()[j];

					for (int i = 0; i < o.getChildren().length; i++) {

						Point sz = new Point(((TreeObject) (tp.getChildren()[i])).getName().length() * 12, 20);
						if (sz.x > big.x)
							big = sz;
					}

				}
			}

		}

		if (tp.getChildren().length > 0) {
			if (tp.getChildren()[0] instanceof TreeMember) {
				drawRectangle(((TreeMember) (tp.getChildren()[0])).getLevel().getName(), new Point(curr.x + 125, legend), null); // draw
																																	// legend
			} else {
				drawRectangle(((TreeObject) (tp.getChildren()[0])).getName(), new Point(curr.x + 125, legend), null); // draw
																														// legend
			}

		}

		for (int i = 0; i < tp.getChildren().length; i++) {
			Point next = new Point(curr.x + 125, start + acc); // decalage a
																// droite
			Point nextsz = drawRectangle(((TreeParent) (tp.getChildren()[i])).getName(), next, big);
			drawArrow(curr.x + (currsz.x / 2) + 2, curr.y + (currsz.y / 2) + 1, next.x - (nextsz.x / 2) - 2, next.y + (nextsz.y / 2) + 1);

			drawRank(next, nextsz, offset, (TreeParent) (tp.getChildren()[i]));

			acc += offset;
		}
	}

	private void drawArrow(int x1, int y1, int x2, int y2) {
		svg.append("<line x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2 + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		svg.append(" stroke-width=\"1\"  stroke=\"red\"/>"); //$NON-NLS-1$

	}

	private Point drawRectangle(String buf, Point curr, Point big) {
		Point sz = new Point(buf.length() * 12, 20);

		if (big == null)
			big = sz;

		svg.append("<g>\n"); //$NON-NLS-1$
		svg.append("<rect x=\"" + String.valueOf(curr.x - ((big.x + 4) / 2)) + "\" y=\"" + String.valueOf(curr.y) + "\" width=\"" + String.valueOf((big.x + 4)) + "\" height=\"" + String.valueOf(big.y + 2) + "\"\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		svg.append("    fill=\"none\" stroke=\"blue\" stroke-width=\"1\" onmouseover=\"select(evt)\" onmouseout=\"unselect(evt)\"/>\n"); //$NON-NLS-1$
		svg.append("<text x=\"" + String.valueOf(curr.x + 2 - ((big.x - (big.x - sz.x) + 4) / 2)) + "\" y=\"" + String.valueOf(curr.y + 1 + 13) + "\" font-size=\"10\"> \n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		svg.append(buf.toString());
		svg.append("</text>\n"); //$NON-NLS-1$
		svg.append("</g>\n"); //$NON-NLS-1$

		return big;
	}
}
