package org.openhab.binding.broadlink.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

import java.util.Map;

@NonNullByDefault
public class PropertyUtils {

    public static final String EMPTY = "<empty>";

    public static boolean isPropertyEmpty(Map<String, String> properties, String propName) {
        if (properties.containsKey(propName)) {
            return EMPTY.equals(properties.get(propName));
        }
        return true;
    }

    public static boolean hasProperty(Map<String, String> properties, String propName) {
        if (properties.containsKey(propName)) {
            return !EMPTY.equals(properties.get(propName));
        }
        return false;
    }
}
