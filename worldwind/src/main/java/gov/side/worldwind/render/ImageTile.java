/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.side.worldwind.render;

import gov.side.worldwind.geom.Sector;
import gov.side.worldwind.util.Level;
import gov.side.worldwind.util.Tile;

public class ImageTile extends Tile {

    protected ImageSource imageSource;

    public ImageTile(Sector sector, Level level, int row, int column) {
        super(sector, level, row, column);
    }

    public ImageSource getImageSource() {
        return imageSource;
    }

    public void setImageSource(ImageSource imageSource) {
        this.imageSource = imageSource;
    }
}
