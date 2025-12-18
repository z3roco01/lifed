package z3roco01.lifed.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import z3roco01.lifed.lifes.LifeManager;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    // needed constructor
    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        // needed to get the actual object, since its in a mixin, not technically the player object
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

        // remove one life
        LifeManager.removeLife(player);
    }
}
