import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { User } from 'src/app/models/User';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {
  public editForm: FormGroup;

  constructor(
    private dialogue: MatDialogRef<EditUserComponent>,
    private fb: FormBuilder,
    private notificationService: NotificationService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private userService: UserService
  ) {
    console.log(data.user);

  }

  ngOnInit(): void {
    this.editForm = this.createForm();
  }

  submit(): void {
    this.userService.updateUser(this.getUserFromForm())
      .subscribe(() => {
        this.notificationService.showSncackBar('User was updated');
        this.dialogue.close();
      });
  }

  closeDialog() {
    this.dialogue.close();
  }

  createForm(): FormGroup {
    console.log(`first name = ${this.data.user.firstname}; last name = ${this.data.user.lastname}; bio = ${this.data.user.bio}`);

    return this.fb.group({
      firstName: [this.data.user.firstname, Validators.compose([Validators.required])],
      lastName: [this.data.user.lastname, Validators.compose([Validators.required])],
      bio: [this.data.user.bio]
    });
  }

  private getUserFromForm(): User {
    this.data.user.firstname = this.editForm.value.firstName;
    this.data.user.lastname = this.editForm.value.lastName;
    this.data.user.bio = this.editForm.value.bio;
    return this.data.user;
  }

}

