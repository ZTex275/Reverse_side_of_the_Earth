/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.side.worldwind.draw;

import gov.side.worldwind.geom.Sector;
import gov.side.worldwind.geom.Vec3;

public interface DrawableTerrain extends Drawable {

    Sector getSector();

    Vec3 getVertexOrigin();

    boolean useVertexPointAttrib(DrawContext dc, int attribLocation);

    boolean useVertexTexCoordAttrib(DrawContext dc, int attribLocation);

    boolean drawLines(DrawContext dc);

    boolean drawTriangles(DrawContext dc);
}
