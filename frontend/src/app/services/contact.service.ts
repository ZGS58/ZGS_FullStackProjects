import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

//
export interface Contact {

  id?: number;
  name: string;
  email: string;
  phone: string;
  message: string;

}


@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private apiUrl = 'http://localhost:8080/api/contact';

  constructor(private http: HttpClient) { }

  //前端使用者送表單
  sendMessage(contact: Contact): Observable<any> {
    return this.http.post<any>(this.apiUrl, contact);
  }

  // admin 查看聯絡表單（分頁）
  getPaged(page: number = 0, size: number = 10): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/paged?page=${page}&size=${size}`, {
      withCredentials: true
    });
  }

  // admin 刪除聯絡表單
  delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }
}
