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

import com.intellij.java.language.psi.PsiClass;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiReference;
import consulo.language.psi.PsiReferenceProvider;
import consulo.language.util.ProcessingContext;
import consulo.xml.psi.xml.XmlAttribute;
import consulo.xml.psi.xml.XmlAttributeValue;
import wicketforge.Constants;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.search.ClassIndex;

import jakarta.annotation.Nonnull;

/**
 */
public class MarkupWicketIdReferenceProvider extends PsiReferenceProvider
{
    @Nonnull
    @Override
    public PsiReference[] getReferencesByElement(@Nonnull PsiElement element, @Nonnull ProcessingContext context) {
        XmlAttributeValue attributeValue = (XmlAttributeValue) element;
        PsiElement parent = attributeValue.getParent();
        if (parent instanceof XmlAttribute && Constants.WICKET_ID.equals(((XmlAttribute) parent).getName())) {
            PsiFile psiFile = attributeValue.getContainingFile();
            if (psiFile != null) {
                if (WicketForgeFacet.hasFacetOrIsFromLibrary(element)) {
                    PsiClass psiClass = ClassIndex.getAssociatedClass(psiFile);
                    if (psiClass != null) {
                        return new PsiReference[] {new MarkupWicketIdReference(attributeValue, psiClass)};
                    }
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
