import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  id: number;
  username: string;
  email: string;
  fullname: string;
  roleNames?: string[];
}

export interface UpdateUserRequest {
  email?: string;
  fullname?: string;
  roleNames?: string[];
}

export interface UpdatePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

export interface BaseResponse<T> {
  success: boolean;
  message: string;
  data?: T;
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
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) { }

  // 一般用戶 API
  getCurrentUser(): Observable<BaseResponse<User>> {
    return this.http.get<BaseResponse<User>>(`${this.apiUrl}/me`, {
      withCredentials: true
    });
  }

  updateCurrentUser(data: UpdateUserRequest): Observable<BaseResponse<User>> {
    return this.http.put<BaseResponse<User>>(`${this.apiUrl}/me`, data, {
      withCredentials: true
    });
  }

  updatePassword(data: UpdatePasswordRequest): Observable<BaseResponse<void>> {
    return this.http.put<BaseResponse<void>>(`${this.apiUrl}/me/password`, data, {
      withCredentials: true
    });
  }

  // 管理員 API
  getAllUsers(): Observable<BaseResponse<User[]>> {
    return this.http.get<BaseResponse<User[]>>(this.apiUrl, {
      withCredentials: true
    });
  }

  getUserById(id: number): Observable<BaseResponse<User>> {
    return this.http.get<BaseResponse<User>>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }

  updateUser(id: number, data: UpdateUserRequest): Observable<BaseResponse<User>> {
    return this.http.put<BaseResponse<User>>(`${this.apiUrl}/${id}`, data, {
      withCredentials: true
    });
  }

  deleteUser(id: number): Observable<BaseResponse<void>> {
    return this.http.delete<BaseResponse<void>>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }

  getPagedUsers(page: number = 0, size: number = 10, keyword?: string): Observable<BaseResponse<PageResponse<User>>> {
    const q = keyword && keyword.trim().length > 0 ? `&keyword=${encodeURIComponent(keyword.trim())}` : '';
    return this.http.get<BaseResponse<PageResponse<User>>>(`${this.apiUrl}/paged?page=${page}&size=${size}${q}`, {
      withCredentials: true
    });
  }
}
