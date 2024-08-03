package ru.mvrlrd.home

import android.R
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    textState: MutableState<TextFieldValue>,
    onSend: () -> Unit
) {
    val debounceDuration: Long = 3000
    var isClickable by remember { mutableStateOf(true) }
    LaunchedEffect(isClickable) {
        while (!isClickable) {
            delay(debounceDuration)
            isClickable = true
        }
    }
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
                        onClick = {
                            if (isClickable) {
                                Log.d("TAG","clicked")
                                isClickable = false
                                onSend()
                            }
                        },
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