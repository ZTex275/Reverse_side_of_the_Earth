/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.side.worldwind.render;

import gov.side.worldwind.draw.DrawContext;
import gov.side.worldwind.geom.Matrix3;
import gov.side.worldwind.geom.Sector;

public interface SurfaceTexture { // TODO this likely belongs in the draw package

    Sector getSector();

    Matrix3 getTexCoordTransform();

    boolean bindTexture(DrawContext dc);
}
