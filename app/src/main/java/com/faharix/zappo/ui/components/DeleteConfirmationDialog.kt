package com.faharix.zappo.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.faharix.zappo.data.Note

@Composable
fun DeleteConfirmationDialog(
    note: Note,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    // Animation pour l'apparition du dialogue
    LaunchedEffect(key1 = true) {
        isVisible = true
    }

    Dialog(
        onDismissRequest = {
            isVisible = false
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(200, easing = EaseOutQuint)
            ) + fadeIn(animationSpec = tween(200)),
            exit = scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(200, easing = EaseInQuint)
            ) + fadeOut(animationSpec = tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Icône animée
                    DeleteIconAnimation(isTask = note.isTask)

                    // Titre
                    Text(
                        text = "Move ${if (note.isTask) "this task" else "this note"} to Trash?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Message
                    Text(
                        text = "Move ${if (note.isTask) "task" else "note"} \"${note.title}\" to trash? You can restore it later from the trash screen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Boutons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Bouton Annuler
                        OutlinedButton(
                            onClick = {
                                isVisible = false
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("Annuler")
                        }

                        // Bouton Supprimer
                        Button(
                            onClick = {
                                isVisible = false
                                onConfirm()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Move to Trash")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteIconAnimation(isTask: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "deleteIconAnimation")

    val iconSize by infiniteTransition.animateFloat(
        initialValue = 48f,
        targetValue = 56f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconSize"
    )

    val iconColor by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.error,
        targetValue = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconColor"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        if (isTask) {
            // Animation spécifique pour les tâches
            Row {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(iconSize.dp)
                )
            }
        } else {
            // Animation pour les notes
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(iconSize.dp)
            )
        }
    }
}