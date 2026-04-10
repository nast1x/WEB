import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../models/task.model';
import { environment } from '../../environment';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  constructor(private http: HttpClient) {}

  getTasks(userId: number, from?: string, to?: string): Observable<Task[]> {
    const defaultFrom = '1900-01-01T00:00:00';
    const defaultTo = '2100-12-31T23:59:59';

    let params = new HttpParams()
      .set('userId', userId.toString())
      .set('from', from || defaultFrom)
      .set('to', to || defaultTo);

    return this.http.get<Task[]>(`${environment.apiUrl}/tasks`, { params });
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${environment.apiUrl}/tasks/${id}`);
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(`${environment.apiUrl}/tasks`, task);
  }

  updateTask(id: number, task: Task): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/tasks/${id}`, task);
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/tasks/${id}`);
  }

  countActiveTasks(userId: number): Observable<number> {
    return this.http.get<number>(`${environment.apiUrl}/tasks/active/count?userId=${userId}`);
  }
}
