package com.vomiter.noisy_armors;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = NoisyArmorsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue PLAYER_ARMOR_MAKES_SOUND_SPEC;
    private static final ForgeConfigSpec.BooleanValue ARMOR_SOUND_ATTRACTS_HOSTILE_MOBS_SPEC;

    public static boolean PLAYER_ARMOR_MAKES_SOUND = true;
    public static boolean ARMOR_SOUND_ATTRACTS_HOSTILE_MOBS = true;

    static {
        BUILDER.push("noisy_armors");

        PLAYER_ARMOR_MAKES_SOUND_SPEC = BUILDER
                .comment("Whether armor worn by players should make noise.")
                .define("playerArmorMakesSound", true);

        ARMOR_SOUND_ATTRACTS_HOSTILE_MOBS_SPEC = BUILDER
                .comment("Whether armor noise made by players should attract hostile mobs.")
                .define("armorSoundAttractsHostileMobs", true);

        BUILDER.pop();
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        PLAYER_ARMOR_MAKES_SOUND = PLAYER_ARMOR_MAKES_SOUND_SPEC.get();
        ARMOR_SOUND_ATTRACTS_HOSTILE_MOBS = ARMOR_SOUND_ATTRACTS_HOSTILE_MOBS_SPEC.get();
    }
}