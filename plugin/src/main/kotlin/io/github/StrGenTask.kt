package io.github

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jdom2.input.SAXBuilder
import java.io.File

abstract class StrGenTask : DefaultTask() {
    @get:Input
    abstract val applicationId: Property<String>

    @get:InputFile
    abstract val stringsXml: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val document = SAXBuilder().build(stringsXml.get().asFile)
        val elements = document.rootElement
            .children
            .filter { it.name == "string" }

        val resources = elements.mapNotNull {
            val name = it.getAttributeValue("name") ?: return@mapNotNull null
            val parts = name.split("_")
            val propertyName = buildString {
                parts.forEachIndexed { index, part ->
                    append(part.replaceFirstChar { if (index == 0) it.toString() else it.titlecase() })
                }
            }
            StringResource(propertyName, "R.string.$name")
        }

        val code = generateCode(resources)

        val outputPath = outputDir.get()
            .dir(applicationId.get().replace(".", File.separator))
            .asFile
            .also { it.mkdirs() }
        val output = File(outputPath, "StrGen.kt")
        output.writeText(code)
        output.setReadOnly()
        println("generated: ${output.absolutePath}")
    }

    private fun generateCode(resources: List<StringResource>): String {
        return buildString {
            appendLine("package ${applicationId.get()}")
            appendLine()
            appendLine("import android.content.Context")
            appendLine()
            appendLine("object StrGen {")
            appendLine("    class StringResource(private val resId: Int) {")
            appendLine("       fun get(context: Context): String = context.getString(resId)")
            appendLine("    }")
            resources.forEach {
                appendLine("    val ${it.id} = StringResource(${it.name})")
            }
            appendLine("}")
        }
    }
}

private data class StringResource(
    val id: String,
    val name: String
)