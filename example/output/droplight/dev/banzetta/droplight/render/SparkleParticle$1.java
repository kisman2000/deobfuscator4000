package dev.banzetta.droplight.render;

import dev.banzetta.droplight.shader.*;
import net.minecraft.*;
import com.mojang.blaze3d.systems.*;

class SparkleParticle$1 implements ParticleTextureSheet {
    public void begin(final BufferBuilder bufferBuilder, final TextureManager textureManager) {
        bufferBuilder.begin(VertexFormat.class_5596.field_27382, BeamShaders.POSITION_TEX_COLOR0_COLOR1);
    }
    
    public void draw(final Tessellator tesselator) {
        tesselator.draw();
        RenderSystem.disableCull();
    }
    
    public String toString() {
        return "SPARKLE_RENDER_TYPE";
    }
}