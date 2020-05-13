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

import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Test;

public class HexTest {
    @Test
    public void differenceOfSameThingIsFalse() {
        byte[] b1 = new byte[] {};
        assertFalse(Hex.isDifferent(b1, b1));
    }

    @Test
    public void differenceOfEmptyAndNonEmptyIsTrue() {
        byte[] b1 = new byte[] {};
        byte[] b2 = new byte[] { 0x01 };
        assertTrue(Hex.isDifferent(b1, b2));
    }
    @Test
    public void differenceOfSameValuesIsFalse() {
        byte[] b1 = new byte[] { 0x01 };
        byte[] b2 = new byte[] { 0x01 };
        assertFalse(Hex.isDifferent(b1, b2));
    }

    @Test
    public void differenceOfZeroedValuesIsFalse() {
        byte[] b1 = new byte[] { 0x0, 0x0, 0x0, 0x0 };
        byte[] b2 = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        assertFalse(Hex.isDifferent(b1, b2));
    }
}
