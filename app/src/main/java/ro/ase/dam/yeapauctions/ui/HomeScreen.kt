package ro.ase.dam.yeapauctions.ui

import KtorHttpClient
import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.bson.types.ObjectId
import ro.ase.dam.yeapauctions.R
import ro.ase.dam.yeapauctions.classes.*
import ro.ase.dam.yeapauctions.data.Datasource
import ro.ase.dam.yeapauctions.ui.components.FlagEmoji
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import ro.ase.dam.yeapauctions.data.Category
import ro.ase.dam.yeapauctions.data.CategorySaver
import ro.ase.dam.yeapauctions.data.Datasource.address
import ro.ase.dam.yeapauctions.data.Datasource.addresses
import ro.ase.dam.yeapauctions.data.Datasource.auctions
import ro.ase.dam.yeapauctions.data.Datasource.descriptions
import ro.ase.dam.yeapauctions.data.Datasource.links
import ro.ase.dam.yeapauctions.data.Datasource.lots
import ro.ase.dam.yeapauctions.data.Datasource.offers
import ro.ase.dam.yeapauctions.data.Datasource.lotLink
import ro.ase.dam.yeapauctions.data.local.LocalCategoryDataProvider

enum class Routes(@StringRes val title: Int) {
    HOME(R.string.home_screen),
    CATEGORIES(R.string.categories),
    FAVORITES(R.string.favorites),
    SETTINGS(R.string.settings),
    HOME_CONTENT(R.string.home_content),
    CATEGORIES_CONTENT(R.string.categories_content),
    CATEGORY_SELECTED(R.string.category_selected),
    AUCTION_TAB(R.string.auction_tab),
    LOT_TAB(R.string.lot_tab),
    FAVORITES_CONTENT(R.string.tab_favorite),
    SETTINGS_CONTENT(R.string.tab_settings),
    PAYMENTS_CONTENT(R.string.payments),
    DETAILS_CONTENT(R.string.personal_details)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: String,
    onTransactionRequest: (Payment) -> Unit,
    onLogoutClicked: (ObjectId?) -> Unit
) {
    var selectedItem by rememberSaveable { mutableStateOf(0) }
    val textStyle = MaterialTheme.typography.labelSmall
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(width = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
                    .shadow(4.dp)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(null)
                val currentDestination = navBackStackEntry?.destination
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = stringResource(id = R.string.tab_home),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(bottom = 12.dp)
                        )
                    },
                    label = {
                        Text(
                            stringResource(id = R.string.tab_home),
                            style = textStyle
                        )
                    },
                    selected = currentDestination?.route == Routes.HOME.name,
                    onClick = {
                        navController.navigate(Routes.HOME.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.gavel),
                            contentDescription = stringResource(id = R.string.tab_auctions),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(bottom = 12.dp)
                        )
                    },
                    label = {
                        Text(
                            stringResource(id = R.string.tab_auctions),
                            style = textStyle
                        )
                    },
                    selected = currentDestination?.route == Routes.CATEGORIES.name,
                    onClick = {
                        navController.navigate(Routes.CATEGORIES.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedItem != 2) Icons.Outlined.FavoriteBorder else Icons.Outlined.Favorite,
                            contentDescription = stringResource(id = R.string.tab_favorite),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(bottom = 12.dp)
                        )
                    },
                    label = {
                        Text(
                            stringResource(id = R.string.tab_favorite),
                            style = textStyle
                        )
                    },
                    selected = currentDestination?.route == Routes.FAVORITES.name,
                    onClick = {
                        navController.navigate(Routes.FAVORITES.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = stringResource(id = R.string.tab_settings),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(bottom = 12.dp)
                        )
                    },
                    label = {
                        Text(
                            stringResource(id = R.string.tab_settings),
                            style = textStyle
                        )
                    },
                    selected = currentDestination?.route == Routes.SETTINGS.name,
                    onClick = {
                        navController.navigate(Routes.SETTINGS.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
        }
    }){
        NavHost(
            navController = navController,
            startDestination = Routes.HOME.name,
            Modifier.padding(it),
        ){
            composable(route = Routes.HOME.name){
                HomeTab(userId)
            }
            composable(route = Routes.CATEGORIES.name){
                CategoriesTab(userId)
            }
            composable(route = Routes.FAVORITES.name){
                FavoritesTab(userId)
            }
            composable(route = Routes.SETTINGS.name){
                SettingsTab(
                    userId,
                    {
                        navController.navigate(Routes.FAVORITES.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogoutClicked,
                    onTransactionRequest
                )
            }
        }
    }
}

@Composable
fun TopBarWithSearch(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearch: (List<Lot>) -> Unit,
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .height(56.dp)
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
            .shadow(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.rbw_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(8.dp)
                    .size(56.dp)
            )
            SearchBar(
                query = query,
                onQueryChanged = onQueryChanged,
                onSearch = onSearch
            )
        }
    }
}

@Composable
fun TopBarWithSearchAndCancel(query: String,
                              onQueryChanged: (String) -> Unit,
                              onSearch: (List<Lot>) -> Unit,
                              onCancel: () -> Unit) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.inversePrimary)
            .height(56.dp)
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
            .shadow(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .padding(8.dp)
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Cancel Search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
                )
            }
            SearchBar(
                query = query,
                onQueryChanged = onQueryChanged,
                onSearch = onSearch,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearch: (List<Lot>) -> Unit,
    modifier: Modifier = Modifier,
) {

    val matchingLots = remember(lots, query) {
        lots.filter { lot ->
            val description = descriptions.firstOrNull { it.id == lot.descriptionId }
            (
                    description?.informations?.contains(query, ignoreCase = true) == true ||
                            lot.name.contains(query, ignoreCase = true) ||
                            lot.category.contains(query, ignoreCase = true) ||
                            lot.subcategory.contains(query, ignoreCase = true)
                    ) && ((lot.startTime.time < System.currentTimeMillis() && lot.endTime.time > System.currentTimeMillis()))
        }
    }

    val focusManager = LocalFocusManager.current
    Surface(
        color = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.padding(top = 6.dp, bottom = 6.dp, end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                value = query,
                onValueChange = onQueryChanged,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search),
                        style = MaterialTheme.typography.labelSmall)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSearch(matchingLots)
                        focusManager.clearFocus()
                    }
                ),
                textStyle = MaterialTheme.typography.labelSmall
            )
        }
    }
}


