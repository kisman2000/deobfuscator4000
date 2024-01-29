package dev.banzetta.droplight;

import net.fabricmc.api.*;
import java.util.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.resource.*;
import net.fabricmc.fabric.api.client.particle.v1.*;
import dev.banzetta.droplight.registry.*;
import dev.banzetta.droplight.render.*;
import dev.banzetta.droplight.shader.*;
import net.minecraft.*;
import dev.banzetta.droplight.config.*;
import com.google.common.collect.*;

public class DroplightClient implements ClientModInitializer, SimpleSynchronousResourceReloadListener
{
    public static final Map<ItemEntity, MatrixStack> VISIBLE_ITEMS;
    
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register((Object)DroplightClient::onTick);
        ResourceManagerHelper.get(ResourceType.field_14188).registerReloadListener((IdentifiableResourceReloadListener)this);
        ParticleFactoryRegistry.getInstance().register((ParticleType)ParticleRegistry.SPARKLE_PARTICLE, SparkleParticle.Provider::new);
    }
    
    public static void onTick(final MinecraftClient server) {
        DroplightClient.VISIBLE_ITEMS.clear();
    }
    
    public Identifier getFabricId() {
        return new Identifier("droplight", "shaders");
    }
    
    public void reload(final ResourceManager resourceManager) {
        BeamShaders.init((ResourceFactory)resourceManager);
        DroplightConfig.clearCaches();
    }
    
    static {
        VISIBLE_ITEMS = (Map)Maps.newHashMap();
    }
}
