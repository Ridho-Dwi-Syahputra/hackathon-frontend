# Video Notification Module

## 📋 Overview

Modul ini menangani semua notifikasi terkait video dalam aplikasi SAKO, termasuk preferensi user, FCM topic subscriptions, dan pengecekan sebelum menampilkan notifikasi.

## 🏗️ Struktur File

```
firebase/notifications/video/
├── VideoNotificationPreferencesManager.kt  - Mengelola preferensi di SharedPreferences
├── VideoNotificationManager.kt             - Koordinasi subscription FCM & preferences
└── VideoNotificationHandler.kt             - Proses notifikasi & buat konten
```

## 🔧 Komponen

### 1. VideoNotificationPreferencesManager

**Fungsi:** Menyimpan dan membaca preferensi notifikasi video dari SharedPreferences.

**Key Features:**

-   ✅ Cek apakah notifikasi video diaktifkan secara global
-   ✅ Cek tipe notifikasi spesifik (favorited, uploaded)
-   ✅ Update preferensi individual atau global
-   ✅ Logging untuk debugging

**Preference Keys:**

-   `video_notifications_enabled` - Global toggle untuk semua notifikasi video
-   `video_favorited_enabled` - Toggle untuk notifikasi video difavoritekan
-   `video_uploaded_enabled` - Toggle untuk notifikasi video baru diupload

### 2. VideoNotificationManager

**Fungsi:** Mengelola subscription ke FCM topics dan koordinasi dengan preferences.

**FCM Topics:**

-   `video_notifications` - Topic umum untuk semua notifikasi video
-   `video_favorited_notifications` - Topic khusus video difavoritekan
-   `video_uploaded_notifications` - Topic khusus video baru

**Methods:**

-   `initializeVideoNotifications()` - Setup awal saat app start
-   `setVideoNotificationsEnabled(Boolean)` - Enable/disable semua notifikasi video
-   `shouldProcessNotification(String)` - Cek apakah notifikasi harus ditampilkan
-   `subscribeToVideoTopics()` - Subscribe ke FCM topics
-   `unsubscribeFromVideoTopics()` - Unsubscribe dari FCM topics

### 3. VideoNotificationHandler

**Fungsi:** Memproses data notifikasi FCM dan membuat konten yang sesuai.

**Supported Notification Types:**

-   `video_favorited` - Video ditambahkan ke favorit
-   `video_uploaded` - Video baru diupload

**Methods:**

-   `processVideoNotification()` - Process FCM data
-   `createNotificationContent()` - Buat title & body notifikasi
-   `getNavigationData()` - Data untuk navigation saat notifikasi diklik

## 🔄 Alur Kerja

### Saat User Mengubah Pengaturan di Settings Screen:

```
User Toggle OFF/ON
       ↓
SettingScreen.kt onToggle
       ↓
VideoNotificationManager.setVideoNotificationsEnabled(Boolean)
       ↓
VideoNotificationPreferencesManager (update SharedPreferences)
       ↓
Subscribe/Unsubscribe FCM Topics
       ↓
ProfileViewModel.updateNotificationPreferences() (sync ke backend)
```

### Saat Notifikasi Masuk dari FCM:

```
FCM Message Received
       ↓
SakoFirebaseMessagingService.onMessageReceived()
       ↓
handleVideoNotification() (detect module="video")
       ↓
VideoNotificationManager.shouldProcessNotification()
       ↓
VideoNotificationPreferencesManager (cek SharedPreferences)
       ↓
✅ ALLOWED → Show notification
❌ BLOCKED → Log & return (notifikasi tidak muncul)
```

## 🎯 Cara Penggunaan

### Initialize Saat App Start (MainActivity)

```kotlin
val videoNotificationManager = VideoNotificationManager.getInstance(context)
videoNotificationManager.initializeVideoNotifications()
```

### Cek Status di UI

