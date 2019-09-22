/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.side.worldwind.layer;

import gov.side.worldwind.draw.Drawable;
import gov.side.worldwind.draw.DrawableTessellation;
import gov.side.worldwind.render.BasicShaderProgram;
import gov.side.worldwind.render.Color;
import gov.side.worldwind.render.RenderContext;
import gov.side.worldwind.util.Logger;
import gov.side.worldwind.util.Pool;

public class ShowTessellationLayer extends AbstractLayer {

    protected Color color = new Color();

    public ShowTessellationLayer() {
        super("Terrain Tessellation");
        this.setPickEnabled(false);
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException(
                Logger.logMessage(Logger.ERROR, "ShowTessellationLayer", "setColor", "missingColor"));
        }

        this.color.set(color);
    }

    @Override
    protected void doRender(RenderContext rc) {

        if (rc.terrain.getSector().isEmpty()) {
            return; // no terrain to render
        }

        // Use WorldWind's basic GLSL program.
        BasicShaderProgram program = (BasicShaderProgram) rc.getShaderProgram(BasicShaderProgram.KEY);
        if (program == null) {
            program = (BasicShaderProgram) rc.putShaderProgram(BasicShaderProgram.KEY, new BasicShaderProgram(rc.resources));
        }

        Pool<DrawableTessellation> pool = rc.getDrawablePool(DrawableTessellation.class);
        Drawable drawable = DrawableTessellation.obtain(pool).set(program, this.color);
        rc.offerSurfaceDrawable(drawable, 1.0 /*z-order after surface textures*/);
    }
}
