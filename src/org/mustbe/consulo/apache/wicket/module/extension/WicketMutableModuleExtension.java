package org.mustbe.consulo.apache.wicket.module.extension;

import javax.swing.JComponent;

import org.consulo.module.extension.MutableModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.roots.ModifiableRootModel;

/**
 * @author VISTALL
 * @since 16.04.14
 */
public class WicketMutableModuleExtension extends WicketModuleExtension implements MutableModuleExtension<WicketModuleExtension>
{
	public WicketMutableModuleExtension(@NotNull String id, @NotNull ModifiableRootModel rootModel)
	{
		super(id, rootModel);
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull Runnable updateOnCheck)
	{
		return null;
	}

	@Override
	public void setEnabled(boolean val)
	{
		myIsEnabled = val;
	}

	@Override
	public boolean isModified(@NotNull WicketModuleExtension originalExtension)
	{
		return myIsEnabled != originalExtension.isEnabled();
	}
}
