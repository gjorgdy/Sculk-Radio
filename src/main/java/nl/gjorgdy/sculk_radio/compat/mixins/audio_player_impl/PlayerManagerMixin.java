package nl.gjorgdy.sculk_radio.compat.mixins.audio_player_impl;

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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.compat.audio_player.MultiLocationalAudioChannel;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.nodes.RadioNode;
import nl.gjorgdy.sculk_radio.utils.NodeUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
public abstract class PlayerManagerMixin {

    @Shadow
    public static PlayerManager instance() {throw new UnsupportedOperationException("Implemented via mixin");}

    @Redirect(
            method = "playType(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Lde/maxhenkel/audioplayer/audioloader/AudioData;Lde/maxhenkel/audioplayer/audioplayback/PlayerType;Lnet/fabricmc/fabric/api/event/Event;Lnet/fabricmc/fabric/api/event/Event;Lnet/minecraft/world/phys/Vec3;)Lde/maxhenkel/audioplayer/api/ChannelReference;",
            at = @At(value = "INVOKE", target = "Lde/maxhenkel/audioplayer/audioplayback/PlayerManager;playLocational(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Ljava/util/UUID;Lnet/minecraft/server/level/ServerPlayer;FLjava/lang/String;Ljava/lang/Float;)Lde/maxhenkel/audioplayer/apiimpl/ChannelReferenceImpl;")
    )
    public ChannelReferenceImpl<LocationalAudioChannel> playType(PlayerManager instance, ServerLevel level, Vec3 pos, UUID sound, ServerPlayer p, float distance, String category, Float maxLengthSeconds, @Local PlayEventImpl event) {
        VoicechatServerApi api = VoicechatAudioPlayerPlugin.voicechatServerApi;
        if (api == null) return null;
        var blockPos = new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
        if (NodeUtils.getFromBlockEntity(level.getBlockEntity(blockPos.above())) instanceof RadioNode) {
            return playMultiLocational(instance, level, blockPos, event.getSoundId(), p, event.getDistance(), event.getCategory(), maxLengthSeconds);
        }
        return instance.playLocational(level, event.getPosition(), event.getSoundId(), p, event.getDistance(), event.getCategory(), maxLengthSeconds);
    }

    @Unique
    @Nullable
    public ChannelReferenceImpl<LocationalAudioChannel> playMultiLocational(PlayerManager instance, ServerLevel level, BlockPos blockPos, UUID sound, @Nullable ServerPlayer p, float distance, @Nullable String category, @Nullable Float maxLengthSeconds) {
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
        }).stream().map(Player::getPlayer).map(ServerPlayer.class::cast).forEach(ChatUtils::sendEnableVoicechatMessage);

        var blockEntity = level.getBlockEntity(blockPos.above());
        var node = NodeUtils.getFromBlockEntity(blockEntity);
        if (node instanceof RadioNode radio) {
            radio.play(
                    speaker -> mlChannel.addChannel(speaker.getLevel(), speaker.getPos().getCenter()),
                    speaker -> mlChannel.removeChannel(speaker.getPos().getCenter())
            );
        }

        System.out.println(mlChannel);

        return instance.playChannel(mlChannel, sound, p, maxLengthSeconds);
    }

}
