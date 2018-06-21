package consulo.apache.wicket.module.extension;

import org.jetbrains.annotations.NotNull;
import consulo.module.extension.impl.ModuleExtensionImpl;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 16.04.14
 */
public class WicketModuleExtension extends ModuleExtensionImpl<WicketModuleExtension>
{
	public WicketModuleExtension(@NotNull String id, @NotNull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}
}
