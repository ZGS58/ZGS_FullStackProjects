import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingDto, BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-my-bookings',
  imports: [CommonModule],
  templateUrl: './my-bookings.component.html',
  styleUrl: './my-bookings.component.css'
})
export class MyBookingsComponent implements OnInit {
  bookings: BookingDto[] = [];
  loading = false;

  constructor(private bookingService: BookingService) { }

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.loading = true;
    this.bookingService.getMyBookings().subscribe({
      next: (bookings) => {
        this.bookings = bookings;
        this.loading = false;
      },
      error: (err) => {
        console.error('載入訂房記錄失敗', err);
        this.loading = false;
        alert('載入訂房記錄失敗，請稍後再試');
      }
    });
  }

  cancelBooking(bookingId: number | undefined): void {
    if (!bookingId) return;

    if (confirm('確定要取消此訂房嗎？')) {
      this.bookingService.cancelBooking(bookingId).subscribe({
        next: () => {
          alert('訂房已取消');
          this.loadBookings();
        },
        error: (err) => {
          console.error('取消訂房失敗', err);
          alert(err.error?.message || '取消訂房失敗');
        }
      });
    }
  }

  getStatusClass(status: string | undefined): string {
    switch (status) {
      case 'CONFIRMED': return 'status-confirmed';
      case 'CANCELLED': return 'status-cancelled';
      case 'COMPLETED': return 'status-completed';
      default: return '';
    }
  }

  getStatusText(status: string | undefined): string {
    switch (status) {
      case 'CONFIRMED': return '已確認';
      case 'CANCELLED': return '已取消';
      case 'COMPLETED': return '已完成';
      default: return status || '未知';
    }
  }
}
