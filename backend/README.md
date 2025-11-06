# 度假村綜合系統 - 後端 API

## 專案概述

基於 Spring Boot 3.5.6 開發的度假村綜合管理系統後端服務，採用標準 MVC 架構設計，提供完整的 RESTful API。系統涵蓋會員管理、房型預訂、電商購物車、評論系統等核心業務功能，適用於旅館、民宿、度假村等住宿業線上預訂場景。

### 技術特色
- **標準 MVC 三層架構**：Controller-Service-Repository 職責分離
- **Session-based 認證**：Spring Security 實作使用者身份驗證與授權
- **統一回應封裝**：BaseResponse<T> 統一 API 回應格式
- **DTO 模式**：使用 MapStruct 進行 Entity 與 DTO 轉換
- **事務管理**：@Transactional 確保資料一致性
- **分頁查詢**：支援大數據量的分頁與關鍵字搜尋

---

## 核心功能模組

### 會員系統
- 使用者註冊、登入、登出（Session-based Authentication）
- 個人資料管理（CRUD）
- 密碼加密儲存（BCrypt）
- 角色權限控管（ADMIN / USER）

### 訂房系統
- 房型瀏覽與詳細資訊查詢（支援分頁）
- 線上訂房（日期驗證、價格計算、入住人數驗證）
- 訂房記錄管理（查詢、取消）
- 訂房狀態追蹤（PENDING / CONFIRMED / CANCELLED）
- 房間庫存管理（訂房時自動扣減，取消時自動恢復）
- 可容納人數與庫存即時顯示
- 管理員訂單管理（批次查詢、狀態更新、關鍵字搜尋）

### 訂單系統
- 購物車結帳功能
- 訂單記錄管理（我的訂單、全部訂單）
- 訂單狀態管理（PENDING / PAID / SHIPPED / COMPLETED / CANCELLED）
- 訂單取消與刪除
- 產品庫存管理（結帳時自動扣減，取消時自動恢復）
- 庫存不足自動阻擋結帳
- 管理員訂單管理（關鍵字搜尋）

### 電商購物車
- 商品加入購物車
- 購物車項目數量調整（1-99）
- 購物車項目移除與清空
- 自動計算小計、總價、總數量
- 購物車持久化儲存
- 管理員查看所有使用者購物車

### 評論系統
- 房型評論（Rating: 1-5 星）
- 產品評論（Rating: 1-5 星）
- 評論權限控管（僅本人與管理員可刪除）
- 評論內容驗證

### 產品管理
- 產品資訊 CRUD
- 產品分頁查詢
- 產品與評論關聯

### 內容管理
- 聯絡表單處理與儲存

### 管理後台
- 使用者管理（分頁查詢、關鍵字搜尋、角色指派）
- 訂房管理（分頁查詢、關鍵字搜尋、狀態管理）
- 訂單管理（分頁查詢、關鍵字搜尋、狀態管理）
- 產品與房型管理
- 評論審核與管理
- 聯絡表單管理（分頁查詢、刪除）

---

## 技術架構

### 核心技術棧
| 技術 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 程式語言 |
| Spring Boot | 3.5.6 | 應用框架 |
| Spring Data JPA | 3.5.6 | ORM 資料存取 |
| Spring Security | 6.x | 認證與授權 |
| H2 Database | 2.x | 嵌入式資料庫（檔案模式） |
| Hibernate | 6.x | JPA 實作 |
| Lombok | 1.18.x | 簡化程式碼 |
| MapStruct | 1.5.x | Bean 映射工具 |
| Maven | 3.9+ | 專案建構工具 |

### 架構設計模式

#### MVC 三層架構
```
┌──────────────────────────────────────────────────┐
│                   前端應用                        │
│                  (Angular)                       │
└────────────────────┬─────────────────────────────┘
                     │ HTTP Request/Response
                     │ (RESTful API)
┌────────────────────▼─────────────────────────────┐
│              Controller Layer                    │
│  ・HTTP 請求處理與參數驗證                         │
│  ・呼叫 Service 層業務邏輯                         │
│  ・封裝統一回應格式 (BaseResponse)                 │
└────────────────────┬─────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────┐
│               Service Layer                      │
│  ・核心業務邏輯實作                                │
│  ・事務管理 (@Transactional)                      │
│  ・資料驗證與例外處理                              │
│  ・DTO 與 Entity 轉換                             │
└────────────────────┬─────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────┐
│             Repository Layer                     │
│  ・JpaRepository 資料存取介面                     │
│  ・自訂 JPQL / Query Method                      │
│  ・分頁與排序查詢                                 │
└────────────────────┬─────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────┐
│              Database Layer                      │
│            H2 Database (File Mode)               │
│           ./data/mydb.mv.db                      │
└──────────────────────────────────────────────────┘
```

