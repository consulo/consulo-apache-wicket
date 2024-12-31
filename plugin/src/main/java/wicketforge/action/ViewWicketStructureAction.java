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

import com.intellij.java.language.psi.PsiJavaFile;
import consulo.codeEditor.Editor;
import consulo.fileEditor.FileEditor;
import consulo.fileEditor.structureView.StructureView;
import consulo.fileEditor.structureView.StructureViewModel;
import consulo.ide.impl.idea.ide.structureView.newStructureView.StructureViewComponent;
import consulo.ide.impl.idea.ide.util.FileStructurePopup;
import consulo.language.editor.PlatformDataKeys;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.Presentation;
import consulo.virtualFileSystem.VirtualFile;
import consulo.xml.psi.xml.XmlFile;
import wicketforge.psi.hierarchy.ClassStructureTreeModel;
import wicketforge.psi.hierarchy.MarkupStructureTreeModel;

import jakarta.annotation.Nonnull;

public class ViewWicketStructureAction extends AnAction
{
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        if (project == null) {
            return;
        }
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        if (offset <= 0) {
            return;
        }
        PsiElement currentElement = psiFile.findElementAt(offset);
        if (currentElement == null) {
            return;
        }
        StructureViewModel viewModel;
        if (psiFile instanceof XmlFile) {
            viewModel = new MarkupStructureTreeModel((XmlFile) psiFile);
        } else if (psiFile instanceof PsiJavaFile) {
            viewModel = new ClassStructureTreeModel((PsiJavaFile) psiFile);
        } else {
            return;
        }
        FileEditor fileEditor = e.getData(PlatformDataKeys.FILE_EDITOR);
        if (fileEditor == null) {
            return;
        }
        StructureView structureView = new StructureViewComponent(fileEditor, viewModel, project, true);

        FileStructurePopup popup = createStructureViewPopup(project, fileEditor, structureView);
        popup.setTitle(psiFile.getName());
        popup.show();
    }

    @Nonnull
    private static FileStructurePopup createStructureViewPopup(@Nonnull Project project, @Nonnull FileEditor fileEditor, @Nonnull StructureView structureView) {
        return new FileStructurePopup(project, fileEditor, structureView, true);
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Project project = e.getData(Project.KEY);
        if (project == null) {
            presentation.setEnabled(false);
            return;
        }
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            presentation.setEnabled(false);
            return;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            presentation.setEnabled(false);
            return;
        }
        final VirtualFile virtualFile = psiFile.getVirtualFile();

        if (virtualFile == null) {
            presentation.setEnabled(false);
            return;
        }

        if (!(psiFile instanceof XmlFile) && !(psiFile instanceof PsiJavaFile)) {
            presentation.setEnabled(false);
            return;
        }

        presentation.setEnabled(true);
    }
}
