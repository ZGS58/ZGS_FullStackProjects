import { CommonModule } from '@angular/common';
import { Component, HostListener, AfterViewInit, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { SlickCarouselModule } from 'ngx-slick-carousel';
import { LoginComponent } from './pages/login/login.component';
import { AuthService } from './services/auth.service';
import * as AOS from 'aos';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SlickCarouselModule, CommonModule, RouterLink, LoginComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements AfterViewInit, OnInit {
  title = 'final_project';
  email = "xxxxx@gmail.com";
  slideConfig = {
    dots: true,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 5000,
  };

  slides = [
    { img: "bg1.png" },
    { img: "bg2.jpg" },
    { img: "bg3.jpg" },
    { img: "bg4.jpg" },
    { img: "bg5.jpg" },
    { img: "bg7.jpg" }
  ];
  // 視窗是否捲動
  isFixed = false;
  // top按鈕是否被按下
  isVisible = false;
  isMenuOpen = false;
  showLoginModal = false;
  isLoggedIn = false;
  username = '';
  isAdmin = false;

  constructor(private authService: AuthService) { }

  ngOnInit() {
    // 訂閱登入狀態
    this.authService.isLoggedIn$.subscribe(loggedIn => {
      this.isLoggedIn = loggedIn;
      // 更新管理員狀態
      this.isAdmin = this.authService.isAdmin();
    });

    // 訂閱用戶名
    this.authService.username$.subscribe(name => {
      this.username = name;
    });

    // 訂閱角色變化
    this.authService.userRoles$.subscribe(() => {
      this.isAdmin = this.authService.isAdmin();
    });
  }

  // 監聽視窗的捲動事件 & top按鈕事件
  @HostListener('window:scroll', []) onWindowScroll() {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop ||
      document.body.scrollTop || 0;
    // 超過 50px 就固定在頂部
    if (scrollTop > 50) {
      this.isFixed = true;
    } else {
      this.isFixed = false;
    }
    // 超過 100px 顯示 Back to Top
    const yOffset = window.pageYOffset || document.documentElement.scrollTop ||
      document.body.scrollTop || 0;
    this.isVisible = yOffset > 100; // 超過 100px 就顯示按鈕
    // 滾動到最上方
  }

  scrollToTop() {
    window.scrollTo({
      top: 0, //設定滾動的目標位置為頁面的頂部，0 表示從最上方開始
      behavior: 'smooth' // 平滑滾動效果
    });
    // 當捲到最上面時，延遲 500ms 把按鈕隱藏 (動畫時間是 0.5s)
    setTimeout(() => {
      this.isVisible = false;
    }, 500);
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  openLoginModal() {
    this.showLoginModal = true;
  }

  closeLoginModal() {
    this.showLoginModal = false;
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        console.log('登出成功');
      },
      error: (error) => {
        console.error('登出失敗', error);
      }
    });
  }

  ngAfterViewInit(): void {
    AOS.init();
  }
}


