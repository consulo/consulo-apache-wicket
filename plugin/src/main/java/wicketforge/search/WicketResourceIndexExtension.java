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

import com.intellij.java.language.psi.PsiClass;
import consulo.index.io.DataIndexer;
import consulo.index.io.EnumeratorStringDescriptor;
import consulo.index.io.ID;
import consulo.index.io.KeyDescriptor;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.language.psi.PsiUtilCore;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.FileBasedIndex;
import consulo.language.psi.stub.FileContent;
import consulo.language.psi.stub.ScalarIndexExtension;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.project.content.scope.ProjectScopes;
import consulo.virtualFileSystem.VirtualFile;
import wicketforge.util.WicketPsiUtil;

import jakarta.annotation.Nonnull;
import java.util.*;

abstract class WicketResourceIndexExtension extends ScalarIndexExtension<String> implements FileBasedIndex.InputFilter, DataIndexer<String, Void, FileContent>
{
    private final EnumeratorStringDescriptor keyDescriptor = new EnumeratorStringDescriptor();
    private static final char LOCALIZEDFILE_INDEXMARKER = '#';

    protected WicketResourceIndexExtension() {
       /* messageBus.connect().subscribe(WicketForgeFacetConfiguration.ADDITIONAL_PATHS_CHANGED, new Runnable() {
            @Override
            public void run() {
                FileBasedIndex.getInstance().requestRebuild(getName());
            }
        }); */
    }

    @Nonnull
    @Override
    public Map<String, Void> map(FileContent inputData) {
        ResourceInfo resourceInfo = ResourceInfo.from(inputData);
        if (resourceInfo == null) {
            return Collections.emptyMap();
        } else if (resourceInfo.locale == null) {
            return Collections.singletonMap(resourceInfo.qualifiedName, null);
        } else {
            return Collections.singletonMap(resourceInfo.qualifiedName + LOCALIZEDFILE_INDEXMARKER, null);
        }
    }

    @Nonnull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return this;
    }

    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return this;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Nonnull
    protected static PsiFile[] getFilesByClass(@Nonnull ID<String, Void> indexId, @Nonnull final PsiClass psiClass, boolean all) {
        String name = psiClass.getQualifiedName();
        if (name == null) {
            return PsiFile.EMPTY_ARRAY;
        }

        GlobalSearchScope scope;
        if (WicketPsiUtil.isInLibrary(psiClass)) {
            scope = (GlobalSearchScope) ProjectScopes.getLibrariesScope(psiClass.getProject());
        } else {
            Module module = ModuleUtilCore.findModuleForPsiElement(psiClass);
            if (module == null) {
                return PsiFile.EMPTY_ARRAY;
            }
            scope = WicketSearchScope.resourcesInModuleWithDependenciesAndLibraries(module);
        }

        final Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(indexId, name, scope);
        if (all) {
            files.addAll(FileBasedIndex.getInstance().getContainingFiles(indexId, name + LOCALIZEDFILE_INDEXMARKER, scope));
        }
        if (files.isEmpty()) {
            return PsiFile.EMPTY_ARRAY;
        }
        List<PsiFile> result = new ArrayList<PsiFile>();
        PsiManager manager = PsiManager.getInstance(psiClass.getProject());
        for (VirtualFile file : files) {
            if (file.isValid()) {
                PsiFile psiFile = manager.findFile(file);
                if (psiFile != null) {
                    result.add(psiFile);
                }
            }
        }
        return PsiUtilCore.toPsiFileArray(result);
    }
}
