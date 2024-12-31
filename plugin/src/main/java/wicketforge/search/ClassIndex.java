/*
 * Copyright 2013 The WicketForge-Team
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
package wicketforge.search;

import com.intellij.java.language.impl.psi.impl.compiled.ClsClassImpl;
import com.intellij.java.language.psi.JavaPsiFacade;
import com.intellij.java.language.psi.PsiClass;
import consulo.language.psi.PsiFile;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.project.Project;
import consulo.project.content.scope.ProjectScopes;
import wicketforge.util.WicketPsiUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class ClassIndex {
    private ClassIndex() {
        // not Index based
    }

    /**
     * Returns the class of an associated resource (markup or properties) file
     *
     * @param psiFile resourcefile
     * @return the associated PsiClass or null if no such class exists.
     */
    @Nullable
    public static PsiClass getAssociatedClass(@Nonnull PsiFile psiFile) {
        ResourceInfo resourceInfo = ResourceInfo.from(psiFile);
        if (resourceInfo == null) {
            return null;
        }

        Project project = psiFile.getProject();

        GlobalSearchScope scope;
        if (WicketPsiUtil.isInLibrary(psiFile)) {
            scope = (GlobalSearchScope) ProjectScopes.getLibrariesScope(project);
        } else {
            Module module = ModuleUtilCore.findModuleForPsiElement(psiFile);
            if (module == null) {
                return null;
            }
            scope = WicketSearchScope.classInModuleWithDependenciesAndLibraries(module);
        }

        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(resourceInfo.qualifiedName, scope);
        if (psiClass instanceof ClsClassImpl) {
            PsiClass sourceMirrorClass = ((ClsClassImpl) psiClass).getSourceMirrorClass();
            if (sourceMirrorClass != null) {
                psiClass = sourceMirrorClass;
            }
        }
        return psiClass;
    }
}
