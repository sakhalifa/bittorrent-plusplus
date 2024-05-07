#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>

#include "thpool.h"

task_queue_t *task_queue_init() {
	task_queue_t *q = malloc(sizeof(task_queue_t));
	q->count        = 0;
	q->front        = NULL;
	q->rear         = NULL;

	pthread_mutex_init(&(q->m_rw), NULL);
	return q;
}

void task_queue_push(task_queue_t *q, task_t *t) {
	pthread_mutex_lock(&(q->m_rw));
	if (q->count == 0) {
		q->front = t;
		q->rear  = t;
	} else {
		q->rear->prev = t;
		q->rear       = t;
	}
	q->count++;
	pthread_mutex_unlock(&(q->m_rw));
}

task_t *task_queue_pull(task_queue_t *q) {
	pthread_mutex_lock(&(q->m_rw));
	task_t *t = q->front;

	if (q->count == 1) {
		q->count = 0;
		t        = q->front;
		q->front = NULL;
		q->rear  = NULL;
	}
	if (q->count > 1 && q->front != NULL) {
		q->count--;
		t        = q->front;
		q->front = t->prev;
	}
	pthread_mutex_unlock(&(q->m_rw));
	return t;
}

void task_queue_clear(task_queue_t *q) {
	while (q->count != 0) {
		free(task_queue_pull(q));
	}

	q->count = 0;
	q->front = NULL;
	q->rear  = NULL;
}

void task_destroy(task_queue_t *q) {
	task_queue_clear(q);
	free(q);
}

void *thread_function(void *arg) {
	return NULL;
}

void thread_init(thpool_t *thpool, thread_t **pool, int id) {
	*pool       = malloc(sizeof(thread_t));
	(*pool)->id = id;
	pthread_create(
	    &((*pool)->p_thread), NULL, (void *(*)(void *))thread_function, NULL);
	pthread_detach(
	    (*pool)->p_thread); // should fix memory leak ? (possibly lost)
}

void thread_destroy(thread_t *thread) {
	// pthread_mutex_destroy(&thread->m_thread); // cause valgrind errors
	free(thread);
}

thpool_t *thpool_init(int size) {
	thpool_t *thpool = malloc(sizeof(thpool_t));
	thread_t **pool  = malloc(sizeof(thread_t) * size);
	thpool->threads  = pool;
	thpool->size     = size;

	task_queue_t *queue = task_queue_init();
	thpool->queue       = *queue;

	for (int i = 0; i < size; i++) {
		thread_init(thpool, &thpool->threads[i], i);
	}

	return thpool;
}

void thpool_add_work(thpool_t *thpool, void *(*task)(void *), void *arg) {
	task_t *new_task = malloc(sizeof(task_t));
	new_task->p_func = task;
	new_task->arg    = arg;

	task_queue_push(&thpool->queue, new_task);
}

void thpool_destroy(thpool_t *thpool) {

	for (int i = 0; i < thpool->size; i++) {
		thread_destroy(thpool->threads[i]);
	}

	while (thpool->queue.count != 0) {
		free(task_queue_pull(&thpool->queue));
	}

	free(thpool->threads);
	free(thpool);
}