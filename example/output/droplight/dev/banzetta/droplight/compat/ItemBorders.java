package dev.banzetta.droplight.compat;

import net.minecraft.*;
import net.fabricmc.loader.api.*;
import dev.banzetta.droplight.*;
import java.lang.reflect.*;

public class ItemBorders
{
    public static TextColor getColorForItem(final ItemStack item) {
        TextColor result = null;
        if (FabricLoader.getInstance().isModLoaded("itemborders")) {
            try {
                final Method getBorderColorForItem = Class.forName("dev.banzetta.droplight.compat.itemborders.ApiAccess").getMethod("getBorderColorForItem", new Class[] { ItemStack.class });
                result = (TextColor)getBorderColorForItem.invoke((Object)null, new Object[] { item });
            }
            catch (final Exception e) {
                Droplight.LOGGER.error((Object)e);
            }
        }
        return result;
    }
}
