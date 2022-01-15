import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Post } from '../models/Post';
import { CREATE, API_URL } from './service-consts';

export const POST_API = API_URL + 'post/';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(private http: HttpClient) { }

  createPost(post: Post): Observable<any> {
    return this.http.post(POST_API + CREATE, post);
  }

  getAllPosts(): Observable<any> {
    return this.http.get(POST_API + 'all');
  }

  getUserPosts(): Observable<any> {
    return this.http.get(POST_API + 'user/posts');
  }

  deletePost(id: number): Observable<any> {
    return this.http.delete(POST_API + id);
  }

  likePost(id: number) {
    return this.http.put(POST_API + id + '/like', null);
  }
}
