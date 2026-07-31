package com.Polarice3.Goety.mixin;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.RaidAdditions;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Shadow private int groupsSpawned;

    @Shadow public abstract int getRaidOmenLevel();

    @Shadow public abstract void stop();

    @Shadow public abstract void joinRaid(int pWave, Raider pRaider, BlockPos pPos, boolean pSpawned);

    @Shadow private BlockPos center;

    @Shadow public abstract boolean isStopped();

    @Shadow public abstract boolean isVictory();

    @ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "spawnGroup")
    private Raider spawnCustomRaider(Raider raider, BlockPos blockPos) {
        if (MobsConfig.ArmoredRavagerRaid.get()){
            if (this.level.random.nextFloat() < (0.25F + this.level.getCurrentDifficultyAt(raider.blockPosition()).getSpecialMultiplier())) {
                if (raider.getType() == EntityType.RAVAGER){
                    raider = ModEntityType.ARMORED_RAVAGER.get().create(this.level);
                    if (raider != null){
                        return raider;
                    }
                }
            }
        }
        return raider;
    }

    @Inject(method = "spawnGroup", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/raid/Raid;waveSpawnPos:Ljava/util/Optional;", opcode = Opcodes.PUTFIELD))
    private void goety$spawnGoetyRaiders(BlockPos blockPos, CallbackInfo ci) {
        int wave = this.groupsSpawned + 1;
        for (RaidAdditions.CustomRaiderType member : RaidAdditions.NEW_RAID_MEMBERS) {
            int count = member.getDefaultNumSpawns(wave);
            for (int i = 0; i < count; i++) {
                Raider raider = member.entityType().create(this.level);
                if (raider == null) {
                    break;
                }
                // Custom raid entries are joined explicitly because the vanilla raid type array is fixed at bootstrap.
                this.joinRaid(wave, raider, blockPos, false);
                this.goety$startRaidNavigation(raider);
            }
        }
    }

    private void goety$startRaidNavigation(Raider raider) {
        if (!this.level.isVillage(raider.blockPosition())) {
            Vec3 target = DefaultRandomPos.getPosTowards(raider, 15, 4, Vec3.atBottomCenterOf(this.center), (float)(Math.PI / 2));
            if (target != null) {
                // Custom Goety goals can take over MOVE before PathfindToRaidGoal's first tick, so seed the same raid-center path vanilla would request.
                raider.getNavigation().moveTo(target.x, target.y, target.z, 1.0D);
            }
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MainConfig.ShriekObeliskRaid.get()) {
            if (this.level.getGameTime() % 20 == 0) {
                if (!this.isStopped() && !this.isVictory()) {
                    int cost = MainConfig.ShriekObeliskCost.get() * this.getRaidOmenLevel();
                    if (BlockFinder.findIllagerWard(this.level, this.center, cost)) {
                        this.stop();
                    }
                }
            }
        }
    }
}
