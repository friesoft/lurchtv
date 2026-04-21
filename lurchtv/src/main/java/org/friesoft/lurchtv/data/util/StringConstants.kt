package org.friesoft.lurchtv.data.util

object StringConstants {

    object Assets {
        const val VideoCategories = "videoCategories.json"
    }

    object Exceptions {
        const val UnknownException = "Unknown Exception!"
        const val InvalidCategoryId = "Invalid category ID!"
    }

    object Composable {
        const val CategoryDetailsFailureSubject = "category details"
        const val VideosFailureSubject = "videos"
        const val VideoDetailsFailureSubject = "video details"
        fun reviewCount(count: String) = "$count reviews"

        object Placeholders {
            val LanguageSectionItems = listOf(
                "Deutsch",
                "English"
            )
            const val HelpAndSupportSectionContactValue = "friesoft@gmail.com"
            const val HelpAndSupportSectionGitHubValue = "https://github.com/friesoft/lurchtv"
            const val HelpAndSupportSectionHomepageValue = "https://friesoft.org"
        }
    }
}
