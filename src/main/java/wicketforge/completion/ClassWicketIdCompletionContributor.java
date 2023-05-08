/*
 * Copyright 2010 The WicketForge-Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicketforge.completion;

import com.intellij.java.language.JavaLanguage;
import com.intellij.java.language.impl.JavaFileType;
import com.intellij.java.language.psi.*;
import consulo.application.ApplicationManager;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.xml.psi.xml.XmlFile;
import wicketforge.Constants;
import wicketforge.psi.hierarchy.HierarchyUtil;
import wicketforge.psi.hierarchy.MarkupWicketIdHierarchy;
import wicketforge.psi.hierarchy.MarkupWicketIdItem;
import wicketforge.search.MarkupIndex;
import wicketforge.util.WicketPsiUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 */
public class ClassWicketIdCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(final CompletionParameters p, final CompletionResultSet rs) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                // lets do some basic checks...
                PsiFile f = p.getOriginalFile();
                if (f.getFileType() == JavaFileType.INSTANCE) {
                    PsiElement psiElement = p.getOriginalPosition();
                    if (psiElement instanceof PsiJavaToken) {
                        PsiExpression wicketIdExpression = getWicketIdExpression((PsiJavaToken) psiElement);
                        if (wicketIdExpression != null) {
                            PsiNewExpression wicketNewExpression = getWicketNewExpression(wicketIdExpression);
                            if (wicketNewExpression != null) {
                                PsiClass psiClass = WicketPsiUtil.getParentWicketClass(wicketNewExpression);
                                if (psiClass != null) {
                                    PsiFile markup = MarkupIndex.getBaseFile(psiClass);
                                    if (markup != null) {
                                        // ... before we search for our parent Item
                                        String parentPath = HierarchyUtil.findPathOf(psiClass, wicketIdExpression, true, true);
                                        if (parentPath != null) {
                                            MarkupWicketIdItem item = MarkupWicketIdHierarchy.create((XmlFile) markup).getWicketIdPathMap().get(parentPath);
                                            if (item != null) {
                                                for (MarkupWicketIdItem child : item.getChildren()) {
                                                    rs.addElement(
                                                            LookupElementBuilder.create(child.getWicketId())
                                                                    //.withIcon(child.getIcon()) // child's icon bother view -> use generic icon
                                                                    .withIcon(Constants.WICKET_COMPONENT_ICON)
                                                                    .withTypeText(".html")
                                                                    .withTailText("  " + child.getLocationString(), true)
                                                    );
                                                }
                                            }
                                        }
                                        rs.stopHere();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * @param position
     * @return possible candidate for wicketId (psiExpression) of position or null if not matches
     */
    @Nullable
    private PsiExpression getWicketIdExpression(@Nonnull PsiJavaToken position) {
        PsiElement element = position.getParent();
        if (!(element instanceof PsiLiteralExpression)) {
            return null;
        }

        return element.getParent() instanceof PsiExpressionList ? (PsiExpression) element : null;
    }

    @Nullable
    private PsiNewExpression getWicketNewExpression(@Nonnull PsiExpression wicketIdExpression) {
        PsiExpressionList expressionList = (PsiExpressionList) wicketIdExpression.getParent();
        PsiElement parent = expressionList.getParent();
        if (parent instanceof PsiAnonymousClass) {
            parent = parent.getParent();
        }
        if (!(parent instanceof PsiNewExpression)) {
            return null;
        }

        PsiClass psiClass = WicketPsiUtil.getClassFromNewExpression((PsiNewExpression) parent);

        return psiClass != null && WicketPsiUtil.isWicketComponent(psiClass) ? (PsiNewExpression) parent : null;
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return JavaLanguage.INSTANCE;
    }
}
