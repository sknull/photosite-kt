package de.visualdigits.photosite.model.utils

import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.writeText

class CreateNewImageFolderTest {

    /**
     * Copy the source directory
     * to the target directory assuming
     * each source folder has a subfolder with a certain name
     * i.e. 'jpg_1200' which contains the fotos to copy.
     * Also creates a basic page.json
     */
    @Test
    fun createNewImageFolder() {
        val imageDirectoryName = "jpg_1200"
//        val sourceDirectory = File("H:/Fotos_processed/2020-2029/2024/2024-09_Urlaub Hvar")
//        val targetDirectory = File("W:/resources/pagetree/Fotos/Unterwegs/Kroatien/Hvar/2024")
        val sourceDirectory = File("H:/Fotos_processed/2020-2029/2024/2024-09_Urlaub Hvar")
        val targetDirectory = File("W:/resources/pagetree/Fotos/Unterwegs/Kroatien/Hvar/2024")
//        val sourceDirectory = File("H:/Fotos_processed/2020-2029/2022/2022-09_Teneriffa")
//        val targetDirectory = File("W:/resources/pagetree/Fotos/Unterwegs/Spanien/Teneriffa/2022")
//        val sourceDirectory = File("H:/Fotos_processed/2020-2029/2023/2023-09_Lanzarote")
//        val targetDirectory = File("W:/resources/pagetree/Fotos/Unterwegs/Spanien/Lanzarote/2023")

        visit(
            sourceDirectory = sourceDirectory,
            targetDirectory = targetDirectory,
            imageDirectoryName = imageDirectoryName,
            dryRun = false
        ) { f ->
            !listOf("jpg_1200", "jpg_orig", "Video", "Videos Final").contains(f.name)
        }
    }

    fun visit(
        sourceDirectory: File,
        targetDirectory: File,
        imageDirectoryName: String,
        directory: File = sourceDirectory,
        level: Int = 0,
        dryRun: Boolean = true,
        subDirectoryFilter: (f: File) -> Boolean
    ) {
        val indent = "  ".repeat(level)
        println("$indent${directory.name}")

        val subDirectories = directory.listFiles { f -> f.isDirectory }
        val imageFiles = File(directory, imageDirectoryName).listFiles { f -> f.isFile && f.extension.lowercase() == "jpg" }
        val relSourcePath = sourceDirectory.toPath().relativize(directory.toPath()).toFile()
        val targetPath = relSourcePath.path
            .split("\\")
            .joinToString("\\") { part ->
                if (part.contains("_")) part.split("_").drop(1).joinToString("_") else part
            }
        val finalTargetPath = targetDirectory.resolve(targetPath)

        if (subDirectories?.any { f-> f.name == imageDirectoryName } == true) {
            processImages(imageFiles, dryRun, finalTargetPath, indent)
        } else {
            val imageFiles = directory.listFiles { f -> f.isFile && f.extension.lowercase() == "jpg" }
            if (imageFiles != null && imageFiles.isNotEmpty()) {
                processImages(imageFiles, dryRun, finalTargetPath, indent)
            } else {
                if (!dryRun) {
                    finalTargetPath.mkdirs()
                    File(finalTargetPath, "page.json").writeText("{\n" +
                            "  \"content\" : { },\n" +
                            "  \"name\" : \"${finalTargetPath.name}\"\n" +
                            "}")
                }
            }
        }

        val filter = subDirectories
            ?.filter(subDirectoryFilter)
        filter
            ?.forEach { d ->
                visit(sourceDirectory, targetDirectory, imageDirectoryName, d, level + 1, dryRun, subDirectoryFilter)
            }
    }

    private fun processImages(
        imageFiles: Array<out File>?,
        dryRun: Boolean,
        finalTargetPath: File,
        indent: String
    ) {
        if (imageFiles != null && imageFiles.isNotEmpty()) {
            if (!dryRun) {
                finalTargetPath.mkdirs()
            } else {
                println("${indent}Creating directory: $finalTargetPath")
            }
            imageFiles.forEach { f ->
                println("$indent- ${f.name}")
                val targetFile = File(finalTargetPath, f.name)
                if (!dryRun) {
                    if (!targetFile.exists()) {
                        f.copyTo(targetFile)
                    }
                }
            }
            if (!dryRun) {
                File(finalTargetPath, "page.json").writeText(
                    "{\n" +
                            "  \"content\" : {\n" +
                            "    \"contentType\" : \"LightGallery\"\n" +
                            "  },\n" +
                            "  \"name\" : \"${finalTargetPath.name}\"\n" +
                            "}"
                )
            } else {
                println("${indent}Creating directory: $finalTargetPath")
            }
        }
    }

}
