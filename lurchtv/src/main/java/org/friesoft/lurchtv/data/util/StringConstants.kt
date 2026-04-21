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
        object ContentDescription {
            fun videoPoster(videoName: String) = "Video poster of $videoName"
            fun image(imageName: String) = "image of $imageName"
            const val VideosCarousel = "Videos Carousel"
            const val UserAvatar = "User Profile Button"
            const val DashboardSearchButton = "Dashboard Search Button"
            const val BrandLogoImage = "Brand Logo Image"
            const val FilterSelected = "Filter Selected"
            fun reviewerName(name: String) = "$name's logo"
        }

        const val CategoryDetailsFailureSubject = "category details"
        const val VideosFailureSubject = "videos"
        const val VideoDetailsFailureSubject = "video details"
        const val HomeScreenRecentTitle = "Recent"
        const val PopularFilmsThisWeekTitle = "Popular films this week"
        const val BingeWatchDramasTitle = "Bingewatch dramas"
        fun videoDetailsScreenSimilarTo(name: String) = "Similar to $name"
        fun reviewCount(count: String) = "$count reviews"

        object Placeholders {
            const val AboutSectionTitle = "About LurchTV"
            const val AboutSectionDescription = "Welcome to LurchTV! We are a new and" +
                " exciting streaming platform that offers a vast selection of videos," +
                " TV shows, and original content for you to enjoy. Our team is dedicated" +
                " to providing an intuitive and seamless streaming experience for all" +
                " users. With a simple and intuitive interface, you can easily find and" +
                " watch your favourite content in just a few clicks. We are constantly" +
                " updating and expanding our library, so there is always something new" +
                " to discover. We also offer personalised recommendations based on your" +
                " viewing history, so you can easily find new and exciting content to" +
                " enjoy. Thank you for choosing LurchTV for all of your entertainment" +
                " needs. We hope you have a great time streaming!"
            const val AboutSectionAppVersionTitle = "Application Version"
            const val LanguageSectionTitle = "Language"
            val LanguageSectionItems = listOf(
                "Deutsch",
                "English"
            )
            const val HelpAndSupportSectionTitle = "Help and Support"
            const val HelpAndSupportSectionListItemIconDescription = "select section"
            const val HelpAndSupportSectionContactItem = "Contact us"
            const val HelpAndSupportSectionContactValue = "friesoft@gmail.com"
            const val HelpAndSupportSectionGitHubItem = "GitHub Issue Tracker"
            const val HelpAndSupportSectionGitHubValue = "https://github.com/friesoft/lurchtv"
            const val HelpAndSupportSectionHomepageItem = "Homepage"
            const val HelpAndSupportSectionHomepageValue = "https://friesoft.org"
        }

        const val VideoPlayerControlPlaylistButton = "Playlist Button"
        const val VideoPlayerControlClosedCaptionsButton = "Playlist Button"
        const val VideoPlayerControlSettingsButton = "Playlist Button"
        const val VideoPlayerControlPlayPauseButton = "Playlist Button"
        const val VideoPlayerControlForward = "Fast forward 10 seconds"
        const val VideoPlayerControlSkipNextButton = "Skip to the next video"
        const val VideoPlayerControlSkipPreviousButton = "Skip to the previous video"
        const val VideoPlayerControlRepeatAll = "Repeat all videos"
        const val VideoPlayerControlRepeatOne = "Repeat video"
        const val VideoPlayerControlRepeatNone = "No repeat"
        const val VideoPlayerControlRepeatButton = "Repeat Button"
    }
}
