package nl.gjorgdy.sculk_radio;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.fabricmc.loader.api.FabricLoader;
import nl.gjorgdy.sculk_radio.listeners.OnUseListener;

public class FabricSculkRadio extends SculkRadio implements ModInitializer {

    @Override
    protected boolean isVoiceChatInstalled() {
        return FabricLoader.getInstance().isModLoaded("voicechat");
    }

    @Override
    protected boolean isFuzzyConfigInstalled() {
        return FabricLoader.getInstance().isModLoaded("fzzy_config");
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerTickEvents.START_SERVER_TICK.register(this::onServerTick);

        var useListener = new OnUseListener();
        BlockEvents.USE_WITHOUT_ITEM.register(useListener);
        BlockEvents.USE_ITEM_ON.register(useListener);

        super.onInitialize();
    }
}
