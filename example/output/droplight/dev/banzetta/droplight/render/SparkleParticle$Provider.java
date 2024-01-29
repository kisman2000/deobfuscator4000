package dev.banzetta.droplight.render;

import net.minecraft.*;

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
