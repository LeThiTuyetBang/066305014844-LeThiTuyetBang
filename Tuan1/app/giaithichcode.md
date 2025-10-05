# Bài Tập 1 - Lập trình Android (XML Layout)

##  Giới thiệu
Bài tập này xây dựng một **màn hình hồ sơ cá nhân (Profile Screen)** với:
- Avatar hình tròn
- Tên người dùng
- Địa chỉ
- Nút **Back** và **Edit** bo góc vuông tròn nhẹ như trong thiết kế.

Ứng dụng viết bằng **Kotlin + XML (View-based UI)**, chưa dùng Jetpack Compose.

##  Cấu trúc file chính

## 1. `activity_main.xml`
- Đây là file layout chính của ứng dụng.
- Thành phần trong layout:
    - `ImageButton btnBack`: nút quay lại (icon mũi tên).
    - `ImageButton btnEdit`: nút chỉnh sửa (icon cây bút).
    - `ShapeableImageView avatar`: hiển thị ảnh đại diện, bo tròn bằng Material ShapeAppearanceOverlay.
    - `TextView tvName`: tên người dùng (in đậm).
    - `TextView tvLocation`: địa chỉ (màu xám).

## 2. `bg_button_square.xml` (drawable)
- File XML để custom nền cho nút Back và Edit.
- Đặc điểm:
    - Nền trắng
    - Viền xám nhạt
    - Bo góc 8dp (bo vuông nhẹ)
- Giúp các nút có giao diện vuông vức, bo tròn mềm mại.

## 3. `ic_arrow_back.xml` (drawable - vector)
- Icon vector cho nút Back.
- Là mũi tên quay về bên trái.

## 4. `edit.xml` (drawable - vector)
- Icon vector cho nút Edit.
- Là hình cây bút chì màu xanh lá/teal.

## 5. `styles.xml`
- Chứa định nghĩa style cho ứng dụng.
- Có style `ShapeAppearanceOverlay.App.CircleImage` để avatar bo tròn 100%.

## 6. `colors.xml`
- Chứa các màu mặc định (đen, trắng, tím, teal...).
- Được dùng trong layout và styles.

## 7. `strings.xml`
- File quản lý chuỗi (text).
- Chứa các text hiển thị như `"Back"`, `"Edit"`, `"Lê Thị Tuyết Băng"`, `"Đắk Lắk, Việt Nam"`.

## 8. `MainActivity.kt`
- Activity chính để load giao diện `activity_main.xml`.
- Hiện chưa có nhiều xử lý code Kotlin, chủ yếu để setContentView.