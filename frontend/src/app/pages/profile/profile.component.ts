import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, User, UpdateUserRequest, UpdatePasswordRequest } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  user: User | null = null;
  editMode = false;
  passwordMode = false;

  // 編輯資料表單
  editForm = {
    email: '',
    fullname: ''
  };

  // 修改密碼表單
  passwordForm = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  message = '';
  errorMessage = '';

  constructor(
    private userService: UserService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    this.userService.getCurrentUser().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.user = response.data;
          this.editForm.email = this.user.email;
          this.editForm.fullname = this.user.fullname;
        }
      },
      error: (error) => {
        this.errorMessage = '載入用戶資料失敗';
        console.error('Error loading user:', error);
      }
    });
  }

  toggleEditMode(): void {
    if (this.editMode) {
      // 取消編輯，恢復原始資料
      if (this.user) {
        this.editForm.email = this.user.email;
        this.editForm.fullname = this.user.fullname;
      }
    }
    this.editMode = !this.editMode;
    this.passwordMode = false;
    this.clearMessages();
  }

  togglePasswordMode(): void {
    this.passwordMode = !this.passwordMode;
    this.editMode = false;
    if (!this.passwordMode) {
      this.resetPasswordForm();
    }
    this.clearMessages();
  }

  updateProfile(): void {
    this.clearMessages();

    const updateData: UpdateUserRequest = {
      email: this.editForm.email,
      fullname: this.editForm.fullname
    };

    this.userService.updateCurrentUser(updateData).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.user = response.data;
          this.message = '個人資料更新成功';
          this.editMode = false;
        }
      },
      error: (error) => {
        this.errorMessage = '更新失敗：' + (error.error?.message || '請稍後再試');
        console.error('Error updating user:', error);
      }
    });
  }

  updatePassword(): void {
    this.clearMessages();

    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.errorMessage = '新密碼與確認密碼不符';
      return;
    }

    if (this.passwordForm.newPassword.length < 6) {
      this.errorMessage = '新密碼至少需要 6 個字元';
      return;
    }

    const passwordData: UpdatePasswordRequest = {
      oldPassword: this.passwordForm.oldPassword,
      newPassword: this.passwordForm.newPassword
    };

    this.userService.updatePassword(passwordData).subscribe({
      next: (response) => {
        if (response.success) {
          this.message = '密碼修改成功';
          this.passwordMode = false;
          this.resetPasswordForm();
        }
      },
      error: (error) => {
        this.errorMessage = '密碼修改失敗：' + (error.error?.message || '請檢查舊密碼是否正確');
        console.error('Error updating password:', error);
      }
    });
  }

  resetPasswordForm(): void {
    this.passwordForm = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
  }

  clearMessages(): void {
    this.message = '';
    this.errorMessage = '';
  }
}
