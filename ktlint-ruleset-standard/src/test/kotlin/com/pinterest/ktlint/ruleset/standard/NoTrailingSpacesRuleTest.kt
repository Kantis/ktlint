package com.pinterest.ktlint.ruleset.standard

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.format
import com.pinterest.ktlint.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NoTrailingSpacesRuleTest {

    @Test
    fun testLint() {
        assertThat(NoTrailingSpacesRule().lint("fun main() {\n    val a = 1\n\n \n} "))
            .isEqualTo(
                listOf(
                    LintError(4, 1, "no-trailing-spaces", "Trailing space(s)"),
                    LintError(5, 2, "no-trailing-spaces", "Trailing space(s)")
                )
            )
    }

    @Test
    fun testFormat() {
        assertThat(NoTrailingSpacesRule().format("fun main() {\n    val a = 1 \n  \n \n} "))
            .isEqualTo("fun main() {\n    val a = 1\n\n\n}")
    }

    @Test
    fun `trailing spaces inside line comments`() {
        val code =
            """
            //${" "}
            // Some comment${" "}
            class Foo {
                //${" "}${" "}
                // Some comment${" "}${" "}
                fun bar() = "foobar"
            }
            """.trimIndent()
        val expectedCode =
            """
            //
            // Some comment
            class Foo {
                //
                // Some comment
                fun bar() = "foobar"
            }
            """.trimIndent()

        assertThat(
            NoTrailingSpacesRule().format(code)
        ).isEqualTo(expectedCode)
        assertThat(
            NoTrailingSpacesRule().lint(code)
        ).isEqualTo(
            listOf(
                LintError(1, 3, "no-trailing-spaces", "Trailing space(s)"),
                LintError(2, 16, "no-trailing-spaces", "Trailing space(s)"),
                LintError(4, 7, "no-trailing-spaces", "Trailing space(s)"),
                LintError(5, 20, "no-trailing-spaces", "Trailing space(s)")
            )
        )
    }

    @Test
    fun `format trailing spaces inside block comments`() {
        val code =
            """
            /*${" "}
             * Some comment${" "}
             */
            class Foo {
                /*${" "}${" "}
                 * Some comment${" "}${" "}
                 */
                fun bar() = "foobar"
            }
            """.trimIndent()
        val expectedCode =
            """
            /*
             * Some comment
             */
            class Foo {
                /*
                 * Some comment
                 */
                fun bar() = "foobar"
            }
            """.trimIndent()

        assertThat(
            NoTrailingSpacesRule().format(code)
        ).isEqualTo(expectedCode)
        assertThat(
            NoTrailingSpacesRule().lint(code)
        ).isEqualTo(
            listOf(
                LintError(1, 3, "no-trailing-spaces", "Trailing space(s)"),
                LintError(2, 16, "no-trailing-spaces", "Trailing space(s)"),
                LintError(5, 7, "no-trailing-spaces", "Trailing space(s)"),
                LintError(6, 20, "no-trailing-spaces", "Trailing space(s)")
            )
        )
    }

    @Test
    fun `trailing spaces inside KDoc`() {
        val code =
            """
            /**${" "}
             * Some comment${" "}
             *${" "}
             */
            class Foo {
                /**${" "}${" "}
                 * Some comment${" "}${" "}
                 *${" "}${" "}
                 */
                fun bar() = "foobar"
            }
            """.trimIndent()
        val codeExpected =
            """
            /**
             * Some comment
             *
             */
            class Foo {
                /**
                 * Some comment
                 *
                 */
                fun bar() = "foobar"
            }
            """.trimIndent()

        assertThat(
            NoTrailingSpacesRule().format(code)
        ).isEqualTo(codeExpected)
        assertThat(
            NoTrailingSpacesRule().lint(code)
        ).isEqualTo(
            listOf(
                LintError(1, 4, "no-trailing-spaces", "Trailing space(s)"),
                LintError(2, 16, "no-trailing-spaces", "Trailing space(s)"),
                LintError(3, 3, "no-trailing-spaces", "Trailing space(s)"),
                LintError(6, 8, "no-trailing-spaces", "Trailing space(s)"),
                LintError(7, 20, "no-trailing-spaces", "Trailing space(s)"),
                LintError(8, 7, "no-trailing-spaces", "Trailing space(s)")
            )
        )
    }

    @Test
    fun `Issue 1334 - trailing spaces should not delete indent of the next line`() {
        val code =
            """
            class Foo {
                // something
            ${"    "}
                /**
                 * Some KDoc
                 */
                val bar: String
            ${"    "}
                val foo = "foo"
            }
            """.trimIndent()
        val codeExpected =
            """
            class Foo {
                // something

                /**
                 * Some KDoc
                 */
                val bar: String

                val foo = "foo"
            }
            """.trimIndent()
        assertThat(
            NoTrailingSpacesRule().format(code)
        ).isEqualTo(codeExpected)
        assertThat(
            NoTrailingSpacesRule().lint(code)
        ).isEqualTo(
            listOf(
                LintError(3, 1, "no-trailing-spaces", "Trailing space(s)"),
                LintError(8, 1, "no-trailing-spaces", "Trailing space(s)")
            )
        )
    }

    @Test
    fun `Issue 1376 - trailing spaces should not delete blank line inside kdoc`() {
        val code =
            """
            /**
             Paragraph 1 which should be followed by a blank line.


             Paragraph 2 which should have a blank line before it.
             */
            class MyClass
            """.trimIndent()
        assertThat(NoTrailingSpacesRule().lint(code)).isEmpty()
        assertThat(NoTrailingSpacesRule().format(code)).isEqualTo(code)
    }

    @Test
    fun `Issue 1376 - trailing spaces should be removed from blank line inside kdoc`() {
        val code =
            """
            /**
             Paragraph 1 which should be followed by a blank line.
             ${"    "}
             ${"    "}
             Paragraph 2 which should have a blank line before it.
             */
            class MyClass
            """.trimIndent()
        val formattedCode =
            """
            /**
             Paragraph 1 which should be followed by a blank line.


             Paragraph 2 which should have a blank line before it.
             */
            class MyClass
            """.trimIndent()
        assertThat(
            NoTrailingSpacesRule().format(code)
        ).isEqualTo(formattedCode)
        assertThat(
            NoTrailingSpacesRule().lint(code)
        ).containsExactly(
            LintError(3, 1, "no-trailing-spaces", "Trailing space(s)")
        )
    }
}
