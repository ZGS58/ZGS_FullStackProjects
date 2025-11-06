import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  @Output() closeModal = new EventEmitter<void>();
  activeTab: 'login' | 'register' = 'login';

  // 登入表單
  username = '';
  password = '';
  loginError = '';

  // 註冊表單
  regUsername = '';
  regEmail = '';
  regFullname = '';
  regPassword = '';
  regConfirmPassword = '';
  registerError = '';
  registerSuccess = '';

  loading = false;

  constructor(private auth: AuthService, private router: Router) { }

  switchTab(tab: 'login' | 'register') {
    this.activeTab = tab;
    this.loginError = '';
    this.registerError = '';
    this.registerSuccess = '';
  }

  close() {
    this.closeModal.emit();
  }

  submitLogin() {
    this.loginError = '';
    if (!this.username || !this.password) {
      this.loginError = '請輸入帳號與密碼';
      return;
    }
    this.loading = true;
    this.auth.login(this.username, this.password).subscribe({
      next: () => {
        this.loading = false;
        this.close(); // 關閉模態框而非導航
      },
      error: (err: any) => {
        this.loading = false;
        this.loginError = '登入失敗，請確認帳密';
        console.error(err);
      }
    });
  }

  submitRegister() {
    this.registerError = '';
    this.registerSuccess = '';

    // 驗證
    if (!this.regUsername || !this.regEmail || !this.regFullname || !this.regPassword || !this.regConfirmPassword) {
      this.registerError = '請填寫所有欄位';
      return;
    }

    if (this.regPassword !== this.regConfirmPassword) {
      this.registerError = '密碼與確認密碼不符';
      return;
    }

    if (this.regPassword.length < 6) {
      this.registerError = '密碼長度至少需要 6 個字元';
      return;
    }

    this.loading = true;

    this.auth.register({
      username: this.regUsername,
      email: this.regEmail,
      fullname: this.regFullname,
      password: this.regPassword
    }).subscribe({
      next: (response: any) => {
        this.loading = false;
        if (response.success) {
          this.registerSuccess = '註冊成功！即將自動登入...';
          // 註冊成功後自動登入
          setTimeout(() => {
            this.username = this.regUsername;
            this.password = this.regPassword;
            this.switchTab('login');
            this.submitLogin();
          }, 1500);
        } else {
          this.registerError = response.message || '註冊失敗';
        }
      },
      error: (error: any) => {
        this.loading = false;
        this.registerError = error.error?.message || '註冊失敗，請稍後再試';
      }
    });
  }
}
