package com.memo.app.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

// ========== Style Types ==========
enum class AppStyle(val label: String) {
    PAPER("纸质风"),
    GLASS("玻璃风"),
    ANIME("二次元");
}

// ========== 10 Color Presets ==========
enum class ColorPreset(val label: String, val emoji: String) {
    CLASSIC("经典暖棕", "📜"),
    OCEAN("海洋蓝", "🌊"),
    SAKURA("樱花粉", "🌸"),
    MINT("薄荷绿", "🍃"),
    SUNSET("日落橙", "🌅"),
    LAVENDER("薰衣草紫", "💜"),
    INK("水墨灰", "🖌️"),
    CHERRY("樱桃红", "🍒"),
    WHEAT("金色麦穗", "🌾"),
    FOREST("森林青", "🌲");
}

// ========== Theme Palette ==========
data class ThemePalette(
    val topBar: Color,
    val topBarIcon: Color,
    val topBarSurface: Color,
    val background: Color,
    val backgroundAlt: Color,
    val surface: Color,
    val surfaceAlpha: Float = 1f,
    val accent: Color,
    val accentDark: Color,
    val text: Color,
    val textSecondary: Color,
    val placeholder: Color,
    val line: Color,
    val divider: Color,
    val error: Color = Color(0xFFCC4A4A),
    val statusBarDark: Boolean = true,
    val glassAlpha: Float = 0f,  // >0 means glass effect
    val cardElevation: Float = 1f,
)

