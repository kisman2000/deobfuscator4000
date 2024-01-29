package dev.banzetta.droplight.sound;

import net.minecraft.*;

public class SoundEvents
{
    private static final Identifier SWISH_ID;
    private static final Identifier LEGENDARY_HUM_ID;
    private static final Identifier DROP_LEGENDARY_ID;
    private static final Identifier DROP_LIGHT_ID;
    private static final Identifier DROP_MEDIUM_SOFT_ID;
    private static final Identifier DROP_MEDIUM_HARD_ID;
    private static final Identifier DROP_HEAVY_ID;
    private static final Identifier DROP_TOOL_ID;
    private static final Identifier DROP_BLOCK_ID;
    private static final Identifier DROP_WOOD_ID;
    private static final Identifier DROP_GEM_ID;
    private static final Identifier DROP_GLASS_ID;
    private static final Identifier DROP_WET_ID;
    private static final Identifier DROP_MISC1_ID;
    private static final Identifier DROP_MISC2_ID;
    private static final Identifier DROP_MISC3_ID;
    public static final SoundEvent SWISH;
    public static final SoundEvent LEGENDARY_HUM;
    public static final SoundEvent DROP_LEGENDARY;
    public static final SoundEvent DROP_LIGHT;
    public static final SoundEvent DROP_MEDIUM_SOFT;
    public static final SoundEvent DROP_MEDIUM_HARD;
    public static final SoundEvent DROP_HEAVY;
    public static final SoundEvent DROP_TOOL;
    public static final SoundEvent DROP_BLOCK;
    public static final SoundEvent DROP_WOOD;
    public static final SoundEvent DROP_GEM;
    public static final SoundEvent DROP_GLASS;
    public static final SoundEvent DROP_WET;
    public static final SoundEvent DROP_MISC1;
    public static final SoundEvent DROP_MISC2;
    public static final SoundEvent DROP_MISC3;
    
    public static void init() {
        Registry.register(Registry.SOUND_EVENT, SoundEvents.SWISH_ID, (Object)SoundEvents.SWISH);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.LEGENDARY_HUM_ID, (Object)SoundEvents.LEGENDARY_HUM);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_LEGENDARY_ID, (Object)SoundEvents.DROP_LEGENDARY);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_LIGHT_ID, (Object)SoundEvents.DROP_LIGHT);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_MEDIUM_SOFT_ID, (Object)SoundEvents.DROP_MEDIUM_SOFT);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_MEDIUM_HARD_ID, (Object)SoundEvents.DROP_MEDIUM_HARD);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_HEAVY_ID, (Object)SoundEvents.DROP_HEAVY);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_TOOL_ID, (Object)SoundEvents.DROP_TOOL);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_BLOCK_ID, (Object)SoundEvents.DROP_BLOCK);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_WOOD_ID, (Object)SoundEvents.DROP_WOOD);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_GEM_ID, (Object)SoundEvents.DROP_GEM);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_GLASS_ID, (Object)SoundEvents.DROP_GLASS);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_WET_ID, (Object)SoundEvents.DROP_WET);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_MISC1_ID, (Object)SoundEvents.DROP_MISC1);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_MISC2_ID, (Object)SoundEvents.DROP_MISC2);
        Registry.register(Registry.SOUND_EVENT, SoundEvents.DROP_MISC3_ID, (Object)SoundEvents.DROP_MISC3);
    }
    
    static {
        SWISH_ID = new Identifier("droplight", "item.misc.swish");
        LEGENDARY_HUM_ID = new Identifier("droplight", "item.misc.legendary_hum");
        DROP_LEGENDARY_ID = new Identifier("droplight", "item.misc.drop_legendary");
        DROP_LIGHT_ID = new Identifier("droplight", "item.misc.drop_light");
        DROP_MEDIUM_SOFT_ID = new Identifier("droplight", "item.misc.drop_medium_soft");
        DROP_MEDIUM_HARD_ID = new Identifier("droplight", "item.misc.drop_medium_hard");
        DROP_HEAVY_ID = new Identifier("droplight", "item.misc.drop_heavy");
        DROP_TOOL_ID = new Identifier("droplight", "item.misc.drop_tool");
        DROP_BLOCK_ID = new Identifier("droplight", "item.misc.drop_block");
        DROP_WOOD_ID = new Identifier("droplight", "item.misc.drop_wood");
        DROP_GEM_ID = new Identifier("droplight", "item.misc.drop_gem");
        DROP_GLASS_ID = new Identifier("droplight", "item.misc.drop_glass");
        DROP_WET_ID = new Identifier("droplight", "item.misc.drop_wet");
        DROP_MISC1_ID = new Identifier("droplight", "item.misc.drop_misc1");
        DROP_MISC2_ID = new Identifier("droplight", "item.misc.drop_misc2");
        DROP_MISC3_ID = new Identifier("droplight", "item.misc.drop_misc3");
        SWISH = new SoundEvent(SoundEvents.SWISH_ID);
        LEGENDARY_HUM = new SoundEvent(SoundEvents.LEGENDARY_HUM_ID);
        DROP_LEGENDARY = new SoundEvent(SoundEvents.DROP_LEGENDARY_ID);
        DROP_LIGHT = new SoundEvent(SoundEvents.DROP_LIGHT_ID);
        DROP_MEDIUM_SOFT = new SoundEvent(SoundEvents.DROP_MEDIUM_SOFT_ID);
        DROP_MEDIUM_HARD = new SoundEvent(SoundEvents.DROP_MEDIUM_HARD_ID);
        DROP_HEAVY = new SoundEvent(SoundEvents.DROP_HEAVY_ID);
        DROP_TOOL = new SoundEvent(SoundEvents.DROP_TOOL_ID);
        DROP_BLOCK = new SoundEvent(SoundEvents.DROP_BLOCK_ID);
        DROP_WOOD = new SoundEvent(SoundEvents.DROP_WOOD_ID);
        DROP_GEM = new SoundEvent(SoundEvents.DROP_GEM_ID);
        DROP_GLASS = new SoundEvent(SoundEvents.DROP_GLASS_ID);
        DROP_WET = new SoundEvent(SoundEvents.DROP_WET_ID);
        DROP_MISC1 = new SoundEvent(SoundEvents.DROP_MISC1_ID);
        DROP_MISC2 = new SoundEvent(SoundEvents.DROP_MISC2_ID);
        DROP_MISC3 = new SoundEvent(SoundEvents.DROP_MISC3_ID);
    }
}
