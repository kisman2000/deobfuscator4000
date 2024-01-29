package dev.banzetta.droplight;

import net.minecraft.*;

record DroppedItem(World level, double x, double y, double z, ItemStack itemStack, int timer) {}
