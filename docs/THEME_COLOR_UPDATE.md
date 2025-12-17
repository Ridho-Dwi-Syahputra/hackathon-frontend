# 🎨 Update Warna Theme SAKO

## Perubahan yang Dilakukan

### File: `ui/theme/Color.kt`

Menyesuaikan warna Material Theme untuk memberikan tampilan yang lebih harmonis dan konsisten di seluruh aplikasi.

## Warna yang Diubah

### **primaryContainer** (Light Theme)

-   **Sebelum**: `Color(0xFFFFDAD6)` - Merah muda terang
-   **Setelah**: `Color(0xFFE8D5B5)` - Krem/beige yang lembut
-   **Alasan**: Memberikan background yang lebih hangat dan nyaman untuk UserLevelCard

### **onPrimaryContainer** (Light Theme)

-   **Sebelum**: `Color(0xFF410002)` - Merah sangat gelap
-   **Setelah**: `Color(0xFF3D1410)` - Coklat tua
-   **Alasan**: Kontras lebih baik dengan background krem

### **onSecondary** (Light Theme)

-   **Sebelum**: `Black`
-   **Setelah**: `Color(0xFF3D2800)` - Coklat gelap
-   **Alasan**: Meningkatkan kontras pada komponen secondary

### **secondaryContainer** (Light Theme)

-   **Sebelum**: `Color(0xFFFFEDC8)` - Kuning terang
-   **Setelah**: `Color(0xFFFFF3E0)` - Kuning sangat terang/ivory
-   **Alasan**: Background yang lebih bersih untuk FeatureCard

### **onSecondaryContainer** (Light Theme)

-   **Sebelum**: `Color(0xFF2B1700)` - Coklat sangat gelap
-   **Setelah**: `Color(0xFF3D2800)` - Coklat gelap
-   **Alasan**: Konsistensi dengan onSecondary

---

## Dampak Visual

### 1. **UserLevelCard** (HomeScreen)

-   Background: Krem/beige yang hangat
-   Badge: Merah SAKO (#9C302C)
-   Text: Coklat tua untuk kontras optimal
-   Progress bar: Kuning aksen dengan track merah transparan

### 2. **FeatureCard** (Kuis, Video, Map)

-   Background: Kuning sangat terang (ivory)
-   Icon & Text: Coklat untuk kontras yang baik

### 3. **StatsOverviewCard**

-   Background: Surface putih bersih
-   Icon: Primary red
-   Text: Default onSurface

---

## Palet Warna SAKO (Reference)

### Brand Colors

```kotlin
Primary Red: #9C302C    // Merah utama
Accent Yellow: #F7CE80  // Kuning aksen
```

### Light Theme Containers

```kotlin
primaryContainer: #E8D5B5      // Krem
onPrimaryContainer: #3D1410    // Coklat tua

secondaryContainer: #FFF3E0    // Ivory
onSecondaryContainer: #3D2800  // Coklat gelap
```

### Neutral Colors

```kotlin
Surface: #FAFAFA           // Putih
onSurface: #000000         // Hitam
Background: #F5F5F5        // Abu-abu terang
```

---

## Cara Menggunakan

Semua warna diakses melalui `MaterialTheme.colorScheme.*`:

```kotlin
// Primary colors
MaterialTheme.colorScheme.primary           // #9C302C
MaterialTheme.colorScheme.onPrimary         // #FFFFFF
MaterialTheme.colorScheme.primaryContainer  // #E8D5B5 (NEW)
MaterialTheme.colorScheme.onPrimaryContainer // #3D1410 (NEW)

// Secondary colors
MaterialTheme.colorScheme.secondary          // #F7CE80
MaterialTheme.colorScheme.onSecondary        // #3D2800 (NEW)
MaterialTheme.colorScheme.secondaryContainer // #FFF3E0 (NEW)
MaterialTheme.colorScheme.onSecondaryContainer // #3D2800 (NEW)

// Surface & Background
MaterialTheme.colorScheme.surface
MaterialTheme.colorScheme.onSurface
MaterialTheme.colorScheme.background
```

---

## Konsistensi Aplikasi

### HomeScreen ✅

-   Menggunakan `MaterialTheme.colorScheme` untuk semua warna
-   UserLevelCard: `primaryContainer`
-   StatsOverviewCard: `surface`
-   FeatureCard: `secondaryContainer`

### VideoListScreen ✅

-   BackgroundImage wrapper
-   Cards: `surface`
-   FAB: `primary`

### MapScreen ✅

-   BackgroundImage wrapper
-   Cards: `surface`
-   Buttons: `primary`

---

## Testing

Untuk melihat perubahan:

1. Build & run aplikasi
2. Buka HomeScreen
3. Perhatikan warna yang lebih harmonis pada:
    - UserLevelCard (background krem)
    - FeatureCard (background ivory)
    - Progress bar (kuning dengan track merah)

---

## Dark Mode Support

Warna dark theme tetap menggunakan konfigurasi original untuk konsistensi. Jika diperlukan penyesuaian dark mode, edit bagian `md_theme_dark_*` di Color.kt.

---

**Update Date**: December 17, 2025
**Modified Files**:

-   `app/src/main/java/com/sako/ui/theme/Color.kt`

**Status**: ✅ Completed & Tested
