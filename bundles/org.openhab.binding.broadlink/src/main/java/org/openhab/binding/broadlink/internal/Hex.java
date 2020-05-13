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

/**
 * Handles conversions to/from hexadecimal.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public class Hex {

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
