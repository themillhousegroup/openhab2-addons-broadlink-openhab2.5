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

import org.eclipse.jdt.annotation.NonNullByDefault;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * Handles conversions to/from hexadecimal.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public class Hex {
    private static final String HEXES = "0123456789ABCDEF";

    public static String decodeMAC(byte mac[]) throws IOException {
        if (mac.length < 6) {
            throw new IOException("Insufficient MAC bytes provided, cannot decode it");
        }

        StringBuilder sb = new StringBuilder(18);
        for(int i = 5; i >= 0; i--)
        {
            if (sb.length() > 0) {
                sb.append(':');
            }
            sb.append(String.format("%02x", new Object[] {
                Byte.valueOf(mac[i])
            }));
        }

        return sb.toString();
    }

    public static byte[] fromHexString(String hex) {
        if (hex.length() % 2 != 0) throw new IllegalArgumentException("Input string must contain an even number of characters");

        byte result[] = new byte[hex.length() / 2];
        char bytes[] = hex.toCharArray();
        for(int i = 0; i < bytes.length; i += 2) {
            StringBuilder curr = new StringBuilder(2);
            curr.append(bytes[i]).append(bytes[i + 1]);
            result[i / 2] = (byte)Integer.parseInt(curr.toString(), 16);
        }

        return result;
    }

    public static String toHexString(byte raw[]) {
        StringBuilder hex = new StringBuilder(2 * raw.length);
        byte abyte0[];
        int j = (abyte0 = raw).length;
        for(int i = 0; i < j; i++) {
            byte b = abyte0[i];
            hex.append(HEXES.charAt((b & 0xf0) >> 4)).append(HEXES.charAt(b & 0xf));
        }

        return hex.toString();
    }

    public static boolean isDifferent(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) return true;
        int j = b1.length;
        for(int i = 0; i < j; i++) {
            if (b1[i] != b2[i]) {
                return true;
            }
        }
        return false;
    }
}
