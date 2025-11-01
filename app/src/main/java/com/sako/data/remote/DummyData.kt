package com.sako.data.remote

import com.sako.data.remote.response.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dummy data untuk testing UI ketika backend tidak tersedia
 * Data ini akan digunakan sebagai fallback ketika API gagal connect
 */
object DummyData {

    /**
     * Dummy Categories
     */
    fun getDummyCategories(): CategoryListResponse {
        return CategoryListResponse(
            status = "success",
            message = "Dummy data - Backend tidak tersedia",
            data = listOf(
                CategoryItem(
                    id = "cat-1",
                    name = "Pengenalan Sako",
                    description = "Pelajari dasar-dasar tentang Sako, sejarah, dan pentingnya pelestarian.",
                    isActive = true,
                    displayOrder = 1,
                    progress = CategoryProgress(
                        percentCompleted = 40.0,
                        completedLevelsCount = 2,
                        totalLevelsCount = 5
                    )
                ),
                CategoryItem(
                    id = "cat-2",
                    name = "Flora & Fauna",
                    description = "Kenali keanekaragaman hayati yang ada di ekosistem Sako.",
                    isActive = true,
                    displayOrder = 2,
                    progress = CategoryProgress(
                        percentCompleted = 0.0,
                        completedLevelsCount = 0,
                        totalLevelsCount = 4
                    )
                ),
                CategoryItem(
                    id = "cat-3",
                    name = "Geografi Sako",
                    description = "Jelajahi lokasi geografis dan karakteristik wilayah Sako.",
                    isActive = true,
                    displayOrder = 3,
                    progress = CategoryProgress(
                        percentCompleted = 33.0,
                        completedLevelsCount = 1,
                        totalLevelsCount = 3
                    )
                ),
                CategoryItem(
                    id = "cat-4",
                    name = "Konservasi",
                    description = "Pelajari upaya konservasi dan perlindungan lingkungan Sako.",
                    isActive = true,
                    displayOrder = 4,
                    progress = CategoryProgress(
                        percentCompleted = 0.0,
                        completedLevelsCount = 0,
                        totalLevelsCount = 6
                    )
                )
            )
        )
    }

    /**
     * Dummy Levels untuk kategori tertentu
     */
    fun getDummyLevels(categoryId: String): LevelListResponse {
        val allCategories = getDummyCategories().data
        val category = allCategories.firstOrNull { it.id == categoryId } ?: allCategories.first()

        return LevelListResponse(
            status = "success",
            message = "Dummy data - Backend tidak tersedia",
            data = LevelListData(
                category = category,
                levels = listOf(
                    LevelItem(
                        id = "level-1",
                        name = "Pengenalan Dasar",
                        description = "Level pengenalan untuk memulai perjalanan belajar",
                        timeLimitSeconds = 900,
                        baseXp = 100,
                        basePoints = 100,
                        maxQuestions = 10,
                        displayOrder = 1,
                        progress = LevelProgress(
                            status = "completed",
                            bestPercentCorrect = 85.0,
                            bestScorePoints = 85,
                            totalAttempts = 2
                        ),
                        passConditionType = "percentage",
                        passThreshold = 70.0
                    ),
                    LevelItem(
                        id = "level-2",
                        name = "Tingkat Lanjut",
                        description = "Tantangan lebih sulit untuk menguji pemahaman",
                        timeLimitSeconds = 1200,
                        baseXp = 150,
                        basePoints = 150,
                        maxQuestions = 15,
                        displayOrder = 2,
                        progress = LevelProgress(
                            status = "in_progress",
                            bestPercentCorrect = 60.0,
                            bestScorePoints = 90,
                            totalAttempts = 1
                        ),
                        passConditionType = "percentage",
                        passThreshold = 75.0
                    ),
                    LevelItem(
                        id = "level-3",
                        name = "Level Mahir",
                        description = "Uji kemampuan dengan soal-soal kompleks",
                        timeLimitSeconds = 1500,
                        baseXp = 200,
                        basePoints = 200,
                        maxQuestions = 20,
                        displayOrder = 3,
                        progress = LevelProgress(
                            status = "unstarted",
                            bestPercentCorrect = 0.0,
                            bestScorePoints = 0,
                            totalAttempts = 0
                        ),
                        passConditionType = "percentage",
                        passThreshold = 80.0
                    ),
                    LevelItem(
                        id = "level-4",
                        name = "Level Expert",
                        description = "Level tertinggi untuk master pembelajar",
                        timeLimitSeconds = 1800,
                        baseXp = 300,
                        basePoints = 300,
                        maxQuestions = 25,
                        displayOrder = 4,
                        progress = LevelProgress(
                            status = "locked",
                            bestPercentCorrect = 0.0,
                            bestScorePoints = 0,
                            totalAttempts = 0
                        ),
                        passConditionType = "percentage",
                        passThreshold = 85.0
                    )
                )
            )
        )
    }