// ========== Color Preset Definitions ==========
private fun presetColors(preset: ColorPreset): Pair<ColorArray, ColorArray> {
    return when (preset) {
        ColorPreset.CLASSIC -> Pair(
            // Light: topBar, topBarIcon, topBarSurface, bg, bgAlt, surface, accent, accentDark, text, textSec, placeholder, line, divider
            arrayOf(Color(0xFF3E2F23), Color(0xFFE8D5B5), Color(0xFF4A3B2E), Color(0xFFE8D5B5), Color(0xFFF5EDE0), Color(0xFFFFF8EF), Color(0xFFD4A04A), Color(0xFF8B6914), Color(0xFF3E2F23), Color(0xFF8B7D6B), Color(0xFFB8A890), Color(0xFFD8C8B0), Color(0xFFD0C0A8)),
            arrayOf(Color(0xFF2A2118), Color(0xFFE8D5B5), Color(0xFF3A2F24), Color(0xFF2A2118), Color(0xFF3A2F24), Color(0xFF4A3F34), Color(0xFFD4A04A), Color(0xFF8B6914), Color(0xFFE8D5B5), Color(0xFF9A8B78), Color(0xFF7A6B58), Color(0xFF4A3A2A), Color(0xFF5A4A3A))
        )
        ColorPreset.OCEAN -> Pair(
            arrayOf(Color(0xFF1A3A5C), Color(0xFFD6EAF8), Color(0xFF24476B), Color(0xFFD6EAF8), Color(0xFFEAF4FC), Color(0xFFF5FBFF), Color(0xFF3498DB), Color(0xFF1A6DAD), Color(0xFF1A3A5C), Color(0xFF6B8FAD), Color(0xFF98B5CC), Color(0xFFB8D4E8), Color(0xFFA8C8E0)),
            arrayOf(Color(0xFF0D1F33), Color(0xFFD6EAF8), Color(0xFF1A3050), Color(0xFF0D1F33), Color(0xFF1A3050), Color(0xFF243D5C), Color(0xFF5DADE2), Color(0xFF2E86C1), Color(0xFFD6EAF8), Color(0xFF7FA5C2), Color(0xFF5C7A95), Color(0xFF1A3A5C), Color(0xFF2A4A6C))
        )
        ColorPreset.SAKURA -> Pair(
            arrayOf(Color(0xFF5C2A3A), Color(0xFFFCE4EC), Color(0xFF6B3448), Color(0xFFFCE4EC), Color(0xFFFFF0F5), Color(0xFFFFF5F8), Color(0xFFE91E63), Color(0xFFAD1457), Color(0xFF5C2A3A), Color(0xFFAD6B85), Color(0xFFCCA0B5), Color(0xFFF0C8D8), Color(0xFFE8B8CC)),
            arrayOf(Color(0xFF2A1520), Color(0xFFFCE4EC), Color(0xFF3A2030), Color(0xFF2A1520), Color(0xFF3A2030), Color(0xFF4A2840), Color(0xFFF06292), Color(0xFFC2185B), Color(0xFFFCE4EC), Color(0xFFAD6B85), Color(0xFF8A5068), Color(0xFF3A2030), Color(0xFF4A2840))
        )
        ColorPreset.MINT -> Pair(
            arrayOf(Color(0xFF1B4332), Color(0xFFD8F3DC), Color(0xFF245740), Color(0xFFD8F3DC), Color(0xFFEDF7F0), Color(0xFFF5FBF7), Color(0xFF2ECC71), Color(0xFF1E8449), Color(0xFF1B4332), Color(0xFF5B9A78), Color(0xFF90BFA5), Color(0xFFB8DCC8), Color(0xFFA8D0B8)),
            arrayOf(Color(0xFF0D2818), Color(0xFFD8F3DC), Color(0xFF1B3828), Color(0xFF0D2818), Color(0xFF1B3828), Color(0xFF245038), Color(0xFF58D68D), Color(0xFF27AE60), Color(0xFFD8F3DC), Color(0xFF5B9A78), Color(0xFF3D7A58), Color(0xFF1B3828), Color(0xFF245038))
        )
        ColorPreset.SUNSET -> Pair(
            arrayOf(Color(0xFF5C2A0A), Color(0xFFFDEBD0), Color(0xFF6B3410), Color(0xFFFDEBD0), Color(0xFFFFF4E8), Color(0xFFFFF8F0), Color(0xFFE67E22), Color(0xFFAF601A), Color(0xFF5C2A0A), Color(0xFFAD7A50), Color(0xFFCCA880), Color(0xFFF0D0B0), Color(0xFFE8C4A0)),
            arrayOf(Color(0xFF2A1505), Color(0xFFFDEBD0), Color(0xFF3A2010), Color(0xFF2A1505), Color(0xFF3A2010), Color(0xFF4A2818), Color(0xFFF39C12), Color(0xFFD68910), Color(0xFFFDEBD0), Color(0xFFAD7A50), Color(0xFF8A5A38), Color(0xFF3A2010), Color(0xFF4A2818))
        )
        ColorPreset.LAVENDER -> Pair(
            arrayOf(Color(0xFF3C1A5C), Color(0xFFEDE7F6), Color(0xFF4A2468), Color(0xFFEDE7F6), Color(0xFFF5F0FA), Color(0xFFFAF5FF), Color(0xFF9B59B6), Color(0xFF7D3C98), Color(0xFF3C1A5C), Color(0xFF8A6BA5), Color(0xFFB098CC), Color(0xFFD0C0E0), Color(0xFFC4B0D8)),
            arrayOf(Color(0xFF1A0D2A), Color(0xFFEDE7F6), Color(0xFF2A1840), Color(0xFF1A0D2A), Color(0xFF2A1840), Color(0xFF3C2458), Color(0xFFBB8FCE), Color(0xFF8E44AD), Color(0xFFEDE7F6), Color(0xFF8A6BA5), Color(0xFF5C4078), Color(0xFF2A1840), Color(0xFF3C2458))
        )
        ColorPreset.INK -> Pair(
            arrayOf(Color(0xFF2C3E50), Color(0xFFECF0F1), Color(0xFF34495E), Color(0xFFECF0F1), Color(0xFFF8F9FA), Color(0xFFFFFFFF), Color(0xFF546E7A), Color(0xFF37474F), Color(0xFF2C3E50), Color(0xFF7F8C8D), Color(0xFFB0BEC5), Color(0xFFCFD8DC), Color(0xFFB0BEC5)),
            arrayOf(Color(0xFF1A1A2E), Color(0xFFECF0F1), Color(0xFF252545), Color(0xFF1A1A2E), Color(0xFF252545), Color(0xFF333355), Color(0xFF78909C), Color(0xFF546E7A), Color(0xFFECF0F1), Color(0xFF8899AA), Color(0xFF556677), Color(0xFF252545), Color(0xFF333355))
        )
        ColorPreset.CHERRY -> Pair(
            arrayOf(Color(0xFF5C0A0A), Color(0xFFFDEDEC), Color(0xFF6B1010), Color(0xFFFDEDEC), Color(0xFFFFF2F2), Color(0xFFFFF8F8), Color(0xFFE74C3C), Color(0xFFC0392B), Color(0xFF5C0A0A), Color(0xFFAD5050), Color(0xFFCC8080), Color(0xFFF0C0C0), Color(0xFFE8B0B0)),
            arrayOf(Color(0xFF2A0505), Color(0xFFFDEDEC), Color(0xFF3A1010), Color(0xFF2A0505), Color(0xFF3A1010), Color(0xFF4A1818), Color(0xFFEC7063), Color(0xFFE74C3C), Color(0xFFFDEDEC), Color(0xFFAD5050), Color(0xFF8A3838), Color(0xFF3A1010), Color(0xFF4A1818))
        )
        ColorPreset.WHEAT -> Pair(
            arrayOf(Color(0xFF5C4A0A), Color(0xFFFEF9E7), Color(0xFF6B5510), Color(0xFFFEF9E7), Color(0xFFFFFCF0), Color(0xFFFFFDF5), Color(0xFFF1C40F), Color(0xFFB7950B), Color(0xFF5C4A0A), Color(0xFFAD9A50), Color(0xFFCCB880), Color(0xFFF0E0B0), Color(0xFFE8D8A0)),
            arrayOf(Color(0xFF2A2205), Color(0xFFFEF9E7), Color(0xFF3A3010), Color(0xFF2A2205), Color(0xFF3A3010), Color(0xFF4A3C18), Color(0xFFF4D03F), Color(0xFFD4AC0D), Color(0xFFFEF9E7), Color(0xFFAD9A50), Color(0xFF8A7A38), Color(0xFF3A3010), Color(0xFF4A3C18))
        )
        ColorPreset.FOREST -> Pair(
            arrayOf(Color(0xFF0A3D2E), Color(0xFFE0F2F1), Color(0xFF104A38), Color(0xFFE0F2F1), Color(0xFFF0F8F7), Color(0xFFF5FCFB), Color(0xFF1ABC9C), Color(0xFF148F77), Color(0xFF0A3D2E), Color(0xFF4D9080), Color(0xFF80B8AA), Color(0xFFB0D8D0), Color(0xFFA0D0C4)),
            arrayOf(Color(0xFF051E18), Color(0xFFE0F2F1), Color(0xFF0A2E22), Color(0xFF051E18), Color(0xFF0A2E22), Color(0xFF103E30), Color(0xFF48C9B0), Color(0xFF17A589), Color(0xFFE0F2F1), Color(0xFF4D9080), Color(0xFF306858), Color(0xFF0A2E22), Color(0xFF103E30))
        )
    }
}

