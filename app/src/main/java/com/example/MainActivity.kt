package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.IntroScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val gameViewModel: GameViewModel = viewModel()
                    val isGameStarted by gameViewModel.isGameStarted.collectAsState()

                    if (!isGameStarted) {
                        val saves by gameViewModel.availableSaves.collectAsState()
                        IntroScreen(
                            availableSaves = saves,
                            onStartGame = { name, industry, difficulty ->
                                gameViewModel.initializeNewGame(name, industry, difficulty)
                            },
                            onLoadGame = { slotId ->
                                gameViewModel.loadGameSlot(slotId)
                            },
                            onDeleteGame = { slotId ->
                                gameViewModel.deleteGameSlot(slotId)
                            }
                        )
                    } else {
                        // Gather game states
                        val companyName by gameViewModel.companyName.collectAsState()
                        val cash by gameViewModel.cash.collectAsState()
                        val reputation by gameViewModel.reputation.collectAsState()
                        val satisfaction by gameViewModel.satisfaction.collectAsState()
                        val turn by gameViewModel.currentTurn.collectAsState()
                        val industry by gameViewModel.currentIndustry.collectAsState()
                        val difficulty by gameViewModel.difficulty.collectAsState()
                        val employees by gameViewModel.employees.collectAsState()
                        val products by gameViewModel.products.collectAsState()
                        val competitors by gameViewModel.competitors.collectAsState()
                        val loans by gameViewModel.loans.collectAsState()
                        val properties by gameViewModel.properties.collectAsState()
                        val stocksOwned by gameViewModel.stocksOwned.collectAsState()
                        val isIpoDone by gameViewModel.isIpoDone.collectAsState()
                        val sharePercentOwned by gameViewModel.sharePercentOwned.collectAsState()
                        val playerStockHistory by gameViewModel.playerStockHistory.collectAsState()
                        val playerStockPrice by gameViewModel.playerStockPrice.collectAsState()
                        val rdProgress by gameViewModel.rdProgress.collectAsState()
                        val marketCycleName by gameViewModel.marketCycleName.collectAsState()
                        val marketBoomCycle by gameViewModel.marketBoomCycle.collectAsState()
                        val taxOwed by gameViewModel.taxOwed.collectAsState()
                        val financials by gameViewModel.financials.collectAsState()
                        val activeCrisis by gameViewModel.activeCrisis.collectAsState()
                        val isGeneratingCrisis by gameViewModel.isGeneratingCrisis.collectAsState()
                        val gameLogs by gameViewModel.gameLogs.collectAsState()
                        val rankings = gameViewModel.getLeaderboardRankings()

                        MainDashboardScreen(
                            companyName = companyName,
                            cash = cash,
                            reputation = reputation,
                            satisfaction = satisfaction,
                            turn = turn,
                            industry = industry,
                            difficulty = difficulty,
                            employees = employees,
                            products = products,
                            competitors = competitors,
                            loans = loans,
                            properties = properties,
                            stocksOwned = stocksOwned,
                            isIpoDone = isIpoDone,
                            sharePercentOwned = sharePercentOwned,
                            playerStockHistory = playerStockHistory,
                            playerStockPrice = playerStockPrice,
                            rdProgress = rdProgress,
                            marketCycleName = marketCycleName,
                            marketBoomCycle = marketBoomCycle,
                            taxOwed = taxOwed,
                            financials = financials,
                            activeCrisis = activeCrisis,
                            isGeneratingCrisis = isGeneratingCrisis,
                            gameLogs = gameLogs,
                            onHireEmployee = { name, role, skill, sal ->
                                gameViewModel.hireEmployee(name, role, skill, sal)
                            },
                            onFireEmployee = { id ->
                                gameViewModel.fireEmployee(id)
                            },
                            onTrainEmployees = {
                                gameViewModel.trainEmployees()
                            },
                            onBuyProperty = { id ->
                                gameViewModel.buyProperty(id)
                            },
                            onTakeLoan = { amount ->
                                gameViewModel.takeLoan(amount)
                            },
                            onRepayLoan = { id ->
                                gameViewModel.repayLoan(id)
                            },
                            onBuyStock = { cid, qty ->
                                gameViewModel.buyCompetitorStock(cid, qty)
                            },
                            onSellStock = { cid, qty ->
                                gameViewModel.sellCompetitorStock(cid, qty)
                            },
                            onLaunchIpo = {
                                gameViewModel.launchIpo()
                            },
                            onBuyBackShares = { pct ->
                                gameViewModel.buyBackOwnShares(pct)
                            },
                            onDevelopNewProduct = { name, bCost, pPrice, mkt ->
                                gameViewModel.developNewProduct(name, bCost, pPrice, mkt)
                            },
                            onExecuteResearch = {
                                gameViewModel.executeResearch()
                            },
                            onResolveCrisis = { idx ->
                                gameViewModel.resolveBoardroomCrisis(idx)
                            },
                            onEndTurn = {
                                gameViewModel.endTurnAndAdvance()
                            },
                            onExitGame = {
                                // Save slot and exit home screen
                                gameViewModel.saveGameSlot("autosave")
                                // Back to menu safely
                                gameViewModel.exitGame()
                            },
                            leaderboardItems = rankings
                        )
                    }
                }
            }
        }
    }
}
