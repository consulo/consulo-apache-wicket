/**
 * @author VISTALL
 * @since 08/05/2023
 */
open module consulo.apache.wicket
{
    requires consulo.ide.api;

    requires consulo.java;
    requires com.intellij.xml;
    requires com.intellij.properties;
    requires consulo.util.xml.fast.reader;

    requires forms.rt;

    // TODO remove in future
    requires java.desktop;
    requires consulo.ide.impl;
}