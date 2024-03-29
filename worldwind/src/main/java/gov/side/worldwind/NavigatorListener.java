/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.side.worldwind;

public interface NavigatorListener {

    void onNavigatorEvent(WorldWindow wwd, NavigatorEvent event);
}