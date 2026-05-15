package com.example.jatre_namma_pride.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.stringResource
import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.data.model.ItemType
import com.example.jatre_namma_pride.data.model.LostFoundItem
import com.example.jatre_namma_pride.data.repository.LostFoundRepository
import com.example.jatre_namma_pride.ui.components.FilterChipRow
import com.example.jatre_namma_pride.ui.components.LostFoundItemCard
import com.example.jatre_namma_pride.ui.components.StatCircle
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreDeepRed
import com.example.jatre_namma_pride.ui.theme.JatreDivider
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLiveGreen
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.ui.theme.JatreSubtext
import com.example.jatre_namma_pride.ui.theme.JatreSurface

/**
 * Lost & Found screen with single-column layout, stat circles,
 * filter chips, and a working "Report Item" dialog with validation.
 */
@Composable
fun LostFoundScreen() {
    val items by LostFoundRepository.getAllItems().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val allLabel = stringResource(R.string.all_items)
    val lostLabel = stringResource(R.string.status_lost)
    val foundLabel = stringResource(R.string.status_found)
    val resolvedLabel = stringResource(R.string.resolved)

    var selectedFilter by remember { mutableStateOf(allLabel) }
    var showPostDialog by remember { mutableStateOf(false) }
    val filterChips = listOf(allLabel, lostLabel, foundLabel, resolvedLabel)

    val filteredItems = when (selectedFilter) {
        lostLabel -> items.filter { it.type == ItemType.LOST && !it.isResolved }
        foundLabel -> items.filter { it.type == ItemType.FOUND && !it.isResolved }
        resolvedLabel -> items.filter { it.isResolved }
        else -> items
    }

    Scaffold(
        containerColor = JatreSurface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPostDialog = true },
                containerColor = JatreSaffron,
                contentColor = JatreDarkBrown
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Report Item")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(JatreSurface)
        ) {
            // ── Header ───────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.lost_found),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = JatreCream,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = stringResource(R.string.lost_found_subtitle),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = JatreGold.copy(alpha = 0.7f)
                    )
                )
            }

            // ── Stat Circles ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCircle(
                    count = LostFoundRepository.getActiveCount(items),
                    label = stringResource(R.string.stat_label_active),
                    color = JatreLiveGreen
                )
                StatCircle(
                    count = LostFoundRepository.getResolvedCount(items),
                    label = stringResource(R.string.resolved),
                    color = JatreLiveGreen
                )
                StatCircle(
                    count = LostFoundRepository.getLostCount(items),
                    label = stringResource(R.string.status_lost),
                    color = JatreSaffron
                )
                StatCircle(
                    count = LostFoundRepository.getFoundCount(items),
                    label = stringResource(R.string.status_found),
                    color = JatreLiveGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Filter Chips ─────────────────────────────────────────────────
            FilterChipRow(
                chips = filterChips,
                selectedChip = selectedFilter,
                onChipSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Single-Column List ───────────────────────────────────────────
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredItems) { item ->
                    LostFoundItemCard(
                        item = item,
                        onResolve = { id ->
                            val itemToResolve = items.find { it.id == id }
                            if (itemToResolve != null) {
                                coroutineScope.launch {
                                    LostFoundRepository.updateItem(itemToResolve.copy(isResolved = true))
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // ── Post New Item Dialog ─────────────────────────────────────────────────
    if (showPostDialog) {
        PostItemDialog(
            onDismiss = { showPostDialog = false },
            onSubmit = { newItem ->
                coroutineScope.launch {
                    LostFoundRepository.insertItem(newItem)
                    showPostDialog = false
                }
            },
            nextId = (items.maxOfOrNull { it.id } ?: 0) + 1
        )
    }
}

// ── Report-Item Dialog with Photo Picker & Validation ────────────────────────
@Composable
private fun PostItemDialog(
    onDismiss: () -> Unit,
    onSubmit: (LostFoundItem) -> Unit,
    nextId: Int
) {
    val context = LocalContext.current

    // Form field states
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var reportedBy by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<ItemType?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Validation error states
    var titleError by remember { mutableStateOf<String?>(null) }
    var descError by remember { mutableStateOf<String?>(null) }
    var catError by remember { mutableStateOf<String?>(null) }
    var locError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var contactError by remember { mutableStateOf<String?>(null) }
    var typeError by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        stringResource(R.string.category_person), 
        stringResource(R.string.category_jewellery), 
        stringResource(R.string.category_bag), 
        stringResource(R.string.category_other)
    )

    // Photo picker launcher
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                // Ignore
            }
        }
        selectedImageUri = uri
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = JatreSaffron,
        unfocusedBorderColor = JatreDivider,
        cursorColor = JatreSaffron,
        focusedLabelColor = JatreSaffron,
        unfocusedLabelColor = JatreSubtext,
        focusedTextColor = JatreCream,
        unfocusedTextColor = JatreCream,
        errorBorderColor = JatreDeepRed,
        errorLabelColor = JatreDeepRed,
        errorCursorColor = JatreDeepRed
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = JatreCardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Dialog header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.report_item),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = JatreCream,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = JatreCream.copy(alpha = 0.6f)
                        )
                    }
                }

                HorizontalDivider(color = JatreDivider.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 12.dp))

                // ── SECTION 1: Item Details ──────────────────────────────────
                DialogSectionHeader(stringResource(R.string.dialog_section_item_details))

                // Type Selection (Lost / Found)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        SelectableChip(
                            text = stringResource(R.string.dialog_lost_it),
                            isSelected = selectedType == ItemType.LOST,
                            selectedColor = JatreSaffron,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { selectedType = ItemType.LOST; typeError = null }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SelectableChip(
                            text = stringResource(R.string.dialog_found_it),
                            isSelected = selectedType == ItemType.FOUND,
                            selectedColor = JatreLiveGreen,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { selectedType = ItemType.FOUND; typeError = null }
                        )
                    }
                }
                if (typeError != null) {
                    Text(
                        text = typeError!!,
                        style = MaterialTheme.typography.labelSmall.copy(color = JatreDeepRed),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Photo Picker
                if (selectedImageUri != null) {
                    val bitmap = remember(selectedImageUri) {
                        try {
                            context.contentResolver
                                .openInputStream(selectedImageUri!!)
                                ?.use { BitmapFactory.decodeStream(it) }
                        } catch (_: Exception) { null }
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Selected photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                        IconButton(
                            onClick = { photoLauncher.launch("image/*") },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove photo",
                                tint = JatreCream,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(JatreDarkBrown.copy(alpha = 0.7f))
                                    .padding(4.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, JatreDivider.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .background(JatreDivider.copy(alpha = 0.1f))
                            .clickable { photoLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.AddPhotoAlternate,
                                contentDescription = "Add Photo",
                                tint = JatreSaffron.copy(alpha = 0.8f),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.dialog_add_photo),
                                style = MaterialTheme.typography.labelLarge.copy(color = JatreSubtext.copy(alpha = 0.8f))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selection
                Text(
                    text = stringResource(R.string.dialog_category_label),
                    style = MaterialTheme.typography.labelMedium.copy(color = JatreCream.copy(alpha = 0.8f))
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        SelectableChip(
                            text = cat,
                            isSelected = selectedCategory == cat,
                            selectedColor = JatreGold,
                            onClick = { selectedCategory = cat; catError = null }
                        )
                    }
                }
                if (catError != null) {
                    Text(
                        text = catError!!,
                        style = MaterialTheme.typography.labelSmall.copy(color = JatreDeepRed),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = null },
                    label = { Text(stringResource(R.string.name_title)) },
                    placeholder = { Text(stringResource(R.string.name_placeholder), color = JatreSubtext.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Outlined.Label, contentDescription = null, tint = JatreSaffron.copy(alpha = 0.7f)) },
                    isError = titleError != null,
                    supportingText = titleError?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; descError = null },
                    label = { Text(stringResource(R.string.description_spec)) },
                    placeholder = { Text(stringResource(R.string.desc_placeholder), color = JatreSubtext.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Outlined.Notes, contentDescription = null, tint = JatreSaffron.copy(alpha = 0.7f)) },
                    isError = descError != null,
                    supportingText = descError?.let { { Text(it) } },
                    minLines = 2,
                    maxLines = 4,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = JatreDivider.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 16.dp))

                // ── SECTION 2: Where was it? ─────────────────────────────────
                DialogSectionHeader(stringResource(R.string.dialog_section_location))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it; locError = null },
                    label = { Text(stringResource(R.string.location)) },
                    placeholder = { Text(stringResource(R.string.loc_placeholder), color = JatreSubtext.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = JatreSaffron.copy(alpha = 0.7f)) },
                    isError = locError != null,
                    supportingText = locError?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = JatreDivider.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 16.dp))

                // ── SECTION 3: Your Info ─────────────────────────────────────
                DialogSectionHeader(stringResource(R.string.dialog_section_contact))

                OutlinedTextField(
                    value = reportedBy,
                    onValueChange = { reportedBy = it; nameError = null },
                    label = { Text(stringResource(R.string.your_name)) },
                    placeholder = { Text(stringResource(R.string.your_name_placeholder), color = JatreSubtext.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = JatreSaffron.copy(alpha = 0.7f)) },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = contactNumber,
                    onValueChange = {
                        val filtered = it.filter { c -> c.isDigit() }.take(10)
                        contactNumber = filtered
                        contactError = null
                    },
                    label = { Text(stringResource(R.string.contact_number)) },
                    placeholder = { Text(stringResource(R.string.contact_placeholder), color = JatreSubtext.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Outlined.Call, contentDescription = null, tint = JatreSaffron.copy(alpha = 0.7f)) },
                    isError = contactError != null,
                    supportingText = contactError?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Submit Button ────────────────────────────────────────────
                Button(
                    onClick = {
                        var valid = true

                        if (selectedType == null) {
                            typeError = context.getString(R.string.err_select_type); valid = false
                        }
                        if (title.isBlank()) {
                            titleError = context.getString(R.string.err_title_required); valid = false
                        } else if (title.length < 3) {
                            titleError = context.getString(R.string.err_title_min); valid = false
                        }
                        if (description.isBlank()) {
                            descError = context.getString(R.string.err_desc_required); valid = false
                        } else if (description.length < 10) {
                            descError = context.getString(R.string.err_desc_min); valid = false
                        }
                        if (selectedCategory.isBlank()) {
                            catError = context.getString(R.string.err_category_required); valid = false
                        }
                        if (location.isBlank()) {
                            locError = context.getString(R.string.err_location_required); valid = false
                        } else if (location.length < 3) {
                            locError = context.getString(R.string.err_location_min); valid = false
                        }
                        if (contactNumber.isBlank()) {
                            contactError = context.getString(R.string.err_contact_required); valid = false
                        } else if (contactNumber.length != 10) {
                            contactError = context.getString(R.string.err_contact_length); valid = false
                        }
                        if (reportedBy.isBlank()) {
                            nameError = context.getString(R.string.err_name_required); valid = false
                        } else if (reportedBy.length < 2) {
                            nameError = context.getString(R.string.err_name_min); valid = false
                        }

                        if (valid) {
                            onSubmit(
                                LostFoundItem(
                                    id = 0,
                                    titleEn = title.trim(),
                                    titleKn = "",
                                    descriptionEn = description.trim(),
                                    descriptionKn = "",
                                    category = selectedCategory,
                                    locationEn = location.trim(),
                                    locationKn = "",
                                    reportedBy = reportedBy.trim(),
                                    contactNumber = contactNumber.trim(),
                                    time = "Just now",
                                    type = selectedType!!,
                                    imageUri = selectedImageUri?.toString()
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JatreSaffron,
                        contentColor = JatreDarkBrown
                    )
                ) {
                    Text(
                        text = stringResource(R.string.submit),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            color = JatreSaffron,
            fontWeight = FontWeight.ExtraBold
        ),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

// ── Reusable selectable chip for the form ────────────────────────────────────
@Composable
private fun SelectableChip(
    text: String,
    isSelected: Boolean,
    selectedColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (isSelected) Modifier.background(selectedColor)
                else Modifier
                    .background(JatreDivider.copy(alpha = 0.2f))
                    .border(1.dp, JatreDivider.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (isSelected) JatreDarkBrown else JatreCream.copy(alpha = 0.7f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}
