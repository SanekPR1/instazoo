import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Post } from 'src/app/models/Post';
import { ImageService } from 'src/app/service/image.service';
import { NotificationService } from 'src/app/service/notification.service';
import { PostService } from 'src/app/service/post.service';

@Component({
  selector: 'app-add-post',
  templateUrl: './add-post.component.html',
  styleUrls: ['./add-post.component.css']
})
export class AddPostComponent implements OnInit {

  postForm: FormGroup;
  selectedFile: File;
  createdPost: Post;
  previewImgURL: any;
  isPostCreated = false;
  isImageAddedToPost = false;

  constructor(
    private postService: PostService,
    private imageService: ImageService,
    private notificationService: NotificationService,
    private router: Router,
    private fb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.postForm = this.createPostForm();
  }

  private createPostForm(): FormGroup {
    return this.fb.group({
      title: ['', Validators.compose([Validators.required])],
      caption: ['', Validators.compose([Validators.required])],
      location: ['']
    });
  }

  submit(): void {

    if (this.isImageAddedToPost) {
      this.postService.createPost({
        title: this.postForm.value.title,
        caption: this.postForm.value.caption,
        location: this.postForm.value.location,
      }).subscribe(data => {
        this.createdPost = data;
        console.log(data);

        if (this.createdPost.id != null) {
          this.imageService.uploadPostImage(this.selectedFile, this.createdPost.id)
            .subscribe(() => {
              this.notificationService.showSnackBar('Post created successfully');
              this.isPostCreated = true;
              this.router.navigate(['/profile']);
            });
        }
      });
    } else {
      this.notificationService.showSnackBar('Image is required');
    }
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];

    const reader = new FileReader();
    reader.readAsDataURL(this.selectedFile);
    reader.onload = (e) => {
      this.previewImgURL = reader.result;
      this.isImageAddedToPost = true;
    };
  }
}
