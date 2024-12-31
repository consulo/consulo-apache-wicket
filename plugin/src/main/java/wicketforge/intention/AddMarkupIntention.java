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
package wicketforge.intention;

import com.intellij.java.language.psi.JavaDirectoryService;
import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiJavaFile;
import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionAction;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiPackage;
import consulo.language.util.IncorrectOperationException;
import consulo.module.Module;
import consulo.project.Project;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.util.WicketFileUtil;
import wicketforge.util.WicketPsiUtil;

import jakarta.annotation.Nonnull;

/**
 * AddMarkupIntention
 */
abstract class AddMarkupIntention implements IntentionAction {

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    @Override
    public boolean isAvailable(@Nonnull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }

        int offset = editor.getCaretModel().getOffset();

        PsiElement element = file.findElementAt(offset);
        if (element == null) {
            return false;
        }

        element = element.getParent();
        if (element == null || !(element instanceof PsiClass)) {
            return false;
        }

        if (WicketPsiUtil.isInLibrary(element)) {
            return false;
        }

        PsiClass psiClass = (PsiClass) element;

        return psiClass.getName() != null && // add..intention needs a name for resource (ex anonymous classes dont have) (issue 54)
                WicketForgeFacet.isLibraryPresent(element.getModule()) && // let user create page/panel when we have a wicket-lib (so we can detect new facet)
                isApplicableForClass(psiClass) &&
                !hasResourceFile(psiClass);
    }

    @Override
    public void invoke(@Nonnull final Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if (element != null) {
            element = element.getParent();
            if (!(element instanceof PsiClass)) {
                return;
            }
            PsiDirectory fileDirectory = file.getContainingDirectory();
            if (fileDirectory == null) {
                return;
            }
            Module module = fileDirectory.getModule();
            if (module == null) {
                return;
            }
            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(fileDirectory);
            if (psiPackage == null) {
                return;
            }

            PsiDirectory directory = WicketFileUtil.selectTargetDirectory(psiPackage.getQualifiedName(), project, module);
            if (directory != null) {
                WicketFileUtil.createFileFromTemplate(getResourceFileName((PsiClass) element), directory, getTemplateName());
            }
        }
    }

    protected abstract boolean hasResourceFile(@Nonnull PsiClass psiClass);

    @Nonnull
    protected abstract String getResourceFileName(@Nonnull PsiClass psiClass);

    @Nonnull
    protected abstract String getTemplateName();

    protected abstract boolean isApplicableForClass(@Nonnull PsiClass psiClass);
}
