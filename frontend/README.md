# 度假村綜合管理系統 - 前端

基於 Angular 19 開發的度假村綜合管理系統前端應用，採用 Standalone Components 架構，提供完整的使用者介面與管理後台功能。

## 專案概述

### 技術特色
- **Angular 19 Standalone Components**：無需 NgModule 的現代化元件架構
- **Reactive Forms**：響應式表單處理與驗證
- **服務導向架構**：統一的 API 服務層封裝
- **Session-based 認證**：與後端 Spring Security 無縫整合
- **響應式設計**：支援多種裝置螢幕尺寸
- **模組化 CSS**：元件級樣式隔離

### 核心功能模組

#### 公開頁面
- **首頁**：展示度假村特色與服務簡介
- **房型瀏覽**：房型列表、詳細資訊、庫存即時顯示、線上訂房
- **產品商城**：產品列表、詳細資訊、庫存顯示、加入購物車
- **關於我們**：度假村介紹與歷史
- **常見問題**：Q&A 列表
- **聯絡我們**：聯絡表單提交

#### 會員功能
- **使用者登入/註冊**：表單驗證與錯誤處理
- **個人資料管理**：查看與編輯個人資訊
- **我的訂房**：訂房記錄查詢與取消
- **我的訂單**：訂單記錄查詢與取消
- **購物車**：商品管理、數量調整、結帳流程

#### 管理後台
- **使用者管理**：分頁查詢、關鍵字搜尋、角色指派
- **訂房管理**：訂房記錄查詢、狀態管理、關鍵字搜尋
- **訂單管理**：訂單查詢、狀態管理、關鍵字搜尋
- **房型管理**：房型 CRUD、容納人數設定、庫存管理、開放狀態控制
- **產品管理**：產品 CRUD、庫存管理
- **購物車監控**：查看所有使用者購物車狀態
- **聯絡表單管理**：查詢與刪除聯絡表單

---

## 技術架構

### 核心技術棧
| 技術 | 版本 | 用途 |
|------|------|------|
| Angular | 19.2.17 | 前端框架 |
| TypeScript | 5.x | 程式語言 |
| RxJS | 7.x | 響應式程式設計 |
| Angular Router | 19.x | 路由管理 |
| Angular Forms | 19.x | 表單處理 |
| HttpClient | 19.x | HTTP 請求 |

### 專案架構

```
frontend/
├── src/
│   ├── app/
│   │   ├── app.component.ts              # 根元件
│   │   ├── app.config.ts                 # 應用配置
│   │   ├── app.routes.ts                 # 路由配置
│   │   │
│   │   ├── pages/                        # 頁面元件
│   │   │   ├── home/                     # 首頁
│   │   │   ├── about/                    # 關於我們
│   │   │   ├── contact/                  # 聯絡我們
│   │   │   ├── qa/                       # 常見問題
│   │   │   ├── news/                     # 最新消息
│   │   │   ├── login/                    # 登入/註冊
│   │   │   ├── profile/                  # 個人資料
│   │   │   ├── room/                     # 房型瀏覽與訂房
│   │   │   ├── product/                  # 產品商城
│   │   │   ├── my-bookings/              # 我的訂房
│   │   │   └── admin/                    # 管理後台
│   │   │       ├── admin-dashboard/      # 管理儀表板（多標籤）
│   │   │       ├── user-management/      # 使用者管理
│   │   │       └── booking-management/   # 訂房管理
│   │   │
│   │   └── services/                     # API 服務層
│   │       ├── auth.service.ts           # 認證服務
│   │       ├── user.service.ts           # 使用者服務
│   │       ├── room.service.ts           # 房型服務
│   │       ├── booking.service.ts        # 訂房服務
│   │       ├── product.service.ts        # 產品服務
│   │       ├── cart.service.ts           # 購物車服務
│   │       ├── order.service.ts          # 訂單服務
│   │       ├── review.service.ts         # 評論服務
│   │       └── contact.service.ts        # 聯絡服務
│   │
│   ├── index.html                        # HTML 進入點
│   ├── main.ts                           # 應用程式啟動
│   └── styles.css                        # 全域樣式
│
├── angular.json                          # Angular 專案配置
├── package.json                          # npm 依賴管理
├── tsconfig.json                         # TypeScript 配置
└── README.md                             # 專案說明
```

