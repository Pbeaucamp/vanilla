package bpm.database.ui.viewer.relations.gef.additional;

import java.awt.LayoutManager;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbstractPointListShape;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RoutingListener;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

import bpm.database.ui.viewer.relations.gef.editparts.TablePart;
import bpm.database.ui.viewer.relations.model.JoinConnection;
import bpm.database.ui.viewer.relations.model.Node;


public class CurveConnection extends PolylineConnection {
	
	private static int OFFSET_X = 80;
      private static int OFFSET_Y = 15;

      private List<PrecisionPoint> connectionPoints = new ArrayList<PrecisionPoint>();

      private int bend_X;
      private int bend_Y;

      private int queueEntries = 0;

      private JoinConnection connection=null;
      
 //     private Label cardinalitySource;
 //     private Label cardinalityTarget;
      
      public CurveConnection(){
    	  super();
      }
      
      public CurveConnection(JoinConnection connection){
    	  super();
    	  this.connection=connection;
 //   	  cardinalitySource = new Label(getCardinalitySource());
 //   	  cardinalityTarget = new Label(getCardinalityTarget());
    	  
    	//  cardinalitySource.setText(getCardinalitySource());
    	//  cardinalityTarget.setText(getCardinalityTarget());
    	  
      }
      
      @Override
      protected void outlineShape(Graphics g) {

              connectionPoints.clear();
              Point bendPointStart = null;
              Point bendPointEnd = null;
              int lineup = calculateLineup(getStart(), getEnd());
              
              Point cardinalitySource= new Point(getStart()) ;
              Point cardinalityTarget= new Point(getEnd());
              
              switch (lineup) {
              case 1:
                      bendPointStart = new Point(getStart().x + bend_X, getStart().y
                                      - bend_Y);
                      bendPointEnd = new Point(getEnd().x - bend_X, getEnd().y + bend_Y);

                      cardinalitySource = new Point (getStart().x+20, getStart().y-15);
                      cardinalityTarget =new Point(getEnd().x-20, getEnd().y-15);
                      
                      break;

              case 2:
                      bendPointStart = new Point(getStart().x + bend_X, getStart().y
                                      + bend_Y);
                      bendPointEnd = new Point(getEnd().x - bend_X, getEnd().y - bend_Y);
                     
                      cardinalitySource = new Point (getStart().x+20, getStart().y-15);
                      cardinalityTarget =new Point(getEnd().x-20, getEnd().y-15);
                      
                      break;

              case 3:
                      bendPointStart = new Point(getStart().x - bend_X, getStart().y
                                      - bend_Y);
                      bendPointEnd = new Point(getEnd().x + bend_X, getEnd().y + bend_Y);

                      cardinalitySource = new Point (getStart().x-20, getStart().y-15);
                      cardinalityTarget =new Point(getEnd().x+20, getEnd().y-15);
                      
                      break;

              case 4:
                      bendPointStart = new Point(getStart().x - bend_X, getStart().y
                                      + bend_Y);
                      bendPointEnd = new Point(getEnd().x + bend_X, getEnd().y - bend_Y);

                      cardinalitySource = new Point (getStart().x-20, getStart().y-15);
                      cardinalityTarget =new Point(getEnd().x+20, getEnd().y-15);
                      
                      break;
                      
              case 5:
                  bendPointStart = new Point(getStart().x - bend_X, getStart().y
                                  - bend_Y);
                  bendPointEnd = new Point(getEnd().x - bend_X, getEnd().y + bend_Y);
                  
                  cardinalitySource = new Point (getStart().x-20, getStart().y-15);
                  cardinalityTarget =new Point(getEnd().x-20, getEnd().y-15);

                  break;
                  
                  
              case 6:
                  bendPointStart = new Point(getStart().x - bend_X, getStart().y
                                  + bend_Y);
                  bendPointEnd = new Point(getEnd().x - bend_X, getEnd().y - bend_Y);
                  
                  cardinalitySource = new Point (getStart().x-20, getStart().y-15);
                  cardinalityTarget =new Point(getEnd().x-20, getEnd().y-15);

                  break;
                  
              default:
                      break;
              }

              CubicCurve2D curve = new CubicCurve2D.Float(getStart().x, getStart().y,
                              bendPointStart.x, bendPointStart.y, bendPointEnd.x,
                              bendPointEnd.y, getEnd().x, getEnd().y);

              PathIterator pi = curve.getPathIterator(null, Float.MIN_VALUE);

              while (pi.isDone() == false) {
                      PrecisionPoint p = getCurrentPoint(pi);
                      connectionPoints.add(p);

                      pi.next();
              }

              Path p = new Path(Display.getCurrent());
              p.moveTo(getStart().x, getStart().y);

              for (PrecisionPoint po : connectionPoints) {
                      p.lineTo((float) po.preciseX(), (float) po.preciseY());
              }
              p.lineTo(getEnd().x, getEnd().y);

              g.setInterpolation(SWT.HIGH);

              Display display = Display.getCurrent();
              g.setAntialias(SWT.ON);
              g.setForegroundColor(display.getSystemColor(SWT.COLOR_BLACK));
              g.setLineWidth(2);
            //  g.setForegroundColor(display.getSystemColor(SWT.COLOR_DARK_BLUE));
            //  g.setLineWidth(4);
             // g.setAlpha(180);

              g.drawPath(p);
             // g.setForegroundColor(display.getSystemColor(SWT.COLOR_WHITE));
             // g.setLineWidth(2);
             // g.drawPath(p);

             // g.setForegroundColor(display.getSystemColor(SWT.COLOR_RED));
             // g.setLineWidth(4);
              int counter = queueEntries;
              Path path = new Path(Display.getCurrent());

              PrecisionPoint last = connectionPoints.get(connectionPoints.size() - 1);
              path.moveTo((float) last.preciseX(), (float) last.preciseY());
              int size = connectionPoints.size() - 1;
              for (int i = 0; i < counter * 35; i++) {
                      PrecisionPoint nextPoint = connectionPoints.get(size - i);
                      path.lineTo((float) nextPoint.preciseX(),
                                      (float) nextPoint.preciseY());
              }
              if (counter > 1) {
                      g.drawPath(path);
              }
              g.drawString(getCardinalitySource(), cardinalitySource);
              g.drawString(getCardinalityTarget(), cardinalityTarget);
      }