typealias ColorArray = Array<Color>

// ========== Build Theme Palette ==========
fun buildPalette(style: AppStyle, preset: ColorPreset, isDark: Boolean): ThemePalette {
    val (lightArr, darkArr) = presetColors(preset)
    val c = if (isDark) darkArr else lightArr
    // indices: 0=topBar, 1=topBarIcon, 2=topBarSurface, 3=bg, 4=bgAlt, 5=surface,
    //          6=accent, 7=accentDark, 8=text, 9=textSec, 10=placeholder, 11=line, 12=divider

    return when (style) {
        AppStyle.PAPER -> ThemePalette(
            topBar = c[0], topBarIcon = c[1], topBarSurface = c[2],
            background = c[3], backgroundAlt = c[4], surface = c[5],
            accent = c[6], accentDark = c[7], text = c[8],
            textSecondary = c[9], placeholder = c[10], line = c[11], divider = c[12],
            statusBarDark = true, cardElevation = 1f
        )
        AppStyle.GLASS -> ThemePalette(
            topBar = c[0].copy(alpha = 0.7f), topBarIcon = c[1], topBarSurface = c[2].copy(alpha = 0.6f),
            background = c[3].copy(alpha = 0.4f), backgroundAlt = c[4].copy(alpha = 0.3f),
            surface = c[5].copy(alpha = 0.65f), surfaceAlpha = 0.65f,
            accent = c[6], accentDark = c[7], text = c[8],
            textSecondary = c[9], placeholder = c[10], line = c[11].copy(alpha = 0.5f),
            divider = c[12].copy(alpha = 0.4f),
            statusBarDark = true, glassAlpha = 0.65f, cardElevation = 0f
        )
        AppStyle.ANIME -> ThemePalette(
            topBar = c[0], topBarIcon = c[1], topBarSurface = c[2],
            background = c[3], backgroundAlt = c[4], surface = c[5],
            accent = c[6], accentDark = c[7], text = c[8],
            textSecondary = c[9], placeholder = c[10], line = c[11], divider = c[12],
            statusBarDark = true, cardElevation = 2f
        )
    }
}

// ========== Theme Manager ==========
object ThemeManager {
    private const val PREF_NAME = "memo_theme_prefs"
    private const val KEY_STYLE = "app_style"
    private const val KEY_PRESET = "color_preset"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        _style.value = loadStyle()
        _preset.value = loadPreset()
    }

    private fun loadStyle(): AppStyle {
        val name = prefs?.getString(KEY_STYLE, AppStyle.PAPER.name) ?: AppStyle.PAPER.name
        return try { AppStyle.valueOf(name) } catch (_: Exception) { AppStyle.PAPER }
    }

    private fun loadPreset(): ColorPreset {
        val name = prefs?.getString(KEY_PRESET, ColorPreset.CLASSIC.name) ?: ColorPreset.CLASSIC.name
        return try { ColorPreset.valueOf(name) } catch (_: Exception) { ColorPreset.CLASSIC }
    }

    private val _style = mutableStateOf(AppStyle.PAPER)
    val style: State<AppStyle> get() = _style

    private val _preset = mutableStateOf(ColorPreset.CLASSIC)
    val preset: State<ColorPreset> get() = _preset

    fun setStyle(s: AppStyle) {
        _style.value = s
        prefs?.edit()?.putString(KEY_STYLE, s.name)?.apply()
    }

    fun setPreset(p: ColorPreset) {
        _preset.value = p
        prefs?.edit()?.putString(KEY_PRESET, p.name)?.apply()
    }

    fun currentPalette(isDark: Boolean): ThemePalette {
        return buildPalette(_style.value, _preset.value, isDark)
    }
}
