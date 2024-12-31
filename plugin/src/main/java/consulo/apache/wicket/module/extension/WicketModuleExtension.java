package consulo.apache.wicket.module.extension;

import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionBase;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 16.04.14
 */
public class WicketModuleExtension extends ModuleExtensionBase<WicketModuleExtension>
{
	public WicketModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}
}