---

## 環境建置與啟動

### 系統需求
- **Node.js**：18.x 或以上版本
- **npm**：9.x 或以上版本
- **記憶體**：建議 4GB 以上

### 安裝步驟

#### 1. 安裝依賴套件

```bash
npm install
```

#### 2. 啟動開發伺服器

```bash
ng serve
```

開發伺服器啟動後，開啟瀏覽器並訪問 `http://localhost:4200/`。應用程式會在您修改原始檔案時自動重新載入。

#### 3. 建置生產版本

```bash
ng build
```

建置產物會儲存在 `dist/` 目錄。生產建置會自動優化應用程式的效能與速度。

---

## 核心功能說明

### 認證機制
- **Session-based 認證**：使用 `withCredentials: true` 發送請求攜帶 Cookie
- **AuthService**：統一管理登入、註冊、登出邏輯
- **路由守衛**：保護需要登入的頁面（如個人資料、管理後台）

### 房型訂房流程
1. **瀏覽房型**：展示所有房型，顯示價格、容納人數、庫存數量、開放狀態
2. **查看詳情**：點擊房型卡片開啟詳細資訊 Modal
3. **選擇日期**：選擇入住/退房日期與入住人數
4. **驗證規則**：
   - 入住人數不可超過房型容納上限
   - 房型必須開放預訂（available = true）
   - 房型必須有剩餘庫存（stock > 0）
5. **提交訂房**：成功後房型庫存自動減 1
6. **取消訂房**：在「我的訂房」頁面取消，庫存自動恢復

### 產品購物流程
1. **瀏覽產品**：展示所有產品，顯示價格、庫存數量
2. **查看詳情**：點擊產品卡片開啟詳細資訊 Modal
3. **選擇數量**：選擇購買數量（最多不超過庫存）
4. **加入購物車**：將產品加入購物車
5. **購物車結帳**：填寫配送地址與電話
6. **庫存驗證**：結帳前後端會驗證所有商品庫存
7. **完成訂單**：成功後商品庫存自動扣減
8. **取消訂單**：在「我的訂單」頁面取消，庫存自動恢復

### 管理後台功能
- **多標籤設計**：使用者、訂房、訂單、產品、房型、評論、購物車、聯絡表單
- **分頁查詢**：所有列表支援分頁與關鍵字搜尋
- **即時編輯**：直接在表格中編輯資料
- **狀態管理**：快速更新訂房/訂單狀態
- **庫存管理**：
  - 房型：設定容納人數、庫存數量、開放狀態
  - 產品：設定庫存數量
  - 即時顯示庫存變化

---

## API 服務整合

所有服務類別統一透過 HttpClient 與後端 RESTful API 通訊，使用 `withCredentials: true` 攜帶 Session Cookie。

### 主要服務類別

#### AuthService
- `register()`：使用者註冊
- `login()`：使用者登入
- `logout()`：使用者登出
- `getCurrentUser()`：取得當前登入使用者資訊

#### RoomService
- `getAllRooms()`：取得所有房型
- `getRoomById()`：取得房型詳細資訊
- `createRoom()`：新增房型（管理員）
- `updateRoom()`：更新房型（管理員）
- `deleteRoom()`：刪除房型（管理員）

#### BookingService
- `createBooking()`：建立訂房（含入住人數）
- `getMyBookings()`：取得我的訂房記錄
- `cancelBooking()`：取消訂房
- `getAllBookings()`：取得所有訂房（管理員）
- `updateBookingStatus()`：更新訂房狀態（管理員）

#### ProductService
- `getAllProducts()`：取得所有產品
- `getProductById()`：取得產品詳細資訊
- `addToCart()`：加入購物車
- `createProduct()`：新增產品（管理員）
- `updateProduct()`：更新產品（管理員）
- `deleteProduct()`：刪除產品（管理員）

#### UserService
- `getUserProfile()`：取得個人資料
- `updateProfile()`：更新個人資料
- `getAllUsers()`：取得所有使用者（管理員）
- `updateUserRoles()`：更新使用者角色（管理員）

