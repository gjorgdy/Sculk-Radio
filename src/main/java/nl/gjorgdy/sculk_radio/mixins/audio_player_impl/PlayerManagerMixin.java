package nl.gjorgdy.sculk_radio.mixins.audio_player_impl;

import com.llamalad7.mixinextras.sugar.Local;
import de.maxhenkel.audioplayer.apiimpl.ChannelReferenceImpl;
import de.maxhenkel.audioplayer.apiimpl.events.PlayEventImpl;
import de.maxhenkel.audioplayer.audioplayback.PlayerManager;
import de.maxhenkel.audioplayer.utils.ChatUtils;
import de.maxhenkel.audioplayer.voicechat.VoicechatAudioPlayerPlugin;
import de.maxhenkel.voicechat.api.Player;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.compat.audio_player.MultiLocationalAudioChannel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Restriction(
        require = {
                @Condition(value = "audioplayer"),
                @Condition(value = "voicechat")
        }
)
@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Redirect(
            method = "playType(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Lde/maxhenkel/audioplayer/audioloader/AudioData;Lde/maxhenkel/audioplayer/audioplayback/PlayerType;Lnet/fabricmc/fabric/api/event/Event;Lnet/fabricmc/fabric/api/event/Event;Lnet/minecraft/util/math/Vec3d;)Lde/maxhenkel/audioplayer/api/ChannelReference;",
            at = @At(value = "INVOKE", target = "Lde/maxhenkel/audioplayer/audioplayback/PlayerManager;playLocational(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;Ljava/util/UUID;Lnet/minecraft/server/network/ServerPlayerEntity;FLjava/lang/String;Ljava/lang/Float;)Lde/maxhenkel/audioplayer/apiimpl/ChannelReferenceImpl;")
    )
    public ChannelReferenceImpl<LocationalAudioChannel> playType(PlayerManager instance, ServerWorld level, Vec3d pos, UUID sound, ServerPlayerEntity p, float distance, String category, Float maxLengthSeconds, @Local PlayEventImpl event) {
        VoicechatServerApi api = VoicechatAudioPlayerPlugin.voicechatServerApi;
        if (api == null) return null;
        var blockPos = new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
        if (SculkRadio.api().isRadio(level, blockPos)) {
            return playMultiLocational(instance, level, blockPos, event.getSoundId(), p, event.getDistance(), event.getCategory(), maxLengthSeconds);
        }
        return instance.playLocational(level, event.getPosition(), event.getSoundId(), p, event.getDistance(), event.getCategory(), maxLengthSeconds);
    }

    @Unique
    @Nullable
    public ChannelReferenceImpl<LocationalAudioChannel> playMultiLocational(PlayerManager instance, ServerWorld level, BlockPos blockPos, UUID sound, @Nullable ServerPlayerEntity p, float distance, @Nullable String category, @Nullable Float maxLengthSeconds) {
        VoicechatServerApi api = VoicechatAudioPlayerPlugin.voicechatServerApi;
        if (api == null) {
            return null;
        }

        UUID channelID = UUID.randomUUID();
        MultiLocationalAudioChannel mlChannel = new MultiLocationalAudioChannel(channelID, api.createPosition(blockPos.getX(), blockPos.getX(), blockPos.getX()));
        if (category != null) {
            mlChannel.setCategory(category);
        }
        mlChannel.setDistance(distance);
        api.getPlayersInRange(api.fromServerLevel(level), mlChannel.getLocation(), distance + 1F, serverPlayer -> {
            VoicechatConnection connection = api.getConnectionOf(serverPlayer);
            return !ChatUtils.isAbleToHearVoicechat(connection);
        }).stream().map(Player::getPlayer).map(ServerPlayerEntity.class::cast).forEach(ChatUtils::sendEnableVoicechatMessage);

        SculkRadio.api().connect(
                level,
                blockPos,
                n -> mlChannel.addChannel(n.getWorld(), n.getPos().toCenterPos()),
                n -> mlChannel.removeChannel(n.getPos().toCenterPos())
        );

        return instance.playChannel(mlChannel, sound, p, maxLengthSeconds);
    }

}
