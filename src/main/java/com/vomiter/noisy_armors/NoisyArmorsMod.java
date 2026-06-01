
package com.vomiter.noisy_armors;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import static com.vomiter.noisy_armors.NoisyArmorSounds.SOUND_EVENTS;

@Mod(NoisyArmorsMod.MOD_ID)
public final class NoisyArmorsMod {
    public static final String MOD_ID = "noisy_armors";
    public static ResourceLocation modLoc(String path){
        return new ResourceLocation(MOD_ID, path);
    }



    public static final Logger LOGGER = LogUtils.getLogger();

    public NoisyArmorsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        SOUND_EVENTS.register(modBus);
        MinecraftForge.EVENT_BUS.register(new NoisyArmorSoundHandler());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
