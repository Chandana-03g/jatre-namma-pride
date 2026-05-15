package com.example.jatre_namma_pride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.data.model.AppDatabase
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.util.EmojiConstants

/**
 * Custom top bar matching the Jatre festive theme.
 * Shows app branding, notification bell, and settings gear.
 */
@Composable
fun JatreTopBar(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).notificationDao() }
    val unreadCount by dao.getUnreadCount().collectAsState(initial = 0)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(JatreDarkBrown)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App icon emoji
        Text(text = EmojiConstants.TEMPLE, fontSize = 28.sp)

        Spacer(modifier = Modifier.width(10.dp))

        // App title
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.app_title_mixed),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = JatreGold,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = stringResource(R.string.app_subtitle),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JatreLightGold.copy(alpha = 0.7f)
                )
            )
        }

        // Action icons
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = JatreSaffron,
                        modifier = Modifier.size(24.dp)
                    )
                }
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 10.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = JatreLightGold.copy(alpha = 0.6f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