### 安全架構
- **認證機制**：Session-based Authentication（Spring Security Form Login）
- **密碼加密**：BCrypt 演算法
- **權限控管**：
  - URL 層級：SecurityFilterChain 配置
  - 方法層級：@PreAuthorize 註解
- **CORS 配置**：允許前端跨域請求（localhost:4200）
- **Session 管理**：JSESSIONID Cookie 機制

---

## 專案結構

```
backend/
├── src/main/java/ZGS/backend/
│   ├── BackendApplication.java           # Spring Boot 主程式入口
│   ├── BaseResponse.java                 # 統一 API 回應封裝類別
│   │
│   ├── config/                           # 系統配置
│   │   ├── DataInitializer.java         # 資料初始化（預設帳號、角色）
│   │   └── WebConfig.java               # Web 配置（CORS）
│   │
│   ├── controller/                       # 控制器層（處理 HTTP 請求）
│   │   ├── AuthController.java          # 認證控制器（註冊/登入/登出）
│   │   ├── UserController.java          # 使用者管理
│   │   ├── RoomController.java          # 房型管理
│   │   ├── BookingController.java       # 訂房管理
│   │   ├── OrderController.java         # 訂單管理
│   │   ├── CartController.java          # 購物車管理
│   │   ├── ProductController.java       # 產品管理
│   │   ├── ReviewController.java        # 評論管理
│   │   └── ContactController.java       # 聯絡表單
│   │
│   ├── service/                          # 業務邏輯層
│   │   ├── AuthService.java             # 註冊邏輯（密碼加密、角色指派）
│   │   ├── UserService.java             # 使用者業務邏輯（關鍵字搜尋、分頁）
│   │   ├── RoomService.java             # 房型業務邏輯
│   │   ├── BookingService.java          # 訂房邏輯（日期驗證、價格計算、關鍵字搜尋）
│   │   ├── OrderService.java            # 訂單邏輯（結帳、狀態管理、關鍵字搜尋）
│   │   ├── CartService.java             # 購物車邏輯（總價計算、數量驗證）
│   │   ├── ProductService.java          # 產品業務邏輯
│   │   ├── ReviewService.java           # 評論邏輯（評分驗證、權限檢查）
│   │   └── ...                          # 其他服務類別
│   │
│   ├── repository/                       # 資料存取層（JPA Repository）
│   │   ├── UserRepository.java          # 使用者資料存取（含關鍵字搜尋）
│   │   ├── RoleRepository.java          # 角色資料存取
│   │   ├── RoomRepository.java          # 房型資料存取
│   │   ├── BookingRepository.java       # 訂房資料存取（含關鍵字搜尋）
│   │   ├── OrderRepository.java         # 訂單資料存取（含關鍵字搜尋）
│   │   ├── CartRepository.java          # 購物車資料存取
│   │   ├── CartItemRepository.java      # 購物車項目資料存取
│   │   ├── ProductRepository.java       # 產品資料存取
│   │   ├── ReviewRepository.java        # 評論資料存取
│   │   └── ...                          # 其他 Repository
│   │
│   ├── entity/                           # JPA 實體類別（對應資料庫表格）
│   │   ├── User.java                    # 使用者實體（多對多 Role）
│   │   ├── Role.java                    # 角色實體（ADMIN/USER）
│   │   ├── Room.java                    # 房型實體
│   │   ├── Booking.java                 # 訂房實體（多對一 User、Room）
│   │   ├── Order.java                   # 訂單實體（快照設計，保留下單時價格）
│   │   ├── OrderItem.java               # 訂單明細實體
│   │   ├── Cart.java                    # 購物車實體（一對一 User）
│   │   ├── CartItem.java                # 購物車項目實體（多對一 Product）
│   │   ├── Product.java                 # 產品實體
│   │   ├── Review.java                  # 評論實體（多型關聯 Room/Product）
│   │   └── Contact.java                 # 聯絡表單實體
│   │
│   ├── dto/                              # 資料傳輸物件（與前端交互）
│   │   ├── UserDto.java                 # 使用者 DTO（不含密碼）
│   │   ├── RoomDto.java                 # 房型 DTO
│   │   ├── BookingDto.java              # 訂房 DTO
│   │   ├── OrderDto.java                # 訂單 DTO
│   │   ├── OrderItemDto.java            # 訂單明細 DTO
│   │   ├── CartDto.java                 # 購物車 DTO（含總價、總數量）
│   │   ├── CartItemDto.java             # 購物車項目 DTO（含產品名稱、價格）
│   │   ├── ProductDto.java              # 產品 DTO
│   │   ├── ReviewDto.java               # 評論 DTO
│   │   └── ContactDto.java              # 聯絡表單 DTO
│   │   # 註：LoginRequest / RegisterRequest 為 AuthController 內部類別
│   │
│   ├── mapper/                           # MapStruct 自動映射介面
│   │   ├── UserMapper.java              # User ↔ UserDto 轉換
│   │   ├── BookingMapper.java           # Booking ↔ BookingDto 轉換
│   │   ├── OrderMapper.java             # Order ↔ OrderDto 轉換
│   │   ├── CartMapper.java              # Cart ↔ CartDto 轉換
│   │   └── ...                          # 其他 Mapper
│   │
│   ├── exception/                        # 全域例外處理
│   │   └── GlobalExceptionHandler.java  # 統一例外攔截與回應
│   │
│   └── security/                         # Spring Security 相關
│       ├── SecurityConfig.java          # 安全配置（認證、授權、CORS）
│       └── CustomUserDetailsService.java # 使用者詳細資訊服務
│
├── src/main/resources/
│   ├── application.properties           # 應用配置檔案
│   └── data.sql                         # 初始化資料 SQL（選用）
│
├── data/                                 # H2 資料庫檔案目錄
│   └── mydb.mv.db                       # H2 資料庫檔案（持久化）
│
├── pom.xml                              # Maven 專案配置
└── README.md                            # 專案說明文件
```

