package nl.gjorgdy.sculk_radio;

import me.fzzyhmstrs.fzzy_config.annotations.*;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.event.api.v2.OnUpdateServerListener;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.minecraft.resources.Identifier;
import nl.gjorgdy.sculk_radio.events.ConfigCallback;

@IgnoreVisibility
public class FzzyConfig extends Config {

    static {
        ConfigApi.event().onUpdateServer((OnUpdateServerListener) ((_, _, _) -> FzzyConfig.load()));
        ConfigApi.event().onSyncServer((_, _) -> FzzyConfig.load());
        ConfigApi.event().onUpdateClient((_, _) -> FzzyConfig.load());
        ConfigApi.event().onSyncClient((_, _) -> FzzyConfig.load());
    }

    public static void load() {
        var config = ConfigApiJava.registerAndLoadConfig(FzzyConfig::new);
        // range
        SculkRadio.maxNodeRange = config.maxNodeRange.get();
        SculkRadio.minAntennaHeight = config.minAntennaHeight.get();
        SculkRadio.speakerRange = config.speakerRange.get();
        SculkRadio.microphoneRange = config.microphoneRange.get();
        // ticks
        SculkRadio.visualsTick = config.visualsTick.get();
        SculkRadio.redstoneTick = config.redstoneTick.get();
        SculkRadio.connectionTick = config.connectionTick.get();
        // options
        SculkRadio.forceSyncSpeakers = config.forceSync.get();
        SculkRadio.speakerCategory = config.speakerCategory.get();
        SculkRadio.microphonesEnabledConfig = config.microphonesEnabled.get();

        ConfigCallback.RELOAD_CONFIG.invoker().onReload();
    }

    private FzzyConfig() {
        super(Identifier.fromNamespaceAndPath(SculkRadio.MOD_ID, "config"));
    }

    @Comment("The maximum distance between nodes.")
    public ValidatedInt maxNodeRange = new ValidatedInt(SculkRadio.maxNodeRange, 128, 8);
    @Comment("The minimum height an antennas needs to be above a relay.")
    public ValidatedInt minAntennaHeight = new ValidatedInt(SculkRadio.minAntennaHeight, 256, 0);
    @Comment("The radius around a speaker in which it can be heard. (AudioPlayer discs and microphones only)")
    public ValidatedInt speakerRange = new ValidatedInt((int) SculkRadio.speakerRange, 64, 1);
    @Comment("The radius around a microphone in which it picks up player audio. (Simple Voice Chat only)")
    public ValidatedInt microphoneRange = new ValidatedInt(SculkRadio.microphoneRange, 64, 1);

    @Comment("The amount of ticks between a visual tick. This controls particles and Sculk activation. (20 ticks equals a second)")
    public ValidatedInt visualsTick = new ValidatedInt(SculkRadio.visualsTick, 200, 1);
    @Comment("The amount of ticks between a redstone tick. This controls the redstone signal updates. (20 ticks equals a second)")
    public ValidatedInt redstoneTick = new ValidatedInt(SculkRadio.redstoneTick, 200, 1);
    @Comment("The amount of ticks between a connection tick. This controls the connection between nodes. (20 ticks equals a second)")
    public ValidatedInt connectionTick = new ValidatedInt(SculkRadio.connectionTick, 200, 1);

    @Comment("Should speakers be synced every connection tick. This can be used to fix desync, but may cause slight pops in the audio.")
    public ValidatedBoolean forceSync = new ValidatedBoolean(SculkRadio.forceSyncSpeakers);
    @Comment("Should speakers use their own volume category instead of AudioPlayers' music disc channel.")
    public ValidatedBoolean speakerCategory = new ValidatedBoolean(SculkRadio.speakerCategory);
    @Comment("Should microphones be enabled. This requires Simple Voice Chat to be installed. [Requires restart]")
    @RequiresAction(action = Action.RESTART)
    public ValidatedBoolean microphonesEnabled = new ValidatedBoolean(SculkRadio.microphonesEnabledConfig);
}
