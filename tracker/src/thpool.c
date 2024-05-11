#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>

#include "thpool.h"

static volatile int keep_alive;

task_queue_t *task_queue_init() {
	task_queue_t *q = malloc(sizeof(task_queue_t));
	q->count        = 0;
	q->front        = NULL;
	q->rear         = NULL;

	sem_init(&(q->has_task), 0, 0);
	pthread_mutex_init(&(q->m_rw), NULL);
	return q;
}

void task_queue_push(task_queue_t *q, task_t *t) {
	int sem_value;
	pthread_mutex_lock(&(q->m_rw));
	if (q->count == 0) {
		q->front = t;
		q->rear  = t;
	} else {
		q->rear->prev = t;
		q->rear       = t;
	}
	sem_getvalue(&(q->has_task), &sem_value);
	// fprintf(stderr, "sem value: %d\n", sem_value); // debug print
	if (sem_value == 0)
		sem_post(&(q->has_task));
	q->count++;
	pthread_mutex_unlock(&(q->m_rw));
}

task_t *task_queue_pull(task_queue_t *q) {
	pthread_mutex_lock(&(q->m_rw));

	int sem_value;
	task_t *t = q->front;

	if (q->count == 1) {
		q->count = 0;
		t        = q->front;
		q->front = NULL;
		q->rear  = NULL;
		sem_getvalue(&(q->has_task), &sem_value);
		// fprintf(stderr, "sem value: %d\n", sem_value); // debug print

		if (sem_value == 1) {
			sem_destroy(&(q->has_task));
			sem_init(&(q->has_task), 0, 0);
		}
	}
	if (q->count > 1 && q->front != NULL) {
		q->count--;
		t        = q->front;
		q->front = t->prev;
		sem_getvalue(&(q->has_task), &sem_value);
		// fprintf(stderr, "sem value: %d\n", sem_value); // debug print
		if (sem_value == 0)
			sem_post(&(q->has_task));
	}
	pthread_mutex_unlock(&(q->m_rw));
	return t;
}

void task_queue_clear(task_queue_t *q) {
	while (q->count != 0) {
		free(task_queue_pull(q));
	}

	sem_destroy(&(q->has_task));
	sem_init(&(q->has_task), 0, 1);
	q->count = 0;
	q->front = NULL;
	q->rear  = NULL;
}

void task_destroy(task_queue_t *q) {
	task_queue_clear(q);
	free(q);
}

void *thread_function(thread_t *thread) {

	assert(thread->thpool != NULL);
	thpool_t *thpool = thread->thpool;

	pthread_mutex_lock(&thpool->m_count);
	// fprintf(stderr, "thread n°%d is alive, total thread alive: %d \n",
	// thread->id, thpool->threads_alive); // debug printf
	thpool->threads_alive++;
	pthread_mutex_unlock(&thpool->m_count);

	while (keep_alive) {

		sem_wait(&(thpool->queue.has_task));

		pthread_mutex_lock(&thpool->m_count);
		thpool->threads_working++;
		pthread_mutex_unlock(&thpool->m_count);

		void *(*func_buff)(void *);
		void *arg_buff;
		char *ret_value;
		task_t *task = task_queue_pull(&thpool->queue);

		if (task) {
			func_buff = task->p_func;
			arg_buff  = task->arg;
			ret_value = (char *)func_buff(arg_buff);
			fprintf(stderr, "[done by thread %d]\n", thread->id);
			free(task);
		}

		pthread_mutex_lock(&thpool->m_count);
		thpool->threads_working--;

		if (!thpool->threads_working) {
			pthread_cond_signal(&thpool->threads_all_idle);
		}

		pthread_mutex_unlock(&thpool->m_count);
		return ret_value;
	}

	pthread_mutex_lock(&thpool->m_count);
	thpool->threads_alive--;
	pthread_mutex_unlock(&thpool->m_count);

	return NULL;
}

void thread_init(thpool_t *thpool, thread_t **pool, int id) {
	*pool           = malloc(sizeof(thread_t));
	(*pool)->id     = id;
	(*pool)->thpool = thpool;
	pthread_create(&((*pool)->p_thread), NULL,
	    (void *(*)(void *))thread_function, (*pool));
	pthread_detach((*pool)->p_thread);
}

void thread_destroy(thread_t *thread) {
	free(thread);
}

thpool_t *thpool_init(int size) {
	thpool_t *thpool        = (thpool_t *)malloc(sizeof(thpool_t));
	thread_t **pool         = (thread_t **)malloc(sizeof(thread_t) * size);
	thpool->threads         = pool;
	thpool->size            = size;
	thpool->threads_alive   = 0;
	thpool->threads_working = 0;

	task_queue_t *queue = task_queue_init();
	thpool->queue       = *queue;

	keep_alive = 1;

	for (int i = 0; i < size; i++) {
		thread_init(thpool, &thpool->threads[i], i);
	}

	pthread_mutex_init(&thpool->m_count, NULL);
	pthread_cond_init(&thpool->threads_all_idle, NULL);
	return thpool;
}

void thpool_add_work(thpool_t *thpool, void *(*task)(void *), void *arg) {
	task_t *new_task = malloc(sizeof(task_t));
	new_task->p_func = task;
	new_task->arg    = arg;

	task_queue_push(&thpool->queue, new_task);
}

void thpool_wait(thpool_t *thpool) {
	pthread_mutex_lock(&thpool->m_count);
	while (thpool->queue.count || thpool->threads_working) {
		pthread_cond_wait(
		    &thpool->threads_all_idle, &thpool->m_count); // fix me
	}
	pthread_mutex_unlock(&thpool->m_count);
}

void thpool_destroy(thpool_t *thpool) {

	keep_alive = 0;

	for (int i = 0; i < thpool->size; i++) {
		thread_destroy(thpool->threads[i]);
	}

	while (thpool->queue.count != 0) {
		free(task_queue_pull(&thpool->queue));
	}

	free(thpool->threads);
	free(thpool);
}