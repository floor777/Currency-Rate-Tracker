import { Component, EventEmitter, Output } from '@angular/core';
import { AxiosService } from '../services/axios.service';
import { response } from 'express';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth-content',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './auth-content.component.html',
  styleUrl: './auth-content.component.sass'
})
export class AuthContentComponent {
  data: string[] = [];
  testData: string | null = "";
  @Output() showError = new EventEmitter<boolean>();
  @Output() testRequest = new EventEmitter();

  constructor(private axiosService: AxiosService) {

  }

  ngOnInit(): void {
    this.axiosService.request(
      "GET",
      "/messages",
      []
      
    ).then(
      (response) => this.data = response.data
    )
  }

  sendTestRequest(): void {
    console.log("send test request clicked")
    alert("hello")
    this.axiosService.request(
      "GET",
      "/currency",
      []

    ).then(
      (response) => {
        this.data = response.data
        let tokenVal = this.axiosService.getAuthToken();
        console.log(tokenVal);

      }
      
      
    ).catch( 
      (error) => {
        console.error("Error occurred: " + error);
        this.showError.emit(true);

        // if(error instanceof TokenExpired)


    }
  );
  }

}
