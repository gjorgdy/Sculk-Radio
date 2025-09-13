package nl.gjorgdy.sculk_radio;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class SculkRadio implements ModInitializer {

    private static MinecraftServer server = null;

    public static void RunIfServerActive(Runnable runnable) {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> runnable.run());
//        if (server != null) {
//            runnable.run();
//        } else {
//        }
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);
    }
}
