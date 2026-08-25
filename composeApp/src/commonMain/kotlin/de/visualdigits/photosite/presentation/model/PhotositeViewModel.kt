package de.visualdigits.photosite.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.common.domain.model.errorhandling.onError
import de.visualdigits.common.domain.model.errorhandling.onSuccess
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.common.presentation.components.applyAppLanguage
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.error_local_wrong_filetype
import de.visualdigits.compose.resources.tab_products
import de.visualdigits.compose.resources.title_settings
import de.visualdigits.generated.AppVersion
import de.visualdigits.photosite.data.datasource.PageDatasource
import de.visualdigits.photosite.domain.model.errorhandling.toUiText
import de.visualdigits.photosite.domain.model.type.Language
import de.visualdigits.photosite.domain.repository.SettingsRepository
import de.visualdigits.photosite.presentation.model.settings.SK
import de.visualdigits.photosite.presentation.model.settings.SettingsForm
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.Sink
import kotlinx.io.Source

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class PhotositeViewModel(
    private val settingsRepository: SettingsRepository,
    private val pageDatasource: PageDatasource
) : ViewModel() {

    val now = KmpOffsetDateTime.fromString("2026-08-15T00:00:00.000000000+02:00") // just for the demo

    val scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>> = mutableMapOf()

    private val _state = MutableStateFlow(PhotositeState())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _settings = MutableStateFlow<SettingsForm?>(null)
    val settings = _settings.asStateFlow()

    private val _editedSettings = MutableStateFlow<SettingsForm?>(null)
    val editedSettings = _editedSettings.asStateFlow()

    init {
        Logger.i("Application version ${AppVersion().version} initializing...")
        loadSettings()
        Logger.i("Application started")

        onAction(PhotositeAction.OnInitializeTabs(
            tabLabels = listOf(
                "tab_photos" to UiText.StringResourceId(Res.string.tab_products),
                "tab_settings" to UiText.StringResourceId(Res.string.title_settings)
            )
        ))

        applyAppLanguage(Language.EN.localeCode)
    }

    fun onCommonAction(action: CommonAction) {
        when (action) {
            is CommonAction.OnScrollPositionChange -> {
                action.id?.also { id ->
                    scrollPosition[id] = Triple(action.position, action.offset, action.scrollIntent)
                }
            }
        }
    }

    fun onAction(action: PhotositeAction) {
        when (action) {

            //
            // Tabs
            //
            is PhotositeAction.OnInitializeTabs -> {
                _state.update {
                    val tabLabelKeys = action.tabLabels.map { tl -> tl.first }
                    val previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex
                    it.copy(
                        tabLabels = action.tabLabels,
                        tabLabelKeys = tabLabelKeys,
                        previousSelectedTabIndexes = previousSelectedTabIndexes,
                        selectedTabIndex = 0,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
            is PhotositeAction.OnTabSelected -> {
                when (state.value.tabLabels[action.index].first) {
                    "tab_photos" -> {
                    }
                    "tab_settings" -> {
                        _editedSettings.update { _settings.value }
                    }
                }
                _state.update {
                    it.copy(
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = action.index,
                        uiMessage = null,
                        uiMessageSeverity = null,
                    )
                }
            }

            //
            // Pages
            //
            is PhotositeAction.OnPageClicked -> {
                loadPage(action.path)
            }

            //
            // Images
            //
            is PhotositeAction.OnImageClicked -> {
                _state.update {
                    it.copy(
                        imageFile = action.imageFile,
                    )
                }
            }
            is PhotositeAction.OnReturnClicked -> {
                _state.update {
                    it.copy(
                        imageFile = null,
                    )
                }
            }

            //
            // Navigation
            //
            is PhotositeAction.OnPageCollapsibleStateChange -> {
                loadPage(action.page.path)
                val stayInGroup = !action.isExpanded &&
                        state.value.page != null &&
                        state.value.previousPage == state.value.page
                if (action.isExpanded && (action.page.parent != null)) {
                    scrollPosition["pages"] = Triple(0,0, ScrollIntent.scrollToStart)
                } else {
                    scrollPosition["pages"] = Triple(0,0, ScrollIntent.standard)
                }
                _state.update {
                    // keep collapsible box open when user switches from single feed to group
                    val newCollapsibleState = if (
                        action.page.children.isNotEmpty()
                    ) {
                        it.collapsibleState + if (stayInGroup) {
                            ("group_${action.page.path.replace("/", "_")}" to true)
                        } else {
                            ("group_${action.page.path.replace("/", "_")}" to action.isExpanded)
                        }
                    } else {
                        it.collapsibleState
                    }
                    it.copy(
                        previousPage = it.page,
                        collapsibleState = newCollapsibleState
                    )
                }
            }

            //
            // Settings
            //
            is PhotositeAction.OnSettingsValueChanged -> {
                _editedSettings.update { current ->
                    current?.copy(
                        key = action.keyValue.descriptor.key as SK,
                        value = action.keyValue.value
                    )
                }
            }
            is PhotositeAction.OnEditSettingsCancelClick -> {
                _settings.value?.get<Language>(SK.language)
                    ?.also { l ->
                        applyAppLanguage(l.localeCode)
                    }
                _editedSettings.update { null }
                _state.update {
                    it.copy(
                        isEditingSettings = false,
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = 0,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
            is PhotositeAction.OnSaveSettingsClick -> {
                saveSettings(_editedSettings.value)
            }
            is PhotositeAction.OnSettingsImport -> {
                importSettings(action.fileName, action.source)
            }
            is PhotositeAction.OnSettingsExport -> {
                exportSettings(action.fileName, action.sink)
            }
        }
    }

    private fun loadPage(path: String) {
        val hostUrl = settings.value?.get<String>(SK.hostUrl)
        _isLoading.update { true }
        viewModelScope.launch {
            pageDatasource.getPage(hostUrl, path)
                .onSuccess { page ->
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            page = page,
                            uiMessage = null,
                            uiMessageSeverity = null
                        )
                    }
                }
                .onError { error, throwable ->
                    Logger.i("Could not fetch page host", throwable)
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        }
    }

    //
    // Pages
    //
    private fun loadPages() {
        val hostUrl = settings.value?.get<String>(SK.hostUrl)
        _isLoading.update { true }
        viewModelScope.launch {
            pageDatasource.getNavigationTree(hostUrl)
                .onSuccess { pageTree ->
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            rootPage = pageTree.values.firstOrNull(),
                            pageMap = pageTree,
                            uiMessage = null,
                            uiMessageSeverity = null
                        )
                    }
                }
                .onError { error, throwable ->
                    Logger.i("Could not fetch pages from host", throwable)
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        }
    }

    //
    // Settings
    //
    private fun loadSettings() = viewModelScope.launch {
        _isLoading.update { true }
        val result = settingsRepository.getSettings()
        if (result is Result.Success) {
            val settings = result.data
            val finalSettings = if (settings != null) {
                settings
            } else {
                val newSettings = SettingsForm(mapOf(
                    SK.language to Language.EN
                ))
                settingsRepository.setSettings(newSettings)
                    .onError { _, throwable ->
                        Logger.e("Could not safe initial settings", throwable)
                    }
                newSettings
            }

            applyAppLanguage(finalSettings.get<Language>(SK.language)?.localeCode?: Language.EN.localeCode)
            _settings.update { finalSettings }
            loadPages()
            _isLoading.update { false }
            _state.update {
                it.copy(
                    uiMessage = null,
                    uiMessageSeverity = null
                )
            }
        } else if (result is Result.Error) {
            Logger.e("Could not load data", result.throwable)
            _isLoading.update { false }
            _state.update {
                it.copy(
                    uiMessage = result.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun saveSettings(
        settings: SettingsForm?,
    ) {
        checkNotNull(settings) { "No settings to save" }
        _isLoading.update { true }
        viewModelScope.launch {
            settingsRepository.setSettings(settings)
                .onSuccess {
                    val language = settings.get<Language>(SK.language) ?: Language.EN
                    applyAppLanguage(language.localeCode)
                    _editedSettings.value = null
                    _settings.update { settings }
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            isEditingSettings = false,
                            previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                            selectedTabIndex = 0,
                            uiMessage = null,
                            uiMessageSeverity = null
                        )
                    }
                }
                .onError { error, throwable ->
                    Logger.e("Could not save settings", throwable)
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            isEditingSettings = false,
                            previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                            selectedTabIndex = 0,
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        }
    }

    private fun importSettings(fileName: String, source: Source) {
        _isLoading.update { true }
        Logger.i("Importing settings")
        viewModelScope.launch {
            if (fileName.endsWith(".json", ignoreCase = true)) {
                val settingsResult = settingsRepository.importSettings(source)
                if (settingsResult is Result.Success) {
                    val settings = settingsResult.data
                    _settings.update { settings }
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            isEditingSettings = false,
                            previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                            selectedTabIndex = 0,
                            uiMessage = null,
                        )
                    }
                } else if (settingsResult is Result.Error) {
                    Logger.e("Could not import settings", settingsResult.throwable)
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            uiMessage = settingsResult.error.toUiText(),
                            uiMessageSeverity = Severity.Error,
                            isEditingSettings = false,
                            previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                            selectedTabIndex = 0,
                        )
                    }
                }
            } else {
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        isEditingSettings = false,
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = 0,
                        uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                        uiMessageSeverity = Severity.Error
                    )
                }
            }
        }
    }

    private fun exportSettings(fileName: String, sink: Sink) {
        _isLoading.update { true }
        Logger.i("Exporting settings")
        viewModelScope.launch {
            if (fileName.endsWith(".json", ignoreCase = true)) {
                val settings = _settings.value
                if(settings != null) {
                    settingsRepository.exportSettings(settings, sink)
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    uiMessage = null,
                                    isEditingSettings = false,
                                    previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                                    selectedTabIndex = 0,
                                )
                            }
                        }
                        .onError { error, throwable ->
                            Logger.e("Could not export settings", throwable)
                            _state.update {
                                it.copy(
                                    uiMessage = error.toUiText(),
                                    uiMessageSeverity = Severity.Error,
                                    isEditingSettings = false,
                                    previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                                    selectedTabIndex = 0,
                                )
                            }
                        }
                }
            } else {
                _state.update {
                    it.copy(
                        isEditingSettings = false,
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = 0,
                        uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                        uiMessageSeverity = Severity.Error
                    )
                }
            }
        }
    }
}
