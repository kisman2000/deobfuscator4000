package dev.banzetta.droplight.config;

import com.electronwill.nightconfig.toml.*;
import com.electronwill.nightconfig.core.*;
import java.util.*;

record ColorDefinition(List<String> items, String color) {
    public Config toConfig() {
        return Config.of(() -> new LinkedHashMap<String, Object>() {
            {
                this.put((Object)"items", (Object)((ColorDefinition.this.items == null) ? List.of() : ColorDefinition.this.items));
                this.put((Object)"color", (Object)((ColorDefinition.this.color == null) ? "" : ColorDefinition.this.color));
            }
        }, (ConfigFormat)TomlFormat.instance());
    }
    
    public static ColorDefinition fromConfig(final Config config) {
        return new ColorDefinition((List<String>)config.get("items"), (String)config.get("color"));
    }
    
    public static boolean validateList(final Object value) {
        if (value instanceof List) {
            final List<?> configList = (List<?>)value;
            for (final Object configValue : configList) {
                if (configValue instanceof final Config config) {
                    if (config.contains("items") && config.contains("color") && config.get("items") instanceof List && config.get("color") instanceof String) {
                        continue;
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }
}