---

## 環境建置與啟動

### 系統需求
- **JDK**：21 或以上版本
- **Maven**：3.9 或以上版本
- **記憶體**：建議 2GB 以上
- **硬碟空間**：500MB 以上

### 建置步驟

#### 1. 取得專案原始碼
使用 Git clone 取得專案後進入 backend 目錄。

#### 2. 編譯專案
使用 Maven clean compile 指令編譯專案。

#### 3. 執行單元測試（選用）
使用 Maven test 指令執行測試。

#### 4. 打包應用程式
使用 Maven clean package 指令打包，會在 target 目錄下生成 JAR 檔案。

#### 5. 啟動應用程式

**方式一：使用 Maven 插件**
執行 mvn spring-boot:run

**方式二：直接執行 JAR 檔案**
執行 java -jar target/backend-0.0.1-SNAPSHOT.jar

**方式三：在 IDE 中執行**
- 於 Visual Studio Code 中開啟專案
- 安裝 Java Extension Pack
- 執行 BackendApplication.java 主程式

### 驗證啟動狀態

應用程式啟動後，可透過以下方式確認：

1. **API 根路徑**：`http://localhost:8080`
2. **健康檢查**：`http://localhost:8080/actuator/health`（若啟用 Actuator）
3. **H2 資料庫控制台**：`http://localhost:8080/h2-console`
   - **JDBC URL**：`jdbc:h2:file:./data/mydb`
   - **Username**：`sa`
   - **Password**：` `

### 預設測試帳號

系統初始化時會建立以下測試帳號：

| 角色 | 帳號 | 密碼 | 說明 |
|------|------|------|------|
| 管理員 | `admin` | `admin123` | 擁有所有權限 |
| 一般使用者 | `user` | `user123` | 基本使用權限 |

### 設定檔說明

`src/main/resources/application.properties` 主要配置項目包含：

- 伺服器埠號：8080
- H2 資料庫配置（檔案模式）：jdbc:h2:file:./data/mydb
- H2 控制台：啟用於 /h2-console
- JPA 配置：DDL auto-update、SQL 顯示、Hibernate dialect
- Log 層級：ZGS.backend 與 Spring Security 設為 DEBUG

---

## API 文件

### 統一回應格式

所有 API 端點統一使用 `BaseResponse<T>` 格式封裝：

**成功回應結構**
- success: true
- message: 操作成功訊息
- data: 實際資料內容（可能為物件、陣列或 null）

