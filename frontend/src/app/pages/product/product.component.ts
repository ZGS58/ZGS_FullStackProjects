import { Component, OnInit } from '@angular/core';
import { Product, ProductService } from '../../services/product.service';
import { Review, ReviewService } from '../../services/review.service';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product',
  imports: [CommonModule, FormsModule],
  templateUrl: './product.component.html',
  styleUrl: './product.component.css'
})
export class ProductComponent implements OnInit {
  productList: Product[] = [];
  selectedProduct: Product | null = null;
  reviews: Review[] = [];
  newReview = {
    rating: 5,
    comment: ''
  };
  isLoggedIn = false;
  addToCartQuantity = 1;
  notificationMessage = '';
  notificationType: 'success' | 'error' | 'info' = 'info';
  showNotificationDiv = false;

  constructor(
    private productService: ProductService,
    private reviewService: ReviewService,
    private authService: AuthService,
    private cartService: CartService
  ) { }

  ngOnInit(): void {
    this.loadProduct();
    this.authService.isLoggedIn$.subscribe(loggedIn => {
      this.isLoggedIn = loggedIn;
    });
  }

  loadProduct(): void {
    this.productService.getAll().subscribe({
      next: (res: any) => {
        this.productList = res.data;
      },
      error(err) {
        console.error(err);
      },
    });
  }

  viewProductDetail(product: Product): void {
    this.selectedProduct = product;
    this.addToCartQuantity = 1;
    if (product.id) {
      this.loadReviews(product.id);
    }
  }

  closeDetail(): void {
    this.selectedProduct = null;
    this.reviews = [];
    this.newReview = { rating: 5, comment: '' };
    this.addToCartQuantity = 1;
  }

  loadReviews(productId: number): void {
    this.reviewService.getProductReviews(productId).subscribe({
      next: (res) => {
        this.reviews = res?.data ?? [];
      },
      error: (err) => {
        console.error('載入評論失敗', err);
      }
    });
  }

  submitReview(): void {
    if (!this.selectedProduct || !this.selectedProduct.id || !this.newReview.comment.trim()) {
      return;
    }

    this.reviewService.createProductReview(
      this.selectedProduct.id,
      this.newReview.rating,
      this.newReview.comment
    ).subscribe({
      next: () => {
        this.newReview = { rating: 5, comment: '' };
        if (this.selectedProduct?.id) {
          this.loadReviews(this.selectedProduct.id);
        }
        this.showNotification('評論已送出！', 'success');
      },
      error: (err) => {
        console.error('送出評論失敗', err);
        this.showNotification('送出評論失敗，請稍後再試', 'error');
      }
    });
  }

  deleteReview(reviewId: number): void {
    if (confirm('確定要刪除此評論嗎？')) {
      this.reviewService.deleteReview(reviewId).subscribe({
        next: () => {
          if (this.selectedProduct?.id) {
            this.loadReviews(this.selectedProduct.id);
          }
          this.showNotification('評論已刪除', 'success');
        },
        error: (err) => {
          console.error('刪除評論失敗', err);
          this.showNotification('刪除評論失敗', 'error');
        }
      });
    }
  }

  addToCart(): void {
    if (!this.isLoggedIn) {
      this.showNotification('請先登入', 'error');
      return;
    }

    if (!this.selectedProduct || !this.selectedProduct.id) {
      return;
    }

    if (this.addToCartQuantity < 1 || this.addToCartQuantity > 99) {
      this.showNotification('數量必須在 1-99 之間', 'error');
      return;
    }

    this.cartService.addToCart(this.selectedProduct.id, this.addToCartQuantity).subscribe({
      next: (response: any) => {
        console.log('加入購物車 API 響應:', response);
        if (response.success) {
          console.log('購物車數據:', response.data);
          this.showNotification(`已加入 ${this.addToCartQuantity} 件到購物車！`, 'success');
          this.addToCartQuantity = 1;
        } else {
          console.log('加入購物車返回錯誤狀態:', response);
          this.showNotification(response.message || '加入購物車失敗', 'error');
        }
      },
      error: (err) => {
        console.error('加入購物車失敗', err);
        this.showNotification('加入購物車失敗', 'error');
      }
    });
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
