package io.github

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

class StrGenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.afterEvaluate {
            val android = project.extensions.getByType<ApplicationExtension>()
            val appId = android.defaultConfig.applicationId
            val outputPath = "generated/source/strGen"
            val task = project.tasks.register<StrGenTask>("generateStrGen") {
                group = "generate StrGen task"
                applicationId.set(appId)
                stringsXml.set(project.layout.projectDirectory.file("src/main/res/values/strings.xml"))
                outputDir.set(project.layout.buildDirectory.dir(outputPath))
            }
            project.tasks.named("preBuild").configure {
                dependsOn(task)
            }
            android.sourceSets["main"].java.srcDir("build/$outputPath")
        }
    }
}
