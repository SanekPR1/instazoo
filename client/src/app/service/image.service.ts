import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from './service-consts';

export const IMG_API = API_URL + 'image/';

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  constructor(private http: HttpClient) { }

  uploadProfileImage(file: File): Observable<any> {
    const uploadData = new FormData();
    uploadData.append("file", file);

    return this.http.post(IMG_API + 'upload', uploadData);
  }

  uploadPostImage(file: File, postId: number): Observable<any> {
    const uploadData = new FormData();
    uploadData.append("file", file);

    return this.http.post(IMG_API + postId + '/upload', uploadData);
  }

  getProfileImage(): Observable<any> {
    return this.http.get(IMG_API + "/profile");
  }

  getPostImage(postId: number): Observable<any> {
    return this.http.get(IMG_API + postId + "/image");
  }
}
