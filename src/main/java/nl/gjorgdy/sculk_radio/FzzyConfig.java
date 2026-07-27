package nl.gjorgdy.sculk_radio;

import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.config.Config;
import net.minecraft.resources.Identifier;

@IgnoreVisibility
public class FzzyConfig extends Config {

    static {
        ConfigApi.event().onSyncServer((_, _) -> FzzyConfig.load());
        ConfigApi.event().onSyncClient((_, _) -> FzzyConfig.load());
    }

    public static void load() {
        var config = ConfigApiJava.registerAndLoadConfig(FzzyConfig::new);
//        SculkRadio.enableExperimentalFrequencies = config.enableExperimentalFrequencies;
    }

    private FzzyConfig() {
        super(Identifier.fromNamespaceAndPath(SculkRadio.MOD_ID, "config"));
    }

//    @Comment("Whether to enable the experimental version of global frequencies. Warning : this will be replaced with a new frequency system in a later update")
//    public boolean enableExperimentalFrequencies = SculkRadio.enableExperimentalFrequencies;
}
