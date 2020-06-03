package consulo.apache.wicket.module.extension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import consulo.disposer.Disposable;
import consulo.module.extension.MutableModuleExtension;
import consulo.roots.ModuleRootLayer;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;

/**
 * @author VISTALL
 * @since 16.04.14
 */
public class WicketMutableModuleExtension extends WicketModuleExtension implements MutableModuleExtension<WicketModuleExtension>
{
	public WicketMutableModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	@RequiredUIAccess
	@Nullable
	@Override
	public Component createConfigurationComponent(@Nonnull Disposable disposable, @Nonnull Runnable runnable)
	{
		return null;
	}

	@Override
	public void setEnabled(boolean val)
	{
		myIsEnabled = val;
	}

	@Override
	public boolean isModified(@Nonnull WicketModuleExtension originalExtension)
	{
		return myIsEnabled != originalExtension.isEnabled();
	}
}
