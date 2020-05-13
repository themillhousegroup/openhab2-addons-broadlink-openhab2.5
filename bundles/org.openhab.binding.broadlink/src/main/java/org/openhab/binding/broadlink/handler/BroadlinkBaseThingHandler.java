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

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;

import org.openhab.binding.broadlink.config.BroadlinkDeviceConfiguration;
import org.openhab.binding.broadlink.internal.*;
import org.openhab.binding.broadlink.internal.discovery.DeviceRediscoveryAgent;
import org.openhab.binding.broadlink.internal.discovery.DeviceRediscoveryListener;
import org.openhab.binding.broadlink.internal.socket.RetryableSocket;
import org.slf4j.Logger;

/**
 * Abstract superclass of all supported Broadlink devices.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public abstract class BroadlinkBaseThingHandler extends BaseThingHandler implements DeviceRediscoveryListener {


    @Nullable
    private RetryableSocket socket;
    private int count;

    protected BroadlinkDeviceConfiguration thingConfig;
    protected final ThingLogger thingLogger;
    @Nullable
    private ScheduledFuture<?> refreshHandle;
    // These get handed to us by the device after successful authentication:
    private byte[] deviceId;
    private byte[] deviceKey;

    public BroadlinkBaseThingHandler(Thing thing, Logger logger) {
        super(thing);
        this.thingLogger = new ThingLogger(thing, logger);
        this.thingConfig = (BroadlinkDeviceConfiguration) getConfigAs(BroadlinkDeviceConfiguration.class);
        count = (new Random()).nextInt(65535);

        // Set the default instance variables, such that the first buildMessage (used to authenticate) will work:
        this.deviceId = Hex.fromHexString(INITIAL_DEVICE_ID); // resetDeviceId()
        this.deviceKey = Hex.fromHexString(thingConfig.getAuthorizationKey());

        this.socket = new RetryableSocket(thingConfig, thingLogger);
    }

    private static final String INITIAL_DEVICE_ID = "00000000";
    private void resetDeviceId() {
        this.deviceId = Hex.fromHexString(INITIAL_DEVICE_ID);
    }

    private boolean hasAuthenticated() {
        return Hex.isDifferent(this.deviceId, Hex.fromHexString(INITIAL_DEVICE_ID));
    }

    public void initialize() {
        thingLogger.logDebug("initializing polling");

        if (thingConfig.getPollingInterval() != 0) {
            refreshHandle = scheduler.scheduleWithFixedDelay(
                    new Runnable() {

                        public void run() {
                            updateItemStatus();
                        }
                    },
                    1L,
                    thingConfig.getPollingInterval(),
                    TimeUnit.SECONDS
            );
        } else {
            updateItemStatus();
        }
    }

    public void thingUpdated(Thing thing) {
        thingLogger.logDebug("thingUpdated");
        forceOffline(ThingStatusDetail.CONFIGURATION_PENDING, "Thing has been updated, will reconnect soon");
    }

    public void dispose() {
        thingLogger.logDebug(getThing().getLabel() + " is being disposed");
        if (refreshHandle != null && !refreshHandle.isDone()) {
            thingLogger.logDebug("Cancelling refresh task");
            boolean cancelled = refreshHandle.cancel(true);
            thingLogger.logDebug("Cancellation successful: " + cancelled);
        }
        if (socket != null) {
            socket.close();
        }
        super.dispose();
    }

    protected boolean authenticate() {
        thingLogger.logDebug("Authenticating");

        try {
            byte authRequest[] = buildMessage((byte) 0x65, BroadlinkProtocol.buildAuthenticationPayload(), -1);
            byte response[] = sendAndReceiveDatagram(authRequest, "authentication");
            byte decryptResponse[] = decodeDevicePacket(response);
            deviceId = BroadlinkProtocol.getDeviceId(decryptResponse);
            deviceKey = BroadlinkProtocol.getDeviceKey(decryptResponse);

            // Update the properties, so that these values can be seen in the UI:
            Map<String, String> properties = editProperties();
            properties.put("id", Hex.toHexString(deviceId));
            properties.put("key", Hex.toHexString(deviceKey));
            updateProperties(properties);
            thingLogger.logDebug(
            "Authenticated with id '{}' and key '{}'",
                Hex.toHexString(deviceId),
                Hex.toHexString(deviceKey)
            );
            return true;
        } catch (Exception e) {
            thingLogger.logError("Authentication failed: ", e);
            return false;
        }

    }

    protected byte @Nullable [] sendAndReceiveDatagram(byte message[], String purpose) {
        return socket.sendAndReceive(message, purpose);
    }

    protected byte[] buildMessage(byte command, byte payload[]) throws IOException {
        return buildMessage(command, payload, thingConfig.getDeviceType());
    }
    
    protected byte[] buildMessage(byte command, byte payload[], int deviceType) throws IOException {
        count = count + 1 & 0xffff;
        thingLogger.logTrace("building message with count: {}, deviceId: {}, deviceKey: {}",
            count,
            Hex.toHexString(deviceId),
            Hex.toHexString(deviceKey)
        );
        return BroadlinkProtocol.buildMessage(
            command,
            payload,
            count,
            thingConfig.getMAC(),
            deviceId,
            Hex.fromHexString(thingConfig.getIV()),
            deviceKey,
            deviceType
        );
    }

    protected byte[] decodeDevicePacket(byte[] responseBytes) throws IOException {
        return BroadlinkProtocol.decodePacket(
            responseBytes,
            this.deviceKey,
            thingConfig.getIV()
        );
    }

    public void handleCommand(ChannelUID channelUID, Command command) {
        thingLogger.logDebug("handleCommand " + command.toString());
        if (command instanceof RefreshType) {
            thingLogger.logTrace("Refresh requested, updating item status ...");

            updateItemStatus();
        }
    }

    // Can be implemented by devices that should do something on being found; e.g. perform a first status query
    protected boolean onBroadlinkDeviceBecomingReachable() {
        return true;
    }

    // Implemented by devices that can update the openHAB state
    // model. Return false if something went wrong that requires
    // a change in the device's online state
    protected boolean getStatusFromDevice() {
        return true;
    }

    public void updateItemStatus() {
        thingLogger.logTrace("updateItemStatus; checking host availability at {}", thingConfig.getIpAddress());
        if (NetworkUtils.hostAvailabilityCheck(thingConfig.getIpAddress(), 3000)) {
            thingLogger.logTrace("updateItemStatus; host found at {}", thingConfig.getIpAddress());
            if (!Utils.isOnline(getThing())) {
                thingLogger.logTrace("updateItemStatus; device not currently online, resolving");
                transitionToOnline();
            } else {
                // Normal operation ...
                boolean gotStatusOk = getStatusFromDevice();
                if (!gotStatusOk) {
                    if (thingConfig.isIgnoreFailedUpdates()) {
                        thingLogger.logWarn("Problem getting status. Not marking offline because configured to ignore failed updates ...");
                    } else {
                        thingLogger.logError("Problem getting status. Marking as offline ...");
                        forceOffline(ThingStatusDetail.GONE,"Problem getting status");
                    }
                }
            }
        } else {
            if (thingConfig.isStaticIp()) {
                if (!Utils.isOffline(getThing())) {
                    thingLogger.logDebug("Statically-IP-addressed device not found at {}", thingConfig.getIpAddress());
                    forceOffline(ThingStatusDetail.GONE,"Couldn't find statically-IP-addressed device");
                }
            } else {
                thingLogger.logDebug("Dynamic IP device not found at {}, will search...", thingConfig.getIpAddress());
                DeviceRediscoveryAgent dra = new DeviceRediscoveryAgent(thingConfig, this);
                dra.attemptRediscovery();
                thingLogger.logDebug("Asynchronous dynamic IP device search initiated...");
            }
        }
    }

    public void onDeviceRediscovered(String newIpAddress) {
        thingLogger.logInfo("Rediscovered this device at IP {}", newIpAddress);
        thingConfig.setIpAddress(newIpAddress);
        transitionToOnline();
    }

    public void onDeviceRediscoveryFailure() {
        if (!Utils.isOffline(getThing())) {
            thingLogger.logDebug("Dynamically-IP-addressed device not found after network scan. Marking offline");
            forceOffline(ThingStatusDetail.GONE,"Couldn't rediscover device");
        }
    }

    private void transitionToOnline() {
        if (!hasAuthenticated()) {
            thingLogger.logDebug("We've never actually successfully authenticated with this device in this session. Doing so now");
            if (authenticate()) {
                thingLogger.logDebug("Authenticated with newly-detected device, will now get its status");
            } else {
                thingLogger.logError("Attempting to authenticate prior to getting device status FAILED. Will mark as offline");
                forceOffline(ThingStatusDetail.COMMUNICATION_ERROR,"Couldn't authenticate");
                return;
            }
        }
        if (onBroadlinkDeviceBecomingReachable()) {
            thingLogger.logDebug("Offline -> Online");
            updateStatus(ThingStatus.ONLINE);
        } else {
            thingLogger.logError("Device became reachable but had trouble getting status. Marking as offline ...");
            forceOffline(ThingStatusDetail.COMMUNICATION_ERROR, "Trouble getting status");
        }
    }

    private void forceOffline(ThingStatusDetail detail, String reason) {
        thingLogger.logWarn("Online -> Offline due to: {}", reason);
        resetDeviceId(); // This session is dead; we'll need to re-authenticate next time
        updateStatus(
                ThingStatus.OFFLINE,
                detail,
                reason
        );
        if (socket != null) {
            socket.close();
        }
    }
}
