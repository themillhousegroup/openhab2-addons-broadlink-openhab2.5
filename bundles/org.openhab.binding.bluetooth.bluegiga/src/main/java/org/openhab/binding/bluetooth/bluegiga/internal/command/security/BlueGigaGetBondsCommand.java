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
package org.openhab.binding.bluetooth.bluegiga.internal.command.security;

import org.openhab.binding.bluetooth.bluegiga.internal.BlueGigaCommand;

/**
 * Class to implement the BlueGiga command <b>getBonds</b>.
 * <p>
 * This command lists all bonded devices. There can be a maximum of 8 bonded devices. The
 * information related to the bonded devices is stored in the Flash memory, so it is persistent
 * across resets and power-cycles.
 * <p>
 * This class provides methods for processing BlueGiga API commands.
 * <p>
 * Note that this code is autogenerated. Manual changes may be overwritten.
 *
 * @author Chris Jackson - Initial contribution of Java code generator
 */
public class BlueGigaGetBondsCommand extends BlueGigaCommand {
    public static int COMMAND_CLASS = 0x05;
    public static int COMMAND_METHOD = 0x05;


    @Override
    public int[] serialize() {
        // Serialize the header
        serializeHeader(COMMAND_CLASS, COMMAND_METHOD);

        return getPayload();
    }

    @Override
    public String toString() {
        return "BlueGigaGetBondsCommand []";
    }
}
