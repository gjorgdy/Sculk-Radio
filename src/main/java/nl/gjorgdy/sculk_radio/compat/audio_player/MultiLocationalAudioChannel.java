package nl.gjorgdy.sculk_radio.compat.audio_player;

import de.maxhenkel.audioplayer.voicechat.VoicechatAudioPlayerPlugin;
import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.ServerPlayer;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MultiLocationalAudioChannel implements LocationalAudioChannel {

    private final UUID id;
    private Position sourcePosition;
    private float distance;
    private Predicate<ServerPlayer> filter;
    private String category;
    private final Map<Position, LocationalAudioChannel> audioChannels = new HashMap<>();

    public MultiLocationalAudioChannel(UUID id, Position sourcePosition) {
        this.id = id;
        this.sourcePosition = sourcePosition;
    }

	private Stream<LocationalAudioChannel> getAudioChannels() {
		return audioChannels.values().stream().filter(Objects::nonNull);
	}

    @Override
    public void updateLocation(Position position) {
        sourcePosition = position;
    }

    public void addChannel(ServerWorld level, Vec3d pos) {
        VoicechatServerApi api = VoicechatAudioPlayerPlugin.voicechatServerApi;
        if (api == null) return;
        var position = api.createPosition(pos.x, pos.y, pos.z);
        var channel = api.createLocationalAudioChannel(UUID.randomUUID(), api.fromServerLevel(level), position);
        if (channel == null) return;
        channel.setDistance(distance);
        channel.setFilter(filter);
        channel.setCategory(category);
        audioChannels.put(position, channel);
    }

    public void removeChannel(Vec3d pos) {
        VoicechatServerApi api = VoicechatAudioPlayerPlugin.voicechatServerApi;
        if (api == null) return;
        var position = api.createPosition(pos.x, pos.y, pos.z);
        var channel = audioChannels.remove(position);
        if (channel != null) channel.flush();
    }

    @Override
    public Position getLocation() {
        return sourcePosition;
    }

    @Override
    public float getDistance() {
        return distance;
    }

    @Override
    public void setDistance(float distance) {
        this.distance = distance;
		getAudioChannels().forEach(channel -> channel.setDistance(distance));
    }

    @Override
    public void send(byte[] opusData) {
        audioChannels.values().forEach(channel -> channel.send(opusData));
    }

    @Override
    public void send(MicrophonePacket packet) {
		getAudioChannels().forEach(channel -> channel.send(packet));
    }

    @Override
    public void setFilter(Predicate<ServerPlayer> filter) {
        this.filter = filter;
		getAudioChannels().forEach(channel -> channel.setFilter(filter));
    }

    @Override
    public void flush() {
		getAudioChannels().forEach(AudioChannel::flush);
    }

    @Override
    public boolean isClosed() {
        return getAudioChannels().allMatch(AudioChannel::isClosed);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public @Nullable String getCategory() {
        return category;
    }

    @Override
    public void setCategory(@Nullable String category) {
        this.category = category;
		getAudioChannels().forEach(channel -> channel.setCategory(category));
    }
}
