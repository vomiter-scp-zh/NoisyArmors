package com.vomiter.noisy_armors;

import com.vomiter.noisy_armors.mixin.NearestTargetAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class NoisyArmorSoundHandler {
    private static final int CHECK_INTERVAL = 6;
    private static final Map<UUID, NoiseState> STATES = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide()) {
            return;
        }

        EquipmentSlot slot = event.getSlot();
        if (!isArmorSlot(slot)) {
            return;
        }

        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();

        boolean fromTagged = !from.isEmpty() && from.is(NoisyArmorTags.TAG_NOISY_ARMOR);
        boolean toTagged = !to.isEmpty() && to.is(NoisyArmorTags.TAG_NOISY_ARMOR);

        if (!fromTagged && !toTagged) {
            return;
        }

        UUID id = living.getUUID();
        Set<SoundEvent> sounds = new HashSet<>();
        int i = isWearingNoisyArmor(living, sounds);

        if (i > 0) {
            NoiseState state = STATES.computeIfAbsent(id, k -> new NoiseState());
            long gameTime = living.level().getGameTime();
            state.wearingNoisyArmor = true;
            state.numberOfNoisyArmor = i;
            state.lastSampleTick = gameTime;
            state.lastX = living.getX();
            state.lastZ = living.getZ();
            state.armorSounds = sounds;
            if (state.nextPlaySoundTick < gameTime) {
                state.nextPlaySoundTick = gameTime;
            }
        } else {
            STATES.remove(id);
        }
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide()) {
            return;
        }
        if (!living.isAlive()) {
            STATES.remove(living.getUUID());
            return;
        }
        if (living.tickCount % CHECK_INTERVAL != 0) {
            return;
        }

        NoiseState state = STATES.get(living.getUUID());
        if (state == null || !state.wearingNoisyArmor) {
            return;
        }

        long gameTime = living.level().getGameTime();
        if (gameTime < state.nextPlaySoundTick) {
            return;
        }

        double averageHorizontalSpeed = sampleAverageHorizontalSpeed(living, state, gameTime);
        state.nextPlaySoundTick = gameTime + calculateNextDelay(averageHorizontalSpeed);

        if(living.getRandom().nextFloat() > (float) state.numberOfNoisyArmor / 4f) return;

        if (!shouldPlayMovementSound(living, averageHorizontalSpeed)) {
            return;
        }


        float volume = Mth.clamp((float) (averageHorizontalSpeed * 18.0D), 0.15F, 0.9F) * 0.25F;
        float pitch = 0.75F + ((living.getRandom().nextFloat() - 0.5f) * 0.1F);

        if(!(living instanceof Player player) || Config.PLAYER_ARMOR_MAKES_SOUND){
            var armorSound = state.getRandomSound(living.getRandom());
            if (armorSound == NoisyArmorSounds.ARMOR_MOVE_CRYSTAL.get()) volume = volume * 1.5f;
            if(armorSound != null) living.level().playSound(
                    null,
                    living.blockPosition(),
                    armorSound,
                    living.getSoundSource(),
                    volume,
                    pitch
            );
        }

        if(living instanceof Player player && Config.ARMOR_SOUND_ATTRACTS_HOSTILE_MOBS){
            var range = player.getBoundingBox().inflate(16);
            player.level().getEntities(player, range, entity -> {
                if(!(entity instanceof Mob mob)) return false;
                if(mob.getTarget() != null) return false;
                return mob.targetSelector.getAvailableGoals().stream().anyMatch(target -> {
                    if(target.getGoal() instanceof NearestTargetAccessor nearestAttackableTargetGoal){
                        return nearestAttackableTargetGoal.getTargetType().getName().endsWith("Player");
                    }
                        return false;
                    }
                );
            })
                    .forEach(entity -> {
                        if(entity instanceof Mob mob) mob.setTarget(player);
                    });
            ;
        }

    }

    private static double sampleAverageHorizontalSpeed(LivingEntity living, NoiseState state, long gameTime) {
        if (state.lastSampleTick < 0L) {
            state.lastSampleTick = gameTime;
            state.lastX = living.getX();
            state.lastZ = living.getZ();
            return 0.0D;
        }

        long elapsedTicks = gameTime - state.lastSampleTick;
        if (elapsedTicks <= 0L) {
            return 0.0D;
        }

        double dx = living.getX() - state.lastX;
        double dz = living.getZ() - state.lastZ;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double averageSpeed = horizontalDistance / (double) elapsedTicks;

        state.lastSampleTick = gameTime;
        state.lastX = living.getX();
        state.lastZ = living.getZ();

        return averageSpeed;
    }

    private static boolean shouldPlayMovementSound(LivingEntity living, double averageHorizontalSpeed) {
        if (living.isPassenger() || living.isShiftKeyDown() || living.isSpectator()) {
            return false;
        }
        return averageHorizontalSpeed > 0.03D;
    }

    private static int calculateNextDelay(double speed) {
        if (speed > 0.18D) {
            return 12;
        }
        if (speed > 0.10D) {
            return 24;
        }
        return 36;
    }

    private static SoundEvent fromArmorToArmorSound(ItemStack stack){
        if(!stack.is(NoisyArmorTags.TAG_NOISY_ARMOR)) return null;
        if(stack.is(NoisyArmorTags.TAG_NOISY_ARMOR_CRYSTAL)) return NoisyArmorSounds.ARMOR_MOVE_CRYSTAL.get();
        for (int i = 0; i < 10; i++) {
            if(stack.is(NoisyArmorTags.TAG_CUSTOM.get(i))) return NoisyArmorSounds.ARMOR_MOVE_CUSTOM.get(1).get();
        }
        return NoisyArmorSounds.ARMOR_MOVE.get();
    }

    private static int isWearingNoisyArmor(LivingEntity living, Set<SoundEvent> set) {
        int i = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        }) {
            ItemStack stack = living.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.is(NoisyArmorTags.TAG_NOISY_ARMOR)) {
                i++;
                set.add(fromArmorToArmorSound(stack));
            }
        }
        return i;
    }

    private static boolean isArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD
                || slot == EquipmentSlot.CHEST
                || slot == EquipmentSlot.LEGS
                || slot == EquipmentSlot.FEET;
    }

    private static final class NoiseState {
        private boolean wearingNoisyArmor;
        private int numberOfNoisyArmor;
        private long nextPlaySoundTick = 0L;
        private long lastSampleTick = -1L;
        private double lastX;
        private double lastZ;
        private Set<SoundEvent> armorSounds;
        SoundEvent getRandomSound(RandomSource random) {
            return armorSounds.stream()
                    .skip(random.nextInt(armorSounds.size()))
                    .findFirst()
                    .orElse(null);
        }

    }
}