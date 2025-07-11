package blazern.langample.domain.settings

class SettingsRepository {
    suspend fun getTatoebaAcceptableTagsSets(): List<Set<String>> {
        return listOf(
            setOf("nominative", "singular"),
            setOf("nominative", "plural"),
        )
    }
}
