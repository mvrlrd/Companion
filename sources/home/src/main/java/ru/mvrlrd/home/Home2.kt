package ru.mvrlrd.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.receiveAsFlow
import ru.mvrlrd.core_api.database.chat.entity.Message
import ru.mvrlrd.core_api.mediators.AppWithFacade
import ru.mvrlrd.home.di.DaggerHomeComponent

@Composable
fun HomeScreen2(navController: NavController, chatId: Long) {
    val facade = (LocalContext.current.applicationContext as AppWithFacade).getFacade()

    val homeComponent = remember {
//        throw RuntimeException()
        DaggerHomeComponent.builder().providersFacade(facade).build()
    }
    val repo = remember {
//        throw RuntimeException()
        homeComponent.getRepo()
    }

    val saveMessageToChatUseCase = remember {
        homeComponent.provideSaveMessageToChatUseCase()
    }
    val getAllMessagesForChatUseCase = remember {
        homeComponent.provideGetAllMessagesForChatUseCase()
    }
    val getAnswerUseCase = remember {
        homeComponent.provideGetAnswerUseCase()
    }

    val deleteMessageUseCase = remember {
        homeComponent.provideDeleteMessageUseCase()
    }
    val clearMessagesUseCase = remember {
        homeComponent.provideClearMessagesUseCase()
    }

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.createHomeViewModelFactory(
            getAnswerUseCase,
            saveMessageToChatUseCase,
            getAllMessagesForChatUseCase,
            deleteMessageUseCase,
            clearMessagesUseCase,
            chatId
        )
    )

    val messages = viewModel.messages


    val userInput = remember { mutableStateOf(TextFieldValue()) }
    var response by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    response = viewModel.responseAnswer.observeAsState("").value
    val isLoading by viewModel.isLoading.observeAsState(false) // наблюдаем за состоянием загрузки

    val flow = viewModel.oneShotEventChannel.receiveAsFlow()



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
    ) {
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
//                    .verticalScroll(scrollState)
            ) {
//                MessageBubble(message = Message(0,0,messages.value.toString(),1L,true))

              MessageList(messages = messages){
                  viewModel.deleteMessage(it)
              }


            }
            ShowToast(flow = flow)
            CustomTextField(
                textState = userInput,
                modifier = Modifier
                    .align(Alignment.End) // Выравнивание CustomTextField внизу экрана
            ) {
//                viewModel.saveMessageToChat(Message(holderChatId = chatId, text = userInput.value.text, date = 1L, isReceived = false ))
                viewModel.sendRequest(userInput.value.text)
            }
        }
    }


}

@Composable
fun MessageList(messages: SnapshotStateList<Message>, onDismiss: (Long) -> Unit){
    val listState = rememberLazyListState()
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }



    LazyColumn(
        state=listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)){
        itemsIndexed(
            items = messages,
            key = {_,item->
                item}
        ){_, item->
            SwipeToDismissMessage(item = item){
                onDismiss(it)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val bubbleColor = if (message.isReceived) Color(0xFFE0E0E0) else Color(0xFFDCF8C6)
    val alignment = if (message.isReceived) Alignment.CenterStart else Alignment.CenterEnd




    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
            if (message.isReceived){
                Arrow(isReceived = message.isReceived,
                    color = bubbleColor)
                BubbleNew(bubbleColor = bubbleColor, text = message.text, isReceived = message.isReceived)
            }else{
                BubbleNew(bubbleColor = bubbleColor, text = message.text, isReceived = message.isReceived)
                Arrow(isReceived = message.isReceived,
                    color = bubbleColor)
            }
        }
    }
}

@Composable
fun Arrow(isReceived: Boolean, color: Color){
    Box(
        modifier = Modifier
            .background(
                color = color,
                shape = TriangleEdgeShape(20, isReceived)
            )
            .width(8.dp)
            .fillMaxHeight()
    )
}

@Composable
fun BubbleNew(bubbleColor: Color, text: String, isReceived: Boolean){
    Box(
            modifier = Modifier
                .background(bubbleColor, shape = RoundedCornerShape(
                    topStart = if (isReceived) 0.dp else 16.dp,
                    topEnd = 16.dp,
                    bottomEnd = if (isReceived) 16.dp else 0.dp,
                    bottomStart = 16.dp
                ))
                .padding(12.dp)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }
}

