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
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.features.BannedItems;
import z3roco01.lifed.features.BoogeymanManager;
import z3roco01.lifed.features.LifeManager;
import z3roco01.lifed.util.WolfCounter;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements WolfCounter {
    @Unique
    public int wolfCount = 0;

    @Override
    public int getWolfCount() {
        return wolfCount;
    }

    @Override
    public void incrementWolfCount() {
        wolfCount++;
        Lifed.LOGGER.info(String.valueOf(wolfCount));
    }

    @Override
    public void decrementWolfCount() {
        wolfCount--;
        Lifed.LOGGER.info(String.valueOf(wolfCount));
    }

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
                EntityType.LIGHTNING_BOLT.spawn(getEntityWorld(), bolt -> {bolt.setCosmetic(true);}, getBlockPos(), SpawnReason.EVENT, false, false);
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

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void readCustomData(ReadView view, CallbackInfo ci) {
        this.wolfCount = view.getInt("wolfCount", 0);
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void writeCustomData(WriteView view, CallbackInfo ci) {
        view.putInt("wolfCount", this.wolfCount);
    }
}
