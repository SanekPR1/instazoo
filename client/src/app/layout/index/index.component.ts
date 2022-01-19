import { Component, OnInit } from '@angular/core';
import { Post } from 'src/app/models/Post';
import { User } from 'src/app/models/User';
import { CommentService } from 'src/app/service/comment.service';
import { ImageService } from 'src/app/service/image.service';
import { NotificationService } from 'src/app/service/notification.service';
import { PostService } from 'src/app/service/post.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit {

  isPostsLoaded = false;
  isUserDataLoaded = false;
  posts: Post[];
  user: User;

  constructor(
    private postService: PostService,
    private userService: UserService,
    private commentService: CommentService,
    private notificationService: NotificationService,
    private imageService: ImageService
  ) { }

  ngOnInit(): void {
    this.postService.getAllPosts()
      .subscribe(data => {
        console.log(data);
        this.posts = data;
        this.getImagesToPosts(this.posts);
        this.getCommentsToPosts(this.posts)
        this.isPostsLoaded = true;
      })

    this.userService.getCurrentUser()
      .subscribe(data => {
        this.user = data;
        this.isUserDataLoaded = true;
      });
  }

  getImagesToPosts(posts: Post[]): void {
    posts.forEach(p => {
      this.imageService.getPostImage(p.id)
        .subscribe(img => {
          p.image = img.imageBytes;
        })
    })
  }

  getCommentsToPosts(posts: Post[]): void {
    posts.forEach(p => {
      this.commentService.getPostComments(p.id)
        .subscribe(comment => {
          p.comments = comment;
        })
    })
  }

  likePost(postId: number, postIndex: number): void {
    const post = this.posts[postIndex];
    console.log(post);

    if (!post.userLiked.includes(this.user.username)) {
      this.postService.likePost(postId)
        .subscribe(() => {
          post.userLiked.push(this.user.username);
          this.notificationService.showSncackBar('Liked!');
        });
    } else {
      this.postService.likePost(postId)
        .subscribe(() => {
          const index = post.userLiked.indexOf(this.user.username, 0);
          post.userLiked.splice(index, 1);
          this.notificationService.showSncackBar('Unliked!');
        });
    }
  }

  postComment(message: string, postId: number, postIndex: number) {
    const post = this.posts[postIndex];
    console.log(post);

    this.commentService.addPostComment(postId, message)
      .subscribe(data => {
        console.log(data);
        post.comments.push(data);
      })
  }

  formatImage(img: any): any {
    if (img == null) {
      return null;
    }

    return 'data:image/jpeg;base64,' + img;
  }

}
