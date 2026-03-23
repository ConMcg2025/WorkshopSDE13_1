package dk.easv.fox.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Holds a complete snapshot of a fox machine's configuration.
 * Keyed by FoxParameter so the controller can fill fields generically.
 */
public class FoxConfig {

    private final Map<FoxParameter, String> values = new EnumMap<>(FoxParameter.class);

    public FoxConfig() {
        // initialise all fields to empty so callers never get null
        for (FoxParameter p : FoxParameter.values()) {
            values.put(p, "");
        }
    }

    public void set(FoxParameter param, String value) {
        values.put(param, value != null ? value : "");
    }

    public String get(FoxParameter param) {
        return values.getOrDefault(param, "");
    }

    public Map<FoxParameter, String> all() {
        return Map.copyOf(values);
    }
}
