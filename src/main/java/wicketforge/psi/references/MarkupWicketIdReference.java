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

import java.util.List;

import javax.annotation.Nonnull;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.SmartList;
import wicketforge.psi.hierarchy.ClassWicketIdHierarchy;
import wicketforge.psi.hierarchy.ClassWicketIdItem;
import wicketforge.psi.hierarchy.ClassWicketIdNewComponentItem;
import wicketforge.psi.hierarchy.HierarchyUtil;

/**
 */
public class MarkupWicketIdReference implements PsiReference, PsiPolyVariantReference {
    private XmlAttributeValue attributeValue;
    private PsiClass psiClass;
    private TextRange textRange;

    public MarkupWicketIdReference(@Nonnull XmlAttributeValue attributeValue, @Nonnull PsiClass psiClass) {
        this.attributeValue = attributeValue;

        this.psiClass = psiClass;
        textRange = new TextRange(1, attributeValue.getTextLength() - 1);
    }

    @Override
    @Nonnull
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        String path = HierarchyUtil.findPathOf(attributeValue, false);
        if (path != null) {
            ClassWicketIdHierarchy hierarchy = ClassWicketIdHierarchy.create(psiClass);
            ClassWicketIdItem item = hierarchy.getWicketIdPathMap().get(path);
            if (item != null) {
                final List<PsiElementResolveResult> list = new SmartList<PsiElementResolveResult>();
                for (ClassWicketIdNewComponentItem newComponentItem : item.getNewComponentItems()) {
                    list.add(new PsiElementResolveResult(newComponentItem.getWicketIdExpression()));
                }
                if (!list.isEmpty()) {
                    return list.toArray(new ResolveResult[list.size()]);
                }
            }
        }
        return ResolveResult.EMPTY_ARRAY;
    }

    @Override
    public PsiElement getElement() {
        return attributeValue;
    }

    @Override
    public TextRange getRangeInElement() {
        return textRange;
    }

    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @Override
    @Nonnull
    public String getCanonicalText() {
        return textRange.substring(attributeValue.getText());
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        final PsiElement elementAt = attributeValue.findElementAt(textRange.getStartOffset());
        assert elementAt != null;
        return ElementManipulators.getManipulator(elementAt).handleContentChange(elementAt, getRangeInElement(), newElementName);
    }

    @Override
    public PsiElement bindToElement(@Nonnull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        final PsiManager manager = attributeValue.getManager();
        for (final ResolveResult result : multiResolve(false)) {
            if (manager.areElementsEquivalent(result.getElement(), element)) return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public Object[] getVariants() {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

    @Override
    public boolean isSoft() {
        return true;
    }
}
