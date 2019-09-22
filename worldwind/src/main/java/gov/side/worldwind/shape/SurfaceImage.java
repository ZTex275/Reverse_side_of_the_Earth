/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.side.worldwind.shape;

import gov.side.worldwind.PickedObject;
import gov.side.worldwind.draw.DrawableSurfaceTexture;
import gov.side.worldwind.geom.Sector;
import gov.side.worldwind.render.AbstractRenderable;
import gov.side.worldwind.render.ImageOptions;
import gov.side.worldwind.render.ImageSource;
import gov.side.worldwind.render.RenderContext;
import gov.side.worldwind.render.SurfaceTextureProgram;
import gov.side.worldwind.render.Texture;
import gov.side.worldwind.util.Logger;
import gov.side.worldwind.util.Pool;

public class SurfaceImage extends AbstractRenderable {

    protected final Sector sector = new Sector();

    protected ImageSource imageSource;

    protected ImageOptions imageOptions;

    public SurfaceImage() {
        super("Surface Image");
    }

    public SurfaceImage(Sector sector, ImageSource imageSource) {
        super("Surface Image");

        if (sector == null) {
            throw new IllegalArgumentException(
                Logger.logMessage(Logger.ERROR, "SurfaceImage", "constructor", "missingSector"));
        }

        this.sector.set(sector);
        this.imageSource = imageSource;
    }

    public Sector getSector() {
        return this.sector;
    }

    public void setSector(Sector sector) {
        if (sector == null) {
            throw new IllegalArgumentException(
                Logger.logMessage(Logger.ERROR, "SurfaceImage", "setSector", "missingSector"));
        }

        this.sector.set(sector);
    }

    public ImageSource getImageSource() {
        return this.imageSource;
    }

    public void setImageSource(ImageSource imageSource) {
        this.imageSource = imageSource;
    }

    public ImageOptions getImageOptions() {
        return this.imageOptions;
    }

    public void setImageOptions(ImageOptions imageOptions) {
        this.imageOptions = imageOptions;
    }

    @Override
    protected void doRender(RenderContext rc) {
        if (this.sector.isEmpty()) {
            return; // nothing to render
        }

        if (!rc.terrain.getSector().intersects(this.sector)) {
            return; // no terrain surface to render on
        }

        Texture texture = rc.getTexture(this.imageSource); // try to get the texture from the cache
        if (texture == null) {
            texture = rc.retrieveTexture(this.imageSource, this.imageOptions); // puts retrieved textures in the cache
        }

        if (texture == null) {
            return; // no texture to draw
        }

        // Enqueue a drawable surface texture for processing on the OpenGL thread.
        SurfaceTextureProgram program = this.getShaderProgram(rc);
        Pool<DrawableSurfaceTexture> pool = rc.getDrawablePool(DrawableSurfaceTexture.class);
        DrawableSurfaceTexture drawable = DrawableSurfaceTexture.obtain(pool).set(program, this.sector, texture, texture.getTexCoordTransform());
        rc.offerSurfaceDrawable(drawable, 0 /*z-order*/);

        // Enqueue a picked object that associates the drawable surface texture with this surface image.
        if (rc.pickMode) {
            int pickedObjectId = rc.nextPickedObjectId();
            PickedObject.identifierToUniqueColor(pickedObjectId, drawable.color);
            rc.offerPickedObject(PickedObject.fromRenderable(pickedObjectId, this, rc.currentLayer));
        }
    }

    protected SurfaceTextureProgram getShaderProgram(RenderContext rc) {
        SurfaceTextureProgram program = (SurfaceTextureProgram) rc.getShaderProgram(SurfaceTextureProgram.KEY);

        if (program == null) {
            program = (SurfaceTextureProgram) rc.putShaderProgram(SurfaceTextureProgram.KEY, new SurfaceTextureProgram(rc.resources));
        }

        return program;
    }
}