**失敗回應結構**
- success: false
- message: 錯誤訊息描述
- data: null

**分頁回應結構**
- success: true
- message: 查詢成功訊息
- data: 包含 content（當前頁資料陣列）、totalElements（總筆數）、totalPages（總頁數）、size（每頁筆數）、number（當前頁碼）等欄位

### 認證相關 API

| HTTP 方法 | 端點 | 說明 | 權限 | 請求參數 |
|-----------|------|------|------|----------|
| POST | `/api/auth/register` | 使用者註冊 | Public | username, password, email, fullName |
| POST | `/api/auth/login` | 使用者登入 | Public | username, password |
| POST | `/api/auth/logout` | 使用者登出 | Authenticated | - |

### 使用者管理 API

| HTTP 方法 | 端點 | 說明 | 權限 | 參數 |
|-----------|------|------|------|------|
| GET | `/api/users/me` | 取得當前使用者資訊 | USER | - |
| PUT | `/api/users/me` | 更新個人資料 | USER | email, fullName |
| PUT | `/api/users/me/password` | 修改密碼 | USER | oldPassword, newPassword |
| GET | `/api/users/paged` | 分頁查詢使用者 | ADMIN | page, size, keyword |
| GET | `/api/users/{id}` | 取得使用者詳細資訊 | ADMIN | - |
| PUT | `/api/users/{id}` | 更新使用者資料 | ADMIN | email, fullName |
| PUT | `/api/users/{id}/roles` | 指派使用者角色 | ADMIN | roleNames[] |
| DELETE | `/api/users/{id}` | 刪除使用者 | ADMIN | - |

### 房型管理 API

| HTTP 方法 | 端點 | 說明 | 權限 | 參數 |
|-----------|------|------|------|------|
| GET | `/api/rooms` | 取得所有房型 | Public | - |
| GET | `/api/rooms/paged` | 分頁查詢房型 | Public | page, size |
| GET | `/api/rooms/{id}` | 取得房型詳細資訊 | Public | - |
| POST | `/api/rooms` | 新增房型 | ADMIN | name, type, price, capacity, description, available, stock |
| PUT | `/api/rooms/{id}` | 更新房型資訊 | ADMIN | (同新增) |
| DELETE | `/api/rooms/{id}` | 刪除房型 | ADMIN | - |

### 訂房管理 API

| HTTP 方法 | 端點 | 說明 | 權限 | 參數 |
|-----------|------|------|------|------|
| POST | `/api/bookings/room/{roomId}` | 建立訂房 | USER | checkInDate, checkOutDate, guestCount |
| GET | `/api/bookings/my` | 取得我的訂房記錄 | USER | - |
| PUT | `/api/bookings/{id}/cancel` | 取消訂房 | USER | - |
| GET | `/api/bookings/admin/all` | 分頁查詢所有訂房 | ADMIN | page, size, keyword |
| PUT | `/api/bookings/admin/{id}/status` | 更新訂房狀態 | ADMIN | status |
| DELETE | `/api/bookings/admin/{id}` | 刪除訂房記錄 | ADMIN | - |

**訂房業務規則**：
- 訂房時會自動驗證房型是否開放預訂（available）
- 訂房時會檢查庫存是否充足（stock > 0）
- 訂房時會驗證入住人數不超過房型容納上限（guestCount ≤ capacity）
- 訂房成功後房型庫存自動減 1
- 取消訂房時（PENDING/CONFIRMED 狀態）房型庫存自動加 1

### 訂單管理 API

| HTTP 方法 | 端點 | 說明 | 權限 | 參數 |
|-----------|------|------|------|------|
| POST | `/api/orders/checkout` | 購物車結帳 | USER | shippingAddress, phoneNumber, note |
| GET | `/api/orders/my-orders` | 取得我的訂單 | USER | - |
| PUT | `/api/orders/{id}/cancel` | 取消訂單 | USER | - |
| GET | `/api/orders/all` | 分頁查詢所有訂單 | ADMIN | page, size, keyword |
| GET | `/api/orders/{id}` | 取得訂單詳細資訊 | Owner/ADMIN | - |
| PUT | `/api/orders/{id}/status` | 更新訂單狀態 | ADMIN | status |
| DELETE | `/api/orders/{id}` | 刪除訂單 | ADMIN | - |

