
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';


export interface Product {
  id?: number;
  name: string;
  description: string;
  imageUrl: string;
  price: number;
  stock?: number;
}

export interface BaseResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}


@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/api/product'
  constructor(private http: HttpClient) { }

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  // 取得商品分頁（管理員用）
  getPaged(page: number = 0, size: number = 10): Observable<BaseResponse<PageResponse<Product>>> {
    return this.http.get<BaseResponse<PageResponse<Product>>>(`${this.apiUrl}/paged?page=${page}&size=${size}`, {
      withCredentials: true
    });
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  addProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product, {
      headers: { Authorization: 'Basic ' + btoa('admin:admin123') }
    });
  }

  create(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product, { withCredentials: true });
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product, {
      headers: { Authorization: 'Basic ' + btoa('admin:admin123') }
    });
  }

  update(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product, { withCredentials: true });
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: { Authorization: 'Basic ' + btoa('admin:admin123') }
    });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}
