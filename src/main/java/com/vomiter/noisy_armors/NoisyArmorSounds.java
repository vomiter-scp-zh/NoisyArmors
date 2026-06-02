package com.vomiter.noisy_armors;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

import static com.vomiter.noisy_armors.NoisyArmorsMod.MOD_ID;
import static com.vomiter.noisy_armors.NoisyArmorsMod.modLoc;

public class NoisyArmorSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    public static final RegistryObject<SoundEvent> ARMOR_MOVE = SOUND_EVENTS.register(
            "armor_move",
            () -> SoundEvent.createVariableRangeEvent(modLoc("armor_move"))
    );
    public static final RegistryObject<SoundEvent> ARMOR_MOVE_CRYSTAL = SOUND_EVENTS.register(
            "armor_move_crystal",
            () -> SoundEvent.createVariableRangeEvent(modLoc("armor_move_crystal"))
    );
    public static final List<RegistryObject<SoundEvent>> ARMOR_MOVE_CUSTOM = new ArrayList<>();
    static {
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            ARMOR_MOVE_CUSTOM.add(
                    SOUND_EVENTS.register(
                            "armor_move_custom" + (i+1),
                            () -> SoundEvent.createVariableRangeEvent(modLoc("armor_move_custom"  + (finalI +1)))
                    )
            );
        }
    }

}
