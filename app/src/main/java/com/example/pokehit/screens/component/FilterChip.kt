package com.example.pokehit.screens.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TypeFilterChip(
    type: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onToggle,
        label = {
            Text(
                text = type.replaceFirstChar { it.uppercase() },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        },
        selected = isSelected,
        modifier = modifier
    )
}