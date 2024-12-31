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
import com.intellij.java.language.psi.PsiJavaFile;
import com.intellij.java.language.psi.PsiNewExpression;
import consulo.fileEditor.structureView.StructureViewTreeElement;
import consulo.fileEditor.structureView.tree.TreeElement;
import consulo.language.editor.structureView.TextEditorBasedStructureViewModel;
import consulo.language.psi.PsiElement;
import consulo.language.psi.util.PsiNavigateUtil;
import consulo.navigation.ItemPresentation;
import consulo.util.collection.SmartList;
import wicketforge.util.WicketPsiUtil;

import jakarta.annotation.Nonnull;
import java.util.List;

/**
 */
public class ClassStructureTreeModel extends TextEditorBasedStructureViewModel
{
    private StructureViewTreeElement root;
    private static final TreeElement[] EMPTY_TREE_ELEMENTS = new TreeElement[0];

    public ClassStructureTreeModel(@Nonnull PsiJavaFile psiJavaFile) {
        super(psiJavaFile);
        root = new JavaFileTreeElement(psiJavaFile);
    }

    @Override
    @Nonnull
    public StructureViewTreeElement getRoot() {
        return root;
    }

    @Override
    protected boolean isSuitable(PsiElement element) {
        if (element instanceof PsiNewExpression) {
            for (TreeElement treeElement : getRoot().getChildren()) {
                if (treeElement instanceof ClassTreeElement) {
                    return ((ClassTreeElement) treeElement).classWicketIdReferences.getNewComponentItem((PsiNewExpression) element) != null;
                }
            }
        } else if (element instanceof PsiClass) {
            return WicketPsiUtil.isWicketComponentWithAssociatedMarkup((PsiClass) element);
        }
        return super.isSuitable(element);
    }

    private static class JavaFileTreeElement implements StructureViewTreeElement {
        private PsiJavaFile psiJavaFile;

        private JavaFileTreeElement(@Nonnull PsiJavaFile psiJavaFile) {
            this.psiJavaFile = psiJavaFile;
        }

        @Override
        public Object getValue() {
            return psiJavaFile;
        }

        @Override
        public void navigate(boolean requestFocus) {
            psiJavaFile.navigate(requestFocus);
        }

        @Override
        public boolean canNavigate() {
            return psiJavaFile.canNavigate();
        }

        @Override
        public boolean canNavigateToSource() {
            return psiJavaFile.canNavigateToSource();
        }

        @Override
        public ItemPresentation getPresentation() {
            return psiJavaFile.getPresentation();
        }

        private TreeElement[] children;

        @Override
        public TreeElement[] getChildren() {
            if (children == null) {
                List<TreeElement> list = new SmartList<TreeElement>();
                for (PsiClass psiClass : psiJavaFile.getClasses()) {
                    if (WicketPsiUtil.isWicketComponentWithAssociatedMarkup(psiClass)) {
                        list.add(new ClassTreeElement(psiClass, ClassWicketIdReferences.build(psiClass, false)));
                    }
                }
                children = list.isEmpty() ? EMPTY_TREE_ELEMENTS : list.toArray(new TreeElement[list.size()]);
            }
            return children;
        }
    }

    private static class ClassTreeElement implements StructureViewTreeElement {
        private TreeElement[] children;
        final ClassWicketIdReferences classWicketIdReferences;
        final PsiClass psiClass;

        private ClassTreeElement(@Nonnull PsiClass psiClass, @Nonnull ClassWicketIdReferences classWicketIdReferences) {
            this.psiClass = psiClass;
            this.classWicketIdReferences = classWicketIdReferences;
        }

        @Override
        public Object getValue() {
            return psiClass;
        }

        @Override
        public void navigate(boolean requestFocus) {
            psiClass.navigate(requestFocus);
        }

        @Override
        public boolean canNavigate() {
            return psiClass.canNavigate();
        }

        @Override
        public boolean canNavigateToSource() {
            return psiClass.canNavigateToSource();
        }

        @Override
        public ItemPresentation getPresentation() {
            return psiClass.getPresentation();
        }

        @Override
        public TreeElement[] getChildren() {
            if (children == null) {
                List<TreeElement> list = new SmartList<TreeElement>();
                List<PsiNewExpression> addedComponents = classWicketIdReferences.getAdded(psiClass);
                if (addedComponents != null) {
                    for (PsiNewExpression addedComponent : addedComponents) {
                        list.add(new WicketIdTreeElement(addedComponent));
                    }
                }
                for (PsiClass aClass : psiClass.getAllInnerClasses()) {
                    if (classWicketIdReferences.containsClass(aClass) && WicketPsiUtil.isWicketComponentWithAssociatedMarkup(aClass)) {
                        list.add(new ClassTreeElement(aClass, classWicketIdReferences));
                    }
                }
                children = list.isEmpty() ? EMPTY_TREE_ELEMENTS : list.toArray(new TreeElement[list.size()]);
            }
            return children;
        }

        private class WicketIdTreeElement implements StructureViewTreeElement {
            private TreeElement[] children;
            private final PsiNewExpression psiElement;
            private final ClassWicketIdNewComponentItem newComponentItem;

            private WicketIdTreeElement(PsiNewExpression psiElement) {
                this.psiElement = psiElement;
                this.newComponentItem = classWicketIdReferences.getNewComponentItem(psiElement);
            }

            @Override
            public Object getValue() {
                return psiElement;
            }

            @Override
            public void navigate(boolean requestFocus) {
                PsiNavigateUtil.navigate(newComponentItem != null ? newComponentItem.getWicketIdExpression() : psiElement);
            }

            @Override
            public boolean canNavigate() {
                return true;
            }

            @Override
            public boolean canNavigateToSource() {
                return true;
            }

            @Override
            public ItemPresentation getPresentation() {
                return newComponentItem;
            }

            @Override
            public TreeElement[] getChildren() {
                if (children == null) {
                    List<PsiNewExpression> addedComponents = classWicketIdReferences.getAdded(psiElement);
                    if (addedComponents != null) {
                        children = new TreeElement[addedComponents.size()];
                        for (int i = 0, addedComponentsSize = addedComponents.size(); i < addedComponentsSize; i++) {
                            PsiNewExpression addedComponent = addedComponents.get(i);
                            children[i] = new WicketIdTreeElement(addedComponent);
                        }
                    } else {
                        children = EMPTY_TREE_ELEMENTS;
                    }
                }
                return children;
            }
        }
    }
}
