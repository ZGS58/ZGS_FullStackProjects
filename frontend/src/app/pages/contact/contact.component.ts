import { Component } from '@angular/core';
import { ContactService,Contact } from '../../services/contact.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-contact',
  imports: [FormsModule, CommonModule],
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css'],
  standalone: true
})

export class ContactComponent {
  email = "xxxxx@gmail.com";

  contact:Contact = { name: '',email: '',phone: '',message: ''};
  success = false;


  constructor(private contactService:ContactService) {}

  submit() {
    this.contactService.sendMessage(this.contact).subscribe({
      next: () => {
        this.success = true;
        this.contact = { name: '',email: '',phone: '',message: ''};
      },
      error:(err) => {
        console.error('送出失敗',err);
      }
    })
  }
}
