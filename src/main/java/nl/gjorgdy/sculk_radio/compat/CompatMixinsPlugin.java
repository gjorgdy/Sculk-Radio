package nl.gjorgdy.sculk_radio.compat;

import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;
import nl.gjorgdy.sculk_radio.SculkRadio;

import java.util.List;
import java.util.Set;

public class CompatMixinsPlugin extends RestrictiveMixinConfigPlugin {

    public CompatMixinsPlugin() {
        super();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var apply = super.shouldApplyMixin(targetClassName, mixinClassName);
        if (mixinClassName.contains("PlayerManagerMixin")) {
            SculkRadio.LOGGER.info("AudioPlayer Compat: {}", apply ? "enabled" : "disabled");
        }
        return apply;
    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

}
