package com.heril.recipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.heril.recipe.ui.theme.RecipeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            RecipeTheme {
                Surface(modifier=Modifier.fillMaxSize(),color= MaterialTheme.colorScheme.background){
                    RecipeApp(navController = navController)
                }
            }
        }
    }
}

sealed class Screen(val route:String){
    object RecipeScreen:Screen("recipescreen")
    object DetailScreen:Screen("detailscreen")
}

@Composable
fun RecipeApp(navController: NavHostController){
    val recipeViewModel: MainViewModel = viewModel()
    val viewstate by recipeViewModel.categoriesState

    NavHost(navController = navController, startDestination = Screen.RecipeScreen.route){
        composable(route = Screen.RecipeScreen.route){
            RecipeScreen(viewstate = viewstate, navigateToDetail = {
                navController.currentBackStackEntry?.savedStateHandle?.set("cat",it)
                navController.navigate(Screen.DetailScreen.route)
            })
        }
        composable(route = Screen.DetailScreen.route){
            val category = navController.previousBackStackEntry?.savedStateHandle?.
            get<Category>("cat") ?: Category("","","","")
            CategoryDetailScreen(category = category)
        }
    }
}
@Composable
fun RecipeScreen(modifier: Modifier = Modifier,
                 viewstate: MainViewModel.RecipeState,
                 navigateToDetail: (Category) -> Unit
                 ) {
    val recipeViewModel: MainViewModel= viewModel()

    Box(modifier=Modifier.fillMaxSize()){
        when{
            viewstate.loading ->{
                CircularProgressIndicator(modifier.align(Alignment.Center))
            }
            viewstate.error !=null ->{
                Text(text = "ERROR OCCURED")
            }
            else ->{
                CategoryScreen(categories = viewstate.list,navigateToDetail)
            }
        }
    }
}

@Composable
fun CategoryScreen(categories: List<Category>,
                   navigateToDetail: (Category) -> Unit
                   ){
    LazyVerticalGrid(GridCells.Fixed(2),modifier=Modifier.fillMaxSize()) {
        items(categories){
            category ->
            CategoryItem(category=category,navigateToDetail)
        }
    }
}

@Composable
fun CategoryItem(category: Category,
                 navigateToDetail: (Category) -> Unit
                 ){
    Column(modifier = Modifier
        .padding(8.dp)
        .fillMaxSize()
        .clickable { navigateToDetail(category) },
        horizontalAlignment=Alignment.CenterHorizontally)
    {
        Image(
            painter = rememberAsyncImagePainter(category.strCategoryThumb),
            contentDescription = "${category.strCategory} Thumbnail", //Handle Accessibility
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )

        Text(
            text = category.strCategory,
            style = TextStyle(fontWeight= FontWeight.Bold, fontSize = 24.sp),
            modifier = Modifier.padding(top=4.dp)
            )
    }
}

@Composable
fun CategoryDetailScreen(category: Category){
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
        horizontalAlignment=Alignment.CenterHorizontally
    ) {
        Text(text = category.strCategory,
            style = TextStyle(fontWeight= FontWeight.Bold, fontSize = 32.sp),
            textAlign = TextAlign.Center)
        Image(
            painter = rememberAsyncImagePainter(category.strCategoryThumb),
            contentDescription = "${category.strCategory} Thumbnail", //Handle Accessibility
            modifier = Modifier
                .wrapContentSize()
                .aspectRatio(1f)
        )
        Text(text = category.strCategoryDescription,
            style = TextStyle(fontSize = 24.sp),
            textAlign = TextAlign.Justify,
            modifier = Modifier.verticalScroll(rememberScrollState())
            )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeScreenPreview() {
    //RecipeScreen()
    val recipeViewModel: MainViewModel= viewModel()
    val viewstate by recipeViewModel.categoriesState
    //CategoryScreen(categories=viewstate.list)
}