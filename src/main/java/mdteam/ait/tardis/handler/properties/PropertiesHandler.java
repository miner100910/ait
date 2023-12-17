package mdteam.ait.tardis.handler.properties;

import mdteam.ait.AITMod;
import mdteam.ait.datagen.datagen_providers.AITLanguageProvider;

import java.util.HashMap;

public class PropertiesHandler { // todo move more things over to properties
    public static final String AUTO_LAND = "auto_land";
    public static final String SEARCH_DOWN = "search_down";
    public static final String PREVIOUSLY_LOCKED = "last_locked";
    public static final String HANDBRAKE = "handbrake";
    public static final String HAIL_MARY = "hail_mary";

    // Should these methods be in the holder instead?

    /**
     * used for setting things to a boolean
     */
    public static void setBool(PropertiesHolder holder, String key, boolean val) {
        if (holder.getData().containsKey(key)) {
            holder.getData().replace(key, val);
            return;
        }

        holder.getData().put(key, val);
    }

    public static void set(PropertiesHolder holder, String key, Object val) {
        if (holder.getData().containsKey(key)) {
            holder.getData().replace(key, val);
            return;
        }

        holder.getData().put(key, val);
    }

    public static Object get(PropertiesHolder holder, String key) {
        if (!holder.getData().containsKey(key)) return null;

        return holder.getData().get(key);
    }

    public static boolean getBool(PropertiesHolder holder, String key) {
        if (!holder.getData().containsKey(key)) return false;

        if (!(holder.getData().get(key) instanceof Boolean)) {
            AITMod.LOGGER.warn("Tried to grab key " + key + " which was not a boolean!");
            return false;
        }

        return (boolean) holder.getData().get(key);
    }

    public static int getInt(PropertiesHolder holder, String key) {
        if (!holder.getData().containsKey(key)) return 0;

        if (!(holder.getData().get(key) instanceof Integer) && !(holder.getData().get(key) instanceof Double) && !(holder.getData().get(key) instanceof Float)) {
            AITMod.LOGGER.error("Tried to grab key " + key + " which was not an Integer!");
            AITMod.LOGGER.warn("Value was instead: " + holder.getData().get(key));
            return 0;
        }

        return (int) holder.getData().get(key);
    }

    public static void setAutoPilot(PropertiesHolder handler, boolean val) {
        setBool(handler, AUTO_LAND, val);
    }

    public static boolean willAutoPilot(PropertiesHolder holder) {
        return getBool(holder, AUTO_LAND);
    }

    public static HashMap<String, Object> createDefaultProperties() {
        HashMap<String, Object> map = new HashMap<>();

        map.put(AUTO_LAND, false);
        map.put(SEARCH_DOWN, false);
        map.put(PREVIOUSLY_LOCKED, false);
        map.put(HANDBRAKE, true);
        map.put(HAIL_MARY, true);

        return map;
    }
}
