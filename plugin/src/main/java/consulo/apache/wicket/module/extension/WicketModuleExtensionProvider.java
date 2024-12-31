package consulo.apache.wicket.module.extension;

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.wicket.icon.ApacheWicketIconGroup;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 08/05/2023
 */
@ExtensionImpl
public class WicketModuleExtensionProvider implements ModuleExtensionProvider<WicketModuleExtension> {
    @Nonnull
    @Override
    public String getId() {
        return "apache-wicket";
    }

    @Nullable
    @Override
    public String getParentId() {
        return "java-web";
    }

    @Nonnull
    @Override
    public LocalizeValue getName() {
        return LocalizeValue.localizeTODO("Apache Wicket");
    }

    @Nonnull
    @Override
    public Image getIcon() {
        return ApacheWicketIconGroup.wicket();
    }

    @Nonnull
    @Override
    public ModuleExtension<WicketModuleExtension> createImmutableExtension(@Nonnull ModuleRootLayer moduleRootLayer) {
        return new WicketModuleExtension(getId(), moduleRootLayer);
    }

    @Nonnull
    @Override
    public MutableModuleExtension<WicketModuleExtension> createMutableExtension(@Nonnull ModuleRootLayer moduleRootLayer) {
        return new WicketMutableModuleExtension(getId(), moduleRootLayer);
    }
}
