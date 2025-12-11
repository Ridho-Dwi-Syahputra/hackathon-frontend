# 🔐 AUTHENTICATION TROUBLESHOOTING GUIDE

## ❌ **MASALAH YANG DITEMUKAN**

### **Issue:** Token Authentication Failure pada Akses Map Module
```
AUTH_INTERCEPTOR: Session - Login: true, AccessToken: ..., DatabaseToken: ...
AUTH_INTERCEPTOR: ⚠️ No token available for http://10.0.2.2:5000/api/map/places?page=1  
Backend Response: {"success":false,"message":"Token tidak ditemukan"}
HTTP Status: 401 Unauthorized
```

### **Root Cause Analysis:**
1. **User berhasil login** ✅ → Session tersimpan dengan `isLogin: true`
2. **Token tersimpan di session** ✅ → Ada `accessToken` dan `databaseToken`
3. **Auth Interceptor gagal attach token** ❌ → Token tidak dikirim ke backend
4. **Backend mengembalikan 401** ❌ → `"Token tidak ditemukan"`

---

## 🔍 **DEBUGGING STEPS YANG DILAKUKAN**

### **1. Analysis Backend Response Format**
**Backend login response (authController.js):**
```javascript
return successResponse(res, {
    token,  // ← Field utama yang dikirim backend
    user: {
        users_id: user.users_id,
        email: user.email,
        full_name: user.full_name,
        // ... other fields
    }
}, 'Login berhasil');
```

**Problem:** Android AuthResponse tidak sesuai dengan backend response structure.

### **2. Analysis Android AuthResponse Mapping**
**SEBELUM PERBAIKAN:**
```kotlin
data class AuthData(
    @SerializedName("access_token") // ← Field ini tidak ada di backend response
    val accessToken: String? = null,
    
    @SerializedName("token")        // ← Field ini ada tapi jadi prioritas kedua
    val token: String? = null,
    
    @SerializedName("database_token") 
    val databaseToken: String? = null
)
```

**SETELAH PERBAIKAN:**
```kotlin
data class AuthData(
    @SerializedName("token")        // ← Field utama dari backend (prioritas 1)
    val token: String? = null,
    
    @SerializedName("access_token") // ← Fallback field (prioritas 2)
    val accessToken: String? = null,
    
    @SerializedName("database_token") 
    val databaseToken: String? = null
)
```

### **3. Enhanced Debug Logging**
**AUTH_INTERCEPTOR Debug Enhancement:**
```kotlin
android.util.Log.d("AUTH_INTERCEPTOR", "🔐 Session Details:")
android.util.Log.d("AUTH_INTERCEPTOR", "   - Login Status: ${userSession.isLogin}")
android.util.Log.d("AUTH_INTERCEPTOR", "   - AccessToken: '${userSession.accessToken}' (length: ${userSession.accessToken.length})")
android.util.Log.d("AUTH_INTERCEPTOR", "   - DatabaseToken: '${userSession.databaseToken}' (length: ${userSession.databaseToken.length})")
```

**AUTH_DEBUG Repository Enhancement:**
```kotlin
android.util.Log.d("AUTH_DEBUG", "🔑 Login Response Details:")
android.util.Log.d("AUTH_DEBUG", "   - Token from 'token' field: ${authData.token ?: "null"}")
android.util.Log.d("AUTH_DEBUG", "   - Token from 'access_token' field: ${authData.accessToken ?: "null"}")
android.util.Log.d("AUTH_DEBUG", "   - Final selected token: '$token' (length: ${token.length})")
```

---

## ✅ **SOLUSI YANG DITERAPKAN**

### **1. Fixed AuthResponse Data Class**
**File:** `AuthResponse.kt`
```kotlin
data class AuthData(
    @SerializedName("user")
    val user: UserData,

    @SerializedName("token") // ← PRIMARY: Field utama dari backend
    val token: String? = null,
    
    @SerializedName("access_token") // ← FALLBACK: Jika backend menggunakan field ini
    val accessToken: String? = null,
    
    @SerializedName("database_token") // ← OPTIONAL: Untuk auto-login
    val databaseToken: String? = null,
    
    @SerializedName("expires_in")
    val expiresIn: Int? = null
)
```

