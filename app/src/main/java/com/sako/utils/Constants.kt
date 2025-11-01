package com.sako.utils

object Constants {

    // Video Categories
    const val VIDEO_CATEGORY_KESENIAN = "Kesenian"
    const val VIDEO_CATEGORY_KULINER = "Kuliner"
    const val VIDEO_CATEGORY_ADAT = "Adat"
    const val VIDEO_CATEGORY_WISATA = "Wisata"

    // User Status
    const val USER_STATUS_ACTIVE = "active"
    const val USER_STATUS_INACTIVE = "inactive"
    const val USER_STATUS_BANNED = "banned"

    // Level Progress Status
    const val LEVEL_STATUS_LOCKED = "locked"
    const val LEVEL_STATUS_UNSTARTED = "unstarted"
    const val LEVEL_STATUS_IN_PROGRESS = "in_progress"
    const val LEVEL_STATUS_COMPLETED = "completed"

    // Quiz Attempt Status
    const val QUIZ_STATUS_IN_PROGRESS = "in_progress"
    const val QUIZ_STATUS_SUBMITTED = "submitted"
    const val QUIZ_STATUS_EXPIRED = "expired"
    const val QUIZ_STATUS_ABORTED = "aborted"

    // Visit Status
    const val VISIT_STATUS_VISITED = "visited"
    const val VISIT_STATUS_NOT_VISITED = "not_visited"

    // Location Validation (in meters)
    const val MAX_CHECKIN_DISTANCE = 100

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val INITIAL_PAGE = 1

    // Image Upload
    const val MAX_IMAGE_SIZE_MB = 5
    const val MAX_IMAGE_SIZE_BYTES = MAX_IMAGE_SIZE_MB * 1024 * 1024

    // Date Format
    const val DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy HH:mm"
    const val DATE_FORMAT_SIMPLE = "dd MMM yyyy"
}