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

import com.intellij.java.analysis.impl.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.java.language.psi.JavaElementVisitor;
import com.intellij.java.language.psi.PsiExpression;
import com.intellij.java.language.psi.PsiLiteralExpression;
import com.intellij.java.language.psi.PsiReferenceExpression;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.inspection.LocalInspectionToolSession;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiReference;
import org.jetbrains.annotations.Nls;
import wicketforge.Constants;
import wicketforge.psi.references.ClassWicketIdReference;

import javax.annotation.Nonnull;

@ExtensionImpl
public class ClassWicketIdInspection extends BaseJavaLocalInspectionTool {
    @Nonnull
    @Override
    public PsiElementVisitor buildVisitorImpl(@Nonnull ProblemsHolder holder, boolean isOnTheFly, LocalInspectionToolSession session, Object o) {
        return new JavaElementVisitor() {
            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
            }

            @Override
            public void visitExpression(PsiExpression expression) {
                super.visitExpression(expression);
                // ClassWicketIdReference are only available on PsiLiteralExpression
                if (!(expression instanceof PsiLiteralExpression)) {
                    return;
                }
                for (PsiReference reference : expression.getReferences()) {
                    if (reference instanceof ClassWicketIdReference && reference.resolve() == null) {
                        holder.registerProblem(holder.getManager().createProblemDescriptor(expression, "Wicket id reference problem",
                                (LocalQuickFix) null, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, true));
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
        return "Wicket Java ID Inspection";
    }

    @Override
    @Nonnull
    public String getShortName() {
        return "WicketForgeJavaIdInspection";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
