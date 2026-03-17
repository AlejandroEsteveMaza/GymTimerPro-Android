package com.alejandroestevemaza.gymtimerpro.feature.paywall

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertTrue
import org.junit.Test

class PaywallLocalizationContractTest {

    @Test
    fun `all paywall locales contain the full keyset and critical auxiliary keys`() {
        val resDir = resolveResDir()
        val baseFile = File(resDir, "values/strings_paywall.xml")
        val baseKeys = parseStringKeys(baseFile)

        assertTrue("Base paywall strings file is missing", baseFile.exists())
        assertTrue("Base paywall keys are empty", baseKeys.isNotEmpty())

        REQUIRED_PAYWALL_LOCALES.forEach { qualifier ->
            val localeFile = File(resDir, "$qualifier/strings_paywall.xml")
            assertTrue("Missing paywall file for locale: $qualifier", localeFile.exists())

            val localeKeys = parseStringKeys(localeFile)
            val missingBaseKeys = baseKeys - localeKeys
            assertTrue(
                "Locale $qualifier is missing paywall keys: ${missingBaseKeys.sorted()}",
                missingBaseKeys.isEmpty(),
            )

            val missingCriticalKeys = CRITICAL_AUXILIARY_KEYS - localeKeys
            assertTrue(
                "Locale $qualifier is missing critical auxiliary paywall keys: ${missingCriticalKeys.sorted()}",
                missingCriticalKeys.isEmpty(),
            )
        }
    }

    @Test
    fun `all locales include paywall copy for the 6 context variants`() {
        val resDir = resolveResDir()

        REQUIRED_PAYWALL_LOCALES.forEach { qualifier ->
            val localeFile = File(resDir, "$qualifier/strings_paywall.xml")
            assertTrue("Missing paywall file for locale: $qualifier", localeFile.exists())

            val localeKeys = parseStringKeys(localeFile)
            val missingVariantPrefixes = VARIANT_PREFIXES.filter { prefix ->
                localeKeys.none { key -> key.startsWith(prefix) }
            }
            assertTrue(
                "Locale $qualifier is missing copy variants: $missingVariantPrefixes",
                missingVariantPrefixes.isEmpty(),
            )
        }
    }

    private fun parseStringKeys(file: File): Set<String> {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(file)
        val nodeList = document.getElementsByTagName("string")
        val keys = mutableSetOf<String>()
        for (index in 0 until nodeList.length) {
            val node = nodeList.item(index)
            val attributes = node.attributes ?: continue
            val nameNode = attributes.getNamedItem("name") ?: continue
            keys += nameNode.nodeValue
        }
        return keys
    }

    private fun resolveResDir(): File {
        val cwd = File(checkNotNull(System.getProperty("user.dir")))
        val candidates = listOf(
            File(cwd, "src/main/res"),
            File(cwd, "app/src/main/res"),
        )
        return candidates.firstOrNull(File::exists)
            ?: error("Unable to locate Android resources directory from ${cwd.absolutePath}")
    }
}

private val REQUIRED_PAYWALL_LOCALES = listOf(
    "values",
    "values-da",
    "values-de",
    "values-es",
    "values-fr",
    "values-hi",
    "values-it",
    "values-ja",
    "values-ko",
    "values-nb",
    "values-nl",
    "values-pt-rBR",
    "values-pt-rPT",
    "values-sv",
    "values-b+zh+Hans",
)

private val VARIANT_PREFIXES = listOf(
    "paywall_copy_light_pro_",
    "paywall_copy_light_limit_",
    "paywall_copy_standard_pro_",
    "paywall_copy_standard_limit_",
    "paywall_copy_detailed_pro_",
    "paywall_copy_detailed_limit_",
)

private val CRITICAL_AUXILIARY_KEYS = setOf(
    "paywall_badge_pro",
    "paywall_subtitle_limit_format",
    "paywall_price_loading",
    "paywall_trial_incentive_format",
    "paywall_period_generic",
    "paywall_button_restore",
    "paywall_button_manage",
    "paywall_button_terms",
    "paywall_button_privacy",
    "paywall_error_title",
    "paywall_error_product_unavailable",
    "paywall_error_failed_verification",
    "paywall_error_user_cancelled",
    "paywall_error_pending",
    "paywall_error_unknown",
    "paywall_restore_no_purchases",
    "paywall_info_title",
    "common_ok",
    "common_cancel",
)
