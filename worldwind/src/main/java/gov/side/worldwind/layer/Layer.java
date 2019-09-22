/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.side.worldwind.layer;

import gov.side.worldwind.render.RenderContext;

public interface Layer {

    String getDisplayName();

    void setDisplayName(String displayName);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isPickEnabled();

    void setPickEnabled(boolean pickEnabled);

    double getOpacity();

    void setOpacity(double opacity);

    double getMinActiveAltitude();

    void setMinActiveAltitude(double minActiveAltitude);

    double getMaxActiveAltitude();

    void setMaxActiveAltitude(double maxActiveAltitude);

    Object getUserProperty(Object key);

    Object putUserProperty(Object key, Object value);

    Object removeUserProperty(Object key);

    boolean hasUserProperty(Object key);

    void render(RenderContext rc);

    boolean isWithinActiveAltitudes(RenderContext rc);
}
