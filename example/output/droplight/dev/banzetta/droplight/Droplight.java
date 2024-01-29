package dev.banzetta.droplight;

import net.fabricmc.api.*;
import dev.banzetta.droplight.config.*;
import net.minecraftforge.api.*;
import net.minecraftforge.fml.config.*;
import dev.banzetta.droplight.sound.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.server.*;
import java.util.*;
import org.apache.logging.log4j.*;
import com.google.common.collect.*;
import net.minecraft.*;

public class Droplight implements ModInitializer
{
    public static final String MODID = "droplight";
    public static final Logger LOGGER;
    private static final Map<Integer, List<DroppedItem>> QUEUED_DROPS;
    private static final Random random;
    
    public void onInitialize() {
        ModLoadingContext.registerConfig("droplight", ModConfig.Type.COMMON, (IConfigSpec)DroplightConfig.SPEC);
        SoundEvents.init();
        ServerTickEvents.START_SERVER_TICK.register((Object)Droplight::onTick);
    }
    
    public static void onTick(final MinecraftServer server) {
        if ((int)DroplightConfig.INSTANCE.dropDelay.get() == 0) {
            Droplight.QUEUED_DROPS.forEach((id, items) -> items.forEach(Droplight::dropItem));
            Droplight.QUEUED_DROPS.clear();
        }
        else if (!Droplight.QUEUED_DROPS.isEmpty()) {
            for (final Integer id : Droplight.QUEUED_DROPS.keySet()) {
                final List<DroppedItem> items = (List<DroppedItem>)Droplight.QUEUED_DROPS.get((Object)id);
                if (items.isEmpty()) {
                    continue;
                }
                DroppedItem item = (DroppedItem)items.get(0);
                item = new DroppedItem(item.level(), item.x(), item.y(), item.z(), item.itemStack(), item.timer() - 1);
                if (item.timer() <= 0) {
                    dropItem(item);
                    items.remove(0);
                }
                else {
                    items.set(0, (Object)item);
                }
                Droplight.QUEUED_DROPS.put((Object)id, (Object)items);
            }
            Droplight.QUEUED_DROPS.values().removeIf(List::isEmpty);
        }
    }
    
    public static void queueDrop(final int entityID, final DroppedItem item) {
        if (!Droplight.QUEUED_DROPS.containsKey((Object)entityID)) {
            Droplight.QUEUED_DROPS.put((Object)entityID, (Object)Lists.newArrayList());
        }
        ((List)Droplight.QUEUED_DROPS.get((Object)entityID)).add((Object)item);
    }
    
    public static void dropItem(final DroppedItem item) {
        final ItemEntity itemEntity = new ItemEntity(item.level(), item.x(), item.y(), item.z(), item.itemStack());
        if (DroplightConfig.INSTANCE.dropSwishSound.get()) {
            item.level().playSound((PlayerEntity)null, itemEntity.getBlockPos(), SoundEvents.SWISH, SoundCategory.field_15254, 1.0f, MathHelper.nextBetween(Droplight.random, 0.8f, 1.2f));
        }
        itemEntity.setPickupDelay(40);
        final float f = Droplight.random.nextFloat() * 0.065f;
        final float g = Droplight.random.nextFloat() * 6.2831855f;
        itemEntity.setVelocity((double)(-MathHelper.sin(g) * f), 0.4749999940395355, (double)(MathHelper.cos(g) * f));
        item.level().spawnEntity((Entity)itemEntity);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        QUEUED_DROPS = (Map)Maps.newHashMap();
        random = Random.create();
    }
    
    record DroppedItem(World level, double x, double y, double z, ItemStack itemStack, int timer) {}
}
