package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile

import java.io.File

class MapperClassCanContainPropertiesTest {

    data class Input(val text: String)
    data class Output(val text: String, val int: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map with a property in constructor of mapper should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperClassCanContainPropertiesTest.*

                class Mapper(private val int: Int) : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::int fromValue int
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call(10)

            assertThat(mapper.map(Input("test")))
                .isEqualTo(Output("test", 10))
        }
    }
}