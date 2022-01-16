import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from './service-consts';

export const COMMENT_API = API_URL + 'comment/';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private http: HttpClient) { }

  addPostComment(postId: number, message: string): Observable<any> {
    return this.http.post(COMMENT_API + postId + '/create', {
      message: message
    });
  }

  getPostComments(postId: number): Observable<any> {
    return this.http.get(COMMENT_API + postId + '/comments');
  }

  deletePostComment(commentId: number,): Observable<any> {
    return this.http.delete(COMMENT_API + commentId);
  }
}