      @Override
      public void paint(Graphics graphics) {
              // TODO Auto-generated method stub

              super.paint(graphics);
              
     //         cardinalitySource.repaint();
     //         cardinalityTarget.repaint();
      }

      private PrecisionPoint getCurrentPoint(PathIterator pi) {
              double[] coordinates = new double[6];
              PrecisionPoint p = new PrecisionPoint();

              int type = pi.currentSegment(coordinates);
              if (type == PathIterator.SEG_LINETO || type == PathIterator.SEG_MOVETO) {

                      double x = coordinates[0];

                      double y = coordinates[1];

                      p.setPreciseX(x);

                      p.setPreciseY(y);

              }
              return p;
      }

      @Override
      public Rectangle getBounds() {
              // TODO Auto-generated method stub
              Rectangle bounds = super.getBounds();

              Rectangle newBounds = new Rectangle();
              newBounds.width = bounds.width + 40;
              newBounds.height = bounds.height + 40;
              newBounds.x = bounds.x - 20;
              newBounds.y = bounds.y - 20;
              bounds.union(newBounds);
              return bounds;
      }

      private int calculateLineup(Point startPoint, Point endPoint) {
             
    	
  		
  		if(connection!=null){
  			Node targetPart = connection.getTarget();
  	  		Node sourcePart =  connection.getSource();
  	  		
  	  		Rectangle r1 = sourcePart.getParent().getLayout().getCopy(); 
	  	  	Rectangle r2 = targetPart.getParent().getLayout().getCopy();	  		
	  		
	  		if (r1.x <= r2.x && r1.y <= r2.y) {
                this.bend_X = OFFSET_X;
                this.bend_Y = OFFSET_Y;
               
                if (r1.x + r1.width < r2.x)
                	return 1;
                return 5;
        }
        if (r1.x <= r2.x && r1.y >= r2.y) {
                this.bend_X = OFFSET_X;
                this.bend_Y = OFFSET_Y;
                if (r1.x + r1.width < r2.x)
                	return 2;
                return 6;
        }
        if (r1.x > r2.x && r1.y >= r2.y) {
                
                this.bend_X = OFFSET_X ;
                this.bend_Y = OFFSET_Y ;
                
                return 3;
        }
        if (r1.x > r2.x && r1.y <= r2.y) {
              
                this.bend_X = OFFSET_X ;
                this.bend_Y = OFFSET_Y ;
                return 4;
        }

        return 0;
  		
  		}
  		 
  		 
    	  if (startPoint.x <= endPoint.x && startPoint.y <= endPoint.y) {
                      this.bend_X = OFFSET_X;
                      this.bend_Y = OFFSET_Y;
                      return 1;
              }
              if (startPoint.x <= endPoint.x && startPoint.y >= endPoint.y) {
                      this.bend_X = OFFSET_X;
                      this.bend_Y = OFFSET_Y;
                      return 2;
              }
              if (startPoint.x >= endPoint.x && startPoint.y >= endPoint.y) {
                      int reference = (startPoint.x - endPoint.x);
                      if (reference > 100)
                              reference = 100;
                      this.bend_X = OFFSET_X + reference;
                      this.bend_Y = OFFSET_Y + 30;
                      return 3;
              }
              if (startPoint.x >= endPoint.x && startPoint.y <= endPoint.y) {
                      int reference = (startPoint.x - endPoint.x) / 2;
                      if (reference > 130)
                              reference = 130;
                      this.bend_X = OFFSET_X + reference;
                      this.bend_Y = OFFSET_Y + 30;
                      return 4;
              }

              return 0;
      }
      
