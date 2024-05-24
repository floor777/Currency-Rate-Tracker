import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header/header.component';
import { AuthContentComponent } from './auth-content/auth-content.component';
import { CommonModule } from '@angular/common';
import { ContentComponent } from './content/content.component';
import { FormsModule } from '@angular/forms';
import { TokenInterceptor } from './token.interceptor';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, AuthContentComponent, ContentComponent, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.sass',
  providers: [{provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true}]
})
export class AppComponent {
  title = 'my-app';
}
