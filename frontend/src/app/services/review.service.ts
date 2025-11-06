import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Review {
  id?: number;
  rating: number;
  comment: string;
  username?: string;
  userId?: number;
  productId?: number;
  roomId?: number;
  createdAt?: string;
}

export interface BaseResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) { }

  // 獲取商品評論
  getProductReviews(productId: number): Observable<BaseResponse<Review[]>> {
    return this.http.get<BaseResponse<Review[]>>(`${this.apiUrl}/product/${productId}`, {
      withCredentials: true
    });
  }

  // 獲取房間評論
  getRoomReviews(roomId: number): Observable<BaseResponse<Review[]>> {
    return this.http.get<BaseResponse<Review[]>>(`${this.apiUrl}/room/${roomId}`, {
      withCredentials: true
    });
  }

  // 新增商品評論
  createProductReview(productId: number, rating: number, comment: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/product/${productId}`,
      { rating, comment },
      { withCredentials: true }
    );
  }

  // 新增房間評論
  createRoomReview(roomId: number, rating: number, comment: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/room/${roomId}`,
      { rating, comment },
      { withCredentials: true }
    );
  }

  // 刪除評論
  deleteReview(reviewId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${reviewId}`, {
      withCredentials: true
    });
  }
}
