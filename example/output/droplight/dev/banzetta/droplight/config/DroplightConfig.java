package dev.banzetta.droplight.config;

import net.minecraftforge.common.*;
import com.electronwill.nightconfig.core.*;
import com.mojang.datafixers.util.*;
import net.minecraft.*;
import com.google.common.collect.*;
import net.minecraftforge.api.fml.event.config.*;
import com.anthonyhilyard.iceberg.util.*;
import com.anthonyhilyard.prism.util.*;
import com.anthonyhilyard.prism.item.*;
import java.util.*;
import dev.banzetta.droplight.sound.*;
import dev.banzetta.droplight.compat.*;
import net.minecraftforge.fml.config.*;
import java.util.stream.*;

public class DroplightConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final DroplightConfig INSTANCE;
    public final ForgeConfigSpec.BooleanValue automaticColor;
    public final ForgeConfigSpec.BooleanValue syncWithItemBorders;
    public final ForgeConfigSpec.DoubleValue beamHeight;
    private final ForgeConfigSpec.ConfigValue<ShaderQuality> quality;
    public final ForgeConfigSpec.BooleanValue pixelatedMode;
    public final ForgeConfigSpec.ConfigValue<List<Config>> colorDefinitionsConfigs;
    public final ForgeConfigSpec.IntValue dropDelay;
    public final ForgeConfigSpec.BooleanValue tossDrops;
    public final ForgeConfigSpec.BooleanValue itemFlipping;
    public final ForgeConfigSpec.ConfigValue<SparklesRendered> sparklesEnabled;
    public final ForgeConfigSpec.BooleanValue dropSwishSound;
    public final ForgeConfigSpec.BooleanValue dropImpactSound;
    public final ForgeConfigSpec.BooleanValue beamSound;
    public final ForgeConfigSpec.ConfigValue<List<Config>> soundDefinitionsConfigs;
    private Map<ItemStack, Pair<TextColor, TextColor>> colorCache;
    private Map<ItemStack, SoundEvent> soundCache;
    private static final List<ColorDefinition> defaultColorDefinitions;
    private static final List<SoundDefinition> defaultSoundDefinitions;
    
    public DroplightConfig(final ForgeConfigSpec.Builder builder) {
        this.colorCache = (Map<ItemStack, Pair<TextColor, TextColor>>)Maps.newHashMap();
        this.soundCache = (Map<ItemStack, SoundEvent>)Maps.newHashMap();
        builder.comment("""
                         DROPLIGHT CONFIGURATION
                          This configuration file controls all aspects of the Droplight mod.
                          Please note that certain parts of this configuration file are controlled by the Iceberg and Prism libraries!
                          Anywhere in this file where you are entering an item, you may enter any Iceberg-supported selector.
                          Likewise, anywhere you are entering a color, you may enter a Prism-supported color definition.
                          The documentation for these can be found at the following webpages:
                          ICEBERG: https://github.com/AHilyard/Iceberg/wiki/Item-Selectors-Documentation
                          PRISM:   https://github.com/AHilyard/Prism/wiki/Prism-Documentation#color-format-options
                        
                         Configure graphics options""").push("Graphics");
        this.sparklesEnabled = (ForgeConfigSpec.ConfigValue<SparklesRendered>)builder.comment(" If items on the ground should have a sparkle effect.").defineEnum("sparklesEnabled", (Enum)SparklesRendered.BEAMS_ONLY);
        this.automaticColor = builder.comment(" If beam/sparkle color should match the item's tooltip name color. (White named items won't have a beam unless specified manually)").define("automaticColor", true);
        this.syncWithItemBorders = builder.comment(" If the Item Borders mod is installed, should beam/sparkle colors match the item's border color.\n (Manual colors specified in this file will take precedence, so remove them if you want Item Borders to control all colors.)").define("syncWithItemBorders", true);
        this.beamHeight = builder.comment(" The height of the beam of light.").defineInRange("beamHeight", 12.0, 0.0, 25.0);
        this.quality = (ForgeConfigSpec.ConfigValue<ShaderQuality>)builder.comment(" The graphical quality of beams and sparkles.  Higher quality means potentially lower performance.  Use low quality if experiencing compatibility issues with other mods.").defineEnum("quality", (Enum)ShaderQuality.HIGH);
        this.pixelatedMode = builder.comment(" If enabled, beams will appear pixelated instead of smooth.").define("pixelatedMode", false);
        builder.comment(" Manual Colors\n Copy/paste one section below and modify to add a new color customization.").push("Customization");
        this.colorDefinitionsConfigs = (ForgeConfigSpec.ConfigValue<List<Config>>)builder.define("manualColor", (Object)DroplightConfig.defaultColorDefinitions.stream().map(ColorDefinition::toConfig).toList(), ColorDefinition::validateList);
        builder.pop();
        builder.pop();
        builder.comment(" Configure item dropping options").push("Drops");
        this.dropDelay = builder.comment(" The minimum delay in ticks between each item drop after killing a mob.  Must be set on server for multiplayer.  (20 ticks = 1 second, 0 = no delay)").defineInRange("dropDelay", 4, 0, 20);
        this.tossDrops = builder.comment(" If items should be tossed into the air when dropped from a slain mob.  Must be set on server for multiplayer.").define("tossDrops", true);
        this.itemFlipping = builder.comment(" If items should rotate wildly when thrown.").define("itemFlipping", true);
        builder.pop();
        builder.comment(" Configure sound options").push("Sound");
        this.dropSwishSound = builder.comment(" If a swish sound should play when items are dropped from slain mobs. (Requires tossDrops is true)").define("dropSwish", true);
        this.dropImpactSound = builder.comment(" If sounds should play when items hit the ground.").define("dropImpact", true);
        this.beamSound = builder.comment(" If beams should emit a constant hum.").define("beamSound", true);
        builder.comment(" Sound customization\n The sections below can be modified or you can copy/paste and modify to add new sound customizations.").push("Customization");
        this.soundDefinitionsConfigs = (ForgeConfigSpec.ConfigValue<List<Config>>)builder.define("soundDefinition", (Object)DroplightConfig.defaultSoundDefinitions.stream().map(SoundDefinition::toConfig).toList(), SoundDefinition::validateList);
        builder.pop();
        builder.pop();
        ModConfigEvent.RELOADING.register((Object)DroplightConfig::onReload);
    }
    
    public static boolean shouldRenderBeam(final ItemStack stack) {
        return ((boolean)DroplightConfig.INSTANCE.automaticColor.get() && ((TextColor)getItemColors(stack, TextColor.fromRgb(16777215)).getFirst()).getRgb() != 16777215) || ((List)DroplightConfig.INSTANCE.colorDefinitionsConfigs.get()).stream().map(ColorDefinition::fromConfig).flatMap(config -> config.items().stream()).anyMatch(selector -> Selectors.itemMatches(stack, selector)) || ((boolean)DroplightConfig.INSTANCE.syncWithItemBorders.get() && ItemBorders.getColorForItem(stack) != null);
    }
    
    public static Pair<TextColor, TextColor> getItemColors(final ItemStack item, final TextColor defaultColor) {
        if (DroplightConfig.INSTANCE.colorCache.containsKey((Object)item)) {
            return (Pair<TextColor, TextColor>)DroplightConfig.INSTANCE.colorCache.get((Object)item);
        }
        TextColor color = null;
        TextColor color2 = null;
        for (final Config colorDefinitionConfig : (List)DroplightConfig.INSTANCE.colorDefinitionsConfigs.get()) {
            final ColorDefinition colorDefinition = ColorDefinition.fromConfig(colorDefinitionConfig);
            for (final String selector : colorDefinition.items()) {
                if (Selectors.itemMatches(item, selector)) {
                    color = (TextColor)ConfigHelper.parseColor((Object)colorDefinition.color());
                    if (color != null) {
                        break;
                    }
                    continue;
                }
            }
        }
        if (color == null && (boolean)DroplightConfig.INSTANCE.syncWithItemBorders.get()) {
            final TextColor itemBordersColor = ItemBorders.getColorForItem(item);
            if (itemBordersColor != null) {
                color = itemBordersColor;
            }
        }
        if (color == null && (boolean)DroplightConfig.INSTANCE.automaticColor.get()) {
            try {
                color = ItemColors.getColorForItem(item, defaultColor);
            }
            catch (final Exception ex) {}
        }
        if (color == null) {
            color = defaultColor;
        }
        if (color2 == null) {
            color2 = ConfigHelper.applyModifiers(List.of((Object)"-v50", (Object)"-h35", (Object)"+s35"), color);
        }
        DroplightConfig.INSTANCE.colorCache.put((Object)item, (Object)Pair.of((Object)color, (Object)color2));
        return (Pair<TextColor, TextColor>)DroplightConfig.INSTANCE.colorCache.get((Object)item);
    }
    
    public static SoundEvent getItemSound(final ItemStack item) {
        if (DroplightConfig.INSTANCE.soundCache.containsKey((Object)item)) {
            return (SoundEvent)DroplightConfig.INSTANCE.soundCache.get((Object)item);
        }
        for (final Config soundDefinitionConfig : (List)DroplightConfig.INSTANCE.soundDefinitionsConfigs.get()) {
            final SoundDefinition soundDefinition = SoundDefinition.fromConfig(soundDefinitionConfig);
            for (final String selector : soundDefinition.items()) {
                if (soundDefinition.sound() != null && Selectors.itemMatches(item, selector)) {
                    DroplightConfig.INSTANCE.soundCache.put((Object)item, (Object)soundDefinition.sound());
                    return (SoundEvent)DroplightConfig.INSTANCE.soundCache.get((Object)item);
                }
            }
        }
        DroplightConfig.INSTANCE.soundCache.put((Object)item, (Object)SoundEvents.DROP_BLOCK);
        return (SoundEvent)DroplightConfig.INSTANCE.soundCache.get((Object)item);
    }
    
    public static ShaderQuality getQuality() {
        if (!Iris.canUseCustomShaders()) {
            return ShaderQuality.LOW;
        }
        return (ShaderQuality)DroplightConfig.INSTANCE.quality.get();
    }
    
    public static void clearCaches() {
        DroplightConfig.INSTANCE.colorCache.clear();
        DroplightConfig.INSTANCE.soundCache.clear();
        Iris.refreshCache();
    }
    
    public static void onReload(final ModConfig config) {
        if (config.getModId().equals((Object)"droplight") || config.getModId().equals((Object)"itemborders")) {
            clearCaches();
        }
    }
    
    static {
        defaultColorDefinitions = List.of((Object)new ColorDefinition((List<String>)List.of(), ""));
        defaultSoundDefinitions = List.of((Object[])new SoundDefinition[] { new SoundDefinition((List<String>)List.of((Object)"!rare", (Object)"!epic"), SoundEvents.DROP_LEGENDARY), new SoundDefinition((List<String>)List.of((Object[])new String[] { "% Sword", "% Pickaxe", "% Axe", "% Hoe", "% Shovel", "%Shears", "% Crossbow", "% Shield", "% Trident", "% Flint and Steel", "%Lantern", "%Bucket", "minecraft:iron_bars", "minecraft:wooden_sword", "minecraft:stone_sword", "minecraft:iron_sword", "minecraft:golden_sword", "minecraft:diamond_sword", "minecraft:netherite_sword", "minecraft:wooden_pickaxe", "minecraft:stone_pickaxe", "minecraft:iron_pickaxe", "minecraft:golden_pickaxe", "minecraft:diamond_pickaxe", "minecraft:netherite_pickaxe", "minecraft:wooden_axe", "minecraft:stone_axe", "minecraft:iron_axe", "minecraft:golden_axe", "minecraft:diamond_axe", "minecraft:netherite_axe", "minecraft:wooden_hoe", "minecraft:stone_hoe", "minecraft:iron_hoe", "minecraft:golden_hoe", "minecraft:diamond_hoe", "minecraft:netherite_hoe", "minecraft:wooden_shovel", "minecraft:stone_shovel", "minecraft:iron_shovel", "minecraft:golden_shovel", "minecraft:diamond_shovel", "minecraft:netherite_shovel", "minecraft:shears", "minecraft:crossbow", "minecraft:shield", "minecraft:trident", "minecraft:flint_and_steel", "minecraft:bucket", "minecraft:lava_bucket", "minecraft:water_bucket", "minecraft:milk_bucket", "minecraft:cod_bucket", "minecraft:salmon_bucket", "minecraft:pufferfish_bucket", "minecraft:tropical_fish_bucket", "minecraft:axolotl_bucket", "minecraft:powder_snow_bucket", "minecraft:spyglass", "minecraft:lantern", "minecraft:soul_lantern" }), SoundEvents.DROP_TOOL), new SoundDefinition((List<String>)List.of((Object)"minecraft:slime_block", (Object)"minecraft:slime_ball", (Object)"minecraft:magma_cream", (Object)"minecraft:honey_block", (Object)"minecraft:wet_sponge", (Object)"minecraft:sea_pickle", (Object)"minecraft:ink_sac"), SoundEvents.DROP_WET), new SoundDefinition((List<String>)List.of((Object)"minecraft:diamond", (Object)"minecraft:emerald", (Object)"minecraft:amethyst_shard", (Object)"minecraft:amethyst_cluster", (Object)"minecraft:small_amethyst_bud", (Object)"minecraft:medium_amethyst_bud", (Object)"minecraft:large_amethyst_bud"), SoundEvents.DROP_GEM), new SoundDefinition((List<String>)List.of((Object[])new String[] { "%Glass", "%glass", "%Ice", "%Bottle", "%Potion", "minecraft:glass_bottle", "minecraft:honey_bottle", "minecraft:experience_bottle", "minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:dragon_breath" }), SoundEvents.DROP_GLASS), new SoundDefinition((List<String>)List.of((Object)"minecraft:leather_helmet", (Object)"minecraft:leather_chestplate", (Object)"minecraft:leather_leggings", (Object)"minecraft:leather_boots", (Object)"minecraft:leather_horse_armor", (Object)"minecraft:saddle"), SoundEvents.DROP_MEDIUM_SOFT), new SoundDefinition((List<String>)List.of((Object)"%Chainmail", (Object)"minecraft:chain", (Object)"minecraft:chainmail_helmet", (Object)"minecraft:chainmail_chestplate", (Object)"minecraft:chainmail_leggings", (Object)"minecraft:chainmail_boots"), SoundEvents.DROP_MEDIUM_HARD), new SoundDefinition((List<String>)List.of((Object[])new String[] { "%Helmet", "%Chestplate", "%Leggings", "%Boots", "%Horse Armor", "minecraft:iron_helmet", "minecraft:iron_chestplate", "minecraft:iron_leggings", "minecraft:iron_boots", "minecraft:iron_horse_armor", "minecraft:golden_helmet", "minecraft:golden_chestplate", "minecraft:golden_leggings", "minecraft:golden_boots", "minecraft:golden_horse_armor", "minecraft:diamond_helmet", "minecraft:diamond_chestplate", "minecraft:diamond_leggings", "minecraft:diamond_boots", "minecraft:diamond_horse_armor", "minecraft:netherite_helmet", "minecraft:netherite_chestplate", "minecraft:netherite_leggings", "minecraft:netherite_boots", "minecraft:bell" }), SoundEvents.DROP_HEAVY), new SoundDefinition((List<String>)List.of((Object[])new String[] { "%Wool", "%Dust", "minecraft:redstone", "minecraft:sugar", "minecraft:feather", "minecraft:paper", "minecraft:map", "%Pattern", "minecraft:sponge", "minecraft:cobweb", "minecraft:dead_bush", "minecraft:grass", "minecraft:fern", "minecraft:seagrass", "minecraft:cookie", "%Kelp", "$minecraft:flowers", "%Carpet", "minecraft:string", "%Seeds", "minecraft:wheat", "minecraft:bread", "minecraft:ghast_tear", "minecraft:blaze_powder", "minecraft:phantom_membrane", "minecraft:lead", "minecraft:name_tag", "%Banner Pattern", "%Leather" }), SoundEvents.DROP_LIGHT), new SoundDefinition((List<String>)List.of((Object)"%Torch", (Object)"minecraft:stick", (Object)"% Stick", (Object)"minecraft:end_rod", (Object)"% Banner", (Object)"minecraft:bow", (Object)"%Arrow"), SoundEvents.DROP_WOOD), new SoundDefinition((List<String>)List.of((Object)"%Block of"), SoundEvents.DROP_MISC3), new SoundDefinition((List<String>)List.of((Object)"*"), SoundEvents.DROP_BLOCK) });
        Config.setInsertionOrderPreserved(true);
        final org.apache.commons.lang3.tuple.Pair<DroplightConfig, ForgeConfigSpec> specPair = (org.apache.commons.lang3.tuple.Pair<DroplightConfig, ForgeConfigSpec>)new ForgeConfigSpec.Builder().configure(DroplightConfig::new);
        SPEC = (ForgeConfigSpec)specPair.getRight();
        INSTANCE = (DroplightConfig)specPair.getLeft();
    }
    
    public enum SparklesRendered
    {
        NONE, 
        BEAMS_ONLY, 
        ALL;
    }
    
    public enum ShaderQuality
    {
        LOW, 
        MEDIUM, 
        HIGH;
    }
}
