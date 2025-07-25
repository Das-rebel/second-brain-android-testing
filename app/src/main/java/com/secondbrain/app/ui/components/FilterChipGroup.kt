package com.secondbrain.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * A horizontal group of filter chips where only one chip can be selected at a time.
 *
 * @param items The list of items to display as filter chips
 * @param selectedItem The currently selected item
 * @param onItemSelected Callback when an item is selected
 * @param modifier Modifier to be applied to the layout
 * @param itemContent The content to be displayed for each item
 */
@Composable
fun <T> FilterChipGroup(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    LazyRow(
        modifier = modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    ) {
        items(items) { item ->
            val selected = item == selectedItem
            FilterChip(
                selected = selected,
                onClick = { onItemSelected(item) },
                label = { itemContent(item) },
                modifier = Modifier.padding(horizontal = 2.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                    selected = selected
                ),
                role = Role.RadioButton
            )
        }
    }
}

/**
 * A horizontal group of filter chips where multiple chips can be selected.
 *
 * @param items The list of items to display as filter chips
 * @param selectedItems The set of currently selected items
 * @param onItemSelected Callback when an item is selected
 * @param onItemDeselected Callback when an item is deselected
 * @param modifier Modifier to be applied to the layout
 * @param itemContent The content to be displayed for each item
 */
@Composable
fun <T> MultiSelectFilterChipGroup(
    items: List<T>,
    selectedItems: Set<T>,
    onItemSelected: (T) -> Unit,
    onItemDeselected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    ) {
        items(items) { item ->
            val selected = item in selectedItems
            FilterChip(
                selected = selected,
                onClick = {
                    if (selected) {
                        onItemDeselected(item)
                    } else {
                        onItemSelected(item)
                    }
                },
                label = { itemContent(item) },
                modifier = Modifier.padding(horizontal = 2.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (selected) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                    selected = selected
                )
            )
        }
    }
}
