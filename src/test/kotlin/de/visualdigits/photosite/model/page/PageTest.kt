package de.visualdigits.photosite.model.page

import de.visualdigits.photosite.model.siteconfig.navi.NaviName
import org.junit.jupiter.api.Test
import java.io.File
import java.util.Locale

class PageTest {

    @Test
    fun testConvertDescriptor() {
//        val tree = Page.readValue(File("W:/"))
        val tree = Page.readValue(File("C:/Users/sknul/.photosite/resources/pagetree"))
//        val mainTree = tree.clone { p -> !(p.name.startsWith("#") || p.name.startsWith("-")) }
//        val staticTree = tree.clone { p -> p.name.startsWith("-") }
//        println(
//            mainTree.mainNaviHtml(
//                naviName = NaviName(
//                    rootFolder = "pagetree/Arts",
//                    numberOfEntries = 10,
//                    label = Label(
//                        translations = listOf(
//                            Translation(lang = Locale.GERMAN, name = "KUNST"),
//                            Translation(lang = Locale.ENGLISH, name = "ARTS")
//                        )
//                    )
//                ),
//                language = Locale.GERMAN,
//                currentPage = mainTree.page("pagetree/Arts")!!,
//                theme = "dark"
//            )
//        )
//        println(
//            staticTree.subNaviHtml(
//                naviName = NaviName(
//                    label = Label(
//                        lang = listOf(
//                            Translation(lang = Locale.GERMAN, name = "STATISCH"),
//                            Translation(lang = Locale.ENGLISH, name = "STATIC")
//                        )
//                    )
//                ),
//                language = Locale.GERMAN,
//                currentPage = mainTree.page("pagetree")!!,
//                theme = "dark"
//            )
//        )
//        println("--------------------------------")
//        println(staticTree)
    }

}
