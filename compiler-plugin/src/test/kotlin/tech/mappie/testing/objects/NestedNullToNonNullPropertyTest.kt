package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class NestedNullToNonNullPropertyTest {
    data class Input(val text: InnerInput?, val int: Int)
    data class InnerInput(val value: String)
    data class Output(val text: InnerOutput, val int: Int)
    data class InnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with nested null to non-null implicit should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage("Target Output::text automatically resolved from Input::text via InnerMapper but cannot assign source type InnerOutput? to target type InnerOutput")
        }
    }

    @Test
    fun `map object with nested null to non-null explicit without via should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() { 
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage("Target Output::text of type InnerOutput cannot be assigned from from::text via InnerMapper of type InnerOutput?")
        }
    }

    @Test
    fun `map object with nested null to non-null explicit should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() { 
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text via InnerMapper
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage("Target Output::text of type InnerOutput cannot be assigned from from::text via InnerMapper of type InnerOutput?")
        }
    }
}