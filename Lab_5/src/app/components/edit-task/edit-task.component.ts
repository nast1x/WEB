import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {TaskService} from '../../services/task.service';
import {Task, TaskStatus} from '../../models/task.model';

@Component({
  selector: 'app-edit-task',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './edit-task.component.html',
  styleUrls: ['./edit-task.component.scss']
})
export class EditTaskComponent implements OnInit {
  task: Task = {title: '', status: TaskStatus.OPEN};
  isEditMode = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService
  ) {
  }

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.taskService.getTaskById(+idParam).subscribe(t => this.task = t);
    }
  }

  saveTask() {
    this.errorMessage = '';
    const observer = {
      next: () => this.router.navigate(['/tasks']),
      error: (err: any) => this.handleError(err)
    };
    if (this.isEditMode && this.task.id) {
      this.taskService.updateTask(this.task.id, this.task).subscribe(observer);
    } else {
      this.taskService.createTask(this.task).subscribe(observer);
    }
  }



  private handleError(err: any) {
    if (err.status === 400 && err.error && err.error.message) {
      if (err.error.message.includes('more than 10 active tasks')) {
        this.errorMessage = 'Вы не можете иметь более 10 активных задач одновременно! Завершите текущие задачи перед созданием новых.';
      } else {
        this.errorMessage = err.error.message;
      }
    } else {
      this.errorMessage = 'Произошла непредвиденная ошибка при сохранении задачи.';
    }
  }

  cancel() {
    this.router.navigate(['/tasks']);
  }
}
