package nl.gjorgdy.sculk_radio.compat.voicechat;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.registries.NodeRegistry;

public class MicrophonePlugin implements VoicechatPlugin {

	public static volatile VoicechatServerApi serverApi;

	@Override
	public String getPluginId() {
		return SculkRadio.MOD_ID;
	}

	@Override
	public void initialize(VoicechatApi api) {
		if (api instanceof VoicechatServerApi) {
			serverApi = (VoicechatServerApi) api;
		}
	}

	@Override
	public void registerEvents(EventRegistration registration) {
		registration.registerEvent(VoicechatServerStartedEvent.class, e -> {
			serverApi = e.getVoicechat();
			serverApi.registerVolumeCategory(
				serverApi.volumeCategoryBuilder()
					.setId("microphones")
					.setName("Microphones")
					.setDescription("Audio from microphone blocks")
					.build()
			);
		});
		registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
	}

	private void onMicrophonePacket(MicrophonePacketEvent event) {
		if (serverApi == null || event.getSenderConnection() == null) return;

		ServerPlayer player = (ServerPlayer) event.getSenderConnection().getPlayer().getEntity();

		NodeRegistry
			.of(player.level())
			.getMicrophonesInRange(player.blockPosition())
			.forEach(mic -> {
				boolean sent = mic.send(event.getPacket());
				if (sent) player.sendOverlayMessage(Component.literal("You are talking into a microphone right now"));
			});
	}

}