**訂單業務規則**：
- 結帳前會自動驗證所有商品庫存是否充足
- 任一商品庫存不足會阻擋整筆交易並回傳錯誤訊息
- 結帳成功後會自動扣減所有商品庫存（stock - quantity）
- 取消訂單時（PENDING 狀態）會自動恢復所有商品庫存（stock + quantity）
- 使用交易管理確保庫存操作的原子性

### 購物車 API

| HTTP 方法 | 端點 | 說明 | 權限 | 參數 |
|-----------|------|------|------|------|
| GET | `/api/cart` | 取得我的購物車 | USER | - |
| POST | `/api/cart/add` | 加入商品至購物車 | USER | productId, quantity |
| PUT | `/api/cart/item/{itemId}` | 更新購物車項目數量 | USER | quantity (1-99) |
| DELETE | `/api/cart/item/{itemId}` | 移除購物車項目 | USER | - |
| DELETE | `/api/cart/clear` | 清空購物車 | USER | - |
| GET | `/api/cart/admin/all` | 查看所有使用者購物車 | ADMIN | - |

### 評論系統 API

| HTTP 方法 | 端點 | 說明 | 權限 | 參數 |
|-----------|------|------|------|------|
| GET | `/api/reviews/room/{roomId}` | 取得房型評論 | Public | - |
| GET | `/api/reviews/product/{productId}` | 取得產品評論 | Public | - |
| POST | `/api/reviews/room/{roomId}` | 新增房型評論 | USER | rating (1-5), comment |
| POST | `/api/reviews/product/{productId}` | 新增產品評論 | USER | rating (1-5), comment |
| DELETE | `/api/reviews/{id}` | 刪除評論 | Owner/ADMIN | - |

### 產品管理 API

| HTTP 方法 | 端點 | 說明 | 權限 | 參數 |
|-----------|------|------|------|------|
| GET | `/api/product` | 取得所有產品 | Public | - |
| GET | `/api/product/paged` | 分頁查詢產品 | Public | page, size |
| GET | `/api/product/{id}` | 取得產品詳細資訊 | Public | - |
| POST | `/api/product` | 新增產品 | ADMIN | name, price, description, imageUrl, stock |
| PUT | `/api/product/{id}` | 更新產品資訊 | ADMIN | (同新增) |
| DELETE | `/api/product/{id}` | 刪除產品 | ADMIN | - |

### 內容管理 API

| HTTP 方法 | 端點 | 說明 | 權限 |
|-----------|------|------|------|
| POST | `/api/contact` | 提交聯絡表單 | Public |
| GET | `/api/contact/paged` | 分頁查詢聯絡表單 | ADMIN |
| DELETE | `/api/contact/{id}` | 刪除聯絡表單 | ADMIN |

---

## 資料庫設計

### ER 圖關聯概述

```
┌──────────────┐
│     User     │
│ (使用者)      │
└───┬────┬─────┘
    │    │
    │    └─────────────────────┐
    │                          │
    ├─────────┐                │
    │         │                │
    ↓         ↓                ↓
┌────────┐ ┌───────┐      ┌────────┐
│Booking │ │Review │      │  Cart  │
│(訂房)  │ │(評論) │      │(購物車)│
└───┬────┘ └┬──┬───┘      └───┬────┘
    │       |  │              │
    │       |  │              ↓
    ↓       |  │         ┌─────────┐
┌────────┐  |  │         │CartItem │
│  Room  │◄─|  |         │(購物車  │
│(房型)  │     │         │  項目)  │
└────────┘     │         └────┬────┘
               │              │
               ↓              ↓
         ┌─────────┐    ┌─────────┐
         │ Product │────│  Order  │
         │ (產品)  │    │  (訂單)  │
         └─────────┘    └─────────┘
                             │
                             ↓
                        ┌──────────┐
                        │OrderItem │
                        │(訂單明細) │
                        └──────────┘

┌─────────┐
│ Contact │  (獨立表格，無外鍵關聯)
│(聯絡表單)│
└─────────┘

Role (角色) ←─ N:M ─→ User (使用者)
Review (評論) ─→ Room (房型) 或 Product (產品) (多型關聯)
```

### 核心資料表結構

#### User（使用者）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主鍵 |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 帳號 |
| password | VARCHAR(255) | NOT NULL | BCrypt 加密密碼 |
| email | VARCHAR(100) | UNIQUE, NOT NULL | 電子郵件 |
| fullName | VARCHAR(100) | NOT NULL | 全名 |
| createdAt | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 建立時間 |
| updatedAt | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | 更新時間 |

