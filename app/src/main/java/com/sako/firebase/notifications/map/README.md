# 🗂️ FIREBASE NOTIFICATIONS MAP FOLDER STRUCTURE

## 📁 **STRUKTUR FOLDER FIREBASE YANG SUDAH DIBUAT:**

```
app/src/main/java/com/sako/firebase/
├── FirebaseHelper.kt                     ✅ Main Firebase utility
├── SakoFirebaseMessagingService.kt       ✅ FCM service (registered in AndroidManifest)
└── notifications/
    └── map/
        ├── MapNotificationHandler.kt      ✅ Handles map notification processing
        ├── MapNotificationManager.kt      ✅ Manages subscriptions and preferences  
        └── MapNotificationPreferencesManager.kt ✅ User notification preferences
```

---

## 🎯 **FUNGSI SETIAP KOMPONEN:**

### 1. **SakoFirebaseMessagingService.kt** (FCM Service Utama)
```kotlin
class SakoFirebaseMessagingService : FirebaseMessagingService() {
    // 🔄 Handle FCM token refresh untuk backend
    override fun onNewToken(token: String)
    
    // 📨 Handle incoming notifications dari backend
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Deteksi jika module="map"
        if (data["module"] == "map") {
            handleMapNotification() // Redirect ke map handler
        }
    }
    
    // 🗺️ Process map notifications dengan preferences check
    private fun handleMapNotification(data, notification) {
        // Check user preferences first
        // Process melalui MapNotificationHandler
        // Show notification dengan navigation data
    }
}
```

### 2. **MapNotificationHandler.kt** (Processor Map Notifications)
```kotlin
object MapNotificationHandler {
    // 📍 Main processor untuk map notifications
    fun processMapNotification(context, data): Boolean {
        when (data["type"]) {
            "review_added" -> handleReviewAddedNotification()
            "place_visited" -> handlePlaceVisitedNotification()
        }
    }
    
    // 🎨 Create notification title & body
    fun createNotificationContent(type, data): Pair<String, String>
    
    // 🧭 Get navigation data untuk notification click
    fun getNavigationData(type, data): Map<String, String>
}
```

### 3. **MapNotificationManager.kt** (Subscription & Coordination)
```kotlin
class MapNotificationManager {
    // 🚀 Initialize pada app start
    fun initializeMapNotifications()
    
    // 📡 FCM topic management
    private fun subscribeToMapTopics()   // Subscribe berdasarkan preferences
    private fun unsubscribeFromMapTopics() // Unsubscribe semua topics
    
    // ⚙️ Preference controls
    fun setReviewNotificationsEnabled(enabled: Boolean)
    fun setVisitNotificationsEnabled(enabled: Boolean)
    fun setMapNotificationsEnabled(enabled: Boolean)
    
    // ✅ Check if notification should be processed
    fun shouldProcessNotification(type: String): Boolean
}
```

### 4. **MapNotificationPreferencesManager.kt** (User Preferences)
```kotlin
class MapNotificationPreferencesManager {
    // ✅ Check preferences
    fun areReviewNotificationsEnabled(): Boolean
    fun areVisitNotificationsEnabled(): Boolean
    fun areMapNotificationsEnabled(): Boolean
    
    // ⚙️ Set preferences  
    fun setReviewNotificationsEnabled(enabled: Boolean)
    fun setVisitNotificationsEnabled(enabled: Boolean)
    fun setMapNotificationsEnabled(enabled: Boolean)
    
    // 🎯 Main check function
    fun shouldShowNotification(type: String): Boolean
}
```

---

## 🔄 **INTEGRASI FLOW LENGKAP:**

### **1. Initialization Flow (App Start):**
```
SakoApplication.onCreate() →
├── FirebaseHelper.initialize()
├── MapNotificationManager.getInstance()
└── mapNotificationManager.initializeMapNotifications()
    ├── preferencesManager.logCurrentPreferences()
    └── subscribeToMapTopics() (jika enabled)
        ├── "map_notifications"
        ├── "map_review_notifications" 
        └── "map_visit_notifications"
```

### **2. Incoming Notification Flow:**
```
FCM Message received →
SakoFirebaseMessagingService.onMessageReceived() →
├── Check if data["module"] == "map" →
└── handleMapNotification()
    ├── Check MapNotificationManager.shouldProcessNotification()
    ├── MapNotificationHandler.processMapNotification()
    ├── Create title/body dengan MapNotificationHandler.createNotificationContent()
    ├── Add navigation data dengan MapNotificationHandler.getNavigationData()
    └── showNotification() dengan enhanced data
```

### **3. Backend Integration Points:**
```javascript
// Backend mengirim notification dengan format:
{
  "module": "map",
  "type": "review_added", // atau "place_visited"
  "place_name": "Benteng Kuto Besak",
  "place_id": "TP001",
  "user_name": "John Doe",
  "rating": "5", // untuk review
  "review_id": "R001", // untuk review
  "qr_code_value": "SAKO-TP001", // untuk visit
  "visit_id": "UV001" // untuk visit
}
```

---

## 📱 **FCM TOPICS YANG DIGUNAKAN:**

1. **`map_notifications`** - Topic umum untuk semua notifikasi map
2. **`map_review_notifications`** - Khusus untuk review notifications
3. **`map_visit_notifications`** - Khusus untuk visit notifications

**User bisa control individual subscriptions berdasarkan preferences.**

---

## ✅ **YANG SUDAH TERINTEGRASI:**

1. **FCM Service Registration** ✅ - `SakoFirebaseMessagingService` di AndroidManifest
2. **Token Management** ✅ - Auto-refresh dan send ke backend via AuthRepository
3. **Map Notification Processing** ✅ - Dedicated handler untuk map events
4. **User Preferences** ✅ - Granular control untuk review/visit notifications
5. **Topic Subscription** ✅ - Auto-subscribe berdasarkan preferences
6. **Navigation Integration** ✅ - Navigation data untuk notification clicks
7. **Backend Format Support** ✅ - Sesuai dengan format dari COMPLETE_AUTH_MAP_DOCUMENTATION.md

---

## 🚀 **CARA MENGGUNAKAN:**

### **Test Notification dari Firebase Console:**
```json
{
  "to": "/topics/map_notifications",
  "data": {
    "module": "map",
    "type": "review_added",
    "place_name": "Test Place",
    "place_id": "TP001",
    "user_name": "Test User",
    "rating": "5",
    "review_id": "R001"
  },
  "notification": {
    "title": "Review Ditambahkan",
    "body": "Test User menambahkan review 5 bintang untuk Test Place"
  }
}
```

### **Control Preferences (dalam Settings UI):**
```kotlin
val mapManager = MapNotificationManager.getInstance(context)

// Enable/disable semua map notifications
mapManager.setMapNotificationsEnabled(true)

// Enable/disable review notifications saja
mapManager.setReviewNotificationsEnabled(false)

// Enable/disable visit notifications saja
mapManager.setVisitNotificationsEnabled(true)
```

**✅ Map notifications sudah fully integrated dan ready untuk production testing!** 🚀