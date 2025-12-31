package z3roco01.lifed.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.features.BannedItems;
import z3roco01.lifed.features.BoogeymanManager;
import z3roco01.lifed.features.LifeManager;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow
    public abstract ServerWorld getEntityWorld();

    // needed constructor
    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        // needed to get the actual object, since its in a mixin, not technically the player object
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

        int livesBeforeDeath = LifeManager.getLives(player);
        // if they just lost their life, summon a lightening
        if(livesBeforeDeath == 1) {
            for(int i = 0; i < Lifed.config.lightningsOnRedDeath; i++)
                EntityType.LIGHTNING_BOLT.spawn(getEntityWorld(), null, getBlockPos(), SpawnReason.EVENT, false, false);
        }

        // remove one life
        LifeManager.removeLife(player);

        // if the killer was a boogey, cure them
        Entity maybeKiller = damageSource.getAttacker();
        if(!(maybeKiller instanceof ServerPlayerEntity)) return;

        if(livesBeforeDeath > 1) {
            ServerPlayerEntity killer = (ServerPlayerEntity)maybeKiller;
            BoogeymanManager.cure(killer);
        }
    }

    @Inject(method = "onStatusEffectApplied", at = @At("HEAD"), cancellable = true)
    private void onStatusEffectApllied(StatusEffectInstance effect, Entity source, CallbackInfo ci) {
        if(BannedItems.BANNED_EFFECTS.contains(effect.getEffectType())) {
            ((ServerPlayerEntity)(Object)this).removeStatusEffect(effect.getEffectType());
            ci.cancel();
        }
    }
}
