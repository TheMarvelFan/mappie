@file:Suppress("unused", "UNUSED_PARAMETER")

package io.github.mappie.api

/**
 * Base class for enum mappers. See the [documentation](https://mappie.tech/enum-mapping/enum-mapping-overview/)
 * for a complete overview of how to generate enum mappers.
 *
 * For example
 * ```kotlin
 * class PersonMapper : EnumMapper<Color, Colour>() {
 *      override fun map(from: Color) = mapping()
 * }
 * ```
 * will generate a mapper from `Color` to `Colour`, assuming both `Color` and `Colour` have a resolvable mapping.
 *
 * @param FROM the source type to map from.
 * @param TO the target type to map to.
 */
abstract class EnumMapper<FROM: Enum<*>, TO : Enum<*>> : Mapper<FROM, TO>() {

    /**
     * Explicitly construct a mapping to [TO] from source entry [source].
     *
     * For example
     * ```kotlin
     * Colour.UNKNOWN mappedFromEnumEntry Color.ORANGE
     * ```
     * will generate an explicit mapping, mapping `Colour.ORANGE` to `Color.UNKNOWN`.
     */
    protected infix fun TO.mappedFromEnumEntry(source: FROM): EnumMapper<FROM, TO> = generated()
}