@Composable
fun TopBar(@StringRes title: Int) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .height(56.dp)
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
            .shadow(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = title),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun TopBarWithBack(
    @StringRes back: Int,
    @StringRes title: Int,
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .height(56.dp)
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
            .shadow(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable { onBackClicked() }
            )
            Text(
                text = stringResource(id = back),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
        Text(
            text = stringResource(id = title),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@SuppressLint("MutableCollectionMutableState")
@Composable
fun HomeTab(
    userId: String
) {
    var query by rememberSaveable { mutableStateOf("") }
    var lotsQuery by rememberSaveable(stateSaver = LotListSaver) {
         mutableStateOf(listOf())
    }

    val navController: NavHostController = rememberNavController()
    var auction by rememberSaveable(stateSaver = AuctionSaver){
        mutableStateOf(Auction())
    }
    var lot by rememberSaveable(stateSaver = LotSaver){
        mutableStateOf(Lot())
    }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(null)
            val currentDestination = navBackStackEntry?.destination
            when (currentDestination?.route){
                Routes.HOME_CONTENT.name -> if(!lotsQuery.isNullOrEmpty()){
                        TopBarWithSearchAndCancel(
                            query = query,
                            onQueryChanged = { query = it },
                            onSearch = { lotsQuery = it.toMutableStateList() },
                            onCancel = { query = ""; lotsQuery = listOf()}
                        )
                    }
                    else{
                        TopBarWithSearch(
                            query = query,
                            onQueryChanged = { query = it },
                            onSearch = { lotsQuery = it.toMutableStateList() }
                        )
                    }


                Routes.AUCTION_TAB.name -> TopBarWithBack(
                    back = R.string.back,
                    title = R.string.auction_tab,
                    onBackClicked = {
                        navController.popBackStack(
                            Routes.HOME_CONTENT.name,
                            inclusive = false
                        )
                    }
                )
                Routes.LOT_TAB.name -> TopBarWithBack(
                    back = R.string.auction_tab,
                    title = R.string.lot_tab,
                    onBackClicked = {
                        navController.popBackStack(
                            Routes.AUCTION_TAB.name,
                            inclusive = false
                        )
                    }
                )
            }

        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME_CONTENT.name,
            Modifier.padding(it),
        ){
            composable(route = Routes.HOME_CONTENT.name){
                HomeContent(
                    userId,
                    lotsQuery,
                    onAuctionClicked = { it->
                    auction = it
                    coroutineScope.launch {
                        val job1 = async { Datasource.loadAddress(auction)}
                        job1.await()
                        navController.navigate(Routes.AUCTION_TAB.name)
                    }
                },
                    onLotClicked = { it->
                        lot = it
                        coroutineScope.launch {
                            val job1 = async { Datasource.loadLink(userId, lot) }
                            job1.await();
                        }
                        navController.navigate(Routes.LOT_TAB.name)
                    }
                )
            }
            composable(route = Routes.AUCTION_TAB.name){
                AuctionTab(
                    userId,
                    auction = auction,
                    onLotClicked = { it->
                        lot = it
                        coroutineScope.launch {
                            val job1 = async { Datasource.loadLink(userId, lot) }
                            job1.await();
                        }
                        navController.navigate(Routes.LOT_TAB.name)
                    }
                )
            }
            composable(route = Routes.LOT_TAB.name){
                LotTab(
                    userId,
                    lot
                )
            }
        }
    }
}



@Composable
fun HomeContent(
    userId: String,
    lotsQuery : List<Lot>?,
    onAuctionClicked : (Auction) -> Unit,
    onLotClicked: (Lot) -> Unit
){
    var showClosed by rememberSaveable { mutableStateOf(false) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var countryCodes: List<String> by rememberSaveable { mutableStateOf(emptyList()) }
    var checkedStates = remember { mutableStateListOf<Boolean>() }
    var selectedCountryCodes: List<String> by rememberSaveable { mutableStateOf(emptyList()) }
    if (countryCodes.isEmpty()) {
        for (address in addresses.distinctBy { it.country }.sortedBy { it.country }) {
            countryCodes = countryCodes + address.country
        }
    }
    if (checkedStates.isEmpty()) {
        for (i in countryCodes.indices) {
            checkedStates.add(false)
        }
    }
    selectedCountryCodes = countryCodes.filterIndexed { index, _ ->
        checkedStates[index]
    }

    Scaffold(
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                        if(lotsQuery.isNullOrEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Text(
                                        text = stringResource(R.string.show_closed),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Box(
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        Switch(
                                            checked = showClosed,
                                            onCheckedChange = { showClosed = !showClosed },
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    shadowElevation = 4.dp,
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .widthIn(max = 120.dp)
                                        .height(40.dp),
                                ) {
                                    Button(
                                        onClick = { showDialog = true },
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            Icon(
                                                Icons.Default.LocationOn,
                                                contentDescription = "Location Icon",
                                            )
                                            Text(
                                                text = stringResource(R.string.country),
                                                style = TextStyle(
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp,
                                                    letterSpacing = 0.15.sp,
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            if (auctions.isNotEmpty()) {
                                val rows = auctions.chunked(2)
                                rows.forEach { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        rowItems.forEach { auction ->
                                            AuctionCard(
                                                auction,
                                                showClosed,
                                                selectedCountryCodes,
                                                onAuctionClicked,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }

                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    title = { Text(stringResource(R.string.select_countries)) },
                                    text = {
                                        Column {
                                            for ((index, countryCode) in countryCodes.withIndex()) {
                                                Row(modifier = Modifier.padding(bottom = 16.dp)) {
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(end = 8.dp)
                                                            .size(12.dp)
                                                    ) {
                                                        Checkbox(
                                                            checked = checkedStates[index],
                                                            onCheckedChange = {
                                                                checkedStates[index] = it
                                                            },
                                                        )
                                                    }
                                                    FlagEmoji(
                                                        countryCode,
                                                        modifier = Modifier.padding(end = 2.dp)
                                                    )
                                                    Text(
                                                        text = countryCode,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = { showDialog = false }
                                        ) {
                                            Text(stringResource(R.string.ok))
                                        }
                                    },
                                )
                            }
                        }else{
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                val rows = lotsQuery.chunked(2)
                                rows.forEach { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        rowItems.forEach { lot ->
                                            LotCard(
                                                userId,
                                                lot,
                                                onLotClicked,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LotTab(
    userId: String,
    lot: Lot
){
    var lotOffers = offers.filter { it.lotId == lot.id}
    val auction = auctions.first { it.id == lot.auctionId}
    val address = addresses.first { it.id == auction.addressId }
    val description  = descriptions.firstOrNull { it.id == lot.descriptionId }
    val pathsList: List<String> = description?.imagePaths?.split(",") ?: emptyList()
    var price by rememberSaveable { mutableStateOf(0.0)}

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showBiddingDialog by rememberSaveable { mutableStateOf(false) }

    val firstPagerState = rememberPagerState()
    var firstPath by rememberSaveable { mutableStateOf(pathsList[0])}
    var secondPath by rememberSaveable { mutableStateOf(pathsList[0])}

    var category by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val calendar = Calendar.getInstance()
    calendar.time = lot.endTime
    val endTimeInMillis = calendar.timeInMillis
    var remainingMillis by remember { mutableStateOf(endTimeInMillis - System.currentTimeMillis()) }
    val minutesLeft = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
    val focusManager = LocalFocusManager.current

    LaunchedEffect(category){
        if(category == "red"){
            showBiddingDialog = false
        }
    }
    LaunchedEffect(lotLink) {
        try {
            KtorHttpClient.put("/api/links/" + lotLink.id){
                contentType(ContentType.Application.Json)
                setBody(lotLink)
            }
            Datasource.clientSocket.emit("linksUpdate")
        } catch (e: Exception) {
            Log.e("updateState", "Failed to update state from DB: ${e.message}", e)
        }
    }
    LaunchedEffect(lotOffers){
        if(lotOffers.isEmpty()){
            price  = lot.startingPrice
        }else{
            price  = lotOffers.maxBy{it.amount}.amount
            if(price > 0 && price < 50){
                price += 5
            }else if(price >= 50 && price < 250){
                price += 10
            }else if(price >= 250 && price < 600){
                price += 20
            }else if(price >= 600 && price < 2000){
                price += 50
            }else if(price >= 2000 && price < 4000){
                price += 100
            }else if(price >= 4000 && price < 10000){
                price += 200
            } else if(price >= 10000 && price < 15000){
                price += 250
            }else if(price >= 15000 && price < 20000){
                price += 500
            }else if(price >= 20000 && price < 50000){
                price += 1000
            }else if(price >= 50000 && price < 100000){
                price += 1500
            }else if(price >= 100000 && price < 200000){
                price += 2000
            }else if(price >= 200000 && price < 900000){
                price += 5000
            }else if(price >= 900000){
                price += 10000
            }
        }
    }


    Scaffold { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = lot.name,
                        style = MaterialTheme.typography.bodyMedium
                            .copy(fontWeight = FontWeight.Bold),
                    )
                    Row() {
                        FlagEmoji(
                            countryCode = address.country,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                        Text(
                            text = address.city + " , " + address.country,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    if(category == "red"){
                        OutlinedTextField(
                            value = stringResource(R.string.lot_closed),
                            onValueChange = {},
                            singleLine = true,
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            textStyle = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            shape = RoundedCornerShape(4.dp),
                            enabled = false,
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                            )
                        )
                    }else{
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = String.format("%,.2f$", price),
                                onValueChange = {},
                                singleLine = true,
                                readOnly = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                textStyle = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                shape = RoundedCornerShape(4.dp),
                                enabled = false,
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = Color.Black,
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                onClick = { showBiddingDialog = true },
                            ) {
                                Text(
                                    text = stringResource(R.string.check_bid),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable {
                                        lotLink = if (lotLink.isFavorite) {
                                            lotLink.copy(isFavorite = false)
                                        } else {
                                            lotLink.copy(isFavorite = true)
                                        }
                                    }
                                    .border(
                                        width = 1.dp,
                                        color = Color.Gray,
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (lotLink.isFavorite) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                                    contentDescription = "Favorite",
                                    tint = if (lotLink.isFavorite) Color.Red else Color.hsl(
                                        0f,
                                        0f,
                                        0.4f
                                    ),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(2.dp)
                                )
                            }
                        }
                    }

                    if (showBiddingDialog) {
                        var amount by rememberSaveable { mutableStateOf(String.format("%.2f", price))}
                        Dialog(onDismissRequest = { showBiddingDialog = false }) {
                            Box(
                                contentAlignment = Alignment.TopStart,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clickable { showDialog = false }
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(8.dp)
                                ){
                                    Text(
                                        text = lot.name,
                                        style = MaterialTheme.typography.bodyMedium
                                            .copy(fontWeight = FontWeight.Bold),
                                        textAlign = TextAlign.Start
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Text(
                                            text = stringResource(R.string.next_bid),
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        )
                                        Text(
                                            text = stringResource(R.string.bids),
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = String.format("%,.2f$", price),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        )
                                        Text(
                                            text = lotOffers.size.toString(),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        )
                                    }
                                    CountDownLine(lot = lot, category = category, onCategoryUpdate = { category = it })
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Outline()
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = amount,
                                        onValueChange = {
                                              amount = it
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        textStyle = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                focusManager.clearFocus()
                                            }
                                        ),
                                        shape = RoundedCornerShape(4.dp),
                                        colors = TextFieldDefaults.colors(
                                            disabledTextColor = Color.Black,
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Outline()
                                    Spacer(modifier = Modifier.height(4.dp))
                                    var amountDouble by rememberSaveable { mutableStateOf(0.0) }
                                    var isValidInput by rememberSaveable { mutableStateOf(true) }
                                    try{
                                        amountDouble = amount.toDouble()
                                        isValidInput = true
                                    }
                                    catch(e: Exception){
                                        isValidInput = false
                                    }
                                    if(amountDouble < price && amountDouble != 0.0){
                                        Text(
                                            text = stringResource(R.string.low_bid),
                                            style = MaterialTheme.typography.labelSmall
                                                .copy(fontWeight =  FontWeight.Bold),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }else{
                                        if(isValidInput){
                                            val auctionCost = amountDouble * 0.18
                                            val vat = amountDouble * lot.vat / 100
                                            val auctionVAT = amountDouble * 0.18 * 0.19
                                            val total = amountDouble + auctionCost + vat + auctionVAT
                                            PricingRow(
                                                stringResource(R.string.your_bid),
                                                amount = amountDouble
                                            )
                                            PricingRow(
                                                stringResource(R.string.auction_costs) + " (18.0%)",
                                                amount = auctionCost
                                            )
                                            PricingRow(
                                                stringResource(R.string.vat) + " (" + String.format("%,.2f", lot.vat) + ")",
                                                amount = vat
                                            )
                                            PricingRow(
                                                stringResource(R.string.vat_auction) + " (19.0%)",
                                                amount = auctionVAT
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.total),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .weight(1f),
                                                    textAlign = TextAlign.Start
                                                )
                                                Text(
                                                    text = String.format("%,.2f $", total),
                                                    style = MaterialTheme.typography.labelMedium
                                                        .copy(
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .weight(1f),
                                                    textAlign = TextAlign.End,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Outline()
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Button(
                                                onClick = {

                                                    if(minutesLeft <= 2 && lot.lastTwo < 3){
                                                        lot.lastTwo++
                                                        calendar.add(Calendar.MINUTE, 2)
                                                        lot.endTime = calendar.time
                                                    }else if(minutesLeft <= 5 && lot.lastFive < 3){
                                                        lot.lastFive++
                                                        calendar.add(Calendar.MINUTE, 5)
                                                        lot.endTime = calendar.time
                                                    }
                                                          coroutineScope.launch {
                                                              try {
                                                                  KtorHttpClient.put("/api/lots/" + lot.id) {
                                                                      contentType(ContentType.Application.Json)
                                                                      setBody(lot)
                                                                  }
                                                              } catch (e: Exception) {
                                                                  Log.e(
                                                                      "updateLot",
                                                                      "Failed to update lot to DB: ${e.message}",
                                                                      e
                                                                  )
                                                              }
                                                              Datasource.submitOffer(lot, userId, amountDouble)
                                                              Datasource.clientSocket.emit("offerAdded")
                                                          }
                                                    showBiddingDialog = false
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.place_bid),
                                                    style = MaterialTheme.typography.labelMedium
                                                        .copy(
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .weight(1f),
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Outline()
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = stringResource(R.string.accept_terms),
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }else{
                                            Text(
                                                text = stringResource(R.string.enter_valid),
                                                style = MaterialTheme.typography.labelSmall
                                                    .copy(fontWeight =  FontWeight.Bold),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HorizontalPager(
                        state = firstPagerState,
                        pageCount = pathsList.size,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                            .background(Color.White)
                            .border(1.dp, Color.Gray.copy(alpha = 0.7f))
                            .clip(RoundedCornerShape(8.dp))
                    ) { page ->
                        firstPath = pathsList[page]
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = "http://192.168.0.183:5001/$firstPath"),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { showDialog = true }
                            )
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(4.dp)
                                    .align(AbsoluteAlignment.BottomRight)
                            ) {
                                Text(
                                    text = stringResource(R.string.page) +
                                            " ${page + 1} " + stringResource(R.string.of) +
                                            " ${pathsList.size}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                        if (showDialog) {
                            val secondPagerState = rememberPagerState(
                                initialPage = firstPagerState.currentPage
                            )
                            Dialog(onDismissRequest = { showDialog = false }) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { showDialog = false }
                                ) {
                                    HorizontalPager(
                                        state = secondPagerState,
                                        pageCount = pathsList.size,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                    ) { page ->
                                        secondPath = pathsList[page]
                                        Image(
                                            painter = rememberAsyncImagePainter(model = "http://192.168.0.183:5001/$secondPath"),
                                            contentDescription = null,
                                            contentScale = ContentScale.FillWidth,
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(pathsList.size) { iteration ->
                            val color = if (firstPagerState.currentPage == iteration) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            val size = if (firstPagerState.currentPage == iteration) 10.dp else 6.dp
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(size)
                            )
                        }
                    }
                    CountDownLine(
                        lot = lot,
                        category = category,
                        onCategoryUpdate = { category = it}
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if(category == "red"){
                        if(lotOffers.isEmpty()){
                            OutlinedTextField(
                                value = stringResource(R.string.no_bids),
                                onValueChange = {},
                                singleLine = true,
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                textStyle = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                shape = RoundedCornerShape(4.dp),
                                enabled = false,
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = Color.Black,
                                )
                            )
                        }else {
                            LotRow(R.string.final_bid)
                        }
                    }else{
                        if(lotOffers.isEmpty()){
                            LotRow(R.string.opening_bid)
                        }else{
                            LotRow(R.string.next_bid)
                        }
                    }
                    if(!((category == "red") && lotOffers.isEmpty())) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("%,.2f$", price.toFloat()),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            Text(
                                text = lotOffers.size.toString(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Outline()
                    Text(
                        text = stringResource(R.string.about),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                    Outline()
                    Text(
                        text = stringResource(R.string.description),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                    if (description != null) {
                        DescriptionRow(
                            drawableId = R.drawable.ic_search,
                            stringId = R.string.condition,
                            label = description.condition,
                            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                            )
                        DescriptionRow(
                            drawableId = R.drawable.ic_settings,
                            stringId = R.string.status,
                            label = description.status,
                            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                        )
                        DescriptionRow(
                            drawableId = R.drawable.ic_eye,
                            stringId = R.string.appearance,
                            label = description.appearance,
                            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                        )
                        DescriptionRow(
                            drawableId = R.drawable.ic_package,
                            stringId = R.string.packaging,
                            label = description.packaging,
                            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                        )
                        DescriptionRow(
                            drawableId = R.drawable.ic_layers,
                            stringId = R.string.quantity,
                            label = description.quantity.toString(),
                            modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                        )
                        Text(
                            text = description.informations,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxSize(),
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PricingRow(
    message: String,
    amount: Double
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.W300
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textAlign = TextAlign.Start
        )
        Text(
            text = String.format("%,.2f $", amount),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textAlign = TextAlign.End
        )
    }
}
@Composable
fun LotRow(
    @StringRes stringId: Int
){
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = stringResource(stringId),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Text(
            text = stringResource(R.string.number_bids),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}


@Composable
fun DescriptionRow(
    @DrawableRes drawableId: Int,
    @StringRes stringId: Int,
    label: String,
    modifier: Modifier
){
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
    ){
        Box(
            modifier = Modifier.size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = drawableId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = stringResource(id = stringId),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.W300
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun Outline(){
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3F))
    )
}
@Composable
fun AuctionTab(
    userId: String,
    auction: Auction,
    onLotClicked: (Lot) -> Unit
){
    val auctionLots = lots.filter { auction.id == it.auctionId}
    var activeButton by rememberSaveable { mutableStateOf(0) }
    val firstLot = auctionLots.minBy { it.endTime }
    val lastLot = auctionLots.maxBy { it.endTime}
    var category by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val currentTime = System.currentTimeMillis()


    var openLots = auctionLots.filter { currentTime < it.endTime.time  }
    var closedLots = auctionLots.filter { currentTime > it.startTime.time && currentTime > it.endTime.time }

    Scaffold(
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = auction.name,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Row(){
                            FlagEmoji(countryCode = address.country, modifier = Modifier.padding(end = 2.dp))
                            Text(
                                text = address.city + " , " + address.country + " | "
                                        + auctionLots.size.toString() + " " + stringResource(R.string.lots),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        if(firstLot.endTime.time > System.currentTimeMillis()){
                            CountDownLine(lot = firstLot, category = category, onCategoryUpdate = { category = it })
                        }else{
                            CountDownLine(lot = lastLot, category = category, onCategoryUpdate = { category = it })
                        }
                        //AuctionDetails()
                        Row(Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { activeButton = 0 },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp, top = 4.dp)
                                    .height(48.dp)
                                    .background(if (activeButton == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = if (activeButton == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(text = stringResource(R.string.open_lots) + " (" + openLots.size + ")", style = MaterialTheme.typography.labelMedium)
                            }
                            Box(modifier = Modifier
                                .size(2.dp, 48.dp)
                                .background(MaterialTheme.colorScheme.onSurface)
                                .align(Alignment.CenterVertically)
                                .offset(x = (activeButton * 2 - 1) * 25.dp)
                                .animateContentSize(animationSpec = tween(durationMillis = 200))
                            )
                            Button(
                                onClick = { activeButton = 1 },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp, top = 4.dp)
                                    .height(48.dp)
                                    .background(if (activeButton == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = if (activeButton == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(text = stringResource(R.string.closed_lots) + " (" + closedLots.size + ")", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        Outline()
                        if(activeButton == 0){
                            val rows = openLots.chunked(2)
                            rows.forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    rowItems.forEach { lot ->
                                        LotCard(
                                            userId,
                                            lot,
                                            onLotClicked,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }else{
                            val rows = closedLots.chunked(2)
                            rows.forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    rowItems.forEach { lot ->
                                        LotCard(
                                            userId,
                                            lot,
                                            onLotClicked,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun LotCard(
    userId: String,
    lot: Lot,
    onLotClicked : (Lot) -> Unit,
    modifier: Modifier
){
    val description  = descriptions.firstOrNull { it.id == lot.descriptionId }
    val pathsList: List<String> = description?.imagePaths?.split(",") ?: emptyList()
    var category by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    var lotCardLink by rememberSaveable(stateSaver = LinkSaver){
        mutableStateOf(Link())
    }

    var lotOffers = offers.filter { it.lotId == lot.id}
    var price by rememberSaveable { mutableStateOf(0.0)}
    LaunchedEffect(lotOffers){
        if(lotOffers.isEmpty()){
            price  = lot.startingPrice
        }else{
            price = lotOffers.maxBy{it.amount}.amount
            if(price > 0 && price < 50){
                price += 5
            }else if(price >= 50 && price < 250){
                price += 10
            }else if(price >= 250 && price < 600){
                price += 20
            }else if(price >= 600 && price < 2000){
                price += 50
            }else if(price >= 2000 && price < 4000){
                price += 100
            }else if(price >= 4000 && price < 10000){
                price += 200
            } else if(price >= 10000 && price < 15000){
                price += 250
            }else if(price >= 15000 && price < 20000){
                price += 500
            }else if(price >= 20000 && price < 50000){
                price += 1000
            }else if(price >= 50000 && price < 100000){
                price += 1500
            }else if(price >= 100000 && price < 200000){
                price += 2000
            }else if(price >= 200000 && price < 900000){
                price += 5000
            }else if(price >= 900000){
                price += 10000
            }
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                lotCardLink  = KtorHttpClient.get("/api/links/" + userId + "/" + lot.id).body()
                Datasource.clientSocket.emit("linksUpdate")
            } catch (e: Exception) {
                Log.e("loadLink", "Failed to load link from DB: ${e.message}", e)
                lotCardLink.userId = ObjectId(userId)
                lotCardLink.lotId = lot.id
                lotCardLink.auctionId = lot.auctionId
                try{
                    KtorHttpClient.post("/api/newLink"){
                        contentType(ContentType.Application.Json)
                        setBody(lotCardLink)
                    }
                    Datasource.clientSocket.emit("linksUpdate")
                }
                catch(e: Exception){
                    Log.e("createLink", "Failed to create link to DB: ${e.message}", e)
                }
            }
        }
    }

    LaunchedEffect(lotCardLink) {
        try {
            KtorHttpClient.put("/api/links/" + lotCardLink.id){
                contentType(ContentType.Application.Json)
                setBody(lotCardLink)
            }
            Datasource.clientSocket.emit("linksUpdate")
        } catch (e: Exception) {
            Log.e("updateState", "Failed to update state from DB: ${e.message}", e)
        }
    }


    Box(modifier = modifier) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(top = 4.dp, bottom = 8.dp)
                .fillMaxWidth()
                .border(
                    1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    if (category != "yellow") {
                        onLotClicked(lot)
                    }
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    text = lot.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(top = 6.dp, end = 6.dp)
                        .clickable {
                            lotCardLink = if (lotCardLink.isFavorite) {
                                lotCardLink.copy(isFavorite = false)
                            } else {
                                lotCardLink.copy(isFavorite = true)
                            }
                        }
                        .border(width = 1.dp, color = Color.Gray, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (lotCardLink.isFavorite) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                        contentDescription = "Favorite",
                        tint = if (lotCardLink.isFavorite) Color.Red else Color.hsl(0f, 0f, 0.4f),
                        modifier = Modifier
                            .size(24.dp)
                            .padding(2.dp)
                    )
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start){
                if (pathsList.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = "http://192.168.0.183:5001/" + pathsList[0]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Row() {
                        Icon(
                            painter = painterResource(id = R.drawable.gavel),
                            contentDescription = null,
                            modifier = Modifier
                                .size(12.dp)
                                .padding(end = 2.dp)
                        )
                        Text(
                            text = String.format("%,.2f$", price),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Row() {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_group),
                            contentDescription = null,
                            modifier = Modifier
                                .size(12.dp)
                                .padding(end = 2.dp)
                        )
                        if(lotOffers.isEmpty()){
                            Text(
                                text = stringResource(R.string.no_bids),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }else{
                            Text(
                                text = lotOffers.size.toString() + " " + stringResource(R.string.bids),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    if(category == "green"){
                        Row(){
                            Icon(
                                painter = painterResource(id = R.drawable.gavel),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(12.dp)
                                    .padding(end = 2.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.place_bid),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
            CountDownLine(lot = lot, category = category, onCategoryUpdate = { category = it },)
        }
    }
}

@Composable
fun AuctionCard(auction: Auction ,
                showClosed: Boolean,
                selectedCountryCodes : List<String>,
                onAuctionClicked : (Auction) -> Unit,
                modifier: Modifier){

    val address = addresses.first { it.id == auction.addressId }
    if(selectedCountryCodes.isNotEmpty()){
        if(address.country !in selectedCountryCodes) {
            return
        }
    }

    val auctionLots = lots.filter { it.auctionId == auction.id }
    val lot = auctionLots.maxByOrNull { it.endTime }
    val description  = descriptions.firstOrNull { it.id == lot?.descriptionId }
    val pathsList: List<String> = description?.imagePaths?.split(",") ?: emptyList()
    val firstLot = auctionLots.minBy { it.endTime }
    val lastLot = auctionLots.maxBy { it.endTime}
    var category by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    if((category  == "red" && showClosed) || category == "yellow" || category == "green" || category == "") {
        Box(modifier = modifier) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        onAuctionClicked(auction)
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                            Column(){
                                Text(
                                    text = auction.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Row() {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_layers),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(end = 2.dp)
                                    )
                                    Text(
                                        text = auctionLots.size.toString() + " " + stringResource(R.string.lots),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Row(){
                                    FlagEmoji(countryCode = address.country, modifier = Modifier.padding(end = 2.dp))
                                    Text(
                                        text = address.city + " , " + address.country,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            if (pathsList.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = "http://192.168.0.183:5001/" + pathsList[0]),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                )
                            }
                }
                if(firstLot.endTime.time > System.currentTimeMillis()){
                    CountDownLine(lot = firstLot, category = category, onCategoryUpdate = { category = it })
                }else{
                    CountDownLine(lot = lastLot, category = category, onCategoryUpdate = { category = it })
                }
            }
        }
    }
}

@Composable
fun AuctionDetails(modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .background(Color.Blue.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .height(24.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                tint = Color.hsl(240F, 1F, 0.4F),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text =  stringResource(
                    R.string.auction_details,
                ),
                color = Color.hsl(240F, 1F, 0.4F),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Color.hsl(240F, 1F, 0.4F),
                modifier = Modifier.size(16.dp)
            )
        }
    }
    //TODO implement click -> details for auction tab
}

@Composable
fun CountDownLine(lot: Lot,
                  category: String,
                  onCategoryUpdate: (String) -> Unit,
                  modifier: Modifier = Modifier
){
    val calendar = Calendar.getInstance()
    var targetTimeInMillis by rememberSaveable { mutableStateOf(0L) }
    var remainingMillis by rememberSaveable { mutableStateOf(0L) }

    LaunchedEffect(lot) {
        val scope = this
        while (true) {
            val currentTime = System.currentTimeMillis()
            if (currentTime < lot.startTime.time) {
                calendar.time = lot.startTime
                targetTimeInMillis = calendar.timeInMillis
                onCategoryUpdate("yellow")
            } else if (currentTime > lot.startTime.time) {
                if (currentTime < lot.endTime.time) {
                    calendar.time = lot.endTime
                    targetTimeInMillis = calendar.timeInMillis
                    onCategoryUpdate("green")
                } else {
                    onCategoryUpdate("red")
                }
            }
            remainingMillis = targetTimeInMillis - System.currentTimeMillis()
            delay(1000)
            if (!scope.isActive) {
                break
            }
        }
    }

    val hoursLeft = TimeUnit.MILLISECONDS.toHours(remainingMillis)
    val minutesLeft = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
    val secondsLeft = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60


    val color = when (category) {
        "yellow" -> {
            Color.hsl(52F, 1F, 0.4F)
        }
        "green" -> {
            Color.hsl(120F, 1F, 0.4F)
        }
        else -> {
            Color.hsl(0F, 1F, 0.4F)
        }
    }

    val background = when (category) {
        "yellow" -> {
            Color.Yellow
        }
        "green" -> {
            Color.Green
        }
        else -> {
            Color.Red
        }
    }

    val lineText = when (category) {
        "yellow" -> {
            stringResource(
                R.string.opensIn,
            ) + " " + String.format("%02d:%02d:%02d", hoursLeft, minutesLeft, secondsLeft)
        }
        "green" -> {
            stringResource(
                R.string.endsIn,
            ) + " " + String.format("%02d:%02d:%02d", hoursLeft, minutesLeft, secondsLeft)
        }
        else -> {
            stringResource(
                R.string.closed
            )
        }
    }

    Box(
        modifier = modifier
            .background(background.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .height(24.dp)
            .border(
                width = 1.dp,
                color = color,
                RoundedCornerShape(4.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_clock),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text =  lineText,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CategoriesTab(
    userId: String
) {
    val navController: NavHostController = rememberNavController()

    var lot by rememberSaveable(stateSaver = LotSaver){
        mutableStateOf(Lot())
    }
    var category : Category by rememberSaveable(stateSaver = CategorySaver){
        mutableStateOf(Category())
    }

    var lotsQuery by rememberSaveable(stateSaver = LotListSaver) {
        mutableStateOf(listOf())
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(null)
            val currentDestination = navBackStackEntry?.destination
            when (currentDestination?.route) {
                Routes.CATEGORY_SELECTED.name  -> TopBarWithBack(
                    back = R.string.back,
                    title = category.nameResId,
                    onBackClicked = {
                        navController.popBackStack(
                            Routes.CATEGORIES_CONTENT.name,
                            inclusive = false
                        )
                    }
                )

                Routes.CATEGORIES_CONTENT.name -> TopBar(title = R.string.categories)

                Routes.LOT_TAB.name -> TopBarWithBack(
                    back = category.nameResId,
                    title = R.string.lot_tab,
                    onBackClicked = {
                        navController.popBackStack(
                            Routes.CATEGORY_SELECTED.name,
                            inclusive = false
                        )
                    }
                )
            }
        }
    ){
            NavHost(
                navController = navController,
                startDestination = Routes.CATEGORIES_CONTENT.name,
                Modifier.padding(it),
            ){
                composable(route = Routes.CATEGORIES_CONTENT.name){
                    CategoriesContent(
                        onCategoryChanged = { it->
                            category = it
                            lotsQuery = lots.filter {
                                it.category == category.categoryName.name
                            }
                            navController.navigate(Routes.CATEGORY_SELECTED.name)
                        }
                    )
                }
                composable(route = Routes.CATEGORY_SELECTED.name) {
                    LotQueryResults(
                        userId,
                        lotsQuery = lotsQuery,
                        onLotClicked = { it->
                            lot = it
                            coroutineScope.launch {
                                val job1 = async { Datasource.loadLink(userId, lot) }
                                job1.await();
                            }
                            navController.navigate(Routes.LOT_TAB.name)
                        }
                    )
                }

                composable(route = Routes.LOT_TAB.name){
                    LotTab(
                        userId,
                        lot
                    )
                }
            }
    }
}

@Composable
fun LotQueryResults(
    userId: String,
    lotsQuery: List<Lot>?,
    onLotClicked: (Lot) -> Unit) {

    Scaffold(
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                item {
                    if(!lotsQuery.isNullOrEmpty()){
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            val rows = lotsQuery.chunked(2)
                            rows.forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    rowItems.forEach { lot ->
                                        LotCard(
                                            userId,
                                            lot,
                                            onLotClicked,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CategoriesContent(
    onCategoryChanged: (Category) -> Unit
){
    val rows = LocalCategoryDataProvider.getList()
    Scaffold(
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                item {
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(color = MaterialTheme.colorScheme.onPrimary)
                                .padding(
                                    horizontal = 16.dp
                                )
                                .clickable {
                                    onCategoryChanged(rowItems)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = rowItems.iconResId),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = stringResource(rowItems.nameResId),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Outline()
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun FavoritesTab(userId: String) {
    val navController: NavHostController = rememberNavController()
    var lot by rememberSaveable(stateSaver = LotSaver){
        mutableStateOf(Lot())
    }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(null)
            val currentDestination = navBackStackEntry?.destination
            when (currentDestination?.route) {
                Routes.FAVORITES_CONTENT.name -> TopBar(R.string.tab_favorite)

                Routes.LOT_TAB.name -> TopBarWithBack(
                    back = R.string.back,
                    title = R.string.lot_tab,
                    onBackClicked = {
                        navController.popBackStack(
                            Routes.FAVORITES_CONTENT.name,
                            inclusive = false
                        )
                    }
                )
            }
        }
    ){
        NavHost(
            navController = navController,
            startDestination = Routes.FAVORITES_CONTENT.name,
            Modifier.padding(it),
        ){
            composable(route = Routes.FAVORITES_CONTENT.name){
                FavoritesContent(
                    userId = userId,
                    onLotClicked = { it->
                        lot = it
                        coroutineScope.launch {
                            val job1 = async { Datasource.loadLink(userId, lot) }
                            job1.await();
                        }
                        navController.navigate(Routes.LOT_TAB.name)
                    })
            }
            composable(route = Routes.LOT_TAB.name){
                LotTab(
                    userId,
                    lot
                )
            }
        }
    }
}

@Composable
fun FavoritesContent(
    userId: String,
    onLotClicked: (Lot) -> Unit
) {
    val options = listOf(R.string.current, R.string.won, R.string.archive)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(options[0]) }
    var tab by rememberSaveable { mutableStateOf(0)}
    val coroutineScope = rememberCoroutineScope()

    var lotsQuery by rememberSaveable(stateSaver = LotListSaver) {
        mutableStateOf(listOf())
    }

    var userLinks : List<Link> by remember { mutableStateOf(emptyList())}

    LaunchedEffect(links){
        userLinks = links.filter { it.userId == ObjectId(userId) }
    }

    LaunchedEffect(tab, userLinks, lotsQuery) {
        val newLots = mutableListOf<Lot>()
        when(tab){
            0 -> {
                for (link in userLinks){
                    lots.find { it ->
                        it.id == link.lotId && link.isFavorite &&
                                !(System.currentTimeMillis() > it.startTime.time && System.currentTimeMillis() > it.endTime.time )
                    }?.let { newLots.add(it) }
                }
            }
            1 -> {
                for (link in userLinks){
                    lots.find { it ->
                        it.id == link.lotId && link.won
                    }?.let { newLots.add(it) }
                }
            }
            2 -> {
                for (link in userLinks){
                    lots.find { it ->
                        it.id == link.lotId && link.isFavorite && !link.won &&
                                (System.currentTimeMillis() > it.startTime.time && System.currentTimeMillis() > it.endTime.time )
                    }?.let { newLots.add(it) }
                }
            }
        }
        lotsQuery = newLots
    }

    Scaffold(
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                options.forEachIndexed { index, option ->
                                    val isSelected = option == selectedOption
                                    val textColor = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                                    }
                                    val backgroundColor = if (isSelected) {
                                        MaterialTheme.colorScheme.inversePrimary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                    Button(
                                        modifier = Modifier
                                            .weight(1f)
                                            .selectable(
                                                selected = (option == selectedOption),
                                                onClick = { onOptionSelected(option) }
                                            )
                                            .background(color = backgroundColor),
                                        onClick = {
                                            onOptionSelected(option)
                                            tab = index
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                                        content = { Text(text = stringResource(option), color = textColor) }
                                    )
                                }
                            }
                        }
                        if(!lotsQuery.isNullOrEmpty()){
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                val rows = (lotsQuery as MutableList<Lot>).chunked(2)
                                rows.forEach { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        rowItems.forEach { lot ->
                                            LotCard(
                                                userId,
                                                lot,
                                                onLotClicked,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(8.dp)
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
    )
}

@Composable
fun SettingsTab(
    userId: String,
    lotsRowClicked: () -> Unit,
    onLogoutClicked: (ObjectId?) -> Unit,
    onTransactionRequest: (Payment) -> Unit
){
    val navController: NavHostController = rememberNavController()

    Scaffold(
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(null)
            val currentDestination = navBackStackEntry?.destination
            when (currentDestination?.route) {
                Routes.SETTINGS_CONTENT.name -> TopBar(R.string.tab_settings)
                Routes.PAYMENTS_CONTENT.name -> TopBarWithBack(
                    back = R.string.tab_settings,
                    title = R.string.payments,
                    onBackClicked = {
                        navController.popBackStack(
                            Routes.SETTINGS_CONTENT.name,
                            inclusive = false
                        )
                    }
                )
            }
        }
    ){
        NavHost(
            navController = navController,
            startDestination = Routes.SETTINGS_CONTENT.name,
            Modifier.padding(it),
        ){
            composable(route = Routes.SETTINGS_CONTENT.name){
                SettingsContent(
                    lotsRowClicked,
                    {
                        navController.navigate(Routes.PAYMENTS_CONTENT.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    {
                        navController.navigate(Routes.DETAILS_CONTENT.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogoutClicked = onLogoutClicked
                )
            }
            composable(route = Routes.PAYMENTS_CONTENT.name){
                PaymentsContent(
                    userId,
                    onTransactionRequest
                )
            }
            composable(route = Routes.DETAILS_CONTENT.name){

            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun PaymentsContent(
    userId: String,
    onTransactionRequest: (Payment) -> Unit
) {

    val payments = remember { mutableStateListOf<Payment>() }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }


    Datasource.clientSocket.on("paymentsRefresh"){
        coroutineScope.launch {
            val job = async {
                try{
                    val newPayments : List<Payment>  = KtorHttpClient.get("/api/payments/user/${userId}/").body()
                    payments.clear()
                    payments.addAll(newPayments)
                    Log.d("updatedPayments", payments.toString())
                }catch(e: Exception){
                    Log.d("retrievePayments", "Failed to retrieve payments from DB" + e.message)
                }
            }
            job.await()
        }
    }

    var user : User

    LaunchedEffect(Unit){
        try{
            val newPayments : List<Payment> = KtorHttpClient.get("/api/payments/user/${userId}/").body()
            payments.clear()
            payments.addAll(newPayments)
        }catch(e: Exception){
            Log.d("retrievePayments", "Failed to retrieve payments from DB" + e.message)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState){ data ->
                Snackbar(
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.secondary)
                        .padding(12.dp),
                    action = {
                        TextButton(
                            onClick = { data.dismiss() },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        ) { Text(data.visuals.actionLabel ?: "") }
                    }
                ) {
                    Text(data.visuals.message)
                }
            }
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.payment_order),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = stringResource(R.string.auction_number),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = stringResource(R.string.total) + " ( $ )",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = stringResource(R.string.option),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Outline()
                        Spacer(modifier = Modifier.height(2.dp))
                        if(!payments.isNullOrEmpty()){
                            payments?.forEach { payment ->
                                var auction: Auction? = auctions.firstOrNull { it.id == payment.auctionId }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = payment.number.toString(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (auction != null) {
                                        Text(
                                            text = auction.number.toString(),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Text(
                                        text = payment.grandTotal.toString(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (payment.paid) {
                                        OutlinedButton(
                                            onClick = {
                                                    coroutineScope.launch {
                                                        user = KtorHttpClient.get("/api/users/${userId}/").body()
                                                        FileGenerator.writePdf(
                                                            context,
                                                            payment,
                                                            user
                                                        )
                                                        snackbarHostState.showSnackbar(
                                                            message =  context.getString(R.string.pdf_ok),
                                                            actionLabel = context.getString(R.string.ok)
                                                        )
                                                    }
                                            },
                                            shape = MaterialTheme.shapes.extraSmall,
                                            modifier = Modifier
                                                .weight(1f)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Text(
                                                    stringResource(R.string.invoice),
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    val job1 = async {
                                                        onTransactionRequest(
                                                            payment
                                                        )
                                                    }
                                                    job1.await()
                                                }
                                            },
                                            shape = MaterialTheme.shapes.extraSmall,
                                            modifier = Modifier
                                                .weight(1f)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Text(
                                                    stringResource(R.string.pay),
                                                    style = MaterialTheme.typography.labelMedium,
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Outline()
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    FileGenerator.writeXlsx(
                                        context,
                                        payments
                                    )
                                    snackbarHostState.showSnackbar(
                                        message =  context.getString(R.string.excel_ok),
                                        actionLabel = context.getString(R.string.ok)
                                    )
                                } },
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    stringResource(R.string.save_payments),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                    }
                }
            }
        }
    )
}

@Composable
fun SettingRow(@DrawableRes icon: Int, @StringRes text: Int, onRowClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onRowClicked)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp)
        )
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
        )
    }
}


@Composable
fun SettingsContent(
    lotsRowClicked: () -> Unit,
    paymentsRowClicked: () -> Unit,
    detailsRowClicked: () -> Unit,
    onLogoutClicked: (ObjectId?) -> Unit
) {

    Scaffold(
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.welcome),
                                contentDescription = stringResource(R.string.welcome),
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                        SettingRow(icon = R.drawable.favorite, text = R.string.tab_favorite, lotsRowClicked)
                        SettingRow(icon = R.drawable.payments, text = R.string.payments, paymentsRowClicked)
                        SettingRow(icon = R.drawable.profile, text = R.string.details, detailsRowClicked)

                        OutlinedButton(
                            onClick = { onLogoutClicked(null) },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    stringResource(R.string.logout),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun AuctionCardPreview() {
}