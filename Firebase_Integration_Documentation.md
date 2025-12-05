# 🔥 FIREBASE INTEGRATION DOCUMENTATION - SAKO FRONTEND

## 📱 **Struktur Firebase Frontend (Sesuai Backend)**

```
app/src/main/java/com/sako/firebase/
├── FCMService.kt                    # Firebase Messaging Service
├── FirebaseConfig.kt                # Firebase configuration & token management
└── notifications/
    └── map/
        ├── MapNotificationManager.kt   # Map notification preferences
        └── MapNotificationHandler.kt   # Handle notification processing
```

## 🔧 **Backend Integration Mapping**

### **Backend Structure:**
```
src/controllers/firebase/
├── firebaseConfig.js                # Firebase Admin SDK setup
└── notifikasi/
    └── modul-map/
        └── mapNotifikasiController.js # Map notifications controller
```

### **Frontend Implementation:**
- **FCMService.kt** ↔ Backend Firebase Admin SDK
- **MapNotificationManager.kt** ↔ mapNotifikasiController.js
- **MapNotificationHandler.kt** ↔ Notification processing logic

## 🎯 **Fitur Notifikasi Yang Diimplementasi**

### ✅ **1. Review Added Notification**
**Backend:** `sendReviewAddedNotification()`
**Frontend:** `handleReviewAddedNotification()`

**Flow:**
1. User adds review via `MapRepository.addReview()`
2. Backend sends FCM notification via `mapNotifikasiController.js`
3. `FCMService.kt` receives notification
4. `MapNotificationHandler.kt` processes notification data
5. App navigates to PlaceDetailScreen

### ✅ **2. Place Visited Notification**
**Backend:** `sendPlaceVisitedNotification()`
**Frontend:** `handlePlaceVisitedNotification()`

**Flow:**
1. User scans QR via `MapRepository.scanQRCode()`
2. Backend sends FCM notification after successful visit recording
3. `FCMService.kt` receives notification
4. `MapNotificationHandler.kt` processes notification data
5. App navigates to AddReviewScreen

### ✅ **3. Notification Preferences**
**Backend:** `updateMapNotificationPreferences()`
**Frontend:** `MapNotificationManager.updateMapNotificationPreferences()`

**Preferences (sesuai database users.notification_preferences):**
```json
{
  "map_notifications": {
    "review_added": true,
    "place_visited": true
  }
}
```

## 🔔 **Notification Channels (Android)**

Sesuai dengan backend notification options:

```kotlin
// Channel IDs
const val CHANNEL_DEFAULT = "sako_default"         // General notifications
const val CHANNEL_MAP_REVIEWS = "sako_map_reviews" // Review notifications
const val CHANNEL_MAP_VISITS = "sako_map_visits"   // Visit notifications
```

## 📱 **FCM Token Management**

### **Token Generation & Sync:**
1. `FirebaseConfig.getFCMToken()` generates token
2. Token sent to backend via `AuthRepository.login()/register()`
3. Backend stores in `users.fcm_token` field
4. Backend uses token for `sendNotification()` calls

### **Token Update:**
```kotlin
// Auto-update token on refresh
override fun onNewToken(token: String) {
    super.onNewToken(token)
    sendTokenToBackend(token)
}
```

## 🛠️ **Setup Instructions**

### **1. Firebase Project Configuration**
```gradle
// build.gradle (project level)
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.0'
    }
}

// build.gradle (app level)
dependencies {
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-messaging-ktx'
}

apply plugin: 'com.google.gms.google-services'
```

### **2. Firebase Project Settings**
- **Project ID:** `sako-cultural-app` (sesuai backend .env)
- **Package Name:** `com.sako` 
- **Download:** `google-services.json` → place in `app/` directory

### **3. AndroidManifest.xml**
```xml
<!-- Add FCMService -->
<service android:name=".firebase.FCMService" android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>

<!-- Notification permissions -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Default notification settings -->
<meta-data android:name="com.google.firebase.messaging.default_notification_channel_id"
           android:value="sako_default" />
```