**關聯關係**：
- 多對多 Role（透過 user_roles 中介表）
- 一對多 Booking、Review、Order
- 一對一 Cart

#### Role（角色）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY | 主鍵 |
| name | VARCHAR(50) | UNIQUE, NOT NULL | 角色名稱（ROLE_ADMIN / ROLE_USER） |

#### Room（房型）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主鍵 |
| name | VARCHAR(100) | NOT NULL | 房型名稱 |
| type | VARCHAR(50) | NOT NULL | 房型類別 |
| price | DECIMAL(10,2) | NOT NULL, CHECK (price > 0) | 每晚價格 |
| capacity | INT | | 可容納人數 |
| description | TEXT | | 詳細描述 |
| available | BOOLEAN | DEFAULT TRUE | 是否可預訂 |
| stock | INT | DEFAULT 0 | 可預訂數量 |

**關聯關係**：一對多 Booking、Review

#### Booking（訂房記錄）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主鍵 |
| userId | BIGINT | FOREIGN KEY → User(id) | 使用者 ID |
| roomId | BIGINT | FOREIGN KEY → Room(id) | 房型 ID |
| checkInDate | DATE | NOT NULL | 入住日期 |
| checkOutDate | DATE | NOT NULL | 退房日期 |
| guestCount | INT | NOT NULL, CHECK (guestCount > 0) | 入住人數 |
| totalPrice | DECIMAL(10,2) | NOT NULL | 總金額 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | 狀態 |
| note | TEXT | | 備註 |
| createdAt | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 建立時間 |

**業務規則**：checkOutDate > checkInDate

#### Cart（購物車）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主鍵 |
| userId | BIGINT | UNIQUE, FOREIGN KEY → User(id) | 使用者 ID（一對一） |
| createdAt | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 建立時間 |
| updatedAt | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | 更新時間 |

**關聯關係**：一對一 User、一對多 CartItem（orphanRemoval = true）

#### CartItem（購物車項目）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主鍵 |
| cartId | BIGINT | FOREIGN KEY → Cart(id) ON DELETE CASCADE | 購物車 ID |
| productId | BIGINT | FOREIGN KEY → Product(id) | 產品 ID |
| quantity | INT | NOT NULL, CHECK (quantity BETWEEN 1 AND 99) | 數量 |
| subtotal | DECIMAL(10,2) | NOT NULL | 小計 |

#### Product（產品）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主鍵 |
| name | VARCHAR(100) | NOT NULL | 產品名稱 |
| price | DECIMAL(10,2) | NOT NULL, CHECK (price > 0) | 單價 |
| description | TEXT | | 描述 |
| imageUrl | VARCHAR(500) | | 圖片網址 |
| stock | INT | NOT NULL, DEFAULT 0 | 庫存數量 |

**關聯關係**：一對多 Review、CartItem、OrderItem

#### Order（訂單）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主鍵 |
| userId | BIGINT | FOREIGN KEY → User(id) | 使用者 ID |
| totalAmount | DECIMAL(10,2) | NOT NULL | 訂單總金額 |
| shippingAddress | VARCHAR(500) | NOT NULL | 配送地址 |
| phoneNumber | VARCHAR(20) | NOT NULL | 聯絡電話 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | 訂單狀態 |
| note | TEXT | | 備註 |
| createdAt | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 建立時間 |

**關聯關係**：多對一 User、一對多 OrderItem

#### OrderItem（訂單明細）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主鍵 |
| orderId | BIGINT | FOREIGN KEY → Order(id) ON DELETE CASCADE | 訂單 ID |
| productName | VARCHAR(100) | NOT NULL | 產品名稱（**快照**） |
| productPrice | DECIMAL(10,2) | NOT NULL | 產品單價（**快照**） |
| quantity | INT | NOT NULL | 購買數量 |
| subtotal | DECIMAL(10,2) | NOT NULL | 小計 |

> **快照設計**：OrderItem 儲存下單當時的 productName 和 productPrice，避免產品資訊異動影響歷史訂單資料完整性。