class TriangleEdgeShape(private val offset: Int, private val isReceived: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val trianglePath = Path().apply {
            if (isReceived) {
                moveTo(x = size.width, y = 0f)
                lineTo(x = size.width, y = offset.toFloat())
                lineTo(x = size.width - offset, y = 0f)
            } else {
                moveTo(x = 0f, y = size.height - offset)
                lineTo(x = 0f, y = size.height)
                lineTo(x = 0f + offset, y = size.height)
            }
            close()
        }
        return Outline.Generic(path = trianglePath)
    }
}




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissMessage(item: Message, onDismiss: (Long)->Unit) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                onDismiss(item.id)
            }
            true
        }
    )
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(
            DismissDirection.EndToStart
        ),
        dismissThresholds = { direction ->
            FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.1f else 0.05f)
        },
        background = {
            val color = when (dismissState.dismissDirection) {
                DismissDirection.StartToEnd -> Color.Blue
                DismissDirection.EndToStart -> Color.Red
                null -> Color.Transparent
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                androidx.compose.material.Text("Delete", color = Color.Black)
            }
        },
        dismissContent = {
            MessageBubble(message = item)
        }
    )
}

//почему то не отображаются с bubble shape

//fun BubbleShape(
//    cornerRadius: Dp = 16.dp,
//    isReceived: Boolean
//) = GenericShape { size, _ ->
//    val cornerSizePx = with(LocalDensity.current) { cornerRadius.toPx() }
//
//    // Ensure the path starts from the correct point
//    if (isReceived) {
//        // For received messages (left side)
//        moveTo(0f, cornerSizePx)
//        arcTo(
//            rect = Rect(0f, 0f, cornerSizePx, cornerSizePx),
//            startAngleDegrees = 180f,
//            sweepAngleDegrees = 90f,
//            forceMoveTo = false
//        )
//        lineTo(size.width - cornerSizePx, 0f)
//        arcTo(
//            rect = Rect(size.width - cornerSizePx, 0f, size.width, cornerSizePx),
//            startAngleDegrees = 270f,
//            sweepAngleDegrees = 90f,
//            forceMoveTo = false
//        )
//        lineTo(size.width, size.height - cornerSizePx)
//        arcTo(
//            rect = Rect(size.width - cornerSizePx, size.height - cornerSizePx, size.width, size.height),
//            startAngleDegrees = 0f,
//            sweepAngleDegrees = 90f,
//            forceMoveTo = false
//        )
//        lineTo(cornerSizePx, size.height)
//        arcTo(
//            rect = Rect(0f, size.height - cornerSizePx, cornerSizePx, size.height),
//            startAngleDegrees = 90f,
//            sweepAngleDegrees = 45f,
//            forceMoveTo = false
//        )
//        lineTo(0f, size.height)
//        lineTo(0f, cornerSizePx)
//    } else {
//        // For sent messages (right side)
//        moveTo(0f, cornerSizePx)
//        arcTo(
//            rect = Rect(0f, 0f, cornerSizePx, cornerSizePx),
//            startAngleDegrees = 180f,
//            sweepAngleDegrees = 90f,
//            forceMoveTo = false
//        )
//        lineTo(size.width - cornerSizePx, 0f)
//        arcTo(
//            rect = Rect(size.width - cornerSizePx, 0f, size.width, cornerSizePx),
//            startAngleDegrees = 270f,
//            sweepAngleDegrees = 90f,
//            forceMoveTo = false
//        )
//        lineTo(size.width, size.height)
//        lineTo(size.width - cornerSizePx, size.height)
//        arcTo(
//            rect = Rect(size.width - cornerSizePx, size.height - cornerSizePx, size.width, size.height),
//            startAngleDegrees = 90f,
//            sweepAngleDegrees = 45f,
//            forceMoveTo = false
//        )
//        lineTo(0f, size.height)
//        lineTo(0f, cornerSizePx)
//    }
//}