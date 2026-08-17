package z3roco01.lifed.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.config.ConfigFiles;
import z3roco01.lifed.features.BoogeymanManager;
import z3roco01.lifed.features.LifeManager;
import z3roco01.lifed.features.SoulmateManager;
import z3roco01.lifed.util.SessionUUID;
import z3roco01.lifed.util.WolfCounter;

import java.util.UUID;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements WolfCounter, SoulmateManager.SoulmateHaver, BoogeymanManager.PotentialBoogey {
    // Was this player a boogey last session and needs to be failed now
    @Unique
    public boolean previousBoogey = false;

    @Override
    public boolean getPreviousBoogey() {
        return previousBoogey;
    }

    // support for counting wolves, needed in an interface, since its a mixin
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

    @Unique
    @Nullable
    private UUID soulmate = null;

    @Unique
    private float incomingDamageAmount = 0;

    @Override
    public void setSoulmate(UUID soulmate) {
        this.soulmate = soulmate;
    }

    @Override
    public @Nullable UUID getSoulmate() {
        return soulmate;
    }

    @Override
    public @Nullable ServerPlayer getSoulmatePlayer() {
        if(soulmate == null) return null;
        return Lifed.server.getPlayerList().getPlayer(soulmate);
    }

    @Override
    public void incomingDamage(float damage) {
        this.incomingDamageAmount = damage;
    }

    @Override
    public float getIncomingDamage() {
        return this.incomingDamageAmount;
    }

    @Override
    public void clearIncoming() {
        this.incomingDamageAmount = 0;
    }

    @Shadow
    public abstract ServerLevel level();

    // needed constructor
    public ServerPlayerEntityMixin(Level world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(MinecraftServer server, ServerLevel level, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo ci) {
        UUID soulmateMaybe = SoulmateManager.getSoulmate(uuid);
        if(soulmateMaybe == null) return;

        this.setSoulmate(soulmateMaybe);
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void die(DamageSource damageSource, CallbackInfo ci) {
        // needed to get the actual object, since its in a mixin, not technically the player object
        ServerPlayer player = (ServerPlayer)(Object)this;

        int livesBeforeDeath = LifeManager.getLives(player);
        // if they just lost their life, summon a lightening
        if(livesBeforeDeath == 1) {
            for(int i = 0; i < ConfigFiles.gameplay.lightningsOnRedDeath; i++)
                EntityType.LIGHTNING_BOLT.spawn(level(), bolt -> {bolt.setVisualOnly(true);}, blockPosition(), EntitySpawnReason.EVENT, false, false);
        }

        // remove one life
        LifeManager.removeLife(player);

        // if the killer was a boogey, cure them
        Entity maybeKiller = damageSource.getEntity();
        if(!(maybeKiller instanceof ServerPlayer)) return;

        if(livesBeforeDeath > 1) {
            ServerPlayer killer = (ServerPlayer) maybeKiller;
            BoogeymanManager.cure(killer);

            if(ConfigFiles.gameplay.logEvents)
                Lifed.LOGGER.info(killer.getPlainTextName() + " boogey killed " + player.getPlainTextName());
        }
    }

    @Inject(method = "onEffectAdded", at = @At("HEAD"), cancellable = true)
    private void onStatusEffectApllied(MobEffectInstance effect, Entity source, CallbackInfo ci) {
        // if they are trying to apply a banned effect, cancel it
        if(ConfigFiles.gameplay.bannedEffects.contains(effect.getEffect().value())) {
            ((ServerPlayer)(Object)this).removeEffect(effect.getEffect());
            ci.cancel();
        }
    }

    @Inject(method = "onAttributeUpdated", at = @At("TAIL"))
    private void onAttributeUpdated(Holder<Attribute> attribute, CallbackInfo ci) {

    }

    @Unique
    private static final String WOLF_COUNT_DATA_ID = "lifed.wolfCount";
    @Unique
    private static final String SOULMATE_DATA_ID = "lifed.soulmateUUID";
    @Unique
    private static final String BOOGEY_DATA_ID = "lifed.boogeySession";

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomData(ValueInput input, CallbackInfo ci) {
        // load in the wolf count from file/network
        this.wolfCount = input.getIntOr(WOLF_COUNT_DATA_ID, 0);

        String soulmateUUID = input.getStringOr(SOULMATE_DATA_ID, "none");
        if(soulmateUUID.equals("none")) {
            setSoulmate(null);
        }else {
            setSoulmate(UUID.fromString(soulmateUUID));
            if(getSoulmatePlayer() != null) {
                // encase it didnt set, do it up, when only one of them has joined it will be null
                ((SoulmateManager.SoulmateHaver)getSoulmatePlayer()).setSoulmate(uuid);
                SoulmateManager.syncPair((ServerPlayer)(Object)this);
            }
        }

        // read stored previous boogey status
        String boogeySession = input.getStringOr(BOOGEY_DATA_ID, "none");
        if(!boogeySession.equals("none")) {
            UUID sessUUID = UUID.fromString(boogeySession);
            // should be boogey so readd the player if they are not in
            if(!sessUUID.equals(SessionUUID.getCurrentUuid())) {
                previousBoogey = true;
                //BoogeymanManager.fail((ServerPlayer)(Object)this);
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeCustomData(ValueOutput output, CallbackInfo ci) {
        // save the wolf count to file/network
        output.putInt(WOLF_COUNT_DATA_ID, this.wolfCount);
        if(soulmate != null)
            output.putString(SOULMATE_DATA_ID, soulmate.toString());
        else
            output.putString(SOULMATE_DATA_ID, "none");

        // save boogey active session uuid
        if(BoogeymanManager.isPlayerBoogey((ServerPlayer)(Object)this))
            output.putString(BOOGEY_DATA_ID, SessionUUID.getCurrentUuid().toString());
        else
            output.putString(BOOGEY_DATA_ID, "none");

    }
}
