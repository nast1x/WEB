import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { AuthService } from '../../services/auth.service';
import {Task, TaskStatus} from '../../models/task.model';
import { TaskComponent } from '../task/task.component';
import { RouterLink } from '@angular/router';
import { forkJoin, Observable } from 'rxjs';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [TaskComponent, RouterLink, FormsModule],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  filteredTasks: Task[] = [];
  isAdmin = false;
  activeTasksCount = 0;
  isLoading = true;
  searchQuery = '';
  selectedStatus: string = 'ALL';


  deleteErrorMessage = '';
  failedTaskId: number | null = null;

  constructor(private taskService: TaskService, private authService: AuthService) {}

  ngOnInit() {
    this.isAdmin = this.authService.isAdmin();
    this.loadData();
  }

  loadData() {
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.isLoading = true;

      this.taskService.getTasks(userId).subscribe({
        next: (res) => {
          this.tasks = res;
          this.applyFilters();
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });

      this.updateActiveTasksCount(userId);
    } else {
      this.isLoading = false;
    }
  }

  applyFilters() {
    this.filteredTasks = this.tasks.filter(task => {
      const matchStatus = this.selectedStatus === 'ALL' || task.status === this.selectedStatus;
      const matchSearch = task.title.toLowerCase().includes(this.searchQuery.toLowerCase());
      return matchStatus && matchSearch;
    });
  }

  onDelete(id: number) {
    this.deleteErrorMessage = '';
    this.failedTaskId = null;

    this.taskService.deleteTask(id).subscribe({
      next: () => {
        this.tasks = this.tasks.filter(t => t.id !== id);
        this.applyFilters();
        this.updateActiveTasksCount(this.authService.getCurrentUserId());
      },
      error: (err) => {

        this.failedTaskId = id;

        if (err.status === 403) {
          this.deleteErrorMessage = 'Невозможно удалить задачу! С момента её создания должно пройти не менее 5 минут.';
        } else {
          this.deleteErrorMessage = 'Произошла ошибка при удалении задачи.';
        }

        setTimeout(() => {
          this.deleteErrorMessage = '';
          this.failedTaskId = null;
        }, 3000);
      }
    });
  }

  fillTestTasks() {
    const tasksNeeded = 10 - this.activeTasksCount;

    if (tasksNeeded <= 0) {
      return;
    }

    this.isLoading = true;

    const creationRequests: Observable<Task>[] = [];

    for (let i = 0; i < tasksNeeded; i++) {
      const testTask: Task = {
        title: `Тестовая задача № ${Math.floor(Math.random() * 10000)}`,
        status: TaskStatus.OPEN
      };
      creationRequests.push(this.taskService.createTask(testTask));
    }

    forkJoin(creationRequests).subscribe({
      next: () => {
        this.loadData();
      },
      error: (err) => {
        console.error('Ошибка при массовом создании задач:', err);
        this.isLoading = false;
        alert('Произошла ошибка при генерации задач.');
      }
    });
  }

  private updateActiveTasksCount(userId: number) {
    this.taskService.countActiveTasks(userId).subscribe(count => {
      this.activeTasksCount = count;
    });
  }
}
