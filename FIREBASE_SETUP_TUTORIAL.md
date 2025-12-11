# 🔥 Firebase Setup Tutorial - Lengkap & Mudah Dipahami

## 📋 Daftar Isi
1. [Firebase Console Setup](#firebase-console-setup)
2. [Android Project Configuration](#android-project-configuration)
3. [Konfigurasi Dependencies](#konfigurasi-dependencies)
4. [Code Implementation](#code-implementation)
5. [Testing & Debugging](#testing--debugging)
6. [Troubleshooting](#troubleshooting)

---

## 🚀 Firebase Console Setup

### 1. Buat Project Firebase
1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Klik "Add project" atau "Tambah project"
3. Masukkan nama project: `sako-cultural-app`
4. Pilih akun Google Analytics (opsional)
5. Klik "Create project"

### 2. Tambahkan Android App
1. Di dashboard Firebase, klik ikon Android
2. **Package name**: `com.sako` (harus sama dengan applicationId di build.gradle)
3. **App nickname**: `Sako Cultural App` (opsional)
4. **SHA-1**: Dapatkan dengan command:
   ```bash
   cd android
   ./gradlew signingReport
   ```
5. Klik "Register app"

### 3. Download google-services.json
1. Download file `google-services.json`
2. **PENTING**: File ini berisi API key dan konfigurasi project
3. Pindahkan ke folder: `app/` (sejajar dengan build.gradle.kts)

---

## 📱 Android Project Configuration

### 1. Project-level build.gradle.kts
```kotlin
plugins {
    // ... existing plugins
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

### 2. App-level build.gradle.kts
```kotlin
plugins {
    // ... existing plugins
    id("com.google.gms.google-services")
}

dependencies {
    // Firebase BOM - manages all versions
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    
    // Firebase components (versions managed by BOM)
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-crashlytics")
}
```

### 3. AndroidManifest.xml
```xml
<!-- Firebase Messaging Service -->
<service
    android:name="com.sako.firebase.SakoFirebaseMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>

<!-- Notification permissions (Android 13+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

## ⚙️ Code Implementation

### 1. Application Class (SakoApplication.kt)
```kotlin
class SakoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseHelper.initialize(this)
        
        // Create notification channel
        createNotificationChannel()
    }
}
```

### 2. Firebase Helper (FirebaseHelper.kt)
File ini sudah dibuat dengan fitur lengkap:
- ✅ Firebase initialization
- ✅ FCM token generation
- ✅ Topic subscription
- ✅ Analytics logging
- ✅ Error handling

### 3. Firebase Messaging Service (SakoFirebaseMessagingService.kt)
File ini menangani:
- ✅ Token refresh
- ✅ Push notification
- ✅ Background message handling
- ✅ Notification channel management

### 4. Authentication Integration
Sudah terintegrasi dengan AuthRepository untuk:
- ✅ Login dengan FCM token
- ✅ Register dengan FCM token
- ✅ Token refresh otomatis

---

## 🧪 Testing & Debugging

### 1. Test Firebase Configuration
```kotlin
// Di MainActivity atau Activity lain
FirebaseInitializationTest.testFirebaseConfiguration(this)
```

### 2. Test FCM Token
```kotlin
FirebaseInitializationTest.testFCMTokenOnly()
```

### 3. Check Firebase Status
```kotlin
val status = FirebaseInitializationTest.checkFirebaseStatus()
Log.d("Firebase", "Status: $status")
```

### 4. Logcat Debugging
Cari log dengan tag:
- `FirebaseHelper`: Informasi Firebase helper
- `FirebaseTest`: Test configuration
- `FCM_TOKEN`: FCM token generation
- `FIREBASE_MESSAGING`: Message handling

---

## 🔧 Troubleshooting

### ❌ "Please set a valid API key"
**Penyebab**: google-services.json tidak valid atau tidak ada
**Solusi**:
1. Pastikan file `google-services.json` ada di folder `app/`
2. Pastikan package name sama dengan Firebase project
3. Build ulang project: `./gradlew clean build`

### ❌ "FCM token generation failed"
**Penyebab**: Firebase tidak terinisialisasi dengan benar
**Solusi**:
1. Cek `google-services` plugin di build.gradle
2. Pastikan dependencies Firebase sudah benar
3. Test dengan `FirebaseInitializationTest`

### ❌ "Token not persisting after login"
**Penyebab**: UserPreference DataStore issue
**Solusi**:
1. Cek enhanced debug logging di AuthRepository
2. Pastikan session save/load bekerja
3. Test dengan immediate verification

### ❌ "Firebase app not initialized"
**Penyebab**: Initialization timing issue
**Solusi**:
1. Pastikan `FirebaseHelper.initialize()` dipanggil di Application.onCreate()
2. Cek apakah google-services.json valid
3. Restart aplikasi setelah perubahan

---

## 📝 Checklist Setup

### ✅ Sudah Selesai:
- [x] Firebase project created: `sako-cultural-app`
- [x] google-services.json dengan API key valid
- [x] Build.gradle.kts updated dengan dependencies
- [x] FirebaseHelper implementation
- [x] SakoFirebaseMessagingService implementation
- [x] AndroidManifest service registration
- [x] SakoApplication Firebase initialization
- [x] AuthRepository integration
- [x] FirebaseInitializationTest untuk debugging

### 🔄 Yang Perlu Ditest:
- [ ] Build aplikasi tanpa error
- [ ] Firebase initialization success
- [ ] FCM token generation berhasil
- [ ] Login dengan token persistence
- [ ] Push notification functionality

---

## 🎯 Next Steps

1. **Build & Test**: Build aplikasi dan test Firebase initialization
2. **FCM Testing**: Test FCM token generation dan push notifications
3. **Login Flow**: Test complete login flow dengan token persistence
4. **Production Setup**: Setup Firebase untuk production environment

---

## 📞 Debug Commands

### Quick Firebase Status Check:
```kotlin
val status = FirebaseInitializationTest.checkFirebaseStatus()
Log.d("FIREBASE_STATUS", "✅ Ready: ${status.canGenerateFCMToken}")
```

### Generate FCM Token:
```kotlin
CoroutineScope(Dispatchers.IO).launch {
    val token = FirebaseHelper.generateFCMToken()
    Log.d("FCM_TOKEN", "Token: $token")
}
```

### Complete Test:
```kotlin
FirebaseInitializationTest.testFirebaseConfiguration(this)
```

---

## 🔗 Resources

- [Firebase Android Setup Guide](https://firebase.google.com/docs/android/setup)
- [FCM Android Implementation](https://firebase.google.com/docs/cloud-messaging/android/client)
- [Firebase Console](https://console.firebase.google.com/)
- [google-services plugin documentation](https://developers.google.com/android/guides/google-services-plugin)

---

*Tutorial ini dibuat untuk project Sako Cultural App dengan konfigurasi Firebase yang lengkap dan ready untuk production.*