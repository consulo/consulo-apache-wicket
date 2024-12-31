package consulo.apache.wicket.editor.highlight;

import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiExpression;
import com.intellij.java.language.psi.PsiNewExpression;
import consulo.document.util.TextRange;
import consulo.language.editor.rawHighlight.HighlightInfo;
import consulo.language.editor.rawHighlight.HighlightInfoHolder;
import consulo.language.editor.rawHighlight.HighlightInfoType;
import consulo.language.editor.rawHighlight.HighlightVisitor;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiReference;
import consulo.xml.psi.xml.XmlAttribute;
import consulo.xml.psi.xml.XmlAttributeValue;
import wicketforge.Constants;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.highlighting.WicketForgeColorSettingsPage;
import wicketforge.psi.references.ClassWicketIdReference;
import wicketforge.psi.references.MarkupWicketIdReference;
import wicketforge.util.WicketPsiUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 24/01/2023
 */
public class WicketForgeHighlightVisitor implements HighlightVisitor {
    private HighlightInfoHolder highlightInfoHolder;

    @Override
    public void visit(@Nonnull PsiElement psiElement) {
        if (psiElement instanceof XmlAttribute attribute) {
            if (Constants.WICKET_ID.equals(attribute.getName())) {
                XmlAttributeValue attributeValue = attribute.getValueElement();
                if (attributeValue != null && hasReference(attributeValue, MarkupWicketIdReference.class)) {
                    HighlightInfo highlightInfo = createHighlightInfo(WicketForgeColorSettingsPage.HIGHLIGHT_MARKUPWICKETID, attributeValue.getTextRange());
                    if (highlightInfo != null) {
                        highlightInfoHolder.add(highlightInfo);
                    }
                }
            }
        } else if (psiElement instanceof PsiNewExpression expression) {
            PsiClass psiClass = WicketPsiUtil.getClassFromNewExpression(expression);
            // if its a component
            if (psiClass != null && WicketPsiUtil.isWicketComponent(psiClass)) {
                // highlight wicketId expression (but only if its not a page)
                if (!WicketPsiUtil.isWicketPage(psiClass)) {
                    PsiExpression wicketIdExpression = WicketPsiUtil.getWicketIdExpressionFromArguments(expression);
                    if (wicketIdExpression != null) {
                        // only PsiLiteralExpression are resolvable wicketIds
                        HighlightInfo highlightInfo = createHighlightInfo(
                                hasReference(wicketIdExpression, ClassWicketIdReference.class) ? WicketForgeColorSettingsPage.HIGHLIGHT_JAVAWICKETID : WicketForgeColorSettingsPage.HIGHLIGHT_JAVAWICKETID_NOTRESOLVABLE,
                                wicketIdExpression.getTextRange());
                        if (highlightInfo != null) {
                            highlightInfoHolder.add(highlightInfo);
                        }
                    }
                }
            }
        }
    }


    private boolean hasReference(@Nonnull final PsiElement element, @Nonnull final Class<? extends PsiReference> referenceClass) {
        for (PsiReference reference : element.getReferences()) {
            if (reference.getClass().equals(referenceClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean analyze(@Nonnull PsiFile file, boolean b, @Nonnull HighlightInfoHolder highlightInfoHolder, @Nonnull Runnable runnable) {
        if (!WicketForgeFacet.hasFacetOrIsFromLibrary(file)) {
            return false;
        }

        this.highlightInfoHolder = highlightInfoHolder;
        runnable.run();
        return true;
    }

    @Nullable
    private static HighlightInfo createHighlightInfo(HighlightInfoType type, TextRange textRange) {
        return HighlightInfo.newHighlightInfo(type).range(textRange).create();
    }
}
