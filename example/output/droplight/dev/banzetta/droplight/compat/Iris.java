package dev.banzetta.droplight.compat;

import dev.banzetta.droplight.*;
import net.fabricmc.loader.api.*;
import java.lang.reflect.*;

public class Iris
{
    private static Boolean cachedState;
    
    public static boolean canUseCustomShaders() {
        if (Iris.cachedState == null) {
            Iris.cachedState = !isShaderPackInUse();
            if (!Iris.cachedState) {
                Droplight.LOGGER.warn("Iris detected, high-quality shaders will be unavailable for Droplight while shader packs are in use!");
            }
        }
        return Iris.cachedState;
    }
    
    public static void refreshCache() {
        Iris.cachedState = null;
    }
    
    private static boolean isShaderPackInUse() {
        if (FabricLoader.getInstance().isModLoaded("iris")) {
            try {
                final Method method = Class.forName("dev.banzetta.droplight.compat.iris.ApiAccess").getMethod("isShaderPackInUse", new Class[0]);
                return (boolean)method.invoke((Object)null, new Object[0]);
            }
            catch (final Exception e) {
                return false;
            }
        }
        return false;
    }
    
    static {
        Iris.cachedState = null;
    }
}