#### Review（評論）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主鍵 |
| userId | BIGINT | FOREIGN KEY → User(id) | 使用者 ID |
| productId | BIGINT | FOREIGN KEY → Product(id) | 產品 ID（可為 null） |
| roomId | BIGINT | FOREIGN KEY → Room(id) | 房型 ID（可為 null） |
| rating | INT | NOT NULL, CHECK (rating BETWEEN 1 AND 5) | 評分 |
| comment | TEXT | | 評論內容 |
| createdAt | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 建立時間 |

**業務規則**：productId 與 roomId 至少需填一個

#### Contact（聯絡表單）
| 欄位 | 類型 | 約束 | 說明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主鍵 |
| name | VARCHAR(255) | NOT NULL | 聯絡人姓名 |
| email | VARCHAR(255) | NOT NULL | 聯絡人 Email |
| phone | VARCHAR(255) | NOT NULL | 聯絡電話 |
| message | VARCHAR(5000) | NOT NULL | 訊息內容 |
| userId | BIGINT | FOREIGN KEY → User(id) | 使用者 ID（可為 null，支援未登入提交） |
| createdAt | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 建立時間 |

**關聯關係**：多對一 User（可選）

### 資料完整性策略

- **唯一性約束**：User.username、User.email、Cart.userId
- **外鍵約束**：確保參照完整性，防止孤兒資料
- **檢查約束**：Rating (1-5)、Quantity (1-99)、Price > 0
- **級聯刪除**：Cart 刪除時自動刪除所有 CartItem
- **索引優化**：User.username、User.email、Booking.userId、Order.userId

---

## 核心業務邏輯

### 使用者註冊與認證
```java
// 密碼加密（BCrypt 強度 10）
String encodedPassword = passwordEncoder.encode(plainPassword);

// 預設角色分配
Set<Role> roles = new HashSet<>();
roles.add(roleRepository.findByName("ROLE_USER"));
user.setRoles(roles);

// Session 管理
// Spring Security 自動建立 HttpSession，回傳 JSESSIONID Cookie
```

### 訂房業務流程
1. **前置驗證**：
   - 檢查房型是否開放預訂（available = true）
   - 檢查房型庫存是否充足（stock > 0）
   - 驗證入住人數不超過房型容納上限（guestCount ≤ capacity）
2. **日期驗證**：checkInDate ≥ 今日，checkOutDate > checkInDate
3. **價格計算**：totalPrice = room.price × (checkOutDate - checkInDate).days
4. **庫存扣減**：訂房成功後自動將房型庫存減 1
5. **狀態管理**：PENDING → CONFIRMED → CANCELLED
6. **取消恢復**：訂房取消時自動將房型庫存加 1（僅限 PENDING/CONFIRMED 狀態）

### 購物車核心邏輯

系統採用 Lazy Initialization 策略，當使用者首次加入商品時才自動建立購物車。每次加入商品時會驗證數量範圍（1-99）並計算小計金額（產品單價 × 數量）。購物車總價透過累加所有項目小計計算得出。

### 訂單結帳流程

結帳流程使用 @Transactional 確保原子性操作：

1. **購物車驗證**：檢查購物車存在且不為空
2. **庫存預檢**：遍歷所有商品驗證庫存充足，任一商品庫存不足則拋出例外，整個交易回滾
3. **建立訂單**：設定使用者、配送地址、聯絡電話等基本資訊
4. **轉換明細**：將購物車項目轉換為訂單明細，採用快照設計保留當下的產品名稱和價格
5. **扣減庫存**：遍歷所有訂單項目，將對應產品庫存減去購買數量
6. **計算總額**：累加所有明細小計得出訂單總金額
7. **儲存訂單**：將訂單及明細持久化至資料庫
8. **清空購物車**：刪除購物車（級聯刪除所有購物車項目）

**訂單取消流程**：
1. 驗證訂單狀態為 PENDING（僅允許待處理訂單取消）
2. 遍歷所有訂單項目，將購買數量加回產品庫存
3. 更新訂單狀態為 CANCELLED

### 評論系統權限控管

建立評論時系統會驗證評分範圍必須在 1-5 之間。刪除評論時會檢查當前使用者是否為評論擁有者或管理員，只有符合條件才允許刪除操作。

### 管理後台搜尋功能

系統提供多欄位模糊搜尋功能，透過 JPA Query Method 實現分頁查詢：

- **使用者搜尋**：支援 username、email、fullName 欄位
- **訂房搜尋**：支援使用者名稱、房型名稱、訂房狀態
- **訂單搜尋**：支援訂單 ID、使用者名稱、配送地址、聯絡電話

