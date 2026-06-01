package com.vomiter.noisy_armors;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

import static com.vomiter.noisy_armors.NoisyArmorsMod.modLoc;

public class NoisyArmorTags {
    public static final TagKey<Item> TAG_NOISY_ARMOR = ItemTags.create(modLoc("noisy_armors"));
    public static final TagKey<Item> TAG_NOISY_ARMOR_METAL = ItemTags.create(modLoc("noisy_armors_metal"));
    public static final TagKey<Item> TAG_NOISY_ARMOR_CRYSTAL = ItemTags.create(modLoc("noisy_armors_crystal"));
    public static final List<TagKey<Item>> TAG_CUSTOM = new ArrayList<>();
    static {
        for (int i = 0; i < 10; i++) {
            TAG_CUSTOM.add(ItemTags.create(modLoc("noisy_armors_custom" + (i + 1))));
        }
    }

}
