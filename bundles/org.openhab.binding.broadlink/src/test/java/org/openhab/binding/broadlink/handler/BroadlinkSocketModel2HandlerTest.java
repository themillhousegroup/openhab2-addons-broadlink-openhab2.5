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

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.openhab.binding.broadlink.handler.BroadlinkSocketModel2Handler.*;


public class BroadlinkSocketModel2HandlerTest {
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

}
