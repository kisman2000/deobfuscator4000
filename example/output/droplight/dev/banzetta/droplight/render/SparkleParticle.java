package dev.banzetta.droplight.render;

import com.mojang.blaze3d.systems.*;
import dev.banzetta.droplight.config.*;
import dev.banzetta.droplight.shader.*;
import net.minecraft.*;

public class SparkleParticle extends SpriteBillboardParticle
{
    public static final ParticleTextureSheet SPARKLE_RENDER_TYPE;
    private final double xStart;
    private final double zStart;
    private final float spiralScale;
    private final float spiralRigidity;
    private final float spiralStart;
    private final boolean backwards;
    private float red2;
    private float green2;
    private float blue2;
    
    public static void setupForRender() {
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.depthMask(false);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableBlend();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
    }
    
    protected SparkleParticle(final ClientWorld clientLevel, final double x, final double y, final double z, final double xDelta, final double yDelta, final double zDelta) {
        super(clientLevel, x, y, z);
        this.red2 = 1.0f;
        this.green2 = 1.0f;
        this.blue2 = 1.0f;
        this.velocityY = this.random.nextDouble() * 0.0125 + 0.0175;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xStart = this.x;
        this.zStart = this.z;
        this.spiralScale = this.random.nextFloat() * 0.325f + 0.005f;
        this.spiralRigidity = this.random.nextFloat() * 0.09f + 0.001f;
        this.spiralStart = this.random.nextFloat();
        this.backwards = this.random.nextBoolean();
        this.scale = 0.15f * (this.random.nextFloat() * 0.25f + 0.5f);
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.alpha = 0.0f;
        this.maxAge = (int)(Math.random() * 70.0) + 30;
        this.velocityMultiplier = 0.94f;
    }
    
    public void setColors(final TextColor color, final TextColor color2) {
        this.red = (color.getRgb() >> 16 & 0xFF) / 255.0f;
        this.green = (color.getRgb() >> 8 & 0xFF) / 255.0f;
        this.blue = (color.getRgb() & 0xFF) / 255.0f;
        this.red2 = (color2.getRgb() >> 16 & 0xFF) / 255.0f;
        this.green2 = (color2.getRgb() >> 8 & 0xFF) / 255.0f;
        this.blue2 = (color2.getRgb() & 0xFF) / 255.0f;
    }
    
    public ParticleTextureSheet getType() {
        return SparkleParticle.SPARKLE_RENDER_TYPE;
    }
    
