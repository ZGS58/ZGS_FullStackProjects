import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Room {
  id?: number;
  name: string;
  type: string;
  price: number;
  description: string;
  capacity?: number;
  available?: boolean;
  stock?: number;
  imageUrl?: string;
}

export interface RoomDto {
  id: number;
  name: string;
  type: string;
  price: number;
  description: string;
  capacity?: number;
  available?: boolean;
  stock?: number;
  imageUrl?: string;
}

export interface BaseResponse<T> {
  success: boolean;
  message: string;
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
export class RoomService {
  private apiUrl = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient) { }

  getRooms(): Observable<BaseResponse<Room[]>> {
    return this.http.get<BaseResponse<Room[]>>(this.apiUrl, {
      withCredentials: true
    });
  }

  // 取得房型分頁（管理員用）
  getPaged(page: number = 0, size: number = 10): Observable<BaseResponse<PageResponse<Room>>> {
    return this.http.get<BaseResponse<PageResponse<Room>>>(`${this.apiUrl}/paged?page=${page}&size=${size}`, {
      withCredentials: true
    });
  }

  getRoomById(id: number): Observable<BaseResponse<Room>> {
    return this.http.get<BaseResponse<Room>>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }

  create(room: Room): Observable<BaseResponse<Room>> {
    return this.http.post<BaseResponse<Room>>(this.apiUrl, room, {
      withCredentials: true
    });
  }

  update(id: number, room: Room): Observable<BaseResponse<Room>> {
    return this.http.put<BaseResponse<Room>>(`${this.apiUrl}/${id}`, room, {
      withCredentials: true
    });
  }

  delete(id: number): Observable<BaseResponse<string>> {
    return this.http.delete<BaseResponse<string>>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }
}
