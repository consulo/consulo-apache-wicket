package consulo.apache.wicket.editor;

import consulo.apache.wicket.module.extension.WicketMutableModuleExtension;
import consulo.language.editor.gutter.LineMarkerProvider;
import consulo.language.editor.gutter.LineMarkerProviderDescriptor;
import consulo.language.psi.PsiFile;
import consulo.module.extension.ModuleExtensionHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 08/05/2023
 */
public abstract class WicketLineMarkerProvider implements LineMarkerProvider {
    @Override
    public boolean isAvailable(@Nonnull PsiFile file) {
        return ModuleExtensionHelper.getInstance(file.getProject()).hasModuleExtension(WicketMutableModuleExtension.class);
    }
}