    /**
     * Dummy Quiz untuk level tertentu
     */
    fun getDummyQuiz(levelId: String): QuizStartResponse {
        val allLevels = getDummyLevels("cat-1").data.levels
        val level = allLevels.firstOrNull { it.id == levelId } ?: allLevels.first()

        return QuizStartResponse(
            status = "success",
            message = "Dummy data - Backend tidak tersedia",
            data = QuizAttemptData(
                attemptId = "attempt-dummy-${System.currentTimeMillis()}",
                level = level,
                questions = listOf(
                    QuestionItem(
                        id = "q1",
                        text = "Apa kepanjangan dari SAKO?",
                        pointsCorrect = 10,
                        displayOrder = 1,
                        pointsWrong = 0,
                        options = listOf(
                            OptionItem(id = "opt1", label = "A", text = "Suaka Alam Kota", displayOrder = 1),
                            OptionItem(id = "opt2", label = "B", text = "Suaka Alam Konservasi Orangutan", displayOrder = 2),
                            OptionItem(id = "opt3", label = "C", text = "Sistem Aplikasi Konservasi", displayOrder = 3),
                            OptionItem(id = "opt4", label = "D", text = "Sentra Alam Konservasi", displayOrder = 4)
                        )
                    ),
                    QuestionItem(
                        id = "q2",
                        text = "Di mana lokasi SAKO berada?",
                        pointsCorrect = 10,
                        displayOrder = 2,
                        pointsWrong = 0,
                        options = listOf(
                            OptionItem(id = "opt5", label = "A", text = "Sumatera Utara", displayOrder = 1),
                            OptionItem(id = "opt6", label = "B", text = "Kalimantan Barat", displayOrder = 2),
                            OptionItem(id = "opt7", label = "C", text = "Sumatera Selatan", displayOrder = 3),
                            OptionItem(id = "opt8", label = "D", text = "Kalimantan Timur", displayOrder = 4)
                        )
                    ),
                    QuestionItem(
                        id = "q3",
                        text = "Fauna apa yang menjadi fokus konservasi di SAKO?",
                        pointsCorrect = 10,
                        displayOrder = 3,
                        pointsWrong = 0,
                        options = listOf(
                            OptionItem(id = "opt9", label = "A", text = "Harimau Sumatera", displayOrder = 1),
                            OptionItem(id = "opt10", label = "B", text = "Orangutan", displayOrder = 2),
                            OptionItem(id = "opt11", label = "C", text = "Gajah Sumatera", displayOrder = 3),
                            OptionItem(id = "opt12", label = "D", text = "Badak Jawa", displayOrder = 4)
                        )
                    ),
                    QuestionItem(
                        id = "q4",
                        text = "Berapa luas wilayah SAKO?",
                        pointsCorrect = 10,
                        displayOrder = 4,
                        pointsWrong = 0,
                        options = listOf(
                            OptionItem(id = "opt13", label = "A", text = "500 hektar", displayOrder = 1),
                            OptionItem(id = "opt14", label = "B", text = "1.000 hektar", displayOrder = 2),
                            OptionItem(id = "opt15", label = "C", text = "2.400 hektar", displayOrder = 3),
                            OptionItem(id = "opt16", label = "D", text = "5.000 hektar", displayOrder = 4)
                        )
                    ),
                    QuestionItem(
                        id = "q5",
                        text = "Apa tujuan utama dari SAKO?",
                        pointsCorrect = 10,
                        displayOrder = 5,
                        pointsWrong = 0,
                        options = listOf(
                            OptionItem(id = "opt17", label = "A", text = "Pariwisata", displayOrder = 1),
                            OptionItem(id = "opt18", label = "B", text = "Konservasi dan Penelitian", displayOrder = 2),
                            OptionItem(id = "opt19", label = "C", text = "Perkebunan", displayOrder = 3),
                            OptionItem(id = "opt20", label = "D", text = "Pemukiman", displayOrder = 4)
                        )
                    )
                ),
                durationSeconds = 600,
                startedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())
            )
        )
    }
}
