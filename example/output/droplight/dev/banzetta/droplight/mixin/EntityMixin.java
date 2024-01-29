package dev.banzetta.droplight.mixin;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.*;
import dev.banzetta.droplight.config.*;
import java.util.*;
import dev.banzetta.droplight.*;
import java.util.stream.*;
import net.minecraft.*;

@Mixin({ Entity.class })
public class EntityMixin
{
    @Shadow
    @Final
    protected Random random;
    
    @Inject(method = { "getBoundingBoxForCulling" }, at = { @At("HEAD") }, cancellable = true)
    public void getBoundingBoxForCulling(final CallbackInfoReturnable<Box> info) {
        final Entity self = (Entity)this;
        if (self instanceof ItemEntity) {
            Box aabb = self.getBoundingBox();
            aabb = aabb.withMaxY(aabb.maxY + 20.0);
            info.setReturnValue((Object)aabb);
        }
    }
    
    @Inject(method = { "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;" }, at = { @At("HEAD") }, cancellable = true)
    public void throwItemUpOnDeath(final ItemStack itemStack, final float height, final CallbackInfoReturnable<ItemEntity> info) {
        if (!(boolean)DroplightConfig.INSTANCE.tossDrops.get()) {
            return;
        }
        final Entity self = (Entity)this;
        final World level = self.getWorld();
        if (level.isClient || itemStack.isEmpty()) {
            info.setReturnValue((Object)null);
        }
        final boolean originatedFromDeath = (boolean)StackWalker.getInstance((Set)EnumSet.allOf((Class)StackWalker.Option.class)).walk(stream -> stream.limit(40L).anyMatch(frame -> frame.getDeclaringClass().equals(LivingEntity.class) && frame.getMethodName().contentEquals((CharSequence)"onDeath")));
        if (!originatedFromDeath) {
            return;
        }
        Droplight.queueDrop(self.getId(), new Droplight.DroppedItem(level, self.getX(), self.getY() + height, self.getZ(), itemStack, (int)DroplightConfig.INSTANCE.dropDelay.get()));
        info.setReturnValue((Object)null);
    }
}
