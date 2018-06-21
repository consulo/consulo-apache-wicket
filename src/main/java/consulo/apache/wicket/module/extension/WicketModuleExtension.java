package consulo.apache.wicket.module.extension;

import javax.annotation.Nonnull;

import consulo.module.extension.impl.ModuleExtensionImpl;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 16.04.14
 */
public class WicketModuleExtension extends ModuleExtensionImpl<WicketModuleExtension>
{
	public WicketModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}
}