  /*    
      Rectangle r1 = ((TablePart)targetPart.getParent()).getFigure().getClientArea().getCopy();
		((TablePart)targetPart.getParent()).getFigure().translateToAbsolute(r1);
		
		Rectangle r2 = ((TablePart)sourcePart.getParent()).getFigure().getClientArea().getCopy();
		((TablePart)sourcePart.getParent()).getFigure().translateToAbsolute(r2);
//		((SchemaPart)sourcePart.getParent().getParent()).getFigure().translateToRelative(r2);
//		Object o = ((FlowLayout)((SchemaPart)sourcePart.getParent().getParent()).getFigure().getLayoutManager()).getConstraint(((TablePart)sourcePart.getParent()).getFigure());
		if (anchorLeft == null) {
			anchorLeft = new MyAnchor(getFigure(), true);
			
		}
		if (anchorRight == null) {
			anchorRight = new MyAnchor(getFigure(), false);
			
		}
		
		
		
		
		if (targetPart == this){
			if (r1.x < r2.x){
				if (r1.x + r1.width < r2.x){
					return anchorRight;
				}
				else{
					return anchorLeft;
				}
			}
			else{
				return anchorLeft;
			}
			
		}
		else{
			if (r1.x > r2.x){
				return anchorRight;
			}
			else{
				return anchorLeft;
			}
			
		}
      */
      
      private String getCardinalitySource(){
    	  try{
    	 if (connection.getCardinality()!=null || !connection.getCardinality().equals("")){
	    	  String[] split = connection.getCardinality().split("_");
	    	  return split[1];
    	 }
    	  }
    	  catch(Exception e){
    		  
    	  }
    	 return "";
    	 
      }
      
      private String getCardinalityTarget(){
    	  try{
	    	  if (connection.getCardinality()!=null || !connection.getCardinality().equals("")){
		    	  String[] split = connection.getCardinality().split("_");
		    	  return split[2];
	    	  }
    	  }
    	  catch(Exception e){
	    		  
	    	  }
    	  return "";
      }

      public List<PrecisionPoint> getConnectionPoints() {
              return connectionPoints;
      }

      public void setConnectionPoints(List<PrecisionPoint> connectionPoints) {
              this.connectionPoints = connectionPoints;
      }

      public void setEntry(int count) {
              this.queueEntries = count;
      }
		
}
