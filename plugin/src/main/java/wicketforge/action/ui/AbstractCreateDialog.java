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
package wicketforge.action.ui;

import com.intellij.java.impl.ui.ReferenceEditorComboWithBrowseButton;
import com.intellij.java.language.psi.JavaDirectoryService;
import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.util.TreeClassChooser;
import com.intellij.java.language.util.TreeClassChooserFactory;
import consulo.component.PropertiesComponent;
import consulo.ide.localize.IdeLocalize;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiPackage;
import consulo.localize.LocalizeValue;
import consulo.module.Module;
import consulo.project.Project;
import consulo.project.ProjectPropertiesComponent;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.RecentsManager;
import consulo.ui.ex.awt.DialogWrapper;
import wicketforge.search.WicketSearchScope;
import wicketforge.util.WicketFileUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public abstract class AbstractCreateDialog extends DialogWrapper
{
    private JTextField classNameTextField;
    private JCheckBox createAssociatedMarkupFileCheckBox;
    private JPanel contentPane;
    private JPanel chooseDifferentDestinationFolderPanel;
    private JCheckBox chooseDifferentDestinationFolderCheckBox;
    private JPanel extendsClassPanel;

    private ActionRunnable actionRunnable;
    private Project project;
    private Module module;
    private PsiDirectory markupDirectory;
    private PsiPackage psiPackage;
    private ReferenceEditorComboWithBrowseButton extendClassEditor;

    private final String storeKey;
    private final static String RECENT_EXTENDCLASS_KEY = "RECENT_EXTENDCLASS_KEY";
    private final static String CREATE_MARKUP_KEY = "CREATE_MARKUP_KEY";
    private final static String CHOOSE_DIFFERENT_DESTINATION_KEY = "CHOOSE_DIFFERENT_DESTINATION_KEY";

    AbstractCreateDialog(@Nonnull Project project, @Nonnull ActionRunnable actionRunnable, @Nonnull String title, @Nonnull PsiDirectory directory) {
        super(project, false);

        this.storeKey = "wicketforge." + getClass().getSimpleName() + ".";

        this.actionRunnable = actionRunnable;
        this.project = project;
        this.module = directory.getModule();
        this.markupDirectory = directory;
        this.psiPackage = JavaDirectoryService.getInstance().getPackage(directory);

        init();
        setTitle(title);
    }

    @Nonnull
    private String getStoreKey(@Nonnull String key) {
        return storeKey + key;
    }

    @Override
    protected void init() {
        super.init();

        setResizable(true);
        setModal(true);

        RecentsManager recentsManager = RecentsManager.getInstance(project);
        List<String> recentList = recentsManager.getRecentEntries(getStoreKey(RECENT_EXTENDCLASS_KEY));
        if (recentList == null || recentList.isEmpty()) { // we dont have recent entries yet -> just add defaultClass (WebPage/Panel)
            PsiClass psiClass = getDefaultClass(project);
            if (psiClass != null) {
                recentsManager.registerRecentEntry(getStoreKey(RECENT_EXTENDCLASS_KEY), psiClass.getQualifiedName());
            }
        }
        PropertiesComponent propertiesComponent = ProjectPropertiesComponent.getInstance(project);
        // restore last used options
        createAssociatedMarkupFileCheckBox.setSelected(propertiesComponent.getBoolean(getStoreKey(CREATE_MARKUP_KEY), false));
        chooseDifferentDestinationFolderCheckBox.setSelected(propertiesComponent.getBoolean(getStoreKey(CHOOSE_DIFFERENT_DESTINATION_KEY), true));

        // if we have only 1 destination, we dont offer a different folder selection
        if (WicketFileUtil.getResourceRoots(module).length < 2) {
            chooseDifferentDestinationFolderPanel.setVisible(false);
        } else {
            createAssociatedMarkupFileCheckBox.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    chooseDifferentDestinationFolderCheckBox.setEnabled(createAssociatedMarkupFileCheckBox.isSelected());
                }
            });
            chooseDifferentDestinationFolderCheckBox.setEnabled(createAssociatedMarkupFileCheckBox.isSelected());
        }

        extendClassEditor = new ReferenceEditorComboWithBrowseButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PsiClass psiClass = getDefaultClass(project);

                TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project).createInheritanceClassChooser(
                        "Choose Class to extend", WicketSearchScope.classInModuleWithDependenciesAndLibraries(module), psiClass, null);

                chooser.showDialog();
                PsiClass aClass = chooser.getSelected();
                if (aClass != null) {
                    extendClassEditor.setText(aClass.getQualifiedName());
                }
            }
        }, "", project, true, getStoreKey(RECENT_EXTENDCLASS_KEY));
        extendsClassPanel.add(extendClassEditor);
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    protected void doOKAction() {
        String inputString = classNameTextField.getText().trim();
        String extendsClass = extendClassEditor.getText();
        boolean createMarkup = createAssociatedMarkupFileCheckBox.isSelected();
        boolean chooseDifferentDestination = chooseDifferentDestinationFolderCheckBox.isSelected();

        if (module != null && psiPackage != null && createMarkup && chooseDifferentDestination) {
            PsiDirectory directory = WicketFileUtil.selectTargetDirectory(psiPackage.getQualifiedName(), project, module);
            if (directory == null) {
                return; // aborted
            }
            markupDirectory = directory;
        }

        if (validateInput(inputString, extendsClass) && actionRunnable.run(inputString, extendsClass, createMarkup, markupDirectory)) {

            // remember last extended class and options
            RecentsManager.getInstance(project).registerRecentEntry(getStoreKey(RECENT_EXTENDCLASS_KEY), extendsClass);
            PropertiesComponent propertiesComponent = ProjectPropertiesComponent.getInstance(project);
            propertiesComponent.setValue(getStoreKey(CREATE_MARKUP_KEY), Boolean.toString(createMarkup));
            propertiesComponent.setValue(getStoreKey(CHOOSE_DIFFERENT_DESTINATION_KEY), Boolean.toString(chooseDifferentDestination));

            super.doOKAction();
        }
    }

    private boolean validateInput(@Nonnull String inputString, @Nonnull String extendsClass) {
        if (inputString.length() == 0) {
            setErrorText(IdeLocalize.errorNameShouldBeSpecified());
            return false;
        }
        if (extendsClass.length() == 0) {
            setErrorText(LocalizeValue.localizeTODO("Invalid base class"));
            return false;
        }

        clearErrorText();
        return true;
    }

    @RequiredUIAccess
    @Override
    public JComponent getPreferredFocusedComponent() {
        return classNameTextField;
    }

    @Nullable
    protected abstract PsiClass getDefaultClass(@Nonnull final Project project);

    public static interface ActionRunnable {
        boolean run(@Nonnull String inputString, @Nonnull String extendsClass, boolean hasMarkup, @Nonnull PsiDirectory markupDirectory);
    }
}
