import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService, CartDto, CartItemDto } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-cart',
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent implements OnInit {
  cart: CartDto | null = null;
  loading = false;
  checkingOut = false;
  notificationMessage = '';
  notificationType: 'success' | 'error' | 'info' = 'info';
  showNotificationDiv = false;

  // 結帳表單
  shippingAddress = '';
  phoneNumber = '';
  note = '';

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private orderService: OrderService,
    private router: Router
  ) { }

  ngOnInit() {
    this.loadCart();
  }

  loadCart() {
    this.loading = true;
    this.cartService.getCart().subscribe({
      next: (response: any) => {
        console.log('購物車 API 響應:', response);
        if (response.success) {
          this.cart = response.data;
          console.log('購物車數據:', this.cart);
          console.log('購物車商品數量:', this.cart?.items?.length);
        } else {
          console.log('購物車 API 返回錯誤狀態:', response);
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('載入購物車失敗:', error);
        this.showNotification('載入購物車失敗', 'error');
        this.loading = false;
      }
    });
  }

  updateQuantity(item: CartItemDto, newQuantity: number) {
    if (newQuantity < 1) {
      this.showNotification('數量不能小於 1', 'error');
      return;
    }

    if (newQuantity > 99) {
      this.showNotification('數量不能超過 99', 'error');
      return;
    }

    this.cartService.updateCartItem(item.id, newQuantity).subscribe({
      next: (response: any) => {
        if (response.success) {
          this.cart = response.data;
          this.showNotification('已更新數量', 'success');
        }
      },
      error: (error) => {
        console.error('更新失敗:', error);
        this.showNotification('更新失敗', 'error');
      }
    });
  }

  removeItem(item: CartItemDto) {
    if (!confirm(`確定要移除 ${item.productName} 嗎？`)) {
      return;
    }

    this.cartService.removeFromCart(item.id).subscribe({
      next: (response: any) => {
        if (response.success) {
          this.cart = response.data;
          this.showNotification('已移除商品', 'success');
        }
      },
      error: (error) => {
        console.error('移除失敗:', error);
        this.showNotification('移除失敗', 'error');
      }
    });
  }

  clearCart() {
    if (!confirm('確定要清空購物車嗎？')) {
      return;
    }

    this.cartService.clearCart().subscribe({
      next: (response: any) => {
        if (response.success) {
          this.loadCart();
          this.showNotification('購物車已清空', 'success');
        }
      },
      error: (error) => {
        console.error('清空失敗:', error);
        this.showNotification('清空失敗', 'error');
      }
    });
  }

  checkout() {
    if (!this.cart || this.cart.items.length === 0) {
      this.showNotification('購物車是空的', 'error');
      return;
    }

    // 驗證必填欄位
    if (!this.shippingAddress.trim()) {
      this.showNotification('請填寫配送地址', 'error');
      return;
    }
    if (!this.phoneNumber.trim()) {
      this.showNotification('請填寫聯絡電話', 'error');
      return;
    }

    this.checkingOut = true;
    this.orderService.checkout(this.shippingAddress, this.phoneNumber, this.note).subscribe({
      next: (response: any) => {
        if (response.success) {
          this.showNotification('結帳成功！', 'success');
          // 清空表單
          this.shippingAddress = '';
          this.phoneNumber = '';
          this.note = '';
          // 重新載入購物車
          this.loadCart();
          // 保持在本頁，不再自動導向「我的訂房」
        } else {
          this.showNotification(response.message || '結帳失敗', 'error');
        }
        this.checkingOut = false;
      },
      error: (error: any) => {
        this.showNotification('結帳失敗', 'error');
        this.checkingOut = false;
      }
    });
  }

  continueShopping() {
    this.router.navigate(['/product']);
  }

  showNotification(message: string, type: 'success' | 'error' | 'info') {
    this.notificationMessage = message;
    this.notificationType = type;
    this.showNotificationDiv = true;

    setTimeout(() => {
      this.showNotificationDiv = false;
    }, 3000);
  }
}
