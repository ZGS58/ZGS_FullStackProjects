import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface News {
  id: number;
  title: string;
  content: string;
  date: string;
  image: string;
  delay?: number;
}

@Component({
  selector: 'app-news',
  imports: [CommonModule],
  standalone: true,
  templateUrl: './news.component.html',
  styleUrl: './news.component.css'
})
export class NewsComponent implements OnInit {
  // 直接在前端定義新聞資料，不從後端 API 獲取
  newsList: News[] = [
    {
      id: 1,
      title: '春季早鳥預訂開跑',
      content: '提早預約您的療癒之旅，即享 85 折優惠，限時限量贈品等您來拿！',
      date: '2025-04-15',
      image: './activity.jpg',
      delay: 0
    },
    {
      id: 2,
      title: '仲夏音樂之夜 開放報名',
      content: '與海風和星光作伴，現場演出與調酒派對，名額有限，手刀預約！',
      date: '2025-03-28',
      image: './bar.jpg',
      delay: 100
    },
    {
      id: 3,
      title: '文化週 | 手作工藝體驗開跑',
      content: '邀請在地職人，打造療癒手作時光，來一場與土地對話的旅程。',
      date: '2025-03-10',
      image: './craft.jpg',
      delay: 200
    }
  ];

  constructor() { }

  ngOnInit(): void {
    // 資料已在組件中定義，不需要從 API 載入
  }
}
