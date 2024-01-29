package dev.banzetta.droplight.compat.itemborders;

import com.anthonyhilyard.itemborders.*;
import net.minecraft.*;

public class ApiAccess
{
    public static TextColor getBorderColorForItem(final ItemStack item) {
        TextColor result = ItemBordersConfig.INSTANCE.getBorderColorForItem(item);
        if (!(boolean)ItemBordersConfig.INSTANCE.showForCommon.get() && result == TextColor.fromFormatting(Formatting.field_1068)) {
            result = null;
        }
        return result;
    }
}
