/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.side.worldwind.layer;

import gov.side.worldwind.WorldWind;
import gov.side.worldwind.geom.Sector;
import gov.side.worldwind.ogc.WmsLayer;
import gov.side.worldwind.ogc.WmsLayerConfig;
import gov.side.worldwind.render.ImageOptions;
import gov.side.worldwind.shape.TiledSurfaceImage;
import gov.side.worldwind.util.Logger;

/**
 * Displays NASA's Blue Marble next generation imagery at 500m resolution from an OGC Web Map Service (WMS). By default,
 * BlueMarbleLayer is configured to retrieve imagery for May 2004 from the WMS at <a
 * href="https://worldwind25.arc.nasa.gov/wms?SERVICE=WMS&REQUEST=GetCapabilities">https://worldwind25.arc.nasa.gov/wms</a>.
 * <p/>
 * Information on NASA's Blue Marble next generation imagery can be found at http://earthobservatory.nasa.gov/Features/BlueMarble/
 */
public class BlueMarbleLayer extends WmsLayer {

    /**
     * Constructs a Blue Marble image layer with the WMS at https://worldwind25.arc.nasa.gov/wms.
     */
    public BlueMarbleLayer() {
        this("https://worldwind25.arc.nasa.gov/wms");
    }

    /**
     * Constructs a Blue Marble image layer with the WMS at a specified address.
     *
     * @param serviceAddress a URL string specifying the WMS address
     *
     * @throws IllegalArgumentException If the service address is null
     */
    public BlueMarbleLayer(String serviceAddress) {
        if (serviceAddress == null) {
            throw new IllegalArgumentException(
                Logger.logMessage(Logger.ERROR, "BlueMarbleLayer", "constructor", "missingServiceAddress"));
        }

        WmsLayerConfig config = new WmsLayerConfig();
        config.serviceAddress = serviceAddress;
        config.wmsVersion = "1.3.0";
        config.layerNames = "BlueMarble-200405";
        config.coordinateSystem = "EPSG:4326";
        config.transparent = false; // the BlueMarble layer is opaque

        this.setDisplayName("Blue Marble");
        this.setConfiguration(new Sector().setFullSphere(), 500, config); // 500m resolution on Earth

        TiledSurfaceImage surfaceImage = (TiledSurfaceImage) this.getRenderable(0);
        surfaceImage.setImageOptions(new ImageOptions(WorldWind.RGB_565));  // exploit opaque imagery to reduce memory usage
    }
}