所有搜尋均不區分大小寫，採用 LIKE 模糊比對。

---

## 安全性設計

### 認證機制
- **Session-based Authentication**：Spring Security Form Login
- **密碼加密**：BCrypt（不可逆，無法解密）
- **Session 管理**：
  - Session ID 儲存於 JSESSIONID Cookie（HttpOnly）
  - 預設 30 分鐘無活動自動過期
  - 支援 Remember-Me（選用功能）

### 授權架構

#### URL 層級權限（SecurityFilterChain）

**公開端點**：
- 認證相關：/api/auth/**
- 查詢房型：GET /api/rooms/**
- 查詢產品：GET /api/product/**

**需登入端點**：
- 購物車操作：/api/cart/**
- 訂單管理：/api/orders/**
- 訂房管理：/api/bookings/**

**管理員端點**：
- 使用者管理：/api/users/**
- 訂房管理後台：/api/bookings/admin/**
- 訂單管理後台：/api/orders/admin/**
- 聯絡表單管理：GET/DELETE /api/contact/**（除了 POST /api/contact）

#### 方法層級權限（@PreAuthorize）

系統使用 @PreAuthorize 註解實現細粒度權限控制：
- hasRole('ADMIN')：限制管理員專用方法
- hasAnyRole('USER', 'ADMIN')：允許一般使用者與管理員
- 自訂 SpEL 表達式：檢查資源擁有者或管理員身分

### CORS 配置

允許前端網址（http://localhost:4200）跨域存取，支援 GET、POST、PUT、DELETE、OPTIONS 方法，允許所有 Headers，並啟用 credentials 以攜帶 Cookie。

### 輸入驗證策略
1. **前端驗證**：Angular Reactive Forms（即時回饋）
2. **後端參數驗證**：Bean Validation (@NotNull, @Email, @Size, @Pattern)
3. **業務邏輯驗證**：Service 層自訂驗證規則
4. **SQL 注入防護**：JPA Parameterized Query

---

## 技術亮點

### 1. 標準 MVC 架構分層
- **Controller**：僅處理 HTTP 請求/回應，參數綁定與驗證
- **Service**：封裝業務邏輯，@Transactional 事務管理
- **Repository**：JPA 資料存取，自訂 Query Method
- **DTO/Mapper**：Entity 與 DTO 分離，MapStruct 自動映射

### 2. 統一回應封裝（BaseResponse<T>）

BaseResponse 類別包含三個欄位：success（布林值）、message（訊息字串）、data（泛型資料）。所有 API 統一使用此格式回應，前端處理邏輯簡化，成功/失敗狀態明確，支援泛型可適配各種資料類型。

### 3. 完整的庫存管理系統
- **房型庫存**：
  - 訂房時驗證庫存充足性（stock > 0）
  - 訂房成功自動扣減庫存（stock - 1）
  - 訂房取消自動恢復庫存（stock + 1）
  - 可容納人數驗證（guestCount ≤ capacity）
  - 房型開放狀態控制（available 標誌）
- **產品庫存**：
  - 結帳前預檢所有商品庫存
  - 庫存不足阻擋整筆交易
  - 結帳成功自動扣減庫存（stock - quantity）
  - 訂單取消自動恢復庫存（stock + quantity）
- **交易安全性**：
  - @Transactional 確保原子性操作
  - 預檢機制避免超賣問題
  - 失敗自動回滾所有變更

### 4. 購物車核心設計
- **Lazy Initialization**：首次加入商品時自動建立購物車
- **Orphan Removal**：購物車刪除時自動清理所有項目
- **即時價格同步**：從 Product 取得最新價格
- **總價自動計算**：getTotalPrice() / getTotalItems() 輔助方法

### 5. 訂單快照設計
- OrderItem 儲存下單當時的產品名稱與價格
- 避免產品資訊變更影響歷史訂單
- 符合電商業務最佳實踐

### 6. 分頁與搜尋功能
- Spring Data JPA Pageable 支援
- 關鍵字模糊搜尋（多欄位 OR 條件）
- 前端統一分頁元件整合

### 7. 完善的例外處理

使用 @RestControllerAdvice 全域攔截例外，將各種例外轉換為統一的 BaseResponse 格式回應。例如 ResourceNotFoundException 會回傳 HTTP 404 狀態碼及錯誤訊息，確保前端能一致處理錯誤情況。

---