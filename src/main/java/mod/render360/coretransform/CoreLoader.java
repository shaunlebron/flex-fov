package mod.render360.coretransform;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.11")
@IFMLLoadingPlugin.TransformerExclusions(value = "mod.render360.coretransform.")
@IFMLLoadingPlugin.Name(CoreLoader.NAME)
@IFMLLoadingPlugin.SortingIndex(value = 999)
public class CoreLoader implements IFMLLoadingPlugin {
	
	public static final String NAME = "Render 360";
	public static boolean isObfuscated = false;
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[]{CoreTransformer.class.getName()};
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
		isObfuscated = (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
