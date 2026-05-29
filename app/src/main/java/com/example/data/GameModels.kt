package com.example.data

import com.squareup.moshi.JsonClass
import java.util.UUID

enum class Difficulty(val label: String, val startingCash: Double, val loanInterest: Double, val description: String) {
    EASY("Startup Genius", 150000.0, 0.08, "High starting capital, low-interest loans. Perfect for learning."),
    MEDIUM("Venture Hustler", 60000.0, 0.12, "Moderate starting money. Real competition challenges."),
    HARD("Bootstrapped Maverick", 15000.0, 0.18, "Tiny capital. High risk of bankruptcy. Only for strategic masters.")
}

enum class IndustryType(val label: String, val baseRDRequirement: Double, val baseMarketingImpact: Double, val desc: String, val iconName: String) {
    TECH("Silicon Tech & AI", 80.0, 60.0, "High margins, heavy R&D requirement. High-risk, rapid returns.", "Memory"),
    CARS("Electrified Automotive", 150.0, 40.0, "Extremely expensive factory startups. Bulky products but huge prestige.", "DirectionsCar"),
    FASHION("Luxury Apparel", 30.0, 95.0, "Low development cost, highly reputation & marketing dependent.", "Checkroom"),
    RESTAURANTS("Gastronomic Chains", 20.0, 50.0, "Highly stable cashflows, staff dependency, low individual margins.", "Restaurant"),
    GAMING("Immersive Interactive Gaming", 50.0, 70.0, "Highly creative, extreme risk-reward volatility. Hit-driven.", "SportsEsports"),
    REAL_ESTATE("Commercial Property Empire", 10.0, 30.0, "Requires massive capital, generates continuous rent cash flows.", "Business")
}

@JsonClass(generateAdapter = true)
data class Employee(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String, // "Developer", "Researcher", "Marketer", "Salesperson", "Manager"
    val skill: Int, // 1 - 100
    val salary: Double, // Monthly wage cost
    val moral: Int = 80 // 1 - 100
)

@JsonClass(generateAdapter = true)
data class ProductTemplate(
    val name: String,
    val devTimeTurns: Int,
    val baseMaterialCost: Double,
    val targetAudience: String
)

@JsonClass(generateAdapter = true)
data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quality: Int, // 1 - 100
    val costPerUnit: Double,
    val retailPrice: Double,
    var inventory: Int = 0,
    var totalSold: Int = 0,
    val marketingInvestment: Double = 0.0,
    val demandIndex: Int = 50 // 0-100 indicating consumer affinity
)

@JsonClass(generateAdapter = true)
data class Competitor(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val industry: IndustryType,
    var cash: Double,
    var marketShare: Double, // percentage 0-100
    var productQuality: Int,
    var stockPrice: Double,
    val stockHistory: List<Double> = listOf(stockPrice),
    val description: String
)

@JsonClass(generateAdapter = true)
data class RealEstateProperty(
    val id: String,
    val name: String,
    val location: String, // "Suburbs", "Tech-Valley", "Downtown"
    val price: Double,
    val capacityBonus: Int, // Employee slots
    val passiveIncome: Double, // Rent collected per turn
    val marketMultiplier: Double, // Increases company prestige/valuation
    var isOwned: Boolean = false
)

@JsonClass(generateAdapter = true)
data class Loan(
    val id: String = UUID.randomUUID().toString(),
    val principal: Double,
    val interestRate: Double,
    var remainingTurns: Int,
    val startingPrincipal: Double
)

@JsonClass(generateAdapter = true)
data class GameEventOption(
    val text: String,
    val effectDescription: String,
    val actionEffect: (GameStateModifier) -> Unit
)

// A token to safely pass and mutate parameters in lambda functions without direct state leaking
data class GameStateModifier(
    var cashDiff: Double = 0.0,
    var reputationDiff: Int = 0,
    var satisfactionDiff: Int = 0,
    var rdProgressDiff: Double = 0.0,
    var stockPriceDiff: Double = 0.0,
    val logMessage: String = ""
)

data class GameEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val isDecision: Boolean,
    val options: List<GameEventOption> = emptyList()
)

@JsonClass(generateAdapter = true)
data class StockShare(
    val tickerSymbol: String,
    val companyName: String,
    var quantityOwned: Int,
    var averagePurchasePrice: Double
)

@JsonClass(generateAdapter = true)
data class FinancialStatement(
    val turn: Int,
    val cashRevenue: Double,
    val payrollExpense: Double,
    val rentExpense: Double,
    val marketingExpense: Double,
    val rdExpense: Double,
    val rawMaterialsExpense: Double,
    val loanRepaymentExpense: Double,
    val investmentProfit: Double,
    val taxesPaid: Double,
    val netIncome: Double
)

@JsonClass(generateAdapter = true)
data class LeaderboardItem(
    val name: String,
    val corporateWorth: Double,
    val isPlayer: Boolean
)
