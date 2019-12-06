// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.Iterator;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import java.util.Arrays;
import java.io.FileFilter;
import java.io.File;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;

public class ProtoMojo extends AbstractMojo
{
    protected MavenProject project;
    private File sourceDirectory;
    private File outputDirectory;
    private String type;
    
    public void execute() throws MojoExecutionException {
        final File[] files = this.sourceDirectory.listFiles(new FileFilter() {
            public boolean accept(final File pathname) {
                return pathname.getName().endsWith(".proto");
            }
        });
        if (files == null || files.length == 0) {
            this.getLog().warn((CharSequence)("No proto files found in directory: " + this.sourceDirectory.getPath()));
            return;
        }
        final List<File> recFiles = Arrays.asList(files);
        for (final File file : recFiles) {
            try {
                this.getLog().info((CharSequence)("Compiling: " + file.getPath()));
                if ("default".equals(this.type)) {
                    final JavaGenerator generator = new JavaGenerator();
                    generator.setOut(this.outputDirectory);
                    generator.compile(file);
                }
                else {
                    if (!"alt".equals(this.type)) {
                        continue;
                    }
                    final AltJavaGenerator generator2 = new AltJavaGenerator();
                    generator2.setOut(this.outputDirectory);
                    generator2.compile(file);
                }
            }
            catch (CompilerException e) {
                this.getLog().error((CharSequence)"Protocol Buffer Compiler failed with the following error(s):");
                for (final String error : e.getErrors()) {
                    this.getLog().error((CharSequence)"");
                    this.getLog().error((CharSequence)error);
                }
                this.getLog().error((CharSequence)"");
                throw new MojoExecutionException("Compile failed.  For more details see error messages listed above.", (Exception)e);
            }
        }
        this.project.addCompileSourceRoot(this.outputDirectory.getAbsolutePath());
    }
}
