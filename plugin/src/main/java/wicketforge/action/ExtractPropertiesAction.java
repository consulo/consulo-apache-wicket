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

import com.intellij.java.language.impl.JavaFileType;
import com.intellij.java.language.psi.*;
import com.intellij.lang.properties.PropertiesUtil;
import com.intellij.lang.properties.psi.PropertiesFile;
import consulo.application.ApplicationManager;
import consulo.application.util.function.Computable;
import consulo.codeEditor.CaretModel;
import consulo.codeEditor.Editor;
import consulo.codeEditor.SelectionModel;
import consulo.codeEditor.util.EditorModificationUtil;
import consulo.language.editor.LangDataKeys;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DumbAwareAction;
import consulo.ui.ex.action.Presentation;
import consulo.ui.ex.awt.Messages;
import consulo.xml.ide.highlighter.HtmlFileType;
import consulo.xml.psi.xml.XmlFile;
import wicketforge.action.ui.ExtractPropertiesDialog;
import wicketforge.search.ClassIndex;
import wicketforge.util.WicketFileUtil;
import wicketforge.util.WicketPsiUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Extract text from java/markup to a properties file.
 */
public class ExtractPropertiesAction extends DumbAwareAction {

    @RequiredUIAccess
    @Override
    public void actionPerformed(@Nonnull AnActionEvent e) {
        Editor editor = e.getData(Editor.KEY);
        if (editor == null) {
            return;
        }
        final Project project = editor.getProject();
        if (project == null) {
            return;
        }
        final PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        PsiDirectory psiDirectory = psiFile.getContainingDirectory();
        if (psiDirectory == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        if (offset <= 0) {
            return;
        }
        final PsiElement psiElement = psiFile.findElementAt(offset);
        if (psiElement == null) {
            return;
        }
        final PsiClass psiClass;
        String selectedText = null;
        if (JavaFileType.INSTANCE.equals(psiFile.getFileType())) {
            psiClass = WicketPsiUtil.getParentWicketClass(psiElement);
            // text to extract from String
            if (psiElement.getParent() instanceof PsiLiteralExpression) {
                selectedText = (String) ((PsiLiteralExpression) psiElement.getParent()).getValue();
            }
            if (selectedText == null) {
                Messages.showErrorDialog(project, "No string to extract", "Extract Text");
                return;
            }
        }
        else if (HtmlFileType.INSTANCE.equals(psiFile.getFileType())) {
            psiClass = ClassIndex.getAssociatedClass(psiFile);
            // text to extract from selection
            selectedText = editor.getSelectionModel().getSelectedText();
            if (selectedText == null) {
                Messages.showErrorDialog(project, "No selected text to extract", "Extract Text");
                return;
            }
        }
        else {
            return;
        }

        if (psiClass == null) {
            return;
        }

        ExtractPropertiesDialog.ActionRunnable actionRunnable = new ExtractPropertiesDialog.ActionRunnable() {
            @Override
            public boolean run(@Nullable final Object selectedItem, @Nonnull final PsiDirectory destinationDirectory, final @Nonnull String key, final @Nonnull String value) {
                return ApplicationManager.getApplication().runWriteAction(new Computable<Boolean>() {
                    @Override
                    public Boolean compute() {
                        final PropertiesFile propertiesFile;

                        if (selectedItem instanceof ExtractPropertiesDialog.NewPropertiesFileInfo) {
                            // create new properties file
                            ExtractPropertiesDialog.NewPropertiesFileInfo newPropertiesFileInfo = (ExtractPropertiesDialog.NewPropertiesFileInfo) selectedItem;
                            PsiElement element = WicketFileUtil.createFileFromTemplate(newPropertiesFileInfo.getName(), destinationDirectory, newPropertiesFileInfo.getPropertiesType().getTemplateName());
                            if (element == null) {
                                Messages.showErrorDialog(project, "Could not create properties file.", "Extract Text");
                                return false;
                            }
                            propertiesFile = PropertiesUtil.getPropertiesFile((PsiFile) element);
                        }
                        else if (selectedItem instanceof PropertiesFile) {
                            // use existing properties file
                            propertiesFile = (PropertiesFile) selectedItem;
                        }
                        else {
                            throw new IllegalArgumentException("Unsupported type " + selectedItem);
                        }

                        if (propertiesFile != null) {
                            // add
                            propertiesFile.addProperty(key, value);

                            if (psiFile instanceof XmlFile) {
                                // replace in html with wicket:message
                                String startTag = String.format("<wicket:message key=\"%s\">", key);
                                String endTag = "</wicket:message>";
                                CaretModel caret = editor.getCaretModel();
                                SelectionModel selection = editor.getSelectionModel();
                                int start = selection.getSelectionStart();
                                int end = selection.getSelectionEnd();
                                selection.removeSelection();

                                caret.moveToOffset(start);
                                EditorModificationUtil.insertStringAtCaret(editor, startTag);
                                // move the caret to the end of the selection
                                caret.moveToOffset(startTag.length() + end);
                                EditorModificationUtil.insertStringAtCaret(editor, endTag);
                            }
                            else if (psiFile instanceof PsiJavaFile) {
                                // replace in java with getString(...)
                                final PsiExpression expression = JavaPsiFacade.getElementFactory(psiClass.getProject()).createExpressionFromText("getString(\"" + key + "\")", psiClass);
                                psiElement.getParent().replace(expression);
                            }
                        }

                        return true;
                    }
                });
            }
        };
        ExtractPropertiesDialog dialog = new ExtractPropertiesDialog(project, actionRunnable, "Extract Text", psiClass, psiDirectory, selectedText);
        dialog.show();
    }

    @RequiredUIAccess
    @Override
    public void update(@Nonnull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Editor editor = e.getData(Editor.KEY);
        presentation.setEnabledAndVisible(editor != null);
    }
}