    public void buildGeometry(final VertexConsumer vertexConsumer, final Camera camera, final float f) {
        final Vec3d vec3 = camera.getPos();
        final float g = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - vec3.getX());
        final float h = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - vec3.getY());
        final float i = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - vec3.getZ());
        Quaternion quaternion;
        if (this.angle == 0.0f) {
            quaternion = camera.getRotation();
        }
        else {
            quaternion = new Quaternion(camera.getRotation());
            final float j = MathHelper.lerp(f, this.prevAngle, this.angle);
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Z.getRadialQuaternion(j));
        }
        final Vec3f vector3f = new Vec3f(-1.0f, -1.0f, 0.0f);
        vector3f.rotate(quaternion);
        final Vec3f[] vector3fs = { new Vec3f(-1.0f, -1.0f, 0.0f), new Vec3f(-1.0f, 1.0f, 0.0f), new Vec3f(1.0f, 1.0f, 0.0f), new Vec3f(1.0f, -1.0f, 0.0f) };
        final float k = this.getSize(f);
        for (int l = 0; l < 4; ++l) {
            final Vec3f vector3f2 = vector3fs[l];
            vector3f2.rotate(quaternion);
            vector3f2.scale(k);
            vector3f2.add(g, h, i);
        }
        final float m = this.method_18133();
        final float n = this.method_18134();
        final float o = this.method_18135();
        final float p = this.method_18136();
        if (DroplightConfig.getQuality() != DroplightConfig.ShaderQuality.LOW) {
            vertexConsumer.vertex((double)vector3fs[0].getX(), (double)vector3fs[0].getY(), (double)vector3fs[0].getZ()).texture(n, p).color(this.red, this.green, this.blue, this.alpha).color(this.red2, this.green2, this.blue2, this.alpha).next();
            vertexConsumer.vertex((double)vector3fs[1].getX(), (double)vector3fs[1].getY(), (double)vector3fs[1].getZ()).texture(n, o).color(this.red, this.green, this.blue, this.alpha).color(this.red2, this.green2, this.blue2, this.alpha).next();
            vertexConsumer.vertex((double)vector3fs[2].getX(), (double)vector3fs[2].getY(), (double)vector3fs[2].getZ()).texture(m, o).color(this.red, this.green, this.blue, this.alpha).color(this.red2, this.green2, this.blue2, this.alpha).next();
            vertexConsumer.vertex((double)vector3fs[3].getX(), (double)vector3fs[3].getY(), (double)vector3fs[3].getZ()).texture(m, p).color(this.red, this.green, this.blue, this.alpha).color(this.red2, this.green2, this.blue2, this.alpha).next();
        }
        else {
            vertexConsumer.vertex((double)vector3fs[0].getX(), (double)vector3fs[0].getY(), (double)vector3fs[0].getZ()).color(this.red, this.green, this.blue, this.alpha).texture(n, p).light(15728880).next();
            vertexConsumer.vertex((double)vector3fs[1].getX(), (double)vector3fs[1].getY(), (double)vector3fs[1].getZ()).color(this.red, this.green, this.blue, this.alpha).texture(n, o).light(15728880).next();
            vertexConsumer.vertex((double)vector3fs[2].getX(), (double)vector3fs[2].getY(), (double)vector3fs[2].getZ()).color(this.red, this.green, this.blue, this.alpha).texture(m, o).light(15728880).next();
            vertexConsumer.vertex((double)vector3fs[3].getX(), (double)vector3fs[3].getY(), (double)vector3fs[3].getZ()).color(this.red, this.green, this.blue, this.alpha).texture(m, p).light(15728880).next();
        }
    }
    
    public void move(final double x, final double y, final double z) {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.repositionFromBoundingBox();
    }
    
    public float getSize(final float partialTick) {
        return this.scale;
    }
    
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        final float alphaInterval = Math.max(0.0f, this.age - 15.0f) / (this.maxAge - 15.0f);
        this.alpha = -(float)(Math.pow(2.0 * alphaInterval - 1.0, 2.0) - 1.0);
        final float interval = this.age / 30.0f;
        float theta = interval * 6.2831855f;
        if (this.backwards) {
            theta = -theta;
        }
        final Vec3f location = new Vec3f((float)(this.spiralScale * Math.cos((double)theta) * Math.pow(2.718281828459045, (double)(this.spiralRigidity * theta))), 0.0f, (float)(this.spiralScale * Math.sin((double)theta) * Math.pow(2.718281828459045, (double)(this.spiralRigidity * theta))));
        location.rotate(Vec3f.POSITIVE_Y.getRadialQuaternion(this.spiralStart * 2.0f * 3.1415927f));
        this.x = this.xStart + this.spiralScale * location.getX();
        this.y += this.velocityY;
        this.z = this.zStart + this.spiralScale * location.getZ();
    }
    
    static {
        SPARKLE_RENDER_TYPE = (ParticleTextureSheet)new ParticleTextureSheet() {
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
        };
    }
    
    public static class Provider implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider sprite;
        
        public Provider(final SpriteProvider spriteSet) {
            this.sprite = spriteSet;
        }
        
        public Particle createParticle(final DefaultParticleType simpleParticleType, final ClientWorld clientLevel, final double x, final double y, final double z, final double xDelta, final double yDelta, final double zDelta) {
            final SparkleParticle sparkleParticle = new SparkleParticle(clientLevel, x, y, z, xDelta, yDelta, zDelta);
            sparkleParticle.setSprite(this.sprite);
            return (Particle)sparkleParticle;
        }
    }
}
