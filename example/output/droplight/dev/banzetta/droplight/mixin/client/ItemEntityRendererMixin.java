package dev.banzetta.droplight.mixin.client;

import org.spongepowered.asm.mixin.*;
import java.util.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import dev.banzetta.droplight.config.*;
import dev.banzetta.droplight.registry.*;
import dev.banzetta.droplight.render.*;
import net.minecraft.*;
import com.mojang.datafixers.util.*;
import org.spongepowered.asm.mixin.injection.*;
import com.google.common.collect.*;

@Mixin({ ItemEntityRenderer.class })
public class ItemEntityRendererMixin
{
    @Unique
    private static final Random random;
    private static final Map<ItemEntity, Long> lastCheckedTicks;
    
    @Inject(method = { "render" }, at = { @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V", shift = At.Shift.AFTER) })
    public void render(final ItemEntity itemEntity, final float interpolatedRot, final float partialTick, final MatrixStack poseStack, final VertexConsumerProvider multiBufferSource, final int packedLightCoords, final CallbackInfo info) {
        if ((boolean)DroplightConfig.INSTANCE.itemFlipping.get() && !itemEntity.isOnGround() && !itemEntity.isInsideWaterOrBubbleColumn()) {
            poseStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(itemEntity.getRotation(partialTick) * 16.5f));
            poseStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(itemEntity.getRotation(partialTick) * 9.0f));
        }
        final World level = itemEntity.world;
        if (level.getTime() > (long)ItemEntityRendererMixin.lastCheckedTicks.getOrDefault((Object)itemEntity, (Object)0L)) {
            ItemEntityRendererMixin.lastCheckedTicks.put((Object)itemEntity, (Object)level.getTime());
            if (ParticleRegistry.SPARKLE_PARTICLE != null && (DroplightConfig.INSTANCE.sparklesEnabled.get() == DroplightConfig.SparklesRendered.ALL || (DroplightConfig.INSTANCE.sparklesEnabled.get() == DroplightConfig.SparklesRendered.BEAMS_ONLY && DroplightConfig.shouldRenderBeam(itemEntity.getStack()))) && ItemEntityRendererMixin.random.nextInt(100) < 20) {
                final double x = itemEntity.getX();
                final double y = itemEntity.getY() - 0.25;
                final double z = itemEntity.getZ();
                final Particle particle = ((ClientWorld)level).worldRenderer.spawnParticle((ParticleEffect)ParticleRegistry.SPARKLE_PARTICLE, ParticleRegistry.SPARKLE_PARTICLE.getType().shouldAlwaysSpawn(), x, y, z, 0.0, 0.0, 0.0);
                if (particle instanceof final SparkleParticle sparkleParticle) {
                    final Pair<TextColor, TextColor> colors = DroplightConfig.getItemColors(itemEntity.getStack(), TextColor.fromFormatting(Formatting.field_1068));
                    sparkleParticle.setColors(TextColor.fromFormatting(Formatting.field_1068), (TextColor)colors.getFirst());
                }
            }
        }
    }
    
    static {
        random = Random.create();
        lastCheckedTicks = (Map)Maps.newHashMap();
    }
}
