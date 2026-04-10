import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Task } from '../../models/task.model';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-task',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss']
})
export class TaskComponent {
  @Input({required: true}) task!: Task;
  @Input() isAdmin = false;
  @Output() deleteTask = new EventEmitter<number>();

  onDelete() {
    if (this.task.id) {
      this.deleteTask.emit(this.task.id);
    }
  }
}
