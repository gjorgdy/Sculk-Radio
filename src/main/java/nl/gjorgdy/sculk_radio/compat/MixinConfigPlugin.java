package nl.gjorgdy.sculk_radio.compat;

import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;
import nl.gjorgdy.sculk_radio.SculkRadio;

import java.util.List;
import java.util.Set;

public class MixinConfigPlugin extends RestrictiveMixinConfigPlugin {

    public MixinConfigPlugin() {
        super();
        SculkRadio.LOGGER.info("Loaded MixinConfigPlugin");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    protected void onRestrictionCheckFailed(String mixinClassName, String reason) {
        System.out.println("Mixin " + mixinClassName + " was not applied due to restriction check failure: " + reason);
    }
}
