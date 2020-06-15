package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.thing.internal.ThingImpl;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;
import org.openhab.binding.broadlink.internal.socket.NetworkTrafficObserver;
import org.openhab.binding.broadlink.internal.socket.RetryableSocket;

import java.util.HashMap;
import java.util.Map;

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
