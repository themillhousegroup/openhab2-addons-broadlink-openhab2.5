/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.thing.internal.ThingImpl;
import org.mockito.Mock;
import org.openhab.binding.broadlink.internal.socket.NetworkTrafficObserver;
import org.openhab.binding.broadlink.internal.socket.RetryableSocket;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract thing handler test.
 * 
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public abstract class AbstractBroadlinkThingHandlerTest {

    protected Map<String, Object> properties;
    protected Configuration config;
    protected ThingImpl thing;

    @Mock
    protected RetryableSocket mockSocket;

    @Mock
    protected NetworkTrafficObserver trafficObserver;

    @Mock
    protected ThingHandlerCallback mockCallback;

    protected void configureUnderlyingThing(ThingTypeUID thingTypeUID, String thingId) {
        properties = new HashMap<>();
        properties.put("authorizationKey", "097628343fe99e23765c1513accf8b02");
        properties.put("mac", "AB:CD:AB:CD:AB:CD");
        properties.put("iv", "562e17996d093d28ddb3ba695a2e6f58");
        config = new Configuration(properties);

        thing = new ThingImpl(thingTypeUID, thingId);
        thing.setConfiguration(config);
    }

    protected void setMocksForTesting(BroadlinkBaseThingHandler handler) {
        handler.setSocket(mockSocket);
        handler.setNetworkTrafficObserver(trafficObserver);
        handler.setCallback(mockCallback);
    }
}
