import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { NotificationService } from 'src/app/service/notification.service';
import { TokenStorageService } from 'src/app/service/token-storage.service';
import { matchValidator } from 'src/app/validators/form-validators';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  public registerForm: FormGroup;

  constructor(
    private authService: AuthService,
    private tokenService: TokenStorageService,
    private notificationService: NotificationService,
    private router: Router,
    private fb: FormBuilder
  ) {
    if (this.tokenService.getUser) {
      this.router.navigate(['main']);
    }
  }

  ngOnInit(): void {
    this.registerForm = this.createSigninForm();
  }

  createSigninForm(): FormGroup {
    return this.fb.group({
      username: ['', Validators.compose([Validators.required, Validators.min(4), Validators.max(50)])],
      email: ['', Validators.compose([Validators.required, Validators.email])],
      firstname: ['', Validators.compose([Validators.required, Validators.min(2), Validators.max(150)])],
      lastname: ['', Validators.compose([Validators.required, Validators.min(2), Validators.max(150)])],
      password: ['', Validators.compose([Validators.required, Validators.min(4), matchValidator('confirmPassword', true)])],
      confirmPassword: ['', Validators.compose([Validators.required, matchValidator('password')])],
    })
  }

  submit() {
    this.authService.register({
      firstname: this.registerForm.value.firstname,
      lastname: this.registerForm.value.lastname,
      email: this.registerForm.value.email,
      username: this.registerForm.value.username,
      password: this.registerForm.value.password,
      confirmedPassword: this.registerForm.value.confirmPassword
    }).subscribe(data => {
      this.notificationService.showSncackBar(data);
      this.router.navigate(['/']);
      window.location.reload();
    }, error => {
      console.error(error);
      this.notificationService.showSncackBar(error);
    });
  }

  back() {
    this.router.navigate(['/login']);
  }

}
