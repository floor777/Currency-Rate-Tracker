import { Component } from '@angular/core';
import { WelcomeContentComponent } from '../welcome-content/welcome-content.component';
import { LoginFormComponent } from '../login-form/login-form.component';
import { AxiosService } from '../services/axios.service';
import { CommonModule } from '@angular/common';
import { ButtonsComponent } from '../buttons/buttons.component';
import { AuthContentComponent } from '../auth-content/auth-content.component';
import { SourceTextModule } from 'vm';

@Component({
  selector: 'app-content',
  standalone: true,
  imports: [WelcomeContentComponent, LoginFormComponent, CommonModule, ButtonsComponent, AuthContentComponent],
  templateUrl: './content.component.html',
  styleUrl: './content.component.sass'
})
export class ContentComponent {
  componentToShow: string = "welcome";
  constructor(private axiosService : AxiosService) {}

  onShowError(errorOccurred: boolean): void {
    if (errorOccurred) {
      alert("Session has expired. Please login again to continue")
      this.componentToShow = "login";
    }
  }

  showComponent(componentToShow: string): void {
    this.componentToShow = componentToShow;
  }

  onLogin(input: any): void {
    this.axiosService.request(
      "POST",
      "/login",
      {
        login: input.login,
        password: input.password
      }
    ).then(response => {
      console.log(response.status);
      console.log("we logged in i guess????")
      console.log("in content.components.ts: response data token on login is: " + response.data.token);
      this.axiosService.setAuthToken(response.data.token);
      this.componentToShow = 'messages';
    }).catch(error => {
      console.error("login error:", error);
      console.log("login status: " + error.response.status);
      // Handle other errors here
    });
  } 

  onRegister(input: any): void {
    this.axiosService.request(
      "POST",
      "/register",
      {
        firstName: input.firstName,
        lastName: input.lastName,
        login: input.login,
        password: input.password
      }
    ).then(response => {
      if(response.status === 401) {
        console.log("IT'S SO OVER GODDDDDDDDDDDDDDDDDDDDDDDDDDD")
        alert("NOOOOOOOOO");
      }
      console.log("we registered")
      console.log("response status:  " + response.status);
      console.log("in content.components.ts: response data token on register is: " + response.data.token);
      this.axiosService.setAuthToken(response.data.token);
      this.componentToShow = 'messages';
    }).catch(error => {
      console.error("Register error:", error);
      // console.log(error)
      console.log("Register status: " + error.response.status);
      // Handle other errors here
    });

  }

}
