import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface QA {
  category: string;
  question: string;
  answer: string;
}

@Component({
  selector: 'app-qa',
  templateUrl: './qa.component.html',
  styleUrls: ['./qa.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class QaComponent {

  qaList: QA[] = [
    {
      category: '訂房相關',
      question: '如何進行線上訂房？',
      answer: '您可以在我們的網站上選擇房型，選定入住與退房日期後，填寫基本資料即可完成預訂。我們會透過 Email 確認您的訂單。'
    },
    {
      category: '訂房相關',
      question: '可以取消或更改訂房嗎？',
      answer: '入住日期前 7 天可免費取消或更改，7 天內取消將收取訂金 50% 作為手續費。'
    },
    {
      category: '訂房相關',
      question: '是否提供早鳥優惠？',
      answer: '是的！提前 30 天預訂可享 85 折優惠，提前 60 天預訂更可享 8 折優惠。'
    },
    {
      category: '入住相關',
      question: '入住與退房時間為何？',
      answer: '入住時間為下午 3:00 後，退房時間為上午 11:00 前。如有特殊需求，請提前與我們聯繫。'
    },
    {
      category: '入住相關',
      question: '是否提供機場接送服務？',
      answer: '我們提供付費機場接送服務，需於入住前 3 天預約。詳細費用請洽服務人員。'
    },
    {
      category: '入住相關',
      question: '可以攜帶寵物嗎？',
      answer: '很抱歉，為維護所有房客的住宿品質，我們目前不接受寵物入住。'
    },
    {
      category: '設施相關',
      question: '飯店提供哪些設施？',
      answer: '我們提供無邊際泳池、健身房、SPA 中心、餐廳、酒吧、會議室等設施，讓您享受完善的度假體驗。'
    },
    {
      category: '設施相關',
      question: '是否有免費 Wi-Fi？',
      answer: '是的，全館提供免費高速 Wi-Fi，讓您隨時保持連線。'
    },
    {
      category: '設施相關',
      question: '是否提供停車場？',
      answer: '我們提供免費專屬停車場，並有 24 小時保全服務。'
    },
    {
      category: '餐飲相關',
      question: '房價是否包含早餐？',
      answer: '部分房型包含早餐，請於訂房時確認。我們的早餐時間為早上 7:00 至 10:30。'
    },
    {
      category: '餐飲相關',
      question: '飯店內有餐廳嗎？',
      answer: '我們設有景觀餐廳及海景酒吧，提供精緻料理與特調飲品，營業時間為 11:00 至 22:00。'
    },
    {
      category: '付款相關',
      question: '接受哪些付款方式？',
      answer: '我們接受現金、信用卡（Visa、MasterCard、JCB）及第三方支付（LINE Pay、街口支付）。'
    }
  ];

  groupedQaList: Record<string, QA[]> = {};
  openState: Record<string, boolean> = {};

  constructor() {
    this.groupByCategory();
  }

  groupByCategory(): void {
    this.groupedQaList = this.qaList.reduce((groups, item) => {
      const category = item.category;
      if (!groups[category]) {
        groups[category] = [];
      }
      groups[category].push(item);
      return groups;
    }, {} as Record<string, QA[]>);
  }

  objectKeys(obj: object): string[] {
    return Object.keys(obj);
  }

  toggle(item: QA): void {
    const key = item.question;
    this.openState[key] = !this.openState[key];
  }
}
