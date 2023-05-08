package consulo.apache.wicket.editor.highlight;

import com.intellij.java.language.psi.PsiJavaFile;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.rawHighlight.HighlightVisitor;
import consulo.language.editor.rawHighlight.HighlightVisitorFactory;
import consulo.language.psi.PsiFile;
import consulo.xml.psi.xml.XmlFile;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 08/05/2023
 */
@ExtensionImpl
public class WicketForgeHighlightVisitorFactory implements HighlightVisitorFactory {
    @Override
    public boolean suitableForFile(@Nonnull PsiFile psiFile) {
        return psiFile instanceof XmlFile || psiFile instanceof PsiJavaFile;
    }

    @Nonnull
    @Override
    public HighlightVisitor createVisitor() {
        return new WicketForgeHighlightVisitor();
    }
}
