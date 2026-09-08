package nl.gjorgdy.sculk_radio;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod(SculkRadio.MOD_ID)
public class NeoForgeSculkRadio extends SculkRadio {

    @Override
    protected boolean isVoiceChatInstalled() {
        return ModList.get().isLoaded("voicechat");
    }

    @Override
    protected boolean isFuzzyConfigInstalled() {
        return ModList.get().isLoaded("fzzy_config");
    }

    public NeoForgeSculkRadio(IEventBus eventBus) {

        NeoForge.EVENT_BUS.addListener(this::onServerStartedNF);
        NeoForge.EVENT_BUS.addListener(this::onServerTickNF);

//        var useListener = new OnUseListener();
//        BlockEvents.USE_WITHOUT_ITEM.register(useListener);
//        BlockEvents.USE_ITEM_ON.register(useListener);

        super.onInitialize();

    }

    private void onServerStartedNF(ServerStartedEvent startedEvent) {
        super.onServerStarted(startedEvent.getServer());
    }

    private void onServerTickNF(ServerTickEvent.Pre tickEvent) {
        super.onServerTick(tickEvent.getServer());
    }
}
