package dev.banzetta.droplight.registry;

import net.minecraft.*;
import net.fabricmc.fabric.api.particle.v1.*;

public class ParticleRegistry
{
    public static final DefaultParticleType SPARKLE_PARTICLE;
    
    static {
        SPARKLE_PARTICLE = (DefaultParticleType)Registry.register(Registry.PARTICLE_TYPE, new Identifier("droplight", "sparkle"), (Object)FabricParticleTypes.simple());
    }
}
