package nl.gjorgdy.sculk_radio;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.event.api.v2.OnUpdateServerListener;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.minecraft.resources.Identifier;

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
        SculkRadio.forceSync = config.forceSync;
    }

    private FzzyConfig() {
        super(Identifier.fromNamespaceAndPath(SculkRadio.MOD_ID, "config"));
    }

    @Comment("The maximum distance between nodes.")
    public ValidatedInt maxNodeRange = new ValidatedInt(SculkRadio.maxNodeRange, 128, 8);
    @Comment("The minimum height an antennas needs to be above a relay.")
    public ValidatedInt minAntennaHeight = new ValidatedInt(SculkRadio.minAntennaHeight, 256, 0);
    @Comment("The radius around a speaker in which it can be heard. (AudioPlayer only)")
    public ValidatedInt speakerRange = new ValidatedInt((int) SculkRadio.speakerRange, 64, 1);
    @Comment("The radius around a microphone in which it picks up player audio. (VoiceChat only)")
    public ValidatedInt microphoneRange = new ValidatedInt(SculkRadio.microphoneRange, 64, 1);

    @Comment("The amount of ticks between a visual tick. This controls particles and Sculk activation. (20 ticks equals a second)")
    public ValidatedInt visualsTick = new ValidatedInt(SculkRadio.visualsTick, 200, 1);
    @Comment("The amount of ticks between a redstone tick. This controls the redstone signal updates. (20 ticks equals a second)")
    public ValidatedInt redstoneTick = new ValidatedInt(SculkRadio.redstoneTick, 200, 1);
    @Comment("The amount of ticks between a connection tick. This controls the connection between nodes. (20 ticks equals a second)")
    public ValidatedInt connectionTick = new ValidatedInt(SculkRadio.connectionTick, 200, 1);

    @Comment("Force sync all speakers every connection tick. This can be used to fix desync, but may cause slight pops in the audio.")
    public boolean forceSync = SculkRadio.forceSync;
}
