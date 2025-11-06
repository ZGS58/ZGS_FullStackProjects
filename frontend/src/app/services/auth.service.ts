import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private loggedIn = new BehaviorSubject<boolean>(false);
  private username = new BehaviorSubject<string>('');
  private userRoles = new BehaviorSubject<string[]>([]);

  isLoggedIn$ = this.loggedIn.asObservable();
  username$ = this.username.asObservable();
  userRoles$ = this.userRoles.asObservable();

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    const body = new URLSearchParams();
    body.set('username', username);
    body.set('password', password);

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    return this.http.post(`${this.apiUrl}/login`, body.toString(), {
      headers,
      withCredentials: true
    }).pipe(
      tap((response: any) => {
        if (response.success) {
          this.loggedIn.next(true);
          this.username.next(response.username || username);
          // 假設後端返回角色信息，如果沒有則設為空數組
          const roles = response.roles || [];
          console.log('登入成功，收到的角色:', roles);
          this.userRoles.next(roles);
        }
      }),
      catchError(error => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => {
        this.loggedIn.next(false);
        this.username.next('');
        this.userRoles.next([]);
      })
    );
  }

  isAdmin(): boolean {
    const roles = this.userRoles.value;
    console.log('檢查管理員權限，當前角色:', roles);
    const isAdmin = roles.includes('ROLE_ADMIN') || roles.includes('ADMIN');
    console.log('是否為管理員:', isAdmin);
    return isAdmin;
  }

  getCurrentUsername(): string {
    return this.username.value;
  }

  register(data: { username: string, email: string, fullname: string, password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data, {
      withCredentials: true
    }).pipe(
      catchError(error => {
        console.error('Register error:', error);
        return throwError(() => error);
      })
    );
  }
}
