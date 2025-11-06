import { HomeComponent } from './pages/home/home.component';
import { AboutComponent } from './pages/about/about.component';
import { ContactComponent } from './pages/contact/contact.component';
import { NewsComponent } from './pages/news/news.component';
import { ProductComponent } from './pages/product/product.component';
import { QaComponent } from './pages/qa/qa.component';
import { LoginComponent } from './pages/login/login.component';
import { RoomComponent } from './pages/room/room.component';
import { MyBookingsComponent } from './pages/my-bookings/my-bookings.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { AdminDashboardComponent } from './pages/admin/admin-dashboard/admin-dashboard.component';
import { CartComponent } from './pages/cart/cart.component';
import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";


export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'about', component: AboutComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'news', component: NewsComponent },
  { path: 'product', component: ProductComponent },
  { path: 'room', component: RoomComponent },
  { path: 'my-bookings', component: MyBookingsComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'admin', component: AdminDashboardComponent },
  { path: 'qa', component: QaComponent },
  { path: 'cart', component: CartComponent },
  { path: 'login', component: LoginComponent },
  { path: '**', component: HomeComponent }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }

