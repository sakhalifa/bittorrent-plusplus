#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>

#include "thpool.h"

typedef struct task_queue {
	int count;
	task_t *front;
	task_t *rear;
	pthread_mutex_t m_rw;
} task_queue_t;

typedef struct task {
	int task_id;
	void *p_func;
	task_t *prev;
} task_t;

typedef struct thread {
	int id;
	pthread_t *p_thread;
	pthread_mutex_t m_thread;
} thread_t;

typedef struct th_pool {
	int size;
	thread_t **threads;
	task_queue_t queue;
} th_pool_t;

task_queue_t *task_queue_init() {
	task_queue_t *q = malloc(sizeof(task_queue_t));
	q->count        = 0;
	q->front        = NULL;
	q->rear         = NULL;

	pthread_mutex_init(&(q->m_rw), NULL);
	return q;
}

void *task_queue_push(task_queue_t *q, task_t *t) {
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

void *task_destroy(task_queue_t *q) {
	task_queue_clear(q);
	free(q);
}

void *thread_function(void *arg) {
	return NULL;
}

th_pool_t *th_pool_init(int size) {
	th_pool_t *th_pool = malloc(sizeof(th_pool_t));
	pthread_t **pool    = malloc(sizeof(thread_t) * size);
	for (int i = 0; i < size; i++) {
		thread_t *thread = malloc(sizeof(thread));
		pthread_create(pool[i], NULL, (void * (*)(void *))thread_function, NULL);
		thread->id = i;
        thread->p_thread = pool[i];
	}
}




