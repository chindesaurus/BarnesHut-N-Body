/**
 * BHTree.java
 *
 * Represents a quadtree for the Barnes-Hut algorithm.
 *
 * Dependencies: Body.java Quad.java
 *
 * @author chindesaurus
 * @version 1.00 
 */

import java.awt.Color;

public class BHTree {

    // threshold value
    private final double Theta = 0.5;

    private Body body;     // body or aggregate body stored in this node
    private Quad quad;     // square region that the tree represents
    private BHTree NW;     // tree representing northwest quadrant
    private BHTree NE;     // tree representing northeast quadrant
    private BHTree SW;     // tree representing southwest quadrant
    private BHTree SE;     // tree representing southeast quadrant
  
    /**
     * Constructor: creates a new Barnes-Hut tree with no bodies. 
     * Each BHTree represents a quadrant and an aggregate body 
     * that represents all bodies inside the quadrant.
     *
     * @param q the quadrant this node is contained within
     */
    public BHTree(Quad q) {
        this.quad = q;
        this.body = null;
        this.NW = null;
        this.NE = null;
        this.SW = null;
        this.SE = null;
    }
 

    /**
     * Adds the Body b to the invoking Barnes-Hut tree.
     */
    public void insert(Body b) {

        // if this node does not contain a body, put the new body b here
        if (body == null) {
            body = b;
            return;
        }
  
        // internal node
        if (! isExternal()) {
            // update the center-of-mass and total mass
            body = body.plus(b);
        
            // recursively insert Body b into the appropriate quadrant
            putBody(b);
        }

        // external node
        else {
            // subdivide the region further by creating four children
            NW = new BHTree(quad.NW());
            NE = new BHTree(quad.NE());
            SE = new BHTree(quad.SE());
            SW = new BHTree(quad.SW());

            // recursively insert both this body and Body b into the appropriate quadrant
            putBody(this.body);
            putBody(b);

            // update the center-of-mass and total mass
            body = body.plus(b);
        }
    }


    /**
     * Inserts a body into the appropriate quadrant.
     */ 
    private void putBody(Body b) {
        if (b.in(quad.NW()))
            NW.insert(b);
        else if (b.in(quad.NE()))
            NE.insert(b);
        else if (b.in(quad.SE()))
            SE.insert(b);
        else if (b.in(quad.SW()))
            SW.insert(b);
    }


    /**
     * Returns true iff this tree node is external.
     */
    private boolean isExternal() {
        // a node is external iff all four children are null
        return (NW == null && NE == null && SW == null && SE == null);
    }


    /**
     * Approximates the net force acting on Body b from all bodies
     * in the invoking Barnes-Hut tree, and updates b's force accordingly.
     */
    public void updateForce(Body b) {
    
        if (body == null || b.equals(body))
            return;

        // if the current node is external, update net force acting on b
        if (isExternal()) 
            b.addForce(body);
 
        // for internal nodes
        else {
    
            // width of region represented by internal node
            double s = quad.length();

            // distance between Body b and this node's center-of-mass
            double d = body.distanceTo(b);

            // compare ratio (s / d) to threshold value Theta
            if ((s / d) < Theta)
                b.addForce(body);   // b is far away
            
            // recurse on each of current node's children
            else {
                NW.updateForce(b);
                NE.updateForce(b);
                SW.updateForce(b);
                SE.updateForce(b);
            }
        }
    }


    /**
     * Returns a string representation of the Barnes-Hut tree
     * in which spaces represent external nodes, and asterisks
     * represent internal nodes.
     *
     * @return a string representation of this quadtree
     */
    public String toString() {
        if (isExternal()) 
            return " " + body + "\n";
        else
            return "*" + body + "\n" + NW + NE + SW + SE;
    }
}
