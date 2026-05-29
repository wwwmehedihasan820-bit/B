package com.example.data

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.BuildConfig

@JsonClass(generateAdapter = true)
data class GeminiPart(val text: String)

@JsonClass(generateAdapter = true)
data class GeminiContent(val parts: List<GeminiPart>)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: GeminiApiService = retrofit.create(GeminiApiService::class.java)

    fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    fun isApiKeyAvailable(): Boolean {
        val key = getApiKey()
        return key.isNotEmpty() && key != "MY_GEMINI_API_KEY"
    }
}

class GeminiEventGenerator {
    suspend fun generateBoardroomEvent(
        companyName: String,
        industry: String,
        cash: Double,
        reputation: Int,
        satisfaction: Int
    ): GeneratedBoardroomEvent {
        if (!GeminiClient.isApiKeyAvailable()) {
            return generateMockBoardroomEvent(companyName, industry, cash)
        }

        val prompt = """
            You are a system generator for an immersive Billionaire Corporate Strategy Android game.
            Create a highly dramatic, realistic corporate negotiation, crisis, lawsuit, or partnership event for:
            Company: "$companyName"
            Industry: "$industry"
            Treasury: ${'$'}${String.format("%,.2f", cash)}
            Reputation Meter: $reputation/100
            Customer Satisfaction: $satisfaction/100

            Include a detailed crisis/deal scenario and 3 highly strategic choices with calculated outcomes.
            You MUST return ONLY a JSON block adhering strictly to the schema below. Do not add markdown backticks like ```json. Output exactly as requested.
            Schema:
            {
              "crisisTitle": "String describing the title",
              "scenarioText": "String with narrative explaining the situation (150-300 chars)",
              "choice1": {
                "text": "Action prompt for choice 1",
                "impactText": "Estimated impact summary text",
                "cashImpact": -25000.0,
                "reputationImpact": 15,
                "satisfactionImpact": 5
              },
              "choice2": {
                "text": "Action prompt for choice 2",
                "impactText": "Estimated impact summary text",
                "cashImpact": 50000.0,
                "reputationImpact": -10,
                "satisfactionImpact": -5
              },
              "choice3": {
                "text": "Action prompt for choice 3",
                "impactText": "Estimated impact summary text",
                "cashImpact": 0.0,
                "reputationImpact": 0,
                "satisfactionImpact": 0
              }
            }
        """.trimIndent()

        return try {
            val request = GeminiRequest(listOf(GeminiContent(listOf(GeminiPart(prompt)))))
            val response = GeminiClient.apiService.generateContent(GeminiClient.getApiKey(), request)
            val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            
            // Clean markdown blocks if Gemini outputs them
            val cleanedText = generatedText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(GeneratedBoardroomEvent::class.java)
            adapter.fromJson(cleanedText) ?: generateMockBoardroomEvent(companyName, industry, cash)
        } catch (e: Exception) {
            e.printStackTrace()
            generateMockBoardroomEvent(companyName, industry, cash)
        }
    }

    private fun generateMockBoardroomEvent(companyName: String, industry: String, cash: Double): GeneratedBoardroomEvent {
        val lists = listOf(
            GeneratedBoardroomEvent(
                crisisTitle = "Venture Equity Takeover",
                scenarioText = "A powerful vulture capital firm, Apollo Peak, claims to have acquired a 12% hostile position in $companyName. They demand a seat on the board of directors and immediate restructuring.",
                choice1 = ChoiceDetail("Reject Demands & Launch Poison Pill defensive share purchase", "Cost: $40,000 | Stock Price +15% | Reputation +10", -40000.0, 10, 0),
                choice2 = ChoiceDetail("Negotiate & Offer Board Seat in exchange for cash infusion", "Cash +$100,000 | Rep +5 | Power is diluted slightly", 100000.0, 5, -5),
                choice3 = ChoiceDetail("Ignore Their Moves and let the lawyers handle it", "Cost: $10,000 | Satisfaction -5 | Unpredictable future", -10000.0, -5, -3)
            ),
            GeneratedBoardroomEvent(
                crisisTitle = "Intellectual Property Breach",
                scenarioText = "A major competitor has manufactured a prototype that looks suspiciously identical to $companyName's upcoming development roadmap. Your legal advisor recommends litigation.",
                choice1 = ChoiceDetail("File a Multi-million dollar Patent Lawsuit immediately", "Cost: $50,000 | Stock Price +10% | Rep +15", -50000.0, 15, 0),
                choice2 = ChoiceDetail("Negotiate a Cross-Licensing Agreement silently", "Cost: $15,000 | Satisfaction +15 | R&D Speed Boost", -15000.0, 5, 15),
                choice3 = ChoiceDetail("Ignore and double marketing spend to outpace them", "Cost: $30,000 | satisfaction +10", -30000.0, 2, 10)
            ),
            GeneratedBoardroomEvent(
                crisisTitle = "Greenwashing Outcry",
                scenarioText = "Environmental watchdog coalition claims your manufacturing practices are contributing to severe electronics waste. The stock price starts feeling the heat of boycott gossip.",
                choice1 = ChoiceDetail("Commit to 100% Zero-Carbon operations instantly", "Cost: $60,000 | Rep +25 | Satisfaction +20", -60000.0, 25, 20),
                choice2 = ChoiceDetail("Issue public relations media release countering claims", "Cost: $10,000 | Rep +5 | Public is divided", -10000.0, 5, 2),
                choice3 = ChoiceDetail("Do nothing. Prioritize pure short-term margins", "Cash +$20,000 | Rep -15 | Satisfaction -10", 20000.0, -15, -10)
            )
        )
        return lists.random()
    }
}

@JsonClass(generateAdapter = true)
data class GeneratedBoardroomEvent(
    val crisisTitle: String,
    val scenarioText: String,
    val choice1: ChoiceDetail,
    val choice2: ChoiceDetail,
    val choice3: ChoiceDetail
)

@JsonClass(generateAdapter = true)
data class ChoiceDetail(
    val text: String,
    val impactText: String,
    val cashImpact: Double,
    val reputationImpact: Int,
    val satisfactionImpact: Int
)
