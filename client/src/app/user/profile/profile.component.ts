import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { User } from 'src/app/models/User';
import { ImageService } from 'src/app/service/image.service';
import { NotificationService } from 'src/app/service/notification.service';
import { PostService } from 'src/app/service/post.service';
import { TokenStorageService } from 'src/app/service/token-storage.service';
import { UserService } from 'src/app/service/user.service';
import { EditUserComponent } from '../edit-user/edit-user.component';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user: User;
  selectedFile: File;
  userProfileImage: File;
  previewImgURL: any;
  isUserDataLoaded = false;

  constructor(
    private tokenService: TokenStorageService,
    private postService: PostService,
    private notificationService: NotificationService,
    private imageService: ImageService,
    private userService: UserService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.userService.getCurrentUser()
      .subscribe(user => {
        this.user = user;
        this.isUserDataLoaded = true;
      });

    this.imageService.getProfileImage()
      .subscribe(image => {
        if (image != null) {
          this.userProfileImage = image.imageBytes;
        }
      });
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
    const reader = new FileReader();
    reader.readAsDataURL(this.selectedFile);
    reader.onload = () => {
      this.previewImgURL = reader.result;
    }
  }

  openEditDialog(): void {
    const dialogUserEditConfig = new MatDialogConfig();
    dialogUserEditConfig.width = '480px';
    dialogUserEditConfig.data = {
      user: this.user
    }
    this.dialog.open(EditUserComponent, dialogUserEditConfig);
  }

  formatImage(img: any): any {
    if (img == null) {
      return null;
    }

    return 'data:image/jpeg;base64,' + img;
  }

  onUpload(): void {
    if (this.selectedFile != null) {
      this.imageService.uploadProfileImage(this.selectedFile)
        .subscribe(() => {
          this.notificationService.showSnackBar('Profile image was uploaded');
        });
    }
  }

}