### **2. Enhanced Token Mapping Logic**
**File:** `AuthRepository.kt`
```kotlin
val token = authData.token ?: authData.accessToken ?: ""

// Validation and logging
if (token.isEmpty()) {
    android.util.Log.e("AUTH_DEBUG", "❌ CRITICAL: No token received from server!")
}
```

### **3. Improved Auth Interceptor Validation**
**File:** `ApiConfig.kt`
```kotlin
when {
    userSession.accessToken.isNotEmpty() && userSession.accessToken.isNotBlank() -> {
        addHeader("Authorization", "Bearer ${userSession.accessToken}")
        android.util.Log.d("AUTH_INTERCEPTOR", "📤 Using access token for ${req.url}")
    }
    userSession.databaseToken.isNotEmpty() && userSession.databaseToken.isNotBlank() -> {
        addHeader("Authorization", "Bearer ${userSession.databaseToken}")
        android.util.Log.d("AUTH_INTERCEPTOR", "📤 Using database token for ${req.url}")
    }
    else -> {
        android.util.Log.w("AUTH_INTERCEPTOR", "⚠️ No valid token available for ${req.url}")
        // Enhanced debugging output
    }
}
```

---

## 🧪 **TESTING & VERIFICATION**

### **Expected Debug Output (SETELAH PERBAIKAN):**
```
AUTH_DEBUG: 🔑 Login Response Details:
AUTH_DEBUG:    - Token from 'token' field: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
AUTH_DEBUG:    - Token from 'access_token' field: null
AUTH_DEBUG:    - Final selected token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...' (length: 157)

AUTH_DEBUG: 💾 Saving session with:
AUTH_DEBUG:    - AccessToken: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...' (length: 157)
AUTH_DEBUG:    - Login status: true

AUTH_INTERCEPTOR: 🔐 Session Details:
AUTH_INTERCEPTOR:    - Login Status: true
AUTH_INTERCEPTOR:    - AccessToken: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...' (length: 157)
AUTH_INTERCEPTOR: 📤 Using access token for http://10.0.2.2:5000/api/map/places?page=1

HTTP Response: 200 OK ✅
```

### **Testing Steps:**
1. **Bersihkan session lama:** Logout atau clear app data
2. **Login ulang:** Gunakan kredensial yang valid
3. **Monitor logs:** Perhatikan debug output di logcat
4. **Test akses map:** Coba buka halaman map untuk test authentication
5. **Verify response:** Pastikan mendapat response 200 OK, bukan 401

---

## 📝 **LESSONS LEARNED**

### **1. Backend-Frontend Response Contract**
- **Selalu sesuaikan data class dengan exact response format dari backend**
- **Jangan asumsikan field name tanpa verifikasi dengan backend code**
- **Gunakan priority order untuk field mapping yang fleksibel**

### **2. Debug Logging Strategy**
- **Tambahkan detailed logging untuk troubleshooting authentication**
- **Log token length dan presence, jangan log full token (security risk)**
- **Gunakan structured logging dengan prefix untuk mudah filtering**

### **3. Auth Interceptor Best Practices**
- **Validasi token presence DAN content (not empty, not blank)**
- **Provide fallback logic untuk multiple token types**
- **Enhanced error reporting untuk troubleshooting**

---

## 🚀 **NEXT STEPS**

### **1. Production Considerations**
- **Remove debug logs yang detail untuk production build**
- **Implement proper token refresh mechanism**
- **Add token expiry handling dengan auto-refresh**

### **2. Security Enhancements**
- **Implement secure token storage (Android Keystore)**
- **Add token validation before sending request**
- **Implement proper logout dan token cleanup**

### **3. Monitoring & Alerting**
- **Add analytics untuk authentication failure rates**
- **Monitor token usage patterns**
- **Alert untuk high authentication failure rates**

---

## ⚠️ **COMMON PITFALLS TO AVOID**

1. **Field Name Mismatch:** Always check backend response structure
2. **Empty Token Validation:** Check both `isEmpty()` and `isBlank()`
3. **Debug Log Security:** Never log full tokens in production
4. **Session State Inconsistency:** Ensure session state matches auth state
5. **Network Request Headers:** Verify Authorization header format (`Bearer {token}`)