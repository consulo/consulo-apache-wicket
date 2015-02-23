package org.mustbe.consulo.apache.wicket.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.roots.ModuleRootLayer;

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
