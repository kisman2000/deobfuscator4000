package dev.banzetta.droplight.config;

import com.electronwill.nightconfig.toml.*;
import com.electronwill.nightconfig.core.*;
import net.minecraft.*;
import java.util.*;

record SoundDefinition(List<String> items, SoundEvent sound) {
    public Config toConfig() {
        return Config.of(() -> new LinkedHashMap<String, Object>() {
            {
                this.put((Object)"items", (Object)((SoundDefinition.this.items == null) ? List.of() : SoundDefinition.this.items));
                this.put((Object)"sound", (Object)((SoundDefinition.this.sound == null) ? "" : SoundDefinition.this.sound.getId().toString()));
            }
        }, (ConfigFormat)TomlFormat.instance());
    }
    
    public static SoundDefinition fromConfig(final Config config) {
        return new SoundDefinition((List<String>)config.get("items"), (config.get("sound") == null) ? null : new SoundEvent(new Identifier((String)config.get("sound"))));
    }
    
    public static boolean validateList(final Object value) {
        if (value instanceof List) {
            final List<?> configList = (List<?>)value;
            for (final Object configValue : configList) {
                if (configValue instanceof final Config config) {
                    if (config.contains("items") && config.contains("sound") && config.get("items") instanceof List && config.get("sound") instanceof String) {
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