### **4. MainActivity Integration**
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize Firebase
    FirebaseConfig.initialize(this)
    
    // Debug logging
    FirebaseDebugUtils.logFirebaseStatus(this)
    FirebaseDebugUtils.testNotificationChannels(this)
    
    // Handle notification intents
    handleNotificationIntent(intent)
}
```

## 📊 **Debugging & Monitoring**

### **LogCat Tags untuk Monitoring:**
```kotlin
// Firebase status
TAG = "FIREBASE_DEBUG"    // Firebase initialization & status
TAG = "FCM_SERVICE"       // FCM message handling
TAG = "MAP_NOTIFICATION"  // Map notification processing

// Backend integration
TAG = "BACKEND_MONITOR"   // Backend connection status
TAG = "HTTP_REQUEST"      // API call monitoring
TAG = "API_REQUEST"       // Request/response logging
```

### **Debug Commands:**
```kotlin
// Test Firebase status
FirebaseDebugUtils.logFirebaseStatus(context)

// Test notification channels
FirebaseDebugUtils.testNotificationChannels(context)

// Test backend integration
FirebaseDebugUtils.logBackendIntegration(isOnline, responseTime, fcmTokenSynced)

// Test map notification flow
FirebaseDebugUtils.logMapNotificationActivity("review_added", placeName, "scan_qr", true)
```

### **LogCat Output Examples:**
```
🔥 Firebase berhasil diinisialisasi
📱 App name: [DEFAULT]
🆔 Project ID: sako-cultural-app
🎯 FCM Token available: AIzaSyBo8h7...
📨 FCM Message received from: 24983268260
⭐ Review notification: John reviewed Jam Gadang (5 stars)
✅ Notification channels: 3 channels created
🌐 Backend: ✅ ONLINE (Response time: 250ms)
```

## 🔄 **Integration Testing**

### **Test Scenarios:**
1. **Registration/Login** → FCM token sent to backend
2. **Add Review** → Backend sends review_added notification
3. **Scan QR Code** → Backend sends place_visited notification
4. **Notification Settings** → Update preferences in backend
5. **App Foreground/Background** → Notifications handled correctly

### **Test Commands di Android Studio:**
```bash
# Test FCM via ADB
adb shell am start -n com.sako/.ui.MainActivity \
  -e navigation_target PlaceDetailScreen \
  -e place_id TP001 \
  -e place_name "Jam Gadang"
```

## 💾 **Data Models Sesuai Backend**

### **Notification Data (dari Backend):**
```kotlin
// Review Added Notification
data = {
    "type": "review_added",
    "module": "map", 
    "tourist_place_id": "TP001",
    "place_name": "Jam Gadang",
    "user_name": "John Doe",
    "rating": "5",
    "screen": "PlaceDetailScreen"
}

// Place Visited Notification  
data = {
    "type": "place_visited",
    "module": "map",
    "tourist_place_id": "TP001", 
    "place_name": "Jam Gadang",
    "user_name": "John Doe",
    "qr_code_value": "SAKO-TP001-BKT",
    "screen": "AddReviewScreen"
}
```

## ✅ **Integration Checklist**

- [✅] Firebase project configured dengan project ID yang benar
- [✅] FCMService handles notification types (review_added, place_visited)
- [✅] FCM token automatically synced dengan backend saat login/register
- [✅] Notification channels created sesuai backend specification
- [✅] MapRepository trigger notifications setelah review/QR scan
- [✅] Debug utilities untuk monitoring di logcat Android Studio
- [✅] Notification preferences management
- [✅] Intent handling untuk navigation dari notification
- [✅] Error handling dan logging comprehensive
- [✅] Backend integration dengan mapNotifikasiController.js

**🚀 Firebase integration siap digunakan dengan monitoring lengkap di logcat!**