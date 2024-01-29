package dev.banzetta.droplight.sound;

import net.minecraft.*;
import dev.banzetta.droplight.config.*;

public class BeamSoundInstance extends MovingSoundInstance
{
    private static final Random random;
    private final ItemEntity itemEntity;
    private int ticks;
    
    public BeamSoundInstance(final ItemEntity itemEntity) {
        super(SoundEvents.LEGENDARY_HUM, SoundCategory.field_15254, BeamSoundInstance.random);
        this.ticks = 0;
        this.itemEntity = itemEntity;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
        this.pitch = MathHelper.nextBetween(BeamSoundInstance.random, 0.9f, 1.1f);
        this.x = (float)itemEntity.getX();
        this.y = (float)itemEntity.getY();
        this.z = (float)itemEntity.getZ();
    }
    
    public boolean canPlay() {
        return (boolean)DroplightConfig.INSTANCE.beamSound.get();
    }
    
    public boolean shouldAlwaysPlay() {
        return true;
    }
    
    public void tick() {
        if (this.itemEntity.isRemoved() || !this.itemEntity.isOnGround()) {
            this.setDone();
            return;
        }
        ++this.ticks;
        this.volume = Math.min(this.ticks / 20.0f, 5.0f) / 5.0f;
    }
    
    static {
        random = Random.create();
    }
}
