
package com.vomiter.noisy_armors;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(NoisyArmorsMod.MOD_ID)
public final class NoisyArmorsMod {
    public static final String MOD_ID = "noisy_armors";
    public static final TagKey<Item> NOISY_ARMOR_TAG = ItemTags.create(new ResourceLocation("noisy_armors", "noisy_armors"));
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    public static final RegistryObject<SoundEvent> ARMOR_MOVE = SOUND_EVENTS.register(
            "armor_move",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "armor_move"))
    );

    public NoisyArmorsMod() {
        IEventBus modBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        SOUND_EVENTS.register(modBus);
        MinecraftForge.EVENT_BUS.register(new NoisyArmorSoundHandler());
    }
}
