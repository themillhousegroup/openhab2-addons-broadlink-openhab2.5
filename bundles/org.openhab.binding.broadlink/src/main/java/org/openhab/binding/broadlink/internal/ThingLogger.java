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
import org.eclipse.smarthome.core.thing.*;
import org.slf4j.Logger;

/**
 * Handles logging on behalf of a given Thing.
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
@NonNullByDefault
public final class ThingLogger  {

    private final Thing thing;
    private final Logger logger;

    public ThingLogger(Thing thing, Logger logger) {
        this.thing = thing;
        this.logger = logger;
    }

    String describeStatus() {
        if (Utils.isOnline(thing)) {
            return "^";
        }
        if (Utils.isOffline(thing)) {
            return "v";
        }
        return "?";
    }

    Object[] prependUID(Object... args) {
        Object[] allArgs = new Object[args.length + 2];
        allArgs[0] = thing.getUID().toString().replaceFirst("^broadlink:", "");;
        allArgs[1] = describeStatus();
        System.arraycopy(args, 0, allArgs, 2, args.length);
        return allArgs;
    }

    Object[] appendMessage(Object[] args, String msg) {
        Object[] allArgs = new Object[args.length + 1];
        System.arraycopy(args, 0, allArgs, 0, args.length);
        allArgs[args.length] = msg;
        return allArgs;
    }

    public void logDebug(String msg, Object... args) {
        if (logger.isDebugEnabled()) {
            if (args.length == 0) {
                logger.debug("{}[{}]: {}", appendMessage(prependUID(), msg));
            } else {
                logger.debug("{}[{}]: " + msg, prependUID(args));
            }
        }
    }

    public void logError(String msg, Object... args) {
        if (args.length == 0) {
            logger.error("{}[{}]: {}", appendMessage(prependUID(), msg));
        } else {
            logger.error("{}[{}]: " + msg, prependUID(args));
        }
    }

    public void logWarn(String msg, Object... args) {
        if (args.length == 0) {
            logger.warn("{}[{}]: {}", appendMessage(prependUID(), msg));
        } else {
            logger.warn("{}[{}]: " + msg, prependUID(args));
        }
    }

    public void logInfo(String msg, Object... args) {
        if (args.length == 0) {
            logger.info("{}[{}]: {}", appendMessage(prependUID(), msg));
        } else {
            logger.info("{}[{}]: " + msg, prependUID(args));
        }
    }

    public void logTrace(String msg, Object... args) {
        if (logger.isTraceEnabled()) {
            if (args.length == 0) {
                logger.trace("{}[{}]: {}", appendMessage(prependUID(), msg));
            } else {
                logger.trace("{}[{}]: " + msg, prependUID(args));
            }
        }
    }
}
