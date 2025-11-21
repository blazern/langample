package blazern.lexisoup.domain.backend_address

import blazern.lexisoup.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BackendAddressProvider(
    private val settings: SettingsRepository,
) {
    val baseUrl: Flow<String> = settings.getBackendBaseUrl(
        defaultValue = DEFAULT_BASE_URL,
    )

    val isLocalhost: Flow<Boolean> = baseUrl.map { it == LOCALHOST_BASE_URL }

    suspend fun setIsLocalhost(isLocalhost: Boolean) {
        if (isLocalhost) {
            settings.setBackendBaseUrl(LOCALHOST_BASE_URL)
        } else {
            settings.setBackendBaseUrl(DEFAULT_BASE_URL)
        }
    }
}

private const val DEFAULT_BASE_URL = "https://blazern.me/langample/"
private const val LOCALHOST_BASE_URL = "http://localhost:8888"
