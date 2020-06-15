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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.Thing;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Remote blaster handler, "generation" 4
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public class BroadlinkRemoteModel4Handler extends BroadlinkRemoteHandler {

    public BroadlinkRemoteModel4Handler(Thing thing) {
        super(thing, LoggerFactory.getLogger(BroadlinkRemoteModel4Handler.class));
    }

    protected boolean onBroadlinkDeviceBecomingReachable() {
        return getStatusFromDevice();
    }

    protected boolean getStatusFromDevice() {
        try {
            // These devices use a 2-byte preamble to the normal protocol;
            // https://github.com/mjg59/python-broadlink/blob/0bd58c6f598fe7239246ad9d61508febea625423/broadlink/__init__.py#L666

            byte payload[] = new byte[16];
            payload[0] = 0x04;
            payload[1] = 0x00;
            payload[2] = 0x24; // Status check is now Ox24, not 0x01 as in earlier devices
            byte message[] = buildMessage((byte) 0x6a, payload);
            byte response[] = sendAndReceiveDatagram(message, "RM4 device status");
            byte decodedPayload[] = decodeDevicePacket(response);
            // Temps and humidity get divided by 100 now, not 10
            float temperature = (float)((double)(decodedPayload[4] * 100 + decodedPayload[5]) / 100D);
            updateState("temperature", new DecimalType(temperature));
            float humidity = (float)((double)(decodedPayload[6] * 100 + decodedPayload[7]) / 100D);
            updateState("humidity", new DecimalType(humidity));
            return true;
        } catch (Exception e) {
            thingLogger.logError("Could not get status: ", e);
            return false;
        }
    }

    protected void sendCode(byte code[]) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // These devices use a 6-byte sendCode instead of the previous 4
            //https://github.com/mjg59/python-broadlink/blob/0.13.0/broadlink/__init__.py#L50 add RM4 list

            byte[] abyte0 = new byte[6];
            abyte0[0] = (byte) 0xd0;
            abyte0[2] = 2;
            outputStream.write(abyte0);
            outputStream.write(code);
        } catch (IOException e) {
            thingLogger.logError("Exception while sending code", e);
        }
        // Have a suspicion this is now wrong due to 6-byte header.
        // Couldn't we just pad with zeroes to the next 16-byte boundary?
        if (outputStream.size() % 16 == 0) {
            sendAndReceiveDatagram(buildMessage((byte) 0x6a, outputStream.toByteArray()), "remote code");
        } else {
            thingLogger.logError(
                    "Will not send remote code because it has an incorrect length (" + outputStream.size() + ")");
        }

    }
}
