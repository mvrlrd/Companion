package ru.mvrlrd.home

import android.R
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import ru.mvrlrd.core_api.mediators.AppWithFacade


@Composable
fun HomeScreen() {
    val facade = (LocalContext.current.applicationContext as AppWithFacade).getFacade()
    val repo = remember {
        DaggerHomeComponent.builder().providersFacade(facade).build().getRepo()
    }

    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.createHomeViewModelFactory(repo))

    val userInput = remember { mutableStateOf(TextFieldValue()) }
    var response by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    response = viewModel.responseAnswer.observeAsState("").value
    val isLoading by viewModel.isLoading.observeAsState(false) // наблюдаем за состоянием загрузки

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),

        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            TypingAnimation(text = response, isLoading = isLoading)
        }
        CustomTextField(
            textState = userInput,
            modifier = Modifier
                .align(Alignment.End) // Выравнивание CustomTextField внизу экрана
        ) {
            viewModel.sendRequest(userInput.value.text)
        }
    }
}

@Composable
fun TypingAnimation(text: String, isLoading: Boolean) {
    var displayedText by remember { mutableStateOf("") }
    var visibleTextLength by remember { mutableStateOf(0) }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            visibleTextLength =  0
            CircularProgressIndicator() // Круговой прогресс-бар
        } else {
            LaunchedEffect(text) {
                for (i in text.indices) {
                    delay(50)
                    visibleTextLength = i + 1
                }
            }
            displayedText = text.take(visibleTextLength)
            BasicText(
                text = displayedText,
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize * 1.2f, // Увеличиваем размер шрифта на 20%
                    color = MaterialTheme.colorScheme.primary // Цвет текста противоположен основному цвету темы
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearEasing
                        )
                    )
            )
        }
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    textState: MutableState<TextFieldValue>,
    onSend: () -> Unit
) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Gray.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    BasicTextField(
                        value = textState.value,
                        onValueChange = { textState.value = it },
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.primary, // Цвет текста противоположен основному цвету темы
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    )
                    IconButton(
                        onClick = onSend,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_send), // Use your own icon here
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

//////
//
//
//    @Preview(showBackground = true)
//    @Composable
//    fun DefaultPreview() {
//        MyApp()
//    }
//
//    @Composable
//    @Preview(showBackground = true)
//    fun CustomTextFieldPreview() {
//        val textState = remember { mutableStateOf(TextFieldValue()) }
//        MaterialTheme {
//            Surface(modifier = Modifier.fillMaxSize()) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),
//                    verticalArrangement = Arrangement.Top,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    CustomTextField(
//                        textState = textState,
//                        onSend = {
//                            // Handle send action
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    )
//                }
//            }
//        }
//    }
//}
//////////////

//@Composable
//fun HomeScreen(
//    items: List<String>,
//    onItemClick: (String) -> Unit
//    ) {
//        LazyColumn {
//            items(items.size) { index ->
//                var expanded by remember { mutableStateOf(false) }
//                val item = items[index]
//
//                // Обработчик клика по элементу списка
//                val onItemClickInternal: () -> Unit = {
//                    expanded = !expanded
//                    onItemClick(item)
//                }
//
//                // Вид списка
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable(onClick = onItemClickInternal)
//                ) {
//                    Text(text = item, modifier = Modifier.padding(16.dp))
//
//                    // Дополнительный контент, который будет показан при раскрытии элемента
//                    if (expanded) {
//                        Text(
//                            text = "Additional content for $item",
//                            modifier = Modifier.padding(start = 16.dp)
//                        )
//                    }
//                }
//            }
//        }
//    }




//    ExpandableRecyclerView(
//        items = listOf("Item 1", "Item 2", "Item 3"),
//        onItemClick = { selectedItem ->
//            // Обработка выбора элемента
//            println("Selected item: $selectedItem")
//        }
//    )