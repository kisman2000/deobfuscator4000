package dev.banzetta.droplight.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import dev.banzetta.droplight.config.*;
import dev.banzetta.droplight.sound.*;
import net.minecraft.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ Entity.class })
public class EntityMixin
{
    @Inject(method = { "checkFallDamage" }, at = { @At("HEAD") }, cancellable = true)
    protected void playDropSound(final double distance, final boolean hitGround, final BlockState blockState, final BlockPos blockPos, final CallbackInfo info) {
        final Entity self = (Entity)this;
        if (hitGround && self.fallDistance > 0.0f) {
            final World level = self.getWorld();
            final MinecraftClient minecraft = MinecraftClient.getInstance();
            if (self instanceof final ItemEntity itemEntity) {
                if (level.isClient) {
                    itemEntity.getWorld().playSound((PlayerEntity)minecraft.player, itemEntity.getBlockPos(), DroplightConfig.getItemSound(itemEntity.getStack()), SoundCategory.field_15254, 1.0f, MathHelper.nextBetween(level.random, 0.9f, 1.1f));
                    if (DroplightConfig.shouldRenderBeam(itemEntity.getStack())) {
                        minecraft.getSoundManager().play((SoundInstance)new BeamSoundInstance(itemEntity));
                    }
                }
            }
        }
    }
}
