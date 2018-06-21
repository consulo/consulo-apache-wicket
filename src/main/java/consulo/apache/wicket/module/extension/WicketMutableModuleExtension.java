package consulo.apache.wicket.module.extension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;

import consulo.module.extension.MutableModuleExtension;
import consulo.roots.ModuleRootLayer;

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

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@Nonnull Runnable updateOnCheck)
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
