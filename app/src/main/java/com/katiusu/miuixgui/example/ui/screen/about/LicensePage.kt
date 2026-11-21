package com.katiusu.miuixgui.example.ui.screen.about

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.katiusu.miuixgui.example.R
import com.katiusu.miuixgui.example.ui.component.AppPageScaffold
import com.katiusu.miuixgui.example.ui.component.AppSectionCard
import com.katiusu.miuixgui.example.ui.util.pageScrollModifiers
import com.katiusu.miuixgui.example.ui.util.pageStartInset
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import com.katiusu.miuixgui.example.ui.component.AppArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.basic.Text as MiuixText

/** 单个第三方库信息。 */
data class LibraryInfo(
    val name: String,
    val version: String,
    val license: String,
    val website: String,
)

/** 第三方库分组。 */
private data class LicenseSection(
    val titleRes: Int,
    val libraries: List<LibraryInfo>,
)

private val licenseSections: List<LicenseSection> = listOf(
    LicenseSection(
        titleRes = R.string.licenses_section_core,
        libraries = listOf(
            LibraryInfo("Kotlin", "2.4.20", "Apache-2.0", "https://kotlinlang.org/"),
            LibraryInfo(
                "Kotlin Coroutines",
                "-",
                "Apache-2.0",
                "https://github.com/Kotlin/kotlinx.coroutines",
            ),
        ),
    ),
    LicenseSection(
        titleRes = R.string.licenses_section_ui,
        libraries = listOf(
            LibraryInfo(
                "Miuix (compose-miuix-ui)",
                "0.9.5-beta02",
                "Apache-2.0",
                "https://github.com/compose-miuix-ui/miuix",
            ),
            LibraryInfo(
                "Jetpack Compose",
                "BOM 2026.09.00",
                "Apache-2.0",
                "https://developer.android.com/jetpack/compose",
            ),
            LibraryInfo(
                "Compose Material3",
                "BOM 2026.09.00",
                "Apache-2.0",
                "https://developer.android.com/jetpack/androidx/releases/compose-material3",
            ),
            LibraryInfo(
                "Compose Material Icons Core",
                "1.7.8",
                "Apache-2.0",
                "https://developer.android.com/jetpack/androidx/releases/compose-material",
            ),
            LibraryInfo(
                "commonmark-java",
                "0.30.0",
                "BSD-2-Clause",
                "https://github.com/commonmark/commonmark-java",
            ),
        ),
    ),
    LicenseSection(
        titleRes = R.string.licenses_section_androidx,
        libraries = listOf(
            LibraryInfo(
                "AndroidX Core KTX",
                "1.19.0",
                "Apache-2.0",
                "https://developer.android.com/jetpack/androidx/releases/core",
            ),
            LibraryInfo(
                "AndroidX Activity Compose",
                "1.13.0",
                "Apache-2.0",
                "https://developer.android.com/jetpack/androidx/releases/activity",
            ),
            LibraryInfo(
                "AndroidX Lifecycle Runtime KTX",
                "2.11.0",
                "Apache-2.0",
                "https://developer.android.com/jetpack/androidx/releases/lifecycle",
            ),
        ),
    ),
)

@Composable
fun LicensePageContent(
    onBack: () -> Unit,
    isBlurEnabled: Boolean = true,
) {
    val topAppBarScrollBehavior = MiuixScrollBehavior()

    AppPageScaffold(
        title = stringResource(R.string.third_party_licenses_title),
        isBlurEnabled = isBlurEnabled,
        scrollBehavior = topAppBarScrollBehavior,
        onBack = onBack,
    ) { innerPadding ->
        val uriHandler = LocalUriHandler.current
        val lazyListState = rememberLazyListState()
        val layoutDirection = LocalLayoutDirection.current
        val cutoutPadding = WindowInsets.displayCutout.asPaddingValues()
        val contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding(),
            start = innerPadding.calculateStartPadding(layoutDirection) + cutoutPadding.calculateLeftPadding(LayoutDirection.Ltr),
            end = innerPadding.calculateEndPadding(layoutDirection) + cutoutPadding.calculateRightPadding(LayoutDirection.Ltr),
        )

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .pageStartInset()
                .pageScrollModifiers(
                    showTopAppBar = true,
                    topAppBarScrollBehavior = topAppBarScrollBehavior,
                )
                .padding(
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection),
                ),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding(),
            ),
        ) {
            item {
                MiuixText(
                    text = stringResource(R.string.licenses_header),
                    color = colorScheme.onSurfaceVariantSummary,
                    style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.footnote2,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 12.dp),
                )
            }

            licenseSections.forEach { section ->
                item(key = "section_${section.titleRes}") {
                    SmallTitle(text = stringResource(section.titleRes))
                }
                items(
                    items = section.libraries,
                    key = { "${section.titleRes}_${it.name}" },
                ) { library ->
                    AppSectionCard {
                        AppArrowPreference(
                            title = library.name,
                            summary = buildString {
                                if (library.version.isNotBlank() && library.version != "-") {
                                    append("v").append(library.version).append(" · ")
                                }
                                append(library.license)
                            },
                            onClick = {
                                uriHandler.openUri(library.website)
                            },
                        )
                    }
                }
            }

        }
    }
}
