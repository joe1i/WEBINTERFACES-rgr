package com.example.announcementchannel.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.announcementchannel.viewmodel.AuthState
import com.example.announcementchannel.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Пам'ять полів
    var username by rememberSaveable { mutableStateOf("") }
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    // Змінні для Статі (Випадаючий список)
    var gender by rememberSaveable { mutableStateOf("") }
    var expandedGender by rememberSaveable { mutableStateOf(false) }
    val genderMap = mapOf(
        "Чоловіча" to "M",
        "Жіноча" to "F",
        "Інша" to "O"
    )
    val genderOptions = genderMap.keys.toList()

    // Змінні для Дати народження (Календар)
    var birthDate by rememberSaveable { mutableStateOf("") }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val isFormValid = username.isNotBlank() && firstName.isNotBlank() &&
            lastName.isNotBlank() && email.isNotBlank() &&
            password.isNotBlank() && password == confirmPassword

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Реєстрація успішна!", Toast.LENGTH_SHORT).show()
                onRegisterSuccess()
                viewModel.resetState()
            }
            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Діалог вибору дати (З'являється поверх екрана, коли showDatePicker = true)
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    // Конвертуємо мілісекунди з календаря у формат "YYYY-MM-DD" для сервера
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        birthDate = formatter.format(Date(millis))
                    }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Скасувати")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Реєстрація", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = "Створіть новий акаунт", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Нікнейм (Username) *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Ім'я *") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Прізвище *") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // РЯДОК ЗІ СТАТТЮ ТА ДАТОЮ
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            // 1. Випадаючий список для статі
            ExposedDropdownMenuBox(
                expanded = expandedGender,
                onExpandedChange = { expandedGender = !expandedGender },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true, // Забороняємо ручний ввід
                    label = { Text("Стать") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedGender,
                    onDismissRequest = { expandedGender = false }
                ) {
                    genderOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                gender = selectionOption
                                expandedGender = false
                            }
                        )
                    }
                }
            }

            // 2. Поле з календарем для дати
            OutlinedTextField(
                value = birthDate,
                onValueChange = {},
                readOnly = true, // Забороняємо ручний ввід, тільки через календар
                label = { Text("Дата нар.") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Вибрати дату")
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль *") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Підтвердіть пароль *") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword
        )

        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            Text(
                text = "Паролі не збігаються!",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (authState is AuthState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    // Відправляємо дані у ViewModel
                    val serverGender = genderMap[gender] ?: ""
                    viewModel.register(
                        username, email, firstName, lastName,
                        serverGender, birthDate, password, confirmPassword
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = isFormValid
            ) {
                Text("Зареєструватися", fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Вже є акаунт? ")
            Text("Увійти", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}