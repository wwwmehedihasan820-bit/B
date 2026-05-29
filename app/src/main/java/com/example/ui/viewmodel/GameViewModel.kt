package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val savedGameDao = db.savedGameDao()
    private val eventGenerator = GeminiEventGenerator()

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val stateAdapter = moshi.adapter(GameSaveState::class.java)

    // UI States
    private val _isGameStarted = MutableStateFlow(false)
    val isGameStarted: StateFlow<Boolean> = _isGameStarted.asStateFlow()

    fun exitGame() {
        _isGameStarted.value = false
    }

    // Core Game States
    private val _companyName = MutableStateFlow("Startup LLC")
    val companyName: StateFlow<String> = _companyName.asStateFlow()

    private val _cash = MutableStateFlow(50000.0)
    val cash: StateFlow<Double> = _cash.asStateFlow()

    private val _reputation = MutableStateFlow(50) // 0 - 100
    val reputation: StateFlow<Int> = _reputation.asStateFlow()

    private val _satisfaction = MutableStateFlow(60) // 0 - 100
    val satisfaction: StateFlow<Int> = _satisfaction.asStateFlow()

    private val _currentTurn = MutableStateFlow(1) // Quarters
    val currentTurn: StateFlow<Int> = _currentTurn.asStateFlow()

    private val _currentIndustry = MutableStateFlow(IndustryType.TECH)
    val currentIndustry: StateFlow<IndustryType> = _currentIndustry.asStateFlow()

    private val _difficulty = MutableStateFlow(Difficulty.MEDIUM)
    val difficulty: StateFlow<Difficulty> = _difficulty.asStateFlow()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _competitors = MutableStateFlow<List<Competitor>>(emptyList())
    val competitors: StateFlow<List<Competitor>> = _competitors.asStateFlow()

    private val _loans = MutableStateFlow<List<Loan>>(emptyList())
    val loans: StateFlow<List<Loan>> = _loans.asStateFlow()

    private val _properties = MutableStateFlow<List<RealEstateProperty>>(emptyList())
    val properties: StateFlow<List<RealEstateProperty>> = _properties.asStateFlow()

    private val _stocksOwned = MutableStateFlow<List<StockShare>>(emptyList())
    val stocksOwned: StateFlow<List<StockShare>> = _stocksOwned.asStateFlow()

    private val _isIpoDone = MutableStateFlow(false)
    val isIpoDone: StateFlow<Boolean> = _isIpoDone.asStateFlow()

    private val _sharePercentOwned = MutableStateFlow(100.0)
    val sharePercentOwned: StateFlow<Double> = _sharePercentOwned.asStateFlow()

    private val _playerStockHistory = MutableStateFlow<List<Double>>(listOf(10.0))
    val playerStockHistory: StateFlow<List<Double>> = _playerStockHistory.asStateFlow()

    private val _playerStockPrice = MutableStateFlow(10.0)
    val playerStockPrice: StateFlow<Double> = _playerStockPrice.asStateFlow()

    private val _rdProgress = MutableStateFlow(0.0)
    val rdProgress: StateFlow<Double> = _rdProgress.asStateFlow()

    private val _marketBoomCycle = MutableStateFlow(1.0)
    val marketBoomCycle: StateFlow<Double> = _marketBoomCycle.asStateFlow()

    private val _marketCycleName = MutableStateFlow("Stable Growth")
    val marketCycleName: StateFlow<String> = _marketCycleName.asStateFlow()

    private val _taxOwed = MutableStateFlow(0.0)
    val taxOwed: StateFlow<Double> = _taxOwed.asStateFlow()

    private val _financials = MutableStateFlow<List<FinancialStatement>>(emptyList())
    val financials: StateFlow<List<FinancialStatement>> = _financials.asStateFlow()

    // Boardroom & AI Events
    private val _activeCrisis = MutableStateFlow<GeneratedBoardroomEvent?>(null)
    val activeCrisis: StateFlow<GeneratedBoardroomEvent?> = _activeCrisis.asStateFlow()

    private val _isGeneratingCrisis = MutableStateFlow(false)
    val isGeneratingCrisis: StateFlow<Boolean> = _isGeneratingCrisis.asStateFlow()

    // Event log
    private val _gameLogs = MutableStateFlow<List<String>>(listOf("Corporate empire tracking commenced. Let's build!"))
    val gameLogs: StateFlow<List<String>> = _gameLogs.asStateFlow()

    // Slot Save Listing State
    private val _availableSaves = MutableStateFlow<List<SavedGame>>(emptyList())
    val availableSaves: StateFlow<List<SavedGame>> = _availableSaves.asStateFlow()

    init {
        loadAvailableSaves()
        // Default property offerings
        _properties.value = createDefaultProperties()
    }

    private fun addLog(msg: String) {
        val current = _gameLogs.value.toMutableList()
        current.add(0, "[Q${_currentTurn.value}] $msg")
        _gameLogs.value = current.take(30)
    }

    private fun createDefaultProperties(): List<RealEstateProperty> {
        return listOf(
            RealEstateProperty("prop_office_sub", "Basement HQ Suboxone", "Suburbs", 10000.0, 5, 0.0, 1.05),
            RealEstateProperty("prop_office_valley", "Cyber Sandbox Garage", "Tech-Valley", 45000.0, 15, 0.0, 1.15),
            RealEstateProperty("prop_office_downtown", "Capital Peak Highrise", "Downtown", 180000.0, 50, 0.0, 1.35),
            RealEstateProperty("prop_com_skysc", "Billionaire Obsidian Skyscraper", "Downtown", 850000.0, 150, 15000.0, 2.10),
            RealEstateProperty("prop_factory_mod", "Autonomous Megafactory", "Tech-Valley", 350000.0, 0, 7500.0, 1.70),
            RealEstateProperty("prop_retail_store", "Aesthetic Prime Retail Node", "Downtown", 120000.0, 10, 2500.0, 1.25)
        )
    }

    fun initializeNewGame(name: String, industry: IndustryType, diff: Difficulty) {
        _companyName.value = name.takeIf { it.isNotBlank() } ?: "Empire Corp"
        _currentIndustry.value = industry
        _difficulty.value = diff
        _cash.value = diff.startingCash
        _reputation.value = 50
        _satisfaction.value = 60
        _currentTurn.value = 1
        _isIpoDone.value = false
        _sharePercentOwned.value = 100.0
        _playerStockPrice.value = 10.0
        _playerStockHistory.value = listOf(10.0)
        _rdProgress.value = 0.0
        _marketBoomCycle.value = 1.0
        _marketCycleName.value = "Stable Growth"
        _taxOwed.value = 0.0
        _employees.value = getInitialEmployees()
        _products.value = emptyList()
        _stocksOwned.value = emptyList()
        _financials.value = emptyList()
        _activeCrisis.value = null

        // Generate AI competitors for the match
        _competitors.value = generateCompetitors(industry)
        _isGameStarted.value = true
        _gameLogs.value = listOf("Game initialized successfully under difficulty '${diff.label}'!")

        saveGameSlot("autosave")
    }

    private fun getInitialEmployees(): List<Employee> {
        return listOf(
            Employee(name = "Chris Vance", role = "Developer", skill = 45, salary = 2500.0),
            Employee(name = "Jordan Miller", role = "Marketer", skill = 38, salary = 2100.0)
        )
    }

    private fun generateCompetitors(industry: IndustryType): List<Competitor> {
        return listOf(
            Competitor(
                name = "Apex Sentinel Ltd",
                industry = industry,
                cash = 250000.0,
                marketShare = 40.0,
                productQuality = 60,
                stockPrice = 25.0,
                stockHistory = listOf(22.0, 23.5, 25.0),
                description = "Deeply integrated legacy corporation. Heavy capital."
            ),
            Competitor(
                name = "Zephyr Horizon Inc.",
                industry = industry,
                cash = 95000.0,
                marketShare = 25.0,
                productQuality = 42,
                stockPrice = 12.0,
                stockHistory = listOf(14.0, 13.2, 12.0),
                description = "Aggressive startup expansion, specializing in high-speed manufacturing."
            ),
            Competitor(
                name = "Dynasty International",
                industry = industry,
                cash = 500000.0,
                marketShare = 35.0,
                productQuality = 75,
                stockPrice = 85.0,
                stockHistory = listOf(80.0, 82.5, 85.0),
                description = "Prestige conglomerates with endless financial reserves."
            )
        )
    }

    // Hiring Employees
    fun hireEmployee(name: String, role: String, skill: Int, salary: Double): Boolean {
        val maxEmployees = getEmployeeCapacity()
        if (_employees.value.size >= maxEmployees) {
            addLog("Hire failed! You need more office space to accommodate more employees.")
            return false
        }
        val newEmp = Employee(name = name, role = role, skill = skill, salary = salary)
        _employees.value = _employees.value + newEmp
        addLog("Hired $name as a $role (Skill: $skill) for $${String.format("%,.0f", salary)}/quarter.")
        return true
    }

    fun fireEmployee(id: String) {
        val emp = _employees.value.find { it.id == id } ?: return
        _employees.value = _employees.value.filter { it.id != id }
        addLog("Dismissed Employee: ${emp.name} (${emp.role}).")
    }

    fun trainEmployees() {
        val trainingCost = _employees.value.size * 3000.0
        if (trainingCost == 0.0) return
        if (_cash.value < trainingCost) {
            addLog("Not enough cash for standard employee training (Cost: $${String.format("%,.0f", trainingCost)}).")
            return
        }
        _cash.value -= trainingCost
        _employees.value = _employees.value.map {
            it.copy(skill = min(100, it.skill + Random.nextInt(5, 12)))
        }
        addLog("Conducted skills training for all staff. Total cost: $${String.format("%,.0f", trainingCost)}.")
    }

    fun getEmployeeCapacity(): Int {
        val base = 5
        val bonus = _properties.value.filter { it.isOwned }.sumOf { it.capacityBonus }
        return base + bonus
    }

    // Property Acquisition
    fun buyProperty(propertyId: String): Boolean {
        val prop = _properties.value.find { it.id == propertyId } ?: return false
        if (prop.isOwned) return false
        if (_cash.value < prop.price) {
            addLog("Insufficient liquid reserves to purchase property: ${prop.name}.")
            return false
        }
        _cash.value -= prop.price
        _properties.value = _properties.value.map {
            if (it.id == propertyId) it.copy(isOwned = true) else it
        }
        addLog("Acquired Real Estate asset: ${prop.name} for $${String.format("%,.0f", prop.price)}.")
        return true
    }

    // Loans
    fun takeLoan(requestedAmount: Double) {
        val rate = _difficulty.value.loanInterest
        val newLoan = Loan(
            principal = requestedAmount,
            startingPrincipal = requestedAmount,
            interestRate = rate,
            remainingTurns = 8 // 2 Years duration
        )
        _cash.value += requestedAmount
        _loans.value = _loans.value + newLoan
        addLog("Approved bank credit line: $${String.format("%,.0f", requestedAmount)} at ${String.format("%.1f", rate * 100)}% dynamic APR.")
    }

    fun repayLoan(loanId: String) {
        val loan = _loans.value.find { it.id == loanId } ?: return
        val payoff = loan.principal * (1.0 + (loan.interestRate / 4.0)) // current interest included
        if (_cash.value < payoff) {
            addLog("Insufficient funds to settle this loan liability.")
            return
        }
        _cash.value -= payoff
        _loans.value = _loans.value.filter { it.id != loanId }
        addLog("Fully liquidated loan liability of $${String.format("%,.0f", payoff)}.")
    }

    // Stock Market Operations
    fun buyCompetitorStock(competitorId: String, qty: Int): Boolean {
        val comp = _competitors.value.find { it.id == competitorId } ?: return false
        val cost = comp.stockPrice * qty
        if (_cash.value < cost) {
            addLog("Insufficient direct cash to accumulate this stock block.")
            return false
        }
        _cash.value -= cost
        val ownedList = _stocksOwned.value.toMutableList()
        val index = ownedList.indexOfFirst { it.tickerSymbol == competitorId }
        if (index >= 0) {
            val holding = ownedList[index]
            val totalQty = holding.quantityOwned + qty
            val avg = ((holding.averagePurchasePrice * holding.quantityOwned) + (comp.stockPrice * qty)) / totalQty
            holding.quantityOwned = totalQty
            holding.averagePurchasePrice = avg
        } else {
            ownedList.add(StockShare(competitorId, comp.name, qty, comp.stockPrice))
        }
        _stocksOwned.value = ownedList
        addLog("Stock Market Purchase: Bought $qty shares of ${comp.name} at $${String.format("%.2f", comp.stockPrice)}/share.")
        return true
    }

    fun sellCompetitorStock(competitorId: String, qty: Int): Boolean {
        val ownedList = _stocksOwned.value.toMutableList()
        val index = ownedList.indexOfFirst { it.tickerSymbol == competitorId }
        if (index < 0) return false
        val holding = ownedList[index]
        if (holding.quantityOwned < qty) {
            addLog("Not enough owned shares to execute this sale.")
            return false
        }

        val comp = _competitors.value.find { it.id == competitorId } ?: return false
        val payout = comp.stockPrice * qty
        _cash.value += payout

        if (holding.quantityOwned == qty) {
            ownedList.removeAt(index)
        } else {
            holding.quantityOwned -= qty
        }
        _stocksOwned.value = ownedList
        addLog("Stock Market Sale: Settled $qty shares of ${comp.name} for cash payout of $${String.format("%,.2f", payout)}.")
        return true
    }

    fun launchIpo() {
        if (_isIpoDone.value) return
        val startingIpoValuation = calculatePrestigeValuation() * 0.40 // IPO 40%
        _cash.value += startingIpoValuation
        _sharePercentOwned.value = 60.0 // Retain 60%
        _isIpoDone.value = true
        _playerStockPrice.value = 10.0
        _playerStockHistory.value = listOf(10.0)
        addLog("IPO Success! Corporate Empire listed on NASDAQ. Raised $${String.format("%,.0f", startingIpoValuation)} liquid cash by selling 40% equity.")
    }

    fun buyBackOwnShares(amountPercent: Double): Boolean {
        if (!_isIpoDone.value) return false
        val sharePrice = _playerStockPrice.value
        val companyTotalShares = 100000
        val sharesToBuy = (amountPercent / 100.0) * companyTotalShares
        val cost = sharesToBuy * sharePrice
        if (_cash.value < cost) {
            addLog("Failed buyback! Insufficient company treasury reserves to execute buyback.")
            return false
        }
        _cash.value -= cost
        _sharePercentOwned.value = min(100.0, _sharePercentOwned.value + amountPercent)
        addLog("Share Buyback: Reacquired ${String.format("%.1f", amountPercent)}% of own equity for $${String.format("%,.0f", cost)}.")
        return true
    }

    // Creating Custom Product
    fun developNewProduct(name: String, buildCost: Double, price: Double, marketing: Double): Boolean {
        val rdNeeded = _currentIndustry.value.baseRDRequirement
        if (_rdProgress.value < rdNeeded) {
            addLog("Need R&D milestones to design prototype! Current progress: ${String.format("%.0f", _rdProgress.value)}/${rdNeeded} units.")
            return false
        }
        if (_cash.value < buildCost + marketing) {
            addLog("Insolvent! Need capital for production batch and marketing launching.")
            return false
        }

        _cash.value -= (buildCost + marketing)
        _rdProgress.value -= rdNeeded

        // Compute developer skill impact on quality
        val devs = _employees.value.filter { it.role == "Developer" || it.role == "Researcher" }
        val avgSkill = if (devs.isEmpty()) 30 else devs.map { it.skill }.average().toInt()
        val computedQuality = min(100, max(15, avgSkill + Random.nextInt(-5, 15)))

        val initialStockUnits = (buildCost / (buildCost * 0.15 + 1.0)).toInt().coerceAtLeast(100)

        val newProduct = Product(
            name = name,
            quality = computedQuality,
            costPerUnit = buildCost / initialStockUnits,
            retailPrice = price,
            inventory = initialStockUnits,
            totalSold = 0,
            marketingInvestment = marketing,
            demandIndex = calculateInitialDemand(computedQuality, price, marketing)
        )

        _products.value = _products.value + newProduct
        addLog("Successfully designed & manufactured product: $name. Inital stock: ${initialStockUnits} units (Quality: $computedQuality/100).")
        return true
    }

    private fun calculateInitialDemand(quality: Int, price: Double, marketing: Double): Int {
        var demand = 50
        demand += (quality - 50) / 2
        val markDiff = (marketing / 2000.0).toInt().coerceIn(0, 35)
        demand += markDiff
        val priceFactor = if (price > 0) (quality * 10.0 / price).toInt().coerceIn(-40, 20) else 20
        demand += priceFactor
        return demand.coerceIn(10, 100)
    }

    // R&D Focus
    fun executeResearch() {
        val researchers = _employees.value.filter { it.role == "Researcher" || it.role == "Developer" }
        val researcherBonus = if (researchers.isEmpty()) 5.0 else researchers.sumOf { it.skill / 15.0 }
        
        val baseCost = 2500.0
        if (_cash.value < baseCost) {
            addLog("Insufficient cash to execute research breakthrough cycle ($2,500 needed).")
            return
        }
        _cash.value -= baseCost
        val gain = Random.nextDouble(10.0, 20.0) + researcherBonus
        _rdProgress.value += gain
        addLog("Invested $2,500 in technology. Earned +${String.format("%.1f", gain)} R&D progress units.")
    }

    // End Turn / Next Quarter Progression Engine
    fun endTurnAndAdvance() {
        if (_isGeneratingCrisis.value) return
        viewModelScope.launch {
            _isGeneratingCrisis.value = true
            
            // Advance state inside dispatchers IO
            withContext(Dispatchers.Default) {
                processQuarterlyCalculations()
            }

            // Generate Boardroom Event
            try {
                val boardroomResult = eventGenerator.generateBoardroomEvent(
                    companyName = _companyName.value,
                    industry = _currentIndustry.value.label,
                    cash = _cash.value,
                    reputation = _reputation.value,
                    satisfaction = _satisfaction.value
                )
                _activeCrisis.value = boardroomResult
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isGeneratingCrisis.value = false
                saveGameSlot("autosave")
            }
        }
    }

    private fun processQuarterlyCalculations() {
        val turn = _currentTurn.value
        val cycleMult = _marketBoomCycle.value

        // 1. Employee Wages Deduction
        val payrollExpense = _employees.value.sumOf { it.salary }
        var cashBefore = _cash.value
        _cash.value -= payrollExpense

        // 3. Rent & Property bonuses
        var rentExpense = 0.0
        var collectionRent = 0.0
        _properties.value.forEach { prop ->
            if (prop.isOwned) {
                if (prop.passiveIncome > 0) {
                    collectionRent += prop.passiveIncome
                } else {
                    rentExpense += prop.price * 0.015 // maintenance is 1.5% of value
                }
            }
        }
        _cash.value -= rentExpense
        _cash.value += collectionRent

        // 4. Product Sales Calculations
        var cashRevenue = 0.0
        var materialsExpense = 0.0
        var marketingExpenseSum = 0.0

        val updatedProducts = _products.value.map { prod ->
            val mktFactor = prod.marketingInvestment
            marketingExpenseSum += prod.marketingInvestment * 0.10 // 10% decay maintenance or re-budget
            
            // Demand formulas
            val baseDemand = prod.demandIndex * cycleMult
            val satisfactionMod = (_satisfaction.value - 60) * 0.1
            val finalDemandPercent = (baseDemand + satisfactionMod).coerceIn(5.0, 95.0)

            // Dynamic sold count
            val potentialSales = (prod.inventory * (finalDemandPercent / 100.0)).toInt().coerceIn(10, prod.inventory)
            val finalSales = if (prod.inventory > 0) potentialSales else 0
            
            val revenue = finalSales * prod.retailPrice
            cashRevenue += revenue

            // Automatically manufacture more units if capacity is valid (keeping 20% of cash for safety)
            val unitsToRestock = if (_cash.value > 10000) {
                ((_cash.value - 10000.0) * 0.10 / prod.costPerUnit).toInt().coerceAtLeast(0)
            } else 0

            val costOfNewManufacture = unitsToRestock * prod.costPerUnit
            materialsExpense += costOfNewManufacture
            _cash.value -= costOfNewManufacture

            prod.copy(
                inventory = (prod.inventory - finalSales + unitsToRestock).coerceAtLeast(0),
                totalSold = prod.totalSold + finalSales
            )
        }
        _products.value = updatedProducts
        _cash.value += cashRevenue

        // 5. Loan repayments
        var loanPayment = 0.0
        val activeLoans = _loans.value.map { loan ->
            val interestPart = (loan.principal * (loan.interestRate / 4.0))
            val principalPayment = loan.startingPrincipal / 8.0 // amortization
            val totalPayment = principalPayment + interestPart
            loanPayment += totalPayment
            _cash.value -= totalPayment
            loan.remainingTurns -= 1
            loan.copy(principal = max(0.0, loan.principal - principalPayment)).apply {
                this.remainingTurns = loan.remainingTurns
            }
        }.filter { it.remainingTurns > 0 }
        _loans.value = activeLoans

        // 6. Tax computations
        val yearCycle = turn % 4
        var netRevenueBeforeTax = cashRevenue - payrollExpense - rentExpense - marketingExpenseSum - materialsExpense - loanPayment + collectionRent
        var taxBill = 0.0
        if (netRevenueBeforeTax > 0.0) {
            taxBill = netRevenueBeforeTax * _taxOwed.value // simplified
            _taxOwed.value += netRevenueBeforeTax * 0.20 // 20% flat tax accumulated
        }

        if (yearCycle == 0 && _taxOwed.value > 0.0) {
            // End of year - deduct taxes
            val taxDeducted = _taxOwed.value
            _cash.value -= taxDeducted
            _taxOwed.value = 0.0
            addLog("Annual Tax Settlement: Paid IRS $${String.format("%,.0f", taxDeducted)} in corporate income taxes.")
        }

        // 7. Adjust Customer Satisfaction & Reputation organically
        if (updatedProducts.isNotEmpty()) {
            val avgQuality = updatedProducts.map { it.quality }.average()
            _satisfaction.value = ((_satisfaction.value * 0.8) + (avgQuality * 0.2)).toInt().coerceIn(10, 100)
            _reputation.value = min(100, max(10, _reputation.value + if (avgQuality > 60) 1 else -1))
        }

        // 8. Dynamic economic cycle changes (25% chance of swing every year)
        if (yearCycle == 0 && Random.nextInt(100) < 25) {
            cycleEconomyPhase()
        }

        // 9. Competitor simulation updates
        _competitors.value = _competitors.value.map { comp ->
            // Competitors make profit and price adjust
            val marketDemandBoost = if (_marketBoomCycle.value > 1.0) Random.nextDouble(1.05, 1.25) else Random.nextDouble(0.85, 1.05)
            val compRevenue = comp.cash * 0.12 * marketDemandBoost
            comp.cash += compRevenue - (comp.cash * 0.05) // expenses

            // Stock drift
            val oldPrice = comp.stockPrice
            val walk = Random.nextDouble(-0.15, 0.20)
            val delta = comp.stockPrice * walk * _marketBoomCycle.value
            val newPrice = max(2.5, comp.stockPrice + delta)

            val newHist = comp.stockHistory.toMutableList()
            newHist.add(newPrice)
            comp.copy(
                cash = comp.cash,
                stockPrice = newPrice,
                stockHistory = newHist.takeLast(10)
            )
        }

        // 10. Update Player Stock Valuation and history
        if (_isIpoDone.value) {
            val companyEvaluation = calculatePrestigeValuation()
            val totalCompanyShares = 100000
            val newPlayerStockPrice = max(1.0, companyEvaluation / totalCompanyShares)
            _playerStockPrice.value = newPlayerStockPrice
            val newHistory = _playerStockHistory.value.toMutableList()
            newHistory.add(newPlayerStockPrice)
            _playerStockHistory.value = newHistory.takeLast(10)
        }

        // 11. Create Statement ledger entry
        val currentQuarterStatement = FinancialStatement(
            turn = turn,
            cashRevenue = cashRevenue,
            payrollExpense = payrollExpense,
            rentExpense = rentExpense,
            marketingExpense = marketingExpenseSum,
            rdExpense = if (researchersCount() > 0) 2500.0 else 0.0,
            rawMaterialsExpense = materialsExpense,
            loanRepaymentExpense = loanPayment,
            investmentProfit = collectionRent,
            taxesPaid = if (yearCycle == 0) taxBill else 0.0,
            netIncome = netRevenueBeforeTax
        )
        _financials.value = _financials.value + currentQuarterStatement

        // Advance turns
        _currentTurn.value = turn + 1
        addLog("Quarter Advanced. Net profit/loss: $${String.format("%,.2f", netRevenueBeforeTax)}.")
    }

    private fun researchersCount() = _employees.value.count { it.role == "Researcher" }

    private fun cycleEconomyPhase() {
        val dice = Random.nextInt(4)
        when (dice) {
            0 -> {
                _marketBoomCycle.value = 0.6
                _marketCycleName.value = "Market Recession"
                addLog("ALERT: Economy entered a major recession. Customer buying enthusiasm has plunged by 40%!")
            }
            1 -> {
                _marketBoomCycle.value = 1.0
                _marketCycleName.value = "Stable Growth"
                addLog("INFO: The market stabilized. Reliable corporate forecasts expected.")
            }
            2 -> {
                _marketBoomCycle.value = 1.3
                _marketCycleName.value = "Capital Boom"
                addLog("HYPE: Central banks slashed rates. High disposable income boosting product demand!")
            }
            3 -> {
                _marketBoomCycle.value = 1.7
                _marketCycleName.value = "Speculative Bubble"
                addLog("CRITICAL: Massive retail mania! Tech valuations soaring into an hyper-extended bubble.")
            }
        }
    }

    // Resolving crisis event chosen in Boardroom
    fun resolveBoardroomCrisis(choiceIndex: Int) {
        val crisis = _activeCrisis.value ?: return
        val choice = when (choiceIndex) {
            1 -> crisis.choice1
            2 -> crisis.choice2
            else -> crisis.choice3
        }

        _cash.value += choice.cashImpact
        _reputation.value = min(100, max(10, _reputation.value + choice.reputationImpact))
        _satisfaction.value = min(100, max(10, _satisfaction.value + choice.satisfactionImpact))

        addLog("Board Choice Made: ${choice.text} -> Resulted in Cash: $${String.format("%,.0f", choice.cashImpact)}, Reputation: ${choice.reputationImpact}, CSat: ${choice.satisfactionImpact}.")
        _activeCrisis.value = null
    }

    // Valuation formula: combines treasury cash, property book values, current active product inventory worth, employee capitalization and reputation ratios
    fun calculatePrestigeValuation(): Double {
        val liquid = _cash.value
        val propertiesBookValue = _properties.value.filter { it.isOwned }.sumOf { it.price }
        val productAssets = _products.value.sumOf { it.inventory * it.costPerUnit }
        val intellectualBonus = _employees.value.sumOf { it.skill * 150.0 } + (_rdProgress.value * 250.0)
        val repMult = 1.0 + (_reputation.value / 100.0)
        return (liquid + propertiesBookValue + productAssets + intellectualBonus) * repMult
    }

    // Leaderboards Rankings
    fun getLeaderboardRankings(): List<LeaderboardItem> {
        val playerVal = calculatePrestigeValuation()
        val list = mutableListOf(
            LeaderboardItem(_companyName.value, playerVal, true),
            LeaderboardItem("Apex Sentinel Ltd", 480000.0 + _competitors.value.firstOrNull { it.name == "Apex Sentinel Ltd" }?.cash.let { it ?: 0.0 }, false),
            LeaderboardItem("Zephyr Horizon Inc.", 220000.0 + _competitors.value.firstOrNull { it.name == "Zephyr Horizon Inc." }?.cash.let { it ?: 0.0 }, false),
            LeaderboardItem("Dynasty International", 950000.0 + _competitors.value.firstOrNull { it.name == "Dynasty International" }?.cash.let { it ?: 0.0 }, false),
            LeaderboardItem("Gates Giga Fund", 12500000.0, false),
            LeaderboardItem("Bezos Prime Empire", 28000000.0, false),
            LeaderboardItem("Musk Interplanetary", 95000000.0, false)
        )
        return list.sortedByDescending { it.corporateWorth }
    }

    // Room persistence
    fun saveGameSlot(slotId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = GameSaveState(
                companyName = _companyName.value,
                playerReputation = _reputation.value,
                playerSatisfaction = _satisfaction.value,
                playerCash = _cash.value,
                currentTurn = _currentTurn.value,
                currentIndustry = _currentIndustry.value,
                currentDifficulty = _difficulty.value,
                employees = _employees.value,
                products = _products.value,
                competitors = _competitors.value,
                loans = _loans.value,
                properties = _properties.value,
                stocksOwned = _stocksOwned.value,
                isIpoDone = _isIpoDone.value,
                sharePercentOwned = _sharePercentOwned.value,
                stockHistory = _playerStockHistory.value,
                currentStockPrice = _playerStockPrice.value,
                rdProgress = _rdProgress.value,
                marketBoomCycle = _marketBoomCycle.value,
                marketCycleName = _marketCycleName.value,
                taxOwed = _taxOwed.value,
                financialList = _financials.value
            )
            val json = stateAdapter.toJson(state)
            val savedGame = SavedGame(
                id = slotId,
                companyName = state.companyName,
                industry = state.currentIndustry.label,
                currentTurn = state.currentTurn,
                cash = state.playerCash,
                savedAt = System.currentTimeMillis(),
                stateJson = json
            )
            savedGameDao.saveGame(savedGame)
            loadAvailableSaves()
        }
    }

    fun loadGameSlot(slotId: String) {
        viewModelScope.launch {
            val savedGame = withContext(Dispatchers.IO) {
                savedGameDao.getSavedGameById(slotId)
            }
            if (savedGame != null) {
                try {
                    val state = stateAdapter.fromJson(savedGame.stateJson) ?: return@launch
                    _companyName.value = state.companyName
                    _reputation.value = state.playerReputation
                    _satisfaction.value = state.playerSatisfaction
                    _cash.value = state.playerCash
                    _currentTurn.value = state.currentTurn
                    _currentIndustry.value = state.currentIndustry
                    _difficulty.value = state.currentDifficulty
                    _employees.value = state.employees
                    _products.value = state.products
                    _competitors.value = state.competitors
                    _loans.value = state.loans
                    _properties.value = state.properties
                    _stocksOwned.value = state.stocksOwned
                    _isIpoDone.value = state.isIpoDone
                    _sharePercentOwned.value = state.sharePercentOwned
                    _playerStockPrice.value = state.currentStockPrice
                    _playerStockHistory.value = state.stockHistory.ifEmpty { listOf(10.0) }
                    _rdProgress.value = state.rdProgress
                    _marketBoomCycle.value = state.marketBoomCycle
                    _marketCycleName.value = state.marketCycleName
                    _taxOwed.value = state.taxOwed
                    _financials.value = state.financialList
                    _isGameStarted.value = true
                    _activeCrisis.value = null
                    addLog("Loaded game slot: ${savedGame.companyName} at Turn ${savedGame.currentTurn}")
                } catch (e: Exception) {
                    addLog("Failed loading saved game slot.")
                }
            }
        }
    }

    fun deleteGameSlot(slotId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            savedGameDao.deleteGame(slotId)
            loadAvailableSaves()
        }
    }

    private fun loadAvailableSaves() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                savedGameDao.getAllSavedGames()
            }
            _availableSaves.value = list
        }
    }
}
