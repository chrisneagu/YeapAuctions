package ro.ase.dam.yeapauctions.data
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId


@Serializable
data class AppSettings(
    val language: Language = Language.ENGLISH,
    @Contextual
    val userId: ObjectId? = null, //logout reinitializing with "" on login -> keep user email persistent in memory maybe another UserSettings datastore
    val useDarkTheme: Boolean = false // false for light theme true for dark theme
)

enum class Language{
    ROMANIAN, ENGLISH, FRENCH, SPANISH
}