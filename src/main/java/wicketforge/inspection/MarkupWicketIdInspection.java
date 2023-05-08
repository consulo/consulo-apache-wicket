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
package wicketforge.inspection;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiReference;
import consulo.xml.codeInspection.XmlSuppressableInspectionTool;
import consulo.xml.psi.XmlElementVisitor;
import consulo.xml.psi.xml.XmlAttribute;
import consulo.xml.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import wicketforge.Constants;
import wicketforge.psi.references.MarkupWicketIdReference;

import javax.annotation.Nonnull;

@ExtensionImpl
public class MarkupWicketIdInspection extends XmlSuppressableInspectionTool {
    @Nonnull
    @Override
    public PsiElementVisitor buildVisitor(@Nonnull final ProblemsHolder holder, boolean isOnTheFly) {
        return new XmlElementVisitor() {
            @Override
            public void visitXmlAttribute(XmlAttribute attribute) {
                super.visitXmlAttribute(attribute);
                if (Constants.WICKET_ID.equals(attribute.getName())) {
                    XmlAttributeValue attributeValue = attribute.getValueElement();
                    if (attributeValue != null && attributeValue.getTextLength() > 0) {
                        for (PsiReference reference : attributeValue.getReferences()) {
                            if (reference instanceof MarkupWicketIdReference && ((MarkupWicketIdReference) reference).multiResolve(false).length == 0) {
                                holder.registerProblem(holder.getManager().createProblemDescriptor(attributeValue, "Wicket id reference problem",
                                        (LocalQuickFix) null, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, true));
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    @Nls
    @Nonnull
    public String getGroupDisplayName() {
        return Constants.INTENSION_INSPECTION_GROUPNAME;
    }

    @Override
    @Nls
    @Nonnull
    public String getDisplayName() {
        return "Wicket HTML ID Inspection";
    }

    @Override
    @NonNls
    @Nonnull
    public String getShortName() {
        return "WicketForgeHtmlIdInspection";
    }

    @Override
    @Nonnull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
