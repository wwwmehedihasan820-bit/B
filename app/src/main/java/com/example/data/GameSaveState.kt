package com.example.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameSaveState(
    val companyName: String,
    val playerReputation: Int,
    val playerSatisfaction: Int,
    val playerCash: Double,
    val currentTurn: Int,
    val currentIndustry: IndustryType,
    val currentDifficulty: Difficulty,
    val employees: List<Employee> = emptyList(),
    val products: List<Product> = emptyList(),
    val competitors: List<Competitor> = emptyList(),
    val loans: List<Loan> = emptyList(),
    val properties: List<RealEstateProperty> = emptyList(),
    val stocksOwned: List<StockShare> = emptyList(),
    val isIpoDone: Boolean = false,
    val sharePercentOwned: Double = 100.0,
    val stockHistory: List<Double> = emptyList(),
    val currentStockPrice: Double = 10.0,
    val rdProgress: Double = 0.0,
    val marketBoomCycle: Double = 1.0,
    val marketCycleName: String = "Stable Growth",
    val taxRate: Double = 0.20,
    val taxOwed: Double = 0.0,
    val financialList: List<FinancialStatement> = emptyList()
)
