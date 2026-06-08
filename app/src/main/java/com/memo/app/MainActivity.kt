package com.memo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.memo.app.ui.screens.MemoEditorScreen
import com.memo.app.ui.screens.MemoListScreen
import com.memo.app.ui.screens.MemoViewModel
import com.memo.app.ui.theme.MemoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoAppTheme {
                MemoNavHost()
            }
        }
    }
}

@Composable
fun MemoNavHost() {
    val navController = rememberNavController()
    val viewModel: MemoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "list",
        modifier = Modifier.fillMaxSize(),
        enterTransition = { slideInHorizontally(tween(300)) { it } },
        exitTransition = { slideOutHorizontally(tween(300)) { -it / 3 } },
        popEnterTransition = { slideInHorizontally(tween(300)) { -it / 3 } },
        popExitTransition = { slideOutHorizontally(tween(300)) { it } }
    ) {
        composable("list") {
            MemoListScreen(
                viewModel = viewModel,
                onNavigateToEditor = { memoId ->
                    navController.navigate("editor/${memoId ?: 0}")
                }
            )
        }
        composable(
            "editor/{memoId}",
            arguments = listOf(navArgument("memoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val memoId = backStackEntry.arguments?.getLong("memoId") ?: 0L
            MemoEditorScreen(
                viewModel = viewModel,
                memoId = if (memoId == 0L) null else memoId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
