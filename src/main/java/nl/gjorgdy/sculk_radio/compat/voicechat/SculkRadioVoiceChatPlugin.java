package nl.gjorgdy.sculk_radio.compat.voicechat;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.events.ConfigCallback;
import nl.gjorgdy.sculk_radio.registries.NodeRegistry;

public class SculkRadioVoiceChatPlugin implements VoicechatPlugin {

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
		ConfigCallback.RELOAD_CONFIG.register(() -> {
			// speaker
			if (SculkRadio.speakerCategory) {
				serverApi.registerVolumeCategory(
					serverApi.volumeCategoryBuilder()
						.setId("sculkradiodisc")
						.setName("Speakers")
						.setDescription("The volume of speakers playing a music disc stream")
						.build()
				);
			} else {
				serverApi.unregisterVolumeCategory("sculkradiodisc");
			}
			// mic
			if (SculkRadio.microphonesEnabled()) {
				serverApi.registerVolumeCategory(
					serverApi.volumeCategoryBuilder()
						.setId("sculkradiomic")
						.setName("Microphones")
						.setDescription("The volume of microphone")
						.build()
				);
			} else {
				serverApi.unregisterVolumeCategory("sculkradiomic");
			}
			return InteractionResult.PASS;
		});

	}

	@Override
	public void registerEvents(EventRegistration registration) {
		registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
	}

	private void onMicrophonePacket(MicrophonePacketEvent event) {
		if (serverApi == null || event.getSenderConnection() == null || !SculkRadio.microphonesEnabled()) return;

		ServerPlayer player = (ServerPlayer) event.getSenderConnection().getPlayer().getEntity();

		NodeRegistry
			.of(player.level())
			.getMicrophonesInRange(player.blockPosition())
			.forEach(mic -> {
				boolean sent = mic.send(player, event.getPacket());
				if (sent) player.sendOverlayMessage(Component.literal("You are talking into a microphone right now"));
			});
	}

}
