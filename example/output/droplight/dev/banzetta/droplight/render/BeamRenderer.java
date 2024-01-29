package dev.banzetta.droplight.render;

import dev.banzetta.droplight.*;
import dev.banzetta.droplight.config.*;
import java.util.*;
import dev.banzetta.droplight.shader.*;
import com.mojang.blaze3d.systems.*;
import com.mojang.datafixers.util.*;
import java.util.function.*;
import net.minecraft.*;
import com.google.common.collect.*;

public class BeamRenderer extends RenderLayer
{
    private static final Identifier LIGHT_BEAM_TEXTURE;
    private static final Identifier LIGHT_BEAM_LOW_TEXTURE;
    private static final Map<ItemEntity, Integer> ITEM_GROUND_START_TIMES;
    
    public BeamRenderer(final String string, final VertexFormat vertexFormat, final VertexFormat.class_5596 mode, final int i, final boolean bl, final boolean bl2, final Runnable runnable, final Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }
    
    public static void renderBeams(final float partialTicks) {
        int index = 0;
        for (final ItemEntity itemEntity : DroplightClient.VISIBLE_ITEMS.keySet()) {
            if (itemEntity.isOnGround()) {
                if (!BeamRenderer.ITEM_GROUND_START_TIMES.containsKey((Object)itemEntity)) {
                    BeamRenderer.ITEM_GROUND_START_TIMES.put((Object)itemEntity, (Object)itemEntity.getItemAge());
                }
                if (!DroplightConfig.shouldRenderBeam(itemEntity.getStack())) {
                    continue;
                }
                renderBeam(itemEntity, (MatrixStack)DroplightClient.VISIBLE_ITEMS.get((Object)itemEntity), partialTicks, itemEntity.world.getTime(), index++);
            }
        }
        BeamRenderer.ITEM_GROUND_START_TIMES.keySet().removeIf(itemEntity -> !DroplightClient.VISIBLE_ITEMS.containsKey((Object)itemEntity));
    }
    
