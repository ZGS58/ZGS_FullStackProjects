import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, User, UpdateUserRequest, PageResponse } from '../../../services/user.service';
import { BookingService, BookingDto } from '../../../services/booking.service';
import { Product, ProductService } from '../../../services/product.service';
import { Room, RoomService } from '../../../services/room.service';
import { Order, OrderService } from '../../../services/order.service';
import { ContactService, Contact } from '../../../services/contact.service';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  activeTab: 'users' | 'bookings' | 'products' | 'rooms' | 'orders' | 'contacts' = 'users';

  // 用戶管理相關
  users: User[] = [];
  usersCurrentPage = 0;
  usersPageSize = 10;
  usersTotalPages = 0;
  usersTotalElements = 0;
  usersKeyword = '';
  editingUser: User | null = null;
  editUserForm = { email: '', fullname: '', selectedRoles: [] as string[] };
  roleOptions = ['USER', 'ADMIN'];

  // 訂房管理相關
  bookings: BookingDto[] = [];
  bookingsCurrentPage = 0;
  bookingsPageSize = 10;
  bookingsTotalPages = 0;
  bookingsTotalElements = 0;
  bookingsKeyword = '';
  editingBooking: BookingDto | null = null;
  editStatus = '';

  // 商品管理相關
  products: Product[] = [];
  productsCurrentPage = 0;
  productsPageSize = 10;
  productsTotalPages = 0;
  productsTotalElements = 0;
  editingProduct: Product | null = null;
  newProduct: Product = { name: '', description: '', price: 0, imageUrl: '', stock: 0 };
  isAddingProduct = false;

  // 房型管理相關
  rooms: Room[] = [];
  roomsCurrentPage = 0;
  roomsPageSize = 10;
  roomsTotalPages = 0;
  roomsTotalElements = 0;
  editingRoom: Room | null = null;
  newRoom: Room = { name: '', type: '', description: '', price: 0, capacity: 1, available: true, stock: 0 };
  isAddingRoom = false;

  // 訂單管理相關
  orders: Order[] = [];
  ordersCurrentPage = 0;
  ordersPageSize = 10;
  ordersTotalPages = 0;
  ordersTotalElements = 0;
  ordersKeyword = '';
  editingOrder: Order | null = null;
  editOrderStatus = '';

  // 聯絡表單管理相關
  contacts: Contact[] = [];
  contactsCurrentPage = 0;
  contactsPageSize = 10;
  contactsTotalPages = 0;
  contactsTotalElements = 0;

  message = '';
  errorMessage = '';

  statusOptions = [
    { value: 'PENDING', label: '待確認' },
    { value: 'CONFIRMED', label: '已確認' },
    { value: 'CANCELLED', label: '已取消' },
    { value: 'COMPLETED', label: '已完成' }
  ];

  orderStatusOptions = [
    { value: 'PENDING', label: '待處理' },
    { value: 'CONFIRMED', label: '已確認' },
    { value: 'PROCESSING', label: '處理中' },
    { value: 'SHIPPED', label: '已出貨' },
    { value: 'DELIVERED', label: '已送達' },
    { value: 'CANCELLED', label: '已取消' }
  ];

  constructor(
    private userService: UserService,
    private bookingService: BookingService,
    private productService: ProductService,
    private roomService: RoomService,
    private orderService: OrderService,
    private contactService: ContactService
  ) { }

  ngOnInit(): void {
    this.loadUsers();
    this.loadBookings();
    this.loadProducts();
    this.loadRooms();
    this.loadOrders();
    this.loadContacts();
  }

  switchTab(tab: 'users' | 'bookings' | 'products' | 'rooms' | 'orders' | 'contacts'): void {
    this.activeTab = tab;
    this.clearMessages();
    this.cancelEdit();
    this.cancelBookingEdit();
    this.cancelProductEdit();
    this.cancelRoomEdit();
    this.cancelOrderEdit();
  }

  //  用戶管理相關方法
  loadUsers(): void {
    this.userService.getPagedUsers(this.usersCurrentPage, this.usersPageSize, this.usersKeyword).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.users = response.data.content;
          this.usersTotalPages = response.data.totalPages;
          this.usersTotalElements = response.data.totalElements;
          this.usersCurrentPage = response.data.number;
        }
      },
      error: (error) => {
        this.errorMessage = '載入用戶列表失敗';
        console.error('Error loading users:', error);
      }
    });
  }

  searchUsers(): void {
    this.usersCurrentPage = 0;
    this.loadUsers();
  }

  startEdit(user: User): void {
    this.editingUser = user;
    this.editUserForm.email = user.email;
    this.editUserForm.fullname = user.fullname;
    this.editUserForm.selectedRoles = user.roleNames ? [...user.roleNames] : ['USER'];
    this.clearMessages();
  }

  cancelEdit(): void {
    this.editingUser = null;
    this.editUserForm = { email: '', fullname: '', selectedRoles: [] };
  }

  toggleRole(role: string): void {
    const index = this.editUserForm.selectedRoles.indexOf(role);
    if (index > -1) {
      this.editUserForm.selectedRoles.splice(index, 1);
    } else {
      this.editUserForm.selectedRoles.push(role);
    }
  }

  isRoleSelected(role: string): boolean {
    return this.editUserForm.selectedRoles.includes(role);
  }

  updateUser(): void {
    if (!this.editingUser) return;

    this.clearMessages();

    const updateData: UpdateUserRequest = {
      email: this.editUserForm.email,
      fullname: this.editUserForm.fullname,
      roleNames: this.editUserForm.selectedRoles
    };

    this.userService.updateUser(this.editingUser.id, updateData).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.message = '用戶資料更新成功';
          this.editingUser = null;
          this.loadUsers();
        }
      },
      error: (error) => {
        this.errorMessage = '更新失敗：' + (error.error?.message || '請稍後再試');
        console.error('Error updating user:', error);
      }
    });
  }

  deleteUser(user: User): void {
    if (!confirm(`確定要刪除用戶 "${user.username}" 嗎？此操作無法撤銷。`)) {
      return;
    }

    this.clearMessages();

    this.userService.deleteUser(user.id).subscribe({
      next: (response) => {
        if (response.success) {
          this.message = '用戶刪除成功';
          this.loadUsers();
        }
      },
      error: (error) => {
        this.errorMessage = '刪除失敗：' + (error.error?.message || '請稍後再試');
        console.error('Error deleting user:', error);
      }
    });
  }

  usersGoToPage(page: number): void {
    if (page >= 0 && page < this.usersTotalPages) {
      this.usersCurrentPage = page;
      this.loadUsers();
    }
  }

  usersPreviousPage(): void {
    if (this.usersCurrentPage > 0) {
      this.usersCurrentPage--;
      this.loadUsers();
    }
  }

  usersNextPage(): void {
    if (this.usersCurrentPage < this.usersTotalPages - 1) {
      this.usersCurrentPage++;
      this.loadUsers();
    }
  }

  //  訂房管理相關方法
  loadBookings(): void {
    this.bookingService.getAllBookings(this.bookingsCurrentPage, this.bookingsPageSize, this.bookingsKeyword).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.bookings = response.data.content;
          this.bookingsTotalPages = response.data.totalPages;
          this.bookingsTotalElements = response.data.totalElements;
          this.bookingsCurrentPage = response.data.number;
        }
      },
      error: (error) => {
        this.errorMessage = '載入訂房列表失敗';
        console.error('Error loading bookings:', error);
      }
    });
  }

  searchBookings(): void {
    this.bookingsCurrentPage = 0;
    this.loadBookings();
  }

  startBookingEdit(booking: BookingDto): void {
    this.editingBooking = booking;
    this.editStatus = booking.status || 'PENDING';
    this.clearMessages();
  }

  cancelBookingEdit(): void {
    this.editingBooking = null;
    this.editStatus = '';
  }

  updateBookingStatus(): void {
    if (!this.editingBooking || !this.editingBooking.id) return;

    this.clearMessages();

    this.bookingService.updateBookingStatus(this.editingBooking.id, this.editStatus).subscribe({
      next: (response) => {
        if (response.success) {
          this.message = '訂房狀態更新成功';
          this.editingBooking = null;
          this.loadBookings();
        }
      },
      error: (error) => {
        this.errorMessage = '更新失敗：' + (error.error?.message || '請稍後再試');
        console.error('Error updating booking:', error);
      }
    });
  }

  deleteBooking(booking: BookingDto): void {
    if (!booking.id) return;

    if (!confirm(`確定要刪除 ${booking.username} 的訂房記錄嗎？此操作無法撤銷。`)) {
      return;
    }

    this.clearMessages();

    this.bookingService.deleteBooking(booking.id).subscribe({
      next: (response) => {
        if (response.success) {
          this.message = '訂房記錄刪除成功';
          this.loadBookings();
        }
      },
      error: (error) => {
        this.errorMessage = '刪除失敗：' + (error.error?.message || '請稍後再試');
        console.error('Error deleting booking:', error);
      }
    });
  }

  getStatusLabel(status: string): string {
    const option = this.statusOptions.find(o => o.value === status);
    return option ? option.label : status;
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'CONFIRMED': return 'status-confirmed';
      case 'PENDING': return 'status-pending';
      case 'CANCELLED': return 'status-cancelled';
      case 'COMPLETED': return 'status-completed';
      default: return '';
    }
  }

  bookingsGoToPage(page: number): void {
    if (page >= 0 && page < this.bookingsTotalPages) {
      this.bookingsCurrentPage = page;
      this.loadBookings();
    }
  }

  bookingsPreviousPage(): void {
    if (this.bookingsCurrentPage > 0) {
      this.bookingsCurrentPage--;
      this.loadBookings();
    }
  }

  bookingsNextPage(): void {
    if (this.bookingsCurrentPage < this.bookingsTotalPages - 1) {
      this.bookingsCurrentPage++;
      this.loadBookings();
    }
  }

  //  商品管理相關方法
  loadProducts(): void {
    this.productService.getPaged(this.productsCurrentPage, this.productsPageSize).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.products = response.data.content;
          this.productsTotalPages = response.data.totalPages;
          this.productsTotalElements = response.data.totalElements;
          this.productsCurrentPage = response.data.number;
        }
      },
      error: (error) => {
        this.errorMessage = '載入商品列表失敗';
        console.error('Error loading products:', error);
      }
    });
  }

  startProductEdit(product: Product): void {
    this.editingProduct = { ...product };
  }

  cancelProductEdit(): void {
    this.editingProduct = null;
    this.isAddingProduct = false;
    this.newProduct = { name: '', description: '', price: 0, imageUrl: '', stock: 0 };
  }

  updateProduct(): void {
    if (!this.editingProduct || !this.editingProduct.id) return;

    this.productService.update(this.editingProduct.id, this.editingProduct).subscribe({
      next: (response: any) => {
        this.message = '商品更新成功';
        this.loadProducts();
        this.cancelProductEdit();
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.errorMessage = '更新商品失敗';
        console.error('Error updating product:', error);
      }
    });
  }

  deleteProduct(product: Product): void {
    if (!product.id || !confirm(`確定要刪除商品「${product.name}」嗎？`)) return;

    this.productService.delete(product.id).subscribe({
      next: () => {
        this.message = '商品刪除成功';
        this.loadProducts();
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.errorMessage = '刪除商品失敗';
        console.error('Error deleting product:', error);
      }
    });
  }

  startAddProduct(): void {
    this.isAddingProduct = true;
    this.newProduct = { name: '', description: '', price: 0, imageUrl: '' };
  }

  createProduct(): void {
    if (!this.newProduct.name || !this.newProduct.description || this.newProduct.price <= 0) {
      this.errorMessage = '請填寫完整的商品資訊';
      return;
    }

    this.productService.create(this.newProduct).subscribe({
      next: (response: any) => {
        this.message = '商品新增成功';
        this.loadProducts();
        this.cancelProductEdit();
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.errorMessage = '新增商品失敗';
        console.error('Error creating product:', error);
      }
    });
  }

  //  房型管理相關方法
  loadRooms(): void {
    this.roomService.getPaged(this.roomsCurrentPage, this.roomsPageSize).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.rooms = response.data.content;
          this.roomsTotalPages = response.data.totalPages;
          this.roomsTotalElements = response.data.totalElements;
          this.roomsCurrentPage = response.data.number;
        }
      },
      error: (error) => {
        this.errorMessage = '載入房型列表失敗';
        console.error('Error loading rooms:', error);
      }
    });
  }

  productsGoToPage(page: number): void {
    if (page >= 0 && page < this.productsTotalPages) {
      this.productsCurrentPage = page;
      this.loadProducts();
    }
  }

  productsPreviousPage(): void {
    if (this.productsCurrentPage > 0) {
      this.productsCurrentPage--;
      this.loadProducts();
    }
  }

  productsNextPage(): void {
    if (this.productsCurrentPage < this.productsTotalPages - 1) {
      this.productsCurrentPage++;
      this.loadProducts();
    }
  }

  roomsGoToPage(page: number): void {
    if (page >= 0 && page < this.roomsTotalPages) {
      this.roomsCurrentPage = page;
      this.loadRooms();
    }
  }

  roomsPreviousPage(): void {
    if (this.roomsCurrentPage > 0) {
      this.roomsCurrentPage--;
      this.loadRooms();
    }
  }

  roomsNextPage(): void {
    if (this.roomsCurrentPage < this.roomsTotalPages - 1) {
      this.roomsCurrentPage++;
      this.loadRooms();
    }
  }

  startRoomEdit(room: Room): void {
    this.editingRoom = { ...room };
  }

  cancelRoomEdit(): void {
    this.editingRoom = null;
    this.isAddingRoom = false;
    this.newRoom = { name: '', type: '', description: '', price: 0, capacity: 1, available: true, stock: 0 };
  }

  updateRoom(): void {
    if (!this.editingRoom || !this.editingRoom.id) return;

    this.roomService.update(this.editingRoom.id, this.editingRoom).subscribe({
      next: (response: any) => {
        this.message = '房型更新成功';
        this.loadRooms();
        this.cancelRoomEdit();
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.errorMessage = '更新房型失敗';
        console.error('Error updating room:', error);
      }
    });
  }

  deleteRoom(room: Room): void {
    if (!room.id || !confirm(`確定要刪除房型「${room.name}」嗎？`)) return;

    this.roomService.delete(room.id).subscribe({
      next: () => {
        this.message = '房型刪除成功';
        this.loadRooms();
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.errorMessage = '刪除房型失敗';
        console.error('Error deleting room:', error);
      }
    });
  }

  startAddRoom(): void {
    this.isAddingRoom = true;
    this.newRoom = { name: '', type: '', description: '', price: 0, imageUrl: '' };
  }

  createRoom(): void {
    if (!this.newRoom.name || !this.newRoom.type || !this.newRoom.description || this.newRoom.price <= 0) {
      this.errorMessage = '請填寫完整的房型資訊';
      return;
    }

    this.roomService.create(this.newRoom).subscribe({
      next: (response: any) => {
        this.message = '房型新增成功';
        this.loadRooms();
        this.cancelRoomEdit();
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.errorMessage = '新增房型失敗';
        console.error('Error creating room:', error);
      }
    });
  }

  // ===== 訂單管理相關方法 =====
  loadOrders(): void {
    this.orderService.getAllOrders(this.ordersCurrentPage, this.ordersPageSize, this.ordersKeyword).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.orders = response.data.content;
          this.ordersTotalPages = response.data.totalPages;
          this.ordersTotalElements = response.data.totalElements;
          this.ordersCurrentPage = response.data.number;
        }
      },
      error: (error) => {
        this.errorMessage = '載入訂單列表失敗';
        console.error('Error loading orders:', error);
      }
    });
  }

  searchOrders(): void {
    this.ordersCurrentPage = 0;
    this.loadOrders();
  }

  startOrderEdit(order: Order): void {
    this.editingOrder = order;
    this.editOrderStatus = order.status || 'PENDING';
    this.clearMessages();
  }

  cancelOrderEdit(): void {
    this.editingOrder = null;
    this.editOrderStatus = '';
  }

  updateOrderStatus(): void {
    if (!this.editingOrder || !this.editingOrder.id) return;

    this.clearMessages();

    this.orderService.updateOrderStatus(this.editingOrder.id, this.editOrderStatus).subscribe({
      next: (response) => {
        if (response.success) {
          this.message = '訂單狀態更新成功';
          this.editingOrder = null;
          this.loadOrders();
        }
      },
      error: (error) => {
        this.errorMessage = '更新失敗：' + (error.error?.message || '請稍後再試');
        console.error('Error updating order:', error);
      }
    });
  }

  deleteOrder(order: Order): void {
    if (!order.id) return;

    if (!confirm(`確定要刪除訂單 #${order.id} 嗎？此操作無法撤銷。`)) {
      return;
    }

    this.clearMessages();

    this.orderService.deleteOrder(order.id).subscribe({
      next: (response) => {
        if (response.success) {
          this.message = '訂單刪除成功';
          this.loadOrders();
        }
      },
      error: (error) => {
        this.errorMessage = '刪除失敗：' + (error.error?.message || '請稍後再試');
        console.error('Error deleting order:', error);
      }
    });
  }

  getOrderStatusLabel(status: string): string {
    const option = this.orderStatusOptions.find(o => o.value === status);
    return option ? option.label : status;
  }

  getOrderStatusClass(status: string): string {
    switch (status) {
      case 'PENDING': return 'status-pending';
      case 'CONFIRMED': return 'status-confirmed';
      case 'PROCESSING': return 'status-processing';
      case 'SHIPPED': return 'status-shipped';
      case 'DELIVERED': return 'status-delivered';
      case 'CANCELLED': return 'status-cancelled';
      default: return '';
    }
  }

  ordersGoToPage(page: number): void {
    if (page >= 0 && page < this.ordersTotalPages) {
      this.ordersCurrentPage = page;
      this.loadOrders();
    }
  }

  ordersPreviousPage(): void {
    if (this.ordersCurrentPage > 0) {
      this.ordersCurrentPage--;
      this.loadOrders();
    }
  }

  ordersNextPage(): void {
    if (this.ordersCurrentPage < this.ordersTotalPages - 1) {
      this.ordersCurrentPage++;
      this.loadOrders();
    }
  }

  // 聯絡表單管理方法
  loadContacts(): void {
    this.contactService.getPaged(this.contactsCurrentPage, this.contactsPageSize).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.contacts = response.data.content;
          this.contactsTotalPages = response.data.totalPages;
          this.contactsTotalElements = response.data.totalElements;
          this.contactsCurrentPage = response.data.number;
        }
      },
      error: (error) => {
        this.errorMessage = '載入聯絡表單失敗';
        console.error('Error loading contacts:', error);
      }
    });
  }

  deleteContact(contact: Contact): void {
    if (!confirm(`確定要刪除來自 ${contact.name} 的聯絡表單嗎？`)) {
      return;
    }

    if (!contact.id) {
      this.errorMessage = '無法刪除：缺少聯絡表單 ID';
      return;
    }

    this.contactService.delete(contact.id).subscribe({
      next: (response) => {
        if (response.success) {
          this.message = '聯絡表單已刪除';
          this.loadContacts();
        } else {
          this.errorMessage = response.message || '刪除失敗';
        }
      },
      error: (error) => {
        this.errorMessage = '刪除聯絡表單失敗';
        console.error('Error deleting contact:', error);
      }
    });
  }

  contactsPreviousPage(): void {
    if (this.contactsCurrentPage > 0) {
      this.contactsCurrentPage--;
      this.loadContacts();
    }
  }

  contactsNextPage(): void {
    if (this.contactsCurrentPage < this.contactsTotalPages - 1) {
      this.contactsCurrentPage++;
      this.loadContacts();
    }
  }

  clearMessages(): void {
    this.message = '';
    this.errorMessage = '';
  }
}
