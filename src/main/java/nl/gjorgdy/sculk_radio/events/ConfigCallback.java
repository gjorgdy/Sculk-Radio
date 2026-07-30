package nl.gjorgdy.sculk_radio.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;

public interface ConfigCallback {

	Event<ConfigCallback> RELOAD_CONFIG = EventFactory.createArrayBacked(ConfigCallback.class, (listeners) -> () -> {
		     for (ConfigCallback listener : listeners) {
		         InteractionResult result = listener.onReload();
		         if (result != InteractionResult.PASS) {
		             return result;
		         }
		     }
		     return InteractionResult.PASS;
		 }
	);

	InteractionResult onReload();

}
