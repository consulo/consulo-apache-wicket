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
import consulo.language.editor.inspection.ProblemDescriptor;
import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.inspection.scheme.InspectionManager;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiFile;
import consulo.util.lang.StringUtil;
import consulo.xml.codeInspection.XmlSuppressableInspectionTool;
import consulo.xml.psi.XmlRecursiveElementVisitor;
import consulo.xml.psi.xml.XmlAttribute;
import consulo.xml.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.Nls;
import wicketforge.Constants;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@ExtensionImpl
public class EmptySrcAttributeInspection extends XmlSuppressableInspectionTool {

    @Override
    public ProblemDescriptor[] checkFile(@Nonnull PsiFile psiFile, @Nonnull InspectionManager manager, boolean b) {
        EmptySrcAttributeVisitor visitor = new EmptySrcAttributeVisitor(manager);
        psiFile.accept(visitor);
        return visitor.getProblemDescriptors();
    }

    @Override
    @Nls
    @Nonnull
    public String getDisplayName() {
        return "Wicket Empty Src Attribute Inspection";
    }

    @Override
    @Nls
    @Nonnull
    public String getGroupDisplayName() {
        return Constants.INTENSION_INSPECTION_GROUPNAME;
    }

    @Override
    @Nonnull
    public String getShortName() {
        return "WicketForgeEmptySrcAttributeInspection";
    }

    @Override
    @Nonnull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.ERROR;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    /**
     *
     */
    private static class EmptySrcAttributeVisitor extends XmlRecursiveElementVisitor {
        private List<ProblemDescriptor> problemDescriptors = new ArrayList<ProblemDescriptor>();
        private InspectionManager manager;

        public EmptySrcAttributeVisitor(InspectionManager manager) {
            this.manager = manager;
        }

        @Override
        public void visitXmlAttribute(XmlAttribute attribute) {
            super.visitXmlAttribute(attribute);

            if ("src".equals(attribute.getName())) {
                XmlAttributeValue attributeValue = attribute.getValueElement();
                if (attributeValue != null && StringUtil.isEmpty(attributeValue.getValue())) {
                    problemDescriptors.add(manager.createProblemDescriptor(attribute, "Empty src attribute will result in extra call to server",
                            (LocalQuickFix) null, ProblemHighlightType.GENERIC_ERROR, true));
                }
            }
        }

        public ProblemDescriptor[] getProblemDescriptors() {
            return problemDescriptors.toArray(new ProblemDescriptor[problemDescriptors.size()]);
        }
    }
}
