import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface BookingDto {
  id?: number;
  checkIn: string;
  checkOut: string;
  totalPrice?: number;
  status?: string;
  guestCount?: number;
  userId?: number;
  roomId?: number;
  roomName?: string;
  username?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface BaseResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private apiUrl = 'http://localhost:8080/api/bookings';

  constructor(private http: HttpClient) { }

  getMyBookings(): Observable<BookingDto[]> {
    return this.http
      .get<BaseResponse<BookingDto[]>>(`${this.apiUrl}/my`, { withCredentials: true })
      .pipe(map(res => res.data || []));
  }

  createBooking(roomId: number, checkIn: string, checkOut: string, guestCount?: number): Observable<any> {
    const body: any = { checkIn, checkOut };
    if (guestCount !== undefined) {
      body.guestCount = guestCount;
    }
    return this.http.post(`${this.apiUrl}/room/${roomId}`, body, { withCredentials: true });
  }

  cancelBooking(bookingId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${bookingId}/cancel`, {}, {
      withCredentials: true
    });
  }

  // 管理員 API
  getAllBookings(page: number = 0, size: number = 10, keyword?: string): Observable<BaseResponse<PageResponse<BookingDto>>> {
    const q = keyword && keyword.trim().length > 0 ? `&keyword=${encodeURIComponent(keyword.trim())}` : '';
    return this.http.get<BaseResponse<PageResponse<BookingDto>>>(
      `${this.apiUrl}/admin/all?page=${page}&size=${size}${q}`,
      { withCredentials: true }
    );
  }

  updateBookingStatus(bookingId: number, status: string): Observable<BaseResponse<BookingDto>> {
    return this.http.put<BaseResponse<BookingDto>>(
      `${this.apiUrl}/admin/${bookingId}/status`,
      { status },
      { withCredentials: true }
    );
  }

  deleteBooking(bookingId: number): Observable<BaseResponse<void>> {
    return this.http.delete<BaseResponse<void>>(
      `${this.apiUrl}/admin/${bookingId}`,
      { withCredentials: true }
    );
  }
}
