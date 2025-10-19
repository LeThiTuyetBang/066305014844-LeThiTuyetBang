package com.example.btvn1.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.btvn1.model.LibraryManager
import com.example.btvn1.model.Book

@Composable
fun ManageScreen() {
    val student = LibraryManager.currentStudent
    val selectedIds = remember { mutableStateListOf<Int>() }
    var name by remember { mutableStateOf(student.name) }
    LaunchedEffect(LibraryManager.currentStudent) {
        name = LibraryManager.currentStudent.name
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hệ thống\nQuản lý Thư viện",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Sinh viên") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val found = LibraryManager.students.find { it.name == name }
                    if (found != null) {
                        LibraryManager.currentStudent = found  // ⚡ đổi student
                    }
                }
            ) {
                Text("Thay đổi")
            }

        }

        Spacer(Modifier.height(20.dp))
        Text("Danh sách sách", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            if (student.borrowedIds.isEmpty()) {
                Text(
                    "Bạn chưa mượn quyền sách nào\nNhấn 'Thêm' để bắt đầu hành trình đọc sách!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    items(LibraryManager.books) { book ->
                        if (student.borrowedIds.contains(book.id)) {
                            BorrowedItem(book)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, false)
        ) {
            items(LibraryManager.books, key = { it.id }) { book ->
                // Kiểm tra tick tạm thời
                val isSelected = selectedIds.contains(book.id)
                val isBorrowed = student.borrowedIds.contains(book.id)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            if (checked) selectedIds.add(book.id)
                            else selectedIds.remove(book.id)
                        },
                        enabled = !isBorrowed // ⚡ nếu đã mượn rồi thì không tick nữa
                    )
                    Text(
                        book.title,
                        fontSize = 16.sp,
                        color = if (isBorrowed) Color.Gray else Color.Unspecified
                    )
                }
            }
        }


        // ⚡ Bắt đầu thêm dialog chọn sách
        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Chọn sách để thêm") },
                text = {
                    Column {
                        LibraryManager.books.forEach { book ->
                            if (book.id !in student.borrowedIds) {
                                TextButton(onClick = {
                                    student.borrowedIds.add(book.id)
                                    showDialog = false
                                }) {
                                    Text(book.title)
                                }
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                //  Khi nhấn "Thêm": thêm toàn bộ sách đang tick vào danh sách mượn
                selectedIds.forEach { id ->
                    if (id !in student.borrowedIds) {
                        student.borrowedIds.add(id)
                    }
                }
                //  Sau khi thêm xong thì xoá tick tạm
                selectedIds.clear()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Thêm")
        }


    }
}

@Composable
private fun BorrowedItem(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = true, onCheckedChange = null)
            Spacer(Modifier.width(8.dp))
            Text(book.title, fontSize = 16.sp)
        }
    }
}
