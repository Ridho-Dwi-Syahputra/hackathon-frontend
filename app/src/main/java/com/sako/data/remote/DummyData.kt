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
                    name = "Sejarah Minangkabau",
                    description = "Pelajari sejarah Minangkabau di Sumatera Barat dan pentingnya pelestarian.",
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
                    name = "Kesenian",
                    description = "Mengenali kesenian yang indah dari Sumatera Barat",
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
                    name = "Geografi dan Wisata Budaya",
                    description = "Seperti apa ya geografis dan karakteristik wilayah Sumatera Barat?",
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
                    name = "Adat dan Filosofi Hidup",
                    description = "Pelajari adat istiadat Minangkabau di Sumatera Barat.",
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
                        text = "Siapa itu Angku dalam Budaya Minang?",
                        pointsCorrect = 10,
                        displayOrder = 1,
                        pointsWrong = 0,
                        options = listOf(
                            OptionItem(id = "opt1", label = "A", text = "Pemimpin Pasukuan", displayOrder = 1),
                            OptionItem(id = "opt2", label = "B", text = "Anak dari Mamak", displayOrder = 2),
                            OptionItem(id = "opt3", label = "C", text = "Kemenakan Ibu", displayOrder = 3),
                            OptionItem(id = "opt4", label = "D", text = "Saudara Ayah", displayOrder = 4)
                        )
                    ),
                    QuestionItem(
                        id = "q2",
                        text = "Yang bukan Sumbang Duo Baleh",
                        pointsCorrect = 10,
                        displayOrder = 2,
                        pointsWrong = 0,
                        options = listOf(
                            OptionItem(id = "opt5", label = "A", text = "Duduk di depan pintu saat makan", displayOrder = 1),
                            OptionItem(id = "opt6", label = "B", text = "Berbicara dengan kasar", displayOrder = 2),
                            OptionItem(id = "opt7", label = "C", text = "Menggunakan pakaian sopan", displayOrder = 3),
                            OptionItem(id = "opt8", label = "D", text = "Berteriak-teriak ketika memanggil orang tua", displayOrder = 4)
                        )
                    ),
                    QuestionItem(
                        id = "q3",
                        text = "Sistem kekerabatan yang dianut oleh masyarakat Minangkabau adalah?",
                        pointsCorrect = 10,
                        displayOrder = 3,
                        pointsWrong = 0,
                        options = listOf(
                            OptionItem(id = "opt9", label = "A", text = "Matrilieneal", displayOrder = 1),
                            OptionItem(id = "opt10", label = "B", text = "Bilaterat", displayOrder = 2),
                            OptionItem(id = "opt11", label = "C", text = "Patrilineal", displayOrder = 3),
                            OptionItem(id = "opt12", label = "D", text = "Unilateral", displayOrder = 4)
                        )
                    ),
                    QuestionItem(
                        id = "q4",
                        text = "Upacara pengangkatan seorang pemimpin adat di Minangkabau disebut?",
                        pointsCorrect = 10,
                        displayOrder = 4,
                        pointsWrong = 0,
                        options = listOf(
                            OptionItem(id = "opt13", label = "A", text = "Baralek Gadang", displayOrder = 1),
                            OptionItem(id = "opt14", label = "B", text = "Batagak Gala", displayOrder = 2),
                            OptionItem(id = "opt15", label = "C", text = "Makan Bajamba", displayOrder = 3),
                            OptionItem(id = "opt16", label = "D", text = "Malewakan Gadang", displayOrder = 4)
                        )
                    ),
                    QuestionItem(
                        id = "q5",
                        text = "Dalam makan bajamba, apa yang harus dilakukan bagi anak yang lebih muda?",
                        pointsCorrect = 10,
                        displayOrder = 5,
                        pointsWrong = 0,
                        options = listOf(
                            OptionItem(id = "opt17", label = "A", text = "Mengambil makan terlebih dahulu", displayOrder = 1),
                            OptionItem(id = "opt18", label = "B", text = "Menyelesaikan makan lebih dahulu", displayOrder = 2),
                            OptionItem(id = "opt19", label = "C", text = "Mendahulukan yang tua", displayOrder = 3),
                            OptionItem(id = "opt20", label = "D", text = "Berdiri", displayOrder = 4)
                        )
                    )
                ),
                durationSeconds = 600,
                startedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())
            )
        )
    }
}
