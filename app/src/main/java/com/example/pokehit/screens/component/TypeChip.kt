package com.example.pokehit.screens.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TypeChip(
    type: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = type,
        fontSize = 12.sp,
        color = Color.White,
        modifier = modifier
            .background(
                color = Color.Gray,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}