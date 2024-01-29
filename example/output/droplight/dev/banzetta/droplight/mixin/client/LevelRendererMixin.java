package dev.banzetta.droplight.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.*;
import com.mojang.blaze3d.systems.*;
import dev.banzetta.droplight.render.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ WorldRenderer.class })
public class LevelRendererMixin
{
    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;
    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private ShaderEffect transparencyShader;
    
    @Inject(method = { "renderLevel" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", shift = At.Shift.AFTER) })
    public void renderEffects(final MatrixStack poseStack, final float partialTicks, final long l, final boolean bl, final Camera camera, final GameRenderer gameRenderer, final LightmapTextureManager lightTexture, final Matrix4f matrix4f, final CallbackInfo info) {
        if (this.transparencyShader != null) {
            RenderPhase.WEATHER_TARGET.endDrawing();
        }
        RenderSystem.getModelViewStack().pop();
        RenderSystem.applyModelViewMatrix();
        lightTexture.enable();
        BeamRenderer.renderBeams(partialTicks);
        final IDroplightParticleEngine particleEngine = (IDroplightParticleEngine)this.client.particleManager;
        if (particleEngine != null) {
            particleEngine.renderSparkles(poseStack, this.bufferBuilders.getEntityVertexConsumers(), lightTexture, camera, partialTicks);
        }
        RenderSystem.getModelViewStack().push();
        RenderSystem.getModelViewStack().multiplyPositionMatrix(poseStack.peek().getPositionMatrix());
        RenderSystem.applyModelViewMatrix();
        if (this.transparencyShader != null) {
            RenderPhase.WEATHER_TARGET.startDrawing();
        }
    }
}
