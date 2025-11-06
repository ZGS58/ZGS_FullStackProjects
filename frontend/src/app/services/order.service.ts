import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OrderItem {
    id?: number;
    productId: number;
    productName: string;
    productPrice: number;
    quantity: number;
    subtotal: number;
}

export interface Order {
    id?: number;
    userId: number;
    username: string;
    items: OrderItem[];
    totalPrice: number;
    totalItems: number;
    status: 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
    shippingAddress: string;
    phoneNumber: string;
    note?: string;
    createdAt?: string;
    updatedAt?: string;
}

export interface BaseResponse<T> {
    success: boolean;
    message: string;
    data: T;
}

export interface PageResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    number: number;
    size: number;
}

@Injectable({
    providedIn: 'root'
})
export class OrderService {
    private apiUrl = 'http://localhost:8080/api/orders';

    constructor(private http: HttpClient) { }

    // 從購物車結帳建立訂單
    checkout(shippingAddress: string, phoneNumber: string, note?: string): Observable<BaseResponse<Order>> {
        return this.http.post<BaseResponse<Order>>(`${this.apiUrl}/checkout`, {
            shippingAddress,
            phoneNumber,
            note: note || ''
        }, { withCredentials: true });
    }

    // 取得我的訂單
    getMyOrders(): Observable<BaseResponse<Order[]>> {
        return this.http.get<BaseResponse<Order[]>>(`${this.apiUrl}/my-orders`, {
            withCredentials: true
        });
    }

    // 取得所有訂單（管理員）
    getAllOrders(page: number = 0, size: number = 10, keyword?: string): Observable<BaseResponse<PageResponse<Order>>> {
        const q = keyword && keyword.trim().length > 0 ? `&keyword=${encodeURIComponent(keyword.trim())}` : '';
        return this.http.get<BaseResponse<PageResponse<Order>>>(`${this.apiUrl}/all?page=${page}&size=${size}${q}`, {
            withCredentials: true
        });
    }

    // 取得訂單詳情
    getOrderById(orderId: number): Observable<BaseResponse<Order>> {
        return this.http.get<BaseResponse<Order>>(`${this.apiUrl}/${orderId}`, {
            withCredentials: true
        });
    }

    // 更新訂單狀態（管理員）
    updateOrderStatus(orderId: number, status: string): Observable<BaseResponse<Order>> {
        return this.http.put<BaseResponse<Order>>(`${this.apiUrl}/${orderId}/status`, {
            status
        }, { withCredentials: true });
    }

    // 取消訂單
    cancelOrder(orderId: number): Observable<BaseResponse<string>> {
        return this.http.put<BaseResponse<string>>(`${this.apiUrl}/${orderId}/cancel`, {}, {
            withCredentials: true
        });
    }

    // 刪除訂單（管理員）
    deleteOrder(orderId: number): Observable<BaseResponse<string>> {
        return this.http.delete<BaseResponse<string>>(`${this.apiUrl}/${orderId}`, {
            withCredentials: true
        });
    }
}
