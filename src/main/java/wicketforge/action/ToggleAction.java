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
package wicketforge.action;

import com.intellij.java.language.psi.JavaDirectoryService;
import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiJavaFile;
import consulo.codeEditor.Editor;
import consulo.fileEditor.FileEditorManager;
import consulo.language.editor.LangDataKeys;
import consulo.language.editor.hint.HintManager;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiPackage;
import consulo.language.psi.util.PsiNavigateUtil;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.awt.Messages;
import consulo.virtualFileSystem.VirtualFile;
import consulo.xml.psi.xml.XmlFile;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.search.ClassIndex;
import wicketforge.search.MarkupIndex;
import wicketforge.templates.WicketTemplates;
import wicketforge.util.WicketFileUtil;
import wicketforge.util.WicketFilenameUtil;
import wicketforge.util.WicketPsiUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 */
public class ToggleAction extends AnAction
{
    @Override
    public void update(AnActionEvent e) {
        final PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        // let user toggle when we have a wicket facet or we are in a lib (could be wicket lib)
        e.getPresentation().setEnabled(WicketForgeFacet.hasFacetOrIsFromLibrary(psiFile));
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Editor editor = event.getData(LangDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }

        if (!WicketForgeFacet.hasFacetOrIsFromLibrary(psiFile)) {
            return;
        }

        if (psiFile instanceof XmlFile) {
            PsiClass psiClass = ClassIndex.getAssociatedClass(psiFile);
            if (psiClass == null) {
                HintManager.getInstance().showInformationHint(editor, "No corresponding java class found");
            } else {
                PsiFile classFile =  psiClass.getContainingFile();
                // if file alredy open just navigate to file (and not to class) so caret position gets not changed (issue 92)
                if (classFile != null) {
                    VirtualFile vf = classFile.getVirtualFile();
                    if (vf != null && FileEditorManager.getInstance(psiClass.getProject()).isFileOpen(vf)) {
                        classFile.navigate(true);
                        return;
                    }
                }
                psiClass.navigate(true);
            }
        } else if (psiFile instanceof PsiJavaFile) {
            PsiClass psiClass = null;
            // look for wicket class at caret
            int offset = editor.getCaretModel().getOffset();
            if (offset >= 0) {
                PsiElement currentElement = psiFile.findElementAt(offset);
                if (currentElement != null) {
                    psiClass = WicketPsiUtil.getParentWicketClass(currentElement);
                }
            }
            // if not found -> look for (first) class of file
            if (psiClass == null) {
                PsiClass[] psiClasses = ((PsiJavaFile) psiFile).getClasses();
                if (psiClasses.length > 0) {
                    psiClass = WicketPsiUtil.getParentWicketClass(psiClasses[0]);
                }
            }

            if (psiClass == null) {
                HintManager.getInstance().showErrorHint(editor, "No wicket page/panel found");
                return;
            }

            // get markupFile
            PsiElement markupFile = MarkupIndex.getBaseFile(psiClass);
            if (markupFile == null) {
                // no markup file found -> ask to create one
                final Module module = ModuleUtilCore.findModuleForPsiElement(psiFile);
                if (module != null && !WicketPsiUtil.isInLibrary(psiClass)) {
                    markupFile = createMarkup(module, psiFile, psiClass);
                    if (markupFile == null) { // cancel
                        return;
                    }
                }
            }
            if (markupFile != null) {
                PsiNavigateUtil.navigate(markupFile);
            } else {
                HintManager.getInstance().showErrorHint(editor, "No corresponding markup file found");
            }
        }
    }

    @Nullable
    private PsiElement createMarkup(@Nonnull Module module, @Nonnull PsiFile psiFile, @Nonnull PsiClass psiClass) {
        PsiDirectory psiDirectory = psiFile.getContainingDirectory();
        if (psiDirectory == null) {
            return null;
        }
        PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
        if (psiPackage == null) {
            return null;
        }

        String templateName = null;
        if (WicketPsiUtil.isWicketPage(psiClass)) {
            templateName = WicketTemplates.WICKET_PAGE_HTML;
        } else if (WicketPsiUtil.isWicketPanel(psiClass)) {
            templateName = WicketTemplates.WICKET_PANEL_HTML;
        } else if (WicketPsiUtil.isWicketBorder(psiClass)) {
            templateName = WicketTemplates.WICKET_BORDER_HTML;
        }
        if (templateName != null &&
                Messages.showYesNoDialog(module.getProject(),
                        String.format("Create a new markup for '%s'?", psiClass.getQualifiedName()),
                        "Create Markup",
                        Messages.getQuestionIcon()) == 0
                ) {
            PsiDirectory directory = WicketFileUtil.selectTargetDirectory(psiPackage.getQualifiedName(), module.getProject(), module);
            if (directory != null) {
                // create
                return WicketFileUtil.createFileFromTemplate(WicketFilenameUtil.getMarkupFilename(psiClass), directory, templateName);
            }
        }
        return null;
    }
}
