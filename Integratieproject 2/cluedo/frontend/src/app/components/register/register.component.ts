import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { first } from 'rxjs/operators';

import { AlertService } from "../../services/alert.service";
import { UserService } from "../../services/user.service";
import { AuthenticationService } from "../../services/authentication.service";

@Component({ selector: 'app-register', templateUrl: 'register.component.html', styleUrls: ['../register/register.component.scss'] })
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  loading = false;
  submitted = false;
  @Output() emitIndex = new EventEmitter<number>();

  validation_messages = {
    password: [
      { type: "required", message: "Password is required" },
      {
        type: "minlength",
        message: "Password must be at least 5 characters long"
      },
      {
        type: "pattern",
        message:
          "Your password must contain at least one uppercase, one lowercase, and one number"
      }
    ],
    email: [
      { type: "required", message: "Email is required" },
      { type: "pattern", message: "Enter a valid email" }
    ],
    username: [{ type: "required", message: "Username is required" },
      { type: 'minlength', message: 'Username must be at least 3 characters long' },
      { type: 'maxlength', message: 'Username cannot be more than 15 characters long' }]
  };

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authenticationService: AuthenticationService,
    private userService: UserService,
    private alertService: AlertService
  ) {
    // redirect to home if already logged in
    if (this.authenticationService.currentTokenValue) {
      this.router.navigate(["/"]);
    }
  }

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
      email: ["", [Validators.required, Validators.email]],
      username: new FormControl("", Validators.compose([
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(15)
      ])),
      password: new FormControl(
        "",
        Validators.compose([
          Validators.minLength(5),
          Validators.required,
          Validators.pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]+$")
        ])
      )
    });
  }

  get f() {
    return this.registerForm.controls;
  }

  onSubmit() {
    this.submitted = true;

    this.alertService.clear();

    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    this.userService
      .register(this.registerForm.value)
      .pipe(first())
      .subscribe(
        data => {
          this.alertService.success("REGISTER_SUCCESFULL", true);
          this.emitIndex.emit(0);
        },
        error => {
          this.alertService.error(error.error.message);
          this.loading = false;
        }
      );
  }
}
