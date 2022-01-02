package me.mindlessly.notenoughcoins.tweaker;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.8.9")
@IFMLLoadingPlugin.Name("NotEnoughCoins FTW")
public class NotEnoughCoinsLoadingPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{NotEnoughCoinsTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
