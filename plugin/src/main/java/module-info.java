/**
 * @author VISTALL
 * @since 08/05/2023
 */
open module consulo.apache.wicket {
    requires consulo.application.api;
    requires consulo.application.content.api;
    requires consulo.code.editor.api;
    requires consulo.color.scheme.api;
    requires consulo.component.api;
    requires consulo.disposer.api;
    requires consulo.document.api;
    requires consulo.file.editor.api;
    requires consulo.file.template.api;
    requires consulo.ide.api;
    requires consulo.ide.impl;
    requires consulo.index.io;
    requires consulo.language.api;
    requires consulo.language.editor.api;
    requires consulo.language.editor.refactoring.api;
    requires consulo.language.editor.ui.api;
    requires consulo.language.impl;
    requires consulo.localize.api;
    requires consulo.logging.api;
    requires consulo.module.api;
    requires consulo.module.content.api;
    requires consulo.navigation.api;
    requires consulo.project.api;
    requires consulo.project.content.api;
    requires consulo.ui.api;
    requires consulo.ui.ex.api;
    requires consulo.ui.ex.awt.api;
    requires consulo.undo.redo.api;
    requires consulo.usage.api;
    requires consulo.util.collection;
    requires consulo.util.lang;
    requires consulo.util.xml.fast.reader;
    requires consulo.virtual.file.system.api;

    requires consulo.java;
    requires com.intellij.xml.api;
    requires com.intellij.xml.editor.api;
    requires com.intellij.xml.html.api;
    requires com.intellij.properties;

    requires forms.rt;

    // TODO remove in future
    requires java.desktop;
}
