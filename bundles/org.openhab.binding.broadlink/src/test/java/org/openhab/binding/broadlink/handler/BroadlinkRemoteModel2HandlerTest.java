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
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.thing.internal.ThingImpl;
import org.eclipse.smarthome.core.thing.type.ThingType;
import org.eclipse.smarthome.core.types.State;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;
import org.openhab.binding.broadlink.config.BroadlinkDeviceConfiguration;
import org.openhab.binding.broadlink.internal.socket.NetworkTrafficObserver;
import org.openhab.binding.broadlink.internal.socket.RetryableSocket;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.openhab.binding.broadlink.handler.BroadlinkSocketModel2Handler.*;


public class BroadlinkRemoteModel2HandlerTest extends AbstractBroadlinkThingHandlerTest {

    @Before
    public void setUp() {
        configureUnderlyingThing(BroadlinkBindingConstants.THING_TYPE_RM2, "rm2-test");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void mergeOnOffBitsAllZero() {
        int result = mergeOnOffBits(OnOffType.OFF, OnOffType.OFF);
        assertEquals(0x00, result);
    }

    @Test
    public void mergeOnOffBitsPowerOn() {
        int result = mergeOnOffBits(OnOffType.ON, OnOffType.OFF);
        assertEquals(0x01, result);
    }
    @Test
    public void mergeOnOffBitsNightlightOn() {
        int result = mergeOnOffBits(OnOffType.OFF, OnOffType.ON);
        assertEquals(0x02, result);
    }
    @Test
    public void mergeOnOffBitsAllOn() {
        int result = mergeOnOffBits(OnOffType.ON, OnOffType.ON);
        assertEquals(0x03, result);
    }
    @Test
    public void derivePowerStateBitsOff() {
        byte[] payload = { 0x00, 0x00, 0x00, 0x00, 0x00};
        OnOffType result = derivePowerStateFromStatusByte(payload);
        assertEquals(OnOffType.OFF, result);
    }
    @Test
    public void derivePowerStateBitsOn1() {
        byte[] payload = { 0x00, 0x00, 0x00, 0x00, 0x01};
        OnOffType result = derivePowerStateFromStatusByte(payload);
        assertEquals(OnOffType.ON, result);
    }
    @Test
    public void derivePowerStateBitsOn3() {
        byte[] payload = { 0x00, 0x00, 0x00, 0x00, 0x03};
        OnOffType result = derivePowerStateFromStatusByte(payload);
        assertEquals(OnOffType.ON, result);
    }
    @Test
    public void derivePowerStateBitsOnFD() {
        byte[] payload = { 0x00, 0x00, 0x00, 0x00, (byte) 0xFD};
        OnOffType result = derivePowerStateFromStatusByte(payload);
        assertEquals(OnOffType.ON, result);
    }
    @Test
    public void deriveNightLightStateBitsOff() {
        byte[] payload = { 0x00, 0x00, 0x00, 0x00, 0x00};
        OnOffType result = deriveNightLightStateFromStatusByte(payload);
        assertEquals(OnOffType.OFF, result);
    }
    @Test
    public void deriveNightLightStateBitsOn2() {
        byte[] payload = { 0x00, 0x00, 0x00, 0x00, 0x02};
        OnOffType result = deriveNightLightStateFromStatusByte(payload);
        assertEquals(OnOffType.ON, result);
    }
    @Test
    public void deriveNightLightStateBitsOn3() {
        byte[] payload = { 0x00, 0x00, 0x00, 0x00, 0x03};
        OnOffType result = deriveNightLightStateFromStatusByte(payload);
        assertEquals(OnOffType.ON, result);
    }
    @Test
    public void deriveNightLightStateBitsOnFF() {
        byte[] payload = { 0x00, 0x00, 0x00, 0x00, (byte) 0xFF};
        OnOffType result = deriveNightLightStateFromStatusByte(payload);
        assertEquals(OnOffType.ON, result);
    }

    @Test
    public void sendsExpectedBytesWhenGettingDeviceStatus() {
        byte[] response = {
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        };
        ArgumentCaptor<Byte> commandCaptor = ArgumentCaptor.forClass(Byte.class);
        ArgumentCaptor<byte[]> byteArrayCaptor = ArgumentCaptor.forClass(byte[].class);
        Mockito.when(mockSocket.sendAndReceive(Mockito.any(byte[].class), Mockito.anyString())).thenReturn(response);
        BroadlinkRemoteHandler model2 = new BroadlinkRemoteModel2Handler(thing);
        setMocksForTesting(model2);
        model2.getStatusFromDevice();

        verify(trafficObserver).onCommandSent(commandCaptor.capture());
        assertEquals(0x6a, commandCaptor.getValue().byteValue());

        verify(trafficObserver).onBytesSent(byteArrayCaptor.capture());

        byte[] sentBytes = byteArrayCaptor.getValue();
        assertEquals(16, sentBytes.length);
        assertEquals(0x01, sentBytes[0]);
    }

    @Test
    public void setsTheTemperatureChannelAfterGettingStatus() {
        byte[] response = {
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x03, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        };
        Mockito.when(mockSocket.sendAndReceive(Mockito.any(byte[].class), Mockito.anyString())).thenReturn(response);
        BroadlinkRemoteHandler model2 = new BroadlinkRemoteModel2Handler(thing);
        setMocksForTesting(model2);

        model2.getStatusFromDevice();

        ArgumentCaptor<ChannelUID> channelCaptor = ArgumentCaptor.forClass(ChannelUID.class);
        ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
        verify(mockCallback).stateUpdated(channelCaptor.capture(), stateCaptor.capture());

        ChannelUID expectedTemperatureChannel = new ChannelUID(
                thing.getUID(),
                "temperature");
        assertEquals(expectedTemperatureChannel, channelCaptor.getValue());

        DecimalType expectedTemperature = new DecimalType(106.0);
        assertEquals(expectedTemperature, stateCaptor.getValue());

    }
}
