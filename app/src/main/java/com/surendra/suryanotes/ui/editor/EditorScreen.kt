package com.surendra.suryanotes.ui.editor

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    val titleFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    var isInitialFocusDone by remember(isLandscape) { mutableStateOf(false) }

    var isTitleFocused by remember { mutableStateOf(false) }
    var isContentFocused by remember { mutableStateOf(false) }

    var lastFocusedField by rememberSaveable { mutableStateOf<String?>(null) }

    var contentTextLayout by remember { mutableStateOf<TextLayoutResult?>(null) }

    LaunchedEffect(uiState.isLoading, isLandscape) {
        if (uiState.isLoading) return@LaunchedEffect
        if (isInitialFocusDone) return@LaunchedEffect
        isInitialFocusDone = true

        if (lastFocusedField != null) {
            when (lastFocusedField) {
                "title" -> titleFocusRequester.requestFocus()
                "content" -> contentFocusRequester.requestFocus()
            }
            return@LaunchedEffect
        }

        if (isLandscape) return@LaunchedEffect

        when {
            uiState.noteId == null -> titleFocusRequester.requestFocus()
            uiState.title.text.isBlank() -> titleFocusRequester.requestFocus()
            else -> contentFocusRequester.requestFocus()
        }
    }

    val density = LocalDensity.current
    val imeBottomPx = WindowInsets.ime.getBottom(density)

    var columnHeightPx by remember { mutableStateOf(0) }
    var titleHeightPx by remember { mutableStateOf(0) }
    val spacingPx = with(density) { 12.dp.toPx() }
    val hysteresisPx = with(density) { 8.dp.toPx() }

    var showTitle by remember { mutableStateOf(true) }

    val forceShowTitle = !isLandscape || isTitleFocused || !isContentFocused

    LaunchedEffect(
        forceShowTitle,
        contentTextLayout,
        uiState.content.selection,
        titleHeightPx,
        columnHeightPx,
        imeBottomPx
    ) {
        if (forceShowTitle) {
            showTitle = true
            return@LaunchedEffect
        }
        val layout = contentTextLayout
        if (layout == null || columnHeightPx <= 0 || imeBottomPx <= 0) {
            showTitle = true
            return@LaunchedEffect
        }

        val cursorOffset = uiState.content.selection.end
            .coerceIn(0, layout.layoutInput.text.length)
        val cursorLine = layout.getLineForOffset(cursorOffset)

        if (cursorLine == 0) {
            showTitle = true
            return@LaunchedEffect
        }

        val cursorBottomPx = layout.getCursorRect(cursorOffset).bottom
        val availableHeightWithTitle = columnHeightPx - titleHeightPx - spacingPx

        showTitle = when {
            cursorBottomPx < availableHeightWithTitle - hysteresisPx -> true
            cursorBottomPx > availableHeightWithTitle + hysteresisPx -> false
            else -> showTitle
        }
    }

    var contentFieldHeightPx by remember { mutableStateOf(0) }
    val imeBottomPxState = rememberUpdatedState(imeBottomPx)

    data class CaretTrigger(
        val selectionEnd: Int,
        val layout: TextLayoutResult?,
        val imeBottomPx: Int,
        val contentFieldHeightPx: Int
    )

    LaunchedEffect(isContentFocused) {
        if (!isContentFocused) return@LaunchedEffect
        snapshotFlow {
            CaretTrigger(
                uiState.content.selection.end,
                contentTextLayout,
                imeBottomPxState.value,
                contentFieldHeightPx
            )
        }.collectLatest { trigger ->
            if (trigger.imeBottomPx <= 0) return@collectLatest
            val layout = trigger.layout ?: return@collectLatest
            val cursorOffset = trigger.selectionEnd
                .coerceIn(0, layout.layoutInput.text.length)
            val cursorRect = layout.getCursorRect(cursorOffset)
            bringIntoViewRequester.bringIntoView(cursorRect)
        }
    }

    Scaffold(
        topBar = {
            if (!isLandscape) {
                TopAppBar(title = { Text("Note") })
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .imePadding()
                .onGloballyPositioned { columnHeightPx = it.size.height }
        ) {
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(animationSpec = tween(200)) + expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
                exit = fadeOut(animationSpec = tween(150)) + shrinkVertically()
            ) {
                BasicTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .focusRequester(titleFocusRequester)
                        .onGloballyPositioned { titleHeightPx = it.size.height }
                        .onFocusChanged {
                            isTitleFocused = it.isFocused
                            if (it.isFocused) lastFocusedField = "title"
                        },
                    decorationBox = { innerTextField ->
                        Box {
                            if (uiState.title.text.isEmpty()) {
                                Text(
                                    text = "Title",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            BasicTextField(
                value = uiState.content,
                onValueChange = viewModel::onContentChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                onTextLayout = { contentTextLayout = it },
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .focusRequester(contentFocusRequester)
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .onGloballyPositioned { contentFieldHeightPx = it.size.height }
                    .onFocusChanged {
                        isContentFocused = it.isFocused
                        if (it.isFocused) lastFocusedField = "content"
                    },
                decorationBox = { innerTextField ->
                    Box {
                        if (uiState.content.text.isEmpty()) {
                            Text(
                                text = "Start writing your note...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}