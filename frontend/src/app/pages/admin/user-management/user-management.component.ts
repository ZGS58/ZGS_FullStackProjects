import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, User, UpdateUserRequest, PageResponse } from '../../../services/user.service';

@Component({
  selector: 'app-user-management',
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.css'
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  editingUser: User | null = null;
  editForm = {
    email: '',
    fullname: ''
  };

  message = '';
  errorMessage = '';

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getPagedUsers(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.users = response.data.content;
          this.totalPages = response.data.totalPages;
          this.totalElements = response.data.totalElements;
          this.currentPage = response.data.number;
        }
      },
      error: (error) => {
        this.errorMessage = '載入用戶列表失敗';
        console.error('Error loading users:', error);
      }
    });
  }

  startEdit(user: User): void {
    this.editingUser = user;
    this.editForm.email = user.email;
    this.editForm.fullname = user.fullname;
    this.clearMessages();
  }

  cancelEdit(): void {
    this.editingUser = null;
    this.editForm = { email: '', fullname: '' };
    this.clearMessages();
  }

  updateUser(): void {
    if (!this.editingUser) return;

    this.clearMessages();

    const updateData: UpdateUserRequest = {
      email: this.editForm.email,
      fullname: this.editForm.fullname
    };

    this.userService.updateUser(this.editingUser.id, updateData).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.message = '用戶資料更新成功';
          this.editingUser = null;
          this.loadUsers(); // 重新載入列表
        }
      },
      error: (error) => {
        this.errorMessage = '更新失敗：' + (error.error?.message || '請稍後再試');
        console.error('Error updating user:', error);
      }
    });
  }

  deleteUser(user: User): void {
    if (!confirm(`確定要刪除用戶 "${user.username}" 嗎？此操作無法撤銷。`)) {
      return;
    }

    this.clearMessages();

    this.userService.deleteUser(user.id).subscribe({
      next: (response) => {
        if (response.success) {
          this.message = '用戶刪除成功';
          this.loadUsers(); // 重新載入列表
        }
      },
      error: (error) => {
        this.errorMessage = '刪除失敗：' + (error.error?.message || '請稍後再試');
        console.error('Error deleting user:', error);
      }
    });
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadUsers();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadUsers();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadUsers();
    }
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  clearMessages(): void {
    this.message = '';
    this.errorMessage = '';
  }
}
