package at.jku.cp.ai.visualization;

import java.awt.Graphics2D;

import prefuse.render.EdgeRenderer;
import prefuse.visual.VisualItem;

public class LabelledEdgeRenderer extends EdgeRenderer {

	public LabelledEdgeRenderer(int edgeType) {
		super(edgeType);
	}
//
//	protected String getText(VisualItem item) {
//	    EdgeItem edge = (EdgeItem)item;
////	    VisualItem parent = edge.getSourceItem();
//	    VisualItem child = edge.getTargetItem();    
//
//	    if(child.canGetString("lastMovePlayedBy") ) {
//	    	System.out.println("actually called" + child.getString("lastMovePlayedBy"));
//	        return child.getString("lastMovePlayedBy");            
//	    }
//	    
//	    return null;
//	}
	
	@Override
	public void render(Graphics2D g, VisualItem item) {
		super.render(g, item);		
	}
	
//	protected void getAlignedPoint(Point2D p, VisualItem item, 
//	        double w, double h, int xAlign, int yAlign)
//	{
//	    double x=0, y=0;                
//
//	    EdgeItem edge = (EdgeItem)item;
//	    VisualItem item1 = edge.getSourceItem();
//	    VisualItem item2 = edge.getTargetItem();
//
//	    // label is positioned to the center of the edge
//	    x = (item1.getX()+item2.getX())/2;
//	    y = (item1.getY()+item2.getY())/2;
//	}
}
