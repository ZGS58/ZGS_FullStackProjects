import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService, BookingDto, PageResponse } from '../../../services/booking.service';

@Component({
  selector: 'app-booking-management',
  imports: [CommonModule, FormsModule],
  templateUrl: './booking-management.component.html',
  styleUrl: './booking-management.component.css'
})
export class BookingManagementComponent implements OnInit {
  bookings: BookingDto[] = [];
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  editingBooking: BookingDto | null = null;
  editStatus = '';

  message = '';
  errorMessage = '';

  statusOptions = [
    { value: 'PENDING', label: '待確認' },
    { value: 'CONFIRMED', label: '已確認' },
    { value: 'CANCELLED', label: '已取消' },
    { value: 'COMPLETED', label: '已完成' }
  ];

  constructor(private bookingService: BookingService) { }

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.bookingService.getAllBookings(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.bookings = response.data.content;
          this.totalPages = response.data.totalPages;
          this.totalElements = response.data.totalElements;
          this.currentPage = response.data.number;
        }
      },
      error: (error) => {
        this.errorMessage = '載入訂房列表失敗';
        console.error('Error loading bookings:', error);
      }
    });
  }

  startEdit(booking: BookingDto): void {
    this.editingBooking = booking;
    this.editStatus = booking.status || 'PENDING';
    this.clearMessages();
  }

  cancelEdit(): void {
    this.editingBooking = null;
    this.editStatus = '';
    this.clearMessages();
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

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadBookings();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadBookings();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadBookings();
    }
  }

  clearMessages(): void {
    this.message = '';
    this.errorMessage = '';
  }
}
