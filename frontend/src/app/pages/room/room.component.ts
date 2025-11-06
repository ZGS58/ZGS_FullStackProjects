import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RoomDto, RoomService } from '../../services/room.service';
import { BookingService } from '../../services/booking.service';
import { AuthService } from '../../services/auth.service';
import { Review, ReviewService } from '../../services/review.service';

@Component({
  selector: 'app-room',
  imports: [CommonModule, FormsModule],
  templateUrl: './room.component.html',
  styleUrl: './room.component.css'
})
export class RoomComponent implements OnInit {
  rooms: RoomDto[] = [];
  selectedRoom: RoomDto | null = null;
  reviews: Review[] = [];
  isLoggedIn = false;
  isAdmin = false;

  // 通知訊息
  notification = {
    show: false,
    message: '',
    type: 'success' // 'success' | 'error' | 'info'
  };

  bookingForm = {
    checkIn: '',
    checkOut: '',
    guestCount: 1
  };

  newReview = {
    rating: 5,
    comment: ''
  };

  constructor(
    private roomService: RoomService,
    private bookingService: BookingService,
    private authService: AuthService,
    private reviewService: ReviewService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadRooms();
    this.authService.isLoggedIn$.subscribe(loggedIn => {
      this.isLoggedIn = loggedIn;
    });

    // 訂閱角色變化來檢查管理員權限
    this.authService.userRoles$.subscribe(roles => {
      this.isAdmin = roles.includes('ROLE_ADMIN') || roles.includes('ADMIN');
      console.log('當前角色:', roles, '是否為管理員:', this.isAdmin);
    });
  }

  loadRooms(): void {
    this.roomService.getRooms().subscribe({
      next: (response: any) => {
        // 處理 BaseResponse 格式
        this.rooms = response.data || response;
      },
      error: (err) => {
        console.error('載入房間失敗', err);
      }
    });
  }

  viewRoomDetail(room: RoomDto): void {
    this.selectedRoom = room;
    this.loadReviews(room.id);

    // 設定預設入住日期為今天，退房日期為明天
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    this.bookingForm.checkIn = this.formatDate(today);
    this.bookingForm.checkOut = this.formatDate(tomorrow);
  }

  closeDetail(): void {
    this.selectedRoom = null;
    this.reviews = [];
    this.bookingForm = { checkIn: '', checkOut: '', guestCount: 1 };
    this.newReview = { rating: 5, comment: '' };
    this.hideNotification();
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  createBooking(): void {
    if (!this.selectedRoom || !this.bookingForm.checkIn || !this.bookingForm.checkOut) {
      alert('請選擇入住和退房日期');
      return;
    }

    // 如果有設定容納人數，檢查輸入的人數
    if (this.selectedRoom.capacity && this.bookingForm.guestCount > this.selectedRoom.capacity) {
      alert(`入住人數不能超過 ${this.selectedRoom.capacity} 人`);
      return;
    }

    this.bookingService.createBooking(
      this.selectedRoom.id,
      this.bookingForm.checkIn,
      this.bookingForm.checkOut,
      this.bookingForm.guestCount
    ).subscribe({
      next: (response) => {
        console.log('訂房回應:', response);
        // 處理不同的回應格式
        const totalPrice = response?.data?.totalPrice || response?.booking?.totalPrice || response?.totalPrice || '未知';

        // 顯示成功通知
        this.showNotification(`訂房成功！總金額：$${totalPrice}`, 'success');
        setTimeout(() => {
          if (confirm('是否要查看您的訂單？')) {
            this.router.navigate(['/my-bookings']);
          } else {
            this.closeDetail();
          }
        }, 1000);
      },
      error: (err) => {
        console.error('訂房失敗', err);
        const errorMessage = err.error?.message || '訂房失敗，請稍後再試';
        this.showNotification('訂房失敗：' + errorMessage, 'error');
      }
    });
  }

  loadReviews(roomId: number): void {
    this.reviewService.getRoomReviews(roomId).subscribe({
      next: (response) => {
        this.reviews = response.data || [];
      },
      error: (err) => {
        console.error('載入評論失敗', err);
      }
    });
  }

  submitReview(): void {
    if (!this.selectedRoom || !this.newReview.comment.trim()) {
      alert('請輸入評論內容');
      return;
    }

    this.reviewService.createRoomReview(
      this.selectedRoom.id,
      this.newReview.rating,
      this.newReview.comment
    ).subscribe({
      next: () => {
        this.showNotification('評論發布成功！', 'success');
        this.newReview = { rating: 5, comment: '' };
        this.loadReviews(this.selectedRoom!.id);
      },
      error: (err) => {
        console.error('送出評論失敗', err);
        this.showNotification('送出評論失敗：' + (err.error?.message || '請稍後再試'), 'error');
      }
    });
  }

  // 管理員刪除評論
  deleteReview(reviewId: number): void {
    console.log('嘗試刪除評論，ID:', reviewId, '是否為管理員:', this.isAdmin);

    if (confirm('確定要刪除這則評論嗎？')) {
      this.reviewService.deleteReview(reviewId).subscribe({
        next: () => {
          this.showNotification('評論已刪除', 'success');
          if (this.selectedRoom) {
            this.loadReviews(this.selectedRoom.id);
          }
        },
        error: (err) => {
          console.error('刪除評論失敗', err);
          this.showNotification('刪除評論失敗：' + (err.error?.message || '請稍後再試'), 'error');
        }
      });
    }
  }

  // 檢查是否可以刪除評論（管理員或評論發布者）
  canDeleteReview(review: Review): boolean {
    if (this.isAdmin) {
      return true; // 管理員可以刪除任何評論
    }
    const currentUsername = this.authService.getCurrentUsername();
    return review.username === currentUsername; // 評論發布者可以刪除自己的評論
  }

  // 顯示通知
  showNotification(message: string, type: 'success' | 'error' | 'info' = 'success'): void {
    console.log('顯示通知:', message, type);
    this.notification = { show: true, message, type };
    setTimeout(() => this.hideNotification(), 5000);
  }

  // 隱藏通知
  hideNotification(): void {
    console.log('隱藏通知');
    this.notification.show = false;
  }
}