    private static void renderBeam(final ItemEntity itemEntity, final MatrixStack poseStack, final float partialTick, final long gameTime, final int index) {
        if (!BeamShaders.ready()) {
            return;
        }
        boolean useCustomShaders = true;
        if (DroplightConfig.getQuality() == DroplightConfig.ShaderQuality.LOW) {
            useCustomShaders = false;
        }
        final MinecraftClient minecraft = MinecraftClient.getInstance();
        final Camera camera = minecraft.gameRenderer.getCamera();
        final float currentGroundTime = itemEntity.getItemAge() - (int)BeamRenderer.ITEM_GROUND_START_TIMES.getOrDefault((Object)itemEntity, (Object)0) + partialTick;
        final Pair<TextColor, TextColor> colors = DroplightConfig.getItemColors(itemEntity.getStack(), TextColor.fromFormatting(Formatting.field_1068));
        float beamHeight = ((Double)DroplightConfig.INSTANCE.beamHeight.get()).floatValue();
        beamHeight = MathHelper.lerp(Math.min(currentGroundTime, 3.0f) / 3.0f, 0.0f, beamHeight);
        float widthMultiplier = Math.min(currentGroundTime, 5.0f) / 5.0f;
        widthMultiplier = (MathHelper.cos((float)(widthMultiplier * 3.141592653589793 + 3.141592653589793)) + 3.0f) / 4.0f;
        final float alpha = ((float)Math.cos(currentGroundTime * 3.141592653589793 / 100.0) + 7.0f) / 8.0f;
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.depthMask(false);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableBlend();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        if (DroplightConfig.INSTANCE.pixelatedMode.get()) {
            minecraft.getTextureManager().getTexture(BeamRenderer.LIGHT_BEAM_LOW_TEXTURE).setFilter(false, false);
            RenderSystem.setShaderTexture(0, BeamRenderer.LIGHT_BEAM_LOW_TEXTURE);
        }
        else {
            minecraft.getTextureManager().getTexture(BeamRenderer.LIGHT_BEAM_TEXTURE).setFilter(true, false);
            RenderSystem.setShaderTexture(0, BeamRenderer.LIGHT_BEAM_TEXTURE);
        }
        if (useCustomShaders) {
            RenderSystem.setShaderTexture(1, minecraft.getFramebuffer().getDepthAttachment());
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        }
        else {
            RenderSystem.setShaderColor((((TextColor)colors.getFirst()).getRgb() >> 16 & 0xFF) / 255.0f, (((TextColor)colors.getFirst()).getRgb() >> 8 & 0xFF) / 255.0f, (((TextColor)colors.getFirst()).getRgb() >> 0 & 0xFF) / 255.0f, 0.6f);
        }
        poseStack.push();
        poseStack.translate(0.0, 0.0625, 0.0);
        poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
        renderFlatQuad(poseStack, (TextColor)colors.getFirst(), (TextColor)colors.getSecond(), 0.0f, 0.0f, 0.001f * index, 3.0f, 3.0f, 0.5625f, 0.0f, 1.0f, 0.4375f, useCustomShaders);
        poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));
        if (!useCustomShaders) {
            RenderSystem.setShaderColor((((TextColor)colors.getFirst()).getRgb() >> 16 & 0xFF) / 255.0f, (((TextColor)colors.getFirst()).getRgb() >> 8 & 0xFF) / 255.0f, (((TextColor)colors.getFirst()).getRgb() >> 0 & 0xFF) / 255.0f, 1.0f);
        }
        RenderSystem.disableCull();
        poseStack.push();
        poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
        renderDepthQuad(poseStack, (TextColor)colors.getFirst(), (TextColor)colors.getSecond(), 0.0f, 1.0f, -0.001f, 1.5f * widthMultiplier, 2.5f, 0.26367188f, 0.0f, 0.5175781f, 0.8125f, useCustomShaders);
        if (beamHeight > 0.0f) {
            renderDepthQuad(poseStack, (TextColor)colors.getFirst(), (TextColor)colors.getSecond(), 0.0f, beamHeight / 2.0f, 0.0f, 1.0f * widthMultiplier, beamHeight, 0.0f, 0.0f, 0.17578125f, 0.7421875f, useCustomShaders);
        }
        poseStack.pop();
        poseStack.pop();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
    
    private static void renderFlatQuad(final MatrixStack poseStack, final TextColor color, final TextColor color2, final float x, final float y, final float z, final float w, final float h, final float u, final float v, final float u2, final float v2, final boolean customShaders) {
        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        if (customShaders) {
            RenderSystem.setShader((Supplier)BeamShaders.getTwoColorFadeFlat());
            bufferBuilder.begin(VertexFormat.class_5596.field_27382, BeamShaders.POSITION_TEX_COLOR0_COLOR1);
        }
        else {
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.enableBlend();
            bufferBuilder.begin(VertexFormat.class_5596.field_27382, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
        }
        renderQuad(poseStack, bufferBuilder, color, color2, x, y, z, w, h, u, v, u2, v2, false, customShaders);
        Tessellator.getInstance().draw();
    }
    
    private static void renderDepthQuad(final MatrixStack poseStack, final TextColor color, final TextColor color2, final float x, final float y, final float z, final float w, final float h, final float u, final float v, final float u2, final float v2, final boolean customShaders) {
        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        if (customShaders) {
            RenderSystem.setShader((Supplier)((DroplightConfig.getQuality() == DroplightConfig.ShaderQuality.HIGH) ? BeamShaders.getTwoColorFade() : BeamShaders.getTwoColorFadeFlat()));
            bufferBuilder.begin(VertexFormat.class_5596.field_27382, BeamShaders.POSITION_TEX_COLOR0_COLOR1);
        }
        else {
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.enableBlend();
            bufferBuilder.begin(VertexFormat.class_5596.field_27382, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
        }
        renderQuad(poseStack, bufferBuilder, color, color2, x, y, z, w, h, u, v, u2, v2, true, customShaders);
        Tessellator.getInstance().draw();
    }
    
    private static void renderQuad(final MatrixStack poseStack, final BufferBuilder builder, final TextColor color, final TextColor color2, final float x, final float y, final float z, final float w, final float h, final float u, final float v, final float u2, final float v2, final boolean fade, final boolean customShaders) {
        final Matrix4f pose = poseStack.peek().getPositionMatrix();
        final float alpha = 1.0f;
        final float alpha2 = fade ? 0.0f : alpha;
        final float red = (color.getRgb() >> 16 & 0xFF) / 255.0f;
        final float green = (color.getRgb() >> 8 & 0xFF) / 255.0f;
        final float blue = (color.getRgb() >> 0 & 0xFF) / 255.0f;
        if (customShaders) {
            final float red2 = (color2.getRgb() >> 16 & 0xFF) / 255.0f;
            final float green2 = (color2.getRgb() >> 8 & 0xFF) / 255.0f;
            final float blue2 = (color2.getRgb() >> 0 & 0xFF) / 255.0f;
            builder.vertex(pose, x - w / 2.0f, y - h / 2.0f, z).texture(u, v2).color(red, green, blue, alpha).color(red2, green2, blue2, alpha).next();
            builder.vertex(pose, x + w / 2.0f, y - h / 2.0f, z).texture(u2, v2).color(red, green, blue, alpha).color(red2, green2, blue2, alpha).next();
            builder.vertex(pose, x + w / 2.0f, y + h / 2.0f, z).texture(u2, v).color(red, green, blue, alpha2).color(red2, green2, blue2, alpha2).next();
            builder.vertex(pose, x - w / 2.0f, y + h / 2.0f, z).texture(u, v).color(red, green, blue, alpha2).color(red2, green2, blue2, alpha2).next();
        }
        else {
            builder.vertex(pose, x - w / 2.0f, y - h / 2.0f, z).color(1.0f, 1.0f, 1.0f, alpha * 0.6f).texture(u, v2).light(15728880).next();
            builder.vertex(pose, x + w / 2.0f, y - h / 2.0f, z).color(1.0f, 1.0f, 1.0f, alpha * 0.6f).texture(u2, v2).light(15728880).next();
            builder.vertex(pose, x + w / 2.0f, y + h / 2.0f, z).color(1.0f, 1.0f, 1.0f, alpha2 * 0.6f).texture(u2, v).light(15728880).next();
            builder.vertex(pose, x - w / 2.0f, y + h / 2.0f, z).color(1.0f, 1.0f, 1.0f, alpha2 * 0.6f).texture(u, v).light(15728880).next();
        }
    }
    
    static {
        LIGHT_BEAM_TEXTURE = new Identifier("droplight", "textures/misc/lightalpha.png");
        LIGHT_BEAM_LOW_TEXTURE = new Identifier("droplight", "textures/misc/lightalphalow.png");
        ITEM_GROUND_START_TIMES = (Map)Maps.newHashMap();
    }
}
