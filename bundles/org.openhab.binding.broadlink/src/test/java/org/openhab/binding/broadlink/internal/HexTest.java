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
package org.openhab.binding.broadlink.internal;

import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;

public class HexTest {
    @Test
    public void decodeMACLittleEndian() throws IOException {
        byte[] mac = new byte[] { (byte) 0x06, (byte) 0x05, (byte) 0x04, (byte) 0x03, (byte) 0x02, (byte) 0x01};
        String result = Hex.decodeMAC(mac);
        assertEquals("01:02:03:04:05:06", result);
    }

    @Test
    public void decodeMACFullHexRange() throws IOException {
        byte[] mac = new byte[] { (byte) 0xff, (byte) 0xee, (byte) 0xdd, (byte) 0xcc, (byte) 0xbb, (byte) 0xaa};
        String result = Hex.decodeMAC(mac);
        assertEquals("aa:bb:cc:dd:ee:ff", result);
    }

    @Test(expected = IOException.class)
    public void decodeMACThrowsOnShortMAC() throws IOException {
        byte[] mac = new byte[] { (byte) 0xff, (byte) 0xee, (byte) 0xdd, (byte) 0xcc, (byte) 0xbb};
        Hex.decodeMAC(mac);
    }
}
