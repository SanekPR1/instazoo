import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/User';
import { UPDATE, API_URL } from './service-consts';

export const USER_API = API_URL + 'user/';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  getUserById(id: number): Observable<any> {
    return this.http.get(USER_API + id);
  }

  getCurrentUser(): Observable<any> {
    return this.http.get(USER_API);
  }

  updateUser(user: User): Observable<any> {
    return this.http.put(USER_API + UPDATE, user);
  }

}
