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
package wicketforge.psi.hierarchy;

import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiNewExpression;
import consulo.language.psi.PsiElement;
import consulo.logging.Logger;
import wicketforge.Constants;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ClassWicketIdHierarchy {
    private static final Logger LOG = Logger.getInstance(ClassWicketIdHierarchy.class);

    private Map<String, ClassWicketIdItem> wicketIdPathMap;

    @Nonnull
    public static ClassWicketIdHierarchy create(@Nonnull PsiClass psiClass) {
        return new ClassWicketIdHierarchy(psiClass);
    }

    private ClassWicketIdHierarchy(@Nonnull final PsiClass psiClass) {
        this.wicketIdPathMap = new HashMap<String, ClassWicketIdItem>();

        ClassWicketIdItem root = new ClassWicketIdItem("", null);
        this.wicketIdPathMap.put("", root);

        ClassWicketIdReferences classWicketIdReferences = ClassWicketIdReferences.build(psiClass);

        List<PsiNewExpression> addedComponents = classWicketIdReferences.getAdded(psiClass);
        if (addedComponents != null) {
            addRecursive(classWicketIdReferences, new StringBuilder(), root, psiClass, addedComponents, 0);
        }
    }

    private void addRecursive(@Nonnull ClassWicketIdReferences classWicketIdReferences,
                              @Nonnull StringBuilder path,
                              @Nonnull ClassWicketIdItem parent,
                              @Nonnull PsiElement parentElement,
                              @Nullable List<PsiNewExpression> addedComponents,
                              int depth) {
        if (depth++ > 50) {
            LOG.error("Deep addRecursive: " + path.toString());
            return;
        }
        
        if (addedComponents != null) {
            for (PsiNewExpression newExpression : addedComponents) {
                // fix issue 95: add the component to itself (no sense) but crash wicketforge
                if (newExpression.equals(parentElement)) {
                    continue;
                }
                ClassWicketIdNewComponentItem newComponentItem = classWicketIdReferences.getNewComponentItem(newExpression);
                if (newComponentItem != null && !parent.contains(newComponentItem)) {
                    int length = path.length();
                    try {
                        path.append(Constants.HIERARCHYSEPARATOR).append(newComponentItem.getWicketId());

                        ClassWicketIdItem child = findOrCreateChild(path, parent, newComponentItem.getWicketId());

                        child.getNewComponentItems().add(newComponentItem);

                        addRecursive(classWicketIdReferences, path, child, newExpression, classWicketIdReferences.getAdded(newExpression), depth);
                    } finally {
                        path.setLength(length);
                    }
                }
            }
        }
        // add also components of superclass (if any... ex: MyPage.java -> inner class MyMarkupContainer -> label myMarkupContainer )
        PsiClass superClass = null;
        if (parentElement instanceof PsiClass) {
            superClass = ((PsiClass) parentElement).getSuperClass();
        } else if (parentElement instanceof PsiNewExpression) {
            PsiNewExpression newExpression = (PsiNewExpression) parentElement;
            ClassWicketIdNewComponentItem newComponentItem = classWicketIdReferences.getNewComponentItem(newExpression);
            if (newComponentItem != null) {
                superClass = newComponentItem.getBaseClassToCreate();
            }
        }
        if (superClass != null) {
            List<PsiNewExpression> superAddedComponents = classWicketIdReferences.getAdded(superClass);
            if (superAddedComponents != null) {
                addRecursive(classWicketIdReferences, path, parent, superClass, superAddedComponents, depth);
            }
        }
    }

    @Nonnull
    private ClassWicketIdItem findOrCreateChild(@Nonnull StringBuilder path, @Nonnull ClassWicketIdItem parent, @Nonnull String wicketId) {
        ClassWicketIdItem child = parent.findChild(wicketId);
        if (child == null) {
            child = new ClassWicketIdItem(wicketId, parent);
            wicketIdPathMap.put(path.toString(), child);
        }
        return child;
    }

    @Nonnull
    public Map<String, ClassWicketIdItem> getWicketIdPathMap() {
        return wicketIdPathMap;
    }
}
