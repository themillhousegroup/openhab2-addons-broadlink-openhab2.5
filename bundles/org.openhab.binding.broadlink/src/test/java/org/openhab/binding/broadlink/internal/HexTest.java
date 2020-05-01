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

    @Test
    public void convertHexToBytes() throws IOException {
        byte[] mac = new byte[] { (byte) 0xff, (byte) 0xee, (byte) 0xdd, (byte) 0xcc, (byte) 0xbb, (byte) 0xaa};
        String s = "ffeeddccbbaa";
        byte[] result = Hex.convertHexToBytes(s);
        assertEquals(mac[0], result[0]);
        assertEquals(mac[1], result[1]);
        assertEquals(mac[2], result[2]);
        assertEquals(mac[3], result[3]);
        assertEquals(mac[4], result[4]);
        assertEquals(mac[5], result[5]);
    }

    @Test
    public void fromHexString() throws IOException {
        byte[] mac = new byte[] { (byte) 0xff, (byte) 0xee, (byte) 0xdd, (byte) 0xcc, (byte) 0xbb, (byte) 0xaa};
        String s = "ffeeddccbbaa";
        byte[] result = Hex.fromHexString(s);
        assertEquals(mac[0], result[0]);
        assertEquals(mac[1], result[1]);
        assertEquals(mac[2], result[2]);
        assertEquals(mac[3], result[3]);
        assertEquals(mac[4], result[4]);
        assertEquals(mac[5], result[5]);
    }

    @Test
    public void toHexString() throws IOException {
        byte[] mac = new byte[] { (byte) 0xff, (byte) 0xee, (byte) 0xdd, (byte) 0xcc, (byte) 0xbb, (byte) 0xaa};
        String result = Hex.toHexString(mac);
        assertEquals("FFEEDDCCBBAA", result);
    }
}
