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
package wicketforge.psi.references;

import com.intellij.java.language.psi.*;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.PsiReferenceProvider;
import consulo.language.util.ProcessingContext;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.search.MarkupIndex;
import wicketforge.util.WicketPsiUtil;

import jakarta.annotation.Nonnull;

/**
 */
public class ClassWicketIdReferenceProvider extends PsiReferenceProvider
{
    @Nonnull
    @Override
    public PsiReference[] getReferencesByElement(@Nonnull PsiElement element, @Nonnull ProcessingContext context) {
        PsiExpressionList expressionList = (PsiExpressionList) element.getParent();
        PsiExpression[] expressions = expressionList.getExpressions();
        if (expressions.length > 0 && expressions[0].equals(element)) {
            if (WicketForgeFacet.hasFacetOrIsFromLibrary(element)) {
                PsiElement parent = expressionList.getParent(); // can be PsiNewExpression or PsiAnonymousClass
                PsiNewExpression newExpression = (PsiNewExpression) (parent instanceof PsiNewExpression ? parent : parent.getParent());
                PsiJavaCodeReferenceElement clazzReference = newExpression.getClassOrAnonymousClassReference();
                if (clazzReference != null) {
                    PsiElement clazzElement = clazzReference.resolve();
                    if (clazzElement instanceof PsiClass) {
                        PsiClass psiClass = (PsiClass) clazzElement;
                        if (WicketPsiUtil.isWicketComponent(psiClass) && !WicketPsiUtil.isWicketPage(psiClass)) {
                            PsiClass wicketClass = WicketPsiUtil.getParentWicketClass(newExpression);
                            if (wicketClass != null && MarkupIndex.getBaseFile(wicketClass) != null) {
                                return new PsiReference[] {new ClassWicketIdReference((PsiLiteralExpression) element, wicketClass)};
                            }
                        }
                    }
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
