/*
 * Copyright 2013 The WicketForge-Team
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
package wicketforge.search;

import com.intellij.java.language.psi.PsiClass;
import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.lang.properties.PropertiesUtil;
import com.intellij.lang.properties.psi.PropertiesFile;
import consulo.annotation.component.ExtensionImpl;
import consulo.index.io.ID;
import consulo.language.psi.PsiFile;
import consulo.language.psi.stub.FileContent;
import consulo.project.Project;
import consulo.util.xml.fastReader.NanoXmlUtil;
import consulo.util.xml.fastReader.XmlFileHeader;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.xml.ide.highlighter.XmlFileType;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@ExtensionImpl
public class PropertiesIndex extends WicketResourceIndexExtension {
    private static final ID<String, Void> NAME = ID.create("WicketPropertiesIndex");

    @Nonnull
    @Override
    public ID<String, Void> getName() {
        return NAME;
    }

    @Override
    public boolean acceptInput(Project project, VirtualFile file) {
        FileType fileType = file.getFileType();
        return PropertiesFileType.INSTANCE.equals(fileType) || XmlFileType.INSTANCE.equals(fileType);
    }

    @Nonnull
    @Override
    public Map<String, Void> map(FileContent inputData) {
        if (XmlFileType.INSTANCE.equals(inputData.getFileType())) {
            // check if its a properties xml
            try (InputStream stream = inputData.getFile().getInputStream()) {
                XmlFileHeader fileHeader = NanoXmlUtil.parseHeaderWithException(stream);
                if (!"properties".equals(fileHeader.getRootTagLocalName())) {
                    return Collections.emptyMap(); // if not, nothing to map here
                }
            }
            catch (Exception ignored) {
            }
        }
        return super.map(inputData);
    }

    /**
     * Returns all properties files for the passed PsiClass.
     *
     * @param psiClass PsiClass
     * @return all properties or empty array if no such file exists.
     */
    @Nonnull
    public static PsiFile[] getAllFiles(@Nonnull final PsiClass psiClass) {
        return getFilesByClass(NAME, psiClass, true);
    }

    /**
     * Returns the base properties file for the passed PsiClass.
     *
     * @param psiClass PsiClass
     * @return the base properties or null if no such file exists.
     */
    @Nullable
    public static PropertiesFile getBaseFile(@Nonnull final PsiClass psiClass) {
        PsiFile[] files = getFilesByClass(NAME, psiClass, false);
        return files.length > 0 ? PropertiesUtil.getPropertiesFile(files[0]) : null;
    }
}
