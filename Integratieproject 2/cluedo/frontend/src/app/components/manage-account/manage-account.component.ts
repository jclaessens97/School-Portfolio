import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormControl, Validators } from "@angular/forms";
import { UserService } from "../../services/user.service";
import { AlertService } from "../../services/alert.service";
import { Account } from "../../models/Account";
import { AuthenticationService } from "../../services/authentication.service";
import { first } from "rxjs/operators";
import { Me } from "../../models/Me";
import { UserInfo } from "../../models/UserInfo";
import { Password } from "../../models/Password";
import { Card } from "../make-suggestion/make-suggestion.component";

@Component({
  selector: "app-manage-account",
  templateUrl: "./manage-account.component.html",
  styleUrls: ["./manage-account.component.css"]
})
export class ManageAccountComponent implements OnInit {
  manageForm;
  passwordForm;
  valid = false;
  change = false;
  info = false;
  user: UserInfo = {
    username: "username",
    email: "email",
    verified: true,
    roles: []
  };
  pass: Password = {
    oldPassword: "",
    newPassword: ""
  };

  profileError = undefined;
  accountError = undefined;

  profileSuccess = undefined;
  accountSuccess = undefined;

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
    username: [{ type: "required", message: "Username is required" }]
  };

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService
  ) {
    this.manageForm = this.formBuilder.group({
      email: new FormControl(
        "",
        Validators.compose([
          Validators.required,
          Validators.pattern("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$")
        ])
      ),
      username: new FormControl(["", Validators.required])
    });

    this.passwordForm = this.formBuilder.group({
      oldPassword: new FormControl(["", [Validators.required]]),
      newPassword: new FormControl(
        "",
        Validators.compose([
          Validators.minLength(5),
          Validators.required,
          Validators.pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]+$")
        ])
      )
    });
  }

  ngOnInit() {
    this.userService.getUserInfo().subscribe((data: UserInfo) => {
      this.user = data;
    });
  }

  onSubmitProfile() {
    this.profileError = undefined;
    this.profileSuccess = undefined;
    this.userService
      .updateInformation(this.user)
      .pipe(first())
      .subscribe(
        data => {
          this.profileSuccess = "INFORMATION_UPDATED";
        },
        error => {
          this.profileError = error.error.message;
        }
      );
  }

  toggleChangePassField() {
    this.change = !this.change;
  }

  toggleChangeInfoField() {
    this.info = !this.info;
  }

  onSubmitPassword() {
    this.accountError = undefined;
    this.accountSuccess = undefined;

    this.userService
      .changePass(this.pass)
      .pipe(first())
      .subscribe(
        (data: boolean) => {
          this.accountSuccess = "PASSWORD_UPDATED";
          this.toggleChangePassField();
        },
        error => {
          this.accountError = error.error.message;
        }
      );
  }
}
