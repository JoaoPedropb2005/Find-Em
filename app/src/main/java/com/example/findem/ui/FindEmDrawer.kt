package com.example.findem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findem.model.Pet

@Composable
fun FindEmDrawerContent(
    pets: List<Pet>,
    userName: String?,
    isUserLoggedIn: Boolean,
    onCloseDrawer: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onMyPostsClick: () -> Unit // NOVO: Callback para navegação
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = Color(0xFF4CAF50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (isUserLoggedIn) {
                        Text(
                            text = "Olá, $userName",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sair (Logout)",
                            color = Color(0xFFBBDEFB),
                            fontSize = 13.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onLogoutClick() }
                        )
                    } else {
                        Text(
                            text = "Usuário Não Logado",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Logar / Criar conta",
                            color = Color(0xFFBBDEFB),
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onLoginClick() }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ITENS DO MENU ---
            DrawerMenuItem(Icons.Default.Star, "Favoritados") { onCloseDrawer() }
            Spacer(modifier = Modifier.height(16.dp))

            // CORREÇÃO: Chama os dois métodos - fecha o drawer e executa a ação de navegar
            DrawerMenuItem(Icons.Default.List, "Minhas postagens") {
                onCloseDrawer()
                onMyPostsClick()
            }

            Spacer(modifier = Modifier.height(16.dp))
            DrawerMenuItem(Icons.Default.Notifications, "Notificações") { onCloseDrawer() }

            Spacer(modifier = Modifier.weight(1f)) // Usa weight para empurrar o rodapé para baixo

            Text(
                "Created by Julia Soares And João Pedro\n2025",
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.clickable { onCloseDrawer() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Settings, null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Configurações", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DrawerMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, title, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}