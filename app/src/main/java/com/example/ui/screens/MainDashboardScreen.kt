package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.theme.*
import kotlin.math.max
import kotlin.math.min

@Composable
fun getM3Colors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CorporateGold,
    unfocusedBorderColor = BorderAccent,
    focusedTextColor = TextLight,
    unfocusedTextColor = TextLight,
    focusedContainerColor = DeepCharcoal,
    unfocusedContainerColor = DeepCharcoal
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    companyName: String,
    cash: Double,
    reputation: Int,
    satisfaction: Int,
    turn: Int,
    industry: IndustryType,
    difficulty: Difficulty,
    employees: List<Employee>,
    products: List<Product>,
    competitors: List<Competitor>,
    loans: List<Loan>,
    properties: List<RealEstateProperty>,
    stocksOwned: List<StockShare>,
    isIpoDone: Boolean,
    sharePercentOwned: Double,
    playerStockHistory: List<Double>,
    playerStockPrice: Double,
    rdProgress: Double,
    marketCycleName: String,
    marketBoomCycle: Double,
    taxOwed: Double,
    financials: List<FinancialStatement>,
    activeCrisis: GeneratedBoardroomEvent?,
    isGeneratingCrisis: Boolean,
    gameLogs: List<String>,
    onHireEmployee: (String, String, Int, Double) -> Boolean,
    onFireEmployee: (String) -> Unit,
    onTrainEmployees: () -> Unit,
    onBuyProperty: (String) -> Boolean,
    onTakeLoan: (Double) -> Unit,
    onRepayLoan: (String) -> Unit,
    onBuyStock: (String, Int) -> Boolean,
    onSellStock: (String, Int) -> Boolean,
    onLaunchIpo: () -> Unit,
    onBuyBackShares: (Double) -> Boolean,
    onDevelopNewProduct: (String, Double, Double, Double) -> Boolean,
    onExecuteResearch: () -> Unit,
    onResolveCrisis: (Int) -> Unit,
    onEndTurn: () -> Unit,
    onExitGame: () -> Unit,
    leaderboardItems: List<LeaderboardItem>
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: HQ, 1: Operations, 2: Market, 3: City, 4: Boardroom
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var showHireDialog by remember { mutableStateOf(false) }
    var showLoanDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DeepCharcoal,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "HQ") },
                    label = { Text("HQ", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CorporateGold,
                        selectedTextColor = CorporateGold,
                        indicatorColor = SoftCardGray,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Ops") },
                    label = { Text("Ops", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CorporateGold,
                        selectedTextColor = CorporateGold,
                        indicatorColor = SoftCardGray,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Stocks") },
                    label = { Text("Stock Panel", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CorporateGold,
                        selectedTextColor = CorporateGold,
                        indicatorColor = SoftCardGray,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "City") },
                    label = { Text("City Grid", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CorporateGold,
                        selectedTextColor = CorporateGold,
                        indicatorColor = SoftCardGray,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Board") },
                    label = { Text("Boardroom", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CorporateGold,
                        selectedTextColor = CorporateGold,
                        indicatorColor = SoftCardGray,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
            }
        },
        containerColor = RichBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Elegant Performance Ticker Header
            PerformanceHeader(
                companyName = companyName,
                cash = cash,
                reputation = reputation,
                satisfaction = satisfaction,
                turn = turn,
                marketCycleName = marketCycleName,
                marketBoomCycle = marketBoomCycle,
                onExitGame = onExitGame
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main Sub-system Tab views
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> HQTab(
                        cash = cash,
                        employees = employees,
                        loans = loans,
                        taxOwed = taxOwed,
                        financials = financials,
                        properties = properties,
                        onHireClick = { showHireDialog = true },
                        onFireEmployee = onFireEmployee,
                        onTrainEmployees = onTrainEmployees,
                        onRepayLoan = onRepayLoan,
                        onTakeLoanClick = { showLoanDialog = true },
                        gameLogs = gameLogs
                    )
                    1 -> OperationsTab(
                        rdProgress = rdProgress,
                        products = products,
                        industryType = industry,
                        onExecuteResearch = onExecuteResearch,
                        onOpenDesignNewProduct = { showCreateProductDialog = true }
                    )
                    2 -> StockMarketTab(
                        competitors = competitors,
                        stocksOwned = stocksOwned,
                        isIpoDone = isIpoDone,
                        sharePercentOwned = sharePercentOwned,
                        playerStockHistory = playerStockHistory,
                        playerStockPrice = playerStockPrice,
                        cash = cash,
                        onBuyStock = onBuyStock,
                        onSellStock = onSellStock,
                        onLaunchIpo = onLaunchIpo,
                        onBuyBackShares = onBuyBackShares
                    )
                    3 -> CityTab(
                        properties = properties,
                        cash = cash,
                        onBuyProperty = onBuyProperty
                    )
                    4 -> BoardroomTab(
                        activeCrisis = activeCrisis,
                        leaderboardItems = leaderboardItems,
                        isGeneratingCrisis = isGeneratingCrisis,
                        onResolveCrisis = onResolveCrisis,
                        onEndTurn = onEndTurn
                    )
                }
            }
        }
    }

    // Modal dialog overlays
    if (showCreateProductDialog) {
        CreateProductDialog(
            industry = industry,
            cash = cash,
            rdProgress = rdProgress,
            onDismiss = { showCreateProductDialog = false },
            onConfirm = { name, bCost, pPrice, mkt ->
                val ok = onDevelopNewProduct(name, bCost, pPrice, mkt)
                if (ok) showCreateProductDialog = false
            }
        )
    }

    if (showHireDialog) {
        HireEmployeeDialog(
            cash = cash,
            onDismiss = { showHireDialog = false },
            onConfirm = { name, role, skill, sal ->
                val ok = onHireEmployee(name, role, skill, sal)
                if (ok) showHireDialog = false
            }
        )
    }

    if (showLoanDialog) {
        RequestLoanDialog(
            difficulty = difficulty,
            onDismiss = { showLoanDialog = false },
            onConfirm = { amount ->
                onTakeLoan(amount)
                showLoanDialog = false
            }
        )
    }
}

@Composable
fun PerformanceHeader(
    companyName: String,
    cash: Double,
    reputation: Int,
    satisfaction: Int,
    turn: Int,
    marketCycleName: String,
    marketBoomCycle: Double,
    onExitGame: () -> Unit
) {
    val cycleColor = when {
        marketBoomCycle > 1.3 -> SuccessGreen
        marketBoomCycle < 0.8 -> AlertRed
        else -> NeonTeal
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
        border = BorderStroke(1.dp, BorderAccent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = companyName.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SoftGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Fiscal Quarter: Q$turn",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(cycleColor.copy(alpha = 0.15f))
                            .border(1.dp, cycleColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = marketCycleName.uppercase(),
                            color = cycleColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onExitGame,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Exit Corporate Desk",
                            tint = AlertRed.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = BorderAccent.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Liquid Reserves
                Column {
                    Text("LIQUID CASH", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$${String.format("%,.2f", cash)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }

                // Reputation
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("REPUTATION", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, "Rep", tint = CorporateGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$reputation/100",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftGold
                        )
                    }
                }

                // Customer Sat
                Column(horizontalAlignment = Alignment.End) {
                    Text("CUSTOMER SAT", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, "CSat", tint = NeonTeal, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$satisfaction%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HQTab(
    cash: Double,
    employees: List<Employee>,
    loans: List<Loan>,
    taxOwed: Double,
    financials: List<FinancialStatement>,
    properties: List<RealEstateProperty>,
    onHireClick: () -> Unit,
    onFireEmployee: (String) -> Unit,
    onTrainEmployees: () -> Unit,
    onRepayLoan: (String) -> Unit,
    onTakeLoanClick: () -> Unit,
    gameLogs: List<String>
) {
    var hqSubSection by remember { mutableStateOf(0) } // 0: Employees, 1: Finance Ledger, 2: Banks & Taxes, 3: Operations Log

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nested Navigation Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val sections = listOf("HR & Staff", "Financials", "Credit & Tax", "Corporate Logs")
            sections.forEachIndexed { idx, label ->
                val active = hqSubSection == idx
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (active) CorporateGold else SoftCardGray)
                        .clickable { hqSubSection = idx }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (active) RichBlack else TextLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Divider(color = BorderAccent.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 12.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (hqSubSection) {
                0 -> StaffHRExpansion(
                    employees = employees,
                    onHireClick = onHireClick,
                    onFireEmployee = onFireEmployee,
                    onTrainEmployees = onTrainEmployees
                )
                1 -> FinancialLedgerSheet(financials = financials)
                2 -> CreditsAndTaxesSection(
                    cash = cash,
                    loans = loans,
                    taxOwed = taxOwed,
                    onRepayLoan = onRepayLoan,
                    onTakeLoanClick = onTakeLoanClick
                )
                3 -> LogsDashboardSection(gameLogs = gameLogs)
            }
        }
    }
}

@Composable
fun StaffHRExpansion(
    employees: List<Employee>,
    onHireClick: () -> Unit,
    onFireEmployee: (String) -> Unit,
    onTrainEmployees: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HR MANAGEMENT DECK",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CorporateGold,
                letterSpacing = 1.sp
            )

            Row {
                Button(
                    onClick = onTrainEmployees,
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCardGray, contentColor = SoftGold),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Star, "Train", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Train All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onHireClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CorporateGold, contentColor = RichBlack),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("hire_employee_btn"),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Add, "Hire", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hire Staff", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (employees.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No active personnel hired. Click 'Hire Staff' to expand operations.", color = TextMuted, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(employees) { emp ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("employee_${emp.id}"),
                        colors = CardDefaults.cardColors(containerColor = SoftCardGray),
                        border = BorderStroke(1.dp, BorderAccent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(emp.name, fontWeight = FontWeight.Bold, color = TextLight, fontSize = 14.sp)
                                Text(
                                    text = "${emp.role} • Skill Level: ${emp.skill}/100",
                                    color = NeonTeal,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$${String.format("%,.0f", emp.salary)}/qtr",
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextLight,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )

                                IconButton(
                                    onClick = { onFireEmployee(emp.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Fire Employee",
                                        tint = AlertRed.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialLedgerSheet(financials: List<FinancialStatement>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "HISTORICAL INCOME STATEMENTS",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CorporateGold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (financials.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Ledger will compile income details starting next quarter.", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(financials.reversed()) { sheet ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SoftCardGray),
                        border = BorderStroke(1.dp, BorderAccent)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Quarter Q${sheet.turn} Statement Summary",
                                    fontWeight = FontWeight.Bold,
                                    color = SoftGold,
                                    fontSize = 13.sp
                                )

                                val profit = sheet.netIncome
                                Text(
                                    text = if (profit >= 0) "+$${String.format("%,.2f", profit)}" else "-$${String.format("%,.2f", -profit)}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (profit >= 0) SuccessGreen else AlertRed,
                                    fontSize = 14.sp
                                )
                            }

                            Divider(color = BorderAccent.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                            // Details
                            FinancialMiniRow("Revenues Collected:", sheet.cashRevenue, true)
                            FinancialMiniRow("Payroll Outflows:", sheet.payrollExpense, false)
                            FinancialMiniRow("R&D Breakthroughs:", sheet.rdExpense, false)
                            FinancialMiniRow("Materials / Restocking:", sheet.rawMaterialsExpense, false)
                            FinancialMiniRow("Marketing Campaigns:", sheet.marketingExpense, false)
                            FinancialMiniRow("Office Lease / Maintenance:", sheet.rentExpense, false)
                            FinancialMiniRow("Debt Leverage Payments:", sheet.loanRepaymentExpense, false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialMiniRow(label: String, value: Double, isCredit: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, color = TextMuted)
        Text(
            text = "$${String.format("%,.0f", value)}",
            fontSize = 11.sp,
            color = if (isCredit) SuccessGreen.copy(alpha = 0.8f) else TextLight,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CreditsAndTaxesSection(
    cash: Double,
    loans: List<Loan>,
    taxOwed: Double,
    onRepayLoan: (String) -> Unit,
    onTakeLoanClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FINANCIAL LEVERAGE DEPOT",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CorporateGold,
                letterSpacing = 1.sp
            )

            Button(
                onClick = onTakeLoanClick,
                colors = ButtonDefaults.buttonColors(containerColor = CorporateGold, contentColor = RichBlack),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("apply_loan_btn")
            ) {
                Icon(Icons.Default.Add, "Apply Loan", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Apply Loan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Taxes Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            border = BorderStroke(1.dp, CorporateGold.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("ACCUMULATED INCOME TAXES", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$${String.format("%,.2f", taxOwed)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed
                    )
                    Text("Auto-liquidated at the end of every fiscal year (Turn 4 cycle)", fontSize = 10.sp, color = TextMuted)
                }

                Icon(Icons.Default.Home, "IRS", tint = CorporateGold, modifier = Modifier.size(36.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ACTIVE CORPORATE LIABILITIES",
            fontSize = 11.sp,
            color = CorporateGold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (loans.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No outstanding liabilities. Excellent credit score!", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(loans) { loan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftCardGray),
                        border = BorderStroke(1.dp, BorderAccent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Principal: $${String.format("%,.0f", loan.principal)}",
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "APR: ${String.format("%.1f", loan.interestRate * 100)}% • Duration: ${loan.remainingTurns} qtrs",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Button(
                                onClick = { onRepayLoan(loan.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = RichBlack),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text("Liquidate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogsDashboardSection(gameLogs: List<String>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "CHRONICLED EMPIRE ACTIONS",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CorporateGold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DeepCharcoal)
                .padding(12.dp)
        ) {
            items(gameLogs) { l ->
                Text(
                    text = l,
                    fontSize = 11.sp,
                    color = if (l.contains("Advanced") || l.contains("Revenues")) SuccessGreen else if (l.contains("ALERT")) AlertRed else TextLight,
                    modifier = Modifier.padding(vertical = 4.dp),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun OperationsTab(
    rdProgress: Double,
    products: List<Product>,
    industryType: IndustryType,
    onExecuteResearch: () -> Unit,
    onOpenDesignNewProduct: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Research Deck
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            border = BorderStroke(1.dp, BorderAccent)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("RESEARCH & DEVELOPMENT ROADMAP", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${String.format("%.1f", rdProgress)} / ${industryType.baseRDRequirement} MIL",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonTeal
                    )
                    Text("Accumulate points to construct specialized product prototypes", fontSize = 10.sp, color = TextMuted)
                }

                Button(
                    onClick = onExecuteResearch,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = RichBlack),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("research_button")
                ) {
                    Icon(Icons.Default.Refresh, "R&D")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("R&D Cycle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACTIVE PRODUCT VENTURES",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CorporateGold,
                letterSpacing = 1.sp
            )

            Button(
                onClick = onOpenDesignNewProduct,
                enabled = rdProgress >= industryType.baseRDRequirement,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CorporateGold,
                    contentColor = RichBlack,
                    disabledContainerColor = BorderAccent,
                    disabledContentColor = TextMuted
                ),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("open_builder_btn")
            ) {
                Icon(Icons.Default.Edit, "Launch")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Build Product", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No manufactured product assets active in this market.\nInvest in R&D to unlock a product specification.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(products) { prod ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SoftCardGray),
                        border = BorderStroke(1.dp, BorderAccent)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(prod.name.uppercase(), fontWeight = FontWeight.Bold, color = TextLight, fontSize = 15.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CorporateGold.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Qual: ${prod.quality}/100",
                                        fontSize = 9.sp,
                                        color = CorporateGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = BorderAccent.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Retail Price", fontSize = 9.sp, color = TextMuted)
                                    Text("$${String.format("%,.2f", prod.retailPrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                }

                                Column {
                                    Text("Unit Cost", fontSize = 9.sp, color = TextMuted)
                                    Text("$${String.format("%,.2f", prod.costPerUnit)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextLight)
                                }

                                Column {
                                    Text("Available Inventory", fontSize = 9.sp, color = TextMuted)
                                    Text("${prod.inventory} units", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextLight)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Cumulative Units Sold", fontSize = 9.sp, color = TextMuted)
                                    Text("${prod.totalSold} sold", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonTeal)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StockMarketTab(
    competitors: List<Competitor>,
    stocksOwned: List<StockShare>,
    isIpoDone: Boolean,
    sharePercentOwned: Double,
    playerStockHistory: List<Double>,
    playerStockPrice: Double,
    cash: Double,
    onBuyStock: (String, Int) -> Boolean,
    onSellStock: (String, Int) -> Boolean,
    onLaunchIpo: () -> Unit,
    onBuyBackShares: (Double) -> Boolean
) {
    var shareMarketSection by remember { mutableStateOf(0) } // 0: Player Equity, 1: Competitors Stocks

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GLOBAL STOCK MARKET TERMINAL",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CorporateGold,
                letterSpacing = 1.sp
            )

            Row(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(SoftCardGray)) {
                Box(
                    modifier = Modifier
                        .clickable { shareMarketSection = 0 }
                        .background(if (shareMarketSection == 0) CorporateGold else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Your IPO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (shareMarketSection == 0) RichBlack else TextLight)
                }
                Box(
                    modifier = Modifier
                        .clickable { shareMarketSection = 1 }
                        .background(if (shareMarketSection == 1) CorporateGold else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("AI Tickers", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (shareMarketSection == 1) RichBlack else TextLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (shareMarketSection == 0) {
            // Player Corporate Equity Workspace
            if (!isIpoDone) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftCardGray),
                    border = BorderStroke(1.dp, BorderAccent)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Star, "IPO", modifier = Modifier.size(48.dp), tint = CorporateGold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("LAUNCH AN INITIAL PUBLIC OFFERING", fontWeight = FontWeight.Bold, color = TextLight, fontSize = 16.sp)
                        Text(
                            text = "List your private business on public markets. Sell 40% company equity to raise massive liquid cash instantly.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        Button(
                            onClick = onLaunchIpo,
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateGold, contentColor = RichBlack),
                            modifier = Modifier.fillMaxWidth().testTag("launch_ipo_btn")
                        ) {
                            Text("GO PUBLIC & IPO", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Render custom Canvas dynamic Chart
                Text(
                    text = "NASDAQ: EMP STOCK PRICE GRID",
                    fontSize = 11.sp,
                    color = CorporateGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                StockVisualChart(history = playerStockHistory, color = CorporateGold)

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                    border = BorderStroke(1.dp, BorderAccent)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("CURRENT TICKER PRICE", fontSize = 9.sp, color = TextMuted)
                                Text("$${String.format("%.2f", playerStockPrice)}/sh", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CorporateGold)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("YOUR EQUITY RETAINED", fontSize = 9.sp, color = TextMuted)
                                Text("${String.format("%.1f", sharePercentOwned)}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonTeal)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("REACQUIRE PRIVATE CONTROL (BUY BACK)", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onBuyBackShares(5.0) },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftCardGray, contentColor = CorporateGold),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Buy Back 5% Equity", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onBuyBackShares(10.0) },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftCardGray, contentColor = CorporateGold),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Buy Back 10% Equity", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Competitors Workspace
            competitors.forEach { comp ->
                val holding = stocksOwned.find { it.tickerSymbol == comp.id }
                val qtyOwned = holding?.quantityOwned ?: 0
                val avgPrice = holding?.averagePurchasePrice ?: 0.0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftCardGray),
                    border = BorderStroke(1.dp, BorderAccent)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(comp.name.uppercase(), fontWeight = FontWeight.Bold, color = TextLight, fontSize = 14.sp)
                                Text(comp.description, fontSize = 10.sp, color = TextMuted)
                            }

                            Text(
                                text = "$${String.format("%.2f", comp.stockPrice)}",
                                fontWeight = FontWeight.Bold,
                                color = TextLight,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = BorderAccent.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("YOUR PORTFOLIO BLOCKS", fontSize = 8.sp, color = TextMuted)
                                Text(
                                    text = if (qtyOwned > 0) "$qtyOwned shares @ $${String.format("%.1f", avgPrice)}" else "None",
                                    fontSize = 11.sp,
                                    color = if (qtyOwned > 0) NeonTeal else TextMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row {
                                Button(
                                    onClick = { onSellStock(comp.id, 100) },
                                    enabled = qtyOwned >= 100,
                                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = TextLight),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp)
                                ) {
                                    Text("Sell 100", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                Button(
                                    onClick = { onBuyStock(comp.id, 100) },
                                    enabled = cash >= comp.stockPrice * 100,
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = RichBlack),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp)
                                ) {
                                    Text("Buy 100", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StockVisualChart(history: List<Double>, color: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(DeepCharcoal, RoundedCornerShape(8.dp))
            .border(1.dp, BorderAccent, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        val points = history.takeLast(10)
        if (points.size < 2) return@Canvas

        val maxVal = points.maxOrNull()?.let { it * 1.1 } ?: 20.0
        val minVal = points.minOrNull()?.let { it * 0.9.coerceAtLeast(0.0) } ?: 0.0
        val range = maxVal - minVal

        val width = size.width
        val height = size.height

        val stepX = width / (points.size - 1)

        // Draw light gridlines
        for (i in 1..3) {
            val h = height * (i / 4f)
            drawLine(
                color = BorderAccent.copy(alpha = 0.3f),
                start = Offset(0f, h),
                end = Offset(width, h),
                strokeWidth = 1f
            )
        }

        val graphPath = Path()
        val areaPath = Path()

        points.forEachIndexed { idx, price ->
            val cx = idx * stepX
            val cy = height - (((price - minVal) / range) * height).toFloat()

            if (idx == 0) {
                graphPath.moveTo(cx, cy)
                areaPath.moveTo(cx, height)
                areaPath.lineTo(cx, cy)
            } else {
                graphPath.lineTo(cx, cy)
                areaPath.lineTo(cx, cy)
            }

            if (idx == points.size - 1) {
                areaPath.lineTo(cx, height)
                areaPath.close()
            }
        }

        // Draw Area gradient under stock path
        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.25f), Color.Transparent)
            )
        )

        // Draw Line Path
        drawPath(
            path = graphPath,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Point highlight on final point
        val finalX = (points.size - 1) * stepX
        val finalY = height - (((points.last() - minVal) / range) * height).toFloat()
        drawCircle(
            color = color,
            radius = 5.dp.toPx(),
            center = Offset(finalX, finalY)
        )
    }
}

@Composable
fun CityTab(
    properties: List<RealEstateProperty>,
    cash: Double,
    onBuyProperty: (String) -> Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "REAL ESTATE INVESTMENT NETWORK",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CorporateGold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(properties) { prop ->
                val border = if (prop.isOwned) BorderStroke(1.dp, CorporateGold) else BorderStroke(1.dp, BorderAccent)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (prop.isOwned) SoftCardGray else DeepCharcoal),
                    border = border
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = prop.name.uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (prop.isOwned) SoftGold else TextLight,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Node Zone: ${prop.location}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            if (prop.isOwned) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CorporateGold.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("OWNED", fontSize = 10.sp, color = CorporateGold, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    text = "$${String.format("%,.0f", prop.price)}",
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = BorderAccent.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                if (prop.capacityBonus > 0) {
                                    Text("Staff Quarters Slot: +${prop.capacityBonus}", fontSize = 11.sp, color = NeonTeal, fontWeight = FontWeight.SemiBold)
                                }
                                if (prop.passiveIncome > 0) {
                                    Text("Tenant Rental Cashflow: +$${String.format("%,.0f", prop.passiveIncome)}/qtr", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                                }
                                Text("Company Prestige Multiplier: ${prop.marketMultiplier}x", fontSize = 11.sp, color = TextMuted)
                            }

                            if (!prop.isOwned) {
                                Button(
                                    onClick = { onBuyProperty(prop.id) },
                                    enabled = cash >= prop.price,
                                    colors = ButtonDefaults.buttonColors(containerColor = CorporateGold, contentColor = RichBlack),
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp)
                                ) {
                                    Text("Aquire", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoardroomTab(
    activeCrisis: GeneratedBoardroomEvent?,
    leaderboardItems: List<LeaderboardItem>,
    isGeneratingCrisis: Boolean,
    onResolveCrisis: (Int) -> Unit,
    onEndTurn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (isGeneratingCrisis) {
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp).padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = SoftCardGray),
                border = BorderStroke(1.dp, BorderAccent)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CorporateGold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Drafting Board Agendas, Generating AI Scenarios...", fontSize = 12.sp, color = TextMuted, textAlign = TextAlign.Center)
                    }
                }
            }
        } else if (activeCrisis != null) {
            // BOARDROOM AI CRISIS ALERT SCREEN
            Text(
                text = "BOARDROOM CONFLICT ESCALATION",
                fontSize = 11.sp,
                color = AlertRed,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().testTag("crisis_card"),
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = activeCrisis.crisisTitle.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = SoftGold,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = activeCrisis.scenarioText,
                        fontSize = 13.sp,
                        color = TextLight,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("DIRECTIVE ACTIONS:", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    CrisisChoiceElement(activeCrisis.choice1, 1, onResolveCrisis)
                    Spacer(modifier = Modifier.height(8.dp))
                    CrisisChoiceElement(activeCrisis.choice2, 2, onResolveCrisis)
                    Spacer(modifier = Modifier.height(8.dp))
                    CrisisChoiceElement(activeCrisis.choice3, 3, onResolveCrisis)
                }
            }
        } else {
            // NO ACTIVE CRISES, SHOW END-TURN CONTROL BLOCK
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SoftCardGray),
                border = BorderStroke(1.dp, BorderAccent)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Check, "Ready", modifier = Modifier.size(48.dp), tint = CorporateGold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("FISCAL DESK READY", fontWeight = FontWeight.Bold, color = TextLight, fontSize = 16.sp)
                    Text(
                        text = "Everything is updated to date. Submit this quarter performance and advance into next fiscal quarter.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    Button(
                        onClick = onEndTurn,
                        colors = ButtonDefaults.buttonColors(containerColor = CorporateGold, contentColor = RichBlack),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("end_turn_btn")
                    ) {
                        Text("ADVANCE FISCAL QUARTER", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // GLOBAL FORBES EMPIRES LEADERBOARDS
        Text(
            text = "FORBES CORPORATE CAPITAL INDEX",
            fontSize = 12.sp,
            color = CorporateGold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            border = BorderStroke(1.dp, BorderAccent)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                leaderboardItems.forEachIndexed { index, item ->
                    val color = if (item.isPlayer) CorporateGold else TextLight
                    val weight = if (item.isPlayer) FontWeight.Bold else FontWeight.Normal

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#${index + 1}",
                                fontSize = 12.sp,
                                color = if (index < 3) CorporateGold else TextMuted,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(32.dp)
                            )
                            Text(
                                text = item.name,
                                fontSize = 13.sp,
                                color = color,
                                fontWeight = weight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = "$${String.format("%,.0f", item.corporateWorth)}",
                            fontSize = 13.sp,
                            color = if (item.isPlayer) SuccessGreen else TextLight,
                            fontWeight = weight
                        )
                    }

                    if (index < leaderboardItems.size - 1) {
                        Divider(color = BorderAccent.copy(alpha = 0.1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CrisisChoiceElement(choice: ChoiceDetail, index: Int, onSelect: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(index) }
            .testTag("choice_${index}"),
        colors = CardDefaults.cardColors(containerColor = SoftCardGray),
        border = BorderStroke(1.dp, BorderAccent)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(choice.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextLight)
            Text(choice.impactText, fontSize = 11.sp, color = NeonTeal, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

// Dialog Layouts
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductDialog(
    industry: IndustryType,
    cash: Double,
    rdProgress: Double,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, Double) -> Unit
) {
    var pName by remember { mutableStateOf("") }
    var buildCostInput by remember { mutableStateOf("15000") }
    var priceInput by remember { mutableStateOf("250") }
    var marketingInput by remember { mutableStateOf("5000") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            border = BorderStroke(1.dp, CorporateGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("DEVELOP NEW PRODUCT SPEC", fontWeight = FontWeight.Bold, color = SoftGold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pName,
                    onValueChange = { pName = it },
                    label = { Text("Product Asset Designation", color = TextMuted) },
                    singleLine = true,
                    colors = getM3Colors(),
                    modifier = Modifier.fillMaxWidth().testTag("product_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = buildCostInput,
                    onValueChange = { buildCostInput = it },
                    label = { Text("Production Cost Batch ($)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = getM3Colors(),
                    modifier = Modifier.fillMaxWidth().testTag("product_cost_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it },
                    label = { Text("Consumer Retail Pricing ($)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = getM3Colors(),
                    modifier = Modifier.fillMaxWidth().testTag("product_price_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = marketingInput,
                    onValueChange = { marketingInput = it },
                    label = { Text("Marketing Launch Budget ($)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = getM3Colors(),
                    modifier = Modifier.fillMaxWidth().testTag("product_marketing_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                val numericBuildCost = buildCostInput.toDoubleOrNull() ?: 0.0
                val numericPrice = priceInput.toDoubleOrNull() ?: 0.0
                val numericMarketing = marketingInput.toDoubleOrNull() ?: 0.0
                val invalid = pName.isBlank() || numericBuildCost <= 0 || numericPrice <= 0 || numericMarketing < 0 || (numericBuildCost + numericMarketing) > cash

                if ((numericBuildCost + numericMarketing) > cash) {
                    Text("Out of cash resources!", color = AlertRed, fontSize = 11.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirm(pName, numericBuildCost, numericPrice, numericMarketing) },
                        enabled = !invalid,
                        colors = ButtonDefaults.buttonColors(containerColor = CorporateGold, contentColor = RichBlack),
                        modifier = Modifier.testTag("confirm_build_product_btn")
                    ) {
                        Text("LAUNCH BATCH", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HireEmployeeDialog(
    cash: Double,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Double) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var roleInput by remember { mutableStateOf("Developer") }
    var skillInput by remember { mutableStateOf("50") }
    var salaryInput by remember { mutableStateOf("4500") }

    val roles = listOf("Developer", "Researcher", "Marketer", "Salesperson", "Manager")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            border = BorderStroke(1.dp, CorporateGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("RECRUIT STRATEGIC EXECUTIVE", fontWeight = FontWeight.Bold, color = SoftGold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Candidate Name", color = TextMuted) },
                    singleLine = true,
                    colors = getM3Colors(),
                    modifier = Modifier.fillMaxWidth().testTag("employee_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("REVENUE DIVISION OPERATIONS:", fontSize = 11.sp, color = CorporateGold, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    roles.forEach { role ->
                        val active = roleInput == role
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (active) CorporateGold else SoftCardGray)
                                .clickable { roleInput = role }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(role, fontSize = 10.sp, color = if (active) RichBlack else TextLight, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = skillInput,
                    onValueChange = { skillInput = it },
                    label = { Text("Experience Rating (1-100)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = getM3Colors(),
                    modifier = Modifier.fillMaxWidth().testTag("employee_skill_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = salaryInput,
                    onValueChange = { salaryInput = it },
                    label = { Text("Quarterly Wage Salary ($)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = getM3Colors(),
                    modifier = Modifier.fillMaxWidth().testTag("employee_salary_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                val numericSkill = skillInput.toIntOrNull() ?: 0
                val numericSalary = salaryInput.toDoubleOrNull() ?: 0.0
                val invalid = nameInput.isBlank() || numericSkill !in 1..100 || numericSalary <= 0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirm(nameInput, roleInput, numericSkill, numericSalary) },
                        enabled = !invalid,
                        colors = ButtonDefaults.buttonColors(containerColor = CorporateGold, contentColor = RichBlack),
                        modifier = Modifier.testTag("confirm_hire_employee_btn")
                    ) {
                        Text("RECRUIT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestLoanDialog(
    difficulty: Difficulty,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountInput by remember { mutableStateOf("25000") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            border = BorderStroke(1.dp, CorporateGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("CREDIT LINE APPLICATION", fontWeight = FontWeight.Bold, color = SoftGold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Your difficulty level sets loan APR to ${String.format("%.1f", difficulty.loanInterest * 100)}% amortized over 8 quarters.", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Leverage Financing Amount ($)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = getM3Colors(),
                    modifier = Modifier.fillMaxWidth().testTag("loan_amount_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                val numericAmount = amountInput.toDoubleOrNull() ?: 0.0
                val invalid = numericAmount <= 0.0 || numericAmount > 500000.0

                if (numericAmount > 500000.0) {
                    Text("Maximum bank limit reached!", color = AlertRed, fontSize = 10.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirm(numericAmount) },
                        enabled = !invalid,
                        colors = ButtonDefaults.buttonColors(containerColor = CorporateGold, contentColor = RichBlack),
                        modifier = Modifier.testTag("confirm_apply_loan_btn")
                    ) {
                        Text("RECOVER LEVERAGE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
