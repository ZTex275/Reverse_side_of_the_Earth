/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.side.worldwind;

import gov.side.worldwind.draw.DrawContext;
import gov.side.worldwind.render.RenderContext;

public interface FrameController {

    void renderFrame(RenderContext rc);

    void drawFrame(DrawContext dc);
}