```kotlin
val videoManager = VideoNotificationManager.getInstance(context)
val prefs = videoManager.getCurrentPreferences()

Log.d(TAG, "Video notifications enabled: ${prefs.allEnabled}")
Log.d(TAG, "Favorited enabled: ${prefs.favoritedEnabled}")
```

### Update Preference Programmatically

```kotlin
val videoManager = VideoNotificationManager.getInstance(context)
videoManager.setVideoNotificationsEnabled(true)
```

## 📊 Format Data FCM

### Video Favorited Notification

```json
{
    "module": "video",
    "type": "video_favorited",
    "video_id": "123",
    "video_title": "Tari Gending Sriwijaya",
    "user_name": "John Doe"
}
```

### Video Uploaded Notification

```json
{
    "module": "video",
    "type": "video_uploaded",
    "video_id": "124",
    "video_title": "Pempek Palembang",
    "category": "Kuliner",
    "user_name": "Jane Smith"
}
```

## 🔍 Debugging

### Melihat Preferensi Saat Ini

```kotlin
val prefsManager = VideoNotificationPreferencesManager.getInstance(context)
prefsManager.logCurrentPreferences()
```

Output Log:

```
=== Video Notification Preferences ===
All notifications: true
Video favorited: true
Video uploaded: true
======================================
```

### Log Penting

Semua komponen menggunakan tag berikut:

-   `VIDEO_NOTIFICATION_PREFS` - PreferencesManager logs
-   `VIDEO_NOTIFICATION_MANAGER` - Manager logs
-   `VIDEO_NOTIFICATION_HANDLER` - Handler logs
-   `SAKO_FCM_SERVICE` - FCM Service logs (untuk semua modul)

## 🐛 Troubleshooting

### Notifikasi Masih Muncul Setelah Dimatikan

**Penyebab:** SharedPreferences belum terupdate atau FCM subscription masih aktif

**Solusi:**

1. Cek log: `VIDEO_NOTIFICATION_PREFS`
2. Pastikan `setVideoNotificationsEnabled()` dipanggil
3. Clear app data dan test lagi

### Notifikasi Tidak Pernah Muncul

**Penyebab:** Topic FCM belum di-subscribe atau preferences false

**Solusi:**

1. Cek log: `VIDEO_NOTIFICATION_MANAGER`
2. Panggil `initializeVideoNotifications()`
3. Pastikan preference enabled di Settings

## ✅ Testing Checklist

-   [ ] Toggle ON di Settings → notifikasi muncul
-   [ ] Toggle OFF di Settings → notifikasi TIDAK muncul
-   [ ] Restart app → preference tersimpan
-   [ ] Backend sync berhasil
-   [ ] FCM subscription terupdate
-   [ ] Log menunjukkan flow yang benar

## 🔗 Integrasi dengan Komponen Lain

### SakoFirebaseMessagingService

Import dan gunakan VideoNotificationManager untuk cek preferences sebelum show notification.

### SettingScreen

Import VideoNotificationManager dan panggil `setVideoNotificationsEnabled()` saat toggle berubah.

### MainActivity (opsional)

Panggil `initializeVideoNotifications()` saat app start atau user login.

## 📝 Catatan Penting

1. **SharedPreferences vs Backend Sync:**
    - SharedPreferences = cek lokal cepat (untuk blok notifikasi)
    - Backend API = sync antar device (untuk consistency)
2. **FCM Topics:**

    - Subscribe/unsubscribe async, mungkin delay beberapa detik
    - Gunakan log untuk verify subscription status

3. **Default Values:**
    - Semua preferences default = `true` (enabled)
    - User harus explicitly disable

## 🎨 Konsistensi dengan Modul Lain

Struktur ini **identik** dengan:

-   `MapNotificationManager` + `MapNotificationPreferencesManager` + `MapNotificationHandler`
-   `QuizNotificationManager` + `QuizNotificationPreferencesManager` + `QuizNotificationHandler`

Sehingga mudah dipahami dan dimaintain.
