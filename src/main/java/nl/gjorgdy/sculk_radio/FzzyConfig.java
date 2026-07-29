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
        SculkRadio.maxNodeRange = config.maxNodeRange.get();
        SculkRadio.minAntennaHeight = config.minAntennaHeight.get();
        SculkRadio.visualsTick = config.visualsTick.get();
        SculkRadio.connectionTick = config.connectionTick.get();
    }

    private FzzyConfig() {
        super(Identifier.fromNamespaceAndPath(SculkRadio.MOD_ID, "config"));
    }

    @Comment("The maximum distance between nodes.")
    public ValidatedInt maxNodeRange = new ValidatedInt(SculkRadio.maxNodeRange, 128, 8);
    @Comment("The minimum height an antennas needs to be above a relay.")
    public ValidatedInt minAntennaHeight = new ValidatedInt(SculkRadio.minAntennaHeight, 256, 0);

    @Comment("The amount of ticks between a visual tick. This controls particles and Sculk activation. (20 ticks equals a second)")
    public ValidatedInt visualsTick = new ValidatedInt(SculkRadio.visualsTick, 200, 1);
    @Comment("The amount of ticks between a redstone tick. This controls the redstone signal updates. (20 ticks equals a second)")
    public ValidatedInt redstoneTick = new ValidatedInt(SculkRadio.redstoneTick, 200, 1);
    @Comment("The amount of ticks between a connection tick. This controls the connection between nodes. (20 ticks equals a second)")
    public ValidatedInt connectionTick = new ValidatedInt(SculkRadio.connectionTick, 200, 1);
}
