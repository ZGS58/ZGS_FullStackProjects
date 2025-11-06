import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CartItemDto {
    id: number;
    productId: number;
    productName: string;
    productPrice: number;
    quantity: number;
    subtotal: number;
}

export interface CartDto {
    id: number;
    userId: number;
    items: CartItemDto[];
    totalPrice: number;
    totalItems: number;
}

export interface BaseResponse<T> {
    status: string;
    message: string;
    data: T;
}

@Injectable({
    providedIn: 'root'
})
export class CartService {
    private apiUrl = 'http://localhost:8080/api/cart';

    constructor(private http: HttpClient) { }

    // 獲取購物車
    getCart(): Observable<BaseResponse<CartDto>> {
        return this.http.get<BaseResponse<CartDto>>(this.apiUrl, { withCredentials: true });
    }

    // 添加商品到購物車
    addToCart(productId: number, quantity: number): Observable<BaseResponse<CartDto>> {
        return this.http.post<BaseResponse<CartDto>>(
            `${this.apiUrl}/add`,
            { productId, quantity },
            { withCredentials: true }
        );
    }

    // 更新商品數量
    updateCartItem(cartItemId: number, quantity: number): Observable<BaseResponse<CartDto>> {
        return this.http.put<BaseResponse<CartDto>>(
            `${this.apiUrl}/item/${cartItemId}`,
            { quantity },
            { withCredentials: true }
        );
    }

    // 移除商品
    removeFromCart(cartItemId: number): Observable<BaseResponse<CartDto>> {
        return this.http.delete<BaseResponse<CartDto>>(
            `${this.apiUrl}/item/${cartItemId}`,
            { withCredentials: true }
        );
    }

    // 清空購物車
    clearCart(): Observable<BaseResponse<string>> {
        return this.http.delete<BaseResponse<string>>(
            `${this.apiUrl}/clear`,
            { withCredentials: true }
        );
    }

    // 管理員 - 獲取所有購物車
    getAllCarts(): Observable<BaseResponse<CartDto[]>> {
        return this.http.get<BaseResponse<CartDto[]>>(
            `${this.apiUrl}/admin/all`,
            { withCredentials: true }
        );
    }

    // 管理員 - 清空用戶購物車
    clearUserCart(userId: number): Observable<BaseResponse<string>> {
        return this.http.delete<BaseResponse<string>>(
            `${this.apiUrl}/admin/clear/${userId}`,
            { withCredentials: true }
        );
    }
}
