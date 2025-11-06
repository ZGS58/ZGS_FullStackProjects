# 度假村綜合管理系統 (ZGS Full Stack Project)

基於 **Spring Boot 3.5.6** 和 **Angular 19** 開發的度假村綜合管理系統，提供完整的前後端分離架構。系統涵蓋會員管理、房型預訂、電商購物、評論系統等核心業務功能，適用於旅館、民宿、度假村等住宿業線上預訂場景。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-19-red.svg)](https://angular.io/)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue.svg)](https://www.typescriptlang.org/)

---

##  目錄

- [專案架構](#專案架構)
- [核心功能](#核心功能)
- [技術棧](#技術棧)
- [系統截圖](#系統截圖)
- [快速開始](#快速開始)
- [專案結構](#專案結構)
- [API 文件](#api-文件)
- [開發指南](#開發指南)
- [部署說明](#部署說明)
- [常見問題](#常見問題)
- [授權資訊](#授權資訊)

---

##  專案架構

```
┌─────────────────────────────────────────────────────────────┐
│                        使用者                                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ HTTP/HTTPS
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    前端 (Angular 19)                        │
│  ・Standalone Components                                    │
│  ・Reactive Forms                                           │
│  ・Angular Router                                           │
│  ・響應式設計                                                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ RESTful API (JSON)
                     │ Session Cookie (JSESSIONID)
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  後端 (Spring Boot 3.5.6)                    │
│  ・MVC 三層架構                                              │
│  ・Spring Security (Session-based Auth)                     │
│  ・Spring Data JPA                                          │
│  ・統一回應封裝 (BaseResponse)                               │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ JPA/Hibernate
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                   資料庫 (H2 Database)                       │
│  ・檔案模式持久化 (./data/mydb.mv.db)                        │
│  ・支援 SQL 與 JPA Query                                     │
└─────────────────────────────────────────────────────────────┘
```

---

##  核心功能

###  會員系統
-   使用者註冊、登入、登出 (Session-based Authentication)
-   個人資料管理 (CRUD)
-   密碼加密儲存 (BCrypt)
-   角色權限控管 (ADMIN / USER)

###  訂房系統
-   房型瀏覽與詳細資訊查詢（支援分頁）
-   線上訂房（日期驗證、價格計算、入住人數驗證）
-   訂房記錄管理（查詢、取消）
-   訂房狀態追蹤（PENDING / CONFIRMED / CANCELLED）
-   **房間庫存管理**（訂房時自動扣減，取消時自動恢復）
-   **可容納人數驗證**（入住人數不可超過容納上限）
-   **房型開放狀態控制**（可設定房型是否開放預訂）

###  電商購物
-   商品加入購物車
-   購物車項目數量調整（1-99）
-   購物車結帳流程
-   自動計算小計、總價、總數量
-   **產品庫存管理**（結帳時自動扣減，取消時自動恢復）
-   **庫存不足自動阻擋結帳**

###  訂單系統
-   訂單記錄管理（我的訂單、全部訂單）
-   訂單狀態管理（PENDING / PAID / SHIPPED / COMPLETED / CANCELLED）
-   訂單取消與刪除
-   **訂單快照設計**（保留下單時的產品名稱與價格）

###  評論系統
-   房型評論（Rating: 1-5 星）
-   產品評論（Rating: 1-5 星）
-   評論權限控管（僅本人與管理員可刪除）

###  管理後台
-   使用者管理（分頁查詢、關鍵字搜尋、角色指派）
-   訂房管理（分頁查詢、關鍵字搜尋、狀態管理）
-   訂單管理（分頁查詢、關鍵字搜尋、狀態管理）
-   產品與房型管理（CRUD、庫存管理）
-   評論審核與管理
-   聯絡表單管理（分頁查詢、刪除）
-   購物車監控（查看所有使用者購物車）

---

##  技術棧

### 後端 (Backend)
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

### 前端 (Frontend)
| 技術 | 版本 | 用途 |
|------|------|------|
| Angular | 19.2.17 | 前端框架 |
| TypeScript | 5.x | 程式語言 |
| RxJS | 7.x | 響應式程式設計 |
| Angular Router | 19.x | 路由管理 |
| Angular Forms | 19.x | 表單處理 |
| HttpClient | 19.x | HTTP 請求 |

---

##  快速開始

### 系統需求

#### 後端
- **JDK**: 21 或以上版本
- **Maven**: 3.9 或以上版本

#### 前端
- **Node.js**: 18.x 或以上版本
- **npm**: 9.x 或以上版本

### 啟動步驟

#### 1 啟動後端服務

```bash
# 進入後端目錄
cd backend

# 使用 Maven 啟動
mvn spring-boot:run

# 或打包後執行
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

後端服務預設運行於 `http://localhost:8080`

**H2 資料庫控制台**: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/mydb`
- Username: `sa`
- Password: *(空白)*

#### 2 啟動前端應用

```bash
# 進入前端目錄
cd frontend

# 安裝依賴
npm install

# 啟動開發伺服器
ng serve
```

前端應用預設運行於 `http://localhost:4200`

#### 3 訪問系統

開啟瀏覽器訪問 `http://localhost:4200`

### 預設測試帳號

| 角色 | 帳號 | 密碼 | 說明 |
|------|------|------|------|
| 管理員 | `admin` | `admin123` | 擁有所有權限 |
| 一般使用者 | `user` | `user123` | 基本使用權限 |

---

##  專案結構

```
ZGS_FullStackProjects/
├── backend/                           # Spring Boot 後端專案
│   ├── src/main/java/ZGS/backend/
│   │   ├── BackendApplication.java  # 主程式入口
│   │   ├── BaseResponse.java        # 統一回應封裝
│   │   │
│   │   ├── controller/               # 控制器層 (RESTful API)
│   │   │   ├── AuthController       # 認證 API
│   │   │   ├── UserController       # 使用者管理 API
│   │   │   ├── RoomController       # 房型管理 API
│   │   │   ├── BookingController    # 訂房管理 API
│   │   │   ├── ProductController    # 產品管理 API
│   │   │   ├── CartController       # 購物車 API
│   │   │   ├── OrderController      # 訂單管理 API
│   │   │   ├── ReviewController     # 評論管理 API
│   │   │   └── ContactController    # 聯絡表單 API
│   │   │
│   │   ├── service/                  # 業務邏輯層
│   │   ├── repository/               # 資料存取層 (JPA)
│   │   ├── entity/                   # JPA 實體類別
│   │   ├── dto/                      # 資料傳輸物件
│   │   ├── mapper/                   # MapStruct 映射介面
│   │   ├── config/                   # 系統配置
│   │   ├── security/                 # Spring Security 配置
│   │   └── exception/                # 全域例外處理
│   │
│   ├── src/main/resources/
│   │   └── application.properties   # 應用配置檔案
│   ├── data/                         # H2 資料庫檔案目錄
│   ├── pom.xml                       # Maven 專案配置
│   └── README.md                     # 後端說明文件
│
├── frontend/                          # Angular 前端專案
│   ├── src/app/
│   │   ├── pages/                    # 頁面元件
│   │   │   ├── home/                # 首頁
│   │   │   ├── about/               # 關於我們
│   │   │   ├── room/                # 房型瀏覽與訂房
│   │   │   ├── product/             # 產品商城
│   │   │   ├── cart/                # 購物車
│   │   │   ├── login/               # 登入/註冊
│   │   │   ├── profile/             # 個人資料
│   │   │   ├── my-bookings/         # 我的訂房
│   │   │   ├── contact/             # 聯絡我們
│   │   │   ├── news/                # 最新消息
│   │   │   ├── qa/                  # 常見問題
│   │   │   └── admin/               # 管理後台
│   │   │
│   │   └── services/                 # API 服務層
│   │       ├── auth.service         # 認證服務
│   │       ├── user.service         # 使用者服務
│   │       ├── room.service         # 房型服務
│   │       ├── booking.service      # 訂房服務
│   │       ├── product.service      # 產品服務
│   │       ├── cart.service         # 購物車服務
│   │       ├── order.service        # 訂單服務
│   │       ├── review.service       # 評論服務
│   │       └── contact.service      # 聯絡服務
│   │
│   ├── angular.json                  # Angular 專案配置
│   ├── package.json                  # npm 依賴管理
│   ├── tsconfig.json                 # TypeScript 配置
│   └── README.md                     # 前端說明文件
├── LICENSE
├── .gitignore                         # Git 忽略檔案規則
└── README.md                          # 專案整體說明（本檔案）
```

---

##  API 文件

### 統一回應格式

所有 API 端點統一使用 `BaseResponse<T>` 格式封裝：

**成功回應結構**
```json
{
  "success": true,
  "message": "操作成功訊息",
  "data": { /* 實際資料內容 */ }
}
```

**失敗回應結構**
```json
{
  "success": false,
  "message": "錯誤訊息描述",
  "data": null
}
```

### 主要 API 端點

| 分類 | HTTP 方法 | 端點 | 說明 | 權限 |
|------|-----------|------|------|------|
| **認證** | POST | `/api/auth/register` | 使用者註冊 | Public |
| | POST | `/api/auth/login` | 使用者登入 | Public |
| | POST | `/api/auth/logout` | 使用者登出 | USER |
| **使用者** | GET | `/api/users/me` | 取得當前使用者資訊 | USER |
| | PUT | `/api/users/me` | 更新個人資料 | USER |
| | GET | `/api/users/paged` | 分頁查詢使用者 | ADMIN |
| **房型** | GET | `/api/rooms` | 取得所有房型 | Public |
| | GET | `/api/rooms/{id}` | 取得房型詳細資訊 | Public |
| | POST | `/api/rooms` | 新增房型 | ADMIN |
| **訂房** | POST | `/api/bookings/room/{roomId}` | 建立訂房 | USER |
| | GET | `/api/bookings/my` | 取得我的訂房記錄 | USER |
| | PUT | `/api/bookings/{id}/cancel` | 取消訂房 | USER |
| **產品** | GET | `/api/product` | 取得所有產品 | Public |
| | POST | `/api/product` | 新增產品 | ADMIN |
| **購物車** | GET | `/api/cart` | 取得我的購物車 | USER |
| | POST | `/api/cart/add` | 加入商品至購物車 | USER |
| | DELETE | `/api/cart/clear` | 清空購物車 | USER |
| **訂單** | POST | `/api/orders/checkout` | 購物車結帳 | USER |
| | GET | `/api/orders/my-orders` | 取得我的訂單 | USER |
| | PUT | `/api/orders/{id}/cancel` | 取消訂單 | USER |
| **評論** | GET | `/api/reviews/room/{roomId}` | 取得房型評論 | Public |
| | POST | `/api/reviews/room/{roomId}` | 新增房型評論 | USER |

>  完整 API 文件請參考 [backend/README.md](./backend/README.md)

---

##  開發指南

### 後端開發

詳細的後端開發說明請參考 [backend/README.md](./backend/README.md)

**主要技術要點**:
- 標準 MVC 三層架構 (Controller-Service-Repository)
- Spring Security Session-based 認證
- JPA Entity 與 DTO 轉換 (MapStruct)
- 統一例外處理 (@RestControllerAdvice)
- 事務管理 (@Transactional)

### 前端開發

詳細的前端開發說明請參考 [frontend/README.md](./frontend/README.md)

**主要技術要點**:
- Angular 19 Standalone Components
- Reactive Forms 表單處理
- 服務導向架構 (Service Layer)
- Session Cookie 認證 (withCredentials)
- 響應式設計

### 開發工具建議

- **IDE**: Visual Studio Code (前端與後端)
- **API 測試**: Postman / Insomnia
- **資料庫工具**: H2 Console / DBeaver
- **版本控制**: Git

---

##  授權資訊

本專案採用 **MIT License** 授權。

---

##  貢獻者

- **ZGS** - 初始開發與維護

---

##  聯絡方式

如有任何問題或建議，歡迎透過以下方式聯繫：

- **Email**: s.a.m.93.5.8.sam@gmail.com
- **GitHub Issues**: [專案 Issues 頁面](https://github.com/ZGS58/ZGS_FullStackProjects/issues)

---

<p align="center">
  Made with ❤️ by ZGS
  <br>
  <sub>© 2025 ZGS Full Stack Projects. All rights reserved.</sub>
</p>
