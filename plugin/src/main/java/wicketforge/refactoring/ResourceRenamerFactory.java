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
package wicketforge.refactoring;

import com.intellij.java.language.psi.PsiClass;
import consulo.language.editor.refactoring.rename.AutomaticRenamer;
import consulo.language.editor.refactoring.rename.AutomaticRenamerFactory;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiNamedElement;
import consulo.localize.LocalizeValue;
import consulo.usage.UsageInfo;
import consulo.util.collection.SmartList;
import wicketforge.util.WicketFilenameUtil;

import jakarta.annotation.Nonnull;
import java.util.*;

abstract class ResourceRenamerFactory implements AutomaticRenamerFactory
{
    private final String entityName;
    private final String[] fileExtensions;

    protected ResourceRenamerFactory(@Nonnull String entityName, @Nonnull String[] fileExtensions) {
        this.entityName = entityName;
        this.fileExtensions = fileExtensions;
    }

    @Override
    public boolean isApplicable(PsiElement element) {
        return element instanceof PsiClass && !getAllFilesIncludeInnerclasses((PsiClass) element).isEmpty();
    }

    @Override
    public LocalizeValue getOptionName() {
        return LocalizeValue.localizeTODO("Rename Wicket " + entityName);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public AutomaticRenamer createRenamer(PsiElement element, String newName, Collection<UsageInfo> usages) {
        return new ResourceRenamer((PsiClass) element, newName);
    }

    @Nonnull
    protected abstract PsiFile[] getAllFiles(@Nonnull PsiClass psiClass);

    @Nonnull
    private List<PsiFile> getAllFilesIncludeInnerclasses(@Nonnull PsiClass psiClass) {
        List<PsiFile> files = new SmartList<PsiFile>();
        Queue<PsiClass> queue = new LinkedList<PsiClass>();
        queue.add(psiClass);
        while (!queue.isEmpty()) {
            psiClass = queue.poll();
            Collections.addAll(queue, psiClass.getInnerClasses());
            Collections.addAll(files, getAllFiles(psiClass));
        }
        return files;
    }

    /**
     *
     */
    private class ResourceRenamer extends AutomaticRenamer {
        public ResourceRenamer(PsiClass aClass, String newClassName) {
            if (aClass.getQualifiedName() != null) {
                this.myElements.addAll(getAllFilesIncludeInnerclasses(aClass));
                suggestAllNames(aClass.getName(), newClassName);
            }
        }

        @Override
        public String nameToCanonicalName(String name, PsiNamedElement element) {
            String filenameWithoutExtension = WicketFilenameUtil.removeExtension(name, fileExtensions);
            return WicketFilenameUtil.extractBasename(filenameWithoutExtension);
        }

        @Override
        public String canonicalNameToName(String canonicalName, PsiNamedElement element) {
            String currentName = element.getName();
            String currentExtension = currentName != null ? WicketFilenameUtil.extractExtension(currentName, fileExtensions) : null;
            String currentLocale = currentName != null ? WicketFilenameUtil.extractLocale(WicketFilenameUtil.removeExtension(currentName, fileExtensions)) : null;

            StringBuilder sb = new StringBuilder(canonicalName);
            if (currentLocale != null) {
                sb.append('_').append(currentLocale);
            }
            if (currentExtension != null) {
                sb.append(currentExtension);
            }
            return sb.toString();
        }

        @Override
        public boolean isSelectedByDefault() {
            return true;
        }

        @Override
        public String getDialogTitle() {
            return "Rename " + entityName;
        }

        @Override
        public String getDialogDescription() {
            return "Rename " + entityName + " to";
        }

        @Override
        public String entityName() {
            return entityName;
        }
    }
}
