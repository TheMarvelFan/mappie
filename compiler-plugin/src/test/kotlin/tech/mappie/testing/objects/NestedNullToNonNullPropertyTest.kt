package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.containsError
import java.io.File

class NestedNullToNonNullPropertyTest {
    data class Input(val text: InnerInput?, val int: Int)
    data class InnerInput(val value: String)
    data class Output(val text: InnerOutput, val int: Int)
    data class InnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map data classes with nested null to non-null implicit should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.NestedNullToNonNullPropertyTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()

                        object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages)
                .containsError("Target Output::text automatically resolved from Input::text via InnerMapper but cannot assign source type InnerOutput? to target type InnerOutput")
        }
    }

    @Test
    fun `map data classes with nested null to non-null explicit without via should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
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
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages)
                .containsError("Target Output::text of type InnerOutput cannot be assigned from from::text via InnerMapper of type InnerOutput?")
        }
    }

    @Test
    fun `map data classes with nested null to non-null explicit should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
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
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages)
                .containsError("Target Output::text of type InnerOutput cannot be assigned from from::text via InnerMapper of type InnerOutput?")
        }
    }
}