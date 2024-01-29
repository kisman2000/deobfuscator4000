package dev.banzetta.droplight.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import dev.banzetta.droplight.render.*;
import com.mojang.blaze3d.systems.*;
import dev.banzetta.droplight.config.*;
import dev.banzetta.droplight.shader.*;
import dev.banzetta.droplight.compat.*;
import java.util.*;
import net.minecraft.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ ParticleManager.class })
public class ParticleEngineMixin implements IDroplightParticleEngine
{
    public boolean sparkleRender;
    @Shadow
    @Final
    private Map<ParticleTextureSheet, Queue<Particle>> particles;
    @Shadow
    @Final
    private TextureManager textureManager;
    
    public ParticleEngineMixin() {
        this.sparkleRender = false;
    }
    
    public void renderSparkles(final MatrixStack poseStack, final VertexConsumerProvider.class_4598 bufferSource, final LightmapTextureManager lightTexture, final Camera camera, final float partialTick) {
        final ParticleManager self = (ParticleManager)this;
        this.sparkleRender = true;
        self.renderParticles(poseStack, bufferSource, lightTexture, camera, partialTick);
        this.sparkleRender = false;
    }
    
    @Inject(method = { "render" }, at = { @At("HEAD") }, cancellable = true)
    public void renderSparkleParticles(final MatrixStack poseStack, final VertexConsumerProvider.class_4598 bufferSource, final LightmapTextureManager lightTexture, final Camera camera, final float partialTick, final CallbackInfo info) {
        final Iterable<Particle> iterable = (Iterable<Particle>)this.particles.get((Object)SparkleParticle.SPARKLE_RENDER_TYPE);
        if (this.sparkleRender && iterable != null) {
            lightTexture.enable();
            final MatrixStack poseStack2 = RenderSystem.getModelViewStack();
            poseStack2.push();
            poseStack2.multiplyPositionMatrix(poseStack.peek().getPositionMatrix());
            RenderSystem.applyModelViewMatrix();
            final Tessellator tesselator = Tessellator.getInstance();
            final BufferBuilder bufferBuilder = tesselator.getBuffer();
            RenderSystem.setShader((DroplightConfig.getQuality() != DroplightConfig.ShaderQuality.LOW) ? BeamShaders.getTwoColorFlat() : GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            SparkleParticle.setupForRender();
            if (Iris.canUseCustomShaders()) {
                SparkleParticle.SPARKLE_RENDER_TYPE.begin(bufferBuilder, this.textureManager);
            }
            else {
                bufferBuilder.begin(VertexFormat.class_5596.field_27382, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
            }
            for (final Particle particle : iterable) {
                try {
                    particle.buildGeometry((VertexConsumer)bufferBuilder, camera, partialTick);
                }
                catch (final Throwable throwable) {
                    final CrashReport crashReport = CrashReport.create(throwable, "Rendering Particle");
                    final CrashReportSection addElement;
                    final CrashReportSection crashReportCategory = addElement = crashReport.addElement("Particle being rendered");
                    final String s = "Particle";
                    final Particle Particle = particle;
                    Objects.requireNonNull((Object)Particle);
                    addElement.add(s, Particle::toString);
                    final CrashReportSection CloudParticle = crashReportCategory;
                    final String s2 = "Particle Type";
                    final ParticleTextureSheet sparkle_RENDER_TYPE = SparkleParticle.SPARKLE_RENDER_TYPE;
                    Objects.requireNonNull((Object)sparkle_RENDER_TYPE);
                    CloudParticle.add(s2, sparkle_RENDER_TYPE::toString);
                    throw new CrashException(crashReport);
                }
            }
            SparkleParticle.SPARKLE_RENDER_TYPE.draw(tesselator);
            poseStack2.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            lightTexture.disable();
            info.cancel();
        }
    }
}