---

## 庫存管理功能

### 房型庫存顯示
- **列表頁**：每個房型卡片顯示「可容納人數」與「剩餘房間」
- **詳細頁**：顯示完整庫存資訊與開放狀態
- **訂房表單**：
  - 入住人數輸入框，預設值為 1
  - 自動驗證入住人數不超過容納上限
  - 房型關閉或庫存為 0 時禁用訂房按鈕
  - 顯示「暫不開放預訂」或「已無剩餘房間」提示

### 產品庫存顯示
- **列表頁**：每個產品卡片顯示「庫存數量」
- **詳細頁**：顯示庫存數量
- **加入購物車**：
  - 數量選擇器最大值限制為庫存數量
  - 庫存為 0 時禁用「加入購物車」按鈕
  - 顯示「此商品目前已售完」提示

### 管理後台庫存管理
- **房型管理標籤**：
  - 可設定「容納人數」、「庫存數量」、「是否開放」
  - 即時顯示庫存狀態（綠色徽章：開放，紅色徽章：關閉）
- **產品管理標籤**：
  - 可設定「庫存數量」
  - 新增產品時預設庫存為 0

---

## 樣式設計

### 全域樣式（styles.css）
- 統一配色方案（主色調：#28a745 綠色）
- 通用按鈕樣式（.btn、.btn-primary、.btn-danger 等）
- 表單元素樣式
- 響應式斷點設定

### 元件級樣式
每個元件擁有獨立的 CSS 檔案，實現樣式隔離：
- **房型卡片**：卡片佈局、hover 效果、庫存資訊樣式
- **產品卡片**：圖片展示、價格標籤、庫存標籤
- **管理後台**：表格樣式、分頁控制、標籤切換
- **表單樣式**：輸入框、驗證提示、禁用狀態

### 庫存相關樣式
- `.stock-info`：庫存資訊顯示區塊
- `.capacity-info`：容納人數資訊區塊
- `.unavailable-prompt`：無法預訂提示（紅色背景）
- `.badge-success` / `.badge-danger`：狀態徽章
- `button:disabled`：禁用按鈕樣式（灰色、不可點擊）

---

## 路由配置

```typescript
export const routes: Routes = [
  { path: '', component: HomeComponent },                    // 首頁
  { path: 'about', component: AboutComponent },              // 關於我們
  { path: 'contact', component: ContactComponent },          // 聯絡我們
  { path: 'qa', component: QaComponent },                    // 常見問題
  { path: 'news', component: NewsComponent },                // 最新消息
  { path: 'login', component: LoginComponent },              // 登入/註冊
  { path: 'profile', component: ProfileComponent },          // 個人資料（需登入）
  { path: 'room', component: RoomComponent },                // 房型瀏覽
  { path: 'product', component: ProductComponent },          // 產品商城
  { path: 'my-bookings', component: MyBookingsComponent },   // 我的訂房（需登入）
  { path: 'admin', component: AdminDashboardComponent },     // 管理後台（需管理員）
];
```
---

## 技術亮點

### 1. Standalone Components 架構
- 無需 NgModule，減少樣板程式碼
- 元件獨立性更高，易於維護與測試
- 支援 Tree Shaking，優化打包大小

### 2. 服務導向架構
- 所有 API 呼叫封裝在服務類別中
- 元件僅負責 UI 邏輯，業務邏輯在服務層
- 易於單元測試與模擬

### 3. 響應式表單與驗證
- 使用 FormsModule 實現雙向綁定
- 即時驗證使用者輸入
- 友善的錯誤提示訊息

### 4. 統一的錯誤處理
- 所有 HTTP 請求統一錯誤攔截
- 使用 alert() 或 console.error() 顯示錯誤
- 可擴展為全域 Toast 通知系統

### 5. 庫存即時顯示與驗證
- 前端即時顯示庫存資訊
- 前端預先驗證庫存充足性
- 後端二次驗證確保資料正確性
- 使用禁用狀態與提示文字引導使用者

---

## 相關資源

如需更多關於 Angular CLI 的資訊，包括詳細的指令參考，請訪問 [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) 頁面。

---

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 19.2.17.